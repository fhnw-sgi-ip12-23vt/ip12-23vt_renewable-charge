package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.components.crowpi.RfidComponent;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyType;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.Season;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.events.*;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.*;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import com.pi4j.context.Context;

import java.util.*;

public class PropertiesRenewableChargeConfiguration implements IRenewableChargeConfiguration {

    private final HashMap<String, String> languages = new HashMap<>();
    private final String selectedLanguage = "de";
    private List<Car> cars;
    private List<Player> players;
    private List<Season> seasons;
    private Season selectedSeason;
    private float timeTillWeatherChange;
    private float timeTillNewEnergyPackage;
    private int lobbyCountdown;
    private float blockedTimePerKWh;
    private final Context pi4j;
    private int minimumRequiredPlayers;
    private final SerialHelper serialHelper;

    public PropertiesRenewableChargeConfiguration(Context context) {
        this.pi4j = context;
        this.serialHelper = new SerialHelper("/dev/ttyS0");
        loadConfiguration();
    }

    public SerialHelper getSerialHelper() {
        return serialHelper;
    }

    @Override
    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    @Override
    public Season getSelectedSeason() {
        return selectedSeason;
    }

    @Override
    public List<Car> getCars() {
        return cars;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the likelihood of the weather changing
     *
     * @return the chance of weather change factor
     */
    public float timeTillWeatherChange() {
        return timeTillWeatherChange;
    }

    @Override
    public float timeTillNewEnergyPackage() {
        return timeTillNewEnergyPackage;
    }

    @Override
    public int getMinimumRequiredPlayers() {
        return minimumRequiredPlayers;
    }

    @Override
    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    @Override
    public float getBlockedTimePerKWh() {
        return blockedTimePerKWh;
    }

    @Override
    public HashMap<String, String> getConfiguredLanguages() {
        return languages;
    }

    @Override
    public int getRaceCountdown() {
        return Integer.parseInt(Configuration.get("RACE_COUNTDOWN"));
    }

    private void loadConfiguration() {
        System.out.println("version: " + Configuration.get("version"));
        loadAllLanguages();
        loadSeasons();
        loadCars();
        loadPlayers();
        loadGameParameters();

        //Season selection
        var random            = new Random();
        int randomSeasonIndex = random.nextInt(seasons.size());
        this.selectedSeason = seasons.get(randomSeasonIndex);
    }


    private void loadGameParameters() {
        this.blockedTimePerKWh = Float.parseFloat(Configuration.get("TIME_BLOCKED_PER_CHARGED_KWH"));
        this.lobbyCountdown = Integer.parseInt(Configuration.get("LOBBY_COUNTDOWN"));
        this.timeTillNewEnergyPackage = Integer.parseInt(Configuration.get("AVERAGE_TIME_SECONDS_TILL_ENERGYPACKAGE"));
        this.timeTillWeatherChange = Integer.parseInt(Configuration.get("AVERAGE_TIME_SECONDS_TILL_WEATHER_CHANGE"));
        this.minimumRequiredPlayers = Integer.parseInt(Configuration.get("MINIMUM_REQUIRED_PLAYERS"));
    }

    private void loadPlayers() {

        RfidComponent player1Rfid = new RfidComponent(pi4j, 25, 0, 100000);
        RfidComponent player2Rfid = new RfidComponent(pi4j, 5, 1, 100000);

        //LedStrip player1LedStrip = new LedStrip(0, serialHelper);
        LedStrip player1LedStrip = new LedStrip(0, serialHelper);
        LedStrip player2LedStrip = new LedStrip(1, serialHelper);
        LedStrip player3LedStrip = new LedStrip(2, serialHelper);
        LedStrip player4LedStrip = new LedStrip(3, serialHelper);

        LedStrip player1ChargeStrip = new LedStrip(4, serialHelper);
        LedStrip player2ChargeStrip = new LedStrip(5, serialHelper);
        LedStrip player3ChargeStrip = new LedStrip(6, serialHelper);
        LedStrip player4ChargeStrip = new LedStrip(7, serialHelper);

        players = new ArrayList<>();
        players.add(new Player(player1Rfid, player1LedStrip, "player1", false, I2CButton.YELLOW, player1ChargeStrip, 0));
        players.add(new Player(player2Rfid, player2LedStrip, "player2", false, I2CButton.BLUE, player2ChargeStrip, 0));
        players.add(new Player("3", player3LedStrip, "player3", false, I2CButton.GREEN, player3ChargeStrip, 0));
        players.add(new Player("4", player4LedStrip, "player4", false, I2CButton.RED, player4ChargeStrip, 0));
    }

    private void loadCars() {
        cars = new ArrayList<>();
        cars.add(new Car("50EBEB1B:468446032", "car1", 100_000, 500));
        cars.add(new Car("613ED427:668221025", "car2", 100_000, 420));
        cars.add(new Car("50BA3A1B:456833616", "car3", 100_000, 450));
        cars.add(new Car("F2B32A1A:439006194", "car4", 100_000, 480));
    }

    private void loadAllLanguages() {
        var propValue = Configuration.get("SUPPORTED_LANGUAGES");
        for (String language : propValue.split(";")) {
            var splitLanguage = language.split(":");
            languages.put(splitLanguage[0], splitLanguage[1]);
        }
    }

    private void loadSeasons() {
        // Seasons
        seasons = new ArrayList<>();
        seasons.add(winter());
        seasons.add(spring());
        seasons.add(summer());
        seasons.add(fall());
    }

    private Season winter() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 2, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 1, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 1, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 1, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 2, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 1, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 0.5F, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 2, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 2, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 1, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 0.5F, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 2, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 1, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 0.5F, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 20000, solarEvents));
        energyTypes.add(new EnergyType("water", 0.5F, 40000, waterEvents));

        return new Season("winter", energyTypes, new Random());
    }

    private Season spring() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 2, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 1, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 1, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 1, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 2, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 1, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 0.5F, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 2, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 2, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 1, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 0.5F, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 2, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 1, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 0.5F, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 20000, solarEvents));
        energyTypes.add(new EnergyType("water", 0.5F, 40000, waterEvents));

        return new Season("spring", energyTypes, new Random());
    }

    private Season summer() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 2, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 1, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 1, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 1, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 2, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 1, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 0.5F, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 2, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 2, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 1, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 0.5F, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 2, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 1, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 0.5F, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 20000, solarEvents));
        energyTypes.add(new EnergyType("water", 0.5F, 40000, waterEvents));

        return new Season("summer", energyTypes, new Random());
    }

    private Season fall() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 2, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 1, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 1, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 1, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 2, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 1, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 0.5F, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 2, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 2, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 1, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 0.5F, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 2, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 1, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 0.5F, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 20000, solarEvents));
        energyTypes.add(new EnergyType("water", 0.5F, 40000, waterEvents));

        return new Season("fall", energyTypes, new Random());
    }

}
