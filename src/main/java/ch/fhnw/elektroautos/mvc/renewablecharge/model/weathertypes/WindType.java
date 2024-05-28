package ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes;

import java.util.Random;

/**
 * The enum that represents the different types of wind.
 * It includes the image, which will then be used in the game.
 */
public enum WindType {
    WindStill("wind-still.png"),
    Breezy("breezy.png"),
    Windy("windy.png");

    /**
     * The path to the image of the weather type.
     */
    private final String imagePath;

    /**
     * The constructor of the enum.
     *
     * @param imageName The name of the image of the weather type.
     */
    WindType(String imageName) {
        this.imagePath = "/mvc/renewablecharge/images/weathertypes/wind/" + imageName;
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
    public static WindType changeEvent(WindType oldState) {
        Random r = new Random();
        if (r.nextInt(1, 3) == 2) {
            switch (oldState) {
                case WindStill -> {
                    return r.nextInt(1, 3) == 2 ? WindType.WindStill : WindType.Breezy;
                }
                case Breezy -> {
                    var change = r.nextInt(1, 4);
                    if (change == 3) return WindType.WindStill;
                    if (change == 2) return oldState;
                    return WindType.Windy;
                }
                case Windy -> {
                    return r.nextInt(1, 3) == 2 ? WindType.Breezy : WindType.Windy;
                }
            }
        }

        return oldState;
    }
}
