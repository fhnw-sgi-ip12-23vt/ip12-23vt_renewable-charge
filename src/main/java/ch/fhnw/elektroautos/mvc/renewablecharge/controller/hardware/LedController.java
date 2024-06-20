package ch.fhnw.elektroautos.mvc.renewablecharge.controller.hardware;

import java.time.Duration;

import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ControllerBase;

/**
 * Handles all the functionality needed to manage the 'LED'.
 * <p>
 * All methods are intentionally 'package private'. Only 'ApplicationController' can access them.
 */
public class LedController extends ControllerBase<MainModel> {

    public LedController(MainModel model) {
        super(model);
    }

    public void setIsActive(boolean glows) {
        setValue(model.isActive, glows);
    }

    /**
     * In this example Controller even controls the blinking behaviour
     */
    public void blink() {
        final Duration pause = Duration.ofMillis(500);
        setIsActive(false);
        for (int i = 0; i < 4; i++) {
            setIsActive(true);
            pauseExecution(pause);
            setIsActive(false);
            pauseExecution(pause);
        }
    }

    /**
     * Example for triggering some built-in action in PUI instead of implement it in Controller.
     * <p>
     * Controller can't call PUI-component methods directly. Use a trigger instead.
     *
     */
//    public void blinkViaBuiltInAction() {
//        toggle(model.blinkingTrigger);
//    }
}
