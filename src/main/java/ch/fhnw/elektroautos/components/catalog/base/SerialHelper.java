package ch.fhnw.elektroautos.components.catalog.base;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.CRC32;

public class SerialHelper {

    private static final byte CH_WS2812_DATA = 1;
    private static final byte CH_DRAW_ALL = 2;

    private ExpanderDataWriteAdapter adapter;

    public SerialHelper(String address) {
        adapter = new ExpanderDataWriteAdapter(address);
    }

    public SerialHelper() {

    }

    public void handleSerialRFID() {
        adapter.handleSerialRFID();
    }

    public void sendAllOff(int channel, int numberOfLeds) {
        sendColors(channel, 3, 1, 0, 2, 0, new byte[numberOfLeds * 3], false);
    }

    public void sendColors(int channel, byte[] rgbPerPixel, boolean debug) {
        sendColors(channel, 3, rgbPerPixel, debug);
    }

    public void sendColors(int channel, int bytesPerPixel, byte[] rgbPerPixel, boolean debug) {
        sendColors(channel, bytesPerPixel, 1, 0, 2, 0, rgbPerPixel, debug);
    }

    public void sendColors(int channel, int bytesPerPixel, int rIndex, int gIndex, int bIndex, int wIndex,
                           byte[] rgbPerPixel, boolean debug) {
        if (debug) {
            System.out.println("Sending colors on channel " + channel);
        }

        if (bytesPerPixel != 3 && bytesPerPixel != 4) {
            return;
        }
        if (rIndex > 3 || gIndex > 3 || bIndex > 3 || wIndex > 3) {
            return;
        }
        if (rgbPerPixel == null) {
            return;
        }

        int   pixels = rgbPerPixel.length / bytesPerPixel;
        CRC32 crc    = new CRC32();
        crc.reset();
        ByteBuffer headerBuffer = initHeaderBuffer(10, (byte) channel, CH_WS2812_DATA);
        headerBuffer.put((byte) bytesPerPixel);
        headerBuffer.put((byte) (rIndex | (gIndex << 2) | (bIndex << 4) | (wIndex << 6)));
        headerBuffer.putShort((short) pixels);
        byte[] header = headerBuffer.array();

        if (debug) {
            // Output the RGB byte array for testing
            // This slows down the execution of the application!
            for (int i = 0; i < rgbPerPixel.length; i++) {
                System.out.printf("%02x ", rgbPerPixel[i]);
                if (i % 12 == 11) {
                    System.out.print("\n");
                } else if (i % 4 == 3) {
                    System.out.print("\t");
                }
            }
            System.out.print("\n");
        }

        crc.update(header);
        adapter.write(header);

        crc.update(rgbPerPixel);
        adapter.write(rgbPerPixel);

        writeCrc(crc);

        sendDrawAll();
    }

    public void closePort() {
        adapter.closePort();
    }

    private void sendDrawAll() {
        CRC32 crc = new CRC32();
        crc.reset();
        ByteBuffer buffer = initHeaderBuffer(6, (byte) 0xff, CH_DRAW_ALL);
        byte[]     bytes  = buffer.array();
        crc.update(bytes);
        adapter.write(bytes);
        writeCrc(crc);
    }

    private void writeCrc(CRC32 crc) {
        byte[] crcBytes = new byte[4];
        packInt(crcBytes, 0, (int) crc.getValue());
        adapter.write(crcBytes);
    }

    private void packInt(byte[] outgoing, int index, int val) {
        outgoing[index++] = (byte) (val & 0xFF);
        val = val >> 8;
        outgoing[index++] = (byte) (val & 0xFF);
        val = val >> 8;
        outgoing[index++] = (byte) (val & 0xFF);
        val = val >> 8;
        outgoing[index] = (byte) (val & 0xFF);
    }

    private ByteBuffer initHeaderBuffer(int size, byte channel, byte command) {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put((byte) 'U');
        buffer.put((byte) 'P');
        buffer.put((byte) 'X');
        buffer.put((byte) 'L');
        buffer.put(channel);
        buffer.put(command);
        return buffer;
    }

    public int subscribe(Consumer<String> event) {
        adapter.events.add(event);
        return adapter.events.size() - 1;
    }

    public void unsubscribe(int pos) {
        adapter.events.remove(pos);
    }

    private static class ExpanderDataWriteAdapter {

        private SerialPort port = null;
        private String portPath;
        public List<Consumer<String>> events = new ArrayList<>();

        public ExpanderDataWriteAdapter(String portPath) {
            this.portPath = portPath;
            openPort();
        }

        public ExpanderDataWriteAdapter() {
            openPort();
        }

        private void openPort() {
            if (port != null) {
                port.closePort();
            }
            try {
                port = null; //set to null in case getCommPort throws, port will remain null.
                port = SerialPort.getCommPort(this.portPath);
                port.setBaudRate(2_000_000);
                port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
                port.openPort(0, 8192, 8192);
            } catch (Exception e) {
                System.err.println("Could not open serial port " + e.getMessage());
            }
        }

        private void closePort() {
            if (port != null) {
                try {
                    // Make sure all pending commands are sent before closing the port
                    port.getOutputStream().flush();
                } catch (IOException e) {
                    System.err.println("Error while flushing the data: " + e.getMessage());
                }
                port.closePort();
            }
        }

        public void write(byte[] data) {
            int     lastErrorCode = port != null ? port.getLastErrorCode() : 0;
            boolean isOpen        = port != null && port.isOpen();
            if (port == null || !isOpen || lastErrorCode != 0) {
                openPort();
            }
            port.writeBytes(data, data.length);
        }

        public void handleSerialRFID() {
            // Create a thread for reading data from the serial port
            Thread readerThread = new Thread(() -> {
                StringBuilder partialData = new StringBuilder(); // To store partial RFID data

                while (true) {
                    try {
                        // Wait until data is available
                        while (port.bytesAvailable() == 0) {
                            Thread.sleep(20);
                        }

                        // Read data from the port
                        InputStream in     = port.getInputStream();
                        byte[]      buffer = new byte[in.available()];
                        int         read   = in.read(buffer);

                        if (read > 0) {
                            String newData = new String(buffer, 0, read);
                            partialData.append(newData); // Append to partial data

                            // Check if end marker is received
                            if (newData.contains("\n") || newData.contains("\r")) {
                                // Complete RFID transmission received
                                String rfidID = partialData.toString().trim();// Trim any leading/trailing whitespace
                                for (Consumer<String> event : events) {
                                    event.accept(rfidID);
                                }
                                partialData.setLength(0); // Clear partial data for next transmission
                            }
                        }

                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "RFID Serial Thread");

            // Start the reader thread
            readerThread.start();
        }

        private static String stringToHex(String inputString) {
            StringBuilder hexString = new StringBuilder();
            for (char c : inputString.toCharArray()) {
                hexString.append(String.format("%02X", (int) c));
            }
            return hexString.toString();
        }
    }
}

