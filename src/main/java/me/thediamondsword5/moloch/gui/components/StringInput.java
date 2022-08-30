package me.thediamondsword5.moloch.gui.components;

import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.core.setting.settings.StringSetting;
import net.spartanb312.base.gui.Component;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class StringInput extends Component {

    Setting<String> setting;
    String moduleName;
    boolean isOverflowing = false;
    Timer typeTimer = new Timer();
    Timer animationTimer = new Timer();
    Timer typingMarkerTimer = new Timer();
    Timer backspaceTimer = new Timer();
    Timer backspaceDelayTimer = new Timer();
    boolean backspaceFlag = false;
    Timer deleteTimer = new Timer();
    Timer deleteDelayTimer = new Timer();
    boolean deleteFlag = false;
    boolean showTypingMarker = true;
    float animationAlpha = 0.0f;
    int typingMarkerOffset = 0;
    float prevTextWidth = 0.0f;
    float typingMarkerInterpDelta = 300.0f;
    boolean typingMarkerInterpFlag = false;

    public static StringInput INSTANCE;

    public StringInput(Setting<String> setting, int width, int height, Panel father, Module module) {
        this.width = width;
        this.height = height + 14;
        this.father = father;
        this.setting = setting;
        this.moduleName = module.name;
        INSTANCE = this;
    }

    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        if (((StringSetting)setting).listening) {
            if (backspaceFlag && Keyboard.isKeyDown(Keyboard.KEY_BACK) && backspaceTimer.passed(700) && backspaceDelayTimer.passed(2) && setting.getValue().length() >= 1) {
                setting.setValue(new StringBuilder(setting.getValue()).replace(setting.getValue().length() - typingMarkerOffset - 1, setting.getValue().length() - typingMarkerOffset, "").toString());
                backspaceDelayTimer.reset();
            }

            if (!Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
                backspaceFlag = false;
            }


            if (deleteFlag && Keyboard.isKeyDown(Keyboard.KEY_DELETE) && deleteTimer.passed(700) && deleteDelayTimer.passed(2) && setting.getValue().length() >= 1) {
                setting.setValue(new StringBuilder(setting.getValue()).replace(setting.getValue().length() - typingMarkerOffset, setting.getValue().length() - typingMarkerOffset + 1, "").toString());
                typingMarkerOffset--;
                if (typingMarkerOffset < 0) typingMarkerOffset = 0;
                deleteDelayTimer.reset();
            }

            if (!Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
                deleteFlag = false;
            }
        }

        int passedms = (int) animationTimer.hasPassed();
        animationTimer.reset();
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
            ((StringSetting)setting).listening = false;
            isTyping = false;
        }
        if (!((StringSetting)setting).listening && typingMarkerOffset != 0) {
            typingMarkerOffset = 0;
        }
        isOverflowing = (FontManager.getWidth(setting.getValue()) * ClickGUI.instance.stringInputValueScale.getValue()) + x + 5 > x + width - 7;

        RenderUtils2D.drawRect(x + 4, y + (height / 2.0f) + 2, x + width - 4, y + height - 2, ClickGUI.instance.stringInputBoxColor.getValue().getColor());

        float settingNameX = x + 5;
        float textY = y + (font.getHeight() / 2.0f) + ClickGUI.instance.stringInputNameOffset.getValue();

        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.getName(), settingNameX, textY, ClickGUI.instance.stringInputNameColor.getValue().getColor(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()), (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());
            if (CustomFont.instance.textShadow.getValue()) {
                FontManager.drawShadow(setting.getName(), settingNameX, textY, ClickGUI.instance.stringInputNameColor.getValue().getColor());
            }
            else {
                FontManager.draw(setting.getName(), settingNameX, textY, ClickGUI.instance.stringInputNameColor.getValue().getColor());
            }
            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((settingNameX) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, (textY) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
        }

        if (isOverflowing) {
            GL11.glTranslatef(-((FontManager.getWidth(setting.getValue()) * ClickGUI.instance.stringInputValueScale.getValue()) + x + 8 - (x + width - 4)), 0.0f, 0.0f);
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glScissor((x + 4) * scaledResolution.getScaleFactor(), (int)(mc.displayHeight - (ClickGUI.instance.guiMove.getValue() ? (mc.displayHeight - (translateDelta * scaledResolution.getScaleFactor())) : 0) - ((y + (height / 2.0f) + 2) * scaledResolution.getScaleFactor()) - (((height / 2.0f) - 4) * scaledResolution.getScaleFactor())), (width - 8) * scaledResolution.getScaleFactor(), (int)((height / 2.0f) - 4) * scaledResolution.getScaleFactor());
        GL11.glEnable(GL_SCISSOR_TEST);

        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((x + 6) * (1.0f - ClickGUI.instance.stringInputValueScale.getValue()), (y + (height * 0.75f) - 2) * (1.0f - ClickGUI.instance.stringInputValueScale.getValue()), 0.0f);
            GL11.glScalef(ClickGUI.instance.stringInputValueScale.getValue(), ClickGUI.instance.stringInputValueScale.getValue(), 1.0f);

            mc.fontRenderer.drawString(setting.getValue(), x + 6, y + (height * 0.75f) - 2, ClickGUI.instance.stringInputValueColor.getValue().getColor(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / ClickGUI.instance.stringInputValueScale.getValue(), 1.0f / ClickGUI.instance.stringInputValueScale.getValue(), 1.0f);
            GL11.glTranslatef((x + 6) * -(1.0f - ClickGUI.instance.stringInputValueScale.getValue()), (y + (height * 0.75f) - 2) * -(1.0f - ClickGUI.instance.stringInputValueScale.getValue()), 0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            GL11.glTranslatef((x + 6) * (1.0f - ClickGUI.instance.stringInputValueScale.getValue()), (y + (height * 0.75f)) * (1.0f - ClickGUI.instance.stringInputValueScale.getValue()), 0.0f);
            GL11.glScalef(ClickGUI.instance.stringInputValueScale.getValue(), ClickGUI.instance.stringInputValueScale.getValue(), 1.0f);
            if (CustomFont.instance.textShadow.getValue()) {
                FontManager.drawShadow(setting.getValue(), x + 6, y + (height * 0.75f), ClickGUI.instance.stringInputValueColor.getValue().getColor());
            }
            else {
                FontManager.draw(setting.getValue(), x + 6, y + (height * 0.75f), ClickGUI.instance.stringInputValueColor.getValue().getColor());
            }
            GL11.glScalef(1.0f / ClickGUI.instance.stringInputValueScale.getValue(), 1.0f / ClickGUI.instance.stringInputValueScale.getValue(), 1.0f);
            GL11.glTranslatef((x + 6) * -(1.0f - ClickGUI.instance.stringInputValueScale.getValue()), (y + (height * 0.75f)) * -(1.0f - ClickGUI.instance.stringInputValueScale.getValue()), 0.0f);
        }
        GL11.glDisable(GL_SCISSOR_TEST);

        if (((StringSetting)setting).listening) {
            if (typingMarkerInterpFlag && passedms < 1000) {
                typingMarkerInterpDelta += passedms * 1.5f;
            }
            if (typingMarkerInterpDelta > 300) {
                typingMarkerInterpDelta = 300;
            }

            if (showTypingMarker) {
                RenderUtils2D.drawRect(((MathUtilFuckYou.linearInterp(prevTextWidth, FontManager.getWidth(setting.getValue().substring(0, setting.getValue().length() - typingMarkerOffset)), typingMarkerInterpDelta)) * ClickGUI.instance.stringInputValueScale.getValue()) + x + 6, y + (height / 2.0f) + 3, ((MathUtilFuckYou.linearInterp(prevTextWidth, FontManager.getWidth(setting.getValue().substring(0, setting.getValue().length() - typingMarkerOffset)), typingMarkerInterpDelta)) * ClickGUI.instance.stringInputValueScale.getValue()) + x + 6.5f, y + height - 3, ClickGUI.instance.stringInputTypingMarkColor.getValue().getColor());
            }
        }

        if (isOverflowing) {
            GL11.glTranslatef((FontManager.getWidth(setting.getValue()) * ClickGUI.instance.stringInputValueScale.getValue()) + x + 8 - (x + width - 4), 0.0f, 0.0f);

            if (passedms < 1000) {
                animationAlpha += passedms * 4.0f / 10.0f;
            }
        }
        else {
            if (passedms < 1000) {
                animationAlpha -= passedms * 4.0f / 10.0f;
            }
        }

        if (animationAlpha > 300.0f) {
            animationAlpha = 300.0f;
        }
        else if (animationAlpha < 0.0f) {
            animationAlpha = 0.0f;
        }

        if (typingMarkerTimer.passed(500)) {
            showTypingMarker = !showTypingMarker;
            typingMarkerTimer.reset();
        }

        GlStateManager.disableAlpha();
        RenderUtils2D.drawCustomRect(x + 4, y + (height / 2.0f) + 2, x + 19, y + height - 2, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, (int)(170 * (animationAlpha / 300.0f))).getRGB(), new Color(0, 0, 0, (int)(170 * (animationAlpha / 300.0f))).getRGB(), new Color(0, 0, 0, 0).getRGB());
        if (ClickGUI.instance.stringInputBoxOutline.getValue()) {
            RenderUtils2D.drawRectOutline(x + 4, y + (height / 2.0f) + 2, x + width - 4, y + height - 2, ClickGUI.instance.stringInputBoxOutlineWidth.getValue(), ClickGUI.instance.stringInputBoxOutlineColor.getValue().getColor(), false, false);
        }
        GlStateManager.enableAlpha();
    }

    @Override
    public void bottomRender(int mouseX, int mouseY, boolean lastSetting, boolean firstSetting, float partialTicks) {
        GlStateManager.disableAlpha();
        drawSettingRects(lastSetting, false);

        drawExtendedGradient(lastSetting, false);
        drawExtendedLine(lastSetting);

        renderHoverRect(moduleName + setting.getName(), mouseX, mouseY, 2.0f, -13.0f, false);

        GlStateManager.enableAlpha();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY) || !setting.isVisible()) {
            ((StringSetting)setting).listening = false;
            isTyping = false;
            return false;
        }

        if (mouseButton == 0) {
            ((StringSetting)setting).listening = !((StringSetting)setting).listening;
            isTyping = true;
            return true;
        }

        return false;
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
        if (!anyExpanded) {
            return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height);
        }
        else {
            return false;
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (((StringSetting)setting).listening) {
            if (isValidInput(keyCode) && typeTimer.passed(2)) {
                setting.setValue(new StringBuilder(setting.getValue()).insert(setting.getValue().length() - typingMarkerOffset, keyCodeToCapitalized(keyCode)).toString());
                typeTimer.reset();
            }

            if (keyCode == Keyboard.KEY_BACK && setting.getValue().length() >= 1) {
                backspaceTimer.reset();
                backspaceFlag = true;
                setting.setValue(new StringBuilder(setting.getValue()).replace(setting.getValue().length() - typingMarkerOffset - 1, setting.getValue().length() - typingMarkerOffset, "").toString());
            }

            if (keyCode == Keyboard.KEY_DELETE && setting.getValue().length() >= 1) {
                deleteTimer.reset();
                deleteFlag = true;
                setting.setValue(new StringBuilder(setting.getValue()).replace(setting.getValue().length() - typingMarkerOffset, setting.getValue().length() - typingMarkerOffset + 1, "").toString());
                typingMarkerOffset--;
                if (typingMarkerOffset < 0) typingMarkerOffset = 0;
            }

            if (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT) {
                typingMarkerInterpDelta = 0.0f;
                typingMarkerInterpFlag = true;
                prevTextWidth = FontManager.getWidth(setting.getValue().substring(0, setting.getValue().length() - typingMarkerOffset));
            }

            if (keyCode == Keyboard.KEY_LEFT) {
                typingMarkerOffset++;
            }
            else if (keyCode == Keyboard.KEY_RIGHT) {
                typingMarkerOffset--;
            }

            typingMarkerOffset = (int)MathUtilFuckYou.clamp(typingMarkerOffset, 0, setting.getValue().length());
        }
    }

    private boolean isValidInput(int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_A:
            case Keyboard.KEY_B:
            case Keyboard.KEY_C:
            case Keyboard.KEY_D:
            case Keyboard.KEY_E:
            case Keyboard.KEY_F:
            case Keyboard.KEY_G:
            case Keyboard.KEY_H:
            case Keyboard.KEY_I:
            case Keyboard.KEY_J:
            case Keyboard.KEY_K:
            case Keyboard.KEY_L:
            case Keyboard.KEY_M:
            case Keyboard.KEY_N:
            case Keyboard.KEY_O:
            case Keyboard.KEY_P:
            case Keyboard.KEY_Q:
            case Keyboard.KEY_R:
            case Keyboard.KEY_S:
            case Keyboard.KEY_T:
            case Keyboard.KEY_U:
            case Keyboard.KEY_V:
            case Keyboard.KEY_W:
            case Keyboard.KEY_X:
            case Keyboard.KEY_Y:
            case Keyboard.KEY_Z:
            case Keyboard.KEY_0:
            case Keyboard.KEY_1:
            case Keyboard.KEY_2:
            case Keyboard.KEY_3:
            case Keyboard.KEY_4:
            case Keyboard.KEY_5:
            case Keyboard.KEY_6:
            case Keyboard.KEY_7:
            case Keyboard.KEY_8:
            case Keyboard.KEY_9:
            case Keyboard.KEY_MINUS:
            case Keyboard.KEY_EQUALS:
            case Keyboard.KEY_LBRACKET:
            case Keyboard.KEY_RBRACKET:
            case Keyboard.KEY_SLASH:
            case Keyboard.KEY_BACKSLASH:
            case Keyboard.KEY_GRAVE:
            case Keyboard.KEY_SEMICOLON:
            case Keyboard.KEY_APOSTROPHE:
            case Keyboard.KEY_COMMA:
            case Keyboard.KEY_PERIOD:
            case Keyboard.KEY_SPACE: return true;
        }
        return false;
    }

    public String keyCodeToCapitalized(int keyCode) {
        if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            switch (keyCode) {
                case Keyboard.KEY_A: return "A";
                case Keyboard.KEY_B: return "B";
                case Keyboard.KEY_C: return "C";
                case Keyboard.KEY_D: return "D";
                case Keyboard.KEY_E: return "E";
                case Keyboard.KEY_F: return "F";
                case Keyboard.KEY_G: return "G";
                case Keyboard.KEY_H: return "H";
                case Keyboard.KEY_I: return "I";
                case Keyboard.KEY_J: return "J";
                case Keyboard.KEY_K: return "K";
                case Keyboard.KEY_L: return "L";
                case Keyboard.KEY_M: return "M";
                case Keyboard.KEY_N: return "N";
                case Keyboard.KEY_O: return "O";
                case Keyboard.KEY_P: return "P";
                case Keyboard.KEY_Q: return "Q";
                case Keyboard.KEY_R: return "R";
                case Keyboard.KEY_S: return "S";
                case Keyboard.KEY_T: return "T";
                case Keyboard.KEY_U: return "U";
                case Keyboard.KEY_V: return "V";
                case Keyboard.KEY_W: return "W";
                case Keyboard.KEY_X: return "X";
                case Keyboard.KEY_Y: return "Y";
                case Keyboard.KEY_Z: return "Z";
                case Keyboard.KEY_0: return ")";
                case Keyboard.KEY_1: return "!";
                case Keyboard.KEY_2: return "@";
                case Keyboard.KEY_3: return "#";
                case Keyboard.KEY_4: return "$";
                case Keyboard.KEY_5: return "%";
                case Keyboard.KEY_6: return "^";
                case Keyboard.KEY_7: return "&";
                case Keyboard.KEY_8: return "*";
                case Keyboard.KEY_9: return "(";
                case Keyboard.KEY_MINUS: return "_";
                case Keyboard.KEY_EQUALS: return "+";
                case Keyboard.KEY_LBRACKET: return "{";
                case Keyboard.KEY_RBRACKET: return "}";
                case Keyboard.KEY_SLASH: return "?";
                case Keyboard.KEY_BACKSLASH: return "|";
                case Keyboard.KEY_GRAVE: return "~";
                case Keyboard.KEY_SEMICOLON: return ":";
                case Keyboard.KEY_APOSTROPHE: return "\"";
                case Keyboard.KEY_COMMA: return "<";
                case Keyboard.KEY_PERIOD: return ">";
                case Keyboard.KEY_SPACE: return " ";
            }
        }
        else {
            switch (keyCode) {
                case Keyboard.KEY_A: return "a";
                case Keyboard.KEY_B: return "b";
                case Keyboard.KEY_C: return "c";
                case Keyboard.KEY_D: return "d";
                case Keyboard.KEY_E: return "e";
                case Keyboard.KEY_F: return "f";
                case Keyboard.KEY_G: return "g";
                case Keyboard.KEY_H: return "h";
                case Keyboard.KEY_I: return "i";
                case Keyboard.KEY_J: return "j";
                case Keyboard.KEY_K: return "k";
                case Keyboard.KEY_L: return "l";
                case Keyboard.KEY_M: return "m";
                case Keyboard.KEY_N: return "n";
                case Keyboard.KEY_O: return "o";
                case Keyboard.KEY_P: return "p";
                case Keyboard.KEY_Q: return "q";
                case Keyboard.KEY_R: return "r";
                case Keyboard.KEY_S: return "s";
                case Keyboard.KEY_T: return "t";
                case Keyboard.KEY_U: return "u";
                case Keyboard.KEY_V: return "v";
                case Keyboard.KEY_W: return "w";
                case Keyboard.KEY_X: return "x";
                case Keyboard.KEY_Y: return "y";
                case Keyboard.KEY_Z: return "z";
                case Keyboard.KEY_0: return "0";
                case Keyboard.KEY_1: return "1";
                case Keyboard.KEY_2: return "2";
                case Keyboard.KEY_3: return "3";
                case Keyboard.KEY_4: return "4";
                case Keyboard.KEY_5: return "5";
                case Keyboard.KEY_6: return "6";
                case Keyboard.KEY_7: return "7";
                case Keyboard.KEY_8: return "8";
                case Keyboard.KEY_9: return "9";
                case Keyboard.KEY_MINUS: return "-";
                case Keyboard.KEY_EQUALS: return "=";
                case Keyboard.KEY_LBRACKET: return "[";
                case Keyboard.KEY_RBRACKET: return "]";
                case Keyboard.KEY_SLASH: return "/";
                case Keyboard.KEY_BACKSLASH: return "\\";
                case Keyboard.KEY_GRAVE: return "`";
                case Keyboard.KEY_SEMICOLON: return ";";
                case Keyboard.KEY_APOSTROPHE: return "'";
                case Keyboard.KEY_COMMA: return ",";
                case Keyboard.KEY_PERIOD: return ".";
                case Keyboard.KEY_SPACE: return " ";
            }
        }
        return "";
    }
}
