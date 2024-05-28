package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.components.crowpi.RfidComponent;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyType;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.Season;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events.Event;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events.SolarEvent;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events.WaterEvent;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.SunType;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WaterType;
import com.pi4j.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This class represents the configuration of the game.
 */
public class MockupRenewableChargeConfiguration implements IRenewableChargeConfiguration {
    private String selectedLanguage = "en";
    private List<Car> cars;
    private List<Player> players;
    private List<Season> seasons;
    private Context pi4J;
    private Season selectedSeason;
    private float chanceOfWeatherchange;
    private float chanceOfEnergyPackage;
    private MainModel mainModel;

    /**
     * Constructs a new GameConfiguration.
     */
    public MockupRenewableChargeConfiguration(Context aContext) {
        this.pi4J = aContext;
        loadConfig();
    }

    public MockupRenewableChargeConfiguration(Context aContext, MainModel mainModel){
        this.pi4J = aContext;
        this.mainModel = mainModel;
        loadConfig();
    }

    /**
     * Gets the selected language of the game.
     *
     * @return The selected language.
     */
    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    /**
     * Gets the selected season of the game.
     *
     * @return The selected season.
     */
    public Season getSelectedSeason() {
        return selectedSeason;
    }

    /**
     * Gets the list of cars in the game.
     *
     * @return The list of cars.
     */
    public List<Car> getCars() {
        return cars;
    }

    /**
     * Gets the list of players in the game.
     *
     * @return The list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the likelihood of the weather changing
     *
     * @return the chance of weather change factor
     */
    public float timeTillWeatherChange() {
        return chanceOfWeatherchange;
    }

    public float timeTillNewEnergyPackage() {
        return chanceOfEnergyPackage;
    }

    @Override
    public int getMinimumRequiredPlayers() {
        return 2;
    }

    @Override
    public int getLobbyCountdown() {
        return 0;
    }

    @Override
    public float getBlockedTimePerKWh() {
        return 0;
    }

    @Override
    public HashMap<String, String> getConfiguredLanguages() {
        return null;
    }

    @Override
    public int getRaceCountdown() {
        return 3;
    }

    @Override
    public SerialHelper getSerialHelper() {
        return null;
    }

    /**
     * Loads a mockup configuration for testing purposes.
     */
    private void loadConfig() {

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 2, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 1, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 0.5F, SunType.Rainy));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 2, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 1, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 0.5F, WaterType.LowWaterLevel));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 20000, solarEvents));
        energyTypes.add(new EnergyType("water", 0.5F, 40000, waterEvents));

        // Seasons
        seasons = new ArrayList<>();
        seasons.add(new Season("spring", energyTypes, new Random()));

        // Cars
        // The ChipIDs are from the chips itself.
        cars = new ArrayList<>();
        cars.add(new Car("50EBEB1B:468446032", "car1", 100_000, 500));
        cars.add(new Car("613ED427:668221025", "car2", 100_000, 420));
        cars.add(new Car("50BA3A1B:456833616", "car3", 100_000, 450));
        cars.add(new Car("F2B32A1A:439006194", "car4", 100_000, 480));

        // RFID Components (Car detection)
        RfidComponent player1Rfid = new RfidComponent(pi4J, 25, 0, 100000);
        RfidComponent player2Rfid = new RfidComponent(pi4J, 21, 1, 100000);

        // LED Strips (Race LED)
        //LedStrip player1LedStrip = new LedStrip(0, mainModel.serialHelper);
        LedStrip player1LedStrip = null;
        LedStrip player2LedStrip = null;
        LedStrip player3LedStrip = null;
        LedStrip player4LedStrip = null;

        // Players
        players = new ArrayList<>();
        players.add(new Player(player1Rfid, player1LedStrip, "player1", false, I2CButton.YELLOW, null, 0));
        players.add(new Player(player2Rfid, player2LedStrip, "player2", false, I2CButton.BLUE, null, 0));
        players.add(new Player("3", player3LedStrip, "player3", false, I2CButton.GREEN, null, 0));
        players.add(new Player("4", player4LedStrip, "player4", false, I2CButton.RED, null, 0));

        // Add cars to players
        /*
        for (int i = 0; i < players.size(); i++) {
            players.get(i).selectCar(cars.get(i));
        }
         */

        //Season selection
        var random            = new Random();
        int randomSeasonIndex = random.nextInt(seasons.size());
        this.selectedSeason = seasons.get(randomSeasonIndex);

        this.chanceOfEnergyPackage = 0.2F;
        this.chanceOfWeatherchange = 0.5F;
    }

}
