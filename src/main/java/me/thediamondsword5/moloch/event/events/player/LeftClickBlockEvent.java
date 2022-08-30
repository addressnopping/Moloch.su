package me.thediamondsword5.moloch.event.events.player;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.spartanb312.base.event.EventCenter;

public class LeftClickBlockEvent extends EventCenter {
    public BlockPos blockPos;
    public EnumFacing face;

    public LeftClickBlockEvent(BlockPos blockPos, EnumFacing face) {
        this.blockPos = blockPos;
        this.face = face;
    }
}
