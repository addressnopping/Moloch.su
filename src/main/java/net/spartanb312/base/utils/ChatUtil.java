package net.spartanb312.base.utils;

import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import net.spartanb312.base.core.setting.Setting;
import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import net.spartanb312.base.BaseCenter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.TextComponentString;

import static net.spartanb312.base.utils.RotationUtil.mc;


public class ChatUtil {
    private static final int DeleteID = 114514;

    public static char SECTIONSIGN = '\u00A7';

    public static String colored(String code) {
        return SECTIONSIGN + code;
    }
    
    public static String bracketLeft (Setting<ChatSettings.Brackets> setting) {
        switch (setting.getValue()) {
            case Chevron: return "<";

            case Box: return "[";

            case Curly: return "{";

            case Round: return "(";

            case None: return " ";
        }
        return "";
    }

    public static String bracketRight (Setting<ChatSettings.Brackets> setting) {
        switch (setting.getValue()) {
            case Chevron: return ">";

            case Box: return "]";

            case Curly: return "}";

            case Round: return ")";

            case None: return " ";
        }
        return "";
    }

    public static String effectString(Setting<ChatSettings.Effects> setting) {
        switch (setting.getValue()) {
            case Bold: return SECTIONSIGN + "l";

            case Underline: return SECTIONSIGN + "n";

            case Italic: return SECTIONSIGN +"o";

            case None: return "";
        }
        return "";
    }

    public static String colorString(Setting<ChatSettings.StringColors> setting) {
        switch (setting.getValue()) {
            case Black: return "0";

            case Gold: return "6";

            case Gray: return "7";

            case Blue: return "9";

            case Green: return "a";

            case Aqua: return "b";

            case Red: return "c";

            case LightPurple: return "d";

            case Yellow: return "e";

            case White: return "f";

            case DarkBlue: return "1";

            case DarkGreen: return "2";

            case DarkAqua: return "3";

            case DarkRed: return "4";

            case DarkPurple: return "5";

            case DarkGray: return "8";

            case Lgbtq: return "\u034f";
        }
        return "";
    }
    
    public static void sendNoSpamMessage(String message, int messageID) {
        sendNoSpamRawChatMessage(SECTIONSIGN + "7" + bracketLeft(ChatSettings.INSTANCE.brackets) + "\u061c" + ((ChatSettings.INSTANCE.stringColor.getValue() == ChatSettings.StringColors.Lgbtq) ? " " : "") + SECTIONSIGN + colorString(ChatSettings.INSTANCE.stringColor) + effectString(ChatSettings.INSTANCE.effects) + ClientInfo.INSTANCE.clientName.getValue() + "\u00a7r" + SECTIONSIGN + "7" + bracketRight(ChatSettings.INSTANCE.brackets) + " " + SECTIONSIGN + "r" + message, messageID);
    }

    public static void sendNoSpamMessage(String message) {
        sendNoSpamRawChatMessage(SECTIONSIGN + "7" + bracketLeft(ChatSettings.INSTANCE.brackets) + "\u061c" + ((ChatSettings.INSTANCE.stringColor.getValue() == ChatSettings.StringColors.Lgbtq) ? " " : "") + SECTIONSIGN + colorString(ChatSettings.INSTANCE.stringColor) + effectString(ChatSettings.INSTANCE.effects) + ClientInfo.INSTANCE.clientName.getValue() + "\u00a7r" + SECTIONSIGN + "7" + bracketRight(ChatSettings.INSTANCE.brackets) + " " + SECTIONSIGN + "r" + message);
    }

    public static void sendNoSpamMessage(String[] messages) {
        sendNoSpamMessage("");
        for (String s : messages) sendNoSpamRawChatMessage(s);
    }

    public static void sendNoSpamErrorMessage(String message) {
        sendNoSpamRawChatMessage(SECTIONSIGN + "7" + bracketLeft(ChatSettings.INSTANCE.brackets)  + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7" + bracketRight(ChatSettings.INSTANCE.brackets) + " " + SECTIONSIGN + "r" + message);
    }

    public static void sendNoSpamErrorMessage(String message, int messageID) {
        sendNoSpamRawChatMessage(SECTIONSIGN + "7" + bracketLeft(ChatSettings.INSTANCE.brackets)  + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7" + bracketRight(ChatSettings.INSTANCE.brackets) + " " + SECTIONSIGN + "r" + message, messageID);
    }

    public static void sendNoSpamRawChatMessage(String message) {
        sendSpamlessMessage(message);
    }

    public static void sendNoSpamRawChatMessage(String message, int messageID) {
        sendSpamlessMessage(messageID, message);
    }

    public static void printRawChatMessage(String message) {
        if (mc.player == null) return;
        ChatMessage(message);
    }

    public static void printChatMessage(String message) {
        printRawChatMessage(SECTIONSIGN + "7" + bracketLeft(ChatSettings.INSTANCE.brackets) + "\u061c" + ((ChatSettings.INSTANCE.stringColor.getValue() == ChatSettings.StringColors.Lgbtq) ? " " : "") + SECTIONSIGN + colorString(ChatSettings.INSTANCE.stringColor) + effectString(ChatSettings.INSTANCE.effects) + ClientInfo.INSTANCE.clientName.getValue() + "\u00a7r" + SECTIONSIGN + "7" + bracketRight(ChatSettings.INSTANCE.brackets) + " " + SECTIONSIGN + "r" + message);
    }

    public static void printErrorChatMessage(String message) {
        printRawChatMessage(SECTIONSIGN + "7" + bracketLeft(ChatSettings.INSTANCE.brackets)  + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7" + bracketRight(ChatSettings.INSTANCE.brackets) + " " + SECTIONSIGN + "r" + message);
    }

    public static void sendSpamlessMessage(String message) {
        if (mc.player == null) return;
        final GuiNewChat chat = mc.ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(new TextComponentString(message), DeleteID);
    }

    public static void sendSpamlessMessage(int messageID, String message) {
        if (mc.player == null) return;
        final GuiNewChat chat = mc.ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(new TextComponentString(message), messageID);
    }

    public static void ChatMessage(String message) {
        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }
}
