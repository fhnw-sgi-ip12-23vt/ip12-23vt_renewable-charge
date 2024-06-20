package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Car;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.RenewableChargeGame;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.Season;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.weathertypes.WeatherConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.pui.GamePUI;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import com.pi4j.context.Context;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

import java.util.List;
import java.util.logging.Logger;

public class GameScreen extends StackPane implements ViewMixin<MainModel, ApplicationFXController> {

    private final Logger logger = Logger.getLogger(GameScreen.class.getName());

    private ApplicationFXController controller;
    private RenewableChargeGame game;

    private Label packageToClaimLabel;
    private Label weatherLabel;
    private Label carsLabel;
    private Label packageChangeIn;
    private Label weatherChangeIn;
    private Label countDown;

    private Arc semiCircle;

    public GameScreen(ApplicationFXController controller) {
        this.controller = controller;
        this.game = controller.getGameController().getGame();
        init(controller);
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        addStylesheetFiles("/mvc/renewablecharge/css/game.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        if (this.controller != null) {
            this.packageToClaimLabel = new Label();
            this.weatherLabel = new Label();
            this.carsLabel = new Label();
            this.packageChangeIn = new Label();
            this.weatherChangeIn = new Label();

            // Set max width and enable text wrapping
            double maxWidth = 700;
            this.packageToClaimLabel.setMaxWidth(maxWidth);
            this.packageToClaimLabel.setWrapText(true);

            this.weatherLabel.setMaxWidth(maxWidth);
            this.weatherLabel.setWrapText(true);

            this.carsLabel.setMaxWidth(maxWidth);
            this.carsLabel.setWrapText(true);

            this.packageChangeIn.setMaxWidth(maxWidth);
            this.packageChangeIn.setWrapText(true);

            this.weatherChangeIn.setMaxWidth(maxWidth);
            this.weatherChangeIn.setWrapText(true);

            this.countDown = new Label("Default value");
            this.countDown.setFont(new Font("Lato", 30));
            this.countDown.setTextFill(Color.WHITE);
            this.countDown.getStyleClass().add("countdown-label");
        }

        // Create semi-circle
        this.semiCircle = new Arc();

        setFocusTraversable(true);
        Platform.runLater(this::requestFocus);
    }

    @Override
    public void layoutParts() {
        // VBox setup
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.BOTTOM_LEFT);

