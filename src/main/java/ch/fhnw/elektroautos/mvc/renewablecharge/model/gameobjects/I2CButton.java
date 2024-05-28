package ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects;

public enum I2CButton {
    YELLOW(0), BLUE(1), GREEN(2), RED(3);

    private int port;

    I2CButton(int port){
        this.port = port;
    }

    public int getPort(){
        return port;
    }

    public byte getRed() {
        switch (this) {
            case YELLOW, RED:
                return (byte) 255;
            default:
                return (byte) 0;
        }
    }

    public byte getGreen() {
        switch (this) {
            case YELLOW, GREEN:
                return (byte) 255;
            default:
                return (byte) 0;
        }
    }

    public byte getBlue() {
        switch (this) {
            case BLUE:
                return (byte) 255;
            default:
                return (byte) 0;
        }
    }
}
