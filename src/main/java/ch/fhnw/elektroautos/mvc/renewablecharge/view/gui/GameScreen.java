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

import java.util.List;
import java.util.logging.Logger;

public class GameScreen extends BorderPane implements ViewMixin<MainModel, ApplicationFXController> {

    private final Logger logger = Logger.getLogger(GameScreen.class.getName());

    private ApplicationFXController controller;
    private RenewableChargeGame game;

    private Label packageToClaimLabel;
    private Label weatherLabel;
    private Label carsLabel;
    private Label packageChangeIn;
    private Label weatherChangeIn;

    public GameScreen(ApplicationFXController controller, Context pi4J) {
        this.controller = controller;
        this.game = controller.getGameController().getGame();
        init(controller);
        new GamePUI(controller, pi4J);
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
        }

        setFocusTraversable(true);
        Platform.runLater(this::requestFocus);
    }

    @Override
    public void layoutParts() {
        // VBox setup
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.BOTTOM_LEFT);

        // Background image based on the selected season
        Season season = this.game.getGameConfiguration().getSelectedSeason();
        String imageUrl = "/mvc/renewablecharge/images/seasons/" + season.getImageName();
        BackgroundImage bgImage = new BackgroundImage(new Image(imageUrl, 1920, 1080, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        vbox.setBackground(new Background(bgImage));

        // statsBox setup
        VBox statsBox = new VBox();
        statsBox.setAlignment(Pos.CENTER_LEFT); // Align statsBox to the left center
        statsBox.setSpacing(20);
        statsBox.getChildren().addAll(packageToClaimLabel, weatherLabel, packageChangeIn, weatherChangeIn, carsLabel);
        statsBox.getStyleClass().add("stats");
        statsBox.setMaxWidth(700);

        // Add statsBox to vbox
        vbox.getChildren().add(statsBox);

        // Loading the weather images
        WeatherConfiguration weather = this.game.getWeather();
        this.controller.updatePackage(this.game.getPackage());
        this.controller.updateWeather(weather);

        // Create weatherPane
        BorderPane weatherPane = drawWeatherImages(weather);

        // Layering all components together in the StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(vbox, weatherPane);

        // Align VBox to the left in the StackPane
        StackPane.setAlignment(vbox, Pos.CENTER_LEFT);
        StackPane.setMargin(vbox, new Insets(0, 0, 0, 0)); // Adjust margins if necessary

        // Center the weatherPane within the StackPane
        StackPane.setAlignment(weatherPane, Pos.CENTER);

        // Set the stackPane as the center of the BorderPane
        setCenter(stackPane);
    }

    private BorderPane drawWeatherImages(WeatherConfiguration weather) {
        // Loading the weather images
        Image sunImage  = new Image(weather.getSunType().getImagePath());
        Image windImage = new Image(weather.getWindType().getImagePath());
        Image thermalImage = new Image(weather.getThermalType().getImagePath());

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

        ImageView thermalImageView = new ImageView(thermalImage);
        thermalImageView.setFitWidth(200); // Set the width of the wind image
        thermalImageView.setFitHeight(200); // Set the height of the wind image
        thermalImageView.setPreserveRatio(true); // Preserve the aspect ratio

        // Use a BorderPane for better control of positioning
        BorderPane weatherPane = new BorderPane();
        weatherPane.setId("weather-pane");
        weatherPane.setPadding(new Insets(50));

        weatherPane.setLeft(sunImageView); // Sun image at the left
        BorderPane.setAlignment(sunImageView, Pos.TOP_LEFT);

        weatherPane.setRight(windImageView); // Wind image at the center
        BorderPane.setAlignment(windImageView, Pos.CENTER_RIGHT);

        weatherPane.setCenter(thermalImageView); // Thermal image at the right
        BorderPane.setAlignment(thermalImageView, Pos.CENTER);

        return weatherPane;
    }

    private void updateWeatherPane(WeatherConfiguration weather) {
        StackPane  stackPane   = (StackPane) getCenter();
        BorderPane weatherPane = (BorderPane) stackPane.lookup("#weather-pane");
        if (weatherPane != null) {
            System.out.println("Removing weatherPane");
            stackPane.getChildren().remove(weatherPane);
        }
        BorderPane newWeatherPane = drawWeatherImages(weather);
        stackPane.getChildren().add(1, newWeatherPane);
    }

    /**
     * TODO: Remove this! This is a button emulator.
     *
     * @param controller The controller to bind the UI to.
     */
    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        logger.info("GameScreen.setupUiToActionBindings");
        List<Player> players = controller.getGameController().getGameConfiguration().getPlayers();
        logger.info("GameScreen.setupUiToActionBindings: " + players.size() + " players");
        setOnKeyPressed(event -> {
            EnergyPackage energyPackage = controller.getModel().displayedPackage.getValue();
            logger.info("(Mockup) Energy Package: " + energyPackage);
            logger.info("(Mockup) Key pressed: " + event.getCode().getCode());
            if (event.getCode().getCode() == 49) { // 1
                logger.info("(Mockup) Player 1 pressed the button");
                controller.claimPackage(players.get(0), energyPackage);
            } else if (event.getCode().getCode() == 50) { // 2
                logger.info("(Mockup) Player 2 pressed the button");
                controller.claimPackage(players.get(1), energyPackage);
            } else if (event.getCode().getCode() == 51) { // 3
                logger.info("(Mockup) Player 3 pressed the button");
                controller.claimPackage(players.get(2), energyPackage);
            } else if (event.getCode().getCode() == 52) { // 3
                logger.info("(Mockup) Player 4 pressed the button");
                controller.claimPackage(players.get(3), energyPackage);
            }
        });
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.displayedPackage)
                .execute((oldValue, newValue) -> {
                    if (newValue == null) return;
                    packageToClaimLabel.setText("Package: " + newValue.getEnergyType().getTranslatePropName() + " (" + newValue.getSize() + " Wh)");
                    System.out.println("-------------------- Package --------------------");
                    System.out.println(newValue);
                    System.out.println("------------------------------------------------");
                });
        onChangeOf(model.displayedWeather).execute(
                (oldValue, newValue) -> {
                    if (newValue == null) return;
                    weatherLabel.setText("Weather: " + newValue);
                    System.out.println("-------------------- Weather --------------------");
                    System.out.println(newValue);
                    System.out.println("------------------------------------------------");
                    updateWeatherPane(newValue);
                }
        );
        onChangeOf(model.packageChangeTimer).execute(
                (oldValue, newValue) -> {
                    if (newValue == null) return;
                    packageChangeIn.setText("Package changing in: " + model.packageChangeTimer.getValue() + "s\n");
                }
        );
        onChangeOf(model.weatherChangeTimer).execute(
                (oldValue, newValue) -> {
                    if (newValue == null) return;
                    weatherChangeIn.setText("Weather changing in: " + model.weatherChangeTimer.getValue() + "s\n");
                }
        );
        onChangeOf(model.updatePlayers)
                .execute((oldValue, newValue) -> {
                    String carString = "";
                    for (Player player : model.players.getValue()) {
                        Car car = player.getSelectedCar();
                        if (car != null){
                            carString += player.getTranslationPropName()
                                    + " -> "
                                    + car.getName()
                                    + " -> "
                                    + car.getChargedCapacityWh()
                                    + "/"
                                    + car.getBatteryCapacityWh()
                                    + "\n";
                        }
                    }
                    carsLabel.setText("Cars:\n" + carString.toString());
                    System.out.println("-------------------- Cars --------------------");
                    System.out.println(carString);
                    System.out.println("------------------------------------------------");
                });
    }
}
