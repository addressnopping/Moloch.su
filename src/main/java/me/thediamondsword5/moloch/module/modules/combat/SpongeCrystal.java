package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.concurrent.repeat.RepeatUnit;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.CrystalUtil;
import net.spartanb312.base.utils.ItemUtils;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import net.spartanb312.base.utils.math.Pair;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runRepeat;

@Parallel(runnable = true)
@ModuleInfo(name = "SpongeCrystal", category = Category.COMBAT, description = "Places sponges to allow for crystal placements in water")
public class SpongeCrystal extends Module {

    Setting<Page> page = setting("Page", Page.General);

    Setting<Integer> targetUpdateDelay = setting("TargetUpdateDelay", 50, 1, 200).des("Milliseconds to update the target position calculations").whenAtMode(page, Page.General);
    Setting<Boolean> packetPlace = setting("PacketPlace", true).des("Uses packets to place sponge").whenAtMode(page, Page.General);
    Setting<Boolean> targetMobs = setting("TargetMobs", false).des("Target monsters").whenAtMode(page, Page.General);
    Setting<Boolean> rotate = setting("Rotate", false).des("Rotates player to face position to place sponge").whenAtMode(page, Page.General);
    Setting<Boolean> conserveSponges = setting("ConserveSponges", false).des("Only places more sponges if the previously placed one is destroyed").whenAtMode(page, Page.General);
    Setting<Float> detectionRange = setting("DetectionRange", 10.0f, 0.0f, 10.0f).des("Minimum distance from a target entity to start calculating place positions").whenAtMode(page, Page.General);
    Setting<Float> range = setting("Range", 4.5f, 0.0f, 8.0f).des("Range to begin attempting to place sponges").whenAtMode(page, Page.General);
    Setting<Float> wallRange = setting("WallRange", 3.0f, 0.0f, 5.0f).des("Range to begin attemping to place sponges when place position is behind a wall").whenAtMode(page, Page.General);
    Setting<Float> minDamage = setting("MinDamage", 4.5f, 0.0f, 36.0f).des("Minimum damage to target to place sponge").whenAtMode(page, Page.General);
    Setting<Boolean> noSuicide = setting("NoSuicide", true).des("When at low health, don't place in areas where a crystal can kill you").whenAtMode(page, Page.General);
    Setting<Boolean> lethalOverride = setting("LethalOverride", true).des("Ignores max self damage when opponent can be popped and you can have a certain amount of health remaining").whenAtMode(page, Page.General);
    Setting<Float> lethalRemainingHealth = setting("LethalRemainingHealth", 8.0f, 0.0f, 36.0f).des("Min health remaining after you can break a crystal to pop opponent").whenTrue(lethalOverride).whenAtMode(page, Page.General);
    Setting<Float> maxSelfDamage = setting("MaxSelfDamage", 12.0f, 0.0f, 36.0f).des("Maximum damage that a crystal placed in targeted position can do to you").whenAtMode(page, Page.General);

