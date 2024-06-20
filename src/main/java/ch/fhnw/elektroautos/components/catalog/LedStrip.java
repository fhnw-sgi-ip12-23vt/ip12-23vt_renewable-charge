package ch.fhnw.elektroautos.components.catalog;

import ch.fhnw.elektroautos.components.catalog.base.SerialHelper;

public class LedStrip {

    private static final int BYTES_PER_PIXEL = 3;
    private final SerialHelper helper;
    private final int channel;

    public LedStrip(int channel, SerialHelper helper) {
        this.channel = channel;
        this.helper = helper;
        //this.helper.sendAllOff(channel, 25);
    }

    public void sendOneByOne(int numberOfLeds, byte red, byte green, byte blue) throws InterruptedException {
        //System.out.println("One by one on channel " + channel);
        for (int i = 0; i < numberOfLeds; i++) {
            System.out.println("Sending color " + i);
            byte[] oneLed = new byte[numberOfLeds * BYTES_PER_PIXEL];
            oneLed[i * BYTES_PER_PIXEL] = red;
            oneLed[(i * BYTES_PER_PIXEL) + 1] = green;
            oneLed[(i * BYTES_PER_PIXEL) + 2] = blue;
            helper.sendColors(channel, BYTES_PER_PIXEL, 1, 0, 2, 0, oneLed, true);
            Thread.sleep(50);
        }
    }

    public void sendMany(int numberOfLeds, byte red, byte green, byte blue) throws InterruptedException {
        //System.out.println("Many on channel " + channel);
        byte[] manyLeds = new byte[numberOfLeds * BYTES_PER_PIXEL];
        for (int i = 0; i < numberOfLeds; i++) {
            manyLeds[i * BYTES_PER_PIXEL] = red;
            manyLeds[(i * BYTES_PER_PIXEL) + 1] = green;
            manyLeds[(i * BYTES_PER_PIXEL) + 2] = blue;
        }
        helper.sendColors(channel, BYTES_PER_PIXEL, 1, 0, 2, 0, manyLeds, false);
    }

    public int getChannel() {
        return channel;
    }

    public SerialHelper getHelper() {
        return helper;
    }
}

