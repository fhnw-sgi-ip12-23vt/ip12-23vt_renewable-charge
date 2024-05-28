package ch.fhnw.elektroautos.mvc.renewablecharge.view.pui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.PuiBase;
import com.pi4j.context.Context;

public class GamePUI extends PuiBase<MainModel, ApplicationFXController> {

    private final ApplicationFXController controller;

    public GamePUI(ApplicationFXController controller, Context pi4J) {
        super(controller, pi4J);
        this.controller = controller;

        /* Handle Button Press */
        this.controller.getI2CController().subscribe((port) -> {
            if (this.controller.getGameController().getGameState().getValue() != GameState.RUNNING) return;
            Player player = controller.getGameController().getGameConfiguration().getPlayers().stream()
                                      .filter(p -> p.getI2CButton().getPort() == port)
                                      .findFirst()
                                      .orElse(null);

            if (player == null) {
                System.out.println("No player found for port: " + port);
                return;
            }

            System.out.println("Button pressed by: " + player.getTranslationPropName());
            controller.claimPackage(player, this.controller.getModel().displayedPackage.getValue());

            // Loading the weather images
            WeatherConfiguration weather = this.controller.getGameController().getGame().getWeather();
            this.controller.updatePackage(this.controller.getGameController().getGame().getPackage());
            this.controller.updateWeather(weather);
        });
    }

    @Override
    public void initializeParts() {

    }
}
