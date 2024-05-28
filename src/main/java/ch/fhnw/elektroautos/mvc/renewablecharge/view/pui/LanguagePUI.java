package ch.fhnw.elektroautos.mvc.renewablecharge.view.pui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.I2CButton;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.PuiBase;
import com.pi4j.context.Context;

public class LanguagePUI extends PuiBase<MainModel, ApplicationFXController> {

    public LanguagePUI(ApplicationFXController controller, Context pi4J) {
        super(controller, pi4J);
    }

    @Override
    public void initializeParts() {
    }

    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        controller.getI2CController().subscribe((port) -> {
            if (I2CButton.GREEN.getPort() == port) {
                controller.moveLanguageArrow("down");
            } else if (I2CButton.BLUE.getPort() == port) {
                controller.moveLanguageArrow("up");
            } else if (I2CButton.RED.getPort() == port) {
                controller.getGameController().changeLanguage();
            }
        });

    }
}
