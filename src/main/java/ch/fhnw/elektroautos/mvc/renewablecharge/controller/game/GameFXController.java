package ch.fhnw.elektroautos.mvc.renewablecharge.controller.game;

import ch.fhnw.elektroautos.components.crowpi.RfidComponent;
import ch.fhnw.elektroautos.mvc.renewablecharge.AppStarter;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.*;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ObservableValue;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * Rewritten GameController for the JavaFX view.
 * <br />
 * TODO: This is currently using {@link MockupRenewableChargeConfiguration}.
 */
public class GameFXController {

    /**
     * The logger for this class.
     */
    private final Logger logger = Logger.getLogger(GameFXController.class.getName());

    /**
     * The game client. Includes various functions
     * to get game information.
     */
    private final RenewableChargeGame game;

    /**
     * The game configuration taken from the game client.
     */
    private final IRenewableChargeConfiguration gameConfiguration;

    /**
     * The translation client. This client is used to translate
     * the game messages.
     */
    private TranslationClient translationClient;

    /**
     * The main model for the UI.
     */
    private final MainModel model;

    /**
     * Whether the game is running.
     */
    private boolean isRunning = false;

    /**
     * The max amount of LEDs per race strip.
     */
    private static final int MAX_LED_STRIP_LEDS = 25;

    /**
     * The LEDStripController.
     */
    private final LedStripController ledStripController;

