package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;

public abstract class Event {
    private String translatePropName;
    private float multiplier;

    /**
     * Constructs a new Event object with the given name and multiplier.
     *
     * @param translatePropName       the name of the event
     * @param multiplier the multiplier associated with the event
     * @throws IllegalArgumentException if the name is null or empty, or if the multiplier is negative
     */
    public Event(String translatePropName, float multiplier) {
//        if (name == null || name.isEmpty()) {
//            throw new IllegalArgumentException("Name cannot be null or empty");
//        }
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        this.translatePropName = translatePropName;
        this.multiplier = multiplier;
    }

    /**
     * Retrieves the name of the event.
     *
     * @return the name of the event
     */
    public String getTranslatePropName() {
        return translatePropName;
    }

    /**
     * Sets the name of the event.
     *
     * @param translatePropName the name of the event
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setTranslatePropName(String translatePropName) {
//        if (name == null || name.isEmpty()) {
//            throw new IllegalArgumentException("Name cannot be null or empty");
//        }
        this.translatePropName = translatePropName;
    }

    /**
     * Retrieves the multiplier associated with the event.
     *
     * @return the multiplier associated with the event
     */
    public float getMultiplier()
    {
        return  multiplier;
    }

    /**
     * Returns if the current event is possible for the weather.
     *
     * @return the multiplier associated with the event
     */
    public abstract boolean matchesWeather(WeatherConfiguration weatherConfiguration);

    /**
     * Sets the multiplier associated with the event.
     *
     * @param multiplier the multiplier associated with the event
     * @throws IllegalArgumentException if the multiplier is negative
     */
    public void setMultiplier(float multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        this.multiplier = multiplier;
    }

    /**
     * Returns a string representation of the Event object.
     *
     * @return a string representation of the Event object
     */
    @Override
    public String toString() {
        return "Event{" +
                "name='" + translatePropName + '\'' +
                ", multiplier=" + multiplier +
                '}';
    }
}
