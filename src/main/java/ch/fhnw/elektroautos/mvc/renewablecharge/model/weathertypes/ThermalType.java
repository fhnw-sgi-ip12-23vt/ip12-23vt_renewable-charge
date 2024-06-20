package ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes;

import java.util.Random;

/**
 * The enum that represents the different types of thermal.
 * It includes the image, which will then be used in the game.
 */
public enum ThermalType {
    Operating("operating.png"),
    Servicing("servicing.png");

    /**
     * The path to the image of the weather type.
     */
    private final String imagePath;

    /**
     * The constructor of the enum.
     *
     * @param imageName The name of the image of the weather type.
     */
    ThermalType(String imageName) {
        this.imagePath = "/mvc/renewablecharge/images/weathertypes/thermal/" + imageName;
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
     * @return The new weather type.
     */
    public static ThermalType changeEvent(ThermalType oldState) {
        Random r = new Random();
        if (r.nextInt(1, 3) == 2) {
            switch (oldState) {
                case Operating -> {
                    return r.nextInt(1, 100) > 2 ? ThermalType.Operating : ThermalType.Servicing;
                }
                case Servicing -> {
                    return ThermalType.Operating;
                }
            }
        }

        return oldState;
    }
}
