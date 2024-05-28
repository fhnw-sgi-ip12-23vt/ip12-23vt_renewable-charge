package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class StartScreen extends BorderPane implements ViewMixin<MainModel, ApplicationFXController> {

    private Label startLabel;
    private Label pressLabel;
    private final TranslationClient translationClient;

    public StartScreen(ApplicationFXController controller) {
        this.translationClient = controller.getTranslationClient();
        init(controller);
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        addStylesheetFiles("/mvc/renewablecharge/css/start.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        /* Start Text */
        System.out.println("une translation du client: " + this.translationClient);
        startLabel = new Label(this.translationClient.get("START_TITLE"));
        startLabel.getStyleClass().add("start-text");

        /* Press any key to start */
        pressLabel = new Label(this.translationClient.get("START_PRESS"));
        pressLabel.getStyleClass().add("press-key");
    }

    @Override
    public void layoutParts() {
        /* Vbox */
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(startLabel, pressLabel);

        // Background image
        String imageName = "bg-summer.png";
        String imageUrl  = "/mvc/renewablecharge/images/seasons/backgrounds/" + imageName;
        BackgroundImage bgImage = new BackgroundImage(new Image(imageUrl, 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        vbox.setBackground(new Background(bgImage));

        /* Pane */
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(vbox);
        stackPane.setAlignment(Pos.CENTER);

        setCenter(stackPane);
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.language).execute((oldValue, newValue) -> {
            startLabel.setText(this.translationClient.get("START_TITLE"));
            pressLabel.setText(this.translationClient.get("START_PRESS"));
        });
    }
}

