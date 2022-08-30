package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.RotationUtil;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.SpartanTessellator;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@Parallel
@ModuleInfo(name = "HoveredHighlight", category = Category.VISUALS, description = "Renders something to highlight the block that you are currently looking at")
public class HoveredHighlight extends Module {
    //see MixinRenderGlobal for canceling vanilla block highlight code

    Setting<Boolean> faceRender = setting("FaceRender", true).des("Renders the face of a block hovered instead of the entire block");
    Setting<Boolean> fade = setting("Fade", true).des("Fades in and out block currently hovered over");
    Setting<Float> fadeSpeed = setting("FadeSpeed", 1.0f, 0.1f, 5.0f).des("Speed that the render fades out").whenTrue(fade);
    Setting<Boolean> solid = setting("Solid", true);
    Setting<Color> solidColor = setting("SolidColor", new Color(new java.awt.Color(255, 255, 255, 20).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 20)).whenTrue(solid);
    Setting<Boolean> lines = setting("Lines", true);
    Setting<Float> linesWidth = setting("LinesWidth", 1.0f, 1.0f, 5.0f).whenTrue(lines);
    Setting<Color> linesColor = setting("LinesColor", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(lines);

    private final HashMap<BlockPos, Integer> fadeMap = new HashMap<>();
    private final HashMap<Map.Entry<BlockPos, EnumFacing>, Integer> fadeFaceMap = new HashMap<>();
    private final Timer fadeTimer = new Timer();

    @Override
    public void onRenderWorld(RenderEvent event) {
        int passedms = (int) fadeTimer.hasPassed();
        fadeTimer.reset();

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (fade.getValue()) {
                if (faceRender.getValue()) {
                    Map.Entry<BlockPos, EnumFacing> highlightData = new AbstractMap.SimpleEntry<>(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);

                    fadeFaceMap.putIfAbsent(highlightData, 0);

                    if (passedms < 1000) {
                        fadeFaceMap.put(highlightData, (int)(fadeFaceMap.get(highlightData) + (fadeSpeed.getValue() * passedms)));
                    }
                }
                else {
                    fadeMap.putIfAbsent(mc.objectMouseOver.getBlockPos(), 0);

                    if (passedms < 1000) {
                        fadeMap.put(mc.objectMouseOver.getBlockPos(), (int)(fadeMap.get(mc.objectMouseOver.getBlockPos()) + (fadeSpeed.getValue() * passedms)));
                    }
                }
            }
            else {
                doRender(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, 1.0f);
            }
        }

        if (fade.getValue()) {
            if (faceRender.getValue()) {
                for (Map.Entry<Map.Entry<BlockPos, EnumFacing>, Integer> entry : new HashMap<>(fadeFaceMap).entrySet()) {
                    if (entry.getValue() > 300) {
                        fadeFaceMap.put(entry.getKey(), 300);
                    }

                    if (entry.getValue() < 0) {
                        fadeFaceMap.put(entry.getKey(), 0);
                    }

                    float alphaFactor = entry.getValue() / 300.0f;
                    doRender(entry.getKey().getKey(), entry.getKey().getValue(), MathUtilFuckYou.clamp(alphaFactor, 0.0f, 1.0f));
                }
            }
            else {
                for (Map.Entry<BlockPos, Integer> entry : new HashMap<>(fadeMap).entrySet()) {
                    if (entry.getValue() > 300) {
                        fadeMap.put(entry.getKey(), 300);
                    }

                    if (entry.getValue() < 0) {
                        fadeMap.put(entry.getKey(), 0);
                    }

                    float alphaFactor = entry.getValue() / 300.0f;
                    doRender(entry.getKey(), EnumFacing.UP, MathUtilFuckYou.clamp(alphaFactor, 0.0f, 1.0f));
                }
            }
        }

        if (fade.getValue() && passedms < 1000) {
            if (faceRender.getValue()) {
                for (Map.Entry<Map.Entry<BlockPos, EnumFacing>, Integer> entry : new HashMap<>(fadeFaceMap).entrySet()) {
                    if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || !BlockUtil.isSameBlockPos(entry.getKey().getKey(), mc.objectMouseOver.getBlockPos()) || entry.getKey().getValue() != mc.objectMouseOver.sideHit) {
                        fadeFaceMap.put(entry.getKey(), (int)(fadeFaceMap.get(entry.getKey()) - (fadeSpeed.getValue() * passedms)));
                    }

                    if (entry.getValue() <= 0) {
                        fadeFaceMap.remove(entry.getKey());
                    }
                }
            }
            else {
                for (Map.Entry<BlockPos, Integer> entry : new HashMap<>(fadeMap).entrySet()) {
                    if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || !BlockUtil.isSameBlockPos(entry.getKey(), mc.objectMouseOver.getBlockPos())) {
                        fadeMap.put(entry.getKey(), (int)(fadeMap.get(entry.getKey()) - (fadeSpeed.getValue() * passedms)));
                    }

                    if (entry.getValue() <= 0) {
                        fadeMap.remove(entry.getKey());
                    }
                }
            }
        }
    }

    private void doRender(BlockPos pos, EnumFacing face, float alphaFactor) {
        if (faceRender.getValue()) {
            if (solid.getValue()) {
                SpartanTessellator.drawBlockFaceFilledBB(pos, face, new java.awt.Color(solidColor.getValue().getColorColor().getRed(), solidColor.getValue().getColorColor().getGreen(), solidColor.getValue().getColorColor().getBlue(), (int)(solidColor.getValue().getAlpha() * alphaFactor)).getRGB());
            }

            if (lines.getValue()) {
                SpartanTessellator.drawBlockFaceLinesBB(pos, face, linesWidth.getValue(), new java.awt.Color(linesColor.getValue().getColorColor().getRed(), linesColor.getValue().getColorColor().getGreen(), linesColor.getValue().getColorColor().getBlue(), (int)(linesColor.getValue().getAlpha() * alphaFactor)).getRGB());
            }
        }
        else {
            if (solid.getValue()) {
                SpartanTessellator.drawBlockBBFullBox(pos, 1.0f, new java.awt.Color(solidColor.getValue().getColorColor().getRed(), solidColor.getValue().getColorColor().getGreen(), solidColor.getValue().getColorColor().getBlue(), (int)(solidColor.getValue().getAlpha() * alphaFactor)).getRGB());
            }

            if (lines.getValue()) {
                SpartanTessellator.drawBlockBBLineBox(pos, 1.0f, linesWidth.getValue(), new java.awt.Color(linesColor.getValue().getColorColor().getRed(), linesColor.getValue().getColorColor().getGreen(), linesColor.getValue().getColorColor().getBlue(), (int)(linesColor.getValue().getAlpha() * alphaFactor)).getRGB());
            }
        }
    }
}
