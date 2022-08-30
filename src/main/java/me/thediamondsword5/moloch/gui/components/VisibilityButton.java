package me.thediamondsword5.moloch.gui.components;

import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.SoundUtil;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import me.thediamondsword5.moloch.core.setting.settings.VisibilitySetting;
import net.minecraft.client.renderer.GlStateManager;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class VisibilityButton extends net.spartanb312.base.gui.Component {

    VisibilitySetting setting;
    String moduleName;
    

    public VisibilityButton(VisibilitySetting setting, int width, int height, Panel father, Module module) {
        this.width = width;
        this.height = height;
        this.father = father;
        this.setting = setting;
        this.moduleName = module.name;
    }

    public static HashMap<String, Integer> storedVisibilityBooleanProgressLoops = new HashMap<>();
    public static HashMap<String, Integer> storedVisibilityBooleanFullRectProgress = new HashMap<>();

    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        Color visibleVisibleTextColor = new Color(ClickGUI.instance.visibilityVisibleTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityVisibleTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityVisibleTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityVisibleTextColor.getValue().getAlpha());

        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f), visibleVisibleTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            if (CustomFont.instance.textShadow.getValue()) {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, visibleVisibleTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, visibleVisibleTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }
        }

        if (ClickGUI.instance.visibilitySettingMode.getValue() == ClickGUI.VisibilitySettingMode.Boolean) {
            Color booleanSecondaryColor;
            int booleanColor = ClickGUI.instance.visibilityBooleanColor.getValue().getColor();
            Color booleanEnabledColor;
            Color booleanDisabledColor;

            storedVisibilityBooleanProgressLoops.putIfAbsent(setting.getName() + moduleName, 0);
            if (setting.getValue().getVisible()) {
                int animateLoops = storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName);
                animateLoops += ClickGUI.instance.visibilityBooleanSmoothFactor.getValue() * 10;
                if (animateLoops >= 300) {
                    animateLoops = 300;
                }
                if (animateLoops <= 0) {
                    animateLoops = 0;
                }
                storedVisibilityBooleanProgressLoops.put(setting.getName() + moduleName, animateLoops);
            }
            else {
                int animateLoops = storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName);
                animateLoops -= ClickGUI.instance.visibilityBooleanSmoothFactor.getValue() * 10;
                if (animateLoops >= 300) {
                    animateLoops = 300;
                }
                if (animateLoops <= 0) {
                    animateLoops = 0;
                }
                storedVisibilityBooleanProgressLoops.put(setting.getName() + moduleName, animateLoops);
            }



            //boolean full rect
            if (ClickGUI.instance.visibilityBooleanFullRect.getValue()) {
                Color booleanFullRectColor = new Color(ClickGUI.instance.visibilityBooleanFullRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityBooleanFullRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityBooleanFullRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityBooleanFullRectColor.getValue().getAlpha());

                if (ClickGUI.instance.visibilityBooleanFullRectSmooth.getValue()) {
                    storedVisibilityBooleanFullRectProgress.putIfAbsent(setting.getName() + moduleName, 0);
                    int animateLoops = storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName);
                    if (setting.getValue().getVisible()) {
                        animateLoops += ClickGUI.instance.visibilityBooleanFullRectSmoothFactor.getValue() * 10;
                    }
                    else {
                        animateLoops -= ClickGUI.instance.visibilityBooleanFullRectSmoothFactor.getValue() * 10;
                    }
                    if (animateLoops >= 300) {
                        animateLoops = 300;
                    }
                    if (animateLoops <= 0) {
                        animateLoops = 0;
                    }
                    storedVisibilityBooleanFullRectProgress.put(setting.getName() + moduleName, animateLoops);

                    if (ClickGUI.instance.visibilityBooleanFullRectSmoothAlpha.getValue()) {
                        booleanFullRectColor = new Color(booleanFullRectColor.getRed(), booleanFullRectColor.getGreen(), booleanFullRectColor.getBlue(), (int)((ClickGUI.instance.visibilityBooleanFullRectColor.getValue().getAlpha() / 300.0f) * storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)));
                    }
                }

                if (setting.getValue().getVisible() && !ClickGUI.instance.visibilityBooleanFullRectSmooth.getValue()) {
                    RenderUtils2D.drawRect(x + 3, y + 1, x + width - 1, y + height, booleanFullRectColor.getRGB());
                }
                else if (ClickGUI.instance.visibilityBooleanFullRectSmooth.getValue() && (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() != ClickGUI.BooleanFullRectScaleType.None || ClickGUI.instance.visibilityBooleanFullRectSmoothAlpha.getValue())) {
                    RenderUtils2D.drawRect(x + 3 + (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.Left ? ((300.0f - storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((width - 4) / 300.0f)) : (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)) * (((width - 4) / 2.0f) / 300.0f)) : 0)), y + 1 + (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((height / 2.0f) / 300.0f)) : 0), x + width - 1 - (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.Right ? ((300.0f - storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((width - 4) / 300.0f)) : (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)) * (((width - 2) / 2.0f) / 300.0f)) : 0)), y + height - (ClickGUI.instance.visibilityBooleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedVisibilityBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((height / 2.0f) / 300.0f)) : 0), booleanFullRectColor.getRGB());
                }
            }

            //boolean switches
            if (ClickGUI.instance.visibilityBooleanSwitchType.getValue() != ClickGUI.BooleanSwitchTypes.None) {
                if (ClickGUI.instance.visibilityBooleanSwitchColorChange.getValue()) {

                    booleanEnabledColor = new Color(ClickGUI.instance.visibilityBooleanEnabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityBooleanEnabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityBooleanEnabledColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityBooleanEnabledColor.getValue().getAlpha());
                    booleanDisabledColor = new Color(ClickGUI.instance.visibilityBooleanDisabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityBooleanDisabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityBooleanDisabledColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityBooleanDisabledColor.getValue().getAlpha());

                    if (ClickGUI.instance.visibilityBooleanSmooth.getValue()) {
                        int red = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getRed(), booleanEnabledColor.getRed(), storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName)));
                        int green = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getGreen(), booleanEnabledColor.getGreen(), storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName)));
                        int blue = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getBlue(), booleanEnabledColor.getBlue(), storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName)));
                        int alpha = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.visibilityBooleanDisabledColor.getValue().getAlpha(), ClickGUI.instance.visibilityBooleanEnabledColor.getValue().getAlpha(), storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName)));
                        booleanColor = new Color(red, green, blue, alpha).getRGB();
                    }
                    else {
                        if (setting.getValue().getVisible()) {
                            booleanColor = booleanEnabledColor.getRGB();
                        }
                        else {
                            booleanColor = booleanDisabledColor.getRGB();
                        }
                    }
                }

                //boolean sliders
                float centerX = x + ((width / 5.0f) * 4.0f) + (18.0f * ClickGUI.instance.visibilityBooleanSwitchScale.getValue()) + ClickGUI.instance.visibilityBooleanSwitchX.getValue() - ((18.0f * ClickGUI.instance.visibilityBooleanSwitchScale.getValue()) - (height / 4.0f)) + (ClickGUI.instance.visibilityBooleanSmooth.getValue() ? (storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName) * (((18.0f * ClickGUI.instance.visibilityBooleanSwitchScale.getValue()) - (height / 2.0f)) / 300.0f)) : (setting.getValue().getVisible() ? ((18.0f * ClickGUI.instance.visibilityBooleanSwitchScale.getValue()) - (height / 2.0f)) : 0));
                float centerY = y + (height / 2.0f);
                if (ClickGUI.instance.visibilityBooleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.SliderRound) {
                    RenderUtils2D.drawCircle(centerX, centerY, (height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue(), booleanColor);
                    RenderUtils2D.drawCustomRoundedRectOutline(x + ((width / 5.0f) * 4.0f) + ClickGUI.instance.visibilityBooleanSwitchX.getValue(), y + (height / 4.0f), x + ((width / 5.0f) * 4.0f) + (18.0f * ClickGUI.instance.visibilityBooleanSwitchScale.getValue()) + ClickGUI.instance.visibilityBooleanSwitchX.getValue(), y + ((height / 4.0f) * 3.0f), 1.0f, ClickGUI.instance.visibilityBooleanSwitchLineWidth.getValue(), true, true, true, true, false, false, booleanColor);
                }
                else if (ClickGUI.instance.visibilityBooleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.SliderNonRound) {
                    RenderUtils2D.drawRect(centerX - ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()), centerY - ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()), centerX + ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()), centerY + ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()), booleanColor);
                    RenderUtils2D.drawRectOutline(x + ((width / 5.0f) * 4.0f) + ClickGUI.instance.visibilityBooleanSwitchX.getValue(), y + (height / 4.0f), x + ((width / 5.0f) * 4.0f) + (18.0f * ClickGUI.instance.visibilityBooleanSwitchScale.getValue()) + ClickGUI.instance.visibilityBooleanSwitchX.getValue(), y + ((height / 4.0f) * 3.0f), ClickGUI.instance.visibilityBooleanSwitchLineWidth.getValue(), booleanColor, false, false);
                }

                //boolean dots
                float centerXDotMode = x + ((width / 5.0f) * 4.0f) + 7.0f + ClickGUI.instance.visibilityBooleanSwitchX.getValue();
                float centerYDotMode = y + (height / 2.0f);
                if ((ClickGUI.instance.visibilityBooleanDotMode.getValue() == ClickGUI.BooleanDotMode.Alpha || ClickGUI.instance.visibilityBooleanDotMode.getValue() == ClickGUI.BooleanDotMode.Both) && (ClickGUI.instance.visibilityBooleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Circle || ClickGUI.instance.visibilityBooleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Square)) {
                    booleanSecondaryColor = new Color(new Color(booleanColor).getRed(), new Color(booleanColor).getGreen(), new Color(booleanColor).getBlue(), (int)((new Color(booleanColor).getAlpha() / 300.0f) * storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName)));
                }
                else {
                    booleanSecondaryColor = new Color(booleanColor);
                }
                if (ClickGUI.instance.visibilityBooleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Circle) {
                    RenderUtils2D.drawCircle(centerXDotMode, centerYDotMode, ClickGUI.instance.visibilityBooleanDotMode.getValue() == ClickGUI.BooleanDotMode.Scale || ClickGUI.instance.visibilityBooleanDotMode.getValue() == ClickGUI.BooleanDotMode.Both ? (ClickGUI.instance.visibilityBooleanSmooth.getValue() ? (storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName) * (((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()) / 300.0f)) : (setting.getValue().getVisible() ? ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()) : 0)) : ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()), booleanSecondaryColor.getRGB());
                    RenderUtils2D.drawCircleOutline(centerXDotMode, centerYDotMode, height / 4.0f, ClickGUI.instance.visibilityBooleanSwitchLineWidth.getValue(), booleanColor);
                }
                else if (ClickGUI.instance.visibilityBooleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Square) {
                    float squareDotScale = (ClickGUI.instance.visibilityBooleanDotMode.getValue() == ClickGUI.BooleanDotMode.Scale || ClickGUI.instance.visibilityBooleanDotMode.getValue() == ClickGUI.BooleanDotMode.Both ? (ClickGUI.instance.visibilityBooleanSmooth.getValue() ? (storedVisibilityBooleanProgressLoops.get(setting.getName() + moduleName) * (((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()) / 300.0f)) : (setting.getValue().getVisible() ? ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()) : 0)) : ((height / 4.0f) * ClickGUI.instance.visibilityBooleanDotFillAmount.getValue()));
                    RenderUtils2D.drawRect(centerXDotMode - squareDotScale, centerYDotMode - squareDotScale, centerXDotMode + squareDotScale, centerYDotMode + squareDotScale, booleanSecondaryColor.getRGB());
                    RenderUtils2D.drawRectOutline(centerXDotMode - (height / 4.0f), centerYDotMode - (height / 4.0f), centerXDotMode + (height / 4.0f), centerYDotMode + (height / 4.0f), ClickGUI.instance.visibilityBooleanSwitchLineWidth.getValue(), booleanColor, false, false);
                }
            }
        }
        else if (ClickGUI.instance.visibilitySettingMode.getValue() == ClickGUI.VisibilitySettingMode.Text) {
            Color visibilityTextColor = new Color(ClickGUI.instance.visibilityTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityTextColor.getValue().getAlpha());
            String theString;
            if (setting.getValue().getVisible()) {
                theString = "True";
            }
            else {
                theString = "False";
            }

            float currentTextWidth = font.getStringWidth(theString);
            if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
                GL11.glTranslatef(((x + width - 3 - font.getStringWidth(theString)) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(theString, x + width - 3 - font.getStringWidth(theString), (int) (y + height / 2 - font.getHeight() / 2f), visibilityTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef(((x + width - 3 - font.getStringWidth(theString)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }
            else {
                if (CustomFont.instance.textShadow.getValue()) {

                    GL11.glTranslatef(((x + width - 3 - font.getStringWidth(theString)) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.drawShadow(theString, x + width - 3 - font.getStringWidth(theString), (int) (y + height / 2 - font.getHeight() / 2f) + 3, visibilityTextColor.getRGB());


                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslatef(((x + width - 3 - font.getStringWidth(theString)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

                }
                else {

                    GL11.glTranslated(((x + width - 3 - font.getStringWidth(theString)) * (1.0f - CustomFont.instance.componentTextScale.getValue())) + ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                    GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                    fontManager.draw(theString, x + width - 3 - font.getStringWidth(theString), (int) (y + height / 2 - font.getHeight() / 2f) + 3, visibilityTextColor);

                    GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                    GL11.glTranslated(((x + width - 3 - font.getStringWidth(theString)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f) - ((1.0f - CustomFont.instance.componentTextScale.getValue()) * currentTextWidth), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

                }
            }
        }
        else {
            Color visibilityIconColor = new Color(ClickGUI.instance.visibilityIconColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityIconColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityIconColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityIconColor.getValue().getAlpha());

            GL11.glTranslatef((x + width - 3 - FontManager.getVisibilityIconWidth()) * (1.0f - ClickGUI.instance.visibilityIconScale.getValue()), ((int)(y + (height / 2.0f) + ClickGUI.instance.visibilityIconYOffset.getValue())) * (1.0f - ClickGUI.instance.visibilityIconScale.getValue()), 0.0f);
            GL11.glScalef(ClickGUI.instance.visibilityIconScale.getValue(), ClickGUI.instance.visibilityIconScale.getValue(), ClickGUI.instance.visibilityIconScale.getValue());
            if (setting.getValue().getVisible()) {
                FontManager.drawVisibilityIconOn(x + width - 3 - FontManager.getVisibilityIconWidth(), (int)(y + (height / 2.0f) + ClickGUI.instance.visibilityIconYOffset.getValue()), visibilityIconColor.getRGB());
            }
            else {
                FontManager.drawVisibilityIconOff(x + width - 3 - FontManager.getVisibilityIconWidth(), (int)(y + (height / 2.0f) + ClickGUI.instance.visibilityIconYOffset.getValue()), visibilityIconColor.getRGB());
            }
            GL11.glScalef(1.0f / ClickGUI.instance.visibilityIconScale.getValue(), 1.0f / ClickGUI.instance.visibilityIconScale.getValue(), 1.0f / ClickGUI.instance.visibilityIconScale.getValue());
            GL11.glTranslatef((x + width - 3 - FontManager.getVisibilityIconWidth()) * (1.0f - ClickGUI.instance.visibilityIconScale.getValue()) * -1.0f, ((int)(y + (height / 2.0f) + ClickGUI.instance.visibilityIconYOffset.getValue())) * (1.0f - ClickGUI.instance.visibilityIconScale.getValue()) * -1.0f, 0.0f);

            if (ClickGUI.instance.visibilityIconGlow.getValue() && !(ClickGUI.instance.visibilityIconGlowToggle.getValue() && !setting.getValue().getVisible())) {
                Color visibilityIconGlowColor = new Color(ClickGUI.instance.visibilityIconGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.visibilityIconGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.visibilityIconGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.visibilityIconGlowColor.getValue().getAlpha());

                GlStateManager.disableAlpha();
                RenderUtils2D.drawCustomCircle(x + width - 3 - (FontManager.getVisibilityIconWidth() / 2.0f) + ClickGUI.instance.visibilityIconGlowX.getValue(), (int)(y + (height / 2.0f) + ClickGUI.instance.visibilityIconYOffset.getValue() + (FontManager.getIconHeight() / 2.0f) + ClickGUI.instance.visibilityIconGlowY.getValue()), ClickGUI.instance.visibilityIconGlowSize.getValue(), visibilityIconGlowColor.getRGB(), new Color(0, 0, 0, 0).getRGB());
                GlStateManager.disableAlpha();
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
        if (!setting.isVisible() || !isHovered(mouseX, mouseY))
            return false;
        if (mouseButton == 0) {
            setting.setOpposite(!setting.getValue().getVisible());
            SoundUtil.playButtonClick();
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
