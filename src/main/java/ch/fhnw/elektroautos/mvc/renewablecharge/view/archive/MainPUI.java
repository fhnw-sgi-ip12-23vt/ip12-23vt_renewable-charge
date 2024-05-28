package ch.fhnw.elektroautos.mvc.renewablecharge.view.pui;

import ch.fhnw.elektroautos.components.catalog.SerialRFID;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware.RfidController;
import com.pi4j.context.Context;

import ch.fhnw.elektroautos.components.catalog.SimpleButton;
import ch.fhnw.elektroautos.components.catalog.SimpleLed;
import ch.fhnw.elektroautos.components.catalog.base.PIN;

import ch.fhnw.elektroautos.components.crowpi.RfidComponent;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.PuiBase;

public class MainPUI extends PuiBase<MainModel, ApplicationController> {
    //declare all hardware components attached to RaspPi
    //these are protected to give unit tests access to them
    protected SimpleLed led;
    protected SimpleButton button;
    protected RfidComponent rfidComponent1;
    protected RfidComponent rfidComponent2;

    public MainPUI(ApplicationController controller, Context pi4J) {
        super(controller, pi4J);
    }

    @Override
    public void initializeParts() {
        led = new SimpleLed(pi4J, PIN.D22);
        button = new SimpleButton(pi4J, PIN.D24, false);
//        rfidComponent1 = new RfidComponent(pi4J, 25, 0, 100000);
//        rfidComponent2 = new RfidComponent(pi4J, 5, 1, 100000);
    }

    @Override
    public void setupUiToActionBindings(ApplicationController controller) {
        rfidComponent1.onCardDetected(card -> {
            System.out.println("Channel 0: " + card.getSerial());
            controller.setRfidSerialNumber(card.getSerial(), 1);

        });
        rfidComponent2.onCardDetected(card -> {
            System.out.println("Channel 1: " + card.getSerial());
            controller.setRfidSerialNumber(card.getSerial(), 2);
        });


//        serialConnectionRfid.subscribe(rfid -> {
//            if (rfid.contains("X")){ //Reader 3 endet mit X
//                rfid = removeLastChar(rfid);
//                controller.setRfidSerialNumber(rfid, 3);
//                System.out.println("Send rfid: " + rfid + " to reader 3");
//            } else { // Reader 4
//                controller.setRfidSerialNumber(rfid, 4);
//                System.out.println("Send rfid: " + rfid + " to reader 4");
//            }
//        });


    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.isActive)
                .execute((oldValue, newValue) -> {
                    if (newValue) {
                        led.on();
                    } else {
                        led.off();
                    }
                });

        // if you want to use the built-in blinking feature (instead of implementing blinking in Controller):
//        onChangeOf(model.blinkingTrigger)
//                .execute((oldValue, newValue) -> led.blink(4, Duration.ofMillis(500)));

    }

}

