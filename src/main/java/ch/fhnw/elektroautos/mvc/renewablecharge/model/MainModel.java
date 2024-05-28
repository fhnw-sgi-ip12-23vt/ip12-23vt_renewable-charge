package ch.fhnw.elektroautos.mvc.renewablecharge.model;

import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.IRenewableChargeConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ObservableValue;

import java.util.ArrayList;
import java.util.HashMap;
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
    public final ObservableValue<Integer> countdown; // All countdowns visible in the UI.
    public final ObservableValue<Boolean> readyToStartCountdown = new ObservableValue<>(false); // At least X players are needed to start the game.
    public final ObservableValue<Boolean> isTimerRunning = new ObservableValue<>(false); // The countdown is running.
    public final ObservableValue<Boolean> runTick = new ObservableValue<>(false); // The gameTick is running.

    public final ObservableValue<Float> packageChangeTimer; // The timer until the next package change.
    public final ObservableValue<Float> weatherChangeTimer; // The timer until the next weather change.
    public final ObservableValue<Boolean> roundTimerRunning = new ObservableValue<>(false); // The round timer is running.

    public final ObservableValue<Boolean> confirmedLanguage = new ObservableValue<>(false);
    public final ObservableValue<Integer> hoveredLanguage = new ObservableValue<>(0);
    public final ObservableValue<String> language;
    public final ObservableValue<TranslationClient> translationClient;

    /* Game Objects */
    public final ObservableValue<List<Player>> players;
    public final ObservableValue<Boolean> updatePlayers = new ObservableValue<>(false); // This will be changed everytime a new card is detected.
    public final ObservableValue<EnergyPackage> displayedPackage = new ObservableValue<>(null);
    public final ObservableValue<WeatherConfiguration> displayedWeather = new ObservableValue<>(null);
    public final ObservableValue<Player> winner = new ObservableValue<>(null);

    //public final SerialHelper serialHelper = new SerialHelper("/dev/ttyS0");

    public MainModel(IRenewableChargeConfiguration configuration){
        this.configuration = configuration;
        countdown = new ObservableValue<>(configuration.getLobbyCountdown());
        language = new ObservableValue<>(configuration.getSelectedLanguage());
        translationClient = new ObservableValue<>(new TranslationClient(configuration.getSelectedLanguage()));
        players = new ObservableValue<>(configuration.getPlayers());
        packageChangeTimer = new ObservableValue<>(configuration.timeTillNewEnergyPackage());
        weatherChangeTimer = new ObservableValue<>(configuration.timeTillWeatherChange());
    }

    public IRenewableChargeConfiguration getConfiguration(){
        return this.configuration;
    }
}
