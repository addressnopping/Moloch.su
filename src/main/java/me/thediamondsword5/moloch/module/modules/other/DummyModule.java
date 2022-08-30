package me.thediamondsword5.moloch.module.modules.other;

import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.notification.NotificationManager;
import net.spartanb312.base.utils.ChatUtil;

@Parallel
@ModuleInfo(name = "DummyModule", category = Category.OTHER, description = "Module that you can rename to act as another module (can be used to basically rename modules from other clients by binding them to the same keys)")
public class DummyModule extends Module {

    Setting<String> info = setting("Information", "");

    @Override
    public String getHudSuffix() {
        return displayName.getValue() + (!info.getValue().equals("") ? (ChatUtil.colored("7") + "[" + ChatUtil.colored("f") + info.getValue() + ChatUtil.colored("7") + "]") : "");
    }
}
