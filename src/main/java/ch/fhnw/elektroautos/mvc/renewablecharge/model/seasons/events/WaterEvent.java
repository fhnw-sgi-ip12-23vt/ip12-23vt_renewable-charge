package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events;


import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WaterType;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;

public class WaterEvent extends Event {

    private WaterType activatingWaterType;

    /**
     * Constructs a new Event object with the given name and multiplier.
     *
     * @param translatePropName the name of the event
     * @param multiplier        the multiplier associated with the event
     * @throws IllegalArgumentException if the name is null or empty, or if the multiplier is negative
     */
    public WaterEvent(String translatePropName, float multiplier, WaterType activatingWaterType) {
        super(translatePropName, multiplier);
        this.activatingWaterType = activatingWaterType;
    }

    @Override
    public boolean matchesWeather(WeatherConfiguration weatherConfiguration) {
        return weatherConfiguration.getWaterType() == activatingWaterType;
    }
}
