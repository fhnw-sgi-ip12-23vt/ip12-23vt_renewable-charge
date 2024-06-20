package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.GameState;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class RaceScreen extends GridPane implements ViewMixin<MainModel, ApplicationFXController> {

    private Label countdownLabel;
    private final ApplicationFXController controller;

    public RaceScreen(ApplicationFXController controller) {
        this.controller = controller;
        init(controller);
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        addStylesheetFiles("/mvc/renewablecharge/css/race.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        this.countdownLabel = new Label();
        this.countdownLabel.getStyleClass().add("countdown");
        this.countdownLabel.setAlignment(Pos.CENTER);
    }

    @Override
    public void layoutParts() {
        setAlignment(Pos.CENTER); // Center the GridPane
        add(countdownLabel, 0, 0); // Add the countdown label to the grid
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.raceCountdown).execute((oldVal, newVal) -> {
            if (!model.runRaceCountdown.getValue()) {
                return;
            }

            countdownLabel.setText(String.valueOf(newVal));

            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), countdownLabel);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);

            SequentialTransition sequentialTransition = new SequentialTransition(fadeTransition);
            sequentialTransition.play();

            if (newVal == 0) {
                countdownLabel.setText("Go!");
                controller.getGameController().startRace();
                model.runRaceCountdown.setValue(false);
            }
        });
    }
}
