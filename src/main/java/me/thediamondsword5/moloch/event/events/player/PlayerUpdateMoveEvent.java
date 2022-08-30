package me.thediamondsword5.moloch.event.events.player;

import net.spartanb312.base.event.EventCenter;
import net.minecraft.util.MovementInput;

public class PlayerUpdateMoveEvent extends EventCenter {
    public MovementInput movementInput;

    public PlayerUpdateMoveEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }
}
