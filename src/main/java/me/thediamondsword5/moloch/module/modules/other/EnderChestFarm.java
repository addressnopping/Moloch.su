package me.thediamondsword5.moloch.module.modules.other;

import me.thediamondsword5.moloch.event.events.player.OnUpdateWalkingPlayerEvent;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.concurrent.repeat.RepeatUnit;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.notification.NotificationManager;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.spartanb312.base.utils.ItemUtils;
import net.spartanb312.base.utils.RotationUtil;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import net.spartanb312.base.utils.math.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runRepeat;

@Parallel(runnable = true)
@ModuleInfo(name = "EnderChestFarm", category = Category.OTHER, description = "Automatically places and mines ender chests for obsidian")
public class EnderChestFarm extends Module {

    private final List<RepeatUnit> repeatUnits = new ArrayList<>();
    private final Timer timer = new Timer();
    private boolean placingUpFlag = false;
    private int prevSlot = 0;
    private int eChestPrevSlot = 0;
    public static boolean switchFlag = false;
    private BlockPos renderPos = null;
    public static final List<Pair<BlockPos, EnumFacing>> placeableSpots = new ArrayList<>();
    private float prevYaw = 0;
    private float prevPitch = 0;
    private boolean prevPlayerChosePlace = false;
    private int eChestSlot = 0;
    private boolean eChestInitSlotFlag = false;
    private long placeTimeStamp = 0L;

    Setting<SwapMode> swapMode = setting("SwapMode", SwapMode.FromInventory).des("Where to swap from and to ender chests to mine");
    Setting<Float> placeDelay = setting("PlaceDelay", 100.0f, 1.0f, 1000.0f).des("Delay for placing ender chests");
    Setting<Boolean> playerChosePlace = setting("PlayerChosePlace", false).des("Only replace ender chests once player puts one down instead of automatically finding a place to place ender chests").whenAtMode(swapMode, SwapMode.HotbarOnly);
    Setting<Boolean> packetPlace = setting("PacketPlace", true).des("Use packets to place ender chests");
    Setting<Boolean> rotate = setting("Rotate", true).des("Rotate to ender chests");
    Setting<Boolean> clientSideRotate = setting("ClientSideRotate", false).des("Force client to look at ender chests").whenTrue(rotate);
    Setting<Boolean> packetMine = setting("PacketMine", true).des("Uses packets to mine instead of clicking on the block").only(v -> !(!clientSideRotate.getValue() && rotate.getValue()));
    Setting<Boolean> render = setting("Render", true).des("Renders a box on the location where ender chests are being placed");
    Setting<Boolean> renderSolid = setting("RenderSolid", true).des("Use solid box for ender chest position render").whenTrue(render);
    Setting<me.thediamondsword5.moloch.core.common.Color> solidColor = setting("SolidColor", new me.thediamondsword5.moloch.core.common.Color(new Color(255, 255, 255, 19).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 19)).whenTrue(renderSolid).whenTrue(render);
    Setting<Boolean> renderLines = setting("RenderLines", true).des("Use outline box for ender chest position render").whenTrue(render);
    Setting<Float> renderLinesWidth = setting("LinesWidth", 1.0f, 1.0f, 5.0f).des("Width of lines of outline box render").whenTrue(renderLines).whenTrue(render);
    Setting<me.thediamondsword5.moloch.core.common.Color> linesColor = setting("LinesColor", new me.thediamondsword5.moloch.core.common.Color(new Color(255, 255, 255, 101).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 101)).whenTrue(renderLines).whenTrue(render);

    public EnderChestFarm() {
        repeatUnits.add(doRotate);
        repeatUnits.forEach(it -> {
            it.suspend();
            runRepeat(it);
        });
    }

    RepeatUnit doRotate = new RepeatUnit(() -> 1, () -> {
        if (rotate.getValue() && clientSideRotate.getValue() && renderPos != null && mc.player != null && mc.world != null) {

            Vec3d rotatePos = BlockUtil.getBlockVecFaceCenter(BlockUtil.extrudeBlock(renderPos, placingUpFlag ? EnumFacing.UP : EnumFacing.DOWN), EnumFacing.UP);
            float[] rotations = RotationUtil.getRotations(
                    mc.player.getPositionEyes(mc.getRenderPartialTicks()),
                    new Vec3d(rotatePos.x, rotatePos.y, rotatePos.z));

            mc.player.rotationYaw = rotations[0];
            mc.player.rotationPitch = rotations[1];
        }
    });

