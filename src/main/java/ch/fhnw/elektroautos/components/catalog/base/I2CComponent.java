package ch.fhnw.elektroautos.components.catalog.base;

import ch.fhnw.elektroautos.components.crowpi.events.EventHandler;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.util.mvcbase.MvcLogger;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;

import java.util.concurrent.atomic.AtomicReference;

public class I2CComponent {
    private final MvcLogger logger = new MvcLogger();

    private I2CDevice device;
    private Context pi4j;
    private int inputAddress;
    private int outputAddress;
    private final AtomicReference<EventHandler<Player>> pressHandler;

    public I2CComponent(Context pi4j, int inputAddress, int outputAddress) {
        this.pi4j = pi4j;
        this.inputAddress = inputAddress;
        this.outputAddress = outputAddress;
        this.pressHandler = new AtomicReference<>();

        try {
            initialize();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initialize() throws InterruptedException {
        DigitalInputConfigBuilder inputConfigBuilder = DigitalInputConfig
                .newBuilder(pi4j)
                .address(inputAddress)
                .pull(PullResistance.PULL_UP);
//        DigitalOutputConfig outputConfigBuilder = DigitalOutputConfig
//                .newBuilder(pi4j)
//                .address(outputAddress)
//                .initial(DigitalState.LOW);
        DigitalInput interrupt = pi4j.din().create(inputConfigBuilder.build());
        I2C input = pi4j.i2c().create(1, 0x38);
        I2C output = pi4j.i2c().create(1, 0x20);
        device = new I2CDevice(pi4j, 0x38, "button-yellow") {
            @Override
            protected void init(I2C i2c) {

            }
        };


        logger.logInfo("Interrupt is on {} {}", interrupt.description(), interrupt.getName());
        System.out.println("Interrupt is on " + interrupt.description() + interrupt.getName());
        byte data = (byte) input.read();
        logger.logInfo("Read input state {} for {} {}", asBinary(data), input.description(), input.getName());
        System.out.println("Read input state "
                + asBinary(data)
                + " for "
                + input.description()
                + " "
                + input.getName());
        data = (byte) output.read();
        logger.logInfo("Read output state {} for {} {}", asBinary(data), output.description(), output.getName());
        System.out.println("Read output state "
                + asBinary(data)
                + " for"
                + output.description()
                + " "
                + output.getName());

        interrupt.addListener(e -> pressHandler.get());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down...");
                pi4j.shutdown();
            } catch (Exception e) {
                System.out.println("Failed to shutdown: " + e.getMessage());
            }
        }));

        synchronized (Thread.currentThread()) {
            Thread.currentThread().wait();
        }
    }

    /**
     * Send a single command to the device to set a specific port to high.
     * The port is specified by its index (1 to 8).
     */
    public void setPortHigh(int portIndex) {
        // Validate the port index
        if (portIndex < 1 || portIndex > 8) {
            throw new IllegalArgumentException("Port index must be between 1 and 8");
        }

        // Calculate the command byte with the corresponding bit set to 1
        byte cmd = (byte) (1 << (portIndex - 1));

        // Send the command to the device
        device.sendCommand(cmd);
    }

    public void onPress(EventHandler<Player> handler) {
        System.out.println("Pressed!");
        this.pressHandler.set(handler);
    }

    private void readInput(I2C input, I2C output) {
        byte data = (byte) input.read();
        System.out.println("Read new state " + asBinary(data));

        for (int j = 0; j < 8; j++) {
            boolean state = isBitSet(data, j);
            logger.logInfo("Detected pin " + j + " = " + (state ? 1 : 0));
            if (j == 0 && state) {
                output.write(0xfE);
            } else {
                output.write(0xff);
            }
        }
    }

    public boolean isBitSet(byte data, int position) {
        if (position > 7) {
            throw new IllegalStateException("Position " + position + " is not available in a byte!");
        }
        return ((data >> position) & 1) == 1;
    }

    public String asBinary(byte b) {

        StringBuilder sb = new StringBuilder();

        sb.append(((b >>> 7) & 1));
        sb.append(((b >>> 6) & 1));
        sb.append(((b >>> 5) & 1));
        sb.append(((b >>> 4) & 1));
        sb.append(((b >>> 3) & 1));
        sb.append(((b >>> 2) & 1));
        sb.append(((b >>> 1) & 1));
        sb.append(((b) & 1));

        return sb.toString();
    }
}
