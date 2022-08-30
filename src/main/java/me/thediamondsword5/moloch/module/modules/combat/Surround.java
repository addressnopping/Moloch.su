package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import net.spartanb312.base.utils.math.Pair;

import java.util.*;
//TODO: extend diagonal, anti hitbox city extend
@Parallel
@ModuleInfo(name = "Surround", category = Category.COMBAT, description = "Put obsidian around your feet to protect them from crystal damage")
public class Surround extends Module {

    Setting<Page> page = setting("Page", Page.Place);
    Setting<Integer> placeDelay = setting("PlaceDelay", 70, 0, 500).des("Delay between place attempts in milliseconds").whenAtMode(page, Page.Place);
    Setting<Integer> multiPlace = setting("MultiPlace", 4, 1, 5).des("Blocks to place at once").whenAtMode(page, Page.Place);
    Setting<Boolean> onPacket = setting("OnPacketBlockChange", true).des("Tries to place on SPacketBlockChange / SPacketMultiBlockChange").whenAtMode(page, Page.Place);
    Setting<Boolean> packetPlace = setting("PacketPlace", true).des("Uses packets to place blocks").whenAtMode(page, Page.Place);
    Setting<Boolean> antiGhostBlock = setting("AntiGhostBlock", true).des("Hits blocks after placing to remove it if its a client side only (ghost) block").whenAtMode(page, Page.Place);
    Setting<Boolean> rotate = setting("Rotate", false).des("Spoofs rotations to place blocks").whenAtMode(page, Page.Place);
    Setting<Boolean> center = setting("Center", false).des("Moves you to the center of the blockpos").whenAtMode(page, Page.Place);
    Setting<Boolean> disableOnLeaveHole = setting("DisableOnLeaveHole", true).des("Automatically disables module when you aren't in the same blockpos anymore").whenAtMode(page, Page.Place);
    Setting<Boolean> extend = setting("Extend", false).des("Extends surround if somebody tries to mine part of it to prevent being citied").whenAtMode(page, Page.Place);
    Setting<Boolean> breakCrystals = setting("BreakCrystals", true).des("Breaks crystals that are blocking surround").whenAtMode(page, Page.Place);
    Setting<Float> breakCrystalsDelay = setting("BreakCrystalsDelay", 50.0f, 0.0f, 1000.0f).des("Delay in milliseconds between attempts to break crystal").whenTrue(breakCrystals).whenAtMode(page, Page.Place);
    Setting<Boolean> antiSuicideCrystal = setting("AntiSuicideCrystal", true).des("Breaks crystal as long as it doesn't make you go below a certain health amount").whenTrue(breakCrystals).whenAtMode(page, Page.Place);
    Setting<Float> minHealthRemaining = setting("MinHealthRemain", 8.0f, 1.0f, 36.0f).des("Min health that crystal should leave you with after you break it").whenTrue(antiSuicideCrystal).whenTrue(breakCrystals).whenAtMode(page, Page.Place);
    Setting<Float> maxCrystalDamage = setting("MaxCrystalDamage", 11.0f, 0.0f, 36.0f).des("Don't break crystal if it could deal this much damage or more").whenFalse(antiSuicideCrystal).whenTrue(breakCrystals).whenAtMode(page, Page.Place);
    Setting<Boolean> onlyVisible = setting("OnlyVisible", false).des("Only tries to place on sides of blocks that you can see").whenAtMode(page, Page.Place);
    Setting<Boolean> useEnderChest = setting("UseEnderChest", false).des("Uses ender chests when you run out of obsidian").whenAtMode(page, Page.Place);

    Setting<Boolean> render = setting("RenderPlacePos", true).des("Render a box for positions to be placed in").whenAtMode(page, Page.Render);
    Setting<Boolean> fade = setting("Fade", false).des("Fades alpha of render after blocks are placed").whenTrue(render).whenAtMode(page, Page.Render);
    Setting<Float> fadeSpeed = setting("FadeSpeed", 2.0f, 0.1f, 3.0f).des("Fade speed of render").whenTrue(fade).whenTrue(render).whenAtMode(page, Page.Render);
    Setting<Boolean> solid = setting("Solid", true).whenTrue(render).whenAtMode(page, Page.Render);
    Setting<Color> solidColor = setting("SolidColor", new Color(new java.awt.Color(100, 61, 255, 19).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 61, 255, 19)).whenTrue(render).whenTrue(solid).whenAtMode(page, Page.Render);
    Setting<Boolean> lines = setting("Lines", true).whenTrue(render).whenAtMode(page, Page.Render);
    Setting<Float> linesWidth = setting("LinesWidth", 1.0f, 1.0f, 5.0f).whenTrue(render).whenTrue(lines).whenAtMode(page, Page.Render);
    Setting<Color> linesColor = setting("LinesColor", new Color(new java.awt.Color(100, 61, 255, 101).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 61, 255, 101)).whenTrue(render).whenTrue(lines).whenAtMode(page, Page.Render);

