package ch.fhnw.elektroautos.components.crowpi.exceptions;

import ch.fhnw.elektroautos.components.crowpi.internal.rfid.RfidCardType;

/**
 * Unsupported card exception for the RFID component based on {@link RfidException}.
 * Happens when the SAK of the PICC is not supported by this library.
 */
public class RfidUnsupportedCardException extends RfidException {
    private final RfidCardType cardType;

    public RfidUnsupportedCardException(RfidCardType cardType) {
        super("Unsupported card type: " + cardType);
        this.cardType = cardType;
    }

    public RfidCardType getCardType() {
        return cardType;
    }
}

