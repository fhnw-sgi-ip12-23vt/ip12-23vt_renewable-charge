package ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes;

import java.util.List;

/**
 * The class that represents the configuration of the weather.
 * It includes the different types of weather.
 */
public class WeatherConfiguration {
    private final StorgeState storageState;
    private final SunType sunType;
    private final ThermalType thermalType;
    private final WaterType waterType;
    private final WindType windType;

    public WeatherConfiguration(StorgeState storageState, SunType sunType, ThermalType thermalType, WaterType waterType, WindType windType) {
        this.storageState = storageState;
        this.sunType = sunType;
        this.thermalType = thermalType;
        this.waterType = waterType;
        this.windType = windType;
    }

    public StorgeState getStorageState() {
        return storageState;
    }

    public SunType getSunType() {
        return sunType;
    }

    public ThermalType getThermalType() {
        return thermalType;
    }

    public WaterType getWaterType() {
        return waterType;
    }

    public WindType getWindType() {
        return windType;
    }

    public List<String> getImagePaths() {
        return List.of(sunType.getImagePath(), thermalType.getImagePath(), waterType.getImagePath(), windType.getImagePath());
    }

    @Override
    public String toString() {
        return "StorageState: "
                + storageState
                + " SunType: "
                + sunType
                + " ThermalType: "
                + thermalType
                + " WaterType: "
                + waterType
                + " WindType: "
                + windType;
    }

}
