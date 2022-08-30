package me.thediamondsword5.moloch.event.events.player;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.spartanb312.base.event.EventCenter;

public class DamageBlockEvent extends EventCenter {
    public BlockPos blockPos;
    public EnumFacing face;

    public DamageBlockEvent(BlockPos blockPos, EnumFacing face) {
        this.blockPos = blockPos;
        this.face = face;
    }
}
