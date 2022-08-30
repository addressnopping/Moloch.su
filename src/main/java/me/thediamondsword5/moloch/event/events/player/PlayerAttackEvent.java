package me.thediamondsword5.moloch.event.events.player;

import net.spartanb312.base.event.EventCenter;
import net.minecraft.entity.Entity;

public class PlayerAttackEvent extends EventCenter {
    public Entity target;

    public PlayerAttackEvent(Entity target) {
        this.target = target;
    }
}