    private final Timer placeTimer = new Timer();
    private final Timer fadeTimer = new Timer();
    private final Timer breakCrystalsTimer = new Timer();
    private final HashMap<BlockPos, Float> toRenderPos = new HashMap<>();
    private final HashMap<BlockPos, Boolean> onPacketPlaceFlagMap = new HashMap<>();
    private BlockPos currentPlayerPos = null;
    private boolean centeredFlag = false;
    private boolean isTickPlacingFlag = false;

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (render.getValue()) {
            int passedms = (int) fadeTimer.hasPassed();
            fadeTimer.reset();
            for (Map.Entry<BlockPos, Float> entry : new HashMap<>(toRenderPos).entrySet()) {
                if (entry.getValue() <= 0.0f) {
                    toRenderPos.remove(entry.getKey());
                    continue;
                }

                if (solid.getValue()) {
                    SpartanTessellator.drawBlockFullBox(new Vec3d(entry.getKey()), false, 1.0f, new java.awt.Color(solidColor.getValue().getColorColor().getRed(), solidColor.getValue().getColorColor().getGreen(), solidColor.getValue().getColorColor().getBlue(), (int)(solidColor.getValue().getAlpha() * entry.getValue() / 300.0f)).getRGB());
                }

                if (lines.getValue()) {
                    SpartanTessellator.drawBlockLineBox(new Vec3d(entry.getKey()), false, 1.0f, linesWidth.getValue(), new java.awt.Color(linesColor.getValue().getColorColor().getRed(), linesColor.getValue().getColorColor().getGreen(), linesColor.getValue().getColorColor().getBlue(), (int)(linesColor.getValue().getAlpha() * entry.getValue() / 300.0f)).getRGB());
                }

                if (fade.getValue()) {
                    if (passedms < 1000) {
                        toRenderPos.put(entry.getKey(), entry.getValue() - passedms * fadeSpeed.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        onPacketPlaceFlagMap.clear();
        currentPlayerPos = null;
        centeredFlag = false;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!isTickPlacingFlag && mc.world != null && mc.player != null) {
            if (extend.getValue() && event.getPacket() instanceof SPacketBlockBreakAnim) {
                SPacketBlockBreakAnim packet = ((SPacketBlockBreakAnim) event.getPacket());
                BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY)), Math.floor(mc.player.posZ));

                if (placePoses(false).contains(packet.getPosition())) {
                    BlockPos extendedPos = new BlockPos((packet.getPosition().x * 2.0f) - playerPos.x, (packet.getPosition().y * 2.0f) - playerPos.y, (packet.getPosition().z * 2.0f) - playerPos.z);
                    extendedPos = BlockUtil.extrudeBlock(extendedPos, EnumFacing.DOWN);

                    if (BlockUtil.isFacePlaceble(extendedPos, EnumFacing.UP, true)) {

                        if (breakCrystals.getValue()) {
                            CrystalUtil.breakBlockingCrystals(mc.world.getBlockState(BlockUtil.extrudeBlock(extendedPos, EnumFacing.UP)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(extendedPos, EnumFacing.UP)), antiSuicideCrystal.getValue(), minHealthRemaining.getValue(), maxCrystalDamage.getValue(), rotate.getValue());
                        }

                        int prevSlot = mc.player.inventory.currentItem;
                        if (ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                            ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.OBSIDIAN));
                        }
                        else if (useEnderChest.getValue() && ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                            ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.ENDER_CHEST));
                        }

                        BlockUtil.placeBlock(extendedPos, EnumFacing.UP, packetPlace.getValue(), false, rotate.getValue());
                        if (antiGhostBlock.getValue() && mc.playerController.currentGameType != GameType.CREATIVE) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockUtil.extrudeBlock(extendedPos, EnumFacing.UP), BlockUtil.getVisibleBlockSide(new Vec3d(BlockUtil.extrudeBlock(extendedPos, EnumFacing.UP)))));
                        if (render.getValue()) toRenderPos.put(BlockUtil.extrudeBlock(extendedPos, EnumFacing.UP), 300.0f);

                        ItemUtils.switchToSlot(prevSlot);
                    }
                }
            }

            if (onPacket.getValue()) {
                if (event.getPacket() instanceof SPacketBlockChange) {

                    BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY) - 1), Math.floor(mc.player.posZ));
                    if (!placePoses(true).contains(((SPacketBlockChange) event.packet).getBlockPosition())) return;

                    boolean flag1 = false;
                    for (Map.Entry<BlockPos, Boolean> entry : onPacketPlaceFlagMap.entrySet()) {
                        if (BlockUtil.isSameBlockPos(entry.getKey(), ((SPacketBlockChange) event.packet).getBlockPosition())) {
                            flag1 = true;
                            if (!entry.getValue()) return;
                            else {
                                onPacketPlaceFlagMap.put(entry.getKey(), false);
                            }
                        }
                    }
                    if (!flag1) {
                        return;
                    }

                    if (!((SPacketBlockChange) event.packet).getBlockState().getMaterial().isReplaceable()) return;

                    Pair<BlockPos, EnumFacing> data = getPlaceData(playerPos, ((SPacketBlockChange) event.packet).getBlockPosition());
                    if (data == null) return;

                    if (!onlyVisible.getValue() && data.a == playerPos && BlockUtil.isBlockPlaceable(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(data.a, data.b), EnumFacing.UP))) {
                        return;
                    }

                    if ((mc.world.getBlockState(BlockUtil.extrudeBlock(data.a, data.b)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(data.a, data.b)))
                            .intersects(mc.player.getEntityBoundingBox())) {
                        return;
                    }

                    EntityUtil.entitiesListFlag = true;
                    boolean flag = false;
                    for (Entity entity : EntityUtil.entitiesList()) {
                        if (entity == mc.player || !(entity instanceof EntityPlayer)) {
                            continue;
                        }

                        if ((mc.world.getBlockState(BlockUtil.extrudeBlock(data.a, data.b)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(data.a, data.b)))
                                .intersects(entity.getEntityBoundingBox())) {
                            flag = true;
                        }
                    }
                    EntityUtil.entitiesListFlag = false;
                    if (flag) {
                        return;
                    }


                    int prevSlot = mc.player.inventory.currentItem;
                    if (ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                        ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.OBSIDIAN));
                    }
                    else if (useEnderChest.getValue() && ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                        ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.ENDER_CHEST));
                    }

                    BlockUtil.placeBlock(data.a, data.b, packetPlace.getValue(), false, rotate.getValue());
                    if (antiGhostBlock.getValue() && mc.playerController.currentGameType != GameType.CREATIVE) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockUtil.extrudeBlock(data.a, data.b), BlockUtil.getVisibleBlockSide(new Vec3d(BlockUtil.extrudeBlock(data.a, data.b)))));
                    if (render.getValue()) toRenderPos.put(BlockUtil.extrudeBlock(data.a, data.b), 300.0f);

                    ItemUtils.switchToSlot(prevSlot);
                }

                /*
                if (event.getPacket() instanceof SPacketMultiBlockChange) {
                    BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY) - 1), Math.floor(mc.player.posZ));
                    int index = 0;

                    int prevSlot = mc.player.inventory.currentItem;
                    if (ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                        ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.OBSIDIAN));
                    }
                    else if (useEnderChest.getValue() && ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                        ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.ENDER_CHEST));
                    }

                    for (SPacketMultiBlockChange.BlockUpdateData blockUpdateData : ((SPacketMultiBlockChange) event.getPacket()).getChangedBlocks()) {
                        if (!placePoses(true).contains(blockUpdateData.getPos())) continue;

                        boolean flag1 = false;
                        boolean flag2 = false;
                        for (Map.Entry<BlockPos, Boolean> entry : onPacketPlaceFlagMap.entrySet()) {
                            if (BlockUtil.isSameBlockPos(entry.getKey(), blockUpdateData.getPos())) {
                                flag2 = true;
                                if (!entry.getValue()) flag1 = true;
                                else {
                                    onPacketPlaceFlagMap.put(entry.getKey(), false);
                                }
                            }
                        }
                        if (flag1 || !flag2) continue;

                        if (onPacketPlaceFlagMap.get(blockUpdateData.getPos()) == null
                                || !onPacketPlaceFlagMap.get(blockUpdateData.getPos())) return;
                        else onPacketPlaceFlagMap.put(blockUpdateData.getPos(), false);

                        if (!blockUpdateData.getBlockState().getMaterial().isReplaceable()) continue;

                        Pair<BlockPos, EnumFacing> data = getPlaceData(playerPos, blockUpdateData.getPos());
                        if (data == null) continue;

                        if (index >= multiPlace.getValue()) {
                            continue;
                        }

                        if (!onlyVisible.getValue() && data.a == playerPos && BlockUtil.isBlockPlaceable(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(data.a, data.b), EnumFacing.UP))) {
                            continue;
                        }

                        if ((mc.world.getBlockState(BlockUtil.extrudeBlock(data.a, data.b)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(data.a, data.b)))
                                .intersects(mc.player.getEntityBoundingBox())) {
                            continue;
                        }

                        EntityUtil.entitiesListFlag = true;
                        boolean flag = false;
                        for (Entity entity : EntityUtil.entitiesList()) {
                            if (entity == mc.player || !(entity instanceof EntityPlayer)) {
                                continue;
                            }

                            if ((mc.world.getBlockState(BlockUtil.extrudeBlock(data.a, data.b)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(data.a, data.b)))
                                    .intersects(entity.getEntityBoundingBox())) {
                                flag = true;
                            }
                        }
                        EntityUtil.entitiesListFlag = false;
                        if (flag) {
                            continue;
                        }

                        BlockUtil.placeBlock(data.a, data.b, packetPlace.getValue(), false, rotate.getValue());
                        if (antiGhostBlock.getValue() && mc.playerController.currentGameType != GameType.CREATIVE) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockUtil.extrudeBlock(data.a, data.b), BlockUtil.getVisibleBlockSide(new Vec3d(BlockUtil.extrudeBlock(data.a, data.b)))));
                        if (render.getValue()) toRenderPos.put(BlockUtil.extrudeBlock(data.a, data.b), 300.0f);

                        index++;
                    }

                    ItemUtils.switchToSlot(prevSlot);
                }
                 */
            }
        }
    }

    @Override
    public void onTick() {
        if (mc.world != null && mc.player != null && onPacket.getValue()) {
            for (BlockPos pos : placePoses(true)) {
                if (!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)) {
                    onPacketPlaceFlagMap.put(pos, true);
                }
            }
        }

        if (mc.world != null && mc.player != null && breakCrystalsTimer.passed(breakCrystalsDelay.getValue()) && breakCrystals.getValue()) {
            for (BlockPos pos : placePoses(true)) {
                CrystalUtil.breakBlockingCrystals(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos), antiSuicideCrystal.getValue(), minHealthRemaining.getValue(), maxCrystalDamage.getValue(), rotate.getValue());
            }
            breakCrystalsTimer.reset();
        }

        tickPlace();
    }

    private void tickPlace() {
        if (mc.world != null && mc.player != null && placeTimer.passed(placeDelay.getValue())) {
            if (render.getValue() && !fade.getValue()) toRenderPos.clear();

            if (!ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) && (!useEnderChest.getValue() || !ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)))) {
                toggle();
                ChatUtil.sendNoSpamErrorMessage("No blocks to place!");
                return;
            }

            if (center.getValue() && !centeredFlag) {
                EntityUtil.setCenter();
                centeredFlag = true;
            }

            BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY) - 1), Math.floor(mc.player.posZ));
            if (currentPlayerPos == null) currentPlayerPos = playerPos;

            if (disableOnLeaveHole.getValue() && !BlockUtil.isSameBlockPos(currentPlayerPos, new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY) - 1), Math.floor(mc.player.posZ)))) {
                toggle();
                return;
            }

            isTickPlacingFlag = true;
            List<Pair<BlockPos, EnumFacing>> list = new ArrayList<>();

            int index = 0;
            for (Pair<BlockPos, EnumFacing> data : onlyVisible.getValue() ? visiblePlacePos(playerPos) : placePos(playerPos)) {
                if (index >= multiPlace.getValue()) {
                    continue;
                }

                if (!onlyVisible.getValue() && data.a == playerPos && BlockUtil.isBlockPlaceable(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(data.a, data.b), EnumFacing.UP))) {
                    continue;
                }

                if (!BlockUtil.isFacePlaceble(data.a, data.b, false)) {
                    continue;
                }

                if ((mc.world.getBlockState(BlockUtil.extrudeBlock(data.a, data.b)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(data.a, data.b)))
                        .intersects(mc.player.getEntityBoundingBox())) {
                    continue;
                }

                EntityUtil.entitiesListFlag = true;
                boolean flag = false;
                for (Entity entity : EntityUtil.entitiesList()) {
                    if (entity == mc.player || !(entity instanceof EntityPlayer)) {
                        continue;
                    }

                    if ((mc.world.getBlockState(BlockUtil.extrudeBlock(data.a, data.b)).getSelectedBoundingBox(mc.world, BlockUtil.extrudeBlock(data.a, data.b)))
                            .intersects(entity.getEntityBoundingBox())) {
                        flag = true;
                    }
                }
                EntityUtil.entitiesListFlag = false;
                if (flag) {
                    continue;
                }

                list.add(new Pair<>(data.a, data.b));

                if (render.getValue()) toRenderPos.put(BlockUtil.extrudeBlock(data.a, data.b), 300.0f);

                index++;
            }

            if (!list.isEmpty()) {
                int prevSlot = mc.player.inventory.currentItem;
                if (ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                    ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.OBSIDIAN));
                }
                else if (useEnderChest.getValue() && ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                    ItemUtils.switchToSlot(ItemUtils.findBlockInHotBar(Blocks.ENDER_CHEST));
                }

                list.forEach(data -> {
                    BlockUtil.placeBlock(data.a, data.b, packetPlace.getValue(), false, rotate.getValue());
                    if (antiGhostBlock.getValue() && mc.playerController.currentGameType != GameType.CREATIVE) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockUtil.extrudeBlock(data.a, data.b), BlockUtil.getVisibleBlockSide(new Vec3d(BlockUtil.extrudeBlock(data.a, data.b)))));
                });
                placeTimer.reset();

                ItemUtils.switchToSlot(prevSlot);
            }
            isTickPlacingFlag = false;
        }
    }

    private Pair<BlockPos, EnumFacing>[] placePos(BlockPos pos) {
        return new Pair[] {
                new Pair<>(new BlockPos(pos.x + 1, pos.y, pos.z), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x - 1, pos.y, pos.z), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x, pos.y, pos.z + 1), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x, pos.y, pos.z - 1), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x + 2, pos.y + 1, pos.z), EnumFacing.WEST),
                new Pair<>(new BlockPos(pos.x - 2, pos.y + 1, pos.z), EnumFacing.EAST),
                new Pair<>(new BlockPos(pos.x, pos.y + 1, pos.z + 2), EnumFacing.NORTH),
                new Pair<>(new BlockPos(pos.x, pos.y + 1, pos.z - 2), EnumFacing.SOUTH),
                new Pair<>(pos, EnumFacing.EAST),
                new Pair<>(pos, EnumFacing.WEST),
                new Pair<>(pos, EnumFacing.SOUTH),
                new Pair<>(pos, EnumFacing.NORTH)

        };
    }

    private Pair<BlockPos, EnumFacing>[] visiblePlacePos(BlockPos pos) {
        return new Pair[] {
                new Pair<>(new BlockPos(pos.x + 1, pos.y, pos.z), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x - 1, pos.y, pos.z), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x, pos.y, pos.z + 1), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x, pos.y, pos.z - 1), EnumFacing.UP),
                new Pair<>(new BlockPos(pos.x + 2, pos.y + 1, pos.z), EnumFacing.WEST),
                new Pair<>(new BlockPos(pos.x - 2, pos.y + 1, pos.z), EnumFacing.EAST),
                new Pair<>(new BlockPos(pos.x, pos.y + 1, pos.z + 2), EnumFacing.NORTH),
                new Pair<>(new BlockPos(pos.x, pos.y + 1, pos.z - 2), EnumFacing.SOUTH)
        };
    }

    private Pair<BlockPos, EnumFacing> getPlaceData(BlockPos playerPos, BlockPos pos) {
        if (!onlyVisible.getValue()) {
            if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x + 1, playerPos.y, playerPos.z))) {
                return new Pair<>(playerPos, EnumFacing.WEST);
            }
            else if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x - 1, playerPos.y, playerPos.z))) {
                return new Pair<>(playerPos, EnumFacing.EAST);
            }
            else if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x, playerPos.y, playerPos.z + 1))) {
                return new Pair<>(playerPos, EnumFacing.NORTH);
            }
            else if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x, playerPos.y, playerPos.z - 1))) {
                return new Pair<>(playerPos, EnumFacing.SOUTH);
            }
        }

        if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x + 1, playerPos.y + 1, playerPos.z))) {
            Pair<BlockPos, EnumFacing> toPlacePos1 = new Pair<>(BlockUtil.extrudeBlock(playerPos, EnumFacing.EAST), EnumFacing.UP);
            Pair<BlockPos, EnumFacing> toPlacePos2 = new Pair<>(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(toPlacePos1.a, EnumFacing.EAST), EnumFacing.UP), EnumFacing.WEST);
            if (BlockUtil.isBlockPlaceable(toPlacePos1.a)) {
                return toPlacePos1;
            }
            else {
                return toPlacePos2;
            }
        }
        else if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x - 1, playerPos.y + 1, playerPos.z))) {
            Pair<BlockPos, EnumFacing> toPlacePos1 = new Pair<>(BlockUtil.extrudeBlock(playerPos, EnumFacing.WEST), EnumFacing.UP);
            Pair<BlockPos, EnumFacing> toPlacePos2 = new Pair<>(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(toPlacePos1.a, EnumFacing.WEST), EnumFacing.UP), EnumFacing.EAST);
            if (BlockUtil.isBlockPlaceable(toPlacePos1.a)) {
                return toPlacePos1;
            }
            else {
                return toPlacePos2;
            }
        }
        else if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x, playerPos.y + 1, playerPos.z + 1))) {
            Pair<BlockPos, EnumFacing> toPlacePos1 = new Pair<>(BlockUtil.extrudeBlock(playerPos, EnumFacing.SOUTH), EnumFacing.UP);
            Pair<BlockPos, EnumFacing> toPlacePos2 = new Pair<>(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(toPlacePos1.a, EnumFacing.SOUTH), EnumFacing.UP), EnumFacing.NORTH);
            if (BlockUtil.isBlockPlaceable(toPlacePos1.a)) {
                return toPlacePos1;
            }
            else {
                return toPlacePos2;
            }
        }
        else if (BlockUtil.isSameBlockPos(pos, new BlockPos(playerPos.x, playerPos.y + 1, playerPos.z - 1))) {
            Pair<BlockPos, EnumFacing> toPlacePos1 = new Pair<>(BlockUtil.extrudeBlock(playerPos, EnumFacing.NORTH), EnumFacing.UP);
            Pair<BlockPos, EnumFacing> toPlacePos2 = new Pair<>(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(toPlacePos1.a, EnumFacing.NORTH), EnumFacing.UP), EnumFacing.SOUTH);
            if (BlockUtil.isBlockPlaceable(toPlacePos1.a)) {
                return toPlacePos1;
            }
            else {
                return toPlacePos2;
            }
        }

        return null;
    }

    private List<BlockPos> placePoses(boolean includeBottom) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(Math.round(mc.player.posY) - 1), Math.floor(mc.player.posZ));

        if (includeBottom) {
            list.add(new BlockPos(pos.x + 1, pos.y, pos.z));
            list.add(new BlockPos(pos.x - 1, pos.y, pos.z));
            list.add(new BlockPos(pos.x, pos.y, pos.z + 1));
            list.add(new BlockPos(pos.x, pos.y, pos.z - 1));
        }
        list.add(new BlockPos(pos.x + 1, pos.y + 1, pos.z));
        list.add(new BlockPos(pos.x - 1, pos.y + 1, pos.z));
        list.add(new BlockPos(pos.x, pos.y + 1, pos.z + 1));
        list.add(new BlockPos(pos.x, pos.y + 1, pos.z - 1));

        return list;
    }

    enum Page {
        Place,
        Render
    }
}
