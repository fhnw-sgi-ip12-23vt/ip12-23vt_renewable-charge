package ch.fhnw.elektroautos.mvc.renewablecharge;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
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
        MainModel model = new MainModel(configuration);
        controller = new ApplicationFXController(context, configuration, model);

        primaryStage.setTitle("Renewable Charge - Elektroautos");
        ViewManager viewManager = new ViewManager(primaryStage, controller, model, context);
        viewManager.start();

        primaryStage.setMaximized(true);
        primaryStage.show();

        // on desktop it's convenient to have a very basic emulator for the PUI to test the interaction between GUI and PUI
        //startPUIEmulator(new MainPuiEmulator(controller));
    }

    @Override
    public void stop() {
        controller.shutdown();
    }

    private void setupStage(Stage stage, Pane gui) {
        //if started in DRM
        if (System.getProperty("egl.displayid") != null) {
            // make stage full-screen
            Rectangle2D bounds = Screen.getPrimary().getBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setResizable(false);

            // to get a nice background and the gui centered
            gui.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            gui.setStyle("-fx-border-color: dodgerblue; -fx-border-width: 3");

            StackPane background = new StackPane(gui);
            background.setStyle("-fx-background-color: linear-gradient(from 50% 0% to 50% 100%, dodgerblue 0%, midnightblue 100%)");

            Scene scene = new Scene(background);

            stage.setScene(scene);
        } else {
            Scene scene = new Scene(gui);
            stage.setScene(scene);
        }
    }

    private void startPUIEmulator(Parent puiEmulator) {
        Scene emulatorScene  = new Scene(puiEmulator);
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("PUI Emulator");
        secondaryStage.setScene(emulatorScene);
        secondaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

