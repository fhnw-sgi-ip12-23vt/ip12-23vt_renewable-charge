package ch.fhnw.elektroautos.mvc.renewablecharge.model;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.IRenewableChargeConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ObservableValue;

import java.util.List;

/**
 * In MVC the 'Model' mainly consists of 'ObservableValues'.
 * <p>
 * There should be no need for additional methods.
 * <p>
 * All the application logic is handled by the 'Controller'
 */
public class MainModel {
    private IRenewableChargeConfiguration configuration;
    public final ObservableValue<String> systemInfo = new ObservableValue<>("JavaFX "
            + System.getProperty("javafx.version")
            + ", running on Java "
            + System.getProperty("java.version")
            + ".");
    public final ObservableValue<Boolean> isActive = new ObservableValue<>(false);

    /* Game State Objects */
    public final ObservableValue<GameState> gameState = new ObservableValue<>(GameState.SETUP_LANGUAGE);

    /* Countdown States */
    public ObservableValue<Integer> countdown; // All countdowns visible in the UI.
    public ObservableValue<Integer> raceCountdown; // The countdown
    public ObservableValue<Boolean> runRaceCountdown = new ObservableValue<>(false); // The countdown is running.
    public ObservableValue<Boolean> didRaceRun = new ObservableValue<>(false);
    public ObservableValue<Boolean> readyToStartCountdown = new ObservableValue<>(false); // At least X players are needed to start the game.
    public ObservableValue<Boolean> isTimerRunning = new ObservableValue<>(false); // The countdown is running.
    public ObservableValue<Boolean> runTick = new ObservableValue<>(false); // The gameTick is running.

    public ObservableValue<Float> packageChangeTimer; // The timer until the next package change.
    public ObservableValue<Float> weatherChangeTimer; // The timer until the next weather change.
    public ObservableValue<Boolean> roundTimerRunning = new ObservableValue<>(false); // The round timer is running.

    public ObservableValue<Boolean> confirmedLanguage = new ObservableValue<>(true);
    public ObservableValue<Integer> hoveredLanguage = new ObservableValue<>(0);
    public ObservableValue<String> language;
    public ObservableValue<TranslationClient> translationClient;

    /* Game Objects */
    public ObservableValue<List<Player>> players;
    public ObservableValue<Boolean> updatePlayers = new ObservableValue<>(false); // This will be changed everytime a new card is detected.
    public ObservableValue<EnergyPackage> displayedPackage = new ObservableValue<>(null);
    public ObservableValue<WeatherConfiguration> displayedWeather = new ObservableValue<>(null);
    public ObservableValue<Player> winner = new ObservableValue<>(null);

    public MainModel(IRenewableChargeConfiguration configuration) {
        this.configuration = configuration;
        countdown = new ObservableValue<>(configuration.getLobbyCountdown());
        raceCountdown = new ObservableValue<>(configuration.getRaceCountdown());
        language = new ObservableValue<>(configuration.getSelectedLanguage());
        translationClient = new ObservableValue<>(new TranslationClient(configuration.getSelectedLanguage()));
        players = new ObservableValue<>(configuration.getPlayers());
        packageChangeTimer = new ObservableValue<>(configuration.timeTillNewEnergyPackage());
        weatherChangeTimer = new ObservableValue<>(configuration.timeTillWeatherChange());

    }

    public IRenewableChargeConfiguration getConfiguration() {
        return this.configuration;
    }

    public void reset() {
        countdown.setValue(configuration.getLobbyCountdown());
        raceCountdown.setValue(configuration.getRaceCountdown());
        language.setValue("de");
        translationClient.setValue(new TranslationClient("de"));
        players.setValue(configuration.getPlayers());
        packageChangeTimer.setValue(configuration.timeTillNewEnergyPackage());
        weatherChangeTimer.setValue(configuration.timeTillWeatherChange());
        gameState.setValue(GameState.SETUP_LANGUAGE);
        displayedPackage.setValue(null);
        displayedWeather.setValue(null);
        winner.setValue(null);
        runRaceCountdown.setValue(false);
        didRaceRun.setValue(false);
        readyToStartCountdown.setValue(false);
        isTimerRunning.setValue(false);
        runTick.setValue(false);
        roundTimerRunning.setValue(false);
        confirmedLanguage.setValue(true);
        hoveredLanguage.setValue(0);
        updatePlayers.setValue(false);
    }
}
