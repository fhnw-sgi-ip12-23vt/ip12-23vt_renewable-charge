package ch.fhnw.elektroautos.mvc.renewablecharge.view.pui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.I2CButton;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.util.mvcbase.PuiBase;
import com.pi4j.context.Context;

public class TutorialPUI extends PuiBase<MainModel, ApplicationFXController> {

    public TutorialPUI(ApplicationFXController controller, Context pi4J) {
        super(controller, pi4J);
    }

    private static boolean SCREEN_ONE_DISPLAYED = false;
    private static boolean SCREEN_TWO_DISPLAYED = false;

    @Override
    public void initializeParts() {
    }

    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        controller.getI2CController().subscribe((port) -> {
            if (I2CButton.RED.getPort() == port && !SCREEN_ONE_DISPLAYED) {
                SCREEN_ONE_DISPLAYED = true;
                controller.getGameController().getGameState().setValue(GameState.TUTORIAL_SCREEN_TWO);
            } else if (I2CButton.RED.getPort() == port && SCREEN_ONE_DISPLAYED && !SCREEN_TWO_DISPLAYED) {
                SCREEN_TWO_DISPLAYED = true;
                controller.getGameController().getGameState().setValue(GameState.TUTORIAL_SCREEN_THREE);
            } else if (I2CButton.RED.getPort() == port && SCREEN_TWO_DISPLAYED) {
                controller.getGameController().getGameState().setValue(GameState.SETUP_START);
            }
        });

    }
}
