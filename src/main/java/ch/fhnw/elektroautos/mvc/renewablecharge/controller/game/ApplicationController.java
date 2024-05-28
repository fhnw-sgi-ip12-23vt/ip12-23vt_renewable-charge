package ch.fhnw.elektroautos.mvc.renewablecharge.controller.game;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.LedStripController;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.RfidController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ControllerBase;
import javafx.event.ActionEvent;

/**
 * Provides all the available actions to the UI.
 * <p>
 * Usually all the methods just delegate the call to the appropriate (Sub-)Controller.
 *
 */
public class ApplicationController extends ControllerBase<MainModel> {

    private final LedController ledController;
    private final RfidController rfidController;
    private final LedStripController ledStripController;
    private final GameController gameController;

    public ApplicationController(MainModel model) {
        super(model);
        ledController = new LedController(model);
        rfidController = new RfidController(model);
        ledStripController = new LedStripController(model);
        this.gameController = new GameController();
    }

    @Override
    public void awaitCompletion() {
        super.awaitCompletion();
        ledController.awaitCompletion();
        rfidController.awaitCompletion();
        ledStripController.awaitCompletion();
    }

    // the actions we need in our application
    // these methods are public and can be called from GUI and PUI (and nothing else)

    @Override
    public void shutdown() {
        super.shutdown();
        ledController.shutdown();
        rfidController.shutdown();
        ledStripController.shutdown();
    }

    public void setLedGlows(boolean glows){
        ledController.setIsActive(glows);
    }

    public void blink(){
        ledController.blink();
    }

    public void setRfidSerialNumber(String serial, int readerNumber) {
        rfidController.setRfidSerialNumber(serial, readerNumber);
    }

    public void blinkStrip() {
        //ledStripController.blinkStrip();
    }

    public void startGame() {
        this.gameController.startGame();
    }

    public void activatePlayer(String serial) {
        this.gameController.assignCarToPlayer(serial);
    }

    public void addFirstCard(String serial) {
//        this.model.card1Serial.setValue(serial);
    }

    public void addSecondCard(String serial) {
        //this.model.card2Serial.setValue(serial);
    }

    public void setLanguage(String language) {
        this.model.language.setValue(language);
    }
}

