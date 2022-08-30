package me.thediamondsword5.moloch.module.modules.movement;

import me.thediamondsword5.moloch.event.events.player.GroundedStepEvent;
import me.thediamondsword5.moloch.event.events.player.PlayerUpdateMoveEvent;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.CPacketPlayer;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.EntityUtil;

@Parallel
@ModuleInfo(name = "Step", category = Category.MOVEMENT, description = "Allows you go up blocks")
public class Step extends Module {

    public Setting<Boolean> vanilla = setting("VanillaMode", true).des("Way to step over blocks");
    Setting<Boolean> entityStep = setting("EntityStep", false).des("Modifies entities' step height");
    Setting<Float> entityStepHeight = setting("EntityStepHeight", 100.0f, 1.0f, 256.0f).des("Max entity step height").whenTrue(entityStep);
    Setting<Float> height = setting("Height", 2.0f, 1.0f, 2.5f).des("Max height to be able to step over");
    Setting<Boolean> toggle = setting("Toggle", false).des("Automatically disables module when you've stepped over blocks once");

    private final double[] offsetsOne = new double[]{0.42, 0.753};
    private final double[] offsetsOneAndHalf = new double[]{0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
    private final double[] offsetsTwo = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    private final double[] offsetsTwoAndHalf = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};

    @Override
    public String getModuleInfo() {
        if (vanilla.getValue()) {
            return "Vanilla";
        }
        else {
            return "Packet";
        }
    }

    @Override
    public void onDisable() {
        moduleDisableFlag = true;
        mc.player.stepHeight = 0.6f;
        if (mc.player.ridingEntity instanceof AbstractHorse || mc.player.ridingEntity instanceof EntityPig) mc.player.ridingEntity.stepHeight = 1.0f;
        else if (mc.player.ridingEntity != null) mc.player.ridingEntity.stepHeight = 0.0f;
    }

    @Override
    public void onRenderTick() {
        if (!vanilla.getValue() && EntityUtil.canStep() && mc.player.onGround) {
            packetStep();
        }

        if (entityStep.getValue() && mc.player.ridingEntity != null) {
            mc.player.ridingEntity.stepHeight = entityStepHeight.getValue();
        }

        if ((vanilla.getValue() || entityStep.getValue()) && toggle.getValue() && mc.player.posY - mc.player.lastTickPosY >= 1.0f) {
            toggle();
        }
    }

    @Listener
    public void onUpdateMove(PlayerUpdateMoveEvent event) {
        if (vanilla.getValue() && EntityUtil.canStep()) {
            mc.player.stepHeight = height.getValue();
        }
        else {
            mc.player.stepHeight = 0.6f;
        }
    }

    @Listener
    public void onGroundedStep(GroundedStepEvent event) {
        if (mc.player.ridingEntity instanceof AbstractHorse || mc.player.ridingEntity instanceof EntityPig) mc.player.ridingEntity.stepHeight = entityStep.getValue() ? entityStepHeight.getValue() : 1.0f;
        else if (mc.player.ridingEntity != null) mc.player.ridingEntity.stepHeight = entityStep.getValue() ? entityStepHeight.getValue() : 0.0f;
    }

    private void packetStep() {
        double[] extension = extend();
        float steppableHeight = 0.0f;
        final float[] offsets = new float[]{2.5f, 2.0f, 1.5f, 1.0f};

        for (float offset : offsets) {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(extension[0], offset + 0.1, extension[1])).isEmpty() &&
                    !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(extension[0], offset - 0.1, extension[1])).isEmpty()) {
                steppableHeight = offset;
            }
        }

        for (float offset : offsets) {
            if (height.getValue() >= offset && steppableHeight == offset) {
                if (offset == 1.0f) {
                    for (double d : offsetsOne) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + d, mc.player.posZ, mc.player.onGround));
                    }
                } else if (offset == 1.5f) {
                    for (double d : offsetsOneAndHalf) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + d, mc.player.posZ, mc.player.onGround));
                    }
                } else if (offset == 2.0f) {
                    for (double d : offsetsTwo) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + d, mc.player.posZ, mc.player.onGround));
                    }
                } else {
                    for (double d : offsetsTwoAndHalf) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + d, mc.player.posZ, mc.player.onGround));
                    }
                }

                mc.player.setPosition(mc.player.posX, mc.player.posY + offset, mc.player.posZ);

                if (toggle.getValue()) ModuleManager.getModule(Step.class).disable();
            }
        }
    }

    //from gamesense
    private static double[] extend() {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45.0f : 45.0f);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45.0f : -45.0f);
            }

            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin((yaw + 90.0f) * (Math.PI / 180.0f));
        final double cos = Math.cos((yaw + 90.0f) * (Math.PI / 180.0f));
        final double posX = forward * 0.1 * cos + (forward != 0.0f ? 0.0f : side) * 0.1 * sin;
        final double posZ = forward * 0.1 * sin - (forward != 0.0f ? 0.0f : side) * 0.1 * cos;
        return new double[]{posX, posZ};
    }
}
