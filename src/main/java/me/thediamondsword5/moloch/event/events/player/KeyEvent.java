package me.thediamondsword5.moloch.event.events.player;

import net.spartanb312.base.event.EventCenter;

public class KeyEvent extends EventCenter {
    public boolean info;
    public boolean pressed;

    public KeyEvent(boolean info, boolean pressed) {
        this.info = info;
        this.pressed = pressed;
    }
}
