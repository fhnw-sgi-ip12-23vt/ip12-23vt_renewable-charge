package ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events.Event;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;

import java.util.List;
import java.util.Objects;

public class EnergyType {
    private String translatePropName;
    private float chancesOfEvent; // 0 to 1
    private int averagePackageSize;
    private final List<Event> events;

    /**
     * Constructs a new EnergyType object with the given parameters.
     *
     * @param translatePropName              the name of the energy type
     * @param chancesOfEvent    the chances of an event happening (0 to 1)
     * @param averagePackageSize the average package size
     * @param events            the list of events associated with the energy type
     * @throws IllegalArgumentException if the name is null or empty, or if the chancesOfEvent is out of range
     */
    public EnergyType(String translatePropName, float chancesOfEvent, int averagePackageSize, List<Event> events) {
        this.translatePropName = Objects.requireNonNull(translatePropName, "Name cannot be null");
//        if (name.isEmpty()) {
//            throw new IllegalArgumentException("Name cannot be empty");
//        }
        if (chancesOfEvent < 0 || chancesOfEvent > 1) {
            throw new IllegalArgumentException("Chances of event must be between 0 and 1");
        }
        this.chancesOfEvent = chancesOfEvent;
        this.averagePackageSize = averagePackageSize;
        this.events = Objects.requireNonNull(events, "Events cannot be null");
        if (events.isEmpty()) {
            throw new IllegalArgumentException("Events list cannot be empty");
        }
    }

    /**
     * Generates a random energy package based on the configured events.
     *
     * @return a randomly generated energy package
     */
    public EnergyPackage getWeatherSpecificPackage(WeatherConfiguration weatherConfiguration) {
        Event eventForWeather = events.stream().filter(e -> e.matchesWeather(weatherConfiguration)).findFirst().orElseThrow();
        int   size            = (int) (this.averagePackageSize * eventForWeather.getMultiplier());
        return new EnergyPackage(size, this, eventForWeather);
    }

    // Getters and setters

    public String getTranslatePropName() {
        return translatePropName;
    }

    public void setTranslatePropName(String translatePropName) {
        this.translatePropName = translatePropName;
    }

    public float getChancesOfEvent() {
        return chancesOfEvent;
    }

    public void setChancesOfEvent(float chancesOfEvent) {
        this.chancesOfEvent = chancesOfEvent;
    }

    public int getAveragePackageSize() {
        return averagePackageSize;
    }

    public void setAveragePackageSize(int averagePackageSize) {
        this.averagePackageSize = averagePackageSize;
    }

    public List<Event> getEvents() {
        return events;
    }

    /**
     * Returns a string representation of the EnergyType object.
     *
     * @return a string representation of the EnergyType object
     */
    @Override
    public String toString() {
        return "EnergyType{" +
                "name='" + translatePropName + '\'' +
                ", chancesOfEvent=" + chancesOfEvent +
                ", averagePackageSize=" + averagePackageSize +
                ", events=" + events +
                '}';
    }
}
