package net.spartanb312.base.event.events.client;

import net.spartanb312.base.event.EventCenter;

public class ChatEvent extends EventCenter {

    protected String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return this.message;
    }

}