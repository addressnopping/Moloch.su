package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.event.events.player.DamageBlockEvent;
import me.thediamondsword5.moloch.event.events.player.LeftClickBlockEvent;
import me.thediamondsword5.moloch.event.events.player.OnUpdateWalkingPlayerEvent;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.graphics.SpartanTessellator;

@Parallel
@ModuleInfo(name = "Mine+", category = Category.COMBAT, description = "More efficient mining techniques (PacketMine && InstantMine)")
public class MinePlus extends Module {

    private BlockPos miningPos = null;
    private EnumFacing miningFace = EnumFacing.UP;
    private int prevSlot = 0;
    private boolean flag = false;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private boolean instantMineRotationFlag = false;
    private boolean onMined = false;
    private final Timer instantMineTimer = new Timer();

    Setting<Page> page = setting("Page", Page.Mine);
    Setting<Mode> mode = setting("Mode", Mode.PacketMine).des("On 2b2tpvp.net (and maybe other servers?) it helps to look at the block for a bit after tapping it to have it consistently work (from experience)").whenAtMode(page, Page.Mine);

    Setting<Swap> swap = setting("Swap", Swap.Normal).des("Ways to switch to best mining tool").whenAtMode(page, Page.Mine);
    Setting<Boolean> oppositeFaceHit = setting("OppositeFaceHit", false).des("Hit opposite face of block you're mining").whenAtMode(page, Page.Mine);
    Setting<Boolean> spamPackets = setting("SpamPackets", false).des("Spam break packets while mining (idk if this actually makes a difference, ig it makes it look more legit?)").whenAtMode(mode, Mode.PacketMine).whenAtMode(page, Page.Mine);
    Setting<Integer> instantMineDelay = setting("InstantMineDelay", 70, 0, 1000).des("Delay between each attempted instant mine break").whenAtMode(mode, Mode.InstantMine).whenAtMode(page, Page.Mine);
    Setting<Float> range = setting("Range", 8.0f, 1.0f, 10.0f).des("Range to stop mining if you get too far from the block").whenAtMode(page, Page.Mine);
    Setting<Boolean> rotate = setting("Rotate", false).des("Rotate to block on finish mining").whenAtMode(page, Page.Mine);

