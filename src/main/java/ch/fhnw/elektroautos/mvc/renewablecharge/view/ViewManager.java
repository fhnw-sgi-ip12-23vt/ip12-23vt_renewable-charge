package ch.fhnw.elektroautos.mvc.renewablecharge.view;

import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.mvc.renewablecharge.AppStarter;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.I2CButton;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.gui.*;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.pui.GamePUI;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.pui.LanguagePUI;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.pui.TutorialPUI;
import com.pi4j.context.Context;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Logger;

import static ch.fhnw.elektroautos.components.catalog.SerialRFID.removeLastChar;

/**
 * The main view manager of the application. This class is responsible
 * for switching between the different scenes of the application.
 */
public class ViewManager {

    private final Logger logger = Logger.getLogger(ViewManager.class.getName());

    /**
     * The stage passed from {@link AppStarter}.
     */
    private final Stage stage;

    private boolean isFirstTimeRunning = true;

    /**
     * The language selection scene. In here, the game language
     * is being selected. Once done, it will switch to the start scene.
     */
    private Scene languageScene;

    /**
     * The tutorial scene. In here, the user will be guided through the game.
     */
    private Scene turtorialScene;

    /**
     * The second tutorial scene. In here, the user will be guided through the game.
     */
    private Scene secondTutorialScreen;

    /**
     * The third tutorial scene. In here, the user will be guided through the game.
     */
    private Scene thirdTutorialScreen;

    /**
     * The start scene. This is the basic "Press to Start" screen.
     */
    private Scene startScene;

    /**
     * The car selection scene. In here, the car is being selected.
     * Once a car is selected, the player will be registered.
     */
    private Scene carSelectionScene;

    /**
     * The game scene. This is the main game scene.
     */
    private Scene gameScene;

    /**
     * The race scene.
     */
    private Scene raceScene;

    /**
     * The result scene.
     */
    private Scene resultScene;

    /**
     * The main model of the application.
     */
    private MainModel model;

    /**
     * The id of the current scene change Event
     */
    private int currentSceneEventId = 0;

    /**
     * Default constructor of the view manager.
     *
     * @param stage      The main stage of the application.
     * @param controller The application controller.
     * @param pi4J       The Pi4J context.
     */
    public ViewManager(Stage stage, ApplicationFXController controller, MainModel model, Context pi4J) {
        this.stage = stage;
        this.model = model;
        initScenes(controller, pi4J);
    }

