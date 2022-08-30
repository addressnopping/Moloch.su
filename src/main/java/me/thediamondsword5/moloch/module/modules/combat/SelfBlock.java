package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.utils.BlockUtil;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.notification.NotificationManager;
import net.spartanb312.base.utils.CrystalUtil;
import net.spartanb312.base.utils.EntityUtil;
import net.spartanb312.base.utils.ItemUtils;
import net.spartanb312.base.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.spartanb312.base.utils.RotationUtil.*;

@Parallel
@ModuleInfo(name = "SelfBlock", category = Category.COMBAT, description = "Fuck a block")
public class SelfBlock extends Module {
    private static final Timer timer = new Timer();
    double prevPlayerPosY;
    int prevSlot;
    BlockPos pos;
    boolean flag;
    double originalPosX;
    double originalPosY;
    double originalPosZ;
    Item originalItem;
    int failedSelfBlockNum = 0;

    Setting<BlockMode> blockMode = setting("BlockMode", BlockMode.Obsidian);
    Setting<Mode> selfBlockMode = setting("SelfBlockMode", Mode.Packet).des("Type of selfblock");
    Setting<Boolean> switchPlus = setting("SwitchAlternate", true).des("Uses different autoswitch to switch to target block in hotbar (is supposed to bypass a cooldown or smt but it cant switch properly while youre holding a tool like pickaxes, swords, bows, etc...)");
    Setting<Boolean> rotate = setting("Rotate", true).des("Rotate to burrow");
    Setting<Boolean> spoofOnGround = setting("SpoofOnGround", true).des("Spoof being on ground").only(v -> selfBlockMode.getValue() == Mode.Packet);
    Setting<Boolean> breakCrystals = setting("BreakCrystals", true).des("If an end crystal's hitbox is blocking your place positions, try and break it");
    Setting<Boolean> antiSuicideCrystal = setting("AntiSuicideCrystal", true).des("Breaks crystal as long as it doesn't make you go below a certain health amount").whenTrue(breakCrystals);
    Setting<Float> minHealthRemaining = setting("MinHealthRemain", 8.0f, 1.0f, 36.0f).des("Min health that crystal should leave you with after you break it").whenTrue(antiSuicideCrystal).whenTrue(breakCrystals);
    Setting<Float> maxCrystalDamage = setting("MaxCrystalDamage", 11.0f, 0.0f, 36.0f).des("Don't break a crystal if it's damage to you exceeds this amount").whenFalse(antiSuicideCrystal).whenTrue(breakCrystals);
    Setting<Boolean> toggle = setting("Toggle", true).des("Disable when done").only(v -> selfBlockMode.getValue() != Mode.NoLag);
    //auto disables on shutting down client bc it spams errors if u reload it with it on (see MixinMinecraft)
    Setting<Integer> delay = setting("Delay", 292, 0, 1000).des("No toggle block place delay").only(v -> selfBlockMode.getValue() != Mode.NoLag).whenFalse(toggle);
    Setting<Boolean> antiStuck = setting("AntiStuck", true).des("Stops trying to place when stuck").only(v -> selfBlockMode.getValue() != Mode.NoLag).whenFalse(toggle);
    Setting<Boolean> waitPlace = setting("WaitPlace", false).des("Waits until able to place then tries to place").only(v -> selfBlockMode.getValue() != Mode.NoLag).whenFalse(toggle);
    Setting<Integer> maxTry = setting("MaxTry", 4, 1, 20).only(v -> selfBlockMode.getValue() != Mode.NoLag).whenFalse(toggle).whenFalse(waitPlace).whenTrue(antiStuck);
    Setting<DisableMode> disableMode = setting("DisableCheckMode", DisableMode.Both).des("No toggle auto disable check mode").only(v -> selfBlockMode.getValue() != Mode.NoLag).whenFalse(toggle);
    Setting<Double> yPower = setting("YPower", 0.9d, -10.0d, 10.0d).des("Y motion").only(v -> selfBlockMode.getValue() != Mode.NoLag);
    Setting<Boolean> center = setting("Center", false).des("Center player on burrow").only(v -> selfBlockMode.getValue() != Mode.NoLag);