    @Override
    public void onEnable() {
        repeatUnits.forEach(RepeatUnit::resume);

        placeableSpots.clear();
        renderPos = null;

        placeTimeStamp = System.currentTimeMillis();

        if (mc.player != null) {
            prevYaw = mc.player.rotationYaw;
            prevPitch = mc.player.rotationPitch;
        }
        else {
            prevYaw = 0.0f;
            prevPitch = 0.0f;
        }

        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        repeatUnits.forEach(RepeatUnit::suspend);

        if (switchFlag) {
            if (swapMode.getValue() == SwapMode.FromInventory && eChestPrevSlot != 99999) {
                mc.playerController.windowClick(0, eChestPrevSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, eChestSlot + 36, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, eChestPrevSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
            }

            if (swapMode.getValue() == SwapMode.Offhand && eChestPrevSlot != 99999) {
                mc.playerController.windowClick(0, eChestPrevSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, eChestPrevSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
            }

            mc.player.inventory.currentItem = prevSlot;
            switchFlag = false;
        }

        if (rotate.getValue()) {
            if (clientSideRotate.getValue()) {
                mc.player.rotationYaw = prevYaw;
                mc.player.rotationPitch = prevPitch;
            }
        }

        placeableSpots.clear();
        renderPos = null;

        eChestInitSlotFlag = false;
        moduleDisableFlag = true;
        BlockUtil.packetMiningFlag = false;
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (renderPos != null && render.getValue()) {
            if (renderSolid.getValue()) {
                SpartanTessellator.drawBlockFullBox(new Vec3d(renderPos), false, 1.0f, solidColor.getValue().getColor());
            }

            if (renderLines.getValue()) {
                SpartanTessellator.drawBlockLineBox(new Vec3d(renderPos), false, 1.0f, renderLinesWidth.getValue(), linesColor.getValue().getColor());
            }
        }
    }

    @Override
    public void onTick() {
        //break stuff
        if (renderPos != null && !placeableSpots.isEmpty()) {
            if (BlockUtil.isBlockPlaceable(renderPos)) {

                ItemUtils.switchToSlot(ItemUtils.fastestMiningTool(Blocks.ENDER_CHEST));
                if (!packetMine.getValue() && (clientSideRotate.getValue() || !rotate.getValue()) || !BlockUtil.packetMiningFlag) {
                    BlockUtil.mineBlock(renderPos, BlockUtil.getVisibleBlockSide(new Vec3d(renderPos)), (!clientSideRotate.getValue() && rotate.getValue()) || packetMine.getValue());
                    BlockUtil.packetMiningFlag = true;
                }
            }

            double remainingTime = BlockUtil.packetMineStartTime + BlockUtil.blockBrokenTime(renderPos, mc.player.itemStackMainHand) - System.currentTimeMillis();
            if (!BlockUtil.isBlockPlaceable(renderPos) || remainingTime < -500) {
                BlockUtil.packetMiningFlag = false;
            }
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (playerChosePlace.getValue() && event.packet instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.ENDER_CHEST)) {
            placeableSpots.add(new Pair<>(((CPacketPlayerTryUseItemOnBlock) event.packet).getPos(),
                    ((CPacketPlayerTryUseItemOnBlock) event.packet).getDirection()));
        }
    }

    @Listener
    public void onUpdateWalkingPlayer(OnUpdateWalkingPlayerEvent event) {
        update();

        if (renderPos != null && !placeableSpots.isEmpty() && BlockUtil.isBlockPlaceable(renderPos)) {
            double remainingTime = BlockUtil.packetMineStartTime + BlockUtil.blockBrokenTime(renderPos, mc.player.itemStackMainHand) - System.currentTimeMillis();
            if (((System.currentTimeMillis() - placeTimeStamp <= 50) || (remainingTime <= 100 && remainingTime >= 0)) && !clientSideRotate.getValue()) {
                float[] rotats = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), BlockUtil.getBlockVecFaceCenter(BlockUtil.extrudeBlock(renderPos, placingUpFlag ? EnumFacing.UP : EnumFacing.DOWN), EnumFacing.UP));
                RotationUtil.setYawAndPitchBlock(rotats[0], rotats[1]);
            }
        }
    }

    private void update() {
        if ((!ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) && swapMode.getValue() == SwapMode.HotbarOnly)
                || (!ItemUtils.isItemInInventory(Item.getItemFromBlock(Blocks.ENDER_CHEST)) && (swapMode.getValue() == SwapMode.FromInventory || swapMode.getValue() == SwapMode.Offhand))) {
            NotificationManager.error("No ender chests to place!");
            ModuleManager.getModule(EnderChestFarm.class).disable();
            return;
        }

        if (!eChestInitSlotFlag) {
            eChestSlot = mc.player.inventory.currentItem;
            eChestInitSlotFlag = true;
        }

        //clear place spots when changing place calc mode
        if (prevPlayerChosePlace != playerChosePlace.getValue()) {
            placeableSpots.clear();
            renderPos = null;
        }
        prevPlayerChosePlace = playerChosePlace.getValue();

        //place stuff
        BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

        if (renderPos != null) {
            placingUpFlag = renderPos.y >= pos.y + 2;
        }

        if (!playerChosePlace.getValue()) {
            placeableSpots.clear();
            findPlacements();
        }

