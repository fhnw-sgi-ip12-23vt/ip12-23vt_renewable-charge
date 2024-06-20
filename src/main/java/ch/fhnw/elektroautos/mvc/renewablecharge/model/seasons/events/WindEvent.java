package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WindType;

public class WindEvent extends Event {

    private WindType activatingWindType;

    /**
     * Constructs a new Event object with the given name and multiplier.
     *
     * @param translatePropName the name of the event
     * @param multiplier        the multiplier associated with the event
     * @throws IllegalArgumentException if the name is null or empty, or if the multiplier is negative
     */
    public WindEvent(String translatePropName, float multiplier, WindType activatingWindType) {
        super(translatePropName, multiplier);
        this.activatingWindType = activatingWindType;
    }

    @Override
    public boolean matchesWeather(WeatherConfiguration weatherConfiguration) {
        return weatherConfiguration.getWindType() == activatingWindType;
    }
}
