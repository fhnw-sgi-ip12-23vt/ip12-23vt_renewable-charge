package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.ThermalType;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;


public class ThermalEvent extends Event {

    private ThermalType activatingThermalType;

    /**
     * Constructs a new Event object with the given name and multiplier.
     *
     * @param translatePropName the name of the event
     * @param multiplier        the multiplier associated with the event
     * @throws IllegalArgumentException if the name is null or empty, or if the multiplier is negative
     */
    public ThermalEvent(String translatePropName, float multiplier, ThermalType activatingThermalType) {
        super(translatePropName, multiplier);
        this.activatingThermalType = activatingThermalType;
    }

    @Override
    public boolean matchesWeather(WeatherConfiguration weatherConfiguration) {
        return weatherConfiguration.getThermalType() == activatingThermalType;
    }
}
