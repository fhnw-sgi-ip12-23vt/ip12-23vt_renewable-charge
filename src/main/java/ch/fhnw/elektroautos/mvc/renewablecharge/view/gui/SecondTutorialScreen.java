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

public class SecondTutorialScreen extends StackPane implements ViewMixin<MainModel, ApplicationFXController> {

    private VBox tutorialText;
    private BackgroundImage backgroundImage;
    private List<HBox> tutorialTexts;
    private HBox tutorialText1;
    private HBox tutorialText2;
    private HBox tutorialText3;
    private HBox tutorialText4;
    private HBox tutorialText5;
    private final ApplicationFXController controller;
    private TranslationClient translationClient;

    public SecondTutorialScreen(ApplicationFXController controller) {
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

        tutorialText = new VBox();

        //adding the text to the VBox
        tutorialText1 = new HBox(new Label("1: "), new Text(translationClient.get("TUTORIAL_WIND_TEXT")));
        tutorialText2 = new HBox(new Label("2: "), new Text(translationClient.get("TUTORIAL_SUN_TEXT")));
        tutorialText3 = new HBox(new Label("3: "), new Text(translationClient.get("TUTORIAL_STORAGE_TEXT")));
        tutorialText4 = new HBox(new Label("4: "), new Text(translationClient.get("TUTORIAL_THERMAL_TEXT")));
        tutorialText5 = new HBox(new Label("5: "), new Text(translationClient.get("TUTORIAL_WATER_TEXT")));

        tutorialTexts = List.of(tutorialText1, tutorialText2, tutorialText3, tutorialText4, tutorialText5);
        tutorialText.getStyleClass().add("title");

        backgroundImage = new BackgroundImage(new Image("/mvc/renewablecharge/images/turtorial_screen_two.png", 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    }

    @Override
    public void layoutParts() {
        VBox tutorialBox = new VBox(tutorialText);

        Font font = new Font("Lato", 30);

        int counter = 0;
        for (HBox tutorialT : tutorialTexts) {
            tutorialText.getChildren().add(tutorialT);
            tutorialT.setPadding(new Insets(0, 0, 20, 0));
            for (Node node : tutorialT.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setFont(font);
                    if (counter >= 2) {
                        ((Label) node).setTextFill(Color.WHITE);
                    }
                } else if (node instanceof Text) {
                    ((Text) node).setFont(font);
                    ((Text) node).setWrappingWidth(500);
                    if (counter >= 2) {
                        ((Text) node).setFill(Color.WHITE);
                    }
                }
            }
            counter++;
        }

        tutorialText.setPadding(new Insets(80, 0, 0, 600));
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
