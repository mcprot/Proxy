package com.tekgator.queryminecraftserver.api;

/**
 * @author Patrick Weiss <info@tekgator.com>
 */
public enum Protocol {
    TCP(47);                // query via TCP for every Minecraft version starting at 1.7 and above

    private final int value;

    Protocol(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}