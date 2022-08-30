package net.spartanb312.base.command.commands;


import net.spartanb312.base.client.CommandManager;
import net.spartanb312.base.command.Command;
import net.spartanb312.base.common.annotations.CommandInfo;
import net.spartanb312.base.utils.ChatUtil;

/**
 * Created by B_312 on 01/15/21
 */
@CommandInfo(command = "commands", description = "Lists all commands.")
public class Commands extends Command {

    @Override
    public void onCall(String s, String[] args) {
        ChatUtil.printChatMessage("\247b" + "Commands:");
        try {
            for (Command cmd : CommandManager.getInstance().commands) {
                if (cmd == this) {
                    continue;
                }
                ChatUtil.printChatMessage("\247b" + cmd.getSyntax().replace("<", "\2473<\2479").replace(">", "\2473>") + "\2478" + " - " + cmd.getDescription());
            }
        } catch (Exception e) {
            ChatUtil.sendNoSpamErrorMessage(getSyntax());
        }
    }

    @Override
    public String getSyntax() {
        return "commands";
    }

}