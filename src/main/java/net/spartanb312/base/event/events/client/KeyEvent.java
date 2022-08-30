package net.spartanb312.base.event.events.client;

import net.spartanb312.base.event.EventCenter;

public final class KeyEvent extends EventCenter {

    private final int key;
    private final char character;

    public KeyEvent(int key, char character) {
        this.key = key;
        this.character = character;
    }

    public final int getKey() {
        return this.key;
    }

    public final char getCharacter() {
        return this.character;
    }

}