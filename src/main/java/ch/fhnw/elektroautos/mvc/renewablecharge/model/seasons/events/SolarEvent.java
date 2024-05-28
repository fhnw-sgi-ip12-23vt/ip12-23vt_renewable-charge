package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.SunType;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;

public class SolarEvent extends Event {

    private SunType activatingSunType;

    /**
     * Constructs a new Event object with the given translatePropName and multiplier.
     *
     * @param translatePropName       the translatePropName of the event
     * @param multiplier the multiplier associated with the event
     * @throws IllegalArgumentException if the translatePropName is null or empty, or if the multiplier is negative
     */
    public SolarEvent(String translatePropName, float multiplier, SunType activatingSunType) {
        super(translatePropName, multiplier);
        this.activatingSunType = activatingSunType;
    }

    @Override
    public boolean matchesWeather(WeatherConfiguration weatherConfiguration) {
        return weatherConfiguration.getSunType() == activatingSunType;
    }
}
