package me.thediamondsword5.moloch.hud.huds;

import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.engine.AsyncRenderer;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.ItemUtils;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@ModuleInfo(name = "ArmorDisplay", category = Category.HUD, description = "Displays armor information on HUD")
public class ArmorDisplay extends HUDModule {

    public static ArmorDisplay INSTANCE;

    Setting<Page> page = setting("Page", Page.ArmorRender);

    Setting<RenderMode> renderMode = setting("RenderMode", RenderMode.Simplified).des("How armor is rendered on HUD").whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> horizontal = setting("Horizontal", true).des("Render items horizontally").whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> shiftInWater = setting("ShiftInWater", true).des("Make display go up slightly to make room for bubbles while underwater").whenTrue(horizontal).whenAtMode(page, Page.ArmorRender);
    Setting<Integer> separationDist = setting("SeparationDist", 20, 0, 50).des("Distance between each armor piece").whenAtMode(page, Page.ArmorRender);
    Setting<Integer> rectsWidth = setting("RectsWidth", 16, 1, 30).des("Width of simplified rect render and damage bar").whenAtMode(page, Page.ArmorRender);
    Setting<Integer> rectHeight = setting("RectHeight", 7, 1, 20).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> roundedRect = setting("RoundedRect", false).des("Rounded rect for non image render mode").only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Float> roundedRectRadius = setting("RoundedRectRadius", 0.6f, 0.0f, 1.0f).des("Radius of rounded rect").whenTrue(roundedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> roundedRectTopRight = setting("RoundedRectTopRight", true).des("Rounded corner for rounded rect top right").whenTrue(roundedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> roundedRectTopLeft = setting("RoundedRectTopLeft", true).des("Rounded corner for rounded rect top left").whenTrue(roundedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> roundedRectDownRight = setting("RoundedRectDownRight", true).des("Rounded corner for rounded rect bottom right").whenTrue(roundedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> roundedRectDownLeft = setting("RoundedRectDownLeft", true).des("Rounded corner for rounded rect bottom left").whenTrue(roundedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Boolean> borderedRect = setting("BorderedRect", true).des("Bordered rect for non image render mode").only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Float> borderedRectOffset = setting("BorderedRectOffset", 2.0f, 0.0f, 5.0f).des("Bordered rect outline offset").whenTrue(borderedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);
    Setting<Float> borderedRectWidth = setting("BorderedRectWidth", 1.2f, 1.0f, 2.0f).des("Bordered rect outline width").whenTrue(borderedRect).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.ArmorRender);

    Setting<Boolean> armorCount = setting("ArmorCount", false).des("Displays how many of a piece of armor you have left in your inventory").whenAtMode(page, Page.Text);
    Setting<Integer> armorCountOffsetX = setting("ArmorCountX", 14, -50, 50).whenTrue(armorCount).whenAtMode(page, Page.Text);
    Setting<Integer> armorCountOffsetY = setting("ArmorCountY", -2, -50, 50).whenTrue(armorCount).whenAtMode(page, Page.Text);
    Setting<Float> armorCountScale = setting("ArmorCountScale", 0.8f, 0.0f, 2.0f).whenTrue(armorCount).whenAtMode(page, Page.Text);
    Setting<Boolean> armorCountShadow = setting("ArmorCountShadow", true).des("Draw string shadow on armor count text").whenTrue(armorCount).whenAtMode(page, Page.Text);
    Setting<Boolean> DMGPercentText = setting("DMGPercentText", false).des("Shows how much durability is left as a number").whenAtMode(page, Page.Text);
    Setting<Boolean> DMGPercentShadow = setting("DMGPercentShadow", true).des("Draw string shadow on durability percentage text").whenTrue(DMGPercentText).whenAtMode(page, Page.Text);
    Setting<Integer> DMGPercentOffsetX = setting("DMGPercentX", 3, -50, 50).whenTrue(DMGPercentText).whenAtMode(page, Page.Text);
    Setting<Integer> DMGPercentOffsetY = setting("DMGPercentY", -7, -50, 50).whenTrue(DMGPercentText).whenAtMode(page, Page.Text);
    Setting<Float> DMGPercentScale = setting("DMGPercentScale", 0.7f, 0.0f, 2.0f).whenTrue(DMGPercentText).whenAtMode(page, Page.Text);

    Setting<Boolean> shadow = setting("Shadow", true).des("Draw gradient shadow behind armor").whenAtMode(page, Page.Shadow);
    Setting<Boolean> shadowIndependent = setting("ShadowIndependent", true).des("Draw different shadow rects for each piece of armor").whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Float> shadowSize = setting("ShadowSize", 0.2f, 0.0f, 1.0f).des("Shadow size").whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Integer> shadowOffset = setting("ShadowOffset", 1, -50, 50).whenFalse(shadowIndependent).whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Integer> shadowOffsetX = setting("ShadowOffsetX", 2, -50, 50).only(v -> shadowIndependent.getValue() || horizontal.getValue()).whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Integer> shadowOffsetY = setting("ShadowOffsetY", 2, -50, 50).only(v -> shadowIndependent.getValue() || !horizontal.getValue()).whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Float> shadowThickness = setting("ShadowThickness", 9.0f, 0.0f, 50.0f).whenFalse(shadowIndependent).whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Float> shadowWidth = setting("ShadowWidth", 1.9f, 0.0f, 50.0f).whenFalse(shadowIndependent).whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Float> shadowScale = setting("ShadowScale", 0.7f, 0.1f, 2.0f).whenTrue(shadowIndependent).whenTrue(shadow).whenAtMode(page, Page.Shadow);
    Setting<Integer> shadowAlpha = setting("ShadowAlpha", 144, 0, 255).des("Shadow alpha").whenTrue(shadow).whenAtMode(page, Page.Shadow);

    Setting<Boolean> damageBar = setting("DamageBar", true).des("Render a bar to show how much durability is left in a piece of armor").whenAtMode(page, Page.DMGBar);
    Setting<Boolean> damageColorShift = setting("DMGColorShift", true).des("Makes the damage color shift as a piece of armor gets mroe damaged").whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Integer> DMGBarOffsetY = setting("DMGBarY", 9, -50, 50).whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Float> DMGBarHeight = setting("DMGBarHeight", 2.9f, 0.1f, 20.0f).whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Boolean> roundedRectDMGBar = setting("RoundedRectDMGBar", false).des("Rounded rect for non image render mode damage bar").whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Float> roundedRectRadiusDMGBar = setting("RRectRadiusDMGBar", 0.5f, 0.0f, 1.0f).des("Radius of rounded rect damage bar").whenTrue(roundedRectDMGBar).whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Boolean> borderedRectDMGBar = setting("BorderedRectDMGBar", true).des("Bordered rect for non image render mode damage bar").whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Float> borderedRectOffsetDMGBar = setting("BRectOffsetDMGBar", 2.0f, 0.0f, 5.0f).des("Bordered rect outline offset damage bar").whenTrue(borderedRectDMGBar).whenTrue(damageBar).whenAtMode(page, Page.DMGBar);
    Setting<Float> borderedRectWidthDMGBar = setting("BRectWidthDMGBar", 1.0f, 1.0f, 2.0f).des("Bordered rect outline width damage bar").whenTrue(borderedRectDMGBar).whenTrue(damageBar).whenAtMode(page, Page.DMGBar);

    Setting<Color> rectColorElytra = setting("RectColorElytra", new Color(new java.awt.Color(150, 100, 150, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 150, 100, 150, 255)).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorElytra = setting("DMGColorElytra", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorElytra2 = setting("DMGColorElytra2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenTrue(damageColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> rectColorDiamond = setting("RectColorDiamond", new Color(new java.awt.Color(53, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 53, 175, 175, 255)).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorDiamond = setting("DMGColorDiamond", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorDiamond2 = setting("DMGColorDiamond2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenTrue(damageColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> rectColorIron = setting("RectColorIron", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorIron = setting("DMGColorIron", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorIron2 = setting("DMGColorIron2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenTrue(damageColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> rectColorGold = setting("RectColorGold", new Color(new java.awt.Color(200, 200, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 50, 255)).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorGold = setting("DMGColorGold", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorGold2 = setting("DMGColorGold2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenTrue(damageColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> rectColorChainmail = setting("RectColorChainmail", new Color(new java.awt.Color(150, 150, 150, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 150, 150, 150, 255)).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorChainmail = setting("DMGColorChainmail", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorChainmail2 = setting("DMGColorChainmail2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenTrue(damageColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> rectColorLeather = setting("RectColorLeather", new Color(new java.awt.Color(110, 75, 0, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 110, 75, 0, 255)).only(v -> renderMode.getValue() != RenderMode.Image).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorLeather = setting("DMGColorLeather", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> DMGColorLeather2 = setting("DMGColorLeather2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> damageBar.getValue() || DMGPercentText.getValue()).whenTrue(damageColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> rectColorDMGBarBG = setting("RectColorDMGBarBG", new Color(new java.awt.Color(20, 20, 20, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 20, 20, 20, 255)).whenTrue(damageBar).whenAtMode(page, Page.Colors);

    public ArmorDisplay() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                int x1 = horizontal.getValue() ? x + (separationDist.getValue() * 3) : x;
                int y1 = horizontal.getValue() ? y - (shiftInWater.getValue() && mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0) : y + (separationDist.getValue() * 3);
                width = horizontal.getValue() ? separationDist.getValue() * 4 : rectsWidth.getValue() + 5;
                height = horizontal.getValue() ? (renderMode.getValue() == RenderMode.Simplified ? rectHeight.getValue() + 5 : 20) : separationDist.getValue() * 4;

                if (shadow.getValue() && !shadowIndependent.getValue()) {
                    int i = 0;
                    for (ItemStack armorItem : mc.player.inventory.armorInventory) {
                        if (armorItem.getItem() != Items.AIR) i++;
                    }

                    if (i >= 1) {
                        drawAsyncFadeRect(horizontal.getValue() ? x + shadowOffsetX.getValue() + shadowWidth.getValue() : x + shadowOffset.getValue(), horizontal.getValue() ? y1 + shadowOffset.getValue() : y1 - height + shadowOffsetY.getValue() + shadowWidth.getValue(), horizontal.getValue() ? x + width + shadowOffsetX.getValue() - shadowWidth.getValue() : x + shadowThickness.getValue() + shadowOffset.getValue(), horizontal.getValue() ? y1 + shadowThickness.getValue() + shadowOffset.getValue() : y1 + shadowOffsetY.getValue() - shadowWidth.getValue(), shadowSize.getValue(), 30.0f, true, new java.awt.Color(0, 0, 0, shadowAlpha.getValue()).getRGB());
                    }
                }

                for (ItemStack armorItem : mc.player.inventory.armorInventory) {
                    if (armorItem.getItem() != Items.AIR) {
                        if (shadow.getValue() && shadowIndependent.getValue()) {
                            drawAsyncFadeRect(x1 + shadowOffsetX.getValue(), y1 + shadowOffsetY.getValue(), x1 + shadowOffsetX.getValue() + (rectsWidth.getValue() * shadowScale.getValue()), y1 + shadowOffsetY.getValue() + (shadowScale.getValue() * (renderMode.getValue() == RenderMode.Simplified ? rectHeight.getValue() : 20.0f)), shadowSize.getValue(), 30.0f, true, new java.awt.Color(0, 0, 0, shadowAlpha.getValue()).getRGB());
                        }

                        switch (renderMode.getValue()) {
                            case Image: {
                                GL11.glEnable(GL_TEXTURE_2D);
                                mc.getRenderItem().renderItemAndEffectIntoGUI(armorItem, x1, y1);
                                GL11.glDisable(GL_TEXTURE_2D);
                                break;
                            }

                            case Simplified: {
                                renderArmorRect(this, x1, y1, armorItem.getItem());
                                break;
                            }
                        }

                        if (damageBar.getValue()) {
                            if (borderedRectDMGBar.getValue()) {
                                if (roundedRectDMGBar.getValue()) {
                                    drawAsyncRoundedRect(x1 + (borderedRectOffsetDMGBar.getValue() / 2.0f), y1 + DMGBarOffsetY.getValue() + (borderedRectOffsetDMGBar.getValue() / 2.0f), roundedRectRadiusDMGBar.getValue(), x1 + rectsWidth.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), y1 + DMGBarOffsetY.getValue() + DMGBarHeight.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), rectColorDMGBarBG.getValue().getColor());
                                }
                                else {
                                    drawAsyncRect(x1 + (borderedRectOffsetDMGBar.getValue() / 2.0f), y1 + DMGBarOffsetY.getValue() + (borderedRectOffsetDMGBar.getValue() / 2.0f), x1 + rectsWidth.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), y1 + DMGBarOffsetY.getValue() + DMGBarHeight.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), rectColorDMGBarBG.getValue().getColor());
                                }
                            }
                            else {
                                if (roundedRectDMGBar.getValue()) {
                                    drawAsyncRoundedRect(x1, y1 + DMGBarOffsetY.getValue(), roundedRectRadiusDMGBar.getValue(), x1 + rectsWidth.getValue(), y1 + DMGBarOffsetY.getValue() + DMGBarHeight.getValue(), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), rectColorDMGBarBG.getValue().getColor());
                                }
                                else {
                                    drawAsyncRect(x1, y1 + DMGBarOffsetY.getValue(), x1 + rectsWidth.getValue(), y1 + DMGBarOffsetY.getValue() + DMGBarHeight.getValue(), rectColorDMGBarBG.getValue().getColor());
                                }
                            }

                            renderDMGBar(this, x1, y1 + DMGBarOffsetY.getValue(), armorItem);
                        }

                        int armorNum = ItemUtils.getItemCount(armorItem.getItem()) + 1;

                        if (armorCount.getValue() && armorNum > 1) {
                            GL11.glTranslatef((x1 + armorCountOffsetX.getValue()) * (1.0f - armorCountScale.getValue()), (y1 + armorCountOffsetY.getValue()) * (1.0f - armorCountScale.getValue()), 0.0f);
                            GL11.glScalef(armorCountScale.getValue(), armorCountScale.getValue(), 1.0f);

                            drawAsyncString(ItemUtils.getItemCount(armorItem.getItem()) + 1 + "", x1 + armorCountOffsetX.getValue(), y1 + armorCountOffsetY.getValue(), new java.awt.Color(255, 255, 255, 255).getRGB(), armorCountShadow.getValue());

                            GL11.glScalef(1.0f / armorCountScale.getValue(), 1.0f / armorCountScale.getValue(), 1.0f);
                            GL11.glTranslatef((x1 + armorCountOffsetX.getValue()) * (1.0f - armorCountScale.getValue()) * -1.0f, (y1 + armorCountOffsetY.getValue()) * (1.0f - armorCountScale.getValue()) * -1.0f, 0.0f);
                        }

                        if (DMGPercentText.getValue()) {
                            GL11.glTranslatef((x1 + DMGPercentOffsetX.getValue()) * (1.0f - DMGPercentScale.getValue()), (y1 + DMGPercentOffsetY.getValue()) * (1.0f - DMGPercentScale.getValue()), 0.0f);
                            GL11.glScalef(DMGPercentScale.getValue(), DMGPercentScale.getValue(), 1.0f);

                            drawAsyncString((int)(getItemDMG(armorItem) * 100) + "", x1 + DMGPercentOffsetX.getValue(), y1 + DMGPercentOffsetY.getValue(), damageColorShift.getValue() ? ColorUtil.colorShift(getDMGColor2(armorItem.getItem()), getDMGColor(armorItem.getItem()), getItemDMG(armorItem) * 300.0f).getRGB() : getDMGColor(armorItem.getItem()).getRGB(), DMGPercentShadow.getValue());

                            GL11.glScalef(1.0f / DMGPercentScale.getValue(), 1.0f / DMGPercentScale.getValue(), 1.0f);
                            GL11.glTranslatef((x1 + DMGPercentOffsetX.getValue()) * (1.0f - DMGPercentScale.getValue()) * -1.0f, (y1 + DMGPercentOffsetY.getValue()) * (1.0f - DMGPercentScale.getValue()) * -1.0f, 0.0f);
                        }
                    }

                    if (horizontal.getValue()) {
                        x1 -= separationDist.getValue();
                    }
                    else {
                        y1 -= separationDist.getValue();
                    }
                }
            }
        };
        INSTANCE = this;
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        /*
        int x = horizontal.getValue() ? this.x + (separationDist.getValue() * 3) : this.x;
        int y = horizontal.getValue() ? this.y - (shiftInWater.getValue() && mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0) : this.y + (separationDist.getValue() * 3);
        width = horizontal.getValue() ? separationDist.getValue() * 4 : rectsWidth.getValue() + 5;
        height = horizontal.getValue() ? (renderMode.getValue() == RenderMode.Simplified ? rectHeight.getValue() + 5 : 20) : separationDist.getValue() * 4;

        if (shadow.getValue() && !shadowIndependent.getValue()) {
            int i = 0;
            for (ItemStack armorItem : mc.player.inventory.armorInventory) {
                if (armorItem.getItem() != Items.AIR) i++;
            }

            if (i >= 1) {
                RenderUtils2D.drawBetterRoundRectFade(horizontal.getValue() ? this.x + shadowOffsetX.getValue() + shadowWidth.getValue() : this.x + shadowOffset.getValue(), horizontal.getValue() ? y + shadowOffset.getValue() : y - height + shadowOffsetY.getValue() + shadowWidth.getValue(), horizontal.getValue() ? this.x + width + shadowOffsetX.getValue() - shadowWidth.getValue() : this.x + shadowThickness.getValue() + shadowOffset.getValue(), horizontal.getValue() ? y + shadowThickness.getValue() + shadowOffset.getValue() : y + shadowOffsetY.getValue() - shadowWidth.getValue(), shadowSize.getValue(), 30.0f, false, true, false, new java.awt.Color(0, 0, 0, shadowAlpha.getValue()).getRGB());
            }
        }

        for (ItemStack armorItem : mc.player.inventory.armorInventory) {
            if (armorItem.getItem() != Items.AIR) {
                if (shadow.getValue() && shadowIndependent.getValue()) {
                    RenderUtils2D.drawBetterRoundRectFade(x + shadowOffsetX.getValue(), y + shadowOffsetY.getValue(), x + shadowOffsetX.getValue() + (rectsWidth.getValue() * shadowScale.getValue()), y + shadowOffsetY.getValue() + (shadowScale.getValue() * (renderMode.getValue() == RenderMode.Simplified ? rectHeight.getValue() : 20.0f)), shadowSize.getValue(), 30.0f, false, true, false, new java.awt.Color(0, 0, 0, shadowAlpha.getValue()).getRGB());
                }

                switch (renderMode.getValue()) {
                    case Image: {
                        GL11.glEnable(GL_TEXTURE_2D);
                        mc.getRenderItem().renderItemAndEffectIntoGUI(armorItem, x, y);
                        GL11.glDisable(GL_TEXTURE_2D);
                        break;
                    }

                    case Simplified: {
                        renderArmorRect(x, y, armorItem.getItem());
                        break;
                    }
                }

                if (damageBar.getValue()) {
                    if (borderedRectDMGBar.getValue()) {
                        if (roundedRectDMGBar.getValue()) {
                            RenderUtils2D.drawRoundedRect(x + (borderedRectOffsetDMGBar.getValue() / 2.0f), y + DMGBarOffsetY.getValue() + (borderedRectOffsetDMGBar.getValue() / 2.0f), roundedRectRadiusDMGBar.getValue(), x + rectsWidth.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), y + DMGBarOffsetY.getValue() + DMGBarHeight.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), false, roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), rectColorDMGBarBG.getValue().getColor());
                        }
                        else {
                            RenderUtils2D.drawRect(x + (borderedRectOffsetDMGBar.getValue() / 2.0f), y + DMGBarOffsetY.getValue() + (borderedRectOffsetDMGBar.getValue() / 2.0f), x + rectsWidth.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), y + DMGBarOffsetY.getValue() + DMGBarHeight.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), rectColorDMGBarBG.getValue().getColor());
                        }
                    }
                    else {
                        if (roundedRectDMGBar.getValue()) {
                            RenderUtils2D.drawRoundedRect(x, y + DMGBarOffsetY.getValue(), roundedRectRadiusDMGBar.getValue(), x + rectsWidth.getValue(), y + DMGBarOffsetY.getValue() + DMGBarHeight.getValue(), false, roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), rectColorDMGBarBG.getValue().getColor());
                        }
                        else {
                            RenderUtils2D.drawRect(x, y + DMGBarOffsetY.getValue(), x + rectsWidth.getValue(), y + DMGBarOffsetY.getValue() + DMGBarHeight.getValue(), rectColorDMGBarBG.getValue().getColor());
                        }
                    }

                    renderDMGBar(x, y + DMGBarOffsetY.getValue(), armorItem);
                }

                int armorNum = ItemUtils.getItemCount(armorItem.getItem()) + 1;

                if (armorCount.getValue() && armorNum > 1) {
                    GL11.glTranslatef((x + armorCountOffsetX.getValue()) * (1.0f - armorCountScale.getValue()), (y + armorCountOffsetY.getValue()) * (1.0f - armorCountScale.getValue()), 0.0f);
                    GL11.glScalef(armorCountScale.getValue(), armorCountScale.getValue(), 1.0f);

                    FontManager.drawHUD(ItemUtils.getItemCount(armorItem.getItem()) + 1 + "", x + armorCountOffsetX.getValue(), y + armorCountOffsetY.getValue(), armorCountShadow.getValue(), new java.awt.Color(255, 255, 255, 255).getRGB());

                    GL11.glScalef(1.0f / armorCountScale.getValue(), 1.0f / armorCountScale.getValue(), 1.0f);
                    GL11.glTranslatef((x + armorCountOffsetX.getValue()) * (1.0f - armorCountScale.getValue()) * -1.0f, (y + armorCountOffsetY.getValue()) * (1.0f - armorCountScale.getValue()) * -1.0f, 0.0f);
                }

                if (DMGPercentText.getValue()) {
                    GL11.glTranslatef((x + DMGPercentOffsetX.getValue()) * (1.0f - DMGPercentScale.getValue()), (y + DMGPercentOffsetY.getValue()) * (1.0f - DMGPercentScale.getValue()), 0.0f);
                    GL11.glScalef(DMGPercentScale.getValue(), DMGPercentScale.getValue(), 1.0f);

                    FontManager.drawHUD((int)(getItemDMG(armorItem) * 100) + "", x + DMGPercentOffsetX.getValue(), y + DMGPercentOffsetY.getValue(), DMGPercentShadow.getValue(), damageColorShift.getValue() ? ColorUtil.colorShift(getDMGColor2(armorItem.getItem()), getDMGColor(armorItem.getItem()), getItemDMG(armorItem) * 300.0f).getRGB() : getDMGColor(armorItem.getItem()).getRGB());

                    GL11.glScalef(1.0f / DMGPercentScale.getValue(), 1.0f / DMGPercentScale.getValue(), 1.0f);
                    GL11.glTranslatef((x + DMGPercentOffsetX.getValue()) * (1.0f - DMGPercentScale.getValue()) * -1.0f, (y + DMGPercentOffsetY.getValue()) * (1.0f - DMGPercentScale.getValue()) * -1.0f, 0.0f);
                }
            }

            if (horizontal.getValue()) {
                x -= separationDist.getValue();
            }
            else {
                y -= separationDist.getValue();
            }
        }
         */
        asyncRenderer.onRender();
    }

    private void renderArmorRect(AsyncRenderer asyncRenderer, int x, int y, Item item) {
        int color = getSimplifiedArmorColor(item, rectColorLeather.getValue(), rectColorChainmail.getValue(), rectColorGold.getValue(), rectColorIron.getValue(), rectColorDiamond.getValue(), rectColorElytra.getDefaultValue()).getRGB();

        if (borderedRect.getValue()) {
            if (roundedRect.getValue()) {
                asyncRenderer.drawAsyncRoundedRectOutline(x, y, roundedRectRadius.getValue(), x + rectsWidth.getValue(), y + rectHeight.getValue(), borderedRectWidth.getValue(), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), color);
                asyncRenderer.drawAsyncRoundedRect(x + (borderedRectOffset.getValue() / 2.0f), y + (borderedRectOffset.getValue() / 2.0f), roundedRectRadius.getValue(), x + rectsWidth.getValue() - (borderedRectOffset.getValue() / 2.0f), y + rectHeight.getValue() - (borderedRectOffset.getValue() / 2.0f), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), color);
            }
            else {
                asyncRenderer.drawAsyncRectOutline(x, y, x + rectsWidth.getValue(), y + rectHeight.getValue(), borderedRectWidth.getValue(), color);
                asyncRenderer.drawAsyncRect(x + (borderedRectOffset.getValue() / 2.0f), y + (borderedRectOffset.getValue() / 2.0f), x + rectsWidth.getValue() - (borderedRectOffset.getValue() / 2.0f), y + rectHeight.getValue() - (borderedRectOffset.getValue() / 2.0f), color);
            }
        }
        else {
            if (roundedRect.getValue()) {
                asyncRenderer.drawAsyncRoundedRect(x, y, roundedRectRadius.getValue(), x + rectsWidth.getValue(), y + rectHeight.getValue(), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), color);
            }
            else {
                asyncRenderer.drawAsyncRect(x, y, x + rectsWidth.getValue(), y + rectHeight.getValue(), color);
            }
        }
    }

    private void renderDMGBar(AsyncRenderer asyncRenderer, int x, int y, ItemStack itemStack) {
        java.awt.Color dmgBarColor = getDMGColor(itemStack.getItem());

        if (damageColorShift.getValue()) {
            dmgBarColor = ColorUtil.colorShift(getDMGColor2(itemStack.getItem()), getDMGColor(itemStack.getItem()), getItemDMG(itemStack) * 300.0f);
        }

        if (borderedRectDMGBar.getValue()) {
            if (roundedRectDMGBar.getValue()) {
                asyncRenderer.drawAsyncRoundedRectOutline(x, y, roundedRectRadiusDMGBar.getValue(), x + rectsWidth.getValue(), y + DMGBarHeight.getValue(), borderedRectWidthDMGBar.getValue(), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), dmgBarColor.getRGB());
                asyncRenderer.drawAsyncRoundedRect(x + (borderedRectOffsetDMGBar.getValue() / 2.0f), y + (borderedRectOffsetDMGBar.getValue() / 2.0f), roundedRectRadiusDMGBar.getValue(), x + (getItemDMG(itemStack) * rectsWidth.getValue()) - (borderedRectOffsetDMGBar.getValue() / 2.0f), y + DMGBarHeight.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), dmgBarColor.getRGB());
            }
            else {
                asyncRenderer.drawAsyncRectOutline(x, y, x + rectsWidth.getValue(), y + DMGBarHeight.getValue(), borderedRectWidthDMGBar.getValue(), dmgBarColor.getRGB());
                asyncRenderer.drawAsyncRect(x + (borderedRectOffsetDMGBar.getValue() / 2.0f), y + (borderedRectOffsetDMGBar.getValue() / 2.0f), x + (getItemDMG(itemStack) * rectsWidth.getValue()) - (borderedRectOffsetDMGBar.getValue() / 2.0f), y + DMGBarHeight.getValue() - (borderedRectOffsetDMGBar.getValue() / 2.0f), dmgBarColor.getRGB());
            }
        }
        else {
            if (roundedRectDMGBar.getValue()) {
                asyncRenderer.drawAsyncRoundedRect(x, y, roundedRectRadiusDMGBar.getValue(), x + (getItemDMG(itemStack) * rectsWidth.getValue()), y + DMGBarHeight.getValue(), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), dmgBarColor.getRGB());
            }
            else {
                asyncRenderer.drawAsyncRect(x, y, x + (getItemDMG(itemStack) * rectsWidth.getValue()), y + DMGBarHeight.getValue(), dmgBarColor.getRGB());
            }
        }
    }

    public float getItemDMG(ItemStack itemStack) {
        return (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float)itemStack.getMaxDamage();
    }

    public java.awt.Color getSimplifiedArmorColor(Item item, Color leatherColor, Color chainMailColor, Color goldColor, Color ironColor, Color diamondColor, Color elytraColor) {
        if (item == Items.LEATHER_BOOTS || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_HELMET) {
            return leatherColor.getColorColor();
        }

        if (item == Items.CHAINMAIL_BOOTS || item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_CHESTPLATE || item == Items.CHAINMAIL_HELMET) {
            return chainMailColor.getColorColor();
        }

        if (item == Items.GOLDEN_BOOTS || item == Items.GOLDEN_LEGGINGS || item == Items.GOLDEN_CHESTPLATE || item == Items.GOLDEN_HELMET) {
            return goldColor.getColorColor();
        }

        if (item == Items.IRON_BOOTS || item == Items.IRON_LEGGINGS || item == Items.IRON_CHESTPLATE || item == Items.IRON_HELMET) {
            return ironColor.getColorColor();
        }

        if (item == Items.DIAMOND_BOOTS || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_CHESTPLATE || item == Items.DIAMOND_HELMET) {
            return diamondColor.getColorColor();
        }

        if (item == Items.ELYTRA) {
            return elytraColor.getColorColor();
        }

        return new java.awt.Color(0, 0, 0, 255);
    }

    private java.awt.Color getDMGColor(Item item) {
        if (item == Items.LEATHER_BOOTS || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_HELMET) {
            return DMGColorLeather.getValue().getColorColor();
        }

        if (item == Items.CHAINMAIL_BOOTS || item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_CHESTPLATE || item == Items.CHAINMAIL_HELMET) {
            return DMGColorChainmail.getValue().getColorColor();
        }

        if (item == Items.GOLDEN_BOOTS || item == Items.GOLDEN_LEGGINGS || item == Items.GOLDEN_CHESTPLATE || item == Items.GOLDEN_HELMET) {
            return DMGColorGold.getValue().getColorColor();
        }

        if (item == Items.IRON_BOOTS || item == Items.IRON_LEGGINGS || item == Items.IRON_CHESTPLATE || item == Items.IRON_HELMET) {
            return DMGColorIron.getValue().getColorColor();
        }

        if (item == Items.DIAMOND_BOOTS || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_CHESTPLATE || item == Items.DIAMOND_HELMET) {
            return DMGColorDiamond.getValue().getColorColor();
        }

        if (item == Items.ELYTRA) {
            return DMGColorElytra.getValue().getColorColor();
        }

        return new java.awt.Color(0);
    }

    private java.awt.Color getDMGColor2(Item item) {
        if (item == Items.LEATHER_BOOTS || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_HELMET) {
            return DMGColorLeather2.getValue().getColorColor();
        }

        if (item == Items.CHAINMAIL_BOOTS || item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_CHESTPLATE || item == Items.CHAINMAIL_HELMET) {
            return DMGColorChainmail2.getValue().getColorColor();
        }

        if (item == Items.GOLDEN_BOOTS || item == Items.GOLDEN_LEGGINGS || item == Items.GOLDEN_CHESTPLATE || item == Items.GOLDEN_HELMET) {
            return DMGColorGold2.getValue().getColorColor();
        }

        if (item == Items.IRON_BOOTS || item == Items.IRON_LEGGINGS || item == Items.IRON_CHESTPLATE || item == Items.IRON_HELMET) {
            return DMGColorIron2.getValue().getColorColor();
        }

        if (item == Items.DIAMOND_BOOTS || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_CHESTPLATE || item == Items.DIAMOND_HELMET) {
            return DMGColorDiamond2.getValue().getColorColor();
        }

        if (item == Items.ELYTRA) {
            return DMGColorElytra2.getValue().getColorColor();
        }

        return new java.awt.Color(0);
    }

    enum Page {
        ArmorRender,
        Text,
        Shadow,
        DMGBar,
        Colors
    }

    public enum RenderMode {
        Image,
        Simplified,
        None
    }
}
