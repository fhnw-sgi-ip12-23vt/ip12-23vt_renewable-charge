package ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes;

import java.util.Random;

/**
 * The enum that represents the different types of sun.
 * It includes the image, which will then be used in the game.
 */
public enum SunType {
    Sunny("sunny.png"),
    Cloudy("cloudy.png"),
    Rainy("rainy.png");

    /**
     * The path to the image of the weather type.
     */
    private final String imagePath;

    /**
     * The constructor of the enum.
     *
     * @param imageName The name of the image of the weather type.
     */
    SunType(String imageName) {
        this.imagePath = "/mvc/renewablecharge/images/weathertypes/sun/" + imageName;
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
    public static SunType changeEvent(SunType oldState) {
        Random r = new Random();
        if (r.nextInt(1, 3) == 2) {
            switch (oldState) {
                case Sunny -> {
                    System.out.println("-----------------------------------------");
                    return r.nextInt(1, 3) == 4 ? SunType.Sunny : SunType.Cloudy;
                }
                case Cloudy -> {
                    var change = r.nextInt(1, 4);
                    if (change == 3) return SunType.Sunny;
                    if (change == 2) return oldState;
                    return SunType.Rainy;
                }
                case Rainy -> {
                    return r.nextInt(1, 3) == 2 ? SunType.Rainy : SunType.Cloudy;
                }
            }
        }

        return oldState;
    }
}
