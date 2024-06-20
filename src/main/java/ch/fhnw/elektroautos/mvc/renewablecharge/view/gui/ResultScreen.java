package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.utils.TranslationClient;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

public class ResultScreen extends BorderPane implements ViewMixin<MainModel, ApplicationFXController> {

    private Label winner;
    private GridPane table;
    private Label noticeLabel;
    private Label countdownLabel;
    private TranslationClient translationClient;
    private ApplicationFXController controller;

    private final List<String> playerNames = new ArrayList<>();
    private final List<String> playerKilometer = new ArrayList<>();
    private final List<String> playerCharged = new ArrayList<>();
    private final List<String> playerCapacities = new ArrayList<>();

    public ResultScreen(ApplicationFXController controller) {
        this.controller = controller;
        this.translationClient = controller.getTranslationClient();
        init(controller);
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Kaushan_Script/KaushanScript-Regular.ttf", "/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        addStylesheetFiles("/mvc/renewablecharge/css/result.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        String winnerString = translationClient.get("RESULT_WINNER") != null ?
                translationClient.get("RESULT_WINNER") :
                "Winner";
        this.winner = new Label(winnerString + ": ");
        this.winner.getStyleClass().add("title");
        this.winner.setPadding(new Insets(20, 0, 20, 0));

        table = new GridPane();
        table.setHgap(20);
        table.setVgap(10);
        table.setAlignment(Pos.CENTER); // Center the table within its cell
        table.setPadding(new Insets(20));

        String noticeString = translationClient.get("RESULT_REMOVE_CARS") != null ?
                translationClient.get("RESULT_REMOVE_CARS") :
                "Please remove the cars from the charging stations.";
        noticeLabel = new Label(noticeString);
        noticeLabel.getStyleClass().add("notice-label");

        countdownLabel = new Label();
        countdownLabel.getStyleClass().add("countdown-label");

        setFocusTraversable(true);
        Platform.runLater(this::requestFocus);
    }

    @Override
    public void layoutParts() {
        /* VBox */
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // Background image
        String imageUrl = "/mvc/renewablecharge/images/seasons/backgrounds/bg-summer.png";
        BackgroundImage bgImage = new BackgroundImage(new Image(imageUrl, 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        vbox.setBackground(new Background(bgImage));

        /* Handle Language Boxes */
        VBox tableBox = new VBox();
        tableBox.setMaxWidth(900);
        tableBox.getStyleClass().add("table-container");
        tableBox.setSpacing(10);
        tableBox.setPadding(new Insets(50));
        tableBox.setAlignment(Pos.CENTER);
        tableBox.getChildren().addAll(table);
        vbox.getChildren().addAll(winner, tableBox, noticeLabel, countdownLabel);

        /* Pane */
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(vbox);
        stackPane.setAlignment(Pos.CENTER);

        setCenter(stackPane);
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.translationClient).execute((oldValue, newValue) -> {
            this.translationClient = newValue;
            initializeParts();
            layoutParts();
        });
        onChangeOf(model.updatePlayers).execute((oldValue, newValue) -> {
            loadData();
        });
        onChangeOf(model.winner)
                .execute((oldValue, newValue) -> {
                    if (newValue == null) return;
                    String winnerText = translationClient.get("RESULT_WINNER") != null ?
                            translationClient.get("RESULT_WINNER") :
                            "Winner";
                    winner.setText(winnerText + ": " + translationClient.get(newValue.getTranslationPropName()));
                });
        onChangeOf(model.countdown)
                .execute((oldValue, newValue) -> {
                    if (newValue == null) return;
                    countdownLabel.setText(newValue + "s");
                });
    }

    private void loadData() {
        // Clear the table
        table.getChildren().clear();

        // Add headers again
        String[] headers = {
                translationClient.get("RESULT_KILOMETER"),
                translationClient.get("RESULT_CHARGED"),
                translationClient.get("RESULT_CAPACITY")
        };
        for (int i = 0; i < headers.length; i++) {
            Label headerLabel = new Label(headers[i]);
            headerLabel.setFont(new Font("Lato", 40));
            headerLabel.setTextFill(Color.WHITE);
            headerLabel.setStyle("-fx-font-weight: bold;");
            table.add(headerLabel, i + 1, 0);
        }

        // Player names:
        this.playerNames.clear();
        for (Player player : controller.getGameController().getGameConfiguration().getPlayers()) {
            if (player.getSelectedCar() == null) continue;
            playerNames.add(translationClient.get(player.getTranslationPropName()));
        }

        // Range
        this.playerKilometer.clear();
        for (Player player : controller.getGameController().getGameConfiguration().getPlayers()) {
            if (player.getSelectedCar() == null) continue;
            playerKilometer.add(player.getSelectedCar().getRangeInKm() + " km");
        }

        // Charged
        this.playerCharged.clear();
        for (Player player : controller.getGameController().getGameConfiguration().getPlayers()) {
            if (player.getSelectedCar() == null) continue;
            int maxCharge = Math.min(player.getSelectedCar().getChargedCapacityWh(), player.getSelectedCar()
                                                                                           .getBatteryCapacityWh());
            playerCharged.add(maxCharge / 1000 + " kWh");
        }

        // Total capacity
        this.playerCapacities.clear();
        for (Player player : controller.getGameController().getGameConfiguration().getPlayers()) {
            if (player.getSelectedCar() == null) continue;
            playerCapacities.add(player.getSelectedCar().getBatteryCapacityWh() / 1000 + " kWh");
        }

        // Fill table with data
        for (int i = 0; i < playerNames.size(); i++) {
            Label nameLabel = new Label(playerNames.get(i));
            nameLabel.setFont(new Font("Lato", 40));
            nameLabel.setTextFill(Color.WHITE);
            table.add(nameLabel, 0, i + 1);

            Label kilometerLabel = new Label(playerKilometer.get(i));
            kilometerLabel.setFont(new Font("Lato", 30));
            kilometerLabel.setTextFill(Color.WHITE);
            table.add(kilometerLabel, 1, i + 1);

            Label chargedLabel = new Label(playerCharged.get(i));
            chargedLabel.setFont(new Font("Lato", 30));
            chargedLabel.setTextFill(Color.WHITE);
            table.add(chargedLabel, 2, i + 1);

            Label capacityLabel = new Label(playerCapacities.get(i));
            capacityLabel.setFont(new Font("Lato", 30));
            capacityLabel.setTextFill(Color.WHITE);
            table.add(capacityLabel, 3, i + 1);
        }

        table.setPadding(new Insets(10));
    }

}
