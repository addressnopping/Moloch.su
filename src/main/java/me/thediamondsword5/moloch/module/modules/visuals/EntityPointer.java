package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.event.events.render.RenderOverlayEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.graphics.RenderHelper;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//TODO: Roll arrow colors between 2 colors,   add other arrow shapes
@Parallel
@ModuleInfo(name = "EntityPointer", category = Category.VISUALS, description = "Draws stuff to point at where entities are")
public class EntityPointer extends Module {

    Setting<Page> page = setting("Page", Page.Tracers);
    Setting<Float> range = setting("Range", 256.0f, 1.0f, 256.0f).des("Distance to start drawing tracers");
    Setting<Boolean> tracers = setting("Tracers", true).des("Draw lines to entities").whenAtMode(page, Page.Tracers);
    Setting<Float> lineWidth = setting("LineWidth", 1.0f, 1.0f, 5.0f).des("Width of tracer lines").whenTrue(tracers).whenAtMode(page, Page.Tracers);
    Setting<Boolean> spine = setting("Spine", true).des("Draw a line going up the entity's bounding box").whenTrue(tracers).whenAtMode(page, Page.Tracers);

    Setting<Boolean> arrows = setting("Arrows", false).des("Draw arrows around crosshairs to point at entities").whenAtMode(page, Page.Arrows);
    Setting<Float> arrowOffset = setting("ArrowOffset", 15.0f, 1.0f, 100.0f).des("Distance from crosshairs that the arrows should render").whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Boolean> offscreenOnly = setting("OffscreenOnly", false).des("Only draw arrows to entities that are offscreen").whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Boolean> offscreenFade = setting("OffscreenFade", false).des("Fade arrows when an entity comes onto screen or goes offscreen").whenTrue(offscreenOnly).whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Float> offscreenFadeFactor = setting("OffscreenFadeFactor", 1.0f, 0.1f, 10.0f).des("Speed of arrows fading").whenTrue(offscreenFade).whenTrue(offscreenOnly).whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Float> arrowWidth = setting("ArrowWidth", 5.0f, 0.0f, 20.0f).des("Width of arrow").whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Float> arrowHeight = setting("ArrowHeight", 5.0f, 0.0f, 20.0f).des("Height of arrow").whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Boolean> arrowLines = setting("ArrowLines", true).des("Draw an outline on arrows").whenTrue(arrows).whenAtMode(page, Page.Arrows);
    Setting<Float> arrowLinesWidth = setting("ArrowLinesWidth", 1.0f, 1.0f, 5.0f).des("Width of arrows outline").whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Arrows);

    Setting<Boolean> players = setting("Players", true).des("Draw stuff to point at players").whenAtMode(page, Page.Entities);
    Setting<Boolean> mobs = setting("Mobs", false).des("Draw stuff to point at mobs").whenAtMode(page, Page.Entities);
    Setting<Boolean> animals = setting("Animals", false).des("Draw stuff to point at animals").whenAtMode(page, Page.Entities);
    Setting<Boolean> items = setting("Items", false).des("Draw stuff to point at dropped items").whenAtMode(page, Page.Entities);
    Setting<Boolean> pearls = setting("Pearls", false).des("Draw stuff to point at pearls").whenAtMode(page, Page.Entities);

    Setting<Boolean> playerDistanceColor = setting("PlayerDistColor", false).des("Change color depending on distance").whenAtMode(page, Page.Colors);
    Setting<Color> playerColor = setting("PlayerColor", new Color(new java.awt.Color(255, 255, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 50, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> playerColorFar = setting("PlayerColorFar", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(playerDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Boolean> playerArrowRainbowWheel = setting("PlayerArrowRainbowWheel", false).des("Colors arrows in a circular rainbow").whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> playerArrowLinesRainbowWheel = setting("PArrowLinesRainbowWheel", false).des("Colors arrows outline in a circular rainbow").whenTrue(arrows).whenTrue(arrowLines).whenAtMode(page, Page.Colors);
    Setting<Float> playerArrowRainbowWheelSpeed = setting("PArrowRainbowWheelSpeed", 0.5f, 0.1f, 3.0f).des("Speed of circular rainbow wave").only(v -> playerArrowRainbowWheel.getValue() || playerArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> playerArrowRainbowWheelSaturation = setting("PArrowRainbowWheelSaturation", 0.75f, 0.0f, 1.0f).des("Saturation of circular rainbow wave").only(v -> playerArrowRainbowWheel.getValue() || playerArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> playerArrowRainbowWheelBrightness = setting("PArrowRainbowWheelBrightness", 0.9f, 0.0f, 1.0f).des("Brightness of circular rainbow wave").only(v -> playerArrowRainbowWheel.getValue() || playerArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Color> playerColorArrowLines = setting("PlayerColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenFalse(playerArrowLinesRainbowWheel).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Color> friendColor = setting("FriendColor", new Color(new java.awt.Color(50, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 255, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> friendColorFar = setting("FriendColorFar", new Color(new java.awt.Color(100, 100, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 255, 175)).whenTrue(playerDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Color> friendColorArrowLines = setting("FriendColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Color> enemyColor = setting("EnemyColor", new Color(new java.awt.Color(255, 0, 0, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 0, 0, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> enemyColorFar = setting("EnemyColorFar", new Color(new java.awt.Color(255, 100, 100, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 175)).whenTrue(playerDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Color> enemyColorArrowLines = setting("EnemyColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> mobDistanceColor = setting("MobDistColor", false).des("Change color depending on distance").whenAtMode(page, Page.Colors);
    Setting<Color> mobColor = setting("MobColor", new Color(new java.awt.Color(255, 170, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 170, 50, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> mobColorFar = setting("MobColorFar", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(mobDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Boolean> mobArrowRainbowWheel = setting("MobArrowRainbowWheel", false).des("Colors arrows in a circular rainbow").whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> mobArrowLinesRainbowWheel = setting("MArrowLinesRainbowWheel", false).des("Colors arrows outline in a circular rainbow").whenTrue(arrows).whenTrue(arrowLines).whenAtMode(page, Page.Colors);
    Setting<Float> mobArrowRainbowWheelSpeed = setting("MArrowRainbowWheelSpeed", 0.5f, 0.1f, 3.0f).des("Speed of circular rainbow wave").only(v -> mobArrowRainbowWheel.getValue() || mobArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> mobArrowRainbowWheelSaturation = setting("MArrowRainbowWheelSaturation", 0.75f, 0.0f, 1.0f).des("Saturation of circular rainbow wave").only(v -> mobArrowRainbowWheel.getValue() || mobArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> mobArrowRainbowWheelBrightness = setting("MArrowRainbowWheelBrightness", 0.9f, 0.0f, 1.0f).des("Brightness of circular rainbow wave").only(v -> mobArrowRainbowWheel.getValue() || mobArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Color> mobColorArrowLines = setting("MobColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenFalse(mobArrowLinesRainbowWheel).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> animalDistanceColor = setting("AnimalDistColor", false).des("Change color depending on distance").whenAtMode(page, Page.Colors);
    Setting<Color> animalColor = setting("AnimalColor", new Color(new java.awt.Color(50, 170, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 170, 255, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> animalColorFar = setting("AnimalColorFar", new Color(new java.awt.Color(50, 50, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 50, 255, 175)).whenTrue(animalDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Boolean> animalArrowRainbowWheel = setting("AnimalArrowRainbowWheel", false).des("Colors arrows in a circular rainbow").whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> animalArrowLinesRainbowWheel = setting("AArrowLinesRainbowWheel", false).des("Colors arrows outline in a circular rainbow").whenTrue(arrows).whenTrue(arrowLines).whenAtMode(page, Page.Colors);
    Setting<Float> animalArrowRainbowWheelSpeed = setting("AArrowRainbowWheelSpeed", 0.5f, 0.1f, 3.0f).des("Speed of circular rainbow wave").only(v -> animalArrowRainbowWheel.getValue() || animalArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> animalArrowRainbowWheelSaturation = setting("AArrowRainbowWheelSaturation", 0.75f, 0.0f, 1.0f).des("Saturation of circular rainbow wave").only(v -> animalArrowRainbowWheel.getValue() || animalArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> animalArrowRainbowWheelBrightness = setting("AArrowRainbowWheelBrightness", 0.9f, 0.0f, 1.0f).des("Brightness of circular rainbow wave").only(v -> animalArrowRainbowWheel.getValue() || animalArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Color> animalColorArrowLines = setting("AnimalColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenFalse(animalArrowLinesRainbowWheel).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> itemDistanceColor = setting("ItemDistColor", false).des("Change color depending on distance").whenAtMode(page, Page.Colors);
    Setting<Color> itemColor = setting("ItemColor", new Color(new java.awt.Color(255, 50, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 255, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> itemColorFar = setting("ItemColorFar", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(itemDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Boolean> itemArrowRainbowWheel = setting("ItemArrowRainbowWheel", false).des("Colors arrows in a circular rainbow").whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> itemArrowLinesRainbowWheel = setting("IArrowLinesRainbowWheel", false).des("Colors arrows outline in a circular rainbow").whenTrue(arrows).whenTrue(arrowLines).whenAtMode(page, Page.Colors);
    Setting<Float> itemArrowRainbowWheelSpeed = setting("IArrowRainbowWheelSpeed", 0.5f, 0.1f, 3.0f).des("Speed of circular rainbow wave").only(v -> itemArrowRainbowWheel.getValue() || itemArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> itemArrowRainbowWheelSaturation = setting("IArrowRainbowWheelSaturation", 0.75f, 0.0f, 1.0f).des("Saturation of circular rainbow wave").only(v -> itemArrowRainbowWheel.getValue() || itemArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> itemArrowRainbowWheelBrightness = setting("IArrowRainbowWheelBrightness", 0.9f, 0.0f, 1.0f).des("Brightness of circular rainbow wave").only(v -> itemArrowRainbowWheel.getValue() || itemArrowLinesRainbowWheel.getValue()).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Color> itemColorArrowLines = setting("ItemColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenFalse(itemArrowLinesRainbowWheel).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Boolean> pearlDistanceColor = setting("PearlDistColor", false).des("Change color depending on distance").whenAtMode(page, Page.Colors);
    Setting<Color> pearlColor = setting("PearlColor", new Color(new java.awt.Color(50, 255, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 175)).whenAtMode(page, Page.Colors);
    Setting<Color> pearlColorFar = setting("PearlColorFar", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(pearlDistanceColor).whenAtMode(page, Page.Colors);
    Setting<Color> pearlColorArrowLines = setting("PearlColorArrowLines", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).whenTrue(arrowLines).whenTrue(arrows).whenAtMode(page, Page.Colors);
    Setting<Float> distanceFactor = setting("DistanceFactor", 0.5f, 0.1f, 1.0f).des("Fraction of range to have entity be considered at the farthest range").only(v -> playerDistanceColor.getValue() || mobDistanceColor.getValue() || animalDistanceColor.getValue() || itemDistanceColor.getValue() || pearlDistanceColor.getValue()).whenAtMode(page, Page.Colors);

    private final HashMap<Entity, Float> arrowFadeMap = new HashMap<>();
    private final Timer arrowTimer = new Timer();

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (tracers.getValue()) {
            GL11.glPushMatrix();
            EntityUtil.entitiesList().stream()
                    .filter(e -> e != mc.renderViewEntity)
                    .filter(e -> !(EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, e) > range.getValue()))
                    .forEach(entity -> {
                        float distanceFactor = 300.0f * MathUtilFuckYou.clamp((float)EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, entity) / (range.getValue() * this.distanceFactor.getValue()), 0.0f, 1.0f);

                        if (players.getValue() && entity instanceof EntityPlayer) {
                            int color;

                            if (FriendManager.isFriend(entity)) {
                                color = playerDistanceColor.getValue() ? ColorUtil.colorShift(friendColor.getValue().getColorColor(), friendColorFar.getValue().getColorColor(), distanceFactor).getRGB() : friendColor.getValue().getColor();
                            } else if (EnemyManager.isEnemy(entity)) {
                                color = playerDistanceColor.getValue() ? ColorUtil.colorShift(enemyColor.getValue().getColorColor(), enemyColorFar.getValue().getColorColor(), distanceFactor).getRGB() : enemyColor.getValue().getColor();
                            } else {
                                color = playerDistanceColor.getValue() ? ColorUtil.colorShift(playerColor.getValue().getColorColor(), playerColorFar.getValue().getColorColor(), distanceFactor).getRGB() : playerColor.getValue().getColor();
                            }

                            SpartanTessellator.drawTracer(entity, lineWidth.getValue(), spine.getValue(), color);
                        }

                        if (mobs.getValue() && EntityUtil.isEntityMob(entity)) {
                            SpartanTessellator.drawTracer(entity, lineWidth.getValue(), spine.getValue(), mobDistanceColor.getValue() ? ColorUtil.colorShift(mobColor.getValue().getColorColor(), mobColorFar.getValue().getColorColor(), distanceFactor).getRGB() : mobColor.getValue().getColor());
                        }

                        if (animals.getValue() && EntityUtil.isEntityAnimal(entity)) {
                            SpartanTessellator.drawTracer(entity, lineWidth.getValue(), spine.getValue(), animalDistanceColor.getValue() ? ColorUtil.colorShift(animalColor.getValue().getColorColor(), animalColorFar.getValue().getColorColor(), distanceFactor).getRGB() : animalColor.getValue().getColor());
                        }

                        if (items.getValue() && entity instanceof EntityItem) {
                            SpartanTessellator.drawTracer(entity, lineWidth.getValue(), spine.getValue(), itemDistanceColor.getValue() ? ColorUtil.colorShift(itemColor.getValue().getColorColor(), itemColorFar.getValue().getColorColor(), distanceFactor).getRGB() : itemColor.getValue().getColor());
                        }

                        if (pearls.getValue() && entity instanceof EntityEnderPearl) {
                            SpartanTessellator.drawTracer(entity, lineWidth.getValue(), spine.getValue(), pearlDistanceColor.getValue() ? ColorUtil.colorShift(pearlColor.getValue().getColorColor(), pearlColorFar.getValue().getColorColor(), distanceFactor).getRGB() : pearlColor.getValue().getColor());
                        }
                    });
            GL11.glPopMatrix();
        }
    }

    @Override
    public void onRender(RenderOverlayEvent event) {
        if (arrows.getValue()) {
            int passedms = (int) arrowTimer.hasPassed();
            arrowTimer.reset();

            List<Entity> entities;

            if (offscreenFade.getValue()) {
                EntityUtil.entitiesListFlag = true;
                for (Entity entity : EntityUtil.entitiesList()) {
                    arrowFadeMap.putIfAbsent(entity, 0.0f);
                }
                EntityUtil.entitiesListFlag = false;
                entities = new ArrayList<>(arrowFadeMap.keySet());
            }
            else {
                entities = EntityUtil.entitiesList();
            }

            RenderUtils2D.prepareGl();
            EntityUtil.entitiesListFlag = true;
            entities.stream()
                    .filter(e -> !e.getName().equals(mc.renderViewEntity.getName()))
                    .filter(e -> !e.getName().equals(mc.player.getName()))
                    .filter(e -> !(EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, e) > range.getValue()))
                    .filter(e -> !offscreenOnly.getValue() || !offscreenFade.getValue() || arrowFadeMap.containsKey(e))
                    .forEach(entity -> {
                        float distanceFactor = 300.0f * MathUtilFuckYou.clamp((float)EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, entity) / (range.getValue() * this.distanceFactor.getValue()), 0.0f, 1.0f);
                        float alphaFactor = 300.0f;

                        if (offscreenOnly.getValue() && offscreenFade.getValue()) {
                            alphaFactor = arrowFadeMap.get(entity);

                            if (passedms < 1000) {
                                if (!EntityUtil.entitiesList().contains(entity) || RenderHelper.isInViewFrustrum(entity)) {
                                    alphaFactor -= (offscreenFadeFactor.getValue() / 10.0f) * passedms;
                                }
                                else {
                                    alphaFactor += (offscreenFadeFactor.getValue() / 10.0f) * passedms;
                                }
                            }
                            alphaFactor = MathUtilFuckYou.clamp(alphaFactor, 0.0f, 300.0f);
                            arrowFadeMap.put(entity, alphaFactor);

                            if (alphaFactor < 0.0f) {
                                arrowFadeMap.remove(entity);
                            }
                        }

                        if (alphaFactor > 0.0f) {
                            float rotation = getYawToEntity(entity) - mc.renderViewEntity.rotationYaw + (mc.gameSettings.thirdPersonView == 2 ? 0.0f : 180.0f);

                            if (players.getValue() && entity instanceof EntityPlayer) {
                                int color;
                                int linesColor;

                                if (FriendManager.isFriend(entity)) {
                                    color = playerDistanceColor.getValue() ? ColorUtil.colorShift(friendColor.getValue().getColorColor(), friendColorFar.getValue().getColorColor(), distanceFactor).getRGB() : friendColor.getValue().getColor();
                                    linesColor = friendColorArrowLines.getValue().getColor();
                                } else if (EnemyManager.isEnemy(entity)) {
                                    color = playerDistanceColor.getValue() ? ColorUtil.colorShift(enemyColor.getValue().getColorColor(), enemyColorFar.getValue().getColorColor(), distanceFactor).getRGB() : enemyColor.getValue().getColor();
                                    linesColor = enemyColorArrowLines.getValue().getColor();
                                } else {
                                    color = playerDistanceColor.getValue() ? ColorUtil.colorShift(playerColor.getValue().getColorColor(), playerColorFar.getValue().getColorColor(), distanceFactor).getRGB() : playerColor.getValue().getColor();
                                    linesColor = playerColorArrowLines.getValue().getColor();

                                    if (playerArrowRainbowWheel.getValue()) {
                                        color = ColorUtil.rolledRainbowCircular((int)rotation, playerArrowRainbowWheelSpeed.getValue() / 10.0f, playerArrowRainbowWheelSaturation.getValue(), playerArrowRainbowWheelBrightness.getValue());
                                    }

                                    if (playerArrowLinesRainbowWheel.getValue()) {
                                        linesColor = ColorUtil.rolledRainbowCircular((int)rotation, playerArrowRainbowWheelSpeed.getValue() / 10.0f, playerArrowRainbowWheelSaturation.getValue(), playerArrowRainbowWheelBrightness.getValue());
                                    }
                                }

                                drawArrow(entity, color, linesColor, alphaFactor / 300.0f);
                            }

                            if (mobs.getValue() && EntityUtil.isEntityMob(entity)) {
                                int color = mobDistanceColor.getValue() ? ColorUtil.colorShift(mobColor.getValue().getColorColor(), mobColorFar.getValue().getColorColor(), distanceFactor).getRGB() : mobColor.getValue().getColor();
                                int linesColor = mobColorArrowLines.getValue().getColor();

                                if (mobArrowRainbowWheel.getValue()) {
                                    color = ColorUtil.rolledRainbowCircular((int)rotation, mobArrowRainbowWheelSpeed.getValue() / 10.0f, mobArrowRainbowWheelSaturation.getValue(), mobArrowRainbowWheelBrightness.getValue());
                                }

                                if (mobArrowLinesRainbowWheel.getValue()) {
                                    linesColor = ColorUtil.rolledRainbowCircular((int)rotation, mobArrowRainbowWheelSpeed.getValue() / 10.0f, mobArrowRainbowWheelSaturation.getValue(), mobArrowRainbowWheelBrightness.getValue());
                                }

                                drawArrow(entity, color, linesColor, alphaFactor / 300.0f);
                            }

                            if (animals.getValue() && EntityUtil.isEntityAnimal(entity)) {
                                int color = animalDistanceColor.getValue() ? ColorUtil.colorShift(animalColor.getValue().getColorColor(), animalColorFar.getValue().getColorColor(), distanceFactor).getRGB() : animalColor.getValue().getColor();
                                int linesColor = animalColorArrowLines.getValue().getColor();

                                if (animalArrowRainbowWheel.getValue()) {
                                    color = ColorUtil.rolledRainbowCircular((int)rotation, animalArrowRainbowWheelSpeed.getValue() / 10.0f, animalArrowRainbowWheelSaturation.getValue(), animalArrowRainbowWheelBrightness.getValue());
                                }

                                if (animalArrowLinesRainbowWheel.getValue()) {
                                    linesColor = ColorUtil.rolledRainbowCircular((int)rotation, animalArrowRainbowWheelSpeed.getValue() / 10.0f, animalArrowRainbowWheelSaturation.getValue(), animalArrowRainbowWheelBrightness.getValue());
                                }

                                drawArrow(entity, color, linesColor, alphaFactor / 300.0f);
                            }

                            if (items.getValue() && entity instanceof EntityItem) {
                                int color = itemDistanceColor.getValue() ? ColorUtil.colorShift(itemColor.getValue().getColorColor(), itemColorFar.getValue().getColorColor(), distanceFactor).getRGB() : itemColor.getValue().getColor();
                                int linesColor = itemColorArrowLines.getValue().getColor();

                                if (itemArrowRainbowWheel.getValue()) {
                                    color = ColorUtil.rolledRainbowCircular((int)rotation, itemArrowRainbowWheelSpeed.getValue() / 10.0f, itemArrowRainbowWheelSaturation.getValue(), itemArrowRainbowWheelBrightness.getValue());
                                }

                                if (itemArrowLinesRainbowWheel.getValue()) {
                                    linesColor = ColorUtil.rolledRainbowCircular((int)rotation, itemArrowRainbowWheelSpeed.getValue() / 10.0f, itemArrowRainbowWheelSaturation.getValue(), itemArrowRainbowWheelBrightness.getValue());
                                }

                                drawArrow(entity, color, linesColor, alphaFactor / 300.0f);
                            }

                            if (pearls.getValue() && entity instanceof EntityEnderPearl) {
                                drawArrow(entity, pearlDistanceColor.getValue() ? ColorUtil.colorShift(pearlColor.getValue().getColorColor(), pearlColorFar.getValue().getColorColor(), distanceFactor).getRGB() : pearlColor.getValue().getColor(), pearlColorArrowLines.getValue().getColor(), alphaFactor / 300.0f);
                            }
                        }
                    });
            RenderUtils2D.releaseGl();
            EntityUtil.entitiesListFlag = false;
        }
    }

    private void drawArrow(Entity entity, int color, int linesColor, float alphaFactor) {
        float rotation = getYawToEntity(entity) - mc.renderViewEntity.rotationYaw + (mc.gameSettings.thirdPersonView == 2 ? 0.0f : 180.0f);
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;

        int la = linesColor >>> 24 & 255;
        int lr = linesColor >>> 16 & 255;
        int lg = linesColor >>> 8 & 255;
        int lb = linesColor & 255;

        GL11.glTranslatef(scaledResolution.getScaledWidth() / 2.0f, scaledResolution.getScaledHeight() / 2.0f, 0.0f);
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef(-scaledResolution.getScaledWidth() / 2.0f, -scaledResolution.getScaledHeight() / 2.0f, 0.0f);

        GL11.glTranslatef(scaledResolution.getScaledWidth() / 2.0f, scaledResolution.getScaledHeight() / 2.0f, 0.0f);
        RenderUtils2D.drawTriangle(0.0f, arrowOffset.getValue() + (arrowHeight.getValue() / 2.0f), arrowWidth.getValue() / 2.0f, arrowOffset.getValue() - (arrowHeight.getValue() / 2.0f), -arrowWidth.getValue() / 2.0f, arrowOffset.getValue() - (arrowHeight.getValue() / 2.0f),
                new java.awt.Color(r, g, b, (int)(a * alphaFactor)).getRGB());
        if (arrowLines.getValue()) {
            RenderUtils2D.drawTriangleOutline(0.0f, arrowOffset.getValue() + (arrowHeight.getValue() / 2.0f), arrowWidth.getValue() / 2.0f, arrowOffset.getValue() - (arrowHeight.getValue() / 2.0f), -arrowWidth.getValue() / 2.0f, arrowOffset.getValue() - (arrowHeight.getValue() / 2.0f),
                    arrowLinesWidth.getValue(), new java.awt.Color(lr, lg, lb, (int)(la * alphaFactor)).getRGB());
        }
        GL11.glTranslatef(-scaledResolution.getScaledWidth() / 2.0f, -scaledResolution.getScaledHeight() / 2.0f, 0.0f);

        GL11.glTranslatef(scaledResolution.getScaledWidth() / 2.0f, scaledResolution.getScaledHeight() / 2.0f, 0.0f);
        GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef(-scaledResolution.getScaledWidth() / 2.0f, -scaledResolution.getScaledHeight() / 2.0f, 0.0f);
    }

    private float getYawToEntity(Entity entity) {
        double x = entity.posX - mc.renderViewEntity.posX;
        double z = entity.posZ - mc.renderViewEntity.posZ;
        return (float)(-(Math.atan2(x, z) * 57.29577951308232));
    }

    enum Page {
        Tracers,
        Arrows,
        Entities,
        Colors
    }
}
