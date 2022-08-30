package me.thediamondsword5.moloch.event.events.player;

import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.event.EventCenter;

public class PlayerMoveEvent extends EventCenter {
    public double motionX;
    public double motionY;
    public double motionZ;

    public PlayerMoveEvent(EntityPlayer player) {
        this.motionX = player.motionX;
        this.motionY = player.motionY;
        this.motionZ = player.motionZ;
    }
}
