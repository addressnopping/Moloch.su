package net.spartanb312.base.client;

import com.google.common.collect.Lists;
import net.spartanb312.base.utils.ChatUtil;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.graphics.font.CFont;
import net.spartanb312.base.utils.graphics.font.CFontRenderer;
import me.thediamondsword5.moloch.hud.huds.CustomHUDFont;
import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import me.thediamondsword5.moloch.module.modules.client.MoreClickGUI;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.util.List;

import static net.spartanb312.base.command.Command.mc;

public class FontManager {

    public static CFontRenderer iconFontPlus;
    public static CFontRenderer iconFontMiniIcon;
    public static CFontRenderer iconFont;
    public static CFontRenderer fontRenderer;
    public static CFontRenderer hudFontRenderer;
    public static CFontRenderer fontArialRenderer;
    public static CFontRenderer hudFontArialRenderer;
    public static CFontRenderer hudFontObjectivityRenderer;
    public static CFontRenderer fontObjectivityRenderer;


    public static void init() {
        iconFont = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/Symbols.ttf", 19.0f, Font.PLAIN), true, false);
        iconFontPlus = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/Symbols.ttf", 14.0f, Font.PLAIN), true, false);
        iconFontMiniIcon = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/Symbols.ttf", 18.0f, Font.PLAIN), true, false);

        hudFontArialRenderer = new CFontRenderer(new Font("Arial", Font.PLAIN, 36), true, false);
        fontArialRenderer = new CFontRenderer(new Font("Arial", Font.PLAIN, 36), true, false);

        hudFontRenderer = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/Comfortaa-Bold.ttf", 18.0f, Font.PLAIN), true, false);
        fontRenderer = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/Comfortaa-Bold.ttf", 14.0f, Font.PLAIN), true, false);

        hudFontObjectivityRenderer = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/objectivity.bold.ttf", 18.0f, Font.PLAIN), true, false);
        fontObjectivityRenderer = new CFontRenderer(new CFont.CustomFont("/assets/moloch/fonts/objectivity.bold.ttf", 14.0f, Font.PLAIN), true, false);
    }

