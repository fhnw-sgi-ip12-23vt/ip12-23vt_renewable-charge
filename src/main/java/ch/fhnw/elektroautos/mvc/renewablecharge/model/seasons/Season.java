package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Season {
    private final String translatePropName;
    private final List<EnergyType> energyTypes;
    private final String imageName;
    private final Random random;

    /**
     * Constructs a new Season object with the given name and energy types.
     *
     * @param translatePropName the name of the season
     * @param energyTypes       the list of energy types associated with the season
     * @throws IllegalArgumentException if energyTypes is null or empty
     */
    public Season(String translatePropName, List<EnergyType> energyTypes, Random random) {
        this.translatePropName = Objects.requireNonNull(translatePropName, "Name cannot be null");
        this.imageName = translatePropName.toLowerCase() + ".jpg";
        this.energyTypes = Objects.requireNonNull(energyTypes, "Energy types cannot be null");
        if (energyTypes.isEmpty()) {
            throw new IllegalArgumentException("Energy types list cannot be empty");
        }
        this.random = Objects.requireNonNull(random, "Random object cannot be null");
    }

    /**
     * Retrieves the name of the season.
     *
     * @return the name of the season
     */
    public String getTranslatePropName() {
        return translatePropName;
    }

    /**
     * Retrieves a randomly generated energy package based on the available energy types.
     *
     * @return a randomly generated energy package
     */
    public EnergyPackage getPackage(WeatherConfiguration weatherConfiguration) {
        int        randomTypeIndex = random.nextInt(this.energyTypes.size());
        EnergyType randomType      = energyTypes.get(randomTypeIndex);
        return randomType.getWeatherSpecificPackage(weatherConfiguration);
    }

    /**
     * The name of the image, which represents the season.
     * The images are located in /resources/backgrounds/{name}
     *
     * @return The name of the image
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Returns a string representation of the Season object.
     *
     * @return a string representation of the Season object
     */
    @Override
    public String toString() {
        return "Season{" +
                "name='" + translatePropName + '\'' +
                ", imageName='" + imageName + '\'' +
                ", energyTypes=" + energyTypes +
                '}';
    }
}
