package net.spartanb312.base.command.commands;

import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.CommandManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.command.Command;
import net.spartanb312.base.common.annotations.CommandInfo;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.ChatUtil;
import org.lwjgl.input.Keyboard;

/**
 * Created by B_312 on 01/15/21
 */
@CommandInfo(command = "help", description = "Get helps.")
public class Help extends Command {

    @Override
    public void onCall(String s, String[] args) {
        ChatUtil.printChatMessage("\247b" + ClientInfo.INSTANCE.clientName.getValue() + " " + "\247a" + ClientInfo.INSTANCE.clientVersion.getValue());
        ChatUtil.printChatMessage("\247c" + "Made by: " + BaseCenter.AUTHOR);
        ChatUtil.printChatMessage("\247c" + "Github: " + BaseCenter.GITHUB);
        ChatUtil.printChatMessage("\2473" + "Press " + "\247c" + Keyboard.getKeyName(ModuleManager.getModule(ClickGUI.class).bindSetting.getValue().getKeyCode()) + "\2473" + " to open ClickGUI");
        ChatUtil.printChatMessage("\2473" + "Use command: " + "\2479" + ClientInfo.INSTANCE.clientPrefix.getValue() + "prefix <target prefix>" + "\2473" + " to set command prefix");
        ChatUtil.printChatMessage("\2473" + "List all available commands: " + "\2479" + ClientInfo.INSTANCE.clientPrefix.getValue() + "commands");
    }

    @Override
    public String getSyntax() {
        return "help";
    }

}