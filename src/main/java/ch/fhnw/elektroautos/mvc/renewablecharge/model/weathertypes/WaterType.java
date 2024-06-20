package ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes;

import java.util.Random;

/**
 * The enum that represents the different types of water.
 * It includes the image, which will then be used in the game.
 */
public enum WaterType {
    HighWaterLevel("high-water-level.png"),
    MediumWaterLevel("medium-water-level.png"),
    LowWaterLevel("low-water-level.png");

    /**
     * The path to the image of the weather type.
     */
    private final String imagePath;

    /**
     * The constructor of the enum.
     *
     * @param imageName The name of the image of the weather type.
     */
    WaterType(String imageName) {
        this.imagePath = "/mvc/renewablecharge/images/weathertypes/water/" + imageName;
    }

    /**
     * @return The path to the image of the weather type.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Changes the weather type based on the current weather type.
     *
     * @param oldState The current weather type.
     * @param sunType  The current sun type.
     * @return The new weather type.
     */
    public static WaterType changeEvent(WaterType oldState, SunType sunType) {
        Random r = new Random();
        if (r.nextInt(1, 3) == 2) {
            switch (oldState) {
                case HighWaterLevel -> {
                    return sunType == SunType.Rainy ? WaterType.HighWaterLevel : WaterType.MediumWaterLevel;
                }
                case MediumWaterLevel -> {
                    return sunType == SunType.Rainy ? WaterType.HighWaterLevel : WaterType.LowWaterLevel;
                }
                case LowWaterLevel -> {
                    return sunType == SunType.Rainy ? WaterType.MediumWaterLevel : WaterType.LowWaterLevel;
                }
            }
        }

        return oldState;
    }
}
