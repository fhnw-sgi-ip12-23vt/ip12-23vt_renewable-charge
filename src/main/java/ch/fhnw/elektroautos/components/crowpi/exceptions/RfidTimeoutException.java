package ch.fhnw.elektroautos.components.crowpi.exceptions;

/**
 * Timeout exception for the RFID component based on {@link RfidException}.
 * Happens when communication with PCD or PICC does not respond within a given timeframe.
 */
public class RfidTimeoutException extends RfidException {
    public RfidTimeoutException(String message) {
        super(message);
    }
}