        if (!placeableSpots.isEmpty()) {
            renderPos = BlockUtil.extrudeBlock(new BlockPos(placeableSpots.get(0).a), placingUpFlag ? EnumFacing.DOWN : EnumFacing.UP);

            if (!BlockUtil.isBlockPlaceable(renderPos)) {
                if (timer.passed(placeDelay.getValue())) {

                    if (!switchFlag) {
                        eChestPrevSlot = ItemUtils.itemSlotIDinInventory(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                        prevSlot = mc.player.inventory.currentItem;
                        switchFlag = true;
                    }

                    if (swapMode.getValue() == SwapMode.HotbarOnly || (swapMode.getValue() == SwapMode.FromInventory && ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)))) {
                        ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.ENDER_CHEST));
                    }

                    if (swapMode.getValue() == SwapMode.FromInventory && ItemUtils.isItemInInventory(Item.getItemFromBlock(Blocks.ENDER_CHEST)) &&
                            !ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) && !ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                        ItemUtils.swapItemFromInvToHotBar(Item.getItemFromBlock(Blocks.ENDER_CHEST), eChestSlot);
                    }

                    if (swapMode.getValue() == SwapMode.Offhand && ItemUtils.isItemInInventory(Item.getItemFromBlock(Blocks.ENDER_CHEST)) && mc.player.getHeldItemOffhand().getItem() != Item.getItemFromBlock(Blocks.ENDER_CHEST)) {
                        int slotID = ItemUtils.itemSlotIDinInventory(Item.getItemFromBlock(Blocks.ENDER_CHEST));

                        if (slotID != 99999) {
                            mc.playerController.windowClick(0, slotID, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(0, slotID, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.updateController();
                        }
                    }

                    placeTimeStamp = System.currentTimeMillis();

                    BlockUtil.placeBlock(placeableSpots.get(0).a, placeableSpots.get(0).b, packetPlace.getValue(), swapMode.getValue() == SwapMode.Offhand,
                            !clientSideRotate.getValue() && rotate.getValue());

                    timer.reset();
                }
            }
        }
    }

    private BlockPos[] tryToPlaceSpots(BlockPos originalPos) {
        return new BlockPos[] {
                new BlockPos(originalPos.x + 1, originalPos.y, originalPos.z),
                new BlockPos(originalPos.x - 1, originalPos.y, originalPos.z),
                new BlockPos(originalPos.x, originalPos.y, originalPos.z + 1),
                new BlockPos(originalPos.x, originalPos.y, originalPos.z - 1),

                new BlockPos(originalPos.x + 1, originalPos.y + 1, originalPos.z),
                new BlockPos(originalPos.x - 1, originalPos.y + 1, originalPos.z),
                new BlockPos(originalPos.x, originalPos.y + 1, originalPos.z + 1),
                new BlockPos(originalPos.x, originalPos.y + 1, originalPos.z - 1),

                new BlockPos(originalPos.x, originalPos.y + 5, originalPos.z)
        };
    }

    private void findPlacements() {
        BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

        for (BlockPos toPlacePos : tryToPlaceSpots(pos)) {
            for (EnumFacing facing : EnumFacing.values()) {
                if ((facing == EnumFacing.UP && toPlacePos.y < pos.y + 2) || (facing == EnumFacing.DOWN && toPlacePos.y >= pos.y + 2)) {

                    if (toPlacePos.y >= pos.y + 2) {
                        for (int i = 1; i < 6; ++i) {
                            toPlacePos = new BlockPos(pos.x, pos.y + i, pos.z);
                            BlockPos base = BlockUtil.extrudeBlock(toPlacePos, EnumFacing.UP);

                            if (isFacePlacebleForEChest(base, EnumFacing.DOWN) &&
                                    isBlockPlaceableForEChestUp(base))
                                break;
                        }
                    }

                    BlockPos offset = BlockUtil.extrudeBlock(toPlacePos, facing.getOpposite());
                    if (isFacePlacebleForEChest(offset, facing) && BlockUtil.isBlockPlaceable(offset))
                        placeableSpots.add(new Pair<>(offset, facing));

                }
            }
        }
    }

    private boolean isFacePlacebleForEChest(BlockPos pos, EnumFacing facing) {
        BlockPos pos1 = BlockUtil.extrudeBlock(pos, facing);
        return mc.world.checkNoEntityCollision(new AxisAlignedBB(pos1), mc.player) && (mc.world.getBlockState(pos1).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(pos1).getBlock() == Blocks.AIR || mc.world.getBlockState(pos1).getBlock() == Blocks.LAVA || mc.world.getBlockState(pos1).getBlock() == Blocks.FLOWING_LAVA || mc.world.getBlockState(pos1).getBlock() == Blocks.WATER || mc.world.getBlockState(pos1).getBlock() == Blocks.FLOWING_WATER);
    }

    private boolean isBlockPlaceableForEChestUp(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() != Blocks.ENDER_CHEST && mc.world.getBlockState(pos).getBlock() != Blocks.AIR && mc.world.getBlockState(pos).getBlock() != Blocks.WATER && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA;
    }

    enum SwapMode {
        FromInventory,
        HotbarOnly,
        Offhand
    }
}
