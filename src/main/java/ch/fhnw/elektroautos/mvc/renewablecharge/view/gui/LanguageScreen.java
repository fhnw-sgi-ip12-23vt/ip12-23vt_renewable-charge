package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.Configuration;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;

public class LanguageScreen extends BorderPane implements ViewMixin<MainModel, ApplicationFXController> {

    private Label titleLabel;
    private ImageView arrowImageView;

    /**
     * The images and labels for the different languages.
     */
    private List <VBox> languageBoxes;
    private String selectedLanguage;
    private String pointingLanguage;

    private ApplicationFXController controller;

    public LanguageScreen(ApplicationFXController controller) {
        this.controller = controller;
        if (controller.getModel().language.getValue() != null) {
            this.selectedLanguage = controller.getModel().language.getValue();
        }
        init(controller);
    }

    @Override
    public void initializeSelf() {
        this.languageBoxes = new ArrayList<>();
        this.selectedLanguage = controller.getModel().language.getValue() != null ? controller.getModel().language.getValue() : "de";
        loadFonts("/fonts/Kaushan_Script/KaushanScript-Regular.ttf", "/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        addStylesheetFiles("/mvc/renewablecharge/css/language.screen.css");
        getStyleClass().add("root");
    }

    public void initializeParts() {
        this.titleLabel = new Label(
                this.controller.getModel().translationClient.getValue().get("SELECT_LANGUAGE"));
        this.titleLabel.getStyleClass().add("title");
        this.titleLabel.setPadding(new Insets(20, 0, 20, 0));

        setFocusTraversable(true);
        Platform.runLater(this::requestFocus);

        HashMap<String, String> supportedLanguages = this.controller.getModel().getConfiguration().getConfiguredLanguages();
        for (Map.Entry<String, String> entry : supportedLanguages.entrySet()) {
            String languageCode = entry.getKey();
            String languageName = entry.getValue();

            ImageView imageView = new ImageView(new Image("mvc/renewablecharge/images/flag_" + languageCode + ".png"));
            imageView.setFitWidth(80);
            imageView.setFitHeight(62);
            imageView.setPreserveRatio(true);

            Label label = new Label(languageName);
            label.setId(languageCode);
            label.getStyleClass().add("language");

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(10);

            /* Arrow to select language */
            if (languageCode.equals(selectedLanguage)) {
                ImageView arrow = new ImageView(new Image("mvc/renewablecharge/images/arrow.png"));
                arrow.setFitWidth(70);
                arrow.setFitHeight(70);
                arrow.setPreserveRatio(true);
                arrow.getStyleClass().add("arrow");
                this.arrowImageView = arrow;
                hBox.getChildren().add(arrow);
            }
            hBox.getChildren().addAll(imageView, label);

            VBox languageBox = new VBox();
            languageBox.getChildren().addAll(hBox);
            this.languageBoxes.add(languageBox);
        }
    }

    @Override
    public void layoutParts() {
        /* Vbox */
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // Background image
        String imageName = "bg-winter.png";
        String imageUrl = "/mvc/renewablecharge/images/seasons/backgrounds/" + imageName;
        BackgroundImage bgImage = new BackgroundImage(new Image(imageUrl, 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        vbox.setBackground(new Background(bgImage));

        /* Handle Language Boxes */
        VBox languageBox = new VBox();
        languageBox.setMaxWidth(500);
        languageBox.getChildren().addAll(languageBoxes);
        languageBox.getStyleClass().add("language-container");
        languageBox.setSpacing(10);
        languageBox.setPadding(new Insets(80));
        languageBox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(titleLabel, languageBox);

        /* Pane */
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(vbox);
        stackPane.setAlignment(Pos.CENTER);

        setCenter(stackPane);
    }

    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        System.out.println("LanguageScreen.setupUiToActionBindings");
        setOnKeyPressed(event -> {
            System.out.println("LanguageScreen.setupUiToActionBindings.setOnKeyPressed");
            switch (event.getCode()) {
                case DOWN:
                    // Handle arrow movement
                    System.out.println("LanguageScreen.setupUiToActionBindings.setOnKeyPressed.DOWN");
                    controller.moveLanguageArrow("down");
                    break;
                case UP:
                    // Handle arrow movement
                    System.out.println("LanguageScreen.setupUiToActionBindings.setOnKeyPressed.UP");
                    controller.moveLanguageArrow("up");
                    break;
                case ENTER:
                    // Set the selected language
                    System.out.println("LanguageScreen.setupUiToActionBindings.setOnKeyPressed.ENTER");
                    controller.getGameController().changeLanguage();
                    break;
            }
        });
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.language)
                .execute((oldValue, newValue) -> {
                    System.out.println(newValue);
                    this.selectedLanguage = newValue;
                    this.titleLabel.setText(this.controller.getModel().translationClient.getValue().get("SELECT_LANGUAGE") + " (" + newValue + ")");
                });
        onChangeOf(model.hoveredLanguage)
                .execute(((oldValue, newValue) -> {
                    if (oldValue.equals(newValue)) return;
                    String direction = newValue > oldValue ? "down" : "up";
                    moveArrow(direction);
                }));
    }

    private void moveArrow(String direction) {
        // Finding the current index of the VBox that contains the arrow
        int currentIndex = -1;
        for (int i = 0; i < languageBoxes.size(); i++) {
            VBox box = languageBoxes.get(i);
            HBox hbox = (HBox) box.getChildren().get(0);  // Assuming the HBox is always the first child
            if (hbox.getChildren().contains(arrowImageView)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) return;  // Arrow not found

        int newIndex = currentIndex + (direction.equals("down") ? 1 : -1);
        if (newIndex >= 0 && newIndex < languageBoxes.size()) {
            // Remove arrow from current position
            HBox currentHBox = (HBox) languageBoxes.get(currentIndex).getChildren().get(0);
            currentHBox.getChildren().remove(arrowImageView);

            // Add arrow to new position at the first position of the HBox
            HBox newHBox = (HBox) languageBoxes.get(newIndex).getChildren().get(0);
            newHBox.getChildren().add(0, arrowImageView);

            // Update pointingLanguage with the new pointed language from the label in HBox
            Label languageLabel = (Label) newHBox.getChildren().get(2);
            pointingLanguage = languageLabel.getId();
            System.out.println("Arrow now points to: " + pointingLanguage);
        }
    }
}
