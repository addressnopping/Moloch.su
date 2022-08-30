package net.spartanb312.base.gui.components;

import net.spartanb312.base.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Component;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.utils.SoundUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class BooleanButton extends Component {

    Setting<Boolean> setting;
    boolean isColorPanel;
    String moduleName;

    public BooleanButton(Setting<Boolean> setting, int width, int height, Panel father, boolean isColorPanel, Module module) {
        this.moduleName = module.name;
        this.width = width;
        this.height = height;
        this.father = father;
        this.setting = setting;
        this.isColorPanel = isColorPanel;
    }

    public static HashMap<String, Integer> storedBooleanTextLoops = new HashMap<>();
    public static HashMap<String, Integer> storedBooleanProgressLoops = new HashMap<>();

    public static HashMap<String, Integer> storedBooleanFullRectProgress = new HashMap<>();


    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        Color booleanTextColor = new Color(255, 255, 255, 255);
        Color booleanSecondaryColor;
        int booleanColor;
        Color booleanEnabledColor;
        Color booleanDisabledColor;

        int booleanTextColorDisabledColorColorDropMenuAnimateAlpha = (int)((ClickGUI.instance.booleanTextColorDisabledColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader);
        if (booleanTextColorDisabledColorColorDropMenuAnimateAlpha <= 4) {
            booleanTextColorDisabledColorColorDropMenuAnimateAlpha = 4;
        }
        int defaultTextColorColorDropMenuAnimateAlpha = (int)((255 / 300.0f) * colorMenuToggleThreader);
        if (defaultTextColorColorDropMenuAnimateAlpha <= 4) {
            defaultTextColorColorDropMenuAnimateAlpha = 4;
        }
        Color settingsTextColor = isColorPanel ? (new Color(ClickGUI.instance.booleanTextColorDisabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.booleanTextColorDisabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.booleanTextColorDisabledColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? booleanTextColorDisabledColorColorDropMenuAnimateAlpha : ClickGUI.instance.booleanTextColorDisabledColor.getValue().getAlpha())) : new Color(ClickGUI.instance.booleanTextColorDisabledColor.getValue().getColor());
        booleanColor = isColorPanel ? (new Color(ClickGUI.instance.booleanColor.getValue().getColorColor().getRed(), ClickGUI.instance.booleanColor.getValue().getColorColor().getGreen(), ClickGUI.instance.booleanColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.booleanColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.booleanColor.getValue().getAlpha()).getRGB()) : ClickGUI.instance.booleanColor.getValue().getColor();
        Color defaultTextColor = isColorPanel ? (new Color(255, 255, 255, ClickGUI.instance.colorDropMenuAnimate.getValue() ? defaultTextColorColorDropMenuAnimateAlpha : 255)) : new Color(255, 255, 255, 255);

        storedBooleanProgressLoops.putIfAbsent(setting.getName() + moduleName, 0);
        if (setting.getValue()) {
            int animateLoops = storedBooleanProgressLoops.get(setting.getName() + moduleName);
            animateLoops += ClickGUI.instance.booleanSmoothFactor.getValue() * 10;
            if (animateLoops >= 300) {
                animateLoops = 300;
            }
            if (animateLoops <= 0) {
                animateLoops = 0;
            }
            storedBooleanProgressLoops.put(setting.getName() + moduleName, animateLoops);
        }
        else {
            int animateLoops = storedBooleanProgressLoops.get(setting.getName() + moduleName);
            animateLoops -= ClickGUI.instance.booleanSmoothFactor.getValue() * 10;
            if (animateLoops >= 300) {
                animateLoops = 300;
            }
            if (animateLoops <= 0) {
                animateLoops = 0;
            }
            storedBooleanProgressLoops.put(setting.getName() + moduleName, animateLoops);
        }

        //boolean text color
        if (ClickGUI.instance.booleanTextColorChange.getValue()) {
            int booleanTextColorEnabledColorColorDropMenuAnimateAlpha = (int)((ClickGUI.instance.booleanTextColorEnabledColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader);
            if (booleanTextColorEnabledColorColorDropMenuAnimateAlpha <= 4) {
                booleanTextColorEnabledColorColorDropMenuAnimateAlpha = 4;
            }
            booleanTextColor = isColorPanel ? (new Color(ClickGUI.instance.booleanTextColorEnabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.booleanTextColorEnabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.booleanTextColorEnabledColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? booleanTextColorEnabledColorColorDropMenuAnimateAlpha : ClickGUI.instance.booleanTextColorEnabledColor.getValue().getAlpha())) : new Color(ClickGUI.instance.booleanTextColorEnabledColor.getValue().getColor());
            if (ClickGUI.instance.booleanTextColorSmooth.getValue()) {
                storedBooleanTextLoops.putIfAbsent(setting.getName() + moduleName, 0);
                if (setting.getValue()) {
                    int animateLoops = storedBooleanTextLoops.get(setting.getName() + moduleName);

                    int red = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getRed(), booleanTextColor.getRed(), animateLoops));
                    int green = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getGreen(), booleanTextColor.getGreen(), animateLoops));
                    int blue = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getBlue(), booleanTextColor.getBlue(), animateLoops));
                    int alpha = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getAlpha(), booleanTextColor.getAlpha(), animateLoops));

                    booleanTextColor = new Color(red, green, blue, alpha);
                    animateLoops += ClickGUI.instance.booleanTextColorSmoothFactor.getValue() * 10;

                    if (animateLoops >= 300) {
                        animateLoops = 300;
                    }
                    if (animateLoops <= 0) {
                        animateLoops = 0;
                    }
                    storedBooleanTextLoops.put(setting.getName() + moduleName, animateLoops);
                }
                else {
                    int animateLoops = storedBooleanTextLoops.get(setting.getName() + moduleName);

                    int red = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getRed(), booleanTextColor.getRed(), animateLoops));
                    int green = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getGreen(), booleanTextColor.getGreen(), animateLoops));
                    int blue = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getBlue(), booleanTextColor.getBlue(), animateLoops));
                    int alpha = (int)(MathUtilFuckYou.linearInterp(settingsTextColor.getAlpha(), booleanTextColor.getAlpha(), animateLoops));

                    booleanTextColor = new Color(red, green, blue, alpha);
                    animateLoops -= ClickGUI.instance.booleanTextColorSmoothFactor.getValue() * 10;

                    if (animateLoops >= 300) {
                        animateLoops = 300;
                    }
                    if (animateLoops <= 0) {
                        animateLoops = 0;
                    }
                    storedBooleanTextLoops.put(setting.getName() + moduleName, animateLoops);
                }
            }
            else {
                if (!setting.getValue()) {
                    booleanTextColor = settingsTextColor;
                }
            }
        }

        //boolean full rect
        if (ClickGUI.instance.booleanFullRect.getValue()) {
            Color booleanFullRectColor = isColorPanel ? (new Color(ClickGUI.instance.booleanFullRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.booleanFullRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.booleanFullRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.booleanFullRectColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.booleanFullRectColor.getValue().getAlpha())) : new Color(ClickGUI.instance.booleanFullRectColor.getValue().getColor());

            if (ClickGUI.instance.booleanFullRectSmooth.getValue()) {
                storedBooleanFullRectProgress.putIfAbsent(setting.getName() + moduleName, 0);
                int animateLoops = storedBooleanFullRectProgress.get(setting.getName() + moduleName);
                if (setting.getValue()) {
                    animateLoops += ClickGUI.instance.booleanFullRectSmoothFactor.getValue() * 10;
                }
                else {
                    animateLoops -= ClickGUI.instance.booleanFullRectSmoothFactor.getValue() * 10;
                }
                if (animateLoops >= 300) {
                    animateLoops = 300;
                }
                if (animateLoops <= 0) {
                    animateLoops = 0;
                }
                storedBooleanFullRectProgress.put(setting.getName() + moduleName, animateLoops);

                if (ClickGUI.instance.booleanFullRectSmoothAlpha.getValue()) {
                    booleanFullRectColor = new Color(booleanFullRectColor.getRed(), booleanFullRectColor.getGreen(), booleanFullRectColor.getBlue(), (int)((booleanFullRectColor.getAlpha() / 300.0f) * storedBooleanFullRectProgress.get(setting.getName() + moduleName)));
                }
            }

            if (setting.getValue() && !ClickGUI.instance.booleanFullRectSmooth.getValue()) {
                RenderUtils2D.drawRect(x + 3, y + 1, x + width - 1, y + height, booleanFullRectColor.getRGB());
            }
            else if (ClickGUI.instance.booleanFullRectSmooth.getValue() && (ClickGUI.instance.booleanFullRectScaleType.getValue() != ClickGUI.BooleanFullRectScaleType.None || ClickGUI.instance.booleanFullRectSmoothAlpha.getValue())) {
                RenderUtils2D.drawRect(x + 3 + (ClickGUI.instance.booleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.Left ? ((300.0f - storedBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((width - 4) / 300.0f)) : (ClickGUI.instance.booleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedBooleanFullRectProgress.get(setting.getName() + moduleName)) * (((width - 4) / 2.0f) / 300.0f)) : 0)), y + 1 + (ClickGUI.instance.booleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((height / 2.0f) / 300.0f)) : 0), x + width - 1 - (ClickGUI.instance.booleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.Right ? ((300.0f - storedBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((width - 4) / 300.0f)) : (ClickGUI.instance.booleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedBooleanFullRectProgress.get(setting.getName() + moduleName)) * (((width - 2) / 2.0f) / 300.0f)) : 0)), y + height - (ClickGUI.instance.booleanFullRectScaleType.getValue() == ClickGUI.BooleanFullRectScaleType.All ? ((300.0f - storedBooleanFullRectProgress.get(setting.getName() + moduleName)) * ((height / 2.0f) / 300.0f)) : 0), booleanFullRectColor.getRGB());
            }
        }

        //boolean switches
        if (ClickGUI.instance.booleanSwitchType.getValue() != ClickGUI.BooleanSwitchTypes.None) {
            if (ClickGUI.instance.booleanSwitchColorChange.getValue()) {

                booleanEnabledColor = isColorPanel ? (new Color(ClickGUI.instance.booleanEnabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.booleanEnabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.booleanEnabledColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.booleanEnabledColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.booleanEnabledColor.getValue().getAlpha())) : new Color(ClickGUI.instance.booleanEnabledColor.getValue().getColor());
                booleanDisabledColor = isColorPanel ? (new Color(ClickGUI.instance.booleanDisabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.booleanDisabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.booleanDisabledColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.booleanDisabledColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.booleanDisabledColor.getValue().getAlpha())) : new Color(ClickGUI.instance.booleanDisabledColor.getValue().getColor());

                if (ClickGUI.instance.booleanSmooth.getValue()) {
                    int red = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getRed(), booleanEnabledColor.getRed(), storedBooleanProgressLoops.get(setting.getName() + moduleName)));
                    int green = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getGreen(), booleanEnabledColor.getGreen(), storedBooleanProgressLoops.get(setting.getName() + moduleName)));
                    int blue = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getBlue(), booleanEnabledColor.getBlue(), storedBooleanProgressLoops.get(setting.getName() + moduleName)));
                    int alpha = (int)(MathUtilFuckYou.linearInterp(booleanDisabledColor.getAlpha(), booleanEnabledColor.getAlpha(), storedBooleanProgressLoops.get(setting.getName() + moduleName)));
                    booleanColor = new Color(red, green, blue, alpha).getRGB();
                }
                else {
                    if (setting.getValue()) {
                        booleanColor = booleanEnabledColor.getRGB();
                    }
                    else {
                        booleanColor = booleanDisabledColor.getRGB();
                    }
                }
            }

            //boolean sliders
            float centerX = x + ((width / 5.0f) * 4.0f) + (18.0f * ClickGUI.instance.booleanSwitchScale.getValue()) + ClickGUI.instance.booleanSwitchX.getValue() - ((18.0f * ClickGUI.instance.booleanSwitchScale.getValue()) - (height / 4.0f)) + (ClickGUI.instance.booleanSmooth.getValue() ? (storedBooleanProgressLoops.get(setting.getName() + moduleName) * (((18.0f * ClickGUI.instance.booleanSwitchScale.getValue()) - (height / 2.0f)) / 300.0f)) : (setting.getValue() ? ((18.0f * ClickGUI.instance.booleanSwitchScale.getValue()) - (height / 2.0f)) : 0));
            float centerY = y + (height / 2.0f);
            if (ClickGUI.instance.booleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.SliderRound) {
                RenderUtils2D.drawCircle(centerX, centerY, (height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue(), booleanColor);
                RenderUtils2D.drawCustomRoundedRectOutline(x + ((width / 5.0f) * 4.0f) + ClickGUI.instance.booleanSwitchX.getValue(), y + (height / 4.0f), x + ((width / 5.0f) * 4.0f) + (18.0f * ClickGUI.instance.booleanSwitchScale.getValue()) + ClickGUI.instance.booleanSwitchX.getValue(), y + ((height / 4.0f) * 3.0f), 1.0f, ClickGUI.instance.booleanSwitchLineWidth.getValue(), true, true, true, true, false, false, booleanColor);
            }
            else if (ClickGUI.instance.booleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.SliderNonRound) {
                RenderUtils2D.drawRect(centerX - ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()), centerY - ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()), centerX + ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()), centerY + ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()), booleanColor);
                RenderUtils2D.drawRectOutline(x + ((width / 5.0f) * 4.0f) + ClickGUI.instance.booleanSwitchX.getValue(), y + (height / 4.0f), x + ((width / 5.0f) * 4.0f) + (18.0f * ClickGUI.instance.booleanSwitchScale.getValue()) + ClickGUI.instance.booleanSwitchX.getValue(), y + ((height / 4.0f) * 3.0f), ClickGUI.instance.booleanSwitchLineWidth.getValue(), booleanColor, false, false);
            }

            //boolean dots
            float centerXDotMode = x + ((width / 5.0f) * 4.0f) + 7.0f + ClickGUI.instance.booleanSwitchX.getValue();
            float centerYDotMode = y + (height / 2.0f);
            if ((ClickGUI.instance.booleanDotMode.getValue() == ClickGUI.BooleanDotMode.Alpha || ClickGUI.instance.booleanDotMode.getValue() == ClickGUI.BooleanDotMode.Both) && (ClickGUI.instance.booleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Circle || ClickGUI.instance.booleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Square)) {
                booleanSecondaryColor = new Color(new Color(booleanColor).getRed(), new Color(booleanColor).getGreen(), new Color(booleanColor).getBlue(), (int)((new Color(booleanColor).getAlpha() / 300.0f) * storedBooleanProgressLoops.get(setting.getName() + moduleName)));
            }
            else {
                booleanSecondaryColor = new Color(booleanColor);
            }
            if (ClickGUI.instance.booleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Circle) {
                RenderUtils2D.drawCircle(centerXDotMode, centerYDotMode, ClickGUI.instance.booleanDotMode.getValue() == ClickGUI.BooleanDotMode.Scale || ClickGUI.instance.booleanDotMode.getValue() == ClickGUI.BooleanDotMode.Both ? (ClickGUI.instance.booleanSmooth.getValue() ? (storedBooleanProgressLoops.get(setting.getName() + moduleName) * (((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()) / 300.0f)) : (setting.getValue() ? ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()) : 0)) : ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()), booleanSecondaryColor.getRGB());
                RenderUtils2D.drawCircleOutline(centerXDotMode, centerYDotMode, height / 4.0f, ClickGUI.instance.booleanSwitchLineWidth.getValue(), booleanColor);
            }
            else if (ClickGUI.instance.booleanSwitchType.getValue() == ClickGUI.BooleanSwitchTypes.Square) {
                float squareDotScale = (ClickGUI.instance.booleanDotMode.getValue() == ClickGUI.BooleanDotMode.Scale || ClickGUI.instance.booleanDotMode.getValue() == ClickGUI.BooleanDotMode.Both ? (ClickGUI.instance.booleanSmooth.getValue() ? (storedBooleanProgressLoops.get(setting.getName() + moduleName) * (((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()) / 300.0f)) : (setting.getValue() ? ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()) : 0)) : ((height / 4.0f) * ClickGUI.instance.booleanDotFillAmount.getValue()));
                RenderUtils2D.drawRect(centerXDotMode - squareDotScale, centerYDotMode - squareDotScale, centerXDotMode + squareDotScale, centerYDotMode + squareDotScale, booleanSecondaryColor.getRGB());
                RenderUtils2D.drawRectOutline(centerXDotMode - (height / 4.0f), centerYDotMode - (height / 4.0f), centerXDotMode + (height / 4.0f), centerYDotMode + (height / 4.0f), ClickGUI.instance.booleanSwitchLineWidth.getValue(), booleanColor, false, false);
            }
        }

        //boolean text rendering
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f), ClickGUI.instance.booleanTextColorChange.getValue() ? booleanTextColor.getRGB() : defaultTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            if (CustomFont.instance.textShadow.getValue()) {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, ClickGUI.instance.booleanTextColorChange.getValue() ? booleanTextColor.getRGB() : defaultTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, ClickGUI.instance.booleanTextColorChange.getValue() ? booleanTextColor.getRGB() : defaultTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
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

        renderHoverRect(moduleName + setting.getName(), mouseX, mouseY, 2.0f, -1.0f, false);

        GlStateManager.enableAlpha();
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


    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!setting.isVisible() || !isHovered(mouseX, mouseY))
            return false;
        if (mouseButton == 0) {
            this.setting.setValue(!setting.getValue());
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
