package net.spartanb312.base.gui.components;

import net.spartanb312.base.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.module.modules.client.ClickGUI;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.core.setting.NumberSetting;
import net.spartanb312.base.core.setting.settings.DoubleSetting;
import net.spartanb312.base.core.setting.settings.FloatSetting;
import net.spartanb312.base.core.setting.settings.IntSetting;
import net.spartanb312.base.gui.Component;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class NumberSlider extends Component {

    public boolean sliding = false;
    NumberSetting<?> setting;
    boolean isColorPanel;
    String moduleName;

    public NumberSlider(NumberSetting<?> setting, int width, int height, Panel father, boolean isColorPanel, Module module) {
        this.moduleName = module.name;
        this.width = width;
        this.height = height + (ClickGUI.instance.numSliderThinMode.getValue() ? 4 : 0);
        this.father = father;
        this.setting = setting;
        this.isColorPanel = isColorPanel;
    }


    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        if (!setting.isVisible()) sliding = false;

        String displayValue = setting instanceof IntSetting ? setting.getValue().toString() : String.format("%.1f", setting.getValue().doubleValue());
        double percentBar = (setting.getValue().doubleValue() - setting.getMin().doubleValue()) / (setting.getMax().doubleValue() - setting.getMin().doubleValue());
        double tempWidth = (width - 4) * percentBar;
        double tempWidthThin = (width - 8) * percentBar;

        int numSliderTextColorColorDropMenuAnimateAlpha = (int)((ClickGUI.instance.numSliderTextColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader);
        if (numSliderTextColorColorDropMenuAnimateAlpha <= 4) {
            numSliderTextColorColorDropMenuAnimateAlpha = 4;
        }
        int numSliderDisplayValueTextColorColorDropMenuAnimateAlpha = (int)((ClickGUI.instance.numSliderDisplayValueTextColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader);
        if (numSliderDisplayValueTextColorColorDropMenuAnimateAlpha <= 4) {
            numSliderDisplayValueTextColorColorDropMenuAnimateAlpha = 4;
        }
        Color settingTextColor = isColorPanel ? (new Color(ClickGUI.instance.numSliderTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? numSliderTextColorColorDropMenuAnimateAlpha : ClickGUI.instance.numSliderTextColor.getValue().getAlpha())) : new Color(ClickGUI.instance.numSliderTextColor.getValue().getColor());
        Color displayValueTextColor = isColorPanel ? (new Color(ClickGUI.instance.numSliderDisplayValueTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderDisplayValueTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderDisplayValueTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? numSliderDisplayValueTextColorColorDropMenuAnimateAlpha : ClickGUI.instance.numSliderDisplayValueTextColor.getValue().getAlpha())) : new Color(ClickGUI.instance.numSliderDisplayValueTextColor.getValue().getColor());

        if (ClickGUI.instance.numSliderThinMode.getValue()) {

            int unSlidedColor = isColorPanel ? (new Color(ClickGUI.instance.numSliderThinModeUnSlidedColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderThinModeUnSlidedColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderThinModeUnSlidedColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderThinModeUnSlidedColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderThinModeUnSlidedColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderThinModeUnSlidedColor.getValue().getColor();
            if (ClickGUI.instance.numSliderThinModeRounded.getValue()) {
                RenderUtils2D.drawRoundedRect(x + 5, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), ClickGUI.instance.numSliderThinModeRoundedRadius.getValue(), x + width - 4, y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), false, true, true, true, true, unSlidedColor);
            }
            else {
                RenderUtils2D.drawRect(x + 5, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), x + width - 4, y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), unSlidedColor);
            }

            if (ClickGUI.instance.numSliderGradient.getValue()) {
                int slidedColorRight = isColorPanel ? (new Color(ClickGUI.instance.numSliderRightColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderRightColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderRightColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderRightColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderRightColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderRightColor.getValue().getColor();
                int slidedColorLeft = isColorPanel ? (new Color(ClickGUI.instance.numSliderLeftColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderLeftColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderLeftColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderLeftColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderLeftColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderLeftColor.getValue().getColor();

                if (ClickGUI.instance.numSliderThinModeRounded.getValue()) {
                    RenderUtils2D.drawCustomRoundedRect(x + 5, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), ClickGUI.instance.numSliderThinModeRoundedRadius.getValue(), x + 5 + (int)tempWidthThin, y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), true, true, true, true, false, true, false, slidedColorRight, slidedColorLeft, slidedColorRight, slidedColorLeft, slidedColorLeft, slidedColorLeft, slidedColorLeft, slidedColorLeft, slidedColorRight, slidedColorLeft, slidedColorLeft, slidedColorRight, slidedColorRight, slidedColorRight, slidedColorRight, slidedColorRight);
                }
                else {
                    RenderUtils2D.drawCustomRect(x + 5, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), x + 5 + (int)tempWidthThin, y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), slidedColorRight, slidedColorLeft, slidedColorLeft, slidedColorRight);
                }
            }
            else {
                if (ClickGUI.instance.numSliderThinModeRounded.getValue()) {
                    RenderUtils2D.drawRoundedRect(x + 5, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), ClickGUI.instance.numSliderThinModeRoundedRadius.getValue(), x + 5 + (int)tempWidthThin, y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), false, true, true, true, true, isColorPanel ? (new Color(ClickGUI.instance.numSliderColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderColor.getValue().getAlpha() / 300.0f)) : ClickGUI.instance.numSliderColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderColor.getValue().getColor());
                }
                else {
                    RenderUtils2D.drawRect(x + 5, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), x + 5 + (int)tempWidthThin, y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), isColorPanel ? (new Color(ClickGUI.instance.numSliderColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderColor.getValue().getAlpha() / 300.0f)) : ClickGUI.instance.numSliderColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderColor.getValue().getColor());
                }
            }

            if (ClickGUI.instance.numSliderThinModeSliderButton.getValue()) {
                int buttonShadowAlpha = isColorPanel ? (ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue()) : ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue();

                if (ClickGUI.instance.numSliderThinModeSliderButtonShadow.getValue()) {
                    if (ClickGUI.instance.numSliderThinModeSliderButtonShadowAlphaFadeOut.getValue() && (percentBar < ClickGUI.instance.numSliderThinModeSliderButtonShadowAlphaFadeOutThreshold.getValue())) {
                        buttonShadowAlpha = (int)(ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue() * (percentBar / ClickGUI.instance.numSliderThinModeSliderButtonShadowAlphaFadeOutThreshold.getValue()));

                        if (buttonShadowAlpha < 0) buttonShadowAlpha = 0;
                        if (buttonShadowAlpha > 255) buttonShadowAlpha = 255;

                        int buttonHorizontalShadowAlpha = ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue() - buttonShadowAlpha;
                        int buttonHorizontalShadowColor = new Color(0, 0, 0, buttonHorizontalShadowAlpha).getRGB();
                        GlStateManager.disableAlpha();
                        RenderUtils2D.drawCustomRect(x + 5 + (int)tempWidthThin + (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f) - 2, y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), x + 5 + (int)tempWidthThin + (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f) - 2 + (ClickGUI.instance.numSliderThinModeSliderButtonShadowSize.getValue() * 35.0f), y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), new Color(0, 0, 0, 0).getRGB(), buttonHorizontalShadowColor, buttonHorizontalShadowColor, new Color(0, 0, 0, 0).getRGB());
                        GlStateManager.enableAlpha();
                    }
                    else if (ClickGUI.instance.numSliderThinModeSliderButtonShadowAlphaFadeOut.getValue() && ((1.0d - percentBar) < ClickGUI.instance.numSliderThinModeSliderButtonShadowAlphaFadeOutThreshold.getValue())) {
                        buttonShadowAlpha = (int)(ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue() * ((1.0d - percentBar) / ClickGUI.instance.numSliderThinModeSliderButtonShadowAlphaFadeOutThreshold.getValue()));

                        if (buttonShadowAlpha < 0) buttonShadowAlpha = 0;
                        if (buttonShadowAlpha > 255) buttonShadowAlpha = 255;

                        int buttonHorizontalShadowAlpha = ClickGUI.instance.numSliderThinModeSliderButtonShadowAlpha.getValue() - buttonShadowAlpha;
                        int buttonHorizontalShadowColor = new Color(0, 0, 0, buttonHorizontalShadowAlpha).getRGB();
                        GlStateManager.disableAlpha();
                        RenderUtils2D.drawCustomRect(x + 5 + (int)tempWidthThin + 2 - (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f) - (ClickGUI.instance.numSliderThinModeSliderButtonShadowSize.getValue() * 35.0f), y + ((height / 14.0f) * 11.0f) - ClickGUI.instance.numSliderThinModeBarThickness.getValue(), x + 5 + (int)tempWidthThin + 2 - (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) + ClickGUI.instance.numSliderThinModeBarThickness.getValue(), buttonHorizontalShadowColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), buttonHorizontalShadowColor);
                        GlStateManager.enableAlpha();
                    }
                    RenderUtils2D.drawBetterRoundRectFade(x + 5 + (int)tempWidthThin - (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) - (ClickGUI.instance.numSliderThinModeSliderButtonHeight.getValue() / 2.0f), x + 5 + (int)tempWidthThin + (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) + (ClickGUI.instance.numSliderThinModeSliderButtonHeight.getValue() / 2.0f), ClickGUI.instance.numSliderThinModeSliderButtonShadowSize.getValue(), 20.0f, false, true, false, new Color(0, 0, 0, buttonShadowAlpha).getRGB());
                }

                int numSliderThinModeSliderButtonColor = isColorPanel ? (new Color(ClickGUI.instance.numSliderThinModeSliderButtonColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderThinModeSliderButtonColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderThinModeSliderButtonColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderThinModeSliderButtonColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderThinModeSliderButtonColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderThinModeSliderButtonColor.getValue().getColor();
                if (ClickGUI.instance.numSliderThinModeSliderButtonRounded.getValue()) {
                    RenderUtils2D.drawRoundedRect(x + 5 + (int)tempWidthThin - (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) - (ClickGUI.instance.numSliderThinModeSliderButtonHeight.getValue() / 2.0f), ClickGUI.instance.numSliderThinModeSliderButtonRoundedRadius.getValue(), x + 5 + (int)tempWidthThin + (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) + (ClickGUI.instance.numSliderThinModeSliderButtonHeight.getValue() / 2.0f), false, true, true, true, true, numSliderThinModeSliderButtonColor);
                }
                else {
                    RenderUtils2D.drawRect(x + 5 + (int)tempWidthThin - (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) - (ClickGUI.instance.numSliderThinModeSliderButtonHeight.getValue() / 2.0f), x + 5 + (int)tempWidthThin + (ClickGUI.instance.numSliderThinModeSliderButtonWidth.getValue() / 2.0f), y + ((height / 14.0f) * 11.0f) + (ClickGUI.instance.numSliderThinModeSliderButtonHeight.getValue() / 2.0f), numSliderThinModeSliderButtonColor);
                }
            }

        }
        else {

            if (ClickGUI.instance.numSliderGradient.getValue()) {
                int slidedColorRight = isColorPanel ? (new Color(ClickGUI.instance.numSliderRightColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderRightColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderRightColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderRightColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderRightColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderRightColor.getValue().getColor();
                int slidedColorLeft = isColorPanel ? (new Color(ClickGUI.instance.numSliderLeftColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderLeftColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderLeftColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderLeftColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.numSliderLeftColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderLeftColor.getValue().getColor();

                RenderUtils2D.drawCustomRect(x + 3, y + 2, x + 3 + (int)tempWidth, y + height, slidedColorRight, slidedColorLeft, slidedColorLeft, slidedColorRight);
            }
            else {
                RenderUtils2D.drawRect(x + 3, y + 1, x + 3 + (int) tempWidth, y + height, isColorPanel ? (new Color(ClickGUI.instance.numSliderColor.getValue().getColorColor().getRed(), ClickGUI.instance.numSliderColor.getValue().getColorColor().getGreen(), ClickGUI.instance.numSliderColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.numSliderColor.getValue().getAlpha() / 300.0f)) : ClickGUI.instance.numSliderColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.numSliderColor.getValue().getColor());
            }


        }


        float currentTextWidth = font.getStringWidth(String.valueOf(displayValue));

        if (this.sliding) {
            double diff = setting.getMax().doubleValue() - setting.getMin().doubleValue();
            double val = setting.getMin().doubleValue() + (MathHelper.clamp((mouseX - (double) (x + 3)) / (double) (width - 4), 0, 1)) * diff;
            if (setting instanceof DoubleSetting) {
                ((DoubleSetting) setting).setValue(val);
            } else if (setting instanceof FloatSetting) {
                ((FloatSetting) setting).setValue((float) val);
            } else if (setting instanceof IntSetting) {
                ((IntSetting) setting).setValue((int) val);
            }
        }
        if (ClickGUI.instance.numSliderThinMode.getValue()) {
            float settingNameX = (x + 5);
            float textY = y + (font.getHeight() / 2.0f) + ClickGUI.instance.numSliderThinModeTextOffset.getValue();

            if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
                float displayValueX = (x + width - 3 - mc.fontRenderer.getStringWidth(String.valueOf(displayValue)));


                GL11.glEnable(GL_TEXTURE_2D);
                GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(setting.getName(), settingNameX, textY, settingTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);



                GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(String.valueOf(displayValue), displayValueX, textY, displayValueTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
                GL11.glDisable(GL_TEXTURE_2D);
            }
            else {
                float displayValueX = (x + width - 3 - font.getStringWidth(String.valueOf(displayValue)));
                if (CustomFont.instance.textShadow.getValue()) {

                    GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.drawShadow(setting.getName(), settingNameX, textY, settingTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);


                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.drawShadow(String.valueOf(displayValue), displayValueX, textY, displayValueTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

                }
                else {
                    GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.draw(setting.getName(), settingNameX, textY, settingTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);



                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.draw(String.valueOf(displayValue), displayValueX, textY, displayValueTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

                }
            }

        }
        else {

            if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
                GL11.glEnable(GL_TEXTURE_2D);
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(setting.getName(), x + 5, (int)(y + height / 2 - font.getHeight() / 2f), settingTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);



                float displayValueX = ClickGUI.instance.numSliderValueLock.getValue() ? (x + width - 3 - mc.fontRenderer.getStringWidth(String.valueOf(displayValue))) : (x + 7 + (mc.fontRenderer.getStringWidth(setting.getName()) * CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(String.valueOf(displayValue), displayValueX, (int) (y + height / 2 - font.getHeight() / 2f), displayValueTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
                GL11.glDisable(GL_TEXTURE_2D);
            }
            else {
                float displayValueX = ClickGUI.instance.numSliderValueLock.getValue() ? (x + width - 3 - font.getStringWidth(String.valueOf(displayValue))) : (x + 7 + (font.getStringWidth(setting.getName()) * CustomFont.instance.componentTextScale.getValue()));
                if (CustomFont.instance.textShadow.getValue()) {
                    GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, settingTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);



                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.drawShadow(String.valueOf(displayValue), displayValueX, (int) (y + height / 2 - font.getHeight() / 2f) + 3, displayValueTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

                }
                else {
                    GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, settingTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);



                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.draw(String.valueOf(displayValue), displayValueX, (int) (y + height / 2 - font.getHeight() / 2f) + 3, displayValueTextColor.getRGB());

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef(((displayValueX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

                }
            }

        }

        GlStateManager.enableAlpha();
    }

    @Override
    public void bottomRender(int mouseX, int mouseY, boolean lastSetting, boolean firstSetting, float partialTicks) {
        GlStateManager.disableAlpha();
        drawSettingRects(lastSetting, isColorPanel);

        drawExtendedGradient(lastSetting, isColorPanel);
        if (isColorPanel) {
            if (ClickGUI.instance.colorDropMenuSideBar.getValue()) {
                drawExtendedLineColor(lastSetting);
            }
            if (ClickGUI.instance.colorDropMenuOutline.getValue()) {
                drawColorMenuOutline(lastSetting, firstSetting, ClickGUI.instance.colorDropMenuExtensions.getValue());
            }
        }
        else {
            drawExtendedLine(lastSetting);
        }

        renderHoverRect(moduleName + setting.getName(), mouseX, mouseY, 2.0f, -5.0f, false);

        GlStateManager.enableAlpha();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!setting.isVisible() || !isHovered(mouseX, mouseY))
            return false;
        if (mouseButton == 0) {
            this.sliding = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        sliding = false;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }

    @Override
    public String getDescription() {
        return setting.getDescription();
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        if (!anyExpanded || isColorPanel) {
            return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height);
        }
        else {
            return false;
        }
    }

}
