package me.thediamondsword5.moloch.event.events.entity;

import net.minecraft.entity.Entity;
import net.spartanb312.base.event.EventCenter;

public class DeathEvent extends EventCenter {
    public Entity entity;

    public DeathEvent(Entity entity) {
        this.entity = entity;
    }
}
