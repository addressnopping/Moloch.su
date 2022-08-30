package net.spartanb312.base.gui.components;

import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.module.modules.client.HUDEditor;
import net.spartanb312.base.utils.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.spartanb312.base.core.setting.settings.EnumSetting;
import net.spartanb312.base.utils.SoundUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class EnumButton extends net.spartanb312.base.gui.Component {

    EnumSetting<?> setting;

    public static EnumButton instance;
    public Enum<?> lastElement;
    boolean flag = false;
    String moduleName;

    public EnumButton(Setting<? extends Enum<?>> setting, int width, int height, Panel father, Module module) {
        this.moduleName = module.name;
        this.width = width;
        this.height = height;
        this.father = father;
        this.setting = (EnumSetting<?>) setting;
        instance = this;
    }


    public static HashMap<String, Integer> storedArrowAnimationLoopsRight = new HashMap<>();
    public static HashMap<String, Integer> storedArrowAnimationLoopsLeft = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuSelectLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuSelectSideRectLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuSelectSideGlowLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuSelectTextLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuExpandRectLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumIconExpandedLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuOpenCloseAlphaLayerLoops = new HashMap<>();
    public static HashMap<String, Integer> storedEnumDropMenuOpenCloseTextAlphaLayerLoops = new HashMap<>();
    public static int staticInt = 0;
    public static float lastSelectedRectStartY = -999;
    public static float lastSelectedRectEndY = -999;
    boolean flag1 = true;
    boolean flag2 = true;
    boolean flag4 = true;
    boolean reverseAlphaLayer2Flag = false;
    boolean reverseAlphaLayerAnimationFlag = false;
    boolean alphaLayerAnimationReverseDoneFlag = false;
    float animatedScale = 0;
    int animateTextAlpha = 0;

    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        Color displayTextColor = new Color(ClickGUI.instance.enumDisplayTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDisplayTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDisplayTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDisplayTextColor.getValue().getAlpha());
        Color nameTextColor = new Color(ClickGUI.instance.enumNameColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumNameColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumNameColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumNameColor.getValue().getAlpha());

        float currentTextWidth = font.getStringWidth(setting.displayValue());
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f), nameTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);


            GL11.glTranslatef(((x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.displayValue(),
                    x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), (int) (y + height / 2 - font.getHeight() / 2f),
                    displayTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef(((x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            if (CustomFont.instance.textShadow.getValue()) {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, nameTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);


                GL11.glTranslatef(((x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.drawShadow(setting.displayValue(),
                        x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), (int) (y + height / 2 - font.getHeight() / 2f) + 3,
                        displayTextColor.getRGB());


                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef(((x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, nameTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);


                GL11.glTranslated(((x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.draw(setting.displayValue(),
                        x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), (int) (y + height / 2 - font.getHeight() / 2f) + 3,
                        displayTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslated(((x + width - 3 - font.getStringWidth(setting.displayValue()) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

            }
        }


        if (ClickGUI.instance.enumDropMenu.getValue()) {

            if (ClickGUI.instance.enumDropMenuIcon.getValue()) {
                Color enumIconColor = new Color(ClickGUI.instance.enumDropMenuIconColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuIconColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuIconColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuIconColor.getValue().getAlpha());

                if (ClickGUI.instance.enumDropMenuIconExpandedChange.getValue()) {
                    Color enumIconExpandedColor = new Color(ClickGUI.instance.enumDropMenuIconExpandedChangedColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuIconExpandedChangedColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuIconExpandedChangedColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuIconExpandedChangedColor.getValue().getAlpha());

                    if (ClickGUI.instance.enumDropMenuIconExpandedChangeAnimation.getValue()) {

                        storedEnumIconExpandedLoops.putIfAbsent(setting.getName(), 0);
                        int animateLoops = storedEnumIconExpandedLoops.get(setting.getName());

                        int red = (int)(MathUtilFuckYou.linearInterp(enumIconColor.getRed(), enumIconExpandedColor.getRed(), animateLoops));
                        int green = (int)(MathUtilFuckYou.linearInterp(enumIconColor.getGreen(), enumIconExpandedColor.getGreen(), animateLoops));
                        int blue = (int)(MathUtilFuckYou.linearInterp(enumIconColor.getBlue(), enumIconExpandedColor.getBlue(), animateLoops));
                        int alpha = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.enumDropMenuIconColor.getValue().getAlpha(), ClickGUI.instance.enumDropMenuIconExpandedChangedColor.getValue().getAlpha(), animateLoops));


                        enumIconColor = new Color(red, green, blue, alpha);

                        if (expanded) {
                            animateLoops += ClickGUI.instance.enumDropMenuIconExpandedChangeAnimationSpeed.getValue() * 10.0f;
                        }
                        else {
                            animateLoops -= ClickGUI.instance.enumDropMenuIconExpandedChangeAnimationSpeed.getValue() * 10.0f;
                        }


                        if (animateLoops >= 300) {
                            animateLoops = 300;
                        }
                        if (animateLoops <= 0) {
                            animateLoops = 0;
                        }

                        storedEnumIconExpandedLoops.put(setting.getName(), animateLoops);
                    }
                    else {
                        if (expanded) {
                            enumIconColor = enumIconExpandedColor;
                        }
                    }

                    if (ClickGUI.instance.enumDropMenuIconExpandedGlow.getValue()) {
                        GlStateManager.disableAlpha();
                        RenderUtils2D.drawCustomCircle(x + width - 3 - (FontManager.getEnumIconWidth() / 2.0f) - ClickGUI.instance.enumDropMenuIconXOffset.getValue(), y + (height / 2.0f), ClickGUI.instance.enumDropMenuIconExpandedGlowSize.getValue(), new Color(enumIconExpandedColor.getRed(), enumIconExpandedColor.getGreen(), enumIconExpandedColor.getBlue(), ClickGUI.instance.enumDropMenuIconExpandedChangeAnimation.getValue() ? (int)((ClickGUI.instance.enumDropMenuIconExpandedGlowAlpha.getValue() / 300.0f) * storedEnumIconExpandedLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuIconExpandedGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                        GlStateManager.enableAlpha();
                    }
                }


                GL11.glTranslatef((x + width - 3 - (FontManager.getEnumIconWidth() / 2.0f) - ClickGUI.instance.enumDropMenuIconXOffset.getValue()) * (1.0f - ClickGUI.instance.enumDropMenuIconScale.getValue()), (y + (height / 2.0f)) * (1.0f - ClickGUI.instance.enumDropMenuIconScale.getValue()), 0.0f);
                GL11.glScalef(ClickGUI.instance.enumDropMenuIconScale.getValue(), ClickGUI.instance.enumDropMenuIconScale.getValue(), 0.0f);

                FontManager.drawEnumIcon(x + width - 3 - FontManager.getEnumIconWidth() - ClickGUI.instance.enumDropMenuIconXOffset.getValue(), (int)(y + (height / 2.0f) - (FontManager.getIconHeight() / 4.0f)), enumIconColor.getRGB());

                GL11.glScalef(1.0f / ClickGUI.instance.enumDropMenuIconScale.getValue(), 1.0f / ClickGUI.instance.enumDropMenuIconScale.getValue(), 0.0f);
                GL11.glTranslatef((x + width - 3 - (FontManager.getEnumIconWidth() / 2.0f) - ClickGUI.instance.enumDropMenuIconXOffset.getValue()) * (1.0f - ClickGUI.instance.enumDropMenuIconScale.getValue()) * -1.0f, (y + (height / 2.0f)) * (1.0f - ClickGUI.instance.enumDropMenuIconScale.getValue()) * -1.0f, 0.0f);
            }

            if (ClickGUI.instance.isDisabled() && HUDEditor.instance.isDisabled()) {
                if (ClickGUI.instance.enumDropMenuExpandAnimate.getValue()) {
                    storedEnumDropMenuOpenCloseAlphaLayerLoops.put(setting.getName(), 0);
                }
                animatedScale = 1;
                expanded = false;
                anyExpanded = false;
            }

            if (expanded) {
                anyExpanded = true;
                if (flag1) {
                    lastElement = setting.getValue();
                    flag1 = false;
                }

                if (ClickGUI.instance.enumDropMenuExpandAnimateScale.getValue() && ClickGUI.instance.enumDropMenuExpandAnimate.getValue()) {
                    animatedScale += reverseAlphaLayerAnimationFlag ? ClickGUI.instance.enumDropMenuExpandAnimateScaleSpeed.getValue() * 10.0f * -1.0f : ClickGUI.instance.enumDropMenuExpandAnimateScaleSpeed.getValue() * 10.0f;

                    if (animatedScale >= 300) {
                        animatedScale = 300;
                    }
                    if (animatedScale <= 1) {
                        animatedScale = 1;
                    }

                    GL11.glTranslatef((x + width + ClickGUI.instance.enumDropMenuXOffset.getValue()) * (1.0f - (animatedScale / 300.0f)), y * (1.0f - (animatedScale / 300.0f)), 0.0f);
                    GL11.glScalef(animatedScale / 300.0f, animatedScale / 300.0f, 0.0f);
                }

                storedEnumDropMenuSelectLoops.putIfAbsent(setting.getName(), 0);
                if (ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() != ClickGUI.EnumDropMenuSelectedRectAnimation.None) {
                    int animateLoops = storedEnumDropMenuSelectLoops.get(setting.getName());

                    animateLoops += ClickGUI.instance.enumDropMenuSelectedRectAnimationSpeed.getValue() * 10;

                    if (animateLoops >= 300) {
                        animateLoops = 300;
                    }
                    storedEnumDropMenuSelectLoops.put(setting.getName(), animateLoops);
                }

                storedEnumDropMenuOpenCloseAlphaLayerLoops.putIfAbsent(setting.getName(), 0);
                if (ClickGUI.instance.enumDropMenuExpandAnimate.getValue()) {
                    int animateLoops = storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName());

                    if (reverseAlphaLayerAnimationFlag) {
                        animateLoops -= ClickGUI.instance.enumDropMenuExpandAnimateSpeed.getValue() * 10;
                    }
                    else {
                        animateLoops += ClickGUI.instance.enumDropMenuExpandAnimateSpeed.getValue() * 10;
                    }


                    if (animateLoops >= 300) {
                        animateLoops = 300;
                    }
                    if (animateLoops <= 4) {
                        animateLoops = 4;
                    }

                    if (ClickGUI.instance.enumDropMenuExpandAnimateScale.getValue()) {
                        if (reverseAlphaLayerAnimationFlag && animateLoops <= 5 && animatedScale <= 1) {
                            alphaLayerAnimationReverseDoneFlag = true;
                        }
                    }
                    else {
                        if (reverseAlphaLayerAnimationFlag && animateLoops <= 5) {
                            alphaLayerAnimationReverseDoneFlag = true;
                        }
                    }

                    storedEnumDropMenuOpenCloseAlphaLayerLoops.put(setting.getName(), animateLoops);
                }

                if (ClickGUI.instance.enumDropMenuExpandAnimate.getValue()) {

                    if (reverseAlphaLayerAnimationFlag) {
                        animateTextAlpha -= ClickGUI.instance.enumDropMenuExpandAnimateSpeed.getValue() * 10;
                    }
                    else {
                        animateTextAlpha += ClickGUI.instance.enumDropMenuExpandAnimateSpeed.getValue() * 10;
                    }

                    if (animateTextAlpha >= 300) {
                        animateTextAlpha = 300;
                    }
                    if (animateTextAlpha <= 0) {
                        animateTextAlpha = 0;
                    }

                    storedEnumDropMenuOpenCloseTextAlphaLayerLoops.put(setting.getName(), animateTextAlpha);
                }

                float selectedRectStartY = 0;
                float selectedRectEndY = 0;
                int counter = 0;
                float heightOffset = 0;
                float startX = x + width + ClickGUI.instance.enumDropMenuXOffset.getValue();
                float endX = x + width + (ClickGUI.instance.enumDropMenuWidthFactor.getValue() * 2) + ClickGUI.instance.enumDropMenuXOffset.getValue() + setting.getLongestElementLength();
                for (Enum<?> element : setting.getValue().getDeclaringClass().getEnumConstants()) {
                    counter += 1;



                    Color rectBGColor = new Color(ClickGUI.instance.enumDropMenuRectBGColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuRectBGColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuRectBGColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuRectBGColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuRectBGColor.getValue().getAlpha());
                    Color rectColor = new Color(ClickGUI.instance.enumDropMenuRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuRectColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuRectColor.getValue().getAlpha());

                    Color outlineColor = new Color(ClickGUI.instance.enumDropMenuOutlineColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuOutlineColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuOutlineColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuOutlineColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuOutlineColor.getValue().getAlpha());


                    float startY = y + heightOffset;
                    float endY = y + height + heightOffset;


                    if (element == setting.getValue()) {
                        selectedRectStartY = startY + 1;
                        selectedRectEndY = (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) ? (endY - 1 - ClickGUI.instance.enumDropMenuRectGap.getValue()) : (endY - ClickGUI.instance.enumDropMenuRectGap.getValue());
                    }
                    //gradient shadow
                    if (ClickGUI.instance.enumDropMenuShadow.getValue() && counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                        RenderUtils2D.drawBetterRoundRectFade(startX, ClickGUI.instance.enumDropMenuExtensions.getValue() ? (y - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue()) : y, endX, ClickGUI.instance.enumDropMenuExtensions.getValue() ? (endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue()) : endY, ClickGUI.instance.enumDropMenuShadowSize.getValue(), 40.0f, false, false, false, new Color(0, 0, 0, ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuShadowAlpha.getValue()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuShadowAlpha.getValue()).getRGB());
                    }

                    //top bottom extensions
                    if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                        if (counter == 1) {
                            RenderUtils2D.drawRect(startX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), endX, startY, rectBGColor.getRGB());
                            RenderUtils2D.drawRect(startX + 1, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue() + 1, endX - 1, startY, rectColor.getRGB());
                        }
                        if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                            RenderUtils2D.drawRect(startX, endY, endX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), rectBGColor.getRGB());
                            RenderUtils2D.drawRect(startX + 1, endY, endX - 1, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue() - 1, rectColor.getRGB());
                        }
                    }

                    //base rects
                    RenderUtils2D.drawRect(startX, startY, endX, endY, rectBGColor.getRGB());

                    //other side glow
                    if (ClickGUI.instance.enumDropMenuOtherSideGlow.getValue() != ClickGUI.EnumDropMenuOtherSideGlowMode.None) {
                        Color otherSideGlowColor = new Color(ClickGUI.instance.enumDropMenuOtherSideGlowColor.getValue().getRed(), ClickGUI.instance.enumDropMenuOtherSideGlowColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuOtherSideGlowColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuOtherSideGlowColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuOtherSideGlowColor.getValue().getAlpha());

                        GlStateManager.disableAlpha();
                        if (ClickGUI.instance.enumDropMenuOtherSideGlow.getValue() == ClickGUI.EnumDropMenuOtherSideGlowMode.Right || ClickGUI.instance.enumDropMenuOtherSideGlow.getValue() == ClickGUI.EnumDropMenuOtherSideGlowMode.Both) {
                            if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                                if (counter == 1) {
                                    RenderUtils2D.drawCustomRect(endX - ClickGUI.instance.enumDropMenuOtherSideGlowWidth.getValue(), startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), endX, startY, otherSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), otherSideGlowColor.getRGB());
                                }
                                if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                    RenderUtils2D.drawCustomRect(endX - ClickGUI.instance.enumDropMenuOtherSideGlowWidth.getValue(), endY, endX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), otherSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), otherSideGlowColor.getRGB());
                                }
                            }
                            RenderUtils2D.drawCustomRect(endX - ClickGUI.instance.enumDropMenuOtherSideGlowWidth.getValue(), startY, endX, endY, otherSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), otherSideGlowColor.getRGB());
                        }
                        if (ClickGUI.instance.enumDropMenuOtherSideGlow.getValue() == ClickGUI.EnumDropMenuOtherSideGlowMode.Left || ClickGUI.instance.enumDropMenuOtherSideGlow.getValue() == ClickGUI.EnumDropMenuOtherSideGlowMode.Both) {
                            if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                                if (counter == 1) {
                                    RenderUtils2D.drawCustomRect(startX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), startX + ClickGUI.instance.enumDropMenuOtherSideGlowWidth.getValue(), startY, new Color(0, 0, 0, 0).getRGB(), otherSideGlowColor.getRGB(), otherSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                                }
                                if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                    RenderUtils2D.drawCustomRect(startX, endY, startX + ClickGUI.instance.enumDropMenuOtherSideGlowWidth.getValue(), endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(),otherSideGlowColor.getRGB(), otherSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                                }
                            }
                            RenderUtils2D.drawCustomRect(startX, startY, startX + ClickGUI.instance.enumDropMenuOtherSideGlowWidth.getValue(), endY, new Color(0, 0, 0, 0).getRGB(), otherSideGlowColor.getRGB(), otherSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                        }
                        GlStateManager.enableAlpha();
                    }

                    //top base rect
                    RenderUtils2D.drawRect(startX + 1, startY + 1, endX - 1, (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) ? (endY - 1 - ClickGUI.instance.enumDropMenuRectGap.getValue()) : (endY - ClickGUI.instance.enumDropMenuRectGap.getValue()), rectColor.getRGB());


                    //side bar
                    if (ClickGUI.instance.enumDropMenuSideBar.getValue()) {
                        if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                            if (counter == 1) {
                                RenderUtils2D.drawCustomLine(startX + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 4.0f), startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), startX + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 4.0f), startY, ClickGUI.instance.enumDropMenuSideBarWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                            }
                            if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                RenderUtils2D.drawCustomLine(startX + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 4.0f), endY, startX + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 4.0f), endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuSideBarWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                            }
                        }
                        RenderUtils2D.drawCustomLine(startX + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 4.0f), startY, startX + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 4.0f), endY, ClickGUI.instance.enumDropMenuSideBarWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                    }

                    //side glow
                    if (ClickGUI.instance.enumDropMenuSideGlow.getValue()) {
                        Color sideGlowColor = new Color(ClickGUI.instance.enumDropMenuSideGlowColor.getValue().getRed(), ClickGUI.instance.enumDropMenuSideGlowColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuSideGlowColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuSideGlowColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuSideGlowColor.getValue().getAlpha());

                        GlStateManager.disableAlpha();
                        if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                            if (counter == 1) {
                                RenderUtils2D.drawCustomRect(startX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), startX + ClickGUI.instance.enumDropMenuSideGlowWidth.getValue(), startY, new Color(0, 0, 0, 0).getRGB(), sideGlowColor.getRGB(), sideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                            if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                RenderUtils2D.drawCustomRect(startX, endY, startX + ClickGUI.instance.enumDropMenuSideGlowWidth.getValue(), endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), sideGlowColor.getRGB(), sideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                        }
                        RenderUtils2D.drawCustomRect(startX, startY, startX + ClickGUI.instance.enumDropMenuSideGlowWidth.getValue(), endY, new Color(0, 0, 0, 0).getRGB(), sideGlowColor.getRGB(), sideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                        GlStateManager.enableAlpha();
                    }

                    //top bottom gradients
                    if (ClickGUI.instance.enumDropMenuTopBottomGradients.getValue()) {
                        Color topBottomGradientColor = new Color(ClickGUI.instance.enumDropMenuTopBottomGradientsColor.getValue().getRed(), ClickGUI.instance.enumDropMenuTopBottomGradientsColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuTopBottomGradientsColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuTopBottomGradientsColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuTopBottomGradientsColor.getValue().getAlpha());

                        GlStateManager.disableAlpha();
                        if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                            if (counter == 1) {
                                RenderUtils2D.drawCustomRect(startX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), endX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue() + ClickGUI.instance.enumDropMenuTopBottomGradientsHeight.getValue(), topBottomGradientColor.getRGB(), topBottomGradientColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                            if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                RenderUtils2D.drawCustomRect(startX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue() - ClickGUI.instance.enumDropMenuTopBottomGradientsHeight.getValue(), endX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), topBottomGradientColor.getRGB(), topBottomGradientColor.getRGB());
                            }
                        }
                        else {
                            if (counter == 1) {
                                RenderUtils2D.drawCustomRect(startX, startY, endX, startY + ClickGUI.instance.enumDropMenuTopBottomGradientsHeight.getValue(), topBottomGradientColor.getRGB(), topBottomGradientColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                            if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                RenderUtils2D.drawCustomRect(startX, endY - ClickGUI.instance.enumDropMenuTopBottomGradientsHeight.getValue(), endX, endY, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), topBottomGradientColor.getRGB(), topBottomGradientColor.getRGB());
                            }
                        }
                        GlStateManager.enableAlpha();
                    }

                    //outline
                    if (ClickGUI.instance.enumDropMenuOutline.getValue()) {
                        if (ClickGUI.instance.enumDropMenuExtensions.getValue()) {
                            if (counter == 1) {
                                RenderUtils2D.drawCustomLine(startX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), endX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                                RenderUtils2D.drawCustomLine(startX, startY, startX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                                RenderUtils2D.drawCustomLine(endX, startY, endX, startY - ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                            }
                            if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                RenderUtils2D.drawCustomLine(startX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), endX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                                RenderUtils2D.drawCustomLine(startX, startY + height, startX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                                RenderUtils2D.drawCustomLine(endX, startY + height, endX, endY + ClickGUI.instance.enumDropMenuExtensionsHeight.getValue(), ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                            }
                        }
                        else {
                            if (counter == 1) {
                                RenderUtils2D.drawCustomLine(startX, startY, endX, startY, ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                            }
                            if (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) {
                                RenderUtils2D.drawCustomLine(startX, endY, endX, endY, ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                            }
                        }
                        RenderUtils2D.drawCustomLine(startX, startY, startX, endY, ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                        RenderUtils2D.drawCustomLine(endX, startY, endX, endY, ClickGUI.instance.enumDropMenuOutlineWidth.getValue(), outlineColor.getRGB(), outlineColor.getRGB());
                    }


                    if ((mouseX >= Math.min(startX, endX) && mouseX <= Math.max(startX, endX) && mouseY >= Math.min(startY, endY - (ClickGUI.instance.enumDropMenuRectGap.getValue() + 1)) && mouseY <= Math.max(startY, endY - (ClickGUI.instance.enumDropMenuRectGap.getValue() + 1))) && Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
                        setting.setByName(element.name());
                    }

                    //detect change select shit
                    if (lastElement != setting.getValue()) {
                        if (!flag) {
                            storedEnumDropMenuSelectLoops.put(setting.getName(), 0);
                            storedEnumDropMenuExpandRectLoops.put(setting.getName() + element.name(), 1);
                            flag = true;
                        }


                        if (element == lastElement) {
                            lastSelectedRectStartY = startY - 1;
                            lastSelectedRectEndY = (counter == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) ? (endY - 1 - ClickGUI.instance.enumDropMenuRectGap.getValue()) : (endY - ClickGUI.instance.enumDropMenuRectGap.getValue());
                            lastElement = setting.getValue();

                            flag = false;
                        }
                        else {
                            lastSelectedRectStartY = selectedRectStartY;
                            lastSelectedRectEndY = selectedRectEndY;
                        }
                    }

                    heightOffset += height;
                }

                //selected rect stuff
                if (ClickGUI.instance.enumDropMenuSelectedRect.getValue()) {
                    Color selectedRectColor = new Color(ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getRed(), ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getAlpha());

                    //slide shit
                    if (ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() == ClickGUI.EnumDropMenuSelectedRectAnimation.Slide) {
                        if (lastSelectedRectStartY == -999 || (lastSelectedRectStartY != selectedRectEndY && flag4)) {
                            lastSelectedRectStartY = selectedRectStartY;
                            flag4 = false;
                        }
                        if (lastSelectedRectEndY == -999 || (lastSelectedRectEndY != selectedRectEndY && flag2)) {
                            lastSelectedRectEndY = selectedRectEndY;
                            flag2 = false;
                        }


                        float tempStartY = (lastSelectedRectStartY + (storedEnumDropMenuSelectLoops.get(setting.getName()) * ((selectedRectStartY - lastSelectedRectStartY) / 300.0f)));
                        float tempEndY = (lastSelectedRectEndY + (storedEnumDropMenuSelectLoops.get(setting.getName()) * ((selectedRectEndY - lastSelectedRectEndY) / 300.0f)));

                        if (ClickGUI.instance.enumDropMenuSelectedRectRounded.getValue()) {
                            RenderUtils2D.drawRoundedRect(startX + (ClickGUI.instance.enumDropMenuSideBar.getValue() ? (1 + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 2.0f) + 0.5f) : (ClickGUI.instance.enumDropMenuOutline.getValue() ? (1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f) : 1)), tempStartY, ClickGUI.instance.enumDropMenuSelectedRectRoundedRadius.getValue(), endX - (ClickGUI.instance.enumDropMenuOutline.getValue() ? 1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f : 1), tempEndY, false, true, true, true, true, selectedRectColor.getRGB());
                        }
                        else {
                            RenderUtils2D.drawRect(startX + (ClickGUI.instance.enumDropMenuSideBar.getValue() ? (1 + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 2.0f) + 0.5f) : (ClickGUI.instance.enumDropMenuOutline.getValue() ? (1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f) : 1)), tempStartY, endX - (ClickGUI.instance.enumDropMenuOutline.getValue() ? 1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f : 1), tempEndY, selectedRectColor.getRGB());
                        }
                    }
                    else {

                        //other animation shit
                        if (ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() != ClickGUI.EnumDropMenuSelectedRectAnimation.Slide && ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() != ClickGUI.EnumDropMenuSelectedRectAnimation.None) {
                            float heightOffset2 = 0;
                            int counter2 = 0;
                            for (Enum<?> element : setting.getValue().getDeclaringClass().getEnumConstants()) {
                                counter2 += 1;
                                float startY = y + heightOffset2;
                                float endY = y + height + heightOffset2;


                                storedEnumDropMenuSelectLoops.putIfAbsent(setting.getName() + element.name(), 0);

                                int animateLoops = storedEnumDropMenuSelectLoops.get(setting.getName() + element.name());

                                animateLoops += element == setting.getValue() ? (ClickGUI.instance.enumDropMenuSelectedRectAnimationSpeed.getValue() * 10) : (ClickGUI.instance.enumDropMenuSelectedRectAnimationSpeed.getValue() * 10 * -1.0f);

                                if (animateLoops >= 300) {
                                    animateLoops = 300;
                                }
                                if (animateLoops <= 0) {
                                    animateLoops = 0;
                                }
                                storedEnumDropMenuSelectLoops.put(setting.getName() + element.name(), animateLoops);

                                if (ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() == ClickGUI.EnumDropMenuSelectedRectAnimation.Alpha || ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() == ClickGUI.EnumDropMenuSelectedRectAnimation.AlpScale) {
                                    selectedRectColor = new Color(selectedRectColor.getRed(), selectedRectColor.getGreen(), selectedRectColor.getBlue(), (int)(((ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuSelectedRectColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuSelectLoops.get(setting.getName() + element.name())));
                                }

                                float animatedSelectedRectStartX = startX + (ClickGUI.instance.enumDropMenuSideBar.getValue() ? (1 + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 2.0f) + 0.5f) : (ClickGUI.instance.enumDropMenuOutline.getValue() ? (1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f) : 1));
                                float animatedSelectedRectEndX = endX - (ClickGUI.instance.enumDropMenuOutline.getValue() ? 1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f : 1);

                                float animatedSelectedRectStartY = startY + 1;
                                float animatedSelectedRectEndY = (counter2 == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) ? (endY - 1 - ClickGUI.instance.enumDropMenuRectGap.getValue()) : (endY - ClickGUI.instance.enumDropMenuRectGap.getValue());

                                if (ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() == ClickGUI.EnumDropMenuSelectedRectAnimation.Scale || ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() == ClickGUI.EnumDropMenuSelectedRectAnimation.AlpScale) {
                                    animatedSelectedRectStartX = animatedSelectedRectStartX + ((endX - startX) / 2.0f) - ((((endX - startX) / 2.0f) / 300.0f) * storedEnumDropMenuSelectLoops.get(setting.getName() + element.name()));
                                    animatedSelectedRectEndX = animatedSelectedRectEndX - ((endX - startX) / 2.0f) + ((((endX - startX) / 2.0f) / 300.0f) * storedEnumDropMenuSelectLoops.get(setting.getName() + element.name()));
                                    animatedSelectedRectStartY = animatedSelectedRectStartY + ((animatedSelectedRectEndY - animatedSelectedRectStartY) / 2.0f) - ((((animatedSelectedRectEndY - animatedSelectedRectStartY) / 2.0f) / 300.0f) * storedEnumDropMenuSelectLoops.get(setting.getName() + element.name()));
                                    animatedSelectedRectEndY = animatedSelectedRectEndY - ((animatedSelectedRectEndY - animatedSelectedRectStartY) / 2.0f) + ((((animatedSelectedRectEndY - animatedSelectedRectStartY) / 2.0f) / 300.0f) * storedEnumDropMenuSelectLoops.get(setting.getName() + element.name()));

                                    if (storedEnumDropMenuSelectLoops.get(setting.getName() + element.name()) == 0) {
                                        animatedSelectedRectStartX = animatedSelectedRectStartX + ((animatedSelectedRectEndX - animatedSelectedRectStartX) / 2.0f);
                                        animatedSelectedRectEndX = animatedSelectedRectEndX - ((animatedSelectedRectEndX - animatedSelectedRectStartX) / 2.0f);
                                        animatedSelectedRectStartY = animatedSelectedRectStartY + ((animatedSelectedRectEndY - animatedSelectedRectStartY) / 2.0f);
                                        animatedSelectedRectEndY = animatedSelectedRectEndY - ((animatedSelectedRectEndY - animatedSelectedRectStartY) / 2.0f);
                                    }
                                }

                                GlStateManager.disableAlpha();
                                if (ClickGUI.instance.enumDropMenuSelectedRectRounded.getValue()) {
                                    RenderUtils2D.drawRoundedRect(animatedSelectedRectStartX, animatedSelectedRectStartY, ClickGUI.instance.enumDropMenuSelectedRectRoundedRadius.getValue(), animatedSelectedRectEndX, animatedSelectedRectEndY, false, true, true, true, true, selectedRectColor.getRGB());
                                }
                                else {
                                    RenderUtils2D.drawRect(animatedSelectedRectStartX, animatedSelectedRectStartY, animatedSelectedRectEndX, animatedSelectedRectEndY, selectedRectColor.getRGB());
                                }
                                GlStateManager.enableAlpha();

                                heightOffset2 += height;
                            }
                        }
                        else if (ClickGUI.instance.enumDropMenuSelectedRectAnimation.getValue() == ClickGUI.EnumDropMenuSelectedRectAnimation.None) {

                            //no animation shit
                            if (ClickGUI.instance.enumDropMenuSelectedRectRounded.getValue()) {
                                RenderUtils2D.drawRoundedRect(startX + (ClickGUI.instance.enumDropMenuSideBar.getValue() ? (1 + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 2.0f) + 0.5f) : (ClickGUI.instance.enumDropMenuOutline.getValue() ? (1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f) : 1)), selectedRectStartY, ClickGUI.instance.enumDropMenuSelectedRectRoundedRadius.getValue(), endX - (ClickGUI.instance.enumDropMenuOutline.getValue() ? 1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f : 1), selectedRectEndY, false, true, true, true, true, selectedRectColor.getRGB());
                            }
                            else {
                                RenderUtils2D.drawRect(startX + (ClickGUI.instance.enumDropMenuSideBar.getValue() ? (1 + (ClickGUI.instance.enumDropMenuSideBarWidth.getValue() / 2.0f) + 0.5f) : (ClickGUI.instance.enumDropMenuOutline.getValue() ? (1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f) : 1)), selectedRectStartY, endX - (ClickGUI.instance.enumDropMenuOutline.getValue() ? 1 + (ClickGUI.instance.enumDropMenuOutlineWidth.getValue() / 2.0f) + 0.5f : 1), selectedRectEndY, selectedRectColor.getRGB());
                            }

                        }

                    }
                }

                //selected side rect
                if (ClickGUI.instance.enumDropMenuSelectedSideRect.getValue()) {
                    Color selectedSideRectColor = new Color(ClickGUI.instance.enumDropMenuSelectedSideRectColor.getValue().getRed(), ClickGUI.instance.enumDropMenuSelectedSideRectColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuSelectedSideRectColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuSelectedSideRectColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuSelectedSideRectColor.getValue().getAlpha());

                    float heightOffset3 = 0;
                    int counter3 = 0;
                    for (Enum<?> element : setting.getValue().getDeclaringClass().getEnumConstants()) {
                        counter3 += 1;
                        float startY = y + heightOffset3;
                        float endY = y + height + heightOffset3;

                        storedEnumDropMenuSelectSideRectLoops.putIfAbsent(setting.getName() + element.name(), 0);

                        int animateLoops = storedEnumDropMenuSelectSideRectLoops.get(setting.getName() + element.name());

                        animateLoops += element == setting.getValue() ? (ClickGUI.instance.enumDropMenuSelectedSideRectAnimationSpeed.getValue() * 10) : (ClickGUI.instance.enumDropMenuSelectedSideRectAnimationSpeed.getValue() * 10 * -1.0f);

                        if (animateLoops >= 300) {
                            animateLoops = 300;
                        }
                        if (animateLoops <= 0) {
                            animateLoops = 0;
                        }
                        storedEnumDropMenuSelectSideRectLoops.put(setting.getName() + element.name(), animateLoops);

                        float selectedSideRectStartX;
                        float selectedSideRectEndX;

                        float selectedSideRectStartY = startY + ((endY - startY) / 2.0f) - (ClickGUI.instance.enumDropMenuSelectedSideRectHeight.getValue()/ 2.0f);
                        float selectedSideRectEndY = endY - ((endY - startY) / 2.0f) + (ClickGUI.instance.enumDropMenuSelectedSideRectHeight.getValue()/ 2.0f);

                        if (ClickGUI.instance.enumDropMenuSelectedSideRectSide.getValue() == ClickGUI.EnumDropMenuSelectedSideRectSide.Right) {
                            selectedSideRectStartX = endX - ClickGUI.instance.enumDropMenuSelectedSideRectWidth.getValue() - ClickGUI.instance.enumDropMenuSelectedSideRectXOffset.getValue();
                            selectedSideRectEndX = endX - ClickGUI.instance.enumDropMenuSelectedSideRectXOffset.getValue();
                        }
                        else {
                            selectedSideRectStartX = startX + ClickGUI.instance.enumDropMenuSelectedSideRectXOffset.getValue();
                            selectedSideRectEndX = startX + ClickGUI.instance.enumDropMenuSelectedSideRectWidth.getValue() + ClickGUI.instance.enumDropMenuSelectedSideRectXOffset.getValue();
                        }

                        if (ClickGUI.instance.enumDropMenuSelectedSideRectAnimation.getValue()) {
                            selectedSideRectStartY = startY + ((endY - startY) / 2.0f) - (((ClickGUI.instance.enumDropMenuSelectedSideRectHeight.getValue()/ 2.0f) / 300.0f) * storedEnumDropMenuSelectSideRectLoops.get(setting.getName() + element.name()));
                            selectedSideRectEndY = endY - ((endY - startY) / 2.0f) + (((ClickGUI.instance.enumDropMenuSelectedSideRectHeight.getValue()/ 2.0f) / 300.0f) * storedEnumDropMenuSelectSideRectLoops.get(setting.getName() + element.name()));
                            if (ClickGUI.instance.enumDropMenuSelectedSideRectSide.getValue() == ClickGUI.EnumDropMenuSelectedSideRectSide.Right) {
                                selectedSideRectStartX = endX - ClickGUI.instance.enumDropMenuSelectedSideRectXOffset.getValue() - ((ClickGUI.instance.enumDropMenuSelectedSideRectWidth.getValue() / 300.0f) * storedEnumDropMenuSelectSideRectLoops.get(setting.getName() + element.name()));
                            }
                            else {
                                selectedSideRectEndX = startX + ClickGUI.instance.enumDropMenuSelectedSideRectXOffset.getValue() + ((ClickGUI.instance.enumDropMenuSelectedSideRectWidth.getValue() / 300.0f) * storedEnumDropMenuSelectSideRectLoops.get(setting.getName() + element.name()));
                            }

                            if (ClickGUI.instance.enumDropMenuSelectedSideRectRounded.getValue()) {
                                if (ClickGUI.instance.enumDropMenuSelectSideRectFull.getValue()) {
                                    RenderUtils2D.drawRoundedRect(selectedSideRectStartX, selectedSideRectStartY, ClickGUI.instance.enumDropMenuSelectedSideRectRoundedRadius.getValue(), selectedSideRectEndX, selectedSideRectEndY, false, true, true, true, true, selectedSideRectColor.getRGB());
                                }
                                else {
                                    RenderUtils2D.drawCustomGradientRoundedRectModuleEnableMode(selectedSideRectStartX, selectedSideRectStartY, selectedSideRectEndX, selectedSideRectEndY, ClickGUI.instance.enumDropMenuSelectedSideRectRoundedRadius.getValue(), ClickGUI.instance.enumDropMenuSelectedSideRectSide.getValue() == ClickGUI.EnumDropMenuSelectedSideRectSide.Right, selectedSideRectColor.getRGB(), selectedSideRectColor.getRGB());
                                }
                            }
                            else {
                                RenderUtils2D.drawRect(selectedSideRectStartX, selectedSideRectStartY, selectedSideRectEndX, selectedSideRectEndY, selectedSideRectColor.getRGB());
                            }

                        }
                        else {
                            if (element == setting.getValue()) {
                                if (ClickGUI.instance.enumDropMenuSelectedSideRectRounded.getValue()) {
                                    if (ClickGUI.instance.enumDropMenuSelectSideRectFull.getValue()) {
                                        RenderUtils2D.drawRoundedRect(selectedSideRectStartX, selectedSideRectStartY, ClickGUI.instance.enumDropMenuSelectedSideRectRoundedRadius.getValue(), selectedSideRectEndX, selectedSideRectEndY, false, true, true, true, true, selectedSideRectColor.getRGB());
                                    }
                                    else {
                                        RenderUtils2D.drawCustomGradientRoundedRectModuleEnableMode(selectedSideRectStartX, selectedSideRectStartY, selectedSideRectEndX, selectedSideRectEndY, ClickGUI.instance.enumDropMenuSelectedSideRectRoundedRadius.getValue(), ClickGUI.instance.enumDropMenuSelectedSideRectSide.getValue() == ClickGUI.EnumDropMenuSelectedSideRectSide.Right, selectedSideRectColor.getRGB(), selectedSideRectColor.getRGB());
                                    }
                                }
                                else {
                                    RenderUtils2D.drawRect(selectedSideRectStartX, selectedSideRectStartY, selectedSideRectEndX, selectedSideRectEndY, selectedSideRectColor.getRGB());
                                }
                            }
                        }

                        heightOffset3 += height;
                    }
                }

                //selected side glow
                if (ClickGUI.instance.enumDropMenuSelectedSideGlow.getValue()) {
                    Color selectedSideSideGlowColor = new Color(ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getRed(), ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getAlpha());

                    if (ClickGUI.instance.enumDropMenuSelectedSideGlowAnimate.getValue()) {
                        float heightOffset4 = 0;
                        int counter4 = 0;
                        for (Enum<?> element : setting.getValue().getDeclaringClass().getEnumConstants()) {
                            counter4 += 1;
                            float startY = y + heightOffset4;
                            float endY = y + height + heightOffset4;

                            storedEnumDropMenuSelectSideGlowLoops.putIfAbsent(setting.getName() + element.name(), 0);

                            int animateLoops = storedEnumDropMenuSelectSideGlowLoops.get(setting.getName() + element.name());

                            animateLoops += element == setting.getValue() ? (ClickGUI.instance.enumDropMenuSelectedSideGlowAnimateSpeed.getValue() * 10) : (ClickGUI.instance.enumDropMenuSelectedSideGlowAnimateSpeed.getValue() * 10 * -1.0f);

                            if (animateLoops >= 300) {
                                animateLoops = 300;
                            }
                            if (animateLoops <= 0) {
                                animateLoops = 0;
                            }
                            storedEnumDropMenuSelectSideGlowLoops.put(setting.getName() + element.name(), animateLoops);

                            selectedSideSideGlowColor = new Color(selectedSideSideGlowColor.getRed(), selectedSideSideGlowColor.getGreen(), selectedSideSideGlowColor.getBlue(), (int)(((ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? (int)(((ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuOpenCloseAlphaLayerLoops.get(setting.getName())) : ClickGUI.instance.enumDropMenuSelectedSideGlowColor.getValue().getAlpha()) / 300.0f) * storedEnumDropMenuSelectSideGlowLoops.get(setting.getName() + element.name())));

                            GlStateManager.disableAlpha();
                            if (ClickGUI.instance.enumDropMenuSelectedSideGlowSide.getValue() == ClickGUI.EnumDropMenuSelectedSideGlowSide.Right) {
                                RenderUtils2D.drawCustomRect(endX - ClickGUI.instance.enumDropMenuSelectedSideGlowWidth.getValue(), startY + 1, endX, (counter4 == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) ? (endY - 1 - ClickGUI.instance.enumDropMenuRectGap.getValue()) : (endY - ClickGUI.instance.enumDropMenuRectGap.getValue()), selectedSideSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), selectedSideSideGlowColor.getRGB());
                            }
                            else {
                                RenderUtils2D.drawCustomRect(startX, startY + 1, startX + ClickGUI.instance.enumDropMenuSelectedSideGlowWidth.getValue(), (counter4 == Arrays.stream(setting.getValue().getDeclaringClass().getEnumConstants()).count()) ? (endY - 1 - ClickGUI.instance.enumDropMenuRectGap.getValue()) : (endY - ClickGUI.instance.enumDropMenuRectGap.getValue()), new Color(0, 0, 0, 0).getRGB(), selectedSideSideGlowColor.getRGB(), selectedSideSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                            GlStateManager.enableAlpha();


                            heightOffset4 += height;
                        }
                    }
                    else {
                        GlStateManager.disableAlpha();
                        if (ClickGUI.instance.enumDropMenuSelectedSideGlowSide.getValue() == ClickGUI.EnumDropMenuSelectedSideGlowSide.Right) {
                            RenderUtils2D.drawCustomRect(endX - ClickGUI.instance.enumDropMenuSelectedSideGlowWidth.getValue(), selectedRectStartY, endX, selectedRectEndY, selectedSideSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), selectedSideSideGlowColor.getRGB());
                        }
                        else {
                            RenderUtils2D.drawCustomRect(startX, selectedRectStartY, startX + ClickGUI.instance.enumDropMenuSelectedSideGlowWidth.getValue(), selectedRectEndY, new Color(0, 0, 0, 0).getRGB(), selectedSideSideGlowColor.getRGB(), selectedSideSideGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                        }
                        GlStateManager.enableAlpha();
                    }
                }


                //selected round glow



                //text stuff
                float heightOffset3 = 0;

                int selectedTextAlphaAnimation = (int)(((ClickGUI.instance.enumDropMenuSelectedTextColor.getValue().getAlpha()) / 300.0f) * animateTextAlpha);
                if (selectedTextAlphaAnimation <= 4) {
                    selectedTextAlphaAnimation = 4;
                }

                int textAlphaAnimation = (int)(((ClickGUI.instance.enumDropMenuTextColor.getValue().getAlpha()) / 300.0f) * animateTextAlpha);
                if (textAlphaAnimation <= 4) {
                    textAlphaAnimation = 4;
                }

                Color selectedTextColor = new Color(ClickGUI.instance.enumDropMenuSelectedTextColor.getValue().getRed(), ClickGUI.instance.enumDropMenuSelectedTextColor.getValue().getGreen(), ClickGUI.instance.enumDropMenuSelectedTextColor.getValue().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? selectedTextAlphaAnimation : ClickGUI.instance.enumDropMenuSelectedTextColor.getValue().getAlpha());

                Color newTextColor = new Color(255, 255, 255, ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? 0 : 255);

                for (Enum<?> element : setting.getValue().getDeclaringClass().getEnumConstants()) {

                    Color textColor = new Color(ClickGUI.instance.enumDropMenuTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? textAlphaAnimation : ClickGUI.instance.enumDropMenuTextColor.getValue().getAlpha());


                    if (ClickGUI.instance.enumDropMenuSelectedTextColorAnimation.getValue() && ClickGUI.instance.enumDropMenuSelectedTextDifColor.getValue()) {
                        storedEnumDropMenuSelectTextLoops.putIfAbsent(setting.getName() + element.name(), 0);
                        int animateLoops = storedEnumDropMenuSelectTextLoops.get(setting.getName() + element.name());

                        int red = (int)(MathUtilFuckYou.linearInterp(textColor.getRed(), selectedTextColor.getRed(), animateLoops));
                        int green = (int)(MathUtilFuckYou.linearInterp(textColor.getGreen(), selectedTextColor.getGreen(), animateLoops));
                        int blue = (int)(MathUtilFuckYou.linearInterp(textColor.getBlue(), selectedTextColor.getBlue(), animateLoops));
                        int alpha = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? textAlphaAnimation : ClickGUI.instance.enumDropMenuTextColor.getValue().getAlpha(), ClickGUI.instance.enumDropMenuExpandAnimate.getValue() ? selectedTextAlphaAnimation : ClickGUI.instance.enumDropMenuSelectedTextColor.getValue().getAlpha(), animateLoops));


                        newTextColor = new Color(red, green, blue, alpha);


                        animateLoops += element == setting.getValue() ? (ClickGUI.instance.enumDropMenuSelectedTextColorAnimationSpeed.getValue() * 10) : (ClickGUI.instance.enumDropMenuSelectedTextColorAnimationSpeed.getValue() * 10 * -1.0f);

                        if (animateLoops >= 300) {
                            animateLoops = 300;
                        }
                        if (animateLoops <= 0) {
                            animateLoops = 0;
                        }
                        storedEnumDropMenuSelectTextLoops.put(setting.getName() + element.name(), animateLoops);
                    }


                    GL11.glTranslatef((x + width + ClickGUI.instance.enumDropMenuXOffset.getValue() + (((ClickGUI.instance.enumDropMenuWidthFactor.getValue() * 2) + setting.getLongestElementLength()) / 2.0f)) * (1.0f - ClickGUI.instance.enumDropMenuTextScale.getValue()),(y + (height / 2.0f) + heightOffset3) * (1.0f - ClickGUI.instance.enumDropMenuTextScale.getValue()), 0.0f);
                    GL11.glScalef(ClickGUI.instance.enumDropMenuTextScale.getValue(), ClickGUI.instance.enumDropMenuTextScale.getValue(), 0.0f);

                    GlStateManager.disableAlpha();
                    if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
                        GL11.glEnable(GL_TEXTURE_2D);
                        mc.fontRenderer.drawString(element.name(), (int)(x + width + ClickGUI.instance.enumDropMenuXOffset.getValue() + (((ClickGUI.instance.enumDropMenuWidthFactor.getValue() * 2) + setting.getLongestElementLength()) / 2.0f) - (FontManager.getWidth(element.name()) / 2.0f)), (int)(y + (height / 2.0f) + heightOffset3), ClickGUI.instance.enumDropMenuSelectedTextDifColor.getValue() ? (ClickGUI.instance.enumDropMenuSelectedTextColorAnimation.getValue() ? (newTextColor.getRGB()) : (element == setting.getValue() ? (selectedTextColor.getRGB()) : textColor.getRGB())) : textColor.getRGB());
                        GL11.glDisable(GL_TEXTURE_2D);
                    }
                    else {
                        fontManager.draw(element.name(), x + width + ClickGUI.instance.enumDropMenuXOffset.getValue() + (((ClickGUI.instance.enumDropMenuWidthFactor.getValue() * 2) + setting.getLongestElementLength()) / 2.0f) - (FontManager.getWidth(element.name()) / 2.0f), y + (height / 2.0f) + heightOffset3, ClickGUI.instance.enumDropMenuSelectedTextDifColor.getValue() ? (ClickGUI.instance.enumDropMenuSelectedTextColorAnimation.getValue() ? (newTextColor.getRGB()) : (element == setting.getValue() ? (selectedTextColor.getRGB()) : textColor.getRGB())) : textColor.getRGB());
                    }
                    GlStateManager.enableAlpha();

                    GL11.glScalef(1.0f / ClickGUI.instance.enumDropMenuTextScale.getValue(), 1.0f / ClickGUI.instance.enumDropMenuTextScale.getValue(), 0.0f);
                    GL11.glTranslatef((x + width + ClickGUI.instance.enumDropMenuXOffset.getValue() + (((ClickGUI.instance.enumDropMenuWidthFactor.getValue() * 2) + setting.getLongestElementLength()) / 2.0f)) * (1.0f - ClickGUI.instance.enumDropMenuTextScale.getValue()) * -1.0f,(y + (height / 2.0f) + heightOffset3) * (1.0f - ClickGUI.instance.enumDropMenuTextScale.getValue()) * -1.0f, 0.0f);

                    heightOffset3 += height;

                }


                //selected expanding rect
                if (ClickGUI.instance.enumDropMenuSelectedRectScaleOut.getValue()) {
                    Color expandedRectColor = new Color(ClickGUI.instance.enumDropMenuSelectedRectScaleOutColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumDropMenuSelectedRectScaleOutColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumDropMenuSelectedRectScaleOutColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumDropMenuSelectedRectScaleOutColor.getValue().getAlpha());

                    float heightOffset5 = 0;
                    for (Enum<?> element : setting.getValue().getDeclaringClass().getEnumConstants()) {
                        float startY = y + heightOffset5;
                        float endY = y + height + heightOffset5;


                        storedEnumDropMenuExpandRectLoops.putIfAbsent(setting.getName() + element.name(), 300);

                        expandedRectColor = new Color(expandedRectColor.getRed(), expandedRectColor.getGreen(), expandedRectColor.getBlue(), (int)((ClickGUI.instance.enumDropMenuSelectedRectScaleOutColor.getValue().getAlpha() / 300.0f) * storedEnumDropMenuExpandRectLoops.get(setting.getName() + element.name()) * -1.0f) + ClickGUI.instance.enumDropMenuSelectedRectScaleOutColor.getValue().getAlpha());

                        int animateLoops = storedEnumDropMenuExpandRectLoops.get(setting.getName() + element.name());

                        animateLoops += ClickGUI.instance.enumDropMenuSelectedRectScaleOutFactor.getValue() * 10;

                        if (animateLoops >= 300) {
                            animateLoops = 300;
                        }

                        storedEnumDropMenuExpandRectLoops.put(setting.getName() + element.name(), animateLoops);

                        GlStateManager.disableAlpha();
                        RenderUtils2D.drawRect(startX + 1 - ((ClickGUI.instance.enumDropMenuSelectedRectScaleMaxScale.getValue() / 300.0f) * storedEnumDropMenuExpandRectLoops.get(setting.getName() + element.name())), startY + 1 - ((ClickGUI.instance.enumDropMenuSelectedRectScaleMaxScale.getValue() / 300.0f) * storedEnumDropMenuExpandRectLoops.get(setting.getName() + element.name())), endX - 1 + ((ClickGUI.instance.enumDropMenuSelectedRectScaleMaxScale.getValue() / 300.0f) * storedEnumDropMenuExpandRectLoops.get(setting.getName() + element.name())), endY + ((ClickGUI.instance.enumDropMenuSelectedRectScaleMaxScale.getValue() / 300.0f) * storedEnumDropMenuExpandRectLoops.get(setting.getName() + element.name())), expandedRectColor.getRGB());
                        GlStateManager.enableAlpha();

                        heightOffset5 += height;
                    }
                }

                if (alphaLayerAnimationReverseDoneFlag) {
                    alphaLayerAnimationReverseDoneFlag = false;
                    expanded = false;
                    anyExpanded = false;
                    reverseAlphaLayerAnimationFlag = false;
                    reverseAlphaLayer2Flag = false;
                }

                if ((!(mouseX >= Math.min(startX, endX) && mouseX <= Math.max(startX, endX) && mouseY >= Math.min(y, y + height + heightOffset) && mouseY <= Math.max(y, y + height + heightOffset)) && !(mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height)) && ((Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) || (Mouse.getEventButton() == 1 && Mouse.isButtonDown(1)))) || (mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height) && (Mouse.getEventButton() == 1 && Mouse.isButtonDown(1)))) {

                    if (ClickGUI.instance.enumDropMenuExpandAnimate.getValue()) {
                        reverseAlphaLayerAnimationFlag = true;
                    }
                    else {
                        expanded = false;
                        anyExpanded = false;
                    }
                }

                if (ClickGUI.instance.enumDropMenuExpandAnimateScale.getValue() && ClickGUI.instance.enumDropMenuExpandAnimate.getValue()) {
                    GL11.glScalef(300.0f / animatedScale, 300.0f / animatedScale, 0.0f);
                    GL11.glTranslatef((x + width + ClickGUI.instance.enumDropMenuXOffset.getValue()) * -1.0f * (1.0f - (animatedScale / 300.0f)), y * -1.0f * (1.0f - (animatedScale / 300.0f)), 0.0f);
                }

            }


        }
        else {

            //arrows
            if (ClickGUI.instance.enumLoopModeArrows.getValue()) {
                Color arrowsColor = new Color(ClickGUI.instance.enumArrowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enumArrowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enumArrowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enumArrowColor.getValue().getAlpha());

                //left arrow
                RenderUtils2D.drawTriangle(x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) - (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f), x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), arrowsColor.getRGB());
                //right arrow
                RenderUtils2D.drawTriangle(x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() + ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f), x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) - (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), arrowsColor.getRGB());

                if (ClickGUI.instance.enumArrowClickAnimationMode.getValue() != ClickGUI.EnumArrowClickAnimationMode.None) {

                    storedArrowAnimationLoopsRight.putIfAbsent(setting.getName(), 0);
                    int animateLoops = storedArrowAnimationLoopsRight.get(setting.getName());

                    if (isHovered(mouseX, mouseY) && Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
                        animateLoops = 0;
                    }

                    animateLoops += ClickGUI.instance.enumArrowClickAnimationFactor.getValue() * 10;

                    if (animateLoops >= 300) {
                        animateLoops = 300;
                    }
                    if (animateLoops <= 0) {
                        animateLoops = 0;
                    }
                    storedArrowAnimationLoopsRight.put(setting.getName(), animateLoops);


                    storedArrowAnimationLoopsLeft.putIfAbsent(setting.getName(), 0);
                    int animateLoops2 = storedArrowAnimationLoopsLeft.get(setting.getName());

                    if (isHovered(mouseX, mouseY) && Mouse.getEventButton() == 1 && Mouse.isButtonDown(1)) {
                        animateLoops2 = 0;
                    }

                    animateLoops2 += ClickGUI.instance.enumArrowClickAnimationFactor.getValue() * 10;

                    if (animateLoops2 >= 300) {
                        animateLoops2 = 300;
                    }
                    if (animateLoops2 <= 0) {
                        animateLoops2 = 0;
                    }
                    storedArrowAnimationLoopsLeft.put(setting.getName(), animateLoops2);

                    Color arrowsColorLeft = new Color(arrowsColor.getRed(), arrowsColor.getGreen(), arrowsColor.getBlue(), ClickGUI.instance.enumArrowClickAnimationMaxAlpha.getValue() - (int)((ClickGUI.instance.enumArrowClickAnimationMaxAlpha.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName())));
                    Color arrowsColorRight = new Color(arrowsColor.getRed(), arrowsColor.getGreen(), arrowsColor.getBlue(), ClickGUI.instance.enumArrowClickAnimationMaxAlpha.getValue() - (int)((ClickGUI.instance.enumArrowClickAnimationMaxAlpha.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName())));


                    if (ClickGUI.instance.enumArrowClickAnimationMode.getValue() == ClickGUI.EnumArrowClickAnimationMode.Scale) {
                        GL11.glTranslatef((x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() + (ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() / 2.0f) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName()))), (y + (height / 2.0f)) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName()))), 0.0f);
                        GL11.glScalef(((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName())), ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName())), 0.0f);
                    }

                    RenderUtils2D.drawTriangle(x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() + ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f), x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) - (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), arrowsColorRight.getRGB());

                    if (ClickGUI.instance.enumArrowClickAnimationMode.getValue() == ClickGUI.EnumArrowClickAnimationMode.Scale) {
                        GL11.glScalef(1.0f / ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName())), 1.0f / ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName())), 0.0f);
                        GL11.glTranslatef((x + width + ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() + (ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() / 2.0f) - ClickGUI.instance.enumLoopModeTextXOffset.getValue()) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName()))) * -1.0f, (y + (height / 2.0f)) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsRight.get(setting.getName()))) * -1.0f, 0.0f);
                    }


                    if (ClickGUI.instance.enumArrowClickAnimationMode.getValue() == ClickGUI.EnumArrowClickAnimationMode.Scale) {
                        GL11.glTranslatef((x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue() - (ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() / 2.0f)) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName()))), (y + (height / 2.0f)) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName()))), 0.0f);
                        GL11.glScalef(((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName())), ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName())), 0.0f);
                    }

                    RenderUtils2D.drawTriangle(x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) - (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f), x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.enumLoopModeArrowsScaleY.getValue() / 2.0f), arrowsColorLeft.getRGB());

                    if (ClickGUI.instance.enumArrowClickAnimationMode.getValue() == ClickGUI.EnumArrowClickAnimationMode.Scale) {
                        GL11.glScalef(1.0f / ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName())), 1.0f / ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName())), 0.0f);
                        GL11.glTranslatef((x + width - 7 - (font.getStringWidth(setting.displayValue()) * CustomFont.instance.componentTextScale.getValue()) - ClickGUI.instance.enumLoopModeArrowsXOffset.getValue() - ClickGUI.instance.enumLoopModeTextXOffset.getValue() - (ClickGUI.instance.enumLoopModeArrowsScaleX.getValue() / 2.0f)) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName()))) * -1.0f, (y + (height / 2.0f)) * (1.0f - ((ClickGUI.instance.enumArrowClickAnimationMaxScale.getValue() / 300.0f) * storedArrowAnimationLoopsLeft.get(setting.getName()))) * -1.0f, 0.0f);
                    }

                }

            }
        }


        GlStateManager.enableAlpha();
    }


    @Override
    public void bottomRender(int mouseX, int mouseY, boolean lastSetting, boolean firstSetting, float partialTicks) {
        GlStateManager.disableAlpha();
        drawSettingRects(lastSetting, false);

        drawExtendedGradient(lastSetting, false);
        drawExtendedLine(lastSetting);

        renderHoverRect(moduleName + setting.getName(), mouseX, mouseY, 2.0f, -1.0f, false);

        GlStateManager.enableAlpha();
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        if (!anyExpanded) {
            return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY) || !setting.isVisible()) return false;
        if (ClickGUI.instance.enumDropMenu.getValue()) {
            if (mouseButton == 0) {
                expanded = !expanded;
            }
        }
        else {
            if (mouseButton == 0) {
                setting.forwardLoop();
                SoundUtil.playButtonClick();
            }
            else if (mouseButton == 1) {
                setting.backwardLoop();
                SoundUtil.playButtonClick();
            }
        }
        return true;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }

    @Override
    public String getDescription() {
        return setting.getDescription();
    }

}
