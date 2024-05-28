package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.StorgeState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;


public class StorageEvent extends Event {

    private StorgeState activatingStorageType;

    /**
     * Constructs a new Event object with the given name and multiplier.
     *
     * @param translatePropName       the name of the event
     * @param multiplier the multiplier associated with the event
     * @throws IllegalArgumentException if the name is null or empty, or if the multiplier is negative
     */
    public StorageEvent(String translatePropName, float multiplier, StorgeState activatingStorageType) {
        super(translatePropName, multiplier);
        this.activatingStorageType = activatingStorageType;
    }

    @Override
    public boolean matchesWeather(WeatherConfiguration weatherConfiguration) {
        return weatherConfiguration.getStorageState() == activatingStorageType;
    }
}