    Setting<Boolean> solid = setting("Solid", true).des("Render solid box at position to place sponge").whenAtMode(page, Page.Render);
    Setting<Color> solidColor = setting("SolidColor", new Color(new java.awt.Color(100, 61, 255, 50).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 61, 255, 50)).whenAtMode(page, Page.Render);
    Setting<Boolean> lines = setting("Lines", true).des("Render wireframe box at position to place sponge").whenAtMode(page, Page.Render);
    Setting<Float> linesWidth = setting("LinesWidth", 1.0f, 1.0f, 5.0f).des("Width of wireframe box lines").whenAtMode(page, Page.Render);
    Setting<Color> linesColor = setting("LinesColor", new Color(new java.awt.Color(255, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 120)).whenAtMode(page, Page.Render);
    Setting<Boolean> fade = setting("Fade", true).des("Fade out render on place").whenAtMode(page, Page.Render);
    Setting<Float> fadeSpeed = setting("FadeSpeed", 1.0f, 0.1f, 3.0f).des("Fade out render speed").whenTrue(fade).whenAtMode(page, Page.Render);
    Setting<Boolean> move = setting("Move", true).des("Move render up on place").whenTrue(fade).whenAtMode(page, Page.Render);
    Setting<Float> moveSpeed = setting("MoveSpeed", 1.0f, 0.1f, 3.0f).des("Move render up speed").whenTrue(move).whenTrue(fade).whenAtMode(page, Page.Render);

    private final List<RepeatUnit> repeatUnits = new ArrayList<>();
    private final HashMap<BlockPos, Float> animateMap = new HashMap<>();
    private final Timer timer = new Timer();
    private BlockPos toPlacePos = null;
    private BlockPos toRenderPos = null;
    private BlockPos prevSpongePos = null;

    public SpongeCrystal() {
        repeatUnits.add(updateCalc);
        repeatUnits.forEach(it -> {
            it.suspend();
            runRepeat(it);
        });
    }

    RepeatUnit updateCalc = new RepeatUnit(() -> targetUpdateDelay.getValue(), () -> {
        Pair<BlockPos, Entity> data = CrystalUtil.calcPlace(targetMobs.getValue(), detectionRange.getValue(), range.getValue(), wallRange.getValue(),
                                                            minDamage.getValue(), maxSelfDamage.getValue(), lethalOverride.getValue(), lethalRemainingHealth.getValue(),
                                                            noSuicide.getValue(), true, false);
        toPlacePos = data.a;
    });

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (fade.getValue()) {
            DebugThing.debugInt = animateMap.size();
            int passedms = (int) timer.hasPassed();
            timer.reset();

            for (Map.Entry<BlockPos, Float> entry : new HashMap<>(animateMap).entrySet()) {
                if (entry.getValue() <= 0.0f) {
                    animateMap.remove(entry.getKey());
                    continue;
                }

                if (move.getValue()) {
                    GL11.glTranslatef(0.0f, (300.0f - entry.getValue()) / 500.0f * moveSpeed.getValue(), 0.0f);
                }
                
                if (solid.getValue()) {
                    SpartanTessellator.drawBlockFullBox(new Vec3d(entry.getKey()), false, 1.0f, new java.awt.Color(solidColor.getValue().getColorColor().getRed(), solidColor.getValue().getColorColor().getGreen(), solidColor.getValue().getColorColor().getBlue(), (int) (solidColor.getValue().getAlpha() * entry.getValue() / 300.0f)).getRGB());
                }

                if (lines.getValue()) {
                    SpartanTessellator.drawBlockLineBox(new Vec3d(entry.getKey()), false, 1.0f, linesWidth.getValue(), new java.awt.Color(linesColor.getValue().getColorColor().getRed(), linesColor.getValue().getColorColor().getGreen(), linesColor.getValue().getColorColor().getBlue(), (int) (linesColor.getValue().getAlpha() * entry.getValue() / 300.0f)).getRGB());
                }

                if (move.getValue()) {
                    GL11.glTranslatef(0.0f, -(300.0f - entry.getValue()) / 500.0f * moveSpeed.getValue(), 0.0f);
                }

                if (passedms < 1000) {
                    animateMap.put(entry.getKey(), entry.getValue() - passedms * fadeSpeed.getValue() / 3.0f);
                }
            }
        }
        else if (toRenderPos != null) {
            if (solid.getValue()) {
                SpartanTessellator.drawBlockFullBox(new Vec3d(toRenderPos), false, 1.0f, solidColor.getValue().getColor());
            }

            if (lines.getValue()) {
                SpartanTessellator.drawBlockLineBox(new Vec3d(toRenderPos), false, 1.0f, linesWidth.getValue(), linesColor.getValue().getColor());
            }
        }
    }

    @Override
    public void onTick() {
        if (toRenderPos != null && mc.world.getBlockState(toRenderPos).getBlock() != Blocks.SPONGE) {
            toRenderPos = null;
        }

        if (toPlacePos == null) {
            toRenderPos = null;
            return;
        }

        if (conserveSponges.getValue() && prevSpongePos != null) {
            if (mc.world.getBlockState(prevSpongePos).getBlock() == Blocks.SPONGE) {
                return;
            }
            else {
                prevSpongePos = null;
            }
        }

        if (mc.world.getBlockState(BlockUtil.extrudeBlock(toPlacePos, EnumFacing.UP)).getBlock() == Blocks.WATER
                || mc.world.getBlockState(BlockUtil.extrudeBlock(toPlacePos, EnumFacing.UP)).getBlock() == Blocks.FLOWING_WATER) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos pos = BlockUtil.extrudeBlock(toPlacePos, facing);
                if (BlockUtil.isFacePlaceble(pos, EnumFacing.UP, true)) {

                    if (conserveSponges.getValue() && prevSpongePos == null) {
                        prevSpongePos = BlockUtil.extrudeBlock(pos, EnumFacing.UP);
                    }

                    if (fade.getValue()) {
                        animateMap.put(BlockUtil.extrudeBlock(pos, EnumFacing.UP), 300.0f);
                    }
                    toRenderPos = BlockUtil.extrudeBlock(pos, EnumFacing.UP);

                    int prevSlot = 9999;
                    if (ItemUtils.isItemInHotbar(Item.getItemFromBlock(Blocks.SPONGE))) {
                        prevSlot = mc.player.inventory.currentItem;
                        ItemUtils.switchToSlot(ItemUtils.findItemInHotBar(Item.getItemFromBlock(Blocks.SPONGE)));
                    }

                    BlockUtil.placeBlock(pos, EnumFacing.UP, packetPlace.getValue(), false, rotate.getValue());

                    if (prevSlot != 9999) {
                        ItemUtils.switchToSlot(prevSlot);
                    }

                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        repeatUnits.forEach(RepeatUnit::resume);
        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        repeatUnits.forEach(RepeatUnit::suspend);
        moduleDisableFlag = true;
        prevSpongePos = null;
        toRenderPos = null;
    }


    enum Page {
        General,
        Render
    }
}