    Setting<Boolean> completedColor = setting("CompletedColor", true).des("Changes color on completion of mining").whenAtMode(page, Page.Render);
    Setting<Float> completedProgress = setting("CompletedProgress", 0.7f, 0.0f, 0.9f).des("At what fraction of progress should the color start changing").whenTrue(completedColor).whenAtMode(page, Page.Render);
    Setting<ScaleMode> scaleMode = setting("ScaleMode", ScaleMode.Expand).des("Changes size of render depending on mining progress").whenAtMode(page, Page.Render);
    Setting<Float> scaleFactor = setting("ScaleFactor", 0.5f, 0.1f, 1.0f).des("Steepness of mining render scale change").only(v -> scaleMode.getValue() != ScaleMode.None).whenAtMode(page, Page.Render);
    Setting<Boolean> solidBox = setting("SolidBox", true).whenAtMode(page, Page.Render);
    Setting<Color> solidBoxColor = setting("SolidBoxColor", new Color(new java.awt.Color(255, 50, 50, 19).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 19)).whenTrue(solidBox).whenAtMode(page, Page.Render);
    Setting<Color> solidBoxCompletedColor = setting("SolidBoxCompletedColor", new Color(new java.awt.Color(50, 255, 50, 19).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 19)).whenTrue(completedColor).whenTrue(solidBox).whenAtMode(page, Page.Render);
    Setting<Boolean> linesBox = setting("LinesBox", true).whenAtMode(page, Page.Render);
    Setting<Float> linesBoxWidth = setting("LinesBoxWidth", 1.0f, 1.0f, 5.0f).whenTrue(linesBox).whenAtMode(page, Page.Render);
    Setting<Color> linesBoxColor = setting("LinesBoxColor", new Color(new java.awt.Color(255, 50, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 175)).whenTrue(linesBox).whenAtMode(page, Page.Render);
    Setting<Color> linesBoxCompletedColor = setting("LinesBoxCompletedColor", new Color(new java.awt.Color(50, 255, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 175)).whenTrue(completedColor).whenTrue(linesBox).whenAtMode(page, Page.Render);

    @Override
    public String getModuleInfo() {
        return String.valueOf(mode.getValue());
    }

    @Override
    public void onDisable() {
        BlockUtil.packetMiningFlag = false;
        miningPos = null;
        moduleDisableFlag = true;
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (miningPos != null) {
            java.awt.Color solidColor = solidBoxColor.getValue().getColorColor();
            java.awt.Color linesColor = linesBoxColor.getValue().getColorColor();
            int bestSlot = ItemUtils.fastestMiningTool(mc.world.getBlockState(miningPos).getBlock());
            float progress = (float)(remainingTime() / BlockUtil.blockBrokenTime(miningPos, mc.player.inventory.getStackInSlot(bestSlot)));
            float renderScale = 1.0f;

            switch (scaleMode.getValue()) {
                case Expand: {
                    renderScale = MathUtilFuckYou.interpNonLinear(1.0f, 0.0f, progress, scaleFactor.getValue() / 15.0f);
                    break;
                }

                case Shrink: {
                    renderScale = MathUtilFuckYou.interpNonLinear(0.0f, 1.0f, progress, scaleFactor.getValue() / 15.0f);
                    break;
                }
            }

            if (completedColor.getValue() && MathUtilFuckYou.clamp(progress, 0.0f, 1.0f) < 1.0f - completedProgress.getValue()) {
                solidColor = ColorUtil.colorShift(solidColor, solidBoxCompletedColor.getValue().getColorColor(), 300.0f * (1.0f - (MathUtilFuckYou.clamp(progress, 0.0f, 1.0f) / (1.0f - completedProgress.getValue()))));
                linesColor = ColorUtil.colorShift(linesColor, linesBoxCompletedColor.getValue().getColorColor(), 300.0f * (1.0f - (MathUtilFuckYou.clamp(progress, 0.0f, 1.0f) / (1.0f - completedProgress.getValue()))));
            }

            if (solidBox.getValue()) {
                SpartanTessellator.drawBlockBBFullBox(miningPos, renderScale, new java.awt.Color(solidColor.getRed(), solidColor.getGreen(), solidColor.getBlue(), solidBoxColor.getValue().getAlpha()).getRGB());
            }

            if (linesBox.getValue()) {
                SpartanTessellator.drawBlockBBLineBox(miningPos, renderScale, linesBoxWidth.getValue(), new java.awt.Color(linesColor.getRed(), linesColor.getGreen(), linesColor.getBlue(), linesBoxColor.getValue().getAlpha()).getRGB());
            }
        }
    }

    @Override
    public void onTick() {
        if (spamPackets.getValue() && miningPos != null && BlockUtil.isBlockPlaceable(miningPos) && mode.getValue() == Mode.PacketMine && BlockUtil.packetMiningFlag) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, miningPos, miningFace));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, miningPos, miningFace));
        }
    }

    @Override
    public void onRenderTick() {
        if (miningPos != null) {
            if (!BlockUtil.isBlockPlaceable(miningPos)) {
                BlockUtil.packetMiningFlag = false;
                miningPos = null;
                return;
            }
            else {
                miningFace = BlockUtil.getVisibleBlockSide(new Vec3d(miningPos.x + 0.5, miningPos.y, miningPos.z + 0.5));
            }

            if (MathUtilFuckYou.getDistance(mc.player.getPositionVector(), new Vec3d(miningPos)) > range.getValue()) {
                miningPos = null;
                return;
            }

            switch (mode.getValue()) {
                case PacketMine: {
                    int bestSlot = ItemUtils.fastestMiningTool(mc.world.getBlockState(miningPos).getBlock());

                    if (remainingTime() <= 0 && remainingTime() >= -750) {
                        if (!flag) {
                            flag = true;
                            prevSlot = mc.player.inventory.currentItem;
                        }

                        switch (swap.getValue()) {
                            case Normal: {
                                ItemUtils.switchToSlot(bestSlot);
                                onMined = true;
                                break;
                            }

                            case Silent: {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(bestSlot));
                                onMined = true;
                                break;
                            }
                        }
                    }

                    if (!flag2 && remainingTime() <= 0 && BlockUtil.isBlockPlaceable(miningPos) && swap.getValue() == Swap.None && mc.player.inventory.currentItem == bestSlot) {
                        onMined = true;
                        flag2 = true;
                    }

                    if (swap.getValue() == Swap.None && !BlockUtil.isBlockPlaceable(miningPos)) {
                        miningPos = null;
                    }
                    break;
                }

                case InstantMine: {
                    if (mc.playerController != null) {
                        if (remainingTime() <= 0) {
                            if (BlockUtil.isBlockPlaceable(miningPos) && instantMineTimer.passed(instantMineDelay.getValue())) {
                                int bestSlot = ItemUtils.fastestMiningTool(mc.world.getBlockState(miningPos).getBlock());

                                instantMineRotationFlag = true;

                                if (!flag3) {
                                    prevSlot = mc.player.inventory.currentItem;
                                }

                                switch (swap.getValue()) {
                                    case Normal: {
                                        ItemUtils.switchToSlot(bestSlot);
                                        flag3 = true;
                                        break;
                                    }

                                    case Silent: {
                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(bestSlot));
                                        flag3 = true;
                                        break;
                                    }
                                }

                                mc.playerController.isHittingBlock = false;
                                mc.playerController.blockHitDelay = 0;
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, miningPos, miningFace));
                                instantMineTimer.reset();
                            }
                            else if (flag3) {
                                switch (swap.getValue()) {
                                    case Normal: {
                                        ItemUtils.switchToSlot(prevSlot);
                                        break;
                                    }

                                    case Silent: {
                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(prevSlot));
                                        break;
                                    }
                                }
                                flag3 = false;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Listener
    public void onLeftClickBlock(LeftClickBlockEvent event) {
        if (miningPos != null && event.blockPos == miningPos) {
            event.cancel();
        }

        BlockPos tempMiningPos = miningPos == null ? new BlockPos(0, -99999, 0) : miningPos;

        if (mc.world.getBlockState(event.blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(event.blockPos).getBlock() != Blocks.BARRIER && (remainingTime() <= 0.0 || ((event.blockPos.x != tempMiningPos.x) || (event.blockPos.y != tempMiningPos.y) || (event.blockPos.z != tempMiningPos.z)))) {

            BlockUtil.mineBlock(event.blockPos, event.face, true);

            if (oppositeFaceHit.getValue()) {
                BlockUtil.mineBlock(event.blockPos, event.face.getOpposite(), true);
            }

            miningPos = event.blockPos;
            BlockUtil.packetMiningFlag = true;
            flag2 = false;
        }
    }

    @Listener
    public void onDamageBlock(DamageBlockEvent event) {
        if (mode.getValue() == Mode.PacketMine && event.blockPos == miningPos) {
            event.cancel();
        }
    }

    @Listener
    public void onUpdateWalkingPlayer(OnUpdateWalkingPlayerEvent event) {
        if (mode.getValue() == Mode.PacketMine && miningPos != null && miningFace != null && mode.getValue() == Mode.PacketMine && onMined) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, miningPos, miningFace));
            mc.player.swingArm(EnumHand.MAIN_HAND);

            if (rotate.getValue()) {
                rotate();
            }

            if (swap.getValue() != Swap.None) {
                int bestSlot = ItemUtils.fastestMiningTool(mc.world.getBlockState(miningPos).getBlock());
                miningPos = null;

                if (flag) {
                    if (prevSlot != bestSlot) {
                        switch (swap.getValue()) {
                            case Normal: {
                                ItemUtils.switchToSlot(prevSlot);
                                break;
                            }

                            case Silent: {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(prevSlot));
                                break;
                            }
                        }
                    }
                    flag = false;
                }
            }

            onMined = false;
        }

        if (instantMineRotationFlag && rotate.getValue()) {
            rotate();
            instantMineRotationFlag = false;
        }
    }

    private void rotate() {
        float[] rotats = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), BlockUtil.getBlockVecFaceCenter(miningPos, miningFace));
        RotationUtil.setYawAndPitchBlock(rotats[0], rotats[1]);
    }

    private double remainingTime() {
        if (miningPos == null) return 0.0;
        int bestSlot = ItemUtils.fastestMiningTool(mc.world.getBlockState(miningPos).getBlock());
        return BlockUtil.packetMineStartTime + BlockUtil.blockBrokenTime(miningPos, mc.player.inventory.getStackInSlot(bestSlot)) - System.currentTimeMillis();
    }

    enum Page {
        Mine,
        Render
    }

    enum Mode {
        PacketMine,
        InstantMine
    }

    enum Swap {
        None,
        Normal,
        Silent
    }

    enum ScaleMode {
        Expand,
        Shrink,
        None
    }
}
