package ch.fhnw.elektroautos.components.crowpi.events;

/**
 * Generic event listener interface for easy removal of existing listeners.
 */
public interface EventListener {
    /**
     * Removes the listener and therefore prevents any execution in the future.
     */
    void remove();
}

