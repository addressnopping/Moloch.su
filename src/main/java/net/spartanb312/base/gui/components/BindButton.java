package net.spartanb312.base.gui.components;

import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.core.common.KeyBind;
import net.spartanb312.base.utils.SoundUtil;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class BindButton extends net.spartanb312.base.gui.Component {

    Setting<KeyBind> setting;
    boolean accepting = false;
    String moduleName;

    public BindButton(Setting<KeyBind> setting, int width, int height, Panel father, Module module) {
        this.setting = setting;
        this.width = width;
        this.height = height;
        this.father = father;
        moduleName = module.name;
    }

    public static HashMap<String, Integer> storedBindWaitingLoops = new HashMap<>();
    public static String staticString = "";

    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        Color textBindColor = new Color(ClickGUI.instance.bindButtonTextColor.getValue().getColorColor().getRed(), ClickGUI.instance.bindButtonTextColor.getValue().getColorColor().getGreen(), ClickGUI.instance.bindButtonTextColor.getValue().getColorColor().getBlue(), ClickGUI.instance.bindButtonTextColor.getValue().getAlpha());

        if (ClickGUI.instance.bindButtonFancy.getValue()) {
            Color keyRectColor = new Color(ClickGUI.instance.bindButtonKeyColor.getValue().getColorColor().getRed(), ClickGUI.instance.bindButtonKeyColor.getValue().getColorColor().getGreen(), ClickGUI.instance.bindButtonKeyColor.getValue().getColorColor().getBlue(), ClickGUI.instance.bindButtonTextColor.getValue().getAlpha());
            Color keyStrColor = new Color(ClickGUI.instance.bindButtonKeyStringColor.getValue().getColorColor().getRed(), ClickGUI.instance.bindButtonKeyStringColor.getValue().getColorColor().getGreen(), ClickGUI.instance.bindButtonKeyStringColor.getValue().getColorColor().getBlue(), ClickGUI.instance.bindButtonKeyStringColor.getValue().getAlpha());

            Color keyRectWaitingColor = new Color(ClickGUI.instance.bindButtonFancyWaitingRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.bindButtonFancyWaitingRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.bindButtonFancyWaitingRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.bindButtonFancyWaitingRectColor.getValue().getAlpha());

            //"bind"
            if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
                GL11.glEnable(GL_TEXTURE_2D);
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f), textBindColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
                GL11.glDisable(GL_TEXTURE_2D);
            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                if (CustomFont.instance.textShadow.getValue()) {
                    fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, textBindColor.getRGB());
                }
                else {
                    fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, textBindColor.getRGB());
                }

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }

            //bind rect
            float rectWidth;
            if ((setting.getValue().getKeyCode() == Keyboard.KEY_RSHIFT || setting.getValue().getKeyCode() == Keyboard.KEY_LSHIFT || setting.getValue().getKeyCode() == Keyboard.KEY_RETURN || setting.getValue().getKeyCode() == Keyboard.KEY_SPACE || setting.getValue().getKeyCode() == Keyboard.KEY_CAPITAL || setting.getValue().getKeyCode() == Keyboard.KEY_TAB || setting.getValue().getKeyCode() == Keyboard.KEY_GRAVE || setting.getValue().getKeyCode() == Keyboard.KEY_INSERT || setting.getValue().getKeyCode() == Keyboard.KEY_RBRACKET || setting.getValue().getKeyCode() == Keyboard.KEY_LBRACKET || setting.getValue().getKeyCode() == Keyboard.KEY_SEMICOLON || setting.getValue().getKeyCode() == Keyboard.KEY_COLON || setting.getValue().getKeyCode() == Keyboard.KEY_ESCAPE || setting.getValue().getKeyCode() == Keyboard.KEY_LCONTROL || setting.getValue().getKeyCode() == Keyboard.KEY_RCONTROL || setting.getValue().getKeyCode() == Keyboard.KEY_MINUS || setting.getValue().getKeyCode() == Keyboard.KEY_EQUALS || setting.getValue().getKeyCode() == Keyboard.KEY_LMENU || setting.getValue().getKeyCode() == Keyboard.KEY_RMENU || setting.getValue().getKeyCode() == Keyboard.KEY_NUMLOCK || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD0 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD1 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD2 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD3 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD4 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD5 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD6 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD7 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD8 || setting.getValue().getKeyCode() == Keyboard.KEY_NUMPAD9 || setting.getValue().getKeyCode() == Keyboard.KEY_PERIOD || setting.getValue().getKeyCode() == Keyboard.KEY_DIVIDE || setting.getValue().getKeyCode() == Keyboard.KEY_COMMA || setting.getValue().getKeyCode() == Keyboard.KEY_SLASH || setting.getValue().getKeyCode() == Keyboard.KEY_BACKSLASH || setting.getValue().getKeyCode() == Keyboard.KEY_ADD || setting.getValue().getKeyCode() == Keyboard.KEY_SUBTRACT || setting.getValue().getKeyCode() == Keyboard.KEY_MULTIPLY || setting.getValue().getKeyCode() == Keyboard.KEY_HOME || setting.getValue().getKeyCode() == Keyboard.KEY_END || setting.getValue().getKeyCode() == Keyboard.KEY_PRIOR || setting.getValue().getKeyCode() == Keyboard.KEY_NEXT || setting.getValue().getKeyCode() == Keyboard.KEY_SYSRQ)) {
                rectWidth = FontManager.getKeyBindWidth(Keyboard.getKeyName(setting.getValue().getKeyCode())) + 2;
            }
            else {
                rectWidth = 9;
            }
            if (ClickGUI.instance.bindButtonFancyOutline.getValue()) {
                if (ClickGUI.instance.bindButtonFancyRounded.getValue()) {
                    RenderUtils2D.drawCustomRoundedRectOutline(x + width - 3 - rectWidth, y + (height / 2.0f) - 4.5f, x + width - 3, y + (height / 2.0f) + 4.5f, ClickGUI.instance.bindButtonFancyRoundedRadius.getValue(), ClickGUI.instance.bindButtonFancyOutlineWidth.getValue(), true, true, true, true, false, false, keyRectColor.getRGB());
                    RenderUtils2D.drawRoundedRect(x + width - 3 - rectWidth + (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f), y + (height / 2.0f) - 4.5f + (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f), ClickGUI.instance.bindButtonFancyRoundedRadius.getValue(), x + width - 3 - ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f, y + (height / 2.0f) + 4.5f - ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f, false, true, true, true, true, keyRectColor.getRGB());
                }
                else {
                    RenderUtils2D.drawRectOutline(x + width - 3 - rectWidth, y + (height / 2.0f) - 4.5f, x + width - 3, y + (height / 2.0f) + 4.5f, ClickGUI.instance.bindButtonFancyOutlineWidth.getValue(), keyRectColor.getRGB(), false, false);
                    RenderUtils2D.drawRect(x + width - 3 - rectWidth + (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f), y + (height / 2.0f) - 4.5f + (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f), x + width - 3 - ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f, y + (height / 2.0f) + 4.5f - ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f, keyRectColor.getRGB());
                }
            }
            else {
                if (ClickGUI.instance.bindButtonFancyRounded.getValue()) {
                    RenderUtils2D.drawRoundedRect(x + width - 3 - rectWidth, y + (height / 2.0f) - 4.5f, ClickGUI.instance.bindButtonFancyRoundedRadius.getValue(), x + width - 3, y + (height / 2.0f) + 4.5f, false, true, true, true, true, keyRectColor.getRGB());
                }
                else {
                    RenderUtils2D.drawRect(x + width - 3 - rectWidth, y + (height / 2.0f) - 4.5f, x + width - 3, y + (height / 2.0f) + 4.5f, keyRectColor.getRGB());
                }
            }

            if ((ClickGUI.instance.bindButtonFancyWaitingAnimate.getValue() && ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() != ClickGUI.BindButtonColoredRectAnimateMode.None) || (ClickGUI.instance.bindButtonFancyWaitingDots.getValue() && ClickGUI.instance.bindButtonFancyWaitingAnimate.getValue())) {
                storedBindWaitingLoops.putIfAbsent(setting.getName() + moduleName, 0);
                int animateLoops = storedBindWaitingLoops.get(setting.getName() + moduleName);

                if (accepting) {
                    animateLoops += ClickGUI.instance.bindButtonFancyWaitingAnimateFactor.getValue() * 10;
                }
                else {
                    animateLoops -= ClickGUI.instance.bindButtonFancyWaitingAnimateFactor.getValue() * 10;
                }

                if (animateLoops >= 300) {
                    animateLoops = 300;
                }
                if (animateLoops <= 0) {
                    animateLoops = 0;
                }
                storedBindWaitingLoops.put(setting.getName() + moduleName, animateLoops);
            }

            //bind rect waiting colored rect
            if (ClickGUI.instance.bindButtonFancyWaitingRect.getValue()) {
                if (ClickGUI.instance.bindButtonFancyWaitingAnimate.getValue() && ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() != ClickGUI.BindButtonColoredRectAnimateMode.None) {

                    if (ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Alpha || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both) {
                        keyRectWaitingColor = new Color(keyRectWaitingColor.getRed(), keyRectWaitingColor.getGreen(), keyRectWaitingColor.getBlue(), (int)((ClickGUI.instance.bindButtonFancyWaitingRectColor.getValue().getAlpha() / 300.0f) * storedBindWaitingLoops.get(setting.getName() + moduleName)));
                    }

                    float rightX = x + width - 3 - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0);
                    float leftX = x + width - 3 + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0);
                    if (ClickGUI.instance.bindButtonFancyRounded.getValue()) {
                        RenderUtils2D.drawRoundedRect(ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (leftX - (rectWidth / 2.0f) -  (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((rectWidth / 2.0f) / 300.0f))) : (leftX - rectWidth), ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (y + (height / 2.0f) - (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)) / 300.0f))) : (y + (height / 2.0f) - 4.5f + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)), ClickGUI.instance.bindButtonFancyRoundedRadius.getValue(), ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (rightX - (rectWidth / 2.0f) + (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((rectWidth / 2.0f) / 300.0f))) : rightX, ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (y + (height / 2.0f) + (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)) / 300.0f))) : (y + (height / 2.0f) + 4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)), false, true, true, true, true, keyRectWaitingColor.getRGB());
                    }
                    else {
                        RenderUtils2D.drawRect(ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (leftX - (rectWidth / 2.0f) - (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((rectWidth / 2.0f) / 300.0f))) : (leftX - rectWidth), ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (y + (height / 2.0f) - (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)) / 300.0f))) : (y + (height / 2.0f) - 4.5f + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)), ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (rightX - (rectWidth / 2.0f) + (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((rectWidth / 2.0f) / 300.0f))) : rightX, ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Scale || ClickGUI.instance.bindButtonColoredRectAnimateMode.getValue() == ClickGUI.BindButtonColoredRectAnimateMode.Both ? (y + (height / 2.0f) + (storedBindWaitingLoops.get(setting.getName() + moduleName) * ((4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)) / 300.0f))) : (y + (height / 2.0f) + 4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0)), keyRectWaitingColor.getRGB());
                    }

                }
                else if (!ClickGUI.instance.bindButtonFancyWaitingAnimate.getValue() && accepting) {
                    if (ClickGUI.instance.bindButtonFancyRounded.getValue()) {
                        RenderUtils2D.drawRoundedRect(x + width - 3 - rectWidth + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), y + (height / 2.0f) - 4.5f + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), ClickGUI.instance.bindButtonFancyRoundedRadius.getValue(), x + width - 3 - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), y + (height / 2.0f) + 4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), false, true, true, true, true, keyRectWaitingColor.getRGB());
                    }
                    else {
                        RenderUtils2D.drawRect(x + width - 3 - rectWidth + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), y + (height / 2.0f) - 4.5f + ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), x + width - 3 - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), y + (height / 2.0f) + 4.5f - ((ClickGUI.instance.bindButtonFancyOutline.getValue()) ? (ClickGUI.instance.bindButtonFancyOutlineOffset.getValue() / 2.0f) : 0), keyRectWaitingColor.getRGB());
                    }
                }
            }

            //bind waiting dots
            if (ClickGUI.instance.bindButtonFancyWaitingDots.getValue()) {
                Color waitingDotsColor1 = ClickGUI.instance.bindButtonFancyWaitingDotsColor.getValue().getColorColor();
                Color waitingDotsColor = new Color(waitingDotsColor1.getRed(), waitingDotsColor1.getGreen(), waitingDotsColor1.getBlue(), ClickGUI.instance.bindButtonFancyWaitingDotsColor.getValue().getAlpha());


                if (ClickGUI.instance.bindButtonFancyWaitingAnimate.getValue()) {
                    waitingDotsColor = new Color(waitingDotsColor.getRed(), waitingDotsColor.getGreen(), waitingDotsColor.getBlue(), (int)((ClickGUI.instance.bindButtonFancyWaitingDotsColor.getValue().getAlpha() / 300.0f) * storedBindWaitingLoops.get(setting.getName() + moduleName)));
                    Color waitingDotsColorAlphaSave = waitingDotsColor;

                    if (ClickGUI.instance.bindButtonFancyWaitingDotsRollingBrightnessAnimate.getValue()) {
                        waitingDotsColor = ColorUtil.rolledBrightness(waitingDotsColor, ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateMaxBright.getValue(), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateMinBright.getValue(), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateFactor.getValue(), x + width - 3 + (ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue() + ClickGUI.instance.bindButtonFancyWaitingDotsGap.getValue()) - (rectWidth / 2.0f), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateRollLength.getValue() / 2.0f, ClickGUI.instance.bindButtonFancyWaitingDotsRollingBrightnessRollDirection.getValue() == ClickGUI.BindButtonWaitingDotsRolledBrightnessDirection.Right, false);
                    }

                    RenderUtils2D.drawCircle(x + width - 3 + (ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue() + ClickGUI.instance.bindButtonFancyWaitingDotsGap.getValue()) - (rectWidth / 2.0f), y + (height / 2.0f) + 2, ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue(), new Color(waitingDotsColor.getRed(), waitingDotsColor.getGreen(), waitingDotsColor.getBlue(), waitingDotsColorAlphaSave.getAlpha()).getRGB());

                    if (ClickGUI.instance.bindButtonFancyWaitingDotsRollingBrightnessAnimate.getValue()) {
                        waitingDotsColor = ColorUtil.rolledBrightness(waitingDotsColor, ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateMaxBright.getValue(), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateMinBright.getValue(), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateFactor.getValue(), x + width - 3 - (rectWidth / 2.0f), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateRollLength.getValue() / 2.0f, ClickGUI.instance.bindButtonFancyWaitingDotsRollingBrightnessRollDirection.getValue() == ClickGUI.BindButtonWaitingDotsRolledBrightnessDirection.Right, false);
                    }

                    RenderUtils2D.drawCircle(x + width - 3 - (rectWidth / 2.0f), y + (height / 2.0f) + 2, ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue(), new Color(waitingDotsColor.getRed(), waitingDotsColor.getGreen(), waitingDotsColor.getBlue(), waitingDotsColorAlphaSave.getAlpha()).getRGB());

                    if (ClickGUI.instance.bindButtonFancyWaitingDotsRollingBrightnessAnimate.getValue()) {
                        waitingDotsColor = ColorUtil.rolledBrightness(waitingDotsColor, ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateMaxBright.getValue(), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateMinBright.getValue(), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateFactor.getValue(), x + width - 3 - (ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue() + ClickGUI.instance.bindButtonFancyWaitingDotsGap.getValue()) - (rectWidth / 2.0f), ClickGUI.instance.bindButtonFancyWaitingAnimateDotsRollingBrightnessAnimateRollLength.getValue() / 2.0f, ClickGUI.instance.bindButtonFancyWaitingDotsRollingBrightnessRollDirection.getValue() == ClickGUI.BindButtonWaitingDotsRolledBrightnessDirection.Right, false);
                    }

                    RenderUtils2D.drawCircle(x + width - 3 - (ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue() + ClickGUI.instance.bindButtonFancyWaitingDotsGap.getValue()) - (rectWidth / 2.0f), y + (height / 2.0f) + 2, ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue(), new Color(waitingDotsColor.getRed(), waitingDotsColor.getGreen(), waitingDotsColor.getBlue(), waitingDotsColorAlphaSave.getAlpha()).getRGB());

                }
                else if (!ClickGUI.instance.bindButtonFancyWaitingAnimate.getValue() && accepting) {

                    RenderUtils2D.drawCircle(x + width - 3 + (ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue() + ClickGUI.instance.bindButtonFancyWaitingDotsGap.getValue()) - (rectWidth / 2.0f), y + (height / 2.0f) + 2, ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue(), waitingDotsColor.getRGB());
                    RenderUtils2D.drawCircle(x + width - 3 - (rectWidth / 2.0f), y + (height / 2.0f) + 2, ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue(), waitingDotsColor.getRGB());
                    RenderUtils2D.drawCircle(x + width - 3 - (ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue() + ClickGUI.instance.bindButtonFancyWaitingDotsGap.getValue()) - (rectWidth / 2.0f), y + (height / 2.0f) + 2, ClickGUI.instance.bindButtonFancyWaitingDotsRadius.getValue(), waitingDotsColor.getRGB());

                }
            }

            //bind key str
            if (!(setting.getValue().getKeyCode() == Keyboard.KEY_NONE || accepting)) {
                GL11.glTranslatef((x + width - 3 - rectWidth + 2 + ClickGUI.instance.bindButtonKeyStrX.getValue()) * (1.0f - ClickGUI.instance.bindButtonKeyStrScale.getValue()), (y + (height / 2.0f) + ClickGUI.instance.bindButtonKeyStrY.getValue()) * (1.0f - ClickGUI.instance.bindButtonKeyStrScale.getValue()), 0.0f);
                GL11.glScalef(ClickGUI.instance.bindButtonKeyStrScale.getValue(), ClickGUI.instance.bindButtonKeyStrScale.getValue(), ClickGUI.instance.bindButtonKeyStrScale.getValue());
                if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Minecraft) {
                    GL11.glEnable(GL_TEXTURE_2D);
                    mc.fontRenderer.drawString(Keyboard.getKeyName(setting.getValue().getKeyCode()), x + width - 3 - rectWidth + 2 + ClickGUI.instance.bindButtonKeyStrX.getValue(), y + (height / 2.0f) + ClickGUI.instance.bindButtonKeyStrY.getValue(), keyStrColor.getRGB(), false);
                    GL11.glDisable(GL_TEXTURE_2D);
                }
                else {
                    fontManager.drawKeyBind(Keyboard.getKeyName(setting.getValue().getKeyCode()), x + width - 3 - rectWidth + 2 + ClickGUI.instance.bindButtonKeyStrX.getValue(), y + (height / 2.0f) + ClickGUI.instance.bindButtonKeyStrY.getValue(), keyStrColor.getRGB());
                }
                GL11.glScalef(1.0f / ClickGUI.instance.bindButtonKeyStrScale.getValue(), 1.0f / ClickGUI.instance.bindButtonKeyStrScale.getValue(), 1.0f / ClickGUI.instance.bindButtonKeyStrScale.getValue());
                GL11.glTranslatef((x + width - 3 - rectWidth + 2 + ClickGUI.instance.bindButtonKeyStrX.getValue()) * (1.0f - ClickGUI.instance.bindButtonKeyStrScale.getValue()) * -1.0f, (y + (height / 2.0f) + ClickGUI.instance.bindButtonKeyStrY.getValue()) * (1.0f - ClickGUI.instance.bindButtonKeyStrScale.getValue()) * -1.0f, 0.0f);
            }

        }
        else {

            if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
                GL11.glEnable(GL_TEXTURE_2D);
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                mc.fontRenderer.drawString(accepting ? (setting.getName() + " | ...") : setting.getName() + " | " + (setting.getValue().getKeyCode() == 0x00 ? "NONE" : Keyboard.getKeyName(setting.getValue().getKeyCode())),
                        x + 5, (int) (y + height / 2 - font.getHeight() / 2f), textBindColor.getRGB(), CustomFont.instance.textShadow.getValue());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
                GL11.glDisable(GL_TEXTURE_2D);
            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                if (CustomFont.instance.textShadow.getValue()) {
                    fontManager.drawShadow(accepting ? (setting.getName() + " | ...") : setting.getName() + " | " + (setting.getValue().getKeyCode() == 0x00 ? "NONE" : Keyboard.getKeyName(setting.getValue().getKeyCode())),
                            x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, textBindColor.getRGB());
                }
                else {
                    fontManager.draw(accepting ? (setting.getName() + " | ...") : setting.getName() + " | " + (setting.getValue().getKeyCode() == 0x00 ? "NONE" : Keyboard.getKeyName(setting.getValue().getKeyCode())),
                            x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, textBindColor.getRGB());
                }

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }

        }

        GlStateManager.enableAlpha();
    }

    @Override
    public void bottomRender(int mouseX, int mouseY, boolean lastSetting, boolean firstSetting, float partialTicks) {
        GlStateManager.disableAlpha();
        drawSettingRects(lastSetting, false);

        //Top Border Rects
        if (ClickGUI.instance.extendedCategoryBar.getValue() && ClickGUI.instance.extendedTopBars.getValue() && firstSetting) {
            RenderUtils2D.drawRect(x - (ClickGUI.instance.extendedCategoryBarXScale.getValue() / 2) + (ClickGUI.instance.extendedCategoryBarX.getValue()), y - (ClickGUI.instance.extendedCategoryBarYScale.getValue() / 2) + (ClickGUI.instance.extendedCategoryBarY.getValue()), x + width + (ClickGUI.instance.extendedCategoryBarXScale.getValue() / 2) + (ClickGUI.instance.extendedCategoryBarX.getValue()), y + (ClickGUI.instance.extendedCategoryBarYScale.getValue() / 2) + (ClickGUI.instance.extendedCategoryBarY.getValue()), ClickGUI.instance.extendedBarColor.getValue().getColor());
        }

        drawExtendedGradient(lastSetting, false);
        drawExtendedLine(lastSetting);

        renderHoverRect(moduleName + setting.getName(), mouseX, mouseY, 2.0f, -1.0f, false);

        GlStateManager.enableAlpha();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (accepting) {
            if (keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE) {
                setting.getValue().setKeyCode(Keyboard.KEY_NONE);
            } else {
                setting.getValue().setKeyCode(keyCode);
            }
            accepting = false;
        }
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
        if (!isHovered(mouseX, mouseY))
            return false;

        if (mouseButton == 0) {
            accepting = true;
            SoundUtil.playButtonClick();
        }
        return true;

    }

    @Override
    public String getDescription() {
        return setting.getDescription();
    }

}
