package ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ControllerBase;

public class LedStripController extends ControllerBase<MainModel> {

    public LedStripController(MainModel model) {
        super(model);
    }

    /*
    public void blinkStrip() {
        System.out.println("blinkStrip() called!");
        LedStrip ledStrip = new LedStrip();

        try {
            System.out.println("Attempting to send one by one");
            final int ledsToActivate = 25;
            ledStrip.sendMany(0, ledsToActivate, (byte) 0x00, (byte) 0x00, (byte) 0xff);
            Thread.sleep(3000);
            ledStrip.getHelper().sendAllOff(0, ledsToActivate);
            ledStrip.sendMany(1, ledsToActivate, (byte) 0x00, (byte) 0xff, (byte) 0x00);
            Thread.sleep(3000);
            ledStrip.getHelper().sendAllOff(1, ledsToActivate);
            ledStrip.sendMany(2, ledsToActivate, (byte) 0xff, (byte) 0x00, (byte) 0x00);
            Thread.sleep(3000);
            ledStrip.getHelper().sendAllOff(2, ledsToActivate);
            ledStrip.sendMany(3, ledsToActivate, (byte) 0xff, (byte) 0xff, (byte) 0xff);
            Thread.sleep(3000);
            ledStrip.getHelper().sendAllOff(3, ledsToActivate);
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
        }

        ledStrip.getHelper().closePort();
    }

     */
}

