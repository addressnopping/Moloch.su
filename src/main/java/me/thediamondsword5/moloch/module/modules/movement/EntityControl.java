package me.thediamondsword5.moloch.module.modules.movement;

import me.thediamondsword5.moloch.event.events.entity.EntityControlEvent;
import me.thediamondsword5.moloch.event.events.player.TravelEvent;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.chunk.EmptyChunk;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.EntityUtil;

@Parallel
@ModuleInfo(name = "EntityControl", category = Category.MOVEMENT, description = "Instantly ride any ridable animal and control it's movements easier")
public class EntityControl extends Module {

    Setting<Boolean> mountBypass = setting("MountBypass", false).des("Allows you to ride chested animals on servers that don't allow it (maybe)");
    Setting<Boolean> speedModify = setting("SpeedModify", true).des("Allows you to go faster than entity");
    Setting<Float> speed = setting("Speed", 2.0f, 0.0f, 10.0f).des("Speed of ridden entities").whenTrue(speedModify);

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (mountBypass.getValue() && event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) event.getPacket()).getAction() != CPacketUseEntity.Action.INTERACT_AT
            && ((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world) instanceof AbstractChestHorse) {
            event.cancel();
        }
    }

    @Listener
    public void entityControlSet(EntityControlEvent event) {
        event.cancel();
    }

    @Listener
    public void onTravel(TravelEvent event) {
        if (mc.world != null && mc.player != null && mc.player.ridingEntity != null) {
            if ((mc.player.ridingEntity instanceof EntityPig || mc.player.ridingEntity instanceof AbstractHorse || mc.player.ridingEntity instanceof EntityBoat)
                    && mc.player.ridingEntity.getControllingPassenger() == mc.player) {

                double motionX = -Math.sin(EntityUtil.getMovementYaw()) * speed.getValue();
                double motionZ = Math.cos(EntityUtil.getMovementYaw()) * speed.getValue();

                if ((mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f)
                        && mc.world.getChunk((int)(mc.player.ridingEntity.posX + motionX), (int)(mc.player.ridingEntity.posZ + motionZ)) instanceof EmptyChunk) {
                    mc.player.ridingEntity.motionX = motionX;
                    mc.player.ridingEntity.motionZ = motionZ;
                }
            }
        }
    }
}