    public static int getWidth(String str){
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            return fontRenderer.getStringWidth(str);
        }
        else if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            return fontArialRenderer.getStringWidth(str);
        }
        else if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            return fontObjectivityRenderer.getStringWidth(str);
        }
        else {
            return mc.fontRenderer.getStringWidth(str);
        }
    }


    public static int getHeight(){
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            return fontRenderer.getHeight() + 2;
        }
        else if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            return fontArialRenderer.getHeight() + 2;
        }
        else if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            return fontObjectivityRenderer.getHeight() + 2;
        }
        else {
            return fontRenderer.getHeight();
        }
    }

    public static CFontRenderer descriptionHubDesTextFontRenderer () {
        switch (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue()) {
            case Comfortaa: return fontRenderer;

            case Arial: return fontArialRenderer;

            case Objectivity: return fontObjectivityRenderer;
        }
        return null;
    }

    public static int getWidthDescriptionHubDesText(String text) {
        switch (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue()) {
            case Comfortaa: return fontRenderer.getStringWidth(text);

            case Arial: return fontArialRenderer.getStringWidth(text);

            case Objectivity: return fontObjectivityRenderer.getStringWidth(text);

            case Minecraft: return mc.fontRenderer.getStringWidth(text);
        }
        return 0;
    }

    public static int getHeightDescriptionHubHeaderText() {
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Comfortaa) {
            return fontRenderer.getHeight() + 2;
        }
        else if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Arial) {
            return fontArialRenderer.getHeight() + 2;
        }
        else if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Objectivity) {
            return fontObjectivityRenderer.getHeight() + 2;
        }
        else {
            return fontRenderer.getHeight();
        }
    }

    public static int getHeightDescriptionHubDesText() {
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Comfortaa) {
            return fontRenderer.getHeight() + 2 + MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue();
        }
        else if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Arial) {
            return fontArialRenderer.getHeight() + 2 + MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue();
        }
        else if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Objectivity) {
            return fontObjectivityRenderer.getHeight() + 2 + MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue();
        }
        else {
            return fontRenderer.getHeight() + MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue();
        }
    }

    public static int getWidthCategory(String str){
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Comfortaa) {
            return fontRenderer.getStringWidth(str);
        }
        else if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Arial) {
            return fontArialRenderer.getStringWidth(str);
        }
        else if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Objectivity) {
            return fontObjectivityRenderer.getStringWidth(str);
        }
        else {
            return mc.fontRenderer.getStringWidth(str);
        }
    }

    public static int getHeightCategory(){
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Comfortaa) {
            return hudFontRenderer.getHeight() + 2;
        }
        else if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Arial) {
            return hudFontArialRenderer.getHeight() + 2;
        }
        else if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Objectivity) {
            return hudFontObjectivityRenderer.getHeight() + 2;
        }
        else {
            return fontRenderer.getHeight() + 2;
        }
    }

    public static int getWidthHUD(String str){
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
            return hudFontRenderer.getStringWidth(str);
        }
        else if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
            return hudFontArialRenderer.getStringWidth(str);
        }
        else if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
            return hudFontObjectivityRenderer.getStringWidth(str);
        }
        else {
            return mc.fontRenderer.getStringWidth(str);
        }
    }

    public static int getHeightHUD(){
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
            return hudFontRenderer.getHeight() + 2;
        }
        else if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
            return hudFontArialRenderer.getHeight() + 2;
        }
        else if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
            return hudFontObjectivityRenderer.getHeight() + 2;
        }
        else {
            return fontRenderer.getHeight() + 2;
        }
    }

    public static void draw(String str, float x, float y, int color) {
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawString(str, x, y, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static void drawShadow(String str, float x, float y, int color) {
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawStringWithShadow(str, x, y, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawStringWithShadow(str, x, y - 2.0f, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawStringWithShadow(str, x, y, color);
        }
    }


    public static void drawCategory(String str, float x, float y, int color) {
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawString(str, x, y, color);
        }
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static void drawShadowCategory(String str, float x, float y, int color) {
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawStringWithShadow(str, x, y, color);
        }
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawStringWithShadow(str, x, y - 2.0f, color);
        }
        if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawStringWithShadow(str, x, y, color);
        }
    }

    public static void drawHeaderText(String str, float x, float y, int color) {
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Comfortaa) {
            fontRenderer.drawString(str, x, y, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static void drawDesText(String str, float x, float y, int color) {
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Comfortaa) {
            fontRenderer.drawString(str, x, y, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static void drawHeaderTextShadow(String str, float x, float y, int color) {
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Comfortaa) {
            fontRenderer.drawString(str, x, y, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static void drawDesTextShadow(String str, float x, float y, int color) {
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Comfortaa) {
            fontRenderer.drawStringWithShadow(str, x, y, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Arial) {
            fontArialRenderer.drawStringWithShadow(str, x, y - 2.0f, color);
        }
        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Objectivity) {
            fontObjectivityRenderer.drawStringWithShadow(str, x, y, color);
        }
    }

    public static void drawHUD(String str, float x, float y, boolean shadow, int color) {
        if (shadow) {
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
                hudFontRenderer.drawStringWithShadow(str, x, y, color);
            }
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
                hudFontArialRenderer.drawStringWithShadow(str, x, y - 2.0f, color);
            }
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
                hudFontObjectivityRenderer.drawStringWithShadow(str, x, y, color);
            }
        }
        else {
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
                hudFontRenderer.drawString(str, x, y, color);
            }
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
                hudFontArialRenderer.drawString(str, x, y - 2.0f, color);
            }
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
                hudFontObjectivityRenderer.drawString(str, x, y, color);
            }
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Minecraft) {
            mc.fontRenderer.drawString(str, x, y, color, shadow);
        }
    }

    public static void drawHUD(String str, float x, float y, int color) {
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
            hudFontRenderer.drawString(str, x, y, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
            hudFontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
            hudFontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static void drawHUDShadow(String str, float x, float y, int color) {
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
            hudFontRenderer.drawStringWithShadow(str, x, y, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
            hudFontArialRenderer.drawStringWithShadow(str, x, y - 2.0f, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
            hudFontObjectivityRenderer.drawStringWithShadow(str, x, y, color);
        }
    }

    public static void drawKeyBind(String str, float x, float y, int color) {
        if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Comfortaa) {
            fontRenderer.drawString(str, x, y, color);
        }
        if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color);
        }
        if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color);
        }
    }

    public static int getKeyBindWidth(String str){
        if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Comfortaa) {
            return fontRenderer.getStringWidth(str);
        }
        else if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Arial) {
            return fontArialRenderer.getStringWidth(str);
        }
        else if (ClickGUI.instance.bindButtonKeyStrFont.getValue() == ClickGUI.KeyBindFancyFont.Objectivity) {
            return fontObjectivityRenderer.getStringWidth(str);
        }
        else {
            return mc.fontRenderer.getStringWidth(str);
        }
    }


    public static void drawCentered(String str, float x, float y, int color) {
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawCenteredString(str, x, y, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawCenteredString(str, x, y - 2.0f, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawCenteredString(str, x, y, color);
        }
    }

    public static void drawShadowCentered(String str, float x, float y, int color) {
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawCenteredStringWithShadow(str, x, y, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawCenteredStringWithShadow(str, x, y - 2.0f, color);
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawCenteredStringWithShadow(str, x, y, color);
        }
    }


    public static void drawHUDCentered(String str, float x, float y, int color) {
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
            hudFontRenderer.drawCenteredString(str, x, y, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
            hudFontArialRenderer.drawCenteredString(str, x, y - 2.0f, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
            hudFontObjectivityRenderer.drawCenteredString(str, x, y, color);
        }
    }

    public static void drawHUDShadowCentered(String str, float x, float y, int color) {
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Comfortaa) {
            hudFontRenderer.drawCenteredStringWithShadow(str, x, y, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Arial) {
            hudFontArialRenderer.drawCenteredStringWithShadow(str, x, y - 2.0f, color);
        }
        if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Objectivity) {
            hudFontObjectivityRenderer.drawCenteredStringWithShadow(str, x, y, color);
        }
    }


    public static void draw(String str, int x, int y, Color color) {
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Comfortaa) {
            fontRenderer.drawString(str, x, y, color.getRGB());
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Arial) {
            fontArialRenderer.drawString(str, x, y - 2.0f, color.getRGB());
        }
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Objectivity) {
            fontObjectivityRenderer.drawString(str, x, y, color.getRGB());
        }
    }

    public static int getIconWidth(){
        return iconFont.getStringWidth("b");
    }

    public static int getIconHeight(){
        return iconFont.getHeight();
    }

    public static String iconType() {
        if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Dots) {
            return "b";
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Arrow) {
            return "e";
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
            return "g";
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.None) {
            return "";
        }
        return "";
    }

    public static String iconTypeExtended() {
        if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Dots) {
            return "a";
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Arrow) {
            return "f";
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
            return "g";
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.None) {
            return "";
        }
        return "";
    }

    public static int getVisibilityIconWidth() {
        return iconFont.getStringWidth("(");
    }

    public static void drawVisibilityIconOn(int x, int y, int color) {
        iconFont.drawString("(", x, y, color);
    }

    public static void drawVisibilityIconOff(int x, int y, int color) {
        iconFont.drawString(")", x, y, color);
    }

    public static int getEnumIconWidth() {
        return iconFont.getStringWidth("@");
    }

    public static void drawEnumIcon(int x, int y, int color) {
        iconFont.drawString("@", x, y, color);
    }

    public static void drawIcon(int x, int y, int color) {
        if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Plus) {
            iconFontPlus.drawString("c", x - 2, y + 1, color);
        }
        else if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
            iconFontPlus.drawString("g", x - 4, y, color);
        }
        else {
            iconFont.drawString(iconType(), x, y, color);
        }
    }

    public static void drawIcon(int x, int y, Color color) {
        if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Plus) {
            iconFontPlus.drawString("c", x - 2, y + 1, color.getRGB());
        }
        else {
            iconFont.drawString(iconType(), x, y, color.getRGB());
        }
    }

    public static void drawIconExtended(int x, int y, Color color) {
        if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Plus) {
            iconFontPlus.drawString("d", x - 2, y + 1, color.getRGB());
        }
        else {
            iconFont.drawString(iconTypeExtended(), x, y, color.getRGB());
        }
    }

    public static void drawModuleMiniIcon(String icon, int x, int y, Color color) {
        iconFontMiniIcon.drawString(icon, x, y, color.getRGB());
    }

    public int drawStringMc(String text, float x, float y, int color, boolean shadow) {
        return mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public int drawStringMcCentered(String text, float x, float y, int color, boolean shadow) {
        return mc.fontRenderer.drawString(text, x - (float) (this.getStringWidth(text) / 2), y, color, shadow);
    }

    public static java.util.List<ITextComponent> splitTextCFont(ITextComponent iTextComponent, int maxTextLength, CFontRenderer fontRendererIn) {
        String localStr = iTextComponent.getFormattedText();
        List<ITextComponent> localList = Lists.newArrayList(new TextComponentString(fontRendererIn.trimCFontStringToWidthBetter(localStr, maxTextLength, true)));

        while (fontRendererIn.getStringWidth(localStr) > 0.0f) {
            localStr = localStr.substring(fontRendererIn.trimCFontStringToWidthBetter(localStr, maxTextLength, true).length());
            localList.add(new TextComponentString(fontRendererIn.trimCFontStringToWidthBetter(localStr, maxTextLength, true)));
        }

        return localList;
    }

    public int colorStringToColor(Setting<ChatSettings.StringColors> setting, int rgbColor) {
        switch (setting.getValue()) {
            case Black: return Color.BLACK.getRGB();

            case Gold: return new Color(255, 170, 0).getRGB();

            case Gray: return new Color(170, 170, 170).getRGB();

            case Blue: return new Color(85, 85, 255).getRGB();

            case Green: return new Color(85, 255, 85).getRGB();

            case Aqua: return new Color(85, 255, 255).getRGB();

            case Red: return new Color(255, 85, 85).getRGB();

            case LightPurple: return new Color(255, 85, 255).getRGB();

            case Yellow: return new Color(255, 255, 85).getRGB();

            case White: return Color.WHITE.getRGB();

            case DarkBlue: return new Color(0, 0, 170).getRGB();

            case DarkGreen: return new Color(0, 170, 0).getRGB();

            case DarkAqua: return new Color(0, 170, 170).getRGB();

            case DarkRed: return new Color(170, 0, 0).getRGB();

            case DarkPurple: return new Color(170, 0, 170).getRGB();

            case DarkGray: return new Color(85, 85, 85).getRGB();

            case Lgbtq: return rgbColor;
        }
        return 0;
    }

    public int getStringWidth (String text) {
        return mc.fontRenderer.getStringWidth(text);
    }

    public void drawLgbtqString ( String text , float x , float y , int startColor , float factor , boolean shadow) {
        Color currentColor = new Color ( startColor );
        float hueIncrement = 1.0f / factor;
        float currentHue = Color.RGBtoHSB ( currentColor.getRed ( ) , currentColor.getGreen ( ) , currentColor.getBlue ( ) , null )[0];
        float saturation = Color.RGBtoHSB ( currentColor.getRed ( ) , currentColor.getGreen ( ) , currentColor.getBlue ( ) , null )[1];
        float brightness = Color.RGBtoHSB ( currentColor.getRed ( ) , currentColor.getGreen ( ) , currentColor.getBlue ( ) , null )[2];
        int currentWidth = 0;
        boolean shouldRainbow = false;
        boolean shouldContinue = false;
        boolean flag = false;
        for (int i = 0; i < text.length ( ); ++ i) {
            char currentChar = text.charAt ( i );
            char nextChar = text.charAt ( (int) MathUtilFuckYou.clamp ( i + 1 , 0 , text.length ( ) - 1 ) );
            final boolean equals = (String.valueOf(currentChar) + nextChar).equals ( "\u00a7r" );
            if (equals) {
                shouldRainbow = false;
            }
            if ( shouldContinue ) {
                shouldContinue = false;
                continue;
            }
            if ( equals ) {
                String escapeString = text.substring ( i );
                drawStringMc( escapeString , x + (float) currentWidth , y , Color.WHITE.getRGB ( ) , shadow );
                break;
            }
            if (String.valueOf(currentChar).equals("\u061c")) {
                shouldRainbow = true;
            }
            drawStringMc(String.valueOf(currentChar).equals("\u00a7") ? "" : String.valueOf(currentChar), x + (float) currentWidth , y , shouldRainbow ? currentColor.getRGB ( ) : ChatSettings.INSTANCE.chatTimeStamps.getValue() && ChatSettings.INSTANCE.chatTimeStampsColor.getValue() != ChatSettings.StringColors.Lgbtq ? colorStringToColor(ChatSettings.INSTANCE.chatTimeStampsColor, currentColor.getRGB()) : Color.WHITE.getRGB() , shadow );
            if (String.valueOf(currentChar).equals(ChatUtil.bracketLeft(ChatSettings.INSTANCE.brackets))) {
                drawStringMc( ChatUtil.bracketLeft(ChatSettings.INSTANCE.brackets) , x + (float) currentWidth , y , Color.GRAY.getRGB() , shadow );
                if (ChatSettings.INSTANCE.chatTimeStamps.getValue() && !flag) {
                    drawStringMc( ChatUtil.bracketLeft(ChatSettings.INSTANCE.brackets) , x + (float) currentWidth , y , colorStringToColor(ChatSettings.INSTANCE.chatTimeStampsColor, currentColor.getRGB()) , shadow );
                    flag = true;
                }
            }
            if ( String.valueOf ( currentChar ).equals ( "\u00a7" ) ) {
                shouldContinue = true;
            }
            currentWidth += this.getStringWidth ( String.valueOf ( currentChar ) );
            if ( String.valueOf ( currentChar ).equals ( " " ) ) continue;
            currentColor = new Color ( Color.HSBtoRGB ( currentHue , saturation , brightness ) );
            currentHue += hueIncrement;
        }
    }
}
