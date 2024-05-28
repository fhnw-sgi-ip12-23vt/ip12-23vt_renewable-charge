package ch.fhnw.elektroautos.mvc.renewablecharge.controller.game;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.I2CController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.RfidController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.IRenewableChargeConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ControllerBase;
import com.pi4j.context.Context;

/**
 * Rewritten ApplicationController for the JavaFX view.
 */
public class ApplicationFXController extends ControllerBase<MainModel> {

    private final LedController ledController;
    private final RfidController rfidController;
    private final LedStripController ledStripController;
    private final I2CController i2CController;
    private final GameFXController gameController;

    private final MainModel model;

    public ApplicationFXController(Context pi4J, IRenewableChargeConfiguration configuration, MainModel model) {
        super(model);
        this.model = model;
        ledController = new LedController(model);
        rfidController = new RfidController(model);
        ledStripController = new LedStripController(model);
        gameController = new GameFXController(pi4J, configuration, model);
        i2CController = new I2CController(pi4J, 17, 0x20, 0x38);
    }

    @Override
    public void awaitCompletion() {
        super.awaitCompletion();
        ledController.awaitCompletion();
        rfidController.awaitCompletion();
        ledStripController.awaitCompletion();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        ledController.shutdown();
        rfidController.shutdown();
        ledStripController.shutdown();
    }

    /**
     * Return the game controller.
     *
     * @return  The game controller.
     */
    public GameFXController getGameController() {
        return gameController;
    }

    /**
     * Return the I2C controller.
     * @return  The I2C controller.
     */
    public I2CController getI2CController() {
        return i2CController;
    }

    /**
     * Return the translation client.
     *
     * @return  The translation client.
     */
    public TranslationClient getTranslationClient() {
        return this.model.translationClient.getValue();
    }

    /**
     * Get the Model of the application.
     * @return The model.
     */
    public MainModel getModel() {
        return model;
    }

    public void moveLanguageArrow(String direction) {
        int newIndex = direction.equals("down") ? this.model.hoveredLanguage.getValue() + 1 : this.model.hoveredLanguage.getValue() - 1;
        if (newIndex < 0) newIndex = 0;
        if (newIndex >= Configuration.get("SUPPORTED_LANGUAGES").split(";").length) newIndex = Configuration.get("SUPPORTED_LANGUAGES").split(";").length - 1;
        this.model.confirmedLanguage.setValue(false);
        this.model.hoveredLanguage.setValue(newIndex);
    }

    /**
     * Update the package.
     */
    public void updatePackage(EnergyPackage newPackage) {
        this.model.displayedPackage.setValue(newPackage);
    }

    /**
     * Update the weather.
     * @param weatherConfiguration
     */
    public void updateWeather(WeatherConfiguration weatherConfiguration) {
        this.model.displayedWeather.setValue(weatherConfiguration);
    }

    /**
     * Claim a package for a player.
     * @param   player          The player.
     * @param   energyPackage   The energy package to claim.
     * @return  True if the package was claimed successfully.
     */
    public boolean claimPackage(Player player, EnergyPackage energyPackage) {
        if (this.gameController.getGameState().getValue() != GameState.RUNNING) return false;

        if (!player.getSelectedCar().isBlocked()) {
            EnergyPackage nextEnergyPackage = this.gameController.getGame().getPackage();
            this.model.displayedWeather.setValue(this.gameController.getGame().getWeather());
            this.model.displayedPackage.setValue(nextEnergyPackage);
        }

        // Reset timer for package & weather change
        model.packageChangeTimer.setValue(gameController.getGameConfiguration().timeTillNewEnergyPackage());
        model.weatherChangeTimer.setValue(gameController.getGameConfiguration().timeTillWeatherChange());

        boolean succeeded = player.getSelectedCar().claimPackage(energyPackage, () -> {
            this.model.updatePlayers.setValue(!this.model.updatePlayers.getValue());
            if (player.getSelectedCar().getBatteryCapacityWh() == player.getSelectedCar().getChargedCapacityWh()) {
                this.getGameController().getGameState().setValue(GameState.RACE_COUNTDOWN);
            }
        });

        return succeeded;
    }

}
