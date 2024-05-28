package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ResultScreen extends GridPane implements ViewMixin<MainModel, ApplicationFXController> {

    private Label winner;
    private final ApplicationFXController controller;

    public ResultScreen(ApplicationFXController controller) {
        init(controller);
        this.controller = controller;
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        //addStylesheetFiles("/mvc/renewablecharge/css/race.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        this.winner = new Label("Winner: ");
    }

    @Override
    public void layoutParts() {
        setAlignment(Pos.CENTER); // Center the GridPane
        add(winner, 0, 0);
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.winner)
                .execute((oldValue, newValue) -> {
                    if (newValue == null) return;
                    winner.setText("Winner: " + newValue.getTranslationPropName());
                });
    }
}
