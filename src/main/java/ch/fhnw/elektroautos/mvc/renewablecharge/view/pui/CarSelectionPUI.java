package ch.fhnw.elektroautos.mvc.renewablecharge.view.pui;

import ch.fhnw.elektroautos.components.catalog.LedStrip;
import ch.fhnw.elektroautos.components.catalog.SerialRFID;
import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;
import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.IRenewableChargeConfiguration;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.util.mvcbase.PuiBase;
import com.pi4j.context.Context;

import java.util.List;

import static ch.fhnw.elektroautos.components.catalog.SerialRFID.removeLastChar;

public class CarSelectionPUI extends PuiBase<MainModel, ApplicationFXController> {

    protected SerialHelper serialHelper;
    protected ApplicationFXController controller;

    public CarSelectionPUI(ApplicationFXController controller, Context pi4J) {
        super(controller, pi4J);
        this.controller = controller;

    }

    @Override
    public void initializeParts() {
    }

    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        List<Player> players = controller.getGameController().getGameConfiguration().getPlayers();
        this.serialHelper = controller.getGameController().getGameConfiguration().getSerialHelper();
        for (Player player : players) {
            try {
                //player.getLedStripComponent().sendMany(15, (byte) 255, (byte) 255, (byte) 0);
                //LedStrip s1 = new LedStrip(4, controller.getGameController().getGameConfiguration().getSerialHelper());
               // s1.sendMany(6, (byte) 130, (byte) 255, (byte) 0);
                //LedStrip s2 = new LedStrip(5, controller.getGameController().getGameConfiguration().getSerialHelper());
                //s2.sendMany(6, (byte) 14, (byte) 255, (byte) 80);
                LedStrip s3 = new LedStrip(6, controller.getGameController().getGameConfiguration().getSerialHelper());
                s3.sendMany(6, (byte) 99, (byte) 255, (byte) 15);
                //LedStrip s4 = new LedStrip(7, controller.getGameController().getGameConfiguration().getSerialHelper());
                //s4.sendMany(6, (byte) 44, (byte) 12, (byte) 244);
                //LedStrip s5 = new LedStrip(8, controller.getGameController().getGameConfiguration().getSerialHelper());
                //s5.sendMany(6, (byte) 255, (byte) 0, (byte) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }


            /**
             * Handle RFID 1 & 2:
             */
            if (player.getRfidComponent() != null) {
                System.out.println("Player has RFID component fucker");
                player.getRfidComponent().onCardDetected(card -> {
                    System.out.println("Card detected: " + card.getSerial());
                    controller.getGameController().assignCar(player.getRfidComponent(), card.getSerial());
                });
            }

            /**
             * Handle RFID 3 & 4:
             */
            else {
                serialHelper.handleSerialRFID();
                serialHelper.subscribe(rfid -> {
                    if (rfid.contains("X")) {
                        rfid = removeLastChar(rfid);
                        System.out.println("Send rfid: " + rfid + " to reader 3");
                        controller.getGameController().assignCarById("3", rfid);
                    } else {
                        System.out.println("Send rfid: " + rfid + " to reader 4");
                        controller.getGameController().assignCarById("4", rfid);
                    }
                });
            }
        }
    }
}