        // Background image based on the selected season
        Season season   = this.game.getGameConfiguration().getSelectedSeason();
        String imageUrl = "/mvc/renewablecharge/images/seasons/" + season.getImageName();
        BackgroundImage bgImage = new BackgroundImage(new Image(imageUrl, 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        // Loading the weather images
        WeatherConfiguration weather = this.game.getWeather();
        this.controller.updatePackage(this.game.getPackage());
        this.controller.updateWeather(weather);

        // Create weatherPane
        HBox weatherPane = drawWeatherImages(weather);

        // Layering all components together in the StackPane

        // Align VBox to the left in the StackPane
        StackPane.setAlignment(vbox, Pos.CENTER_LEFT);
        StackPane.setMargin(vbox, new Insets(0, 0, 0, 0)); // Adjust margins if necessary

        // Center the weatherPane within the StackPane
        StackPane.setAlignment(weatherPane, Pos.CENTER);

        // Add the semi-circle and align it to the top center
        this.semiCircle.setRadiusX(95);
        this.semiCircle.setRadiusY(95);
        this.semiCircle.setStartAngle(180);
        this.semiCircle.setLength(180);
        this.semiCircle.setType(ArcType.ROUND);
        this.semiCircle.setFill(Color.valueOf("#710000"));
        StackPane.setAlignment(semiCircle, Pos.TOP_CENTER);

        // Add the countdown label and align it to the top center
        StackPane.setAlignment(countDown, Pos.TOP_CENTER);
        countDown.setPadding(new Insets(10, 0, 0, 0));

        // Create a wrapper box for the countdown and VBox
        var wrapperBox = new HBox(vbox);
        wrapperBox.setBackground(new Background(bgImage));

        // Add all components to the StackPane
        this.getChildren().addAll(wrapperBox, weatherPane, semiCircle, countDown);
    }

    private HBox drawWeatherImages(WeatherConfiguration weather) {
        // Loading the weather images
        Image sunImage     = new Image(weather.getSunType().getImagePath());
        Image windImage    = new Image(weather.getWindType().getImagePath());
        Image thermalImage = new Image(weather.getThermalType().getImagePath());
        Image storageImage = new Image(weather.getStorageState().getImagePath());
        Image waterImage   = new Image(weather.getWaterType().getImagePath());

        this.controller.updatePackage(this.game.getPackage());
        this.controller.updateWeather(weather);

        // Create ImageView objects for the images and resize them
        ImageView sunImageView = new ImageView(sunImage);
        sunImageView.setFitWidth(200); // Set the width of the sun image
        sunImageView.setFitHeight(200); // Set the height of the sun image
        sunImageView.setPreserveRatio(true); // Preserve the aspect ratio

        ImageView windImageView = new ImageView(windImage);
        windImageView.setFitWidth(200); // Set the width of the wind image
        windImageView.setFitHeight(200); // Set the height of the wind image
        windImageView.setPreserveRatio(true); // Preserve the aspect ratio

        ImageView waterImageView = new ImageView(waterImage);
        waterImageView.setFitWidth(200); // Set the width of the wind image
        waterImageView.setFitHeight(200); // Set the height of the wind image
        waterImageView.setPreserveRatio(true);

        ImageView thermalImageView = new ImageView(thermalImage);
        thermalImageView.setFitWidth(200); // Set the width of the wind image
        thermalImageView.setFitHeight(200); // Set the height of the wind image
        thermalImageView.setPreserveRatio(true); // Preserve the aspect ratio

        ImageView storageImageView = new ImageView(storageImage);
        storageImageView.setFitWidth(200); // Set the width of the wind image
        storageImageView.setFitHeight(200); // Set the height of the wind image
        storageImageView.setPreserveRatio(true); // Preserve the aspect ratio

        // Create an HBox for the weather images
        HBox weatherPane = new HBox();
        weatherPane.setId("weather-pane");

        VBox sunPane = new VBox(sunImageView);
        sunPane.setPadding(new Insets(20, 0, 0, 10));
        weatherPane.getChildren().add(sunPane); // Sun image at the left

        VBox thermalPane = new VBox(thermalImageView);
        thermalPane.setPadding(new Insets(600, 0, 0, 200));
        weatherPane.getChildren().add(thermalPane); // Thermal image at the right

        VBox storagePane = new VBox(waterImageView);
        storagePane.setPadding(new Insets(800, 0, 0, 500));
        weatherPane.getChildren().add(storagePane); // Storage image at the bottom

        var windImagePane = new VBox(windImageView);
        windImagePane.setPadding(new Insets(200, 0, 0, 0));
        VBox windPane = new VBox(storageImageView, windImagePane);
        windPane.setPadding(new Insets(250, 0, 0, 100));
        weatherPane.getChildren().add(windPane); // Wind image at the center

        return weatherPane;
    }

    private void updateWeatherPane(WeatherConfiguration weather) {
        HBox weatherPane = (HBox) this.lookup("#weather-pane");
        if (weatherPane != null) {
            this.getChildren().remove(weatherPane);
        }
        HBox newWeatherPane = drawWeatherImages(weather);
        this.getChildren().add(1, newWeatherPane);
    }

    private void showEnergyPackage(EnergyPackage energyPackage) {
        ImageView packageImage = new ImageView(energyPackage.getImagePath());

        // Removing the old package image
        removeEnergyPackage();

        packageImage.setId("package-image");
        packageImage.fitWidthProperty().bind(this.widthProperty());
        packageImage.fitHeightProperty().bind(this.heightProperty());
        packageImage.setPreserveRatio(true);
        this.getChildren().add(packageImage);
    }

    private void removeEnergyPackage() {
        this.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("package-image"));
    }

    /**
     * TODO: Remove this! This is a button emulator.
     *
     * @param controller The controller to bind the UI to.
     */
    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        List<Player> players = controller.getGameController().getGameConfiguration().getPlayers();
        setOnKeyPressed(event -> {
            EnergyPackage energyPackage = controller.getModel().displayedPackage.getValue();
            if (event.getCode().getCode() == 49) { // 1
                controller.claimPackage(players.get(0), energyPackage);
            } else if (event.getCode().getCode() == 50) { // 2
                controller.claimPackage(players.get(1), energyPackage);
            } else if (event.getCode().getCode() == 51) { // 3
                controller.claimPackage(players.get(2), energyPackage);
            } else if (event.getCode().getCode() == 52) { // 3
                controller.claimPackage(players.get(3), energyPackage);
            }
        });
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.displayedPackage).execute((oldValue, newValue) -> {
            if (newValue == null) {
                removeEnergyPackage();
            } else {
                showEnergyPackage(newValue);
            }
        });

        onChangeOf(model.displayedWeather).execute(
                (oldValue, newValue) -> {
                    if (newValue == null) return;
                    weatherLabel.setText("Weather: " + newValue);
                    updateWeatherPane(newValue);
                }
        );

        onChangeOf(model.countdown).execute((oldValue, newValue) -> {
            countDown.setText(newValue + " s");
        });
    }
}
