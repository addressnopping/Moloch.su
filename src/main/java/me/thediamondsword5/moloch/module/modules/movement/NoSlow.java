package me.thediamondsword5.moloch.module.modules.movement;

import me.thediamondsword5.moloch.event.events.player.UpdateTimerEvent;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.event.events.player.PlayerUpdateMoveEvent;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.spartanb312.base.utils.RotationUtil;

@Parallel
@ModuleInfo(name = "NoSlow", category = Category.MOVEMENT, description = "Prevent slowing down")
public class NoSlow extends Module {
    public static NoSlow instance;
    boolean sneakingFlag;

    Setting<ItemMode> itemMode = setting("ItemMode", ItemMode.Normal).des("No slow mode for items");
    Setting<Boolean> items = setting("Items", true).des("No slowing down on item use");
    public Setting<Boolean> soulSand = setting("SoulSand", false).des("No slowing down on soul sand");
    //see MixinBlockSoulSand
    Setting<Boolean> slime = setting("Slime", false).des("No slowing down on slime blocks");
    public Setting<CobWebMode> cobWebMode = setting("CobWebMode", CobWebMode.None).des("Ways to prevent webs from slowing you down");
    //see MixinBlockWeb
    Setting<Float> webHorizontalFactor = setting("WebHSpeed", 2.0f, 0.0f, 100.0f).des("Horizontal speed in web multiplier").whenAtMode(cobWebMode, CobWebMode.Motion);
    Setting<Float> webVerticalFactor = setting("WebVSpeed", 2.0f, 0.0f, 100.0f).des("Vertical speed in web multiplier").whenAtMode(cobWebMode, CobWebMode.Motion);
    Setting<Float> cobwebTimerSpeed = setting("CobwebTimerSpeed", 10.0f, 1.0f, 15.0f).des("Speed of timer in cobweb").whenAtMode(cobWebMode, CobWebMode.Timer);
    Setting<Boolean> sneak = setting("Sneak", false).des("No slowing down on sneaking");

    public NoSlow() {
        instance = this;
    }

    @Override
    public void onRenderTick() {
        if (slime.getValue()) Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.4945f);
        else Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8f);
    }

    @Listener
    public void onUpdateMove(PlayerUpdateMoveEvent event) {
        if (itemMode.getValue() == ItemMode._2B2TSneak && !mc.player.isSneaking() && !mc.player.isRiding()) {
            if (mc.player.isHandActive()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneakingFlag = true;
            }
            else if (sneakingFlag) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneakingFlag = false;
            }
        }

        if (cobWebMode.getValue() == CobWebMode.Motion && mc.player.isInWeb) {
            mc.player.motionX *= webHorizontalFactor.getValue();
            mc.player.motionZ *= webHorizontalFactor.getValue();
            mc.player.motionY *= webVerticalFactor.getValue();
        }
    }

    @Listener
    public void onUpdateTimer(UpdateTimerEvent event) {
        if (mc.world == null || mc.player == null) return;

        BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
        if (cobWebMode.getValue() == CobWebMode.Timer && (mc.world.getBlockState(BlockUtil.extrudeBlock(playerPos, EnumFacing.UP)).getBlock() == Blocks.WEB || mc.world.getBlockState(playerPos).getBlock() == Blocks.WEB || mc.world.getBlockState(BlockUtil.extrudeBlock(playerPos, EnumFacing.DOWN)).getBlock() == Blocks.WEB)) {
            event.timerSpeed = cobwebTimerSpeed.getValue();
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if ((items.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) || (sneak.getValue() && mc.player.isSneaking())) {
            event.getMovementInput().moveForward /= 0.2f;
            event.getMovementInput().moveStrafe /= 0.2f;
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        moduleDisableFlag = true;
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && items.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            switch (itemMode.getValue()) {
                case NCPStrict: {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
                    break;
                }

                case _2B2TBypass: {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    break;
                }
            }
        }
    }


    enum ItemMode {
        Normal,
        NCPStrict,
        _2B2TSneak,
        _2B2TBypass
    }

    public enum CobWebMode {
        None,
        Cancel,
        Motion,
        Timer
    }
}