    @Override
    public void onEnable() {
        moduleEnableFlag = true;
        originalItem = mc.player.getHeldItemMainhand().getItem();

        prevPlayerPosY = mc.player.posY;
        pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY)), Math.floor(mc.player.posZ));

        if (selfBlockMode.getValue() != Mode.Packet) {
            mc.player.jump();
        }

        originalPosX = mc.player.posX;
        originalPosY = mc.player.posY;
        originalPosZ = mc.player.posZ;
    }

    @Override
    public void onTick() {
        if (!ItemUtils.isItemInHotbar(blockMode.getValue() == BlockMode.WitherSkull ? Items.SKULL : Item.getItemFromBlock(burrowBlock()))) {
            NotificationManager.error("No blocks to place!");
            ModuleManager.getModule(SelfBlock.class).disable();
            return;
        }

        if (breakCrystals.getValue()) {
            CrystalUtil.breakBlockingCrystals(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos), antiSuicideCrystal.getValue(), minHealthRemaining.getValue(), maxCrystalDamage.getValue(), rotate.getValue());
        }

        switch (selfBlockMode.getValue()) {
            case Packet: {
                if (!toggle.getValue() && selfBlockMode.getValue() == Mode.Packet) {
                    if (!EntityUtil.isBurrowed(mc.player)) {

                        if (antiStuck.getValue() ? failedSelfBlockNum >= maxTry.getValue() : failedSelfBlockNum == -999) {
                            this.disable();
                        }
                        else if (timer.passed(delay.getValue()) && (!waitPlace.getValue() || EntityUtil.isPosPlaceable(new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY)), Math.floor(mc.player.posZ))))) {

                            if (center.getValue()) EntityUtil.setCenter();
                            posPacket();
                            packetBurrow();
                            timer.reset();
                            if (antiStuck.getValue()) {
                                BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY + 0.2), Math.floor(mc.player.posZ));
                                if (mc.world.getBlockState(pos).getBlock() != (blockMode.getValue() == BlockMode.WitherSkull ? Blocks.SKULL : burrowBlock())) {
                                    failedSelfBlockNum += 1;
                                }
                            }
                        }
                    }
                    else {
                        if ((((originalPosX > EntityUtil.selfCenterPos().x + 0.6 || originalPosX < EntityUtil.selfCenterPos().x - 0.6) || (originalPosZ > EntityUtil.selfCenterPos().z + 0.6 || originalPosZ < EntityUtil.selfCenterPos().z - 0.6)) && (disableMode.getValue() == DisableMode.Horizontal || disableMode.getValue() == DisableMode.Both)) || (originalPosY != mc.player.posY && (disableMode.getValue() == DisableMode.Vertical || disableMode.getValue() == DisableMode.Both)))
                            this.disable();
                    }
                }

                if (toggle.getValue()) {
                    if (center.getValue()) EntityUtil.setCenter();
                    if (blockMode.getValue() != BlockMode.WitherSkull) posPacket();
                    packetBurrow();
                    this.disable();
                }

                break;
            }

            case NoLag: {
                if (mc.player.onGround) {
                    mc.player.jump();
                }

                if (mc.player.posY >= prevPlayerPosY + 1.04) {
                    prevSlot = mc.player.inventory.currentItem;
                    if (!switchPlus.getValue() || (switchPlus.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.AIR)) {
                        if (blockMode.getValue() == BlockMode.WitherSkull) ItemUtils.switchToSlot(ItemUtils.findItemInHotBar(Items.SKULL));
                        else ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(burrowBlock()));
                    }
                    else {
                        if (blockMode.getValue() == BlockMode.WitherSkull) ItemUtils.switchToSlotButBetter(ItemUtils.findItemInHotBar(Items.SKULL));
                        else ItemUtils.switchToSlotButBetter(ItemUtils.findBlockInHotBar(burrowBlock()));
                    }

                    BlockUtil.placeBlock(BlockUtil.extrudeBlock(pos, EnumFacing.DOWN), EnumFacing.UP, true,false, rotate.getValue());

                    if (!switchPlus.getValue() || (switchPlus.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.AIR)) {
                        ItemUtils.switchToSlot(prevSlot);
                    }
                    else {
                        if (blockMode.getValue() == BlockMode.WitherSkull) ItemUtils.switchToSlotButBetter(ItemUtils.findItemInHotBar(Items.SKULL));
                        else ItemUtils.switchToSlotButBetter(ItemUtils.findBlockInHotBar(burrowBlock()));
                    }
                    mc.player.motionY = 0.0f;
                    resetRotationBlock();
                    flag = true;
                }

                if (!mc.player.onGround && flag) {
                    flag = false;
                    this.disable();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        failedSelfBlockNum = 0;
        moduleDisableFlag = true;
    }


    private void posPacket() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42, mc.player.posZ, spoofOnGround.getValue()));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, spoofOnGround.getValue()));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.01, mc.player.posZ, spoofOnGround.getValue()));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16, mc.player.posZ, spoofOnGround.getValue()));
    }

    private void packetBurrow() {
        prevSlot = mc.player.inventory.currentItem;
        if (!switchPlus.getValue() || (switchPlus.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.AIR)) {
            if (blockMode.getValue() == BlockMode.WitherSkull) ItemUtils.switchToSlot(ItemUtils.findItemInHotBar(Items.SKULL));
            else ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(burrowBlock()));
        }
        else {
            if (blockMode.getValue() == BlockMode.WitherSkull) ItemUtils.switchToSlotButBetter(ItemUtils.findItemInHotBar(Items.SKULL));
            else ItemUtils.switchToSlotButBetter(ItemUtils.findBlockInHotBar(burrowBlock()));
        }

        pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY)), Math.floor(mc.player.posZ));
        BlockUtil.placeBlock(BlockUtil.extrudeBlock(pos, EnumFacing.DOWN), EnumFacing.UP, true,false, rotate.getValue());
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPower.getValue(), mc.player.posZ, false));

        if (!switchPlus.getValue() || (switchPlus.getValue() && originalItem == Items.AIR)) {
            ItemUtils.switchToSlot(prevSlot);
        }
        else {
            if (blockMode.getValue() == BlockMode.WitherSkull) ItemUtils.switchToSlotButBetter(ItemUtils.findItemInHotBar(Items.SKULL));
            else ItemUtils.switchToSlotButBetter(ItemUtils.findBlockInHotBar(burrowBlock()));
        }
    }

    private Block burrowBlock() {
        switch (blockMode.getValue()) {
            case Obsidian: return Blocks.OBSIDIAN;

            case EnderChest: return Blocks.ENDER_CHEST;

            case EndRod: return Blocks.END_ROD;
        }
        return Blocks.OBSIDIAN;
    }

    enum BlockMode {
        Obsidian,
        EnderChest,
        WitherSkull,
        EndRod
    }

    enum Mode {
        Packet,
        NoLag
    }

    enum DisableMode {
        Horizontal,
        Vertical,
        Both
    }
}
