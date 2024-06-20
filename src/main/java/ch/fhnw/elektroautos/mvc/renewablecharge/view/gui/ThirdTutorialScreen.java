package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class ThirdTutorialScreen extends StackPane implements ViewMixin<MainModel, ApplicationFXController> {

    private BackgroundImage backgroundImage;
    private List<HBox> tutorialTexts;
    private Text tutorialHeader;
    private Text tutorialText;
    private final ApplicationFXController controller;
    private TranslationClient translationClient;

    public ThirdTutorialScreen(ApplicationFXController controller) {
        this.controller = controller;
        this.translationClient = controller.getTranslationClient();
        init(controller);
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        setFocusTraversable(true);
        Platform.runLater(this::requestFocus);

        //adding the text to the VBox
        tutorialHeader = new Text(translationClient.get("TUTORIAL_ENERGY_PACKAGE_HEADER"));
        tutorialText = new Text(translationClient.get("TUTORIAL_ENERGY_PACKAGE_TEXT"));

        tutorialText.getStyleClass().add("title");

        backgroundImage = new BackgroundImage(new Image("/mvc/renewablecharge/images/turtorial_screen_three.png", 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    }

    @Override
    public void layoutParts() {
        VBox      tutorialBox       = new VBox();
        StackPane tutorialHeaderBox = new StackPane(tutorialHeader);
        StackPane tutorialTextBox   = new StackPane(tutorialText);

        Font font = new Font("Lato", 40);
        tutorialHeaderBox.setPadding(new Insets(80, 0, 0, 100));
        tutorialHeader.setFont(font);
        tutorialHeader.setFill(Color.BLACK);
        tutorialHeader.setWrappingWidth(700);
        tutorialTextBox.setPadding(new Insets(600, 0, 0, 0));
        tutorialText.setFont(font);
        tutorialText.setFill(Color.WHITE);
        tutorialText.setWrappingWidth(700);

        tutorialBox.getChildren().add(tutorialHeaderBox);
        tutorialBox.getChildren().add(tutorialTextBox);
        tutorialBox.setBackground(new Background(backgroundImage));

        this.getChildren().add(tutorialBox);
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.translationClient).execute(
                (oldValue, newValue) -> {
                    this.translationClient = newValue;
                    initializeParts();
                    layoutParts();
                }
        );
    }
}
