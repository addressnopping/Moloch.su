package me.thediamondsword5.moloch.gui.components;

import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Component;
import net.spartanb312.base.gui.components.BooleanButton;
import net.spartanb312.base.gui.components.NumberSlider;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.module.modules.client.HUDEditor;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.core.setting.NumberSetting;
import net.spartanb312.base.core.setting.settings.BooleanSetting;
import net.spartanb312.base.utils.SoundUtil;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class ColorButton extends Component {

    Setting<me.thediamondsword5.moloch.core.common.Color> setting;
    String moduleName;
    boolean colorExpanded = false;
    List<Component> visibleColorSettings = new ArrayList<>();
    public BooleanSetting syncGlobalSetting = new BooleanSetting("SyncGlobal", false);
    BooleanSetting rainbowSetting = new BooleanSetting("Rainbow", false);
    NumberSetting rainbowSpeedSetting = new NumberSetting("RainbowSpeed", 1.0f, 0.0f, 4.0f);
    NumberSetting rainbowSaturationSetting = new NumberSetting("RainbowSaturation", 0.75f, 0.0f, 1.0f);
    NumberSetting rainbowBrightnessSetting = new NumberSetting("RainbowBrightness", 0.9f, 0.0f, 1.0f);
    NumberSetting redSetting = new NumberSetting("Red", 255, 0, 255);
    NumberSetting greenSetting = new NumberSetting("Green", 255, 0, 255);
    NumberSetting blueSetting = new NumberSetting("Blue", 255, 0, 255);
    NumberSetting alphaSetting = new NumberSetting("Alpha", 255, 0, 255);
    Component syncGlobalButton;
    Component rainbowButton;
    Component rainbowSpeedSlider;
    Component rainbowSaturationSlider;
    Component rainbowBrightnessSlider;
    Component redSlider;
    Component greenSlider;
    Component blueSlider;
    Component alphaSlider;
    public int startY = y;
    boolean colorSomewhereClicked;
    boolean pastRainbowSetting;
    boolean isSlidingRainbowSpeedSlider;
    boolean isSlidingRainbowSaturationSlider;
    boolean isSlidingRainbowBrightnessSlider;
    boolean isSlidingRedSlider;
    boolean isSlidingGreenSlider;
    boolean isSlidingBlueSlider;
    boolean isSlidingAlphaSlider;
    boolean reverseAnimationFlag = false;
    long lastTime = 0;


    public ColorButton(Setting<me.thediamondsword5.moloch.core.common.Color> setting, int width, int height, net.spartanb312.base.gui.Panel father, Module module) {

        this.syncGlobalButton = new BooleanButton(syncGlobalSetting, width, height, father, true, module);
        this.rainbowButton = new BooleanButton(rainbowSetting, width, height, father, true, module);
        this.rainbowSpeedSlider = new NumberSlider(rainbowSpeedSetting, width, height, father, true, module);
        this.rainbowSaturationSlider = new NumberSlider(rainbowSaturationSetting, width, height, father, true, module);
        this.rainbowBrightnessSlider = new NumberSlider(rainbowBrightnessSetting, width, height, father, true, module);
        this.redSlider = new NumberSlider(redSetting, width, height, father, true, module);
        this.greenSlider = new NumberSlider(greenSetting, width, height, father, true, module);
        this.blueSlider = new NumberSlider(blueSetting, width, height, father, true, module);
        this.alphaSlider = new NumberSlider(alphaSetting, width, height, father, true, module);

        this.width = width;
        this.height = height;
        this.father = father;
        this.setting = setting;
        this.moduleName = module.name;


        visibleColorSettings.add(syncGlobalButton);
        visibleColorSettings.add(rainbowButton);
        visibleColorSettings.add(alphaSlider);
        if (rainbowSetting.getValue()) {
            visibleColorSettings.add(rainbowSpeedSlider);
            visibleColorSettings.add(rainbowSaturationSlider);
            visibleColorSettings.add(rainbowBrightnessSlider);
            visibleColorSettings.remove(redSlider);
            visibleColorSettings.remove(greenSlider);
            visibleColorSettings.remove(blueSlider);
        }
        else {
            visibleColorSettings.add(redSlider);
            visibleColorSettings.add(greenSlider);
            visibleColorSettings.add(blueSlider);
            visibleColorSettings.remove(rainbowSpeedSlider);
            visibleColorSettings.remove(rainbowSaturationSlider);
            visibleColorSettings.remove(rainbowBrightnessSlider);
        }
    }

    public void setSetting(Component setting) {
        if (!visibleColorSettings.contains(setting)) {
            visibleColorSettings.add(setting);
        }
    }

    public void setSlider(NumberSetting setting, boolean isSliding, int mouseX, int theX) {
        if (isSliding) {
            double diff = setting.getMax().doubleValue() - setting.getMin().doubleValue();
            double val = setting.getMin().doubleValue() + (MathHelper.clamp((mouseX - (double) (theX + 3)) / (double) (width - 4), 0, 1)) * diff;
            if (setting.equals(rainbowSpeedSetting)) {
                this.setting.getValue().setRainbowSpeed((float)val);
            }if (setting.equals(rainbowSaturationSetting)) {
                this.setting.getValue().setRainbowSaturation((float)val);
            }if (setting.equals(rainbowBrightnessSetting)) {
                this.setting.getValue().setRainbowBrightness((float)val);
            }if (setting.equals(redSetting)) {
                this.setting.getValue().setRed((int)val);
            }if (setting.equals(greenSetting)) {
                this.setting.getValue().setGreen((int)val);
            }if (setting.equals(blueSetting)) {
                this.setting.getValue().setBlue((int)val);
            }if (setting.equals(alphaSetting)) {
                this.setting.getValue().setAlpha((int)val);
            }
        }
    }

    public void colorPanelGlowExtensions(Component component, boolean lastSetting, boolean firstSetting) {
        if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
            GlStateManager.disableAlpha();
            if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                if (firstSetting)
                    RenderUtils2D.drawCustomRect(component.x, component.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), component.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), component.y, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB(),  new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                if (lastSetting)
                    RenderUtils2D.drawCustomRect(component.x, component.y + height + 4, component.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), component.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB(),  new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB(), new Color(0, 0, 0, 0).getRGB());
            }
            if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                if (firstSetting)
                    RenderUtils2D.drawCustomRect(component.x + width - ClickGUI.instance.moduleSideGlowWidth.getValue(), component.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), component.x + width, component.y, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB());
                if (lastSetting)
                    RenderUtils2D.drawCustomRect(component.x + width - ClickGUI.instance.moduleSideGlowWidth.getValue(), component.y + height + 4, component.x + width, component.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowColor.getValue().getAlpha()).getRGB());
            }
            GlStateManager.enableAlpha();
        }
    }


    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        Color colorTextColor = new Color(ClickGUI.instance.colorNameTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.colorNameTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.colorNameTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorNameTextColor.getValue().getAlpha());

        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f), colorTextColor.getRGB(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            if (CustomFont.instance.textShadow.getValue()) {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, colorTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, colorTextColor.getRGB());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }
        }


        //color menu setting stuff
        syncGlobalSetting.setValue(setting.getValue().getSyncGlobal());
        rainbowSetting.setValue(setting.getValue().getRainbow());
        alphaSetting.setValue(setting.getValue().getAlpha());
        rainbowSpeedSetting.setValue(setting.getValue().getRainbowSpeed());
        rainbowSaturationSetting.setValue(setting.getValue().getRainbowSaturation());
        rainbowBrightnessSetting.setValue(setting.getValue().getRainbowBrightness());
        redSetting.setValue(setting.getValue().getRed());
        greenSetting.setValue(setting.getValue().getGreen());
        blueSetting.setValue(setting.getValue().getBlue());

        //color menu render
        if (colorExpanded) {
            //new system time based animation system instead of loop time based (add the animation progress by the amount of ms passed between the current time and the time last loop divided by 50 and multiplied by some factor)
            //this will make the animations not slow down when the longer it takes to render a frame by compensating via adding all the lost progress at once when the frame renders
            if (ClickGUI.instance.colorDropMenuAnimate.getValue()) {
                for (int i = 0; i < (int)(((System.currentTimeMillis() - lastTime) / 50.0f) * ClickGUI.instance.colorDropMenuAnimateSpeed.getValue()); i++) {
                    colorMenuToggleThreader += reverseAnimationFlag ? -1 : 1;
                }

                if (colorMenuToggleThreader >= 300) {
                    colorMenuToggleThreader = 300;
                }
                if (colorMenuToggleThreader <= 0) {
                    colorMenuToggleThreader = 0;
                }

            }
            else {
                 reverseAnimationFlag = false;
            }

            if (rainbowSetting.getValue() != pastRainbowSetting) {
                setSetting(syncGlobalButton);
                setSetting(rainbowButton);
                setSetting(alphaSlider);
                if (rainbowSetting.getValue()) {
                    setSetting(rainbowSpeedSlider);
                    setSetting(rainbowSaturationSlider);
                    setSetting(rainbowBrightnessSlider);
                    visibleColorSettings.remove(redSlider);
                    visibleColorSettings.remove(greenSlider);
                    visibleColorSettings.remove(blueSlider);
                }
                else {
                    setSetting(redSlider);
                    setSetting(greenSlider);
                    setSetting(blueSlider);
                    visibleColorSettings.remove(rainbowSpeedSlider);
                    visibleColorSettings.remove(rainbowSaturationSlider);
                    visibleColorSettings.remove(rainbowBrightnessSlider);
                }
                pastRainbowSetting = rainbowSetting.getValue();
            }



            int startX = x + width + ClickGUI.instance.colorDropMenuXOffset.getValue();
            int endX = x + (width * 2) + ClickGUI.instance.colorDropMenuXOffset.getValue();

            //scale animation shit
            if (ClickGUI.instance.colorDropMenuAnimate.getValue() && ClickGUI.instance.colorDropMenuAnimateScale.getValue()) {
                int colorMenuToggleThreaderScale = colorMenuToggleThreader;
                if (colorMenuToggleThreaderScale <= 1) {
                    colorMenuToggleThreaderScale = 1;
                }
                GL11.glTranslatef(startX * (1.0f - (colorMenuToggleThreaderScale / 300.0f)), y * (1.0f - (colorMenuToggleThreaderScale / 300.0f)), 0.0f);
                GL11.glScalef(colorMenuToggleThreaderScale / 300.0f, colorMenuToggleThreaderScale / 300.0f, 0.0f);
            }

            int index = 0;
            int heightOffset = 0;
            int index2 = 0;
            int heightOffset2 = 0;

            //menu base render
            for (Component subSetting : visibleColorSettings) {
                index2++;

                subSetting.x = startX;
                subSetting.y = y + heightOffset2;

                //color menu extensions
                if (ClickGUI.instance.colorDropMenuExtensions.getValue()) {
                    GlStateManager.disableAlpha();
                    int extendedGradientColor = new Color(ClickGUI.instance.extendedGradientColor.getValue().getColorColor().getRed(), ClickGUI.instance.extendedGradientColor.getValue().getColorColor().getGreen(), ClickGUI.instance.extendedGradientColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.extendedGradientColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.extendedGradientColor.getValue().getAlpha()).getRGB();
                    int colorDropMenuSideBarColor = new Color(ClickGUI.instance.colorDropMenuSideBarColor.getValue().getColorColor().getRed(), ClickGUI.instance.colorDropMenuSideBarColor.getValue().getColorColor().getGreen(), ClickGUI.instance.colorDropMenuSideBarColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.colorDropMenuSideBarColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.colorDropMenuSideBarColor.getValue().getAlpha()).getRGB();
                    int colorDropMenuOutlineColorColor = new Color(ClickGUI.instance.colorDropMenuOutlineColor.getValue().getColorColor().getRed(), ClickGUI.instance.colorDropMenuOutlineColor.getValue().getColorColor().getGreen(), ClickGUI.instance.colorDropMenuOutlineColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.colorDropMenuOutlineColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.colorDropMenuOutlineColor.getValue().getAlpha()).getRGB();

                    if (index2 == 1) {
                        if (!ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                            colorPanelGlowExtensions(subSetting, false, true);
                        }
                        RenderUtils2D.drawRect(subSetting.x, subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + width, subSetting.y, new Color(ClickGUI.instance.moduleBGColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleBGColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleBGColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleBGColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleBGColor.getValue().getAlpha()).getRGB());
                        RenderUtils2D.drawRect(subSetting.x + 1, subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 1, subSetting.x + width - 1, subSetting.y, new Color(ClickGUI.instance.extendedRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.extendedRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.extendedRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.extendedRectColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.extendedRectColor.getValue().getAlpha()).getRGB());
                        if (ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                            colorPanelGlowExtensions(subSetting, false, true);
                        }
                        if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                            GlStateManager.disableAlpha();
                            if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Left) {
                                RenderUtils2D.drawCustomRect(subSetting.x, subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), subSetting.y, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(),  new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                            else if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Right) {
                                RenderUtils2D.drawCustomRect(subSetting.x + width - ClickGUI.instance.moduleSideGlowWidth.getValue(), subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + width, subSetting.y, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB());
                            }
                            GlStateManager.enableAlpha();
                        }


                        if (ClickGUI.instance.extendedVerticalGradient.getValue()) {
                            RenderUtils2D.drawCustomRect(subSetting.x, subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + ClickGUI.instance.extendedGradientWidth.getValue(), subSetting.y, new Color(0, 0, 0, 0).getRGB(), extendedGradientColor, extendedGradientColor, new Color(0, 0, 0, 0).getRGB());
                        }
                        if (ClickGUI.instance.colorDropMenuSideBar.getValue()) {
                            RenderUtils2D.drawCustomLine(subSetting.x + (ClickGUI.instance.colorDropMenuSideBarWidth.getValue() / 2), subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + (ClickGUI.instance.colorDropMenuSideBarWidth.getValue() / 2), subSetting.y, ClickGUI.instance.colorDropMenuSideBarWidth.getValue(), colorDropMenuSideBarColor, colorDropMenuSideBarColor);
                        }
                        if (ClickGUI.instance.colorDropMenuOutline.getValue()) {
                            RenderUtils2D.drawCustomLine(subSetting.x + (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y, ClickGUI.instance.colorDropMenuOutlineWidth.getValue(), colorDropMenuOutlineColorColor, colorDropMenuOutlineColorColor);
                            RenderUtils2D.drawCustomLine(subSetting.x + width - (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + width - (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y, ClickGUI.instance.colorDropMenuOutlineWidth.getValue(), colorDropMenuOutlineColorColor, colorDropMenuOutlineColorColor);
                            RenderUtils2D.drawCustomLine(subSetting.x + (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), subSetting.x + width - (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue(), ClickGUI.instance.colorDropMenuOutlineWidth.getValue(), colorDropMenuOutlineColorColor, colorDropMenuOutlineColorColor);
                        }

                    }

                    if (index2 == visibleColorSettings.size()) {
                        if (!ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                            colorPanelGlowExtensions(subSetting, true, false);
                        }
                        RenderUtils2D.drawRect(subSetting.x, subSetting.y + height + 4, subSetting.x + width, subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, new Color(ClickGUI.instance.moduleBGColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleBGColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleBGColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleBGColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleBGColor.getValue().getAlpha()).getRGB());
                        RenderUtils2D.drawRect(subSetting.x + 1, subSetting.y + height + 4, subSetting.x + width - 1, subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 5, new Color(ClickGUI.instance.extendedRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.extendedRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.extendedRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.extendedRectColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.extendedRectColor.getValue().getAlpha()).getRGB());
                        if (ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                            colorPanelGlowExtensions(subSetting, true, false);
                        }
                        if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                            GlStateManager.disableAlpha();
                            if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Left) {
                                RenderUtils2D.drawCustomRect(subSetting.x, subSetting.y + height + 4, subSetting.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(),  new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                            }
                            else if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Right) {
                                RenderUtils2D.drawCustomRect(subSetting.x + width - ClickGUI.instance.moduleSideGlowWidth.getValue(), subSetting.y + height + 4, subSetting.x + width, subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB());
                            }
                            GlStateManager.enableAlpha();
                        }

                        if (ClickGUI.instance.extendedVerticalGradient.getValue()) {
                            RenderUtils2D.drawCustomRect(subSetting.x, subSetting.y + height + 4, subSetting.x + ClickGUI.instance.extendedGradientWidth.getValue(), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, new Color(0, 0, 0, 0).getRGB(), extendedGradientColor, extendedGradientColor, new Color(0, 0, 0, 0).getRGB());
                        }
                        if (ClickGUI.instance.colorDropMenuSideBar.getValue()) {
                            RenderUtils2D.drawCustomLine(subSetting.x + (ClickGUI.instance.colorDropMenuSideBarWidth.getValue() / 2), subSetting.y + height + 4, subSetting.x + (ClickGUI.instance.colorDropMenuSideBarWidth.getValue() / 2), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, ClickGUI.instance.colorDropMenuSideBarWidth.getValue(), colorDropMenuSideBarColor, colorDropMenuSideBarColor);
                        }
                        if (ClickGUI.instance.colorDropMenuOutline.getValue()) {
                            RenderUtils2D.drawCustomLine(subSetting.x + (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y + height + 4, subSetting.x + (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, ClickGUI.instance.colorDropMenuOutlineWidth.getValue(), colorDropMenuOutlineColorColor, colorDropMenuOutlineColorColor);
                            RenderUtils2D.drawCustomLine(subSetting.x + width - (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y + height + 4, subSetting.x + width - (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, ClickGUI.instance.colorDropMenuOutlineWidth.getValue(), colorDropMenuOutlineColorColor, colorDropMenuOutlineColorColor);
                            RenderUtils2D.drawCustomLine(subSetting.x + (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, subSetting.x + width - (ClickGUI.instance.colorDropMenuOutlineWidth.getValue() / 2), subSetting.y + height + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() + 4, ClickGUI.instance.colorDropMenuOutlineWidth.getValue(), colorDropMenuOutlineColorColor, colorDropMenuOutlineColorColor);
                        }

                    }

                    GlStateManager.enableAlpha();
                }


                subSetting.bottomRender(mouseX, mouseY, index2 == visibleColorSettings.size(), index2 == 1, partialTicks);


                heightOffset2 += (!(subSetting.equals(syncGlobalButton) || subSetting.equals(rainbowButton))) ? height + 4 : height;
            }

            //every other menu thing
            for (Component subSetting : visibleColorSettings) {
                index++;

                subSetting.x = startX;
                subSetting.y = y + heightOffset;

                //color menu top bottom gradients
                int topBottomGradientsColor = new Color(ClickGUI.instance.colorDropMenuTopBottomGradientsColor.getValue().getColorColor().getRed(), ClickGUI.instance.colorDropMenuTopBottomGradientsColor.getValue().getColorColor().getGreen(), ClickGUI.instance.colorDropMenuTopBottomGradientsColor.getValue().getColorColor().getBlue(), ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.colorDropMenuTopBottomGradientsColor.getValue().getAlpha() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.colorDropMenuTopBottomGradientsColor.getValue().getAlpha()).getRGB();
                if (index == 1 && ClickGUI.instance.colorDropMenuTopBottomGradients.getValue()) {
                    GlStateManager.disableAlpha();
                    RenderUtils2D.drawCustomRect(subSetting.x, y - (ClickGUI.instance.colorDropMenuExtensions.getValue() ? ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() : 0), subSetting.x + width, y + (ClickGUI.instance.colorDropMenuTopBottomGradientsHeight.getValue()) - (ClickGUI.instance.colorDropMenuExtensions.getValue() ? ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() : 0), topBottomGradientsColor, topBottomGradientsColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                    GlStateManager.enableAlpha();
                }
                if (index == visibleColorSettings.size() && ClickGUI.instance.colorDropMenuTopBottomGradients.getValue()) {
                    GlStateManager.disableAlpha();
                    RenderUtils2D.drawCustomRect(subSetting.x, subSetting.y + height + 4 - ClickGUI.instance.colorDropMenuTopBottomGradientsHeight.getValue() + (ClickGUI.instance.colorDropMenuExtensions.getValue() ? ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() : 0), subSetting.x + width, subSetting.y + height + 4 + (ClickGUI.instance.colorDropMenuExtensions.getValue() ? ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() : 0), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), topBottomGradientsColor, topBottomGradientsColor);
                    GlStateManager.enableAlpha();
                }

                subSetting.render(mouseX, mouseY, translateDelta, partialTicks);


                if ((mouseX >= Math.min(subSetting.x, subSetting.x + width) && mouseX <= Math.max(subSetting.x, subSetting.x + width) && mouseY >= Math.min(subSetting.y, subSetting.y + height) && mouseY <= Math.max(subSetting.y, subSetting.y + height)) && colorSomewhereClicked && Mouse.isButtonDown(0)) {
                    if (subSetting.equals(syncGlobalButton)) {
                        setting.getValue().setSyncGlobal(!setting.getValue().getSyncGlobal());
                    }
                    if (subSetting.equals(rainbowButton)) {
                        setting.getValue().setRainbow(!setting.getValue().getRainbow());
                    }

                    colorSomewhereClicked = !colorSomewhereClicked;
                }

                if ((mouseX >= Math.min(subSetting.x, subSetting.x + width) && mouseX <= Math.max(subSetting.x, subSetting.x + width) && mouseY >= Math.min(subSetting.y, subSetting.y + height) && mouseY <= Math.max(subSetting.y, subSetting.y + height)) && Mouse.isButtonDown(0)) {
                    if (subSetting.equals(rainbowSpeedSlider) && (!isSlidingRainbowSaturationSlider && !isSlidingRainbowBrightnessSlider && !isSlidingRedSlider && !isSlidingGreenSlider && !isSlidingBlueSlider && !isSlidingAlphaSlider)) {
                        isSlidingRainbowSpeedSlider = true;
                    }
                    if (subSetting.equals(rainbowSaturationSlider) && (!isSlidingRainbowSpeedSlider && !isSlidingRainbowBrightnessSlider && !isSlidingRedSlider && !isSlidingGreenSlider && !isSlidingBlueSlider && !isSlidingAlphaSlider)) {
                        isSlidingRainbowSaturationSlider = true;
                    }
                    if (subSetting.equals(rainbowBrightnessSlider) && (!isSlidingRainbowSpeedSlider && !isSlidingRainbowSaturationSlider && !isSlidingRedSlider && !isSlidingGreenSlider && !isSlidingBlueSlider && !isSlidingAlphaSlider)) {
                        isSlidingRainbowBrightnessSlider = true;
                    }
                    if (subSetting.equals(redSlider) && (!isSlidingRainbowSpeedSlider && !isSlidingRainbowSaturationSlider && !isSlidingRainbowBrightnessSlider && !isSlidingGreenSlider && !isSlidingBlueSlider && !isSlidingAlphaSlider)) {
                        isSlidingRedSlider = true;
                    }
                    if (subSetting.equals(greenSlider) && (!isSlidingRainbowSpeedSlider && !isSlidingRainbowSaturationSlider && !isSlidingRainbowBrightnessSlider && !isSlidingRedSlider && !isSlidingBlueSlider && !isSlidingAlphaSlider)) {
                        isSlidingGreenSlider = true;
                    }
                    if (subSetting.equals(blueSlider) && (!isSlidingRainbowSpeedSlider && !isSlidingRainbowSaturationSlider && !isSlidingRainbowBrightnessSlider && !isSlidingRedSlider && !isSlidingGreenSlider && !isSlidingAlphaSlider)) {
                        isSlidingBlueSlider = true;
                    }
                    if (subSetting.equals(alphaSlider) && (!isSlidingRainbowSpeedSlider && !isSlidingRainbowSaturationSlider && !isSlidingRainbowBrightnessSlider && !isSlidingRedSlider && !isSlidingGreenSlider && !isSlidingBlueSlider)) {
                        isSlidingAlphaSlider = true;
                    }
                }


                if (isSlidingRainbowSpeedSlider && !Mouse.isButtonDown(0)) {
                    isSlidingRainbowSpeedSlider = false;
                } if (isSlidingRainbowSaturationSlider && !Mouse.isButtonDown(0)) {
                    isSlidingRainbowSaturationSlider = false;
                } if (isSlidingRainbowBrightnessSlider && !Mouse.isButtonDown(0)) {
                    isSlidingRainbowBrightnessSlider = false;
                } if (isSlidingRedSlider && !Mouse.isButtonDown(0)) {
                    isSlidingRedSlider = false;
                } if (isSlidingGreenSlider && !Mouse.isButtonDown(0)) {
                    isSlidingGreenSlider = false;
                } if (isSlidingBlueSlider && !Mouse.isButtonDown(0)) {
                    isSlidingBlueSlider = false;
                } if (isSlidingAlphaSlider && !Mouse.isButtonDown(0)) {
                    isSlidingAlphaSlider = false;
                }

                setSlider(rainbowSpeedSetting, isSlidingRainbowSpeedSlider, mouseX, subSetting.x);
                setSlider(rainbowSaturationSetting, isSlidingRainbowSaturationSlider, mouseX, subSetting.x);
                setSlider(rainbowBrightnessSetting, isSlidingRainbowBrightnessSlider, mouseX, subSetting.x);
                setSlider(redSetting, isSlidingRedSlider, mouseX, subSetting.x);
                setSlider(greenSetting, isSlidingGreenSlider, mouseX, subSetting.x);
                setSlider(blueSetting, isSlidingBlueSlider, mouseX, subSetting.x);
                setSlider(alphaSetting, isSlidingAlphaSlider, mouseX, subSetting.x);

                heightOffset += (!(subSetting.equals(syncGlobalButton) || subSetting.equals(rainbowButton))) ? height + 4 : height;
            }

            if (ClickGUI.instance.colorDropMenuShadow.getValue()) {
                RenderUtils2D.drawBetterRoundRectFade(startX, ClickGUI.instance.colorDropMenuExtensions.getValue() ? y - ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() : y, endX, ClickGUI.instance.colorDropMenuExtensions.getValue() ? y + heightOffset + ClickGUI.instance.colorDropMenuExtensionsHeight.getValue() : y + heightOffset, ClickGUI.instance.colorDropMenuShadowSize.getValue(), 40.0f, false, false,false, new Color(0, 0, 0, ClickGUI.instance.colorDropMenuAnimate.getValue() ? (int)((ClickGUI.instance.colorDropMenuShadowAlpha.getValue() / 300.0f) * colorMenuToggleThreader) : ClickGUI.instance.colorDropMenuShadowAlpha.getValue()).getRGB());
            }

            startY = y + heightOffset;

            if (((!(mouseX >= Math.min(startX, endX) && mouseX <= Math.max(startX, endX) && mouseY >= Math.min(y, y + heightOffset) && mouseY <= Math.max(y, y + heightOffset)) && !(mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height)) && ((Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) || (Mouse.getEventButton() == 1 && Mouse.isButtonDown(1)))) || (mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height) && (Mouse.getEventButton() == 1 && Mouse.isButtonDown(1))))) {
                if (ClickGUI.instance.colorDropMenuAnimate.getValue()) {
                    reverseAnimationFlag = true;
                }
                else {
                    colorExpanded = false;
                    anyExpanded = false;
                }
            }
            if (ClickGUI.instance.isDisabled() && HUDEditor.instance.isDisabled()) {
                if (ClickGUI.instance.colorDropMenuAnimate.getValue()) {
                    colorMenuToggleThreader = 0;
                }
                colorExpanded = false;
                anyExpanded = false;
            }

            if (ClickGUI.instance.colorDropMenuAnimate.getValue() && reverseAnimationFlag) {
                if (colorMenuToggleThreader <= 0) {
                    reverseAnimationFlag = false;
                    colorExpanded = false;
                    anyExpanded = false;
                }
            }

            //scale animation shit
            if (ClickGUI.instance.colorDropMenuAnimate.getValue() && ClickGUI.instance.colorDropMenuAnimateScale.getValue()) {
                int colorMenuToggleThreaderScale = colorMenuToggleThreader;
                if (colorMenuToggleThreaderScale <= 1) {
                    colorMenuToggleThreaderScale = 1;
                }
                GL11.glScalef(300.0f / colorMenuToggleThreaderScale, 300.0f / colorMenuToggleThreaderScale, 0.0f);
                GL11.glTranslatef(startX * (1.0f - (colorMenuToggleThreaderScale / 300.0f)) * -1.0f, y * (1.0f - (colorMenuToggleThreaderScale / 300.0f)) * -1.0f, 0.0f);
            }
        }

        //more time system animation stuff
        if (ClickGUI.instance.colorDropMenuAnimate.getValue()) {
            lastTime = System.currentTimeMillis();
        }

        //color display
        Color theColor = new Color(setting.getValue().getColor());

        switch (ClickGUI.instance.colorDisplayShape.getValue()) {
            case Rect: {
                if (ClickGUI.instance.colorDisplayRounded.getValue()) {
                    if (ClickGUI.instance.colorDisplayOutline.getValue())
                        RenderUtils2D.drawCustomRoundedRectOutline(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplayRectWidth.getValue(), y + (height / 2.0f) - (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f), ClickGUI.instance.colorDisplayRoundedRadius.getValue(), ClickGUI.instance.colorDisplayOutlineWidth.getValue(), true, true, true, true, false, false, theColor.getRGB());
                    RenderUtils2D.drawRoundedRect(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplayRectWidth.getValue() + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) - (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f) + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), ClickGUI.instance.colorDisplayRoundedRadius.getValue(), x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) + (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f) - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), false, true, true, true, true, theColor.getRGB());
                }
                else {
                    if (ClickGUI.instance.colorDisplayOutline.getValue())
                        RenderUtils2D.drawRectOutline(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplayRectWidth.getValue(), y + (height / 2.0f) - (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f), ClickGUI.instance.colorDisplayOutlineWidth.getValue(), theColor.getRGB(), false, false);
                    RenderUtils2D.drawRect(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplayRectWidth.getValue() + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) - (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f) + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) + (ClickGUI.instance.colorDisplayRectHeight.getValue() / 2.0f) - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), theColor.getRGB());
                }
                break;
            }
            case Square: {
                if (ClickGUI.instance.colorDisplayRounded.getValue()) {
                    if (ClickGUI.instance.colorDisplayOutline.getValue())
                        RenderUtils2D.drawCustomRoundedRectOutline(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue(), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), ClickGUI.instance.colorDisplayRoundedRadius.getValue(), ClickGUI.instance.colorDisplayOutlineWidth.getValue(), true, true, true, true, false, false, theColor.getRGB());
                    RenderUtils2D.drawRoundedRect(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue() + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), ClickGUI.instance.colorDisplayRoundedRadius.getValue(), x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), false, true, true, true, true, theColor.getRGB());
                }
                else {
                    if (ClickGUI.instance.colorDisplayOutline.getValue())
                        RenderUtils2D.drawRectOutline(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue(), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), ClickGUI.instance.colorDisplayOutlineWidth.getValue(), theColor.getRGB(), false, false);
                    RenderUtils2D.drawRect(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue() + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), theColor.getRGB());
                }
                break;
            }
            case Diamond: {
                GL11.glTranslatef(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), y + (height / 2.0f), 0.0f);
                GL11.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f)) * -1.0f, (y + (height / 2.0f)) * -1.0f, 0.0f);
                if (ClickGUI.instance.colorDisplayRounded.getValue()) {
                    if (ClickGUI.instance.colorDisplayOutline.getValue())
                        RenderUtils2D.drawCustomRoundedRectOutline(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue(), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), ClickGUI.instance.colorDisplayRoundedRadius.getValue(), ClickGUI.instance.colorDisplayOutlineWidth.getValue(), true, true, true, true, false, false, theColor.getRGB());
                    RenderUtils2D.drawRoundedRect(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue() + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), ClickGUI.instance.colorDisplayRoundedRadius.getValue(), x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), false, true, true, true, true, theColor.getRGB());
                }
                else {
                    if (ClickGUI.instance.colorDisplayOutline.getValue())
                        RenderUtils2D.drawRectOutline(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue(), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue(), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), ClickGUI.instance.colorDisplayOutlineWidth.getValue(), theColor.getRGB(), false, false);
                    RenderUtils2D.drawRect(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - ClickGUI.instance.colorDisplaySize.getValue() + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) + (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), y + (height / 2.0f) + (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f) - (ClickGUI.instance.colorDisplayOutline.getValue() ? ClickGUI.instance.colorDisplayOutlineOffset.getValue() : 0.0f), theColor.getRGB());
                }
                GL11.glTranslatef(x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f), y + (height / 2.0f), 0.0f);
                GL11.glRotatef(-45.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + width - ClickGUI.instance.colorDisplayXOffset.getValue() - (ClickGUI.instance.colorDisplaySize.getValue() / 2.0f)) * -1.0f, (y + (height / 2.0f)) * -1.0f, 0.0f);
                break;
            }
        }

        if (syncGlobalSetting.getValue()) {
            setting.getValue().setColor(ClickGUI.instance.globalColor.getValue().getColor());
        }
        else {
            if (rainbowSetting.getValue()) {
                Color lgbtq = new Color(ColorUtil.getBetterRainbow((float)rainbowSpeedSetting.getValue(), (float)rainbowSaturationSetting.getValue(), (float)rainbowBrightnessSetting.getValue()));
                setting.getValue().setColor(lgbtq.getRGB());
            }
            else {
                setting.getValue().setColor(new Color((int)redSetting.getValue(), (int)greenSetting.getValue(), (int)blueSetting.getValue(), (int)alphaSetting.getValue()).getRGB());
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
        if (!colorExpanded) {
            if (!setting.isVisible() || !isHovered(mouseX, mouseY))
                return false;
            if (mouseButton == 0) {
                anyExpanded = !anyExpanded;
                colorExpanded = !colorExpanded;
                SoundUtil.playButtonClick();
            }
        }
        else {
            if (mouseButton == 0 && Mouse.isButtonDown(0)) {
                colorSomewhereClicked = !colorSomewhereClicked;
            }
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
