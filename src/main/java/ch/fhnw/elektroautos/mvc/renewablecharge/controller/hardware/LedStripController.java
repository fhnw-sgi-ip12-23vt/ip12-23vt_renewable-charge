package ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ControllerBase;

import java.io.Serial;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Controller class for handling LED strip operations.
 */
public class LedStripController extends ControllerBase<MainModel> {
    private SerialHelper serialHelper;

    //Long player strips
    private LedStrip player1LedStrip;
    private LedStrip player2LedStrip;
    private LedStrip player3LedStrip;
    private LedStrip player4LedStrip;

    //Charging station strips
    private LedStrip chargingStation1LedStrip;
    private LedStrip chargingStation2LedStrip;
    private LedStrip chargingStation3LedStrip;
    private LedStrip chargingStation4LedStrip;

    //Idle mode thread
    private Thread idleModeThread;

    /**
     * Constructor to initialize the LED strips and SerialHelper.
     *
     * @param model The main model.
     */
    public LedStripController(MainModel model) {
        super(model);
        initialize();
        //idleMode();
    }

    private void initialize() {
        if (model.getConfiguration() == null) {
            throw new IllegalArgumentException("Configuration is null");
        }
        this.serialHelper = model.getConfiguration().getSerialHelper();

        // Initialize the LedStrip for player
        this.player1LedStrip = new LedStrip(0, serialHelper);
        this.player2LedStrip = new LedStrip(1, serialHelper);
        this.player3LedStrip = new LedStrip(2, serialHelper);
        this.player4LedStrip = new LedStrip(3, serialHelper);

        //Initialize the LedStrip for charging stations
        this.chargingStation1LedStrip = new LedStrip(4, serialHelper);
        this.chargingStation2LedStrip = new LedStrip(5, serialHelper);
        this.chargingStation3LedStrip = new LedStrip(6, serialHelper);
        this.chargingStation4LedStrip = new LedStrip(7, serialHelper);

        deactivateAllLeds();
    }

    /**
     * Enum representing different LED colors.
     */
    public enum LedColors {
        RED((byte) 255, (byte) 0, (byte) 0),
        GREEN((byte) 0, (byte) 255, (byte) 0),
        BLUE((byte) 0, (byte) 0, (byte) 255),
        YELLOW((byte) 255, (byte) 255, (byte) 0),
        WHITE((byte) 255, (byte) 255, (byte) 255);

        private final byte red;
        private final byte green;
        private final byte blue;

        LedColors(byte red, byte green, byte blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public byte getRed() {
            return red;
        }

        public byte getGreen() {
            return green;
        }

        public byte getBlue() {
            return blue;
        }
    }

    /**
     * Enum representing different types of LED strips.
     */
    public enum LedStripType {
        PLAYER1,
        PLAYER2,
        PLAYER3,
        PLAYER4,
        CHARGING_STATION1,
        CHARGING_STATION2,
        CHARGING_STATION3,
        CHARGING_STATION4
    }


