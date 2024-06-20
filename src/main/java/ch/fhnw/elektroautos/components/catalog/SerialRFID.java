package ch.fhnw.elektroautos.components.catalog;

import com.fazecast.jSerialComm.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SerialRFID {
    private String rfidID;
    private List<Consumer<String>> events = new ArrayList<>();

    public void handleSerialRFID() {

        // Create a thread for reading data from the serial port
        Thread readerThread = new Thread(() -> {
            // Get a list of available serial ports
            SerialPort[] ports = SerialPort.getCommPorts();

            if (ports.length == 0) {
                System.out.println("No serial ports found.");
                return;
            }

            // Choose the serial port to read from (you might need to change the index)
            SerialPort port = ports[0];

            // Open the serial port
            if (!port.openPort()) {
                System.out.println("Failed to open port.");
                return;
            }

            // Set the desired parameters (baud rate, data bits, stop bits, parity)
            port.setBaudRate(9600);

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

    public static String removeLastChar(String s) {
        return (s == null || s.isEmpty()) ? null : (s.substring(0, s.length() - 1));
    }

    public int subscribe(Consumer<String> event) {
        events.add(event);
        return events.size() - 1;
    }

    public void unsubscribe(int pos) {
        events.remove(pos);
    }

    public String getRfidID() {
        return rfidID;
    }

    /**
     * Converts a string to its hexadecimal representation.
     *
     * @param inputString The string to be converted.
     * @return A string representing the hexadecimal values of the input string.
     */
    private static String stringToHex(String inputString) {
        StringBuilder hexString = new StringBuilder();
        for (char c : inputString.toCharArray()) {
            hexString.append(String.format("%02X", (int) c));
        }
        return hexString.toString();
    }
}

