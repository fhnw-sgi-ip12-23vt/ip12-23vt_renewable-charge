package ch.fhnw.elektroautos.mvc.renewablecharge;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.IRenewableChargeConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.PropertiesRenewableChargeConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.ViewManager;
import ch.fhnw.elektroautos.mvc.util.Pi4JContext;
import com.pi4j.context.Context;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AppStarter extends Application {
    private ApplicationFXController controller;

    @Override
    public void start(Stage primaryStage) {
        // that's your 'information hub'.
        Context context = Pi4JContext.createContext();

        IRenewableChargeConfiguration configuration = new PropertiesRenewableChargeConfiguration(context);
        MainModel                     model         = new MainModel(configuration);
        controller = new ApplicationFXController(context, configuration, model);

        primaryStage.setTitle("Renewable Charge - Elektroautos");
        ViewManager viewManager = new ViewManager(primaryStage, controller, model, context);
        viewManager.start();
        primaryStage.show();
    }

    @Override
    public void stop() {
        controller.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