    /**
     * Sets the color of a specified LED strip.
     *
     * @param ledStripType The type of the LED strip.
     * @param color        The color to set.
     * @param numberOfLEDs The number of LEDs to set the color for.
     */
    public void setLedStripColor(LedStripType ledStripType, LedColors color, int numberOfLEDs) {
        switch (ledStripType) {
            case PLAYER1:
                try {
                    player1LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PLAYER2:
                try {
                    player2LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PLAYER3:
                try {
                    player3LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PLAYER4:
                try {
                    player4LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CHARGING_STATION1:
                try {
                    chargingStation1LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CHARGING_STATION2:
                try {
                    chargingStation2LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CHARGING_STATION3:
                try {
                    chargingStation3LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CHARGING_STATION4:
                try {
                    chargingStation4LedStrip.sendMany(numberOfLEDs, color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * Activates idle mode, which animates the LEDs with random colors going up and down.
     */
    public void idleMode() {
        final int numberOfLEDs        = 25;
        final int chargingStationLEDs = 6;
        Random    random              = new Random();
        LedStripType[] playerStrips = {
                LedStripType.PLAYER1,
                LedStripType.PLAYER2,
                LedStripType.PLAYER3,
                LedStripType.PLAYER4
        };
        LedStripType[] chargingStationStrips = {
                LedStripType.CHARGING_STATION1,
                LedStripType.CHARGING_STATION2,
                LedStripType.CHARGING_STATION3,
                LedStripType.CHARGING_STATION4
        };

        idleModeThread = new Thread(() -> {
            try {
                while (true) {
                    // Going up
                    for (int i = 1; i <= numberOfLEDs; i++) {
                        LedColors randomColor = getRandomColor(random);
                        for (LedStripType strip : playerStrips) {
                            setLedStripColor(strip, randomColor, i);
                        }
                        sleep(100);  // Adjust delay as needed
                    }

                    // Going down
                    for (int i = numberOfLEDs - 1; i >= 0; i--) {
                        LedColors randomColor = getRandomColor(random);
                        for (LedStripType strip : playerStrips) {
                            setLedStripColor(strip, randomColor, i);
                        }
                        sleep(100);  // Adjust delay as needed
                    }

                    // Going up for charging stations
                    for (int i = 1; i <= chargingStationLEDs; i++) {
                        LedColors randomColor = getRandomColor(random);
                        for (LedStripType strip : chargingStationStrips) {
                            setLedStripColor(strip, randomColor, i);
                        }
                        sleep(100);  // Adjust delay as needed
                    }

                    // Going down for charging stations
                    for (int i = chargingStationLEDs - 1; i >= 0; i--) {
                        LedColors randomColor = getRandomColor(random);
                        for (LedStripType strip : chargingStationStrips) {
                            setLedStripColor(strip, randomColor, i);
                        }
                        sleep(100);  // Adjust delay as needed
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        idleModeThread.start();
    }

    /**
     * Stops the idle mode.
     */
    public void stopIdleMode() {
        if (idleModeThread == null) {
            return;
        }
        idleModeThread.interrupt();
        deactivateAllLeds();
    }

    /**
     * Gets a random LED color.
     *
     * @param random The Random instance used to generate the color.
     * @return A randomly selected LedColors value.
     */
    private LedColors getRandomColor(Random random) {
        LedColors[] colors = LedColors.values();
        return colors[random.nextInt(colors.length)];
    }

    /**
     * Deactivates all LEDs on all strips.
     */
    public void deactivateAllLeds() {
        turnOffAllLeds();
        try {
            sleep(20);
        } catch (InterruptedException e) {
        }
        turnOffAllLeds();
    }

    private void turnOffAllLeds() {
        serialHelper.sendAllOff(0, 25);
        serialHelper.sendAllOff(1, 25);
        serialHelper.sendAllOff(2, 25);
        serialHelper.sendAllOff(3, 25);
        serialHelper.sendAllOff(4, 6);
        serialHelper.sendAllOff(5, 6);
        serialHelper.sendAllOff(6, 6);
        serialHelper.sendAllOff(7, 6);
    }

    public void deactivatePlayerLeds() {
        serialHelper.sendAllOff(0, 25);
        serialHelper.sendAllOff(1, 25);
        serialHelper.sendAllOff(2, 25);
        serialHelper.sendAllOff(3, 25);
    }

    /**
     * Deactivates all LEDs on a specified LED strip.
     *
     * @param ledStripType The type of the LED strip to deactivate.
     */
    public void deactivateLED(LedStripType ledStripType) {
        switch (ledStripType) {
            case PLAYER1:
                serialHelper.sendAllOff(0, 25);
                break;
            case PLAYER2:
                serialHelper.sendAllOff(1, 25);
                break;
            case PLAYER3:
                serialHelper.sendAllOff(2, 25);
                break;
            case PLAYER4:
                serialHelper.sendAllOff(3, 25);
                break;
            case CHARGING_STATION1:
                serialHelper.sendAllOff(4, 6);
                break;
            case CHARGING_STATION2:
                serialHelper.sendAllOff(5, 6);
                break;
            case CHARGING_STATION3:
                serialHelper.sendAllOff(6, 6);
                break;
            case CHARGING_STATION4:
                serialHelper.sendAllOff(7, 6);
                break;
        }
    }

    public LedStripType getRaceLedOfPlayer(String name) {
        switch (name) {
            case "player1":
                return LedStripType.PLAYER1;
            case "player2":
                return LedStripType.PLAYER2;
            case "player3":
                return LedStripType.PLAYER3;
            case "player4":
                return LedStripType.PLAYER4;
            default:
                return null;
        }
    }

    public LedStripType getChargeLedOfPlayer(String name) {
        switch (name) {
            case "player1":
                return LedStripType.CHARGING_STATION1;
            case "player2":
                return LedStripType.CHARGING_STATION2;
            case "player3":
                return LedStripType.CHARGING_STATION3;
            case "player4":
                return LedStripType.CHARGING_STATION4;
            default:
                return null;
        }
    }

    public LedColors getColorOfPlayer(Player player) {
        switch (player.getI2CButton()) {
            case YELLOW:
                return LedColors.YELLOW;
            case BLUE:
                return LedColors.BLUE;
            case GREEN:
                return LedColors.GREEN;
            case RED:
                return LedColors.RED;
            default:
                return LedColors.WHITE;
        }
    }

    /**
     * Getters for the LedStrips.
     */
    public SerialHelper getSerialHelper() {
        return serialHelper;
    }

    public LedStrip getPlayer1LedStrip() {
        return player1LedStrip;
    }

    public LedStrip getPlayer2LedStrip() {
        return player2LedStrip;
    }

    public LedStrip getPlayer3LedStrip() {
        return player3LedStrip;
    }

    public LedStrip getPlayer4LedStrip() {
        return player4LedStrip;
    }

    public LedStrip getChargingStation1LedStrip() {
        return chargingStation1LedStrip;
    }

    public LedStrip getChargingStation2LedStrip() {
        return chargingStation2LedStrip;
    }

    public LedStrip getChargingStation3LedStrip() {
        return chargingStation3LedStrip;
    }

    public LedStrip getChargingStation4LedStrip() {
        return chargingStation4LedStrip;
    }

}