    /**
     * Initialize the scenes of the application.
     *
     * @param controller The application controller.
     * @param pi4J       The Pi4J context.
     */
    private void initScenes(ApplicationFXController controller, Context pi4J) {
        LanguageScreen       languageScreen       = new LanguageScreen(controller);
        TutorialScreen       tutorialScreen       = new TutorialScreen(controller);
        SecondTutorialScreen secondTutorialScreen = new SecondTutorialScreen(controller);
        ThirdTutorialScreen  thirdTutorialScreen  = new ThirdTutorialScreen(controller);
        StartScreen          startScreen          = new StartScreen(controller);
        CarSelectionScreen   carSelectionScreen   = new CarSelectionScreen(controller, pi4J);
        GameScreen   gameScreen   = new GameScreen(controller);
        RaceScreen   raceScreen   = new RaceScreen(controller);
        ResultScreen resultScreen = new ResultScreen(controller);

        this.languageScene = new Scene(languageScreen);
        this.turtorialScene = new Scene(tutorialScreen);
        this.secondTutorialScreen = new Scene(secondTutorialScreen);
        this.thirdTutorialScreen = new Scene(thirdTutorialScreen);
        this.startScene = new Scene(startScreen);
        this.carSelectionScene = new Scene(carSelectionScreen);
        this.gameScene = new Scene(gameScreen);
        this.raceScene = new Scene(raceScreen);
        this.resultScene = new Scene(resultScreen);

        listenToRfids(controller);

        /**
         * Turn on usable buttons during language selection.
         */
        controller.getI2CController().turnAllOff(List.copyOf(model.players.getValue()), false);
        controller.getI2CController().setPort(I2CButton.BLUE.getPort(), true);
        controller.getI2CController().setPort(I2CButton.GREEN.getPort(), true);
        controller.getI2CController().setPort(I2CButton.RED.getPort(), true);

        /**
         * In the language screen, the user can select the language.
         * Once done, the start screen will be shown.
         */
        LanguagePUI languagePUI = new LanguagePUI(controller, pi4J);
        languageScreen.setOnMouseClicked((e) -> switchScene(startScene));

        /**
         * In the start screen, the user can start the game.
         * Once done, the car selection screen will be shown.
         */
        startScreen.setOnMouseClicked((e) -> {
            switchScene(carSelectionScene);
            controller.getGameController().initGame();
        });

        /**
         * A listener for the game state. Once the game state is set to
         * {@link GameState#RUNNING}, the game scene will be shown.
         */
        controller.getGameController().getGameState().onChange((oldState, newState) -> {
            if (newState == GameState.SETUP_LANGUAGE && !isFirstTimeRunning) {
                Platform.runLater(() -> switchScene(languageScene));
                languagePUI.setupUiToActionBindings(controller);
                languagePUI.setScheduledFuture(controller);
            }

            isFirstTimeRunning = false;

            if (newState == GameState.TUTORIAL) {
                new TutorialPUI(controller, pi4J);
                // Turn on usable buttons during tutorial.
                controller.getI2CController().turnAllOff(List.copyOf(model.players.getValue()), false);
                controller.getI2CController().setPort(I2CButton.RED.getPort(), true);
                Platform.runLater(() -> switchScene(turtorialScene));
            }

            if (newState == GameState.TUTORIAL_SCREEN_TWO) {
                Platform.runLater(() -> switchScene(this.secondTutorialScreen));
            }

            if (newState == GameState.TUTORIAL_SCREEN_THREE) {
                Platform.runLater(() -> switchScene(this.thirdTutorialScreen));
            }

            if (newState == GameState.SETUP_START) {
                Platform.runLater(() -> {
                    controller.getI2CController().reset();
                    new GamePUI(controller, pi4J);
                    switchScene(startScene);
                    currentSceneEventId = controller.getI2CController().subscribe(port -> {
                        if (model.gameState.getValue() == GameState.SETUP_START) {
                            switchScene(carSelectionScene, controller);
                            controller.getGameController().initGame();

                            // Turn all buttons off
                            controller.getI2CController().turnAllOff(List.copyOf(model.players.getValue()), false);
                        }
                    });
                });
            }

            if (newState == GameState.RUNNING) {
                Platform.runLater(() -> {
                    controller.getGameController().handleGameCountdown();
                    switchScene(gameScene);
                    controller.getI2CController().turnAllOn(List.copyOf(model.players.getValue()), true);
                });
            }

            if (newState == GameState.RACE_COUNTDOWN) {
                Platform.runLater(() -> {
                    switchScene(raceScene);
                    controller.getGameController().startRaceCountdown();
                });
            }

            if (newState == GameState.RESULT) {
                Platform.runLater(() -> switchScene(resultScene));
            }
        });
    }

    /**
     * Switch between the different scenes of the application.
     *
     * @param scene The scene to switch to.
     */
    public void switchScene(Scene scene) {
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        });
    }

    /**
     * Switch between the different scenes of the application and unsubscribes the I2C event.
     *
     * @param scene      The scene to switch to.
     * @param controller The ApplicationFxController
     */
    public void switchScene(Scene scene, ApplicationFXController controller) {
        switchScene(scene);
        controller.getI2CController().unsubscribe(currentSceneEventId);
    }

    /**
     * Start the application. This will start with the language selection.
     */
    public void start() {
        Platform.runLater(() -> {
            switchScene(languageScene);
        }); // Start with the language selection
    }

    /**
     * * Function to listen to the RFID readers.
     */
    private void listenToRfids(ApplicationFXController controller) {
        List<Player> players      = model.players.getValue();
        SerialHelper serialHelper = model.getConfiguration().getSerialHelper();

        for (Player player : players) {

            /**
             * Handle RFID 1 & 2:
             */
            if (player.getRfidComponent() != null) {
                player.getRfidComponent().onCardDetected(card -> {
                    controller.getGameController().assignCar(player.getRfidComponent(), card.getSerial());
                });
            }

            /**
             * Handle RFID 3 & 4:
             */
            else {
                serialHelper.handleSerialRFID();
                serialHelper.subscribe(rfid -> {
                    if (rfid.contains("X")) {
                        rfid = removeLastChar(rfid);
                        controller.getGameController().assignCarById("3", rfid);
                    } else {
                        controller.getGameController().assignCarById("4", rfid);
                    }
                });
            }
        }
    }
}
