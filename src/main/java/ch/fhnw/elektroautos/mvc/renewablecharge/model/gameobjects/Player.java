package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.crowpi.RfidComponent;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a player in the renewable charge game.
 */
public class Player implements Serializable {
    private final String translationPropName;
    private boolean active;

    /* Components */
    private String rfId;
    private RfidComponent rfidComponent;
    private final LedStrip ledStrip;
    private final I2CButton i2cButton;
    private final LedStrip chargeStrip;
    private final int raceLEDPort;
    private Car selectedCar;

    /**
     * Constructs a new Player object with the given parameters.
     *
     * @param translationPropName    the name of the player
     * @param active                 indicates if the player is active
     * @param i2cButton              the button associated with the player
     * @param chargeStrip            the port of the LED indicator for charging associated with the player
     * @param raceLEDPort            the port of the LED indicator for racing associated with the player
     */
    public Player(RfidComponent rfidComponent, LedStrip ledStrip, String translationPropName, boolean active, I2CButton i2cButton, LedStrip chargeStrip, int raceLEDPort) {
        this.rfidComponent = rfidComponent;
        this.ledStrip = ledStrip;
        this.translationPropName = Objects.requireNonNull(translationPropName, "Name cannot be null");
        this.active = active;
        this.i2cButton = i2cButton;
        this.chargeStrip = chargeStrip;
        this.raceLEDPort = raceLEDPort;
    }

    public Player(String rfId, LedStrip ledStrip, String translationPropName, boolean active, I2CButton i2cButton, LedStrip chargeStrip, int raceLEDPort) {
        this.rfId = rfId;
        this.ledStrip = ledStrip;
        this.translationPropName = Objects.requireNonNull(translationPropName, "Name cannot be null");
        this.active = active;
        this.i2cButton = i2cButton;
        this.chargeStrip = chargeStrip;
        this.raceLEDPort = raceLEDPort;
    }

    /**
     * Retrieves the RFID of the player.
     * @return  the RFID of the player
     */
    public String getRfId() {
        return rfId;
    }

    /**
     * Retrieves the RFID component associated with the player.
     *
     * @return the RFID component associated with the player
     */
    public RfidComponent getRfidComponent() {
        return rfidComponent;
    }

    /**
     * Retrieves the LED strip component associated with the player.
     *
     * @return  the LED strip component associated with the player
     */
    public LedStrip getLedStripComponent() {
        return ledStrip;
    }

    /**
     * Retrieves the selected car of the player.
     *
     * @return the selected car of the player
     */
    public Car getSelectedCar() {
        return selectedCar;
    }

    /**
     * Sets the selected car of the player.
     *
     * @param car the car to be selected
     */
    public void selectCar(Car car) {
        System.out.println("Player " + translationPropName + " selected car " + car.getName());
        car.setOwner(this);
        this.selectedCar = car;
    }

    /**
     * Activates the player.
     */
    public void activate() {
        active = true;
    }

    /**
     * Deactivates the player.
     */
    public void deactivate() {
        active = false;
    }

    /**
     * Retrieves the name of the player.
     *
     * @return the name of the player
     */
    public String getTranslationPropName() {
        return translationPropName;
    }

    /**
     * Checks if the player is active.
     *
     * @return true if the player is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Retrieves the port of the button associated with the player.
     *
     * @return the port of the button associated with the player
     */
    public I2CButton getI2CButton() {
        return i2cButton;
    }

    /**
     * Retrieves the port of the LED indicator for charging associated with the player.
     *
     * @return the port of the LED indicator for charging associated with the player
     */
    public LedStrip getChargeStrip() {
        return chargeStrip;
    }

    /**
     * Retrieves the port of the LED indicator for racing associated with the player.
     *
     * @return the port of the LED indicator for racing associated with the player
     */
    public int getRaceLEDPort() {
        return raceLEDPort;
    }

    /**
     * Returns a string representation of the Player object.
     *
     * @return a string representation of the Player object
     */
    @Override
    public String toString() {
        return "Player{" +
                "name='" + translationPropName + '\'' +
                "car='" + selectedCar + '\'' +
                ", active=" + active +
                ", buttonPort=" + i2cButton.getPort() +
                ", chargeStrip=" + chargeStrip.getChannel() +
                ", raceLEDPort=" + raceLEDPort +
                ", selectedCar=" + selectedCar +
                '}';
    }

    /**
     * Compares this player to the specified object.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return active == player.active &&
                i2cButton == player.i2cButton &&
                chargeStrip == player.chargeStrip &&
                raceLEDPort == player.raceLEDPort &&
                Objects.equals(translationPropName, player.translationPropName) &&
                Objects.equals(selectedCar, player.selectedCar);
    }

    /**
     * Returns a hash code value for the Player object.
     *
     * @return a hash code value for the Player object
     */
    @Override
    public int hashCode() {
        return Objects.hash(translationPropName, active, i2cButton, chargeStrip, raceLEDPort, selectedCar);
    }
}
