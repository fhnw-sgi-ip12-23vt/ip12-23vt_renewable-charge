package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.crowpi.internal.rfid.RfidCard;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.I2CController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * Represents a car in the renewable charge game.
 */
public final class Car implements Serializable {

    private final Logger logger = Logger.getLogger(Car.class.getName());

    /**
     * The unique RFID CHIP serial key.
     * The ID must be registered in the properties file
     * or else the car will not be recognized.
     */
    private final String chipId;

    /**
     * The property name for the car. This name will be used
     * for the translation.
     */
    private final String name;

    /**
     * The battery capacity of the car in kilowatt-hours.
     */
    private final int batteryCapacityWh;

    /**
     * The currently charged capacity of the car in kilowatt-hours.
     */
    private int chargedCapacityWh;

    /**
     * The energy efficiency of the car in kilometers per kilowatt-hour.
     */
    private final float energyEfficiencyKmWh;

    /**
     * Whether the car is blocked from charging.
     */
    private boolean isBlocked;

    /**
     * The owner of this car, or null if the car is not owned by a player.
     */
    private Player owner;

    /**
     * Constructs a new Car object with the specified parameters.
     *
     * @param name              the name of the car
     * @param batteryCapacityWh the battery capacity of the car in watt-hours
     * @param maxRange          the maximum range of the car in kilometers
     */
    public Car(String chipId, String name, int batteryCapacityWh, int maxRange) {
        this.chipId = chipId;
        this.name = name;
        this.batteryCapacityWh = batteryCapacityWh;
        this.energyEfficiencyKmWh = (float) maxRange / batteryCapacityWh;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public List<String> getChipIds() {
        return List.of(chipId.split(":"));
    }

    /**
     * Retrieves the name of the car.
     *
     * @return the name of the car
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the battery capacity of the car in watt-hours.
     *
     * @return the battery capacity of the car in watt-hours
     */
    public int getBatteryCapacityWh() {
        return batteryCapacityWh;
    }

    /**
     * Retrieves the currently charged capacity of the car in watt-hours.
     *
     * @return the currently charged capacity of the car in watt-hours
     */
    public int getChargedCapacityWh() {
        return chargedCapacityWh;
    }

    /**
     * Retrieves the energy efficiency of the car in kilometers per kilowatt-hour.
     *
     * @return the energy efficiency of the car in kilometers per kilowatt-hour
     */
    public float getEnergyEfficiencyKmWh() {
        return energyEfficiencyKmWh;
    }

    /**
     * Resets the car.
     */
    public void reset() {
        chargedCapacityWh = 0;
        isBlocked = false;
    }

    /**
     * The main method to charge a car with a package.
     * <br />
     * Whenever this is called, the car will be blocked from charging until
     * {@link Configuration}(TIME_BLOCKED_PER_CHARGED_KWH) * {@link EnergyPackage#getSize()}
     * seconds have passed.
     * <br />
     * During this time, the car will be charged a calculated amount of energy every second.
     *
     * @param energyPackage The energy package to charge the car with
     *                      (the size of the package will be used to calculate the time the car is blocked)
     */
    public boolean claimPackage(EnergyPackage energyPackage, MainModel model, LedStripController ledStripController, I2CController i2CController, Runnable onEveryCharge) {
        if (isBlocked) {
            return false;  // Car is currently blocked, so reject the charge attempt.
        }

        isBlocked = true;
        int totalChargeDurationSeconds = (int) (Configuration.get("TIME_BLOCKED_PER_CHARGED_KWH", Float.class)
                * (energyPackage.getSize()) + Configuration.get("BASE_BLOCK_TIME", Integer.class));  // Time the car is blocked
        int totalEnergyToDeliver = energyPackage.getSize()
                * 1000;  // Total energy in watt-hours the package can deliver
        int chargeIncrement = totalEnergyToDeliver
                / totalChargeDurationSeconds;  // Energy added each second
        final int[] secondsPassed   = {0};
        final int[] energyDelivered = {0};  // Track the energy actually delivered

        // Scheduler for charging the car
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (model.gameState.getValue() != GameState.RUNNING) timer.cancel();
                logger.info("Running charging task for " + name);
                if (energyDelivered[0] < totalEnergyToDeliver && secondsPassed[0] < totalChargeDurationSeconds) {
                    if (energyDelivered[0] + chargeIncrement <= totalEnergyToDeliver) {
                        charge(chargeIncrement, ledStripController);
                        energyDelivered[0] += chargeIncrement;
                        logger.info("Charging "
                                + name
                                + ": Added "
                                + chargeIncrement
                                + "Wh, total charged: "
                                + chargedCapacityWh
                                + "Wh "
                                + "total capacity: "
                                + batteryCapacityWh
                        );
                        i2CController.setPort(owner.getI2CButton().getPort(), false); // ch
                        if (onEveryCharge != null) {
                            onEveryCharge.run();
                        }
                        if (chargedCapacityWh + chargeIncrement >= batteryCapacityWh) onFinish();
                    } else {
                        // Battery full or allocated energy delivered
                        onComplete();
                    }
                    secondsPassed[0]++;
                } else {
                    // Time elapsed or total energy delivered
                    onComplete();
                }
            }

            /**
             * Function to run when the charging is complete.
             */
            private void onComplete() {
                isBlocked = false;
                i2CController.setPort(owner.getI2CButton().getPort(), true); // ch
                this.cancel();
            }

            private void onFinish() {
                isBlocked = false;
                i2CController.setPort(owner.getI2CButton().getPort(), true); // ch
                logger.info(owner.getTranslationPropName() + " fully charged!");
                timer.cancel();
            }
        }, 0, 1000);

        return true;
    }


    /**
     * Charges the car with the specified amount of energy.
     *
     * @param energyWh the amount of energy to charge the car with, in watt-hours
     */
    public void charge(int energyWh, LedStripController ledStripController) {
        this.chargedCapacityWh += energyWh;
        int ledsToLight = (int) Math.ceil((chargedCapacityWh / (float) batteryCapacityWh) * 6);
        ledStripController.setLedStripColor(
                ledStripController.getChargeLedOfPlayer(owner.getTranslationPropName()),
                ledStripController.getColorOfPlayer(owner),
                ledsToLight
        );
    }

    /**
     * Returns the range of the car in kilometers.
     *
     * @return the range of the car in kilometers
     */
    public int getRangeInKm() {
        return (int) (chargedCapacityWh * energyEfficiencyKmWh);
    }

    /**
     * Retrieves the owner of this car.
     *
     * @return the owner of this car
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Whether the car is blocked from charging.
     *
     * @return true if the car is blocked, false otherwise
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    /**
     * Returns a string representation of the Car object.
     *
     * @return a string representation of the Car object
     */
    @Override
    public String toString() {
        return "Car{" +
                "chipId=" + chipId +
                ", name='" + name + '\'' +
                ", batteryCapacityWh=" + batteryCapacityWh +
                ", chargedCapacityWh=" + chargedCapacityWh +
                ", energyEfficiencyKmWh=" + energyEfficiencyKmWh +
                '}';
    }
}
