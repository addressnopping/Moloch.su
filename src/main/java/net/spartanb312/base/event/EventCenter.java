package net.spartanb312.base.event;

public class EventCenter {

    private volatile boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        cancelled = true;
    }

}
