package me.thediamondsword5.moloch.module.modules.client;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.mixin.mixins.accessor.AccessorCPacketChatMessage;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

import java.util.List;

@Parallel
@ModuleInfo(name = "ChatSetting", category = Category.CLIENT, description = "Modify Other Chat")
public class ChatSettings extends Module {

    public static ChatSettings INSTANCE;
    public static List<ChatLine> drawnChatLines = Lists.newArrayList();

    public Setting<Boolean> invisibleToggleMessages = setting("InvisibleToggleMessages", false).des("Modules that aren't visible will not send a chat notification on toggle");
    //check PopManager for popnotifications
    public Setting<Boolean> popNotifications = setting("PopNotifications", false).des("Puts client side notifications in chat whenever someone pops");
    public Setting<Boolean> popNotificationsMarked = setting("PopNotificationsMarked", true).des("Put client name in front of pop notification messages").whenTrue(popNotifications);
    public Setting<Effects> popNotificationsEffect = setting("PopNotificationsEffect", Effects.None).des("Effects for pop notification message").whenTrue(popNotifications);
    public Setting<StringColorsNoRainbow> popNotificationsColor = setting("PopNotificationsColor", StringColorsNoRainbow.DarkPurple).des("Color of pop notification message").whenTrue(popNotifications);
    public Setting<StringColorsNoRainbow> popNotificationsPopNumColor = setting("PopNotifPopNumColor", StringColorsNoRainbow.White).des("Color of pop notification popped totems number").whenTrue(popNotifications);
    public Setting<Effects> popNotificationsDeathEffect = setting("PopNotifDeathEffect", Effects.Bold).des("Effects for pop notification death message").whenTrue(popNotifications);
    public Setting<StringColorsNoRainbow> popNotificationsDeathColor = setting("PopNotifDeathColor", StringColorsNoRainbow.Red).des("Color of pop notification death message").whenTrue(popNotifications);
    public Setting<Boolean> chatTimeStamps = setting("ChatTimeStamps", false).des("Puts Time In Front Of Chat Messages");
    public Setting<Boolean> chatTimeStamps24hr = setting("ChatTimeStamps24hr", true).des("Chat TimeStamps In 24 Hours Format").whenTrue(chatTimeStamps);
    public Setting<StringColors> chatTimeStampsColor = setting("ChatTimeStampsColor", StringColors.Blue).des("Color For Chat TimeStamps").whenTrue(chatTimeStamps);
    public Setting<Brackets> chatTimeStampBrackets = setting("ChatTimeStampsBrackets", Brackets.Chevron).des("Brackets For Chat TimeStamps").whenTrue(chatTimeStamps);
    public Setting<Boolean> chatTimeStampSpace = setting("ChatTimStampsSpace", false).des("Space After Chat TimeStamps").whenTrue(chatTimeStamps);
    public Setting<Boolean> chatSuffix = setting("ChatSuffix", false).des("Appends Client Name To Message");
    public Setting<Brackets> brackets = setting("Brackets", Brackets.Chevron).des("Command Prefix Frame Brackets");
    public Setting<Effects> effects = setting("Effects", Effects.None).des("Command Prefix Effect");
    public Setting<Effects> moduleEffects = setting("ModuleEffects", Effects.Bold).des("Command Module Effect");
    public Setting<StringColors> stringColor = setting("ChatColor", StringColors.DarkPurple).des("Color For Client Chat Stuff");
    public Setting<Boolean> lgbtqDynamic = setting("RainbowMove", false).only(v -> stringColor.getValue() == StringColors.Lgbtq || (chatTimeStampsColor.getValue() == StringColors.Lgbtq && chatTimeStamps.getValue())).des("Rainbow Move");
    public Setting<Integer> lgbtqRealSpeed = setting("Speed", 1, 0, 20).whenTrue(lgbtqDynamic).only(v -> stringColor.getValue() == StringColors.Lgbtq || (chatTimeStampsColor.getValue() == StringColors.Lgbtq && chatTimeStamps.getValue()));
    public Setting<Float> lgbtqStart = setting("Hue", 0.0f, 0.0f, 360.0f).only(v -> stringColor.getValue() == StringColors.Lgbtq || (chatTimeStampsColor.getValue() == StringColors.Lgbtq && chatTimeStamps.getValue())).des("Rainbow Hue");
    public Setting<Float> lgbtqSpeed = setting("ColorSize", 100.0f, 0.0f, 100.0f).whenFalse(lgbtqDynamic).only(v -> stringColor.getValue() == StringColors.Lgbtq || (chatTimeStampsColor.getValue() == StringColors.Lgbtq && chatTimeStamps.getValue())).des("Rainbow sSze");
    public Setting<Float> lgbtqBright = setting("Brightness", 1.0f, 0.0f, 1.0f).only(v -> stringColor.getValue() == StringColors.Lgbtq || (chatTimeStampsColor.getValue() == StringColors.Lgbtq && chatTimeStamps.getValue())).des("Rainbow Brightness");
    public Setting<Float> lgbtqSaturation = setting("Saturation", 1.0f, 0.0f, 1.0f).only(v -> stringColor.getValue() == StringColors.Lgbtq || (chatTimeStampsColor.getValue() == StringColors.Lgbtq && chatTimeStamps.getValue())).des("Rainbow Saturation");

    public ChatSettings() {
        INSTANCE = this;
    }

    public enum Brackets {
        Chevron, Box, Curly, Round, None
    }

    public enum Effects {
        Bold, Underline, Italic, None
    }

    public enum StringColors {
        Black, Gold, Gray, Blue, Green, Aqua, Red, LightPurple, Yellow, White, DarkBlue, DarkGreen, DarkAqua, DarkRed, DarkPurple, DarkGray, Lgbtq
    }

    public String colorString(Setting<StringColorsNoRainbow> setting) {
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
        }
        return "";
    }

    public enum StringColorsNoRainbow {
        Black, Gold, Gray, Blue, Green, Aqua, Red, LightPurple, Yellow, White, DarkBlue, DarkGreen, DarkAqua, DarkRed, DarkPurple, DarkGray
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof CPacketChatMessage && chatSuffix.getValue()) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") || s.startsWith("+") || s.startsWith(".") || s.startsWith("#") || s.startsWith(";") || s.endsWith(BaseCenter.CHAT_SUFFIX)) return;
            s += BaseCenter.CHAT_SUFFIX;
            if (s.length() >= 256) s = s.substring(0, 256);
            ((AccessorCPacketChatMessage) event.getPacket()).setMessage(s);
        }
    }


    @Override
    public void onDisable() {
        enable();
    }
}
