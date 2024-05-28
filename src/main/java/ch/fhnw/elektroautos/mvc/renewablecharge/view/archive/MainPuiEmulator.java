package ch.fhnw.elektroautos.mvc.renewablecharge.view.archive;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;


public class MainPuiEmulator extends VBox implements ViewMixin<MainModel, ApplicationController> {

    // for each PUI component, declare a corresponding JavaFX-control
    private Label led;
    private Button decreaseButton;

    public MainPuiEmulator(ApplicationController controller) {
        init(controller);
    }

    @Override
    public void initializeSelf() {
        setPrefWidth(250);
    }

    @Override
    public void initializeParts() {
        led = new Label();
        decreaseButton = new Button("Decrease");
    }

    @Override
    public void layoutParts() {
        setPadding(new Insets(20));
        setSpacing(20);
        setAlignment(Pos.CENTER);
        getChildren().addAll(led, decreaseButton);
    }

    @Override
    public void setupUiToActionBindings(ApplicationController controller) {
        //trigger the same actions as the real PUI
        throw new UnsupportedOperationException("Implement");
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        //observe the same values as the real PUI

        onChangeOf(model.isActive)
                .convertedBy(glows -> glows ? "on" : "off")
                .update(led.textProperty());
    }
}

