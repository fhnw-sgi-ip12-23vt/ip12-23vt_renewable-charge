package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.components.crowpi.RfidComponent;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
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
    private int resultCountdown;
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
    public LedStripController getLedStripController() {
        throw new UnsupportedOperationException("Get the controller from the Model");
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
    public int getResultCountdown() {
        return resultCountdown;
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

    @Override
    public int getGameDuration() {
        return Integer.parseInt(Configuration.get("GAME_DURATION"));
    }

    private void loadConfiguration() {
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
        this.resultCountdown = Integer.parseInt(Configuration.get("RESULT_COUNTDOWN"));
        this.timeTillNewEnergyPackage = Integer.parseInt(Configuration.get("AVERAGE_TIME_SECONDS_TILL_ENERGYPACKAGE"));
        this.timeTillWeatherChange = Integer.parseInt(Configuration.get("AVERAGE_TIME_SECONDS_TILL_WEATHER_CHANGE"));
        this.minimumRequiredPlayers = Integer.parseInt(Configuration.get("MINIMUM_REQUIRED_PLAYERS"));
    }

    private void loadPlayers() {
        RfidComponent firstRfid  = new RfidComponent(pi4j, 19, 0, 100000);
        RfidComponent secondRfid = new RfidComponent(pi4j, 21, 1, 100000);

        players = new ArrayList<>();
        players.add(new Player("3", "player1", false, I2CButton.BLUE, 0));
        players.add(new Player(secondRfid, "player2", false, I2CButton.YELLOW, 0));
        players.add(new Player("4", "player3", false, I2CButton.GREEN, 0));
        players.add(new Player(firstRfid, "player4", false, I2CButton.RED, 0));
    }

    @Override
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    private void loadCars() {
        cars = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            String carName         = Configuration.get("CAR[" + i + "].name");
            String carId           = Configuration.get("CAR[" + i + "].id");
            int    batteryCapacity = Integer.parseInt(Configuration.get("CAR[" + i + "].batteryCapacity"));
            int    maxRange        = Integer.parseInt(Configuration.get("CAR[" + i + "].maxRange"));
            cars.add(new Car(carId, carName, batteryCapacity, maxRange));
        }
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
        storageEvents.add(new StorageEvent("storage-state-full", 12, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 9, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 6, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 3, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 0.1f, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 4, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 2, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 1, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 5, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 0.1f, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 6, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 4, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 2, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 8, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 5, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 2, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 2, solarEvents, "solar.png"));
        energyTypes.add(new EnergyType("water", 0.5F, 4, waterEvents, "water.png"));
        energyTypes.add(new EnergyType("storage", 0.5F, 4, storageEvents, "pumpedhydro.png"));
        energyTypes.add(new EnergyType("wind", 0.5F, 4, windEvents, "wind.png"));
        energyTypes.add(new EnergyType("thermal", 0.5F, 4, thermalEvents, "geothermal.png"));

        return new Season("winter", energyTypes, new Random());
    }

    private Season spring() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 12, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 9, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 6, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 3, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 10, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 6, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 3, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 5, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 9, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 7, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 5, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 8, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 5, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 2, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 2, solarEvents, "solar.png"));
        energyTypes.add(new EnergyType("water", 0.5F, 4, waterEvents, "water.png"));
        energyTypes.add(new EnergyType("storage", 0.5F, 4, storageEvents, "pumpedhydro.png"));
        energyTypes.add(new EnergyType("wind", 0.5F, 4, windEvents, "wind.png"));
        energyTypes.add(new EnergyType("thermal", 0.5F, 4, thermalEvents, "geothermal.png"));

        return new Season("spring", energyTypes, new Random());
    }

    private Season summer() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 12, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 9, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 6, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 3, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 12, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 7, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 4, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 5, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 8, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 6, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 4, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 8, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 5, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 2, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 2, solarEvents, "solar.png"));
        energyTypes.add(new EnergyType("water", 0.5F, 4, waterEvents, "water.png"));
        energyTypes.add(new EnergyType("storage", 0.5F, 4, storageEvents, "pumpedhydro.png"));
        energyTypes.add(new EnergyType("wind", 0.5F, 4, windEvents, "wind.png"));
        energyTypes.add(new EnergyType("thermal", 0.5F, 4, thermalEvents, "geothermal.png"));

        return new Season("summer", energyTypes, new Random());
    }

    private Season fall() {
        //Storage Events
        List<Event> storageEvents = new ArrayList<>();
        storageEvents.add(new StorageEvent("storage-state-full", 12, StorgeState.Full));
        storageEvents.add(new StorageEvent("storage-state-three-quarter", 9, StorgeState.ThreeQuartersFull));
        storageEvents.add(new StorageEvent("storage-state-half-full", 6, StorgeState.HalfFull));
        storageEvents.add(new StorageEvent("storage-state-one-quarter", 3, StorgeState.OneQuarterFull));
        storageEvents.add(new StorageEvent("storage-state-empty", 1, StorgeState.Empty));

        //Solar Events
        List<Event> solarEvents = new ArrayList<>();
        solarEvents.add(new SolarEvent("sunny", 6, SunType.Sunny));
        solarEvents.add(new SolarEvent("cloudy", 3, SunType.Cloudy));
        solarEvents.add(new SolarEvent("rainy", 2, SunType.Rainy));

        //Thermal Events
        List<Event> thermalEvents = new ArrayList<>();
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 5, ThermalType.Operating));
        thermalEvents.add(new ThermalEvent("thermal-plant-operating", 1, ThermalType.Servicing));

        //Water Events
        List<Event> waterEvents = new ArrayList<>();
        waterEvents.add(new WaterEvent("high-water-level", 7, WaterType.HighWaterLevel));
        waterEvents.add(new WaterEvent("medium-water-level", 5, WaterType.MediumWaterLevel));
        waterEvents.add(new WaterEvent("low-water-level", 3, WaterType.LowWaterLevel));

        //Wind Events
        List<Event> windEvents = new ArrayList<>();
        windEvents.add(new WindEvent("wind-windy", 8, WindType.Windy));
        windEvents.add(new WindEvent("wind-breezy", 5, WindType.Breezy));
        windEvents.add(new WindEvent("wind-wind-still", 2, WindType.WindStill));

        // EnergyTypes
        List<EnergyType> energyTypes = new ArrayList<>();
        energyTypes.add(new EnergyType("solar", 0.5F, 2, solarEvents, "solar.png"));
        energyTypes.add(new EnergyType("water", 0.5F, 4, waterEvents, "water.png"));
        energyTypes.add(new EnergyType("storage", 0.5F, 4, storageEvents, "pumpedhydro.png"));
        energyTypes.add(new EnergyType("wind", 0.5F, 4, windEvents, "wind.png"));
        energyTypes.add(new EnergyType("thermal", 0.5F, 4, thermalEvents, "geothermal.png"));

        return new Season("fall", energyTypes, new Random());
    }

}
