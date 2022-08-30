package me.thediamondsword5.moloch.event.events.render;

import net.spartanb312.base.event.EventCenter;
import net.minecraft.entity.Entity;

public class RenderEntityPreEvent extends EventCenter {
    public Entity entityIn;

    public RenderEntityPreEvent(Entity entityIn) {
        this.entityIn = entityIn;
    }
}
