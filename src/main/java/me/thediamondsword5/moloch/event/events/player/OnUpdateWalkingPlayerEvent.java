package me.thediamondsword5.moloch.event.events.player;

import net.spartanb312.base.event.EventCenter;

public class OnUpdateWalkingPlayerEvent extends EventCenter {
    public float yaw;
    public float pitch;

    public OnUpdateWalkingPlayerEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
