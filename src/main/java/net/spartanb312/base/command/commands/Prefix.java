package net.spartanb312.base.command.commands;


import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import net.spartanb312.base.client.CommandManager;
import net.spartanb312.base.command.Command;
import net.spartanb312.base.common.annotations.CommandInfo;
import net.spartanb312.base.utils.ChatUtil;
import net.spartanb312.base.utils.SoundUtil;

/**
 * Created by killRED on 2020
 * Updated by B_312 on 01/15/21
 */
@CommandInfo(command = "prefix", description = "Set command prefix.")
public class Prefix extends Command {

    @Override
    public void onCall(String s, String[] args) {
        if (args.length <= 0) {
            ChatUtil.sendNoSpamErrorMessage("Please specify a new prefix!");
            return;
        }
        if (args[0] != null) {
            ClientInfo.INSTANCE.clientPrefix.setValue(args[0]);
            ChatUtil.sendNoSpamMessage("Prefix set to " + ChatUtil.SECTIONSIGN + "b" + args[0] + "!");
            SoundUtil.playButtonClick();
        }
    }

    @Override
    public String getSyntax() {
        return "prefix <char>";
    }

}
