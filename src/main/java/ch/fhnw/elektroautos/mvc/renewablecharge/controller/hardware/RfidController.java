package ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ControllerBase;


public class RfidController extends ControllerBase<MainModel> {
    /**
     * Controller needs a Model.
     *
     * @param model Model managed by this Controller
     */


    public RfidController(MainModel model) {
        super(model);
    }

    public void setRfidSerialNumber(String serial, int readerNumber){
        switch (readerNumber) {
//            case 1:
//                setValue(model.card1Serial, serial);
//                break;
//            case 2:
//                setValue(model.card2Serial, serial);
//                break;
//            case 3:
//                setValue(model.card3Serial, serial);
//                break;
//            case 4:
//                setValue(model.card4Serial, serial);
//                break;
            default:
                System.out.println("Cannot set serial: " + serial + " to reader: " + readerNumber);
                throw new IllegalArgumentException();
        }

    }

}

