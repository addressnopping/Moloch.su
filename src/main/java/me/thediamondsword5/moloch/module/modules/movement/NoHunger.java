package me.thediamondsword5.moloch.module.modules.movement;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.mixin.mixins.accessor.AccessorCPacketPlayer;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "NoHunger", category = Category.MOVEMENT, description = "Stop being hungry")
public class NoHunger extends Module {

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.player != null) {
            if (event.packet instanceof CPacketEntityAction && (((CPacketEntityAction) event.packet).getAction().equals(CPacketEntityAction.Action.START_SPRINTING) || ((CPacketEntityAction) event.packet).getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING))) {
                event.cancel();
            }
            if (event.packet instanceof CPacketPlayer) {
                ((AccessorCPacketPlayer) event.packet).setOnGround((mc.player.fallDistance <= 0 || mc.playerController.getIsHittingBlock()) && mc.player.isElytraFlying());
            }
        }
    }
}
