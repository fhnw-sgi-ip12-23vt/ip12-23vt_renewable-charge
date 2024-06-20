package ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes;

import java.util.Random;

/**
 * The enum that represents the different types of storage.
 * It includes the image, which will then be used in the game.
 */
public enum StorgeState {
    Full("full.png"),
    ThreeQuartersFull("three-quarters-full.png"),
    HalfFull("half-full.png"),
    OneQuarterFull("one-quarter-full.png"),
    Empty("empty.png");

    /**
     * The path to the image of the weather type.
     */
    private final String imagePath;

    /**
     * The constructor of the enum.
     *
     * @param imageName The name of the image of the weather type.
     */
    StorgeState(String imageName) {
        this.imagePath = "/mvc/renewablecharge/images/weathertypes/storage/" + imageName;
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
    public static StorgeState changeEvent(StorgeState oldState, SunType sunType) {
        Random r = new Random();
        if (r.nextInt(1, 3) == 2) {
            switch (oldState) {
                case Full -> {
                    return sunType == SunType.Rainy ? StorgeState.Full : StorgeState.ThreeQuartersFull;
                }
                case ThreeQuartersFull -> {
                    return sunType == SunType.Rainy ? StorgeState.Full : StorgeState.HalfFull;
                }
                case HalfFull -> {
                    return sunType == SunType.Rainy ? StorgeState.ThreeQuartersFull : StorgeState.OneQuarterFull;
                }
                case OneQuarterFull -> {
                    return sunType == SunType.Rainy ? StorgeState.HalfFull : StorgeState.Empty;
                }
                case Empty -> {
                    return sunType == SunType.Rainy ? StorgeState.OneQuarterFull : StorgeState.Empty;
                }
            }
        }

        return oldState;
    }
}
