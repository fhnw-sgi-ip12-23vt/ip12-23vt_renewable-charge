package ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.i2c.I2C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class I2CController {
    private final I2C inputI2c;
    private final I2C outputI2c;
    private HashMap<Integer, Boolean> ports;
    private List<Consumer<Integer>> events = new ArrayList<>();
    private Context pi4j;
    private DigitalInputConfigBuilder inputConfigBuilder;
    private DigitalInput interrupt;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> checkTask;
    public I2CController(Context pi4j, int interruptPin, int inputDevice, int outputDevice) {
        //Initializing all devices
        this.pi4j = pi4j;
        inputI2c = pi4j.i2c().create(1, inputDevice);
        outputI2c = pi4j.i2c().create(1, outputDevice);
        inputConfigBuilder = DigitalInputConfig
                .newBuilder(pi4j)
                .address(interruptPin)
                .pull(PullResistance.PULL_UP);
        interrupt = pi4j.din().create(inputConfigBuilder.build());

        //Setting all ports to off
        ports = new HashMap<>(Map.of(0, false, 1, false, 2, false, 3, false));

        // read current input state
        AtomicReference<Byte> currentInputState = new AtomicReference<>((byte) inputI2c.read());
        System.out.println("Im running");
        // register a poller on the interrupt pin, reading the new state
        runCheckTask(inputI2c, currentInputState);
    }

    /**
     * This function handles the event change of the Input I2C board
     * @param inputI2c this is the Input I2C device
     * @param initialInputState this is the AtomicReference of the state of the Input, when it was triggered
     */
    private void handleNewState(I2C inputI2c, AtomicReference<Byte> initialInputState) {
        // Read new state of the input port
        byte inputState = (byte) inputI2c.read();

        for (int pin = 0; pin < Byte.SIZE; pin++) {
            boolean initialBitState = isBitSet(initialInputState.get(), pin);
            boolean currentBitState = isBitSet(inputState, pin);

            if (initialBitState != currentBitState) {
                // The bit has changed from low to high, indicating a button press
                System.out.println("Port " + pin + " has been pressed.");
                for (Consumer<Integer> event : List.copyOf(events)) {
                    System.out.println("Event: " + event);
                    event.accept(pin);
                }
            }
        }
    }

    /**
     * This function checks if the bit at a Position is set
     * @param data the Byte data
     * @param position the position of the data to check
     * @return Boolean, if the bit is set or not
     */
    private boolean isBitSet(byte data, int position) {
        if (position > 7)
            throw new IllegalStateException("Position " + position + " is not available in a byte!");
        return ((data >> position) & 1) == 1;
    }

    /**
     * This function generates the byte Data to set a specific sequence of ports to High and Low
     * @param positions The HashMap with all Ports and if they are High (true) or Low(false)
     * @return Returns a Byte which turns on the defined Configuration of Ports
     */
    private byte setBits(HashMap<Integer, Boolean> positions) {
        byte data = (byte)0xff;
        for (Map.Entry<Integer, Boolean> entry : positions.entrySet()) {
            int position = entry.getKey();
            boolean bitState = entry.getValue();

            if (position < 0 || position > 7) {
                throw new IllegalArgumentException("Invalid position: " + position);
            }

            if (!bitState) {
                data |= (byte) (1 << position); // Set the bit to 1
            } else {
                data &= (byte) ~(1 << position); // Set the bit to 0
            }
        }

        return data;
    }


    /**
     * Turns the Port on the Specified Port High or Low
     * @param port The port ond the I2C Port
     * @param state True if the Port should be set to high or false if it should be set to low
     */
    public void setPort(int port, boolean state){
        //Channel 0 = 0x00, Alle ein = 0x20 0x00, alle aus = 0x20 0xff
        ports.put(port, state);
        outputI2c.write(setBits(ports));
    }


    /**
     * Subscribe with a Consumer to get notified, when the State of a Port has changed
     * @param event The Consumer which will be added to the List of events
     * @return The position of the event in the Events list, needed to unsubscribe
     */
    public int subscribe(Consumer<Integer> event){
        System.out.println("subscribed");
        events.add(event);
        return events.size()-1;
    }

    /**
     * This function unsubscribes a Consumer from the events List
     * @param pos The Position of the Consumer in the events
     */
    public void unsubscribe(int pos){
        events.remove(pos);
    }

    /**
     * Reset the subscription list
     */
    public void reset() {
        events.clear();
    }

    /**
     * Get the events
     * @return  The events
     */
    public List<Consumer<Integer>> getEvents() {
        System.out.println(interrupt.getDescription());
        return events;
    }

    private void runCheckTask(I2C inputI2c, AtomicReference<Byte> initialInputState) {
        final Runnable task = new Runnable() {
            public void run() {
                interrupt.pull();
                if (interrupt.state().isLow()){
                    handleNewState(inputI2c, initialInputState);
                }
            }
        };

        // Schedule the task to run every second, with an initial delay of 0 seconds
        checkTask = scheduler.scheduleAtFixedRate(task, 0, 100, TimeUnit.MILLISECONDS);
    }

}
