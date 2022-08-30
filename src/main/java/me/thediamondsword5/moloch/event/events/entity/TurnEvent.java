package me.thediamondsword5.moloch.event.events.entity;

import net.minecraft.entity.Entity;
import net.spartanb312.base.event.EventCenter;

public class TurnEvent extends EventCenter {
    public Entity entity;
    public float yaw;
    public float pitch;

    public TurnEvent(Entity entity, float yaw, float pitch) {
        this.entity = entity;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
