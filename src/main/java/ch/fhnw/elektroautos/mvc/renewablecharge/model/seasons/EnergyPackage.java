package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events.Event;

public class EnergyPackage {
    private final int size;
    private final EnergyType energyType;
    private final Event event;

    /**
     * Constructs a new EnergyPackage object with the given size, energy type, and event.
     *
     * @param size       the size of the energy package
     * @param energyType the energy type associated with the package
     * @param event      the event associated with the package
     */
    public EnergyPackage(int size, EnergyType energyType, Event event) {
        this.size = size;
        this.energyType = energyType;
        this.event = event;

    }

    /**
     * Retrieves the size of the energy package.
     *
     * @return the size of the energy package
     */
    public int getSize() {
        return size;
    }

    /**
     * Retrieves the energy type associated with the package.
     *
     * @return the energy type associated with the package
     */
    public EnergyType getEnergyType() {
        return energyType;
    }

    /**
     * Retrieves the event associated with the package.
     *
     * @return the event associated with the package
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Returns a string representation of the EnergyPackage object.
     *
     * @return a string representation of the EnergyPackage object
     */
    @Override
    public String toString() {
        return "EnergyPackage{" +
                "size=" + size +
                ", energyType=" + energyType +
                ", event=" + event +
                '}';
    }
}