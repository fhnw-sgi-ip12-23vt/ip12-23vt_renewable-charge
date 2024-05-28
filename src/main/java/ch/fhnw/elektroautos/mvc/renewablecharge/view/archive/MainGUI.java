package ch.fhnw.elektroautos.mvc.renewablecharge.view.archive;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.I2CController;
import com.pi4j.context.Context;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;

public class MainGUI extends BorderPane implements ViewMixin<MainModel, ApplicationController> {
    private static final String LIGHT_BULB = "\uf0eb";  // the unicode of the lightbulb-icon in fontawesome font
    private static final String HEARTBEAT = "\uf21e";  // the unicode of the heartbeat-icon in fontawesome font

    // declare all the UI elements you need
    private Button ledButton;
    private Button blinkButton;
    private Label rfidlabel;
    private Label startGamelabel;
    private Label infoLabel;
    private Button rfidButton;
    private Button ledStripButton;
    private Button startGameButton;
    private Context context;
    private boolean valueSet = false;

    public MainGUI(ApplicationController controller, Context context) {
        init(controller); //don't forget to call init
        this.context = context;
    }

    @Override
    public void initializeSelf() {
        //load all fonts you need
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");

        //apply your style
        addStylesheetFiles("/mvc/multicontrollerapp/style.css");

        getStyleClass().add("root-pane");
    }

    @Override
    public void initializeParts() {
        ledButton = new Button(LIGHT_BULB);
        ledButton.getStyleClass().add("icon-button");

        blinkButton = new Button(HEARTBEAT);
        blinkButton.getStyleClass().add("icon-button");

        rfidlabel = new Label();
        rfidlabel.getStyleClass().add("rfid-label");

        startGamelabel = new Label();
        startGamelabel.getStyleClass().add("start-game-label");

        infoLabel = new Label();
        infoLabel.getStyleClass().add("info-label");

        rfidButton = new Button("rfid");

        ledStripButton = new Button("strip");

        startGameButton = new Button("Start Game");
        startGameButton.getStyleClass().add("start-game-button");
    }

    @Override
    public void layoutParts() {
        // consider to use GridPane instead
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBox = new HBox(ledButton, spacer, blinkButton);
        topBox.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(rfidButton, ledStripButton);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(true);
        centerBox.setPadding(new Insets(30));

        VBox centerBoxStartGame = new VBox(startGamelabel, startGameButton);
        centerBoxStartGame.setAlignment(Pos.BOTTOM_LEFT);
        centerBoxStartGame.setFillWidth(true);
        centerBoxStartGame.setPadding(new Insets(30));


        setTop(topBox);
        setRight(centerBox);
        setLeft(centerBoxStartGame);
        setBottom(infoLabel);
    }

    @Override
    public void setupUiToActionBindings(ApplicationController controller) {
        // look at that: all EventHandlers just trigger an action on Controller
        // by calling a single method

        ledButton.setOnMousePressed(event -> controller.setLedGlows(true));
        ledButton.setOnMouseReleased(event -> controller.setLedGlows(false));
        blinkButton.setOnAction(event -> controller.blink());
        ledStripButton.setOnAction(event -> controller.blinkStrip());
        startGameButton.setOnAction(event -> controller.startGame());

    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.systemInfo) // the value we need to observe, in this case that's an ObservableValue<String>, no need to convert it
            .update(infoLabel.textProperty());         // keeps textProperty and systemInfo in sync

//        onChangeOf(model.outputLabel)  // the value we need to observe, in this case that's an ObservableValue<Integer>
//           .convertedBy(String::valueOf)              // we have to convert the Integer to a String
//           .update(rfidlabel.textProperty());      // keeps textProperty and counter in sync
    }
}

