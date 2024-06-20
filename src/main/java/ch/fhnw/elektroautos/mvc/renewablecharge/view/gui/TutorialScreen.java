package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TutorialScreen extends StackPane implements ViewMixin<MainModel, ApplicationFXController> {

    private Label tutorialHeader;
    private Text tutorialText2;
    private BackgroundImage backgroundImage;
    private final ApplicationFXController controller;
    private TranslationClient translationClient;

    public TutorialScreen(ApplicationFXController controller) {
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

        tutorialHeader = new Label(translationClient.get("TUTORIAL_HEADER"));
        tutorialHeader.getStyleClass().add("title");

        tutorialText2 = new Text(translationClient.get("TUTORIAL_TEXT"));
        backgroundImage = new BackgroundImage(new Image("/mvc/renewablecharge/images/tutorial.png", 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    }

    @Override
    public void layoutParts() {
        VBox tutorialBox = new VBox(tutorialHeader, tutorialText2);
        tutorialText2.setFont(new Font("Lato", 25));
        tutorialText2.setWrappingWidth(1000);
        tutorialHeader.setPadding(new Insets(0, 0, 40, 0));
        tutorialHeader.setFont(new Font("Lato", 70));
        tutorialBox.setPadding(new Insets(140, 0, 120, 470));
        tutorialBox.setBackground(new Background(backgroundImage));

        this.setPadding(new Insets(0, 0, 0, 400));
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
