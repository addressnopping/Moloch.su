package me.thediamondsword5.moloch.module.modules.client;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "ClientInfo", category = Category.CLIENT, description = "Change client information")
public class ClientInfo extends Module {

    public static ClientInfo INSTANCE;
    public final String modVersion = "b3"; //this exists so version can be updated without user having to manually update config

    public Setting<String> clientName = setting("ClientName", "moloch.su");
    public Setting<String> clientVersion = setting("ClientVersion", modVersion);
    public Setting<String> clientPrefix = setting("ClientPrefix", "+");

    public ClientInfo() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        enable();
    }
}
