package me.thediamondsword5.moloch.event.events.render;

import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.event.EventCenter;

public class RenderViewEntityGuiEvent extends EventCenter {
    public EntityPlayer entityPlayer;

    public RenderViewEntityGuiEvent(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }
}
