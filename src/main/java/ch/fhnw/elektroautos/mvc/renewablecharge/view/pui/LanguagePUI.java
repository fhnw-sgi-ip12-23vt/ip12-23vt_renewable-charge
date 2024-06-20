package ch.fhnw.elektroautos.mvc.renewablecharge.view.pui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.I2CButton;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.PuiBase;
import com.pi4j.context.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LanguagePUI extends PuiBase<MainModel, ApplicationFXController> {

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;

    public LanguagePUI(ApplicationFXController controller, Context pi4J) {
        super(controller, pi4J);
        executorService = Executors.newSingleThreadScheduledExecutor();

        setScheduledFuture(controller);
    }

    /**
     * Sets the scheduled future for the idle mode.
     *
     * @param controller the controller
     */
    public void setScheduledFuture(ApplicationFXController controller) {
        if (Configuration.get("IDLE_MODE_ENABLED").equals("false")) {
            return;
        }
        scheduledFuture = executorService.schedule(() -> {
            controller.getLedStripController().idleMode();
        }, Integer.parseInt(Configuration.get("IDLE_MODE_TIME")), TimeUnit.SECONDS);
    }

    @Override
    public void initializeParts() {

    }

    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        controller.getI2CController().subscribe((port) -> {
            if (scheduledFuture != null && controller.getModel().gameState.getValue() == GameState.SETUP_LANGUAGE) {
                scheduledFuture.cancel(true);
                setScheduledFuture(controller);
            }

            controller.getLedStripController().stopIdleMode();
            if (I2CButton.GREEN.getPort() == port) {
                controller.moveLanguageArrow("down");
            } else if (I2CButton.BLUE.getPort() == port) {
                controller.moveLanguageArrow("up");
            } else if (I2CButton.RED.getPort() == port) {
                controller.getGameController().changeLanguage(scheduledFuture);
            }
        });

    }
}
