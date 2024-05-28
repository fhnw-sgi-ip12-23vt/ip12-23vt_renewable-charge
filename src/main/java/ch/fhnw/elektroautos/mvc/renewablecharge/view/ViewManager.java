package ch.fhnw.elektroautos.mvc.renewablecharge.view;

import ch.fhnw.elektroautos.mvc.renewablecharge.AppStarter;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.I2CButton;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.gui.*;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.pui.LanguagePUI;
import com.pi4j.context.Context;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

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

    /**
     * The language selection scene. In here, the game language
     * is being selected. Once done, it will switch to the start scene.
     */
    private Scene languageScene;

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
     * @param   stage       The main stage of the application.
     * @param   controller  The application controller.
     * @param   pi4J        The Pi4J context.
     */
    public ViewManager(Stage stage, ApplicationFXController controller, MainModel model, Context pi4J)
    {
        this.stage = stage;
        this.model = model;
        initScenes(controller, pi4J);
    }

    /**
     * Initialize the scenes of the application.
     *
     * @param   controller  The application controller.
     * @param   pi4J        The Pi4J context.
     */
    private void initScenes(ApplicationFXController controller, Context pi4J)
    {
        LanguageScreen languageScreen = new LanguageScreen(controller);
        StartScreen startScreen = new StartScreen(controller);
        CarSelectionScreen carSelectionScreen = new CarSelectionScreen(controller, pi4J);
        GameScreen gameScreen = new GameScreen(controller, pi4J);
        RaceScreen raceScreen = new RaceScreen(controller);
        ResultScreen resultScreen = new ResultScreen(controller);

        this.languageScene = new Scene(languageScreen);
        this.startScene = new Scene(startScreen);
        this.carSelectionScene = new Scene(carSelectionScreen);
        this.gameScene = new Scene(gameScreen);
        this.raceScene = new Scene(raceScreen);
        this.resultScene = new Scene(resultScreen);

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
            if (newState != GameState.SETUP_LANGUAGE) {
                logger.info("Shutting down language PUI");
                languagePUI.shutdown();
            }

            if (newState == GameState.SETUP_START) {
                Platform.runLater(() -> {
                    System.out.println(controller.hashCode());
                    controller.getI2CController().reset();
                    switchScene(startScene);
                    currentSceneEventId = controller.getI2CController().subscribe(port -> {
                        if (model.gameState.getValue() == GameState.SETUP_START){
                            switchScene(carSelectionScene, controller);
                            controller.getGameController().initGame();
                        }
                    });
                });
            }

            if (newState == GameState.RUNNING) {
                Platform.runLater(() -> switchScene(gameScene));
            }

            if (newState == GameState.RACE_COUNTDOWN) {
                Platform.runLater(() -> {
                    switchScene(raceScene);
                    controller.getGameController().startRaceCountdown();
                });
            }

            if (newState == GameState.RESULT) {
                System.out.println("Switching to result scene");
                Platform.runLater(() -> switchScene(resultScene));
            }
        });
    }

    /**
     * Switch between the different scenes of the application.
     *
     * @param   scene   The scene to switch to.
     */
    public void switchScene(Scene scene)
    {
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setMaximized(false);  // It needs to be set to false first
            stage.setMaximized(true);   // Then set to true to make it fullscreen
        });
    }

    /**
     * Switch between the different scenes of the application and unsubscribes the I2C event.
     *
     * @param scene The scene to switch to.
     * @param controller The ApplicationFxController
     */
    public void switchScene(Scene scene, ApplicationFXController controller)
    {
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setMaximized(false);  // It needs to be set to false first
            stage.setMaximized(true);   // Then set to true to make it fullscreen
        });
        controller.getI2CController().unsubscribe(currentSceneEventId);
    }

    /**
     * Start the application. This will start with the language selection.
     */
    public void start()
    {
        Platform.runLater(() -> switchScene(languageScene)); // Start with the language selection
    }
}
