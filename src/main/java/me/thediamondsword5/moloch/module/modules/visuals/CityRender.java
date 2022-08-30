package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.graphics.SpartanTessellator;

import java.util.HashMap;
import java.util.Map;

@Parallel
@ModuleInfo(name = "CityRender", category = Category.VISUALS, description = "Renders stuff to indicate blocks that can be used to city someone (mined out to crystal them)")
public class CityRender extends Module {

    Setting<Float> range = setting("Range", 6.0f, 0.0f, 15.0f).des("Range to start checking players for cityable blocks");
    Setting<Boolean> checkDiagonalCity = setting("DiagonalCity", true).des("Checks if a player can be citied diagonally instead of just directly next to the player");
    Setting<Boolean> oneBlockCrystalMode = setting("1.13+", false).des("Uses 1.13+ crystal placements to find cityable blocks where crystals can be placed in one block spaces");
    Setting<Boolean> self = setting("Self", true).des("Render cityable blocks for yourself");
    Setting<Boolean> ignoreFriends = setting("IgnoreFriends", true).des("Dont render cityable blocks for friends");
    Setting<Boolean> fade = setting("Fade", true).des("Fade renders in and out when cityable blocks are mined or when the player moves");
    Setting<Float> fadeSpeed = setting("FadeSpeed", 2.0f, 0.1f, 3.0f).des("Speed of how fast renders fade").whenTrue(fade);
    Setting<RenderMode> renderMode = setting("RenderMode", RenderMode.Box);
    Setting<Float> boxHeight = setting("BoxHeight", 1.0f, 0.0f, 1.0f).only(v -> renderMode.getValue() != RenderMode.Flat);
    Setting<Boolean> solid = setting("Solid", true).des("Solid render");
    Setting<Color> solidColor = setting("SolidColor", new Color(new java.awt.Color(255, 50, 50, 19).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 19)).whenTrue(solid);
    Setting<Color> selfSolidColor = setting("SelfSolidColor", new Color(new java.awt.Color(50, 255, 50, 19).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 19)).whenTrue(self).whenTrue(solid);
    Setting<Boolean> lines = setting("Lines", true).des("Lines render");
    Setting<Float> linesWidth = setting("LinesWidth", 1.0f, 1.0f, 5.0f).whenTrue(lines);
    Setting<Color> linesColor = setting("LinesColor", new Color(new java.awt.Color(255, 50, 50, 101).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 101)).whenTrue(solid);
    Setting<Color> selfLinesColor = setting("SelfLinesColor", new Color(new java.awt.Color(50, 255, 50, 101).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 101)).whenTrue(self).whenTrue(lines);

    private final HashMap<BlockPos, Float> toRenderEnemyPos = new HashMap<>();
    private final HashMap<BlockPos, Float> toRenderEnemyPos2 = new HashMap<>();
    private final HashMap<BlockPos, Float> toRenderSelfPos = new HashMap<>();
    private final HashMap<BlockPos, Float> toRenderSelfPos2 = new HashMap<>();
    private final Timer timer = new Timer();

    @Override
    public void onRenderWorld(RenderEvent event) {
        int passedms = (int) timer.hasPassed();
        timer.reset();
        if (self.getValue()) {
            toRenderSelfPos2.clear();
            BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos pos = BlockUtil.extrudeBlock(playerPos, facing);

                if (CrystalUtil.isCityable(pos, facing, checkDiagonalCity.getValue(), oneBlockCrystalMode.getValue())) {
                    toRenderSelfPos2.put(pos, 0.0f);
                    if (fade.getValue()) {
                        if (passedms < 1000) {
                            toRenderSelfPos.putIfAbsent(pos, 0.0f);
                            toRenderSelfPos.put(pos, toRenderSelfPos.get(pos) + (fadeSpeed.getValue() * passedms));

                            if (toRenderSelfPos.get(pos) > 300) {
                                toRenderSelfPos.put(pos, 300.0f);
                            }
                        }
                    }
                    else {
                        toRenderSelfPos.put(pos, 300.0f);
                    }
                }
            }

            for (Map.Entry<BlockPos, Float> entry : new HashMap<>(toRenderSelfPos).entrySet()) {
                if (!toRenderSelfPos2.containsKey(entry.getKey())) {
                    if (fade.getValue()) {
                        if (passedms < 1000) {
                            toRenderSelfPos.put(entry.getKey(), toRenderSelfPos.get(entry.getKey()) - (fadeSpeed.getValue() * passedms));

                            if (toRenderSelfPos.get(entry.getKey()) <= 0) {
                                toRenderSelfPos.remove(entry.getKey());
                                continue;
                            }
                        }
                    }
                    else {
                        toRenderSelfPos.remove(entry.getKey());
                    }
                }

                renderStuff(entry.getKey(), selfSolidColor.getValue(), selfLinesColor.getValue(), fade.getValue() ? entry.getValue() / 300.0f : 1.0f);
            }
        }

        EntityUtil.entitiesListFlag = true;
        toRenderEnemyPos2.clear();
        mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityPlayer)
                .filter(e -> e != mc.player)
                .filter(e -> !ignoreFriends.getValue() || !FriendManager.isFriend(e))
                .filter(e -> EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), e, mc.player) <= range.getValue())
                .forEach(e -> {
                    BlockPos playerPos = new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));

                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                        BlockPos pos = BlockUtil.extrudeBlock(playerPos, facing);

                        if (CrystalUtil.isCityable(pos, facing, checkDiagonalCity.getValue(), oneBlockCrystalMode.getValue())) {
                            toRenderEnemyPos2.put(pos, 0.0f);
                            if (fade.getValue()) {
                                if (passedms < 1000) {
                                    toRenderEnemyPos.putIfAbsent(pos, 0.0f);
                                    toRenderEnemyPos.put(pos, toRenderEnemyPos.get(pos) + (fadeSpeed.getValue() * passedms));

                                    if (toRenderEnemyPos.get(pos) > 300) {
                                        toRenderEnemyPos.put(pos, 300.0f);
                                    }
                                }
                            }
                            else {
                                toRenderEnemyPos.put(pos, 300.0f);
                            }
                        }
                    }
                });
        EntityUtil.entitiesListFlag = false;

        for (Map.Entry<BlockPos, Float> entry : new HashMap<>(toRenderEnemyPos).entrySet()) {
            if (!toRenderEnemyPos2.containsKey(entry.getKey())) {
                if (fade.getValue()) {
                    if (passedms < 1000) {
                        toRenderEnemyPos.put(entry.getKey(), toRenderEnemyPos.get(entry.getKey()) - (fadeSpeed.getValue() * passedms));

                        if (toRenderEnemyPos.get(entry.getKey()) <= 0) {
                            toRenderEnemyPos.remove(entry.getKey());
                            continue;
                        }
                    }
                }
                else {
                    toRenderEnemyPos.remove(entry.getKey());
                }
            }

            renderStuff(entry.getKey(), solidColor.getValue(), linesColor.getValue(), fade.getValue() ? entry.getValue() / 300.0f : 1.0f);
        }
    }

    private void renderStuff(BlockPos pos, Color solidColor, Color linesColor, float alphaFactor) {
        alphaFactor = MathUtilFuckYou.clamp(alphaFactor, 0.0f, 1.0f);
        switch (renderMode.getValue()) {
            case Box: {
                if (solid.getValue()) {
                    SpartanTessellator.drawBlockFullBox(new Vec3d(pos), false, boxHeight.getValue(), new java.awt.Color(solidColor.getColorColor().getRed(), solidColor.getColorColor().getGreen(), solidColor.getColorColor().getBlue(), (int)(solidColor.getAlpha() * alphaFactor)).getRGB());
                }

                if (lines.getValue()) {
                    SpartanTessellator.drawBlockLineBox(new Vec3d(pos), false, boxHeight.getValue(), linesWidth.getValue(), new java.awt.Color(linesColor.getColorColor().getRed(), linesColor.getColorColor().getGreen(), linesColor.getColorColor().getBlue(), (int)(linesColor.getAlpha() * alphaFactor)).getRGB());
                }
                break;
            }

            case Flat: {
                if (solid.getValue()) {
                    SpartanTessellator.drawFlatFullBox(new Vec3d(pos), false, new java.awt.Color(solidColor.getColorColor().getRed(), solidColor.getColorColor().getGreen(), solidColor.getColorColor().getBlue(), (int)(solidColor.getAlpha() * alphaFactor)).getRGB());
                }

                if (lines.getValue()) {
                    SpartanTessellator.drawFlatLineBox(new Vec3d(pos), false, linesWidth.getValue(), new java.awt.Color(linesColor.getColorColor().getRed(), linesColor.getColorColor().getGreen(), linesColor.getColorColor().getBlue(), (int)(linesColor.getAlpha() * alphaFactor)).getRGB());
                }
                break;
            }

            case Pyramid: {
                if (solid.getValue()) {
                    SpartanTessellator.drawPyramidFullBox(new Vec3d(pos), false, boxHeight.getValue(), new java.awt.Color(solidColor.getColorColor().getRed(), solidColor.getColorColor().getGreen(), solidColor.getColorColor().getBlue(), (int)(solidColor.getAlpha() * alphaFactor)).getRGB());
                }

                if (lines.getValue()) {
                    SpartanTessellator.drawPyramidLineBox(new Vec3d(pos), false, boxHeight.getValue(), linesWidth.getValue(), new java.awt.Color(linesColor.getColorColor().getRed(), linesColor.getColorColor().getGreen(), linesColor.getColorColor().getBlue(), (int)(linesColor.getAlpha() * alphaFactor)).getRGB());
                }
                break;
            }
        }
    }

    enum RenderMode {
        Box,
        Flat,
        Pyramid
    }
}
