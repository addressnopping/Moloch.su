package me.thediamondsword5.moloch.event.events.entity;

import net.minecraft.entity.Entity;
import net.spartanb312.base.event.EventCenter;

public class ChorusEvent extends EventCenter {
    public Entity entity;

    public ChorusEvent(Entity entity) {
        this.entity = entity;
    }
}