    /**
     * The constructor for the controller.
     *
     * @param aModel The main model for the UI.
     */
    public GameFXController(IRenewableChargeConfiguration configuration, MainModel aModel, LedStripController ledStripController) {
        this.model = aModel;
        this.ledStripController = ledStripController;

        /*
         * This loads the events, energy types & players. It will also
         * select a specific random season. The car selection needs to be done
         * here before the game starts.
         */
        this.gameConfiguration = configuration;
        this.game = new RenewableChargeGame(gameConfiguration);

        /*
         * Initialize the translations & listen to the language changes.
         */
        this.translationClient = new TranslationClient(this.model.language.getValue());

        this.model.language.onChange((oldValue, newValue) -> {
            try {
                if (newValue == null) return;
                initTranslations(newValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This function is called before the game starts.
     * It creates all the players and configurations for the game
     * and waits until cars are selected.
     */
    public void initGame() {
        /*
         * Get the players and add them to the model.
         */
        this.model.players.setValue(new ArrayList<>(this.gameConfiguration.getPlayers()));

        /*
         * Tell the model we're now in the lobby phase.
         */
        this.model.gameState.setValue(GameState.LOBBY);
        this.model.countdown.setValue(this.gameConfiguration.getLobbyCountdown());
        this.model.runTick.setValue(true);
        this.isRunning = true;
        this.startGame();
    }

    /**
     * Initializes the translations for the game into the model.
     */
    public void initTranslations(String language) {
        TranslationClient newClient = new TranslationClient(language);
        this.model.translationClient.setValue(newClient);
    }

    /**
     * Function to select a car. This is triggered by the UI once
     * an RFID Chip is detected. The player that selects the car is
     * the owner of the RFID Reader.
     *
     * @param rfid   The RFID component that detected the RFID chip.
     * @param chipId The ID of the RFID chip of the car.
     */
    public void assignCar(RfidComponent rfid, String chipId) {
        if (this.model.gameState.getValue() != GameState.LOBBY) {
            return;
        }
        for (Player player : this.gameConfiguration.getPlayers()) {
            if (player.getRfId() == null && player.getRfidComponent().equals(rfid)) {
                findAndAddCar(player, chipId);
            }
        }
    }

    /**
     * Function to select a car. This is triggered by the UI once
     * an RFID Chip is detected. The player that selects the car is
     * the owner of the RFID Reader.
     * <p>
     * The difference to {@link #assignCar(RfidComponent, String)} is that this function
     * uses the RFID ID instead of the RFID component. This is used for the external
     * RFID readers 3 & 4, which are powered by PICO-PI.
     *
     * @param rfId   The RFID ID.
     * @param chipId The ID of the RFID chip of the car.
     */
    public void assignCarById(String rfId, String chipId) {
        if (this.model.gameState.getValue() != GameState.LOBBY) {
            return;
        }
        for (Player player : this.gameConfiguration.getPlayers()) {
            if (player.getRfidComponent() == null && player.getRfId().equals(rfId)) {
                findAndAddCar(player, chipId);
            }
        }
    }

    /**
     * Helper function to find and add a car to a player.
     * TODO: Set private later.
     */
    public void findAndAddCar(Player player, String chipId) {
        Car car = game.getGameConfiguration().getCars().stream()
                      .filter(c -> c.getChipIds().contains(chipId))
                      .findFirst()
                      .orElse(null);
        if (car == null) {
            System.out.println("Car not found!");
        } else {

            /*
             * Check if car is already selected by another player.
             */
            List<Car> selectedCars = this.gameConfiguration.getPlayers().stream()
                                                           .map(Player::getSelectedCar)
                                                           .filter(selectedCar -> selectedCar != null)
                                                           .toList();

            if (selectedCars.contains(car)) {
                return;
            }

            int readyPlayers = selectedCars.size() + 1;
            if (readyPlayers >= gameConfiguration.getMinimumRequiredPlayers()) {
                this.model.readyToStartCountdown.setValue(true);
            }

            player.selectCar(car);
            this.model.updatePlayers.setValue(!this.model.updatePlayers.getValue());
        }
    }

    /**
     * Start the game.
     */
    public void startGame() {
        Thread gameThread = new Thread(() -> {
            long         lastTime      = System.nanoTime();
            final double amountOfTicks = 60.0; // Adjusted tick rate
            double       ns            = 1_000_000_000 / amountOfTicks;
            double       delta         = 0;

            while (this.isRunning) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;

                while (delta >= 1) {
                    if (this.model.runTick.getValue()) {
                        Platform.runLater(this::gameTick);
                    }
                    delta--;
                }

                // Frame cap to limit the loop speed
                long sleepTime = (lastTime + (long) ns) - System.nanoTime();
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }, "GameThread");
        gameThread.setDaemon(true); // Ensures the thread doesn't prevent application exit
        gameThread.start();
    }

    /**
     * This function is called when the race is about to start.
     */
    public void startRaceCountdown() {
        this.ledStripController.deactivateAllLeds();
        this.model.raceCountdown.setValue(this.gameConfiguration.getRaceCountdown());
        if (!this.model.isTimerRunning.getValue()) {
            model.isTimerRunning.setValue(true);
            Timer timer = new Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (model.didRaceRun.getValue()) timer.cancel();
                    int countdownValue = model.raceCountdown.getValue() - 1;
                    model.raceCountdown.setValue(countdownValue);
                    if (countdownValue == 0) {
                        model.gameState.setValue(GameState.RACE_RUNNING);
                        model.isTimerRunning.setValue(false);
                        model.runTick.setValue(true);
                        model.didRaceRun.setValue(true);
                        timer.cancel();
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * This function is called when the race starts.
     * It will run the LED strips of all players.
     */
    public void startRace() {
        List<Boolean> playerFinished = new ArrayList<>();
        Player winner = this.gameConfiguration.getPlayers().stream()
                                              .filter(p -> p.getSelectedCar() != null)
                                              .max(Comparator.comparingInt(p ->
                                                      getDistance(p.getSelectedCar()
                                                                   .getRangeInKm(), game.raceLength(), MAX_LED_STRIP_LEDS)
                                              ))
                                              .orElse(null);

        for (Player player : this.model.players.getValue()) {
            if (player.getSelectedCar() == null) continue;
            int amountOfLedsToLight = getDistance(player.getSelectedCar()
                                                        .getRangeInKm(), game.raceLength(), MAX_LED_STRIP_LEDS);

            // Light up some of the leds every second until all are lit
            Timer timer = new Timer();
            timer.schedule(new java.util.TimerTask() {
                int currentLed = 0;

                @Override
                public void run() {
                    if (currentLed == amountOfLedsToLight) {
                        playerFinished.add(true);
                        timer.cancel();
                    } else {
                        ledStripController.setLedStripColor(ledStripController.getRaceLedOfPlayer(player.getTranslationPropName()),
                                ledStripController.getColorOfPlayer(player)
                                , currentLed);
                        currentLed++;
                    }
                }
            }, 0, 500);
        }

        // Check if all players have finished
        Timer checkTimer = new Timer();
        checkTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (playerFinished.size() == model.players.getValue().size()) {
                    model.winner.setValue(winner);
                    model.countdown.setValue(gameConfiguration.getResultCountdown());
                    model.gameState.setValue(GameState.RESULT);
                    startTimer(GameState.SHUTDOWN, () -> {
                        ledStripController.deactivateAllLeds();
                        resetGame();
                    });
                    checkTimer.cancel();
                }
            }
        }, 0, 500);
    }

    /**
     * The game tick.
     */
    private void gameTick() {
        switch (this.model.gameState.getValue()) {
            case LOBBY:
                if (this.model.readyToStartCountdown.getValue()) {
                    handleLobby();
                }
                break;
            case RUNNING:
                handleRoundTimer();
                if (this.model.packageChangeTimer.getValue() <= 0) {
                    this.model.packageChangeTimer.setValue(gameConfiguration.timeTillNewEnergyPackage());
                    this.model.displayedPackage.setValue(this.game.getPackage());
                }
                if (this.model.weatherChangeTimer.getValue() <= 0) {
                    this.model.weatherChangeTimer.setValue(gameConfiguration.timeTillWeatherChange());
                    this.model.displayedWeather.setValue(this.game.getWeather());
                }
                break;
            case RESULT:
                model.runTick.setValue(false);
                break;
        }
    }

    private void resetGame() {
        System.exit(0);
    }

    /**
     * Handle the {@link GameState#LOBBY} state.
     * This will start the lobby timer.
     */
    private void handleLobby() {
        if (!this.model.isTimerRunning.getValue()) {
            startTimer(GameState.RUNNING, () -> {
                List<Player> playersWithCars = model.players.getValue();
                playersWithCars.removeIf(player -> player.getSelectedCar() == null);
                model.countdown.setValue(gameConfiguration.getGameDuration());
                model.players.setValue(playersWithCars);
                gameConfiguration.setPlayers(playersWithCars);
            });
        }
    }

    /**
     * Increases the round timer by one second.
     */
    private void handleRoundTimer() {
        if (this.model.roundTimerRunning.getValue()) return;
        this.model.roundTimerRunning.setValue(true);

        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (model.displayedPackage.getValue() == null) {
                    model.packageChangeTimer.setValue(model.packageChangeTimer.getValue() - 1);
                }

                model.weatherChangeTimer.setValue(model.weatherChangeTimer.getValue() - 1);

                if (model.gameState.getValue() != GameState.RUNNING) {
                    model.roundTimerRunning.setValue(false);
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    /**
     * Handle the game countdown.
     * This will start the game countdown.
     */
    public void handleGameCountdown() {
        this.model.countdown.setValue(gameConfiguration.getGameDuration());
        if (!this.model.isTimerRunning.getValue()) {
            model.isTimerRunning.setValue(true);
            Timer timer = new Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (model.didRaceRun.getValue()) cancel();
                    int countdownValue = model.countdown.getValue() - 1;
                    model.countdown.setValue(countdownValue);
                    if (countdownValue == 0) {
                        model.isTimerRunning.setValue(false);
                        model.runTick.setValue(true);
                        if (!model.runRaceCountdown.getValue()) {
                            model.runRaceCountdown.setValue(true);
                            model.gameState.setValue(GameState.RACE_COUNTDOWN);
                        }
                        cancel();
                    }
                }

                @Override
                public boolean cancel() {
                    model.isTimerRunning.setValue(false);
                    model.countdown.setValue(0);
                    return super.cancel();
                }
            }, 0, 1000);
        }
    }

    /**
     * Helper function to start a timer.
     */
    private void startTimer(GameState toState, Runnable executeOnFinish) {
        model.isTimerRunning.setValue(true);
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                int countdownValue = model.countdown.getValue() - 1;
                model.countdown.setValue(countdownValue);
                if (countdownValue == 0) {
                    model.gameState.setValue(toState);
                    model.isTimerRunning.setValue(false);
                    model.runTick.setValue(true);
                    if (executeOnFinish != null) {
                        executeOnFinish.run();
                    }
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    /**
     * Get the game state.
     *
     * @return The game state.
     */
    public ObservableValue<GameState> getGameState() {
        return this.model.gameState;
    }

    /**
     * Change the language of the game.
     */
    public void changeLanguage(ScheduledFuture<?> scheduledFuture) {
        int    currentIndex = this.model.hoveredLanguage.getValue();
        String newLanguage  = Configuration.get("SUPPORTED_LANGUAGES").split(";")[currentIndex].split(":")[0];
        if (!this.model.confirmedLanguage.getValue()) {
            this.model.confirmedLanguage.setValue(true);
        } else {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            this.model.language.setValue(newLanguage);
            this.model.gameState.setValue(GameState.TUTORIAL);
            return;
        }
        this.model.language.setValue(newLanguage);
    }

    /**
     * Get the game configuration.
     *
     * @return The game configuration.
     */
    public IRenewableChargeConfiguration getGameConfiguration() {
        return model.getConfiguration();
    }

    /**
     * Get the game and its functions.
     *
     * @return The game.
     */
    public RenewableChargeGame getGame() {
        return game;
    }

    /**
     * Generates a visual representation of the distance traveled by a car.
     *
     * @param maxDistance      The maximum distance to represent.
     * @param achievedDistance The actual distance achieved.
     * @param numberOfLeds     The number of LEDs available for visualization.
     * @return The amount of LEDs to light up.
     */
    private int getDistance(int maxDistance, int achievedDistance, int numberOfLeds) {
        var rangePerLED = Math.max(1, achievedDistance / numberOfLeds);
        return maxDistance / rangePerLED;
    }


}
