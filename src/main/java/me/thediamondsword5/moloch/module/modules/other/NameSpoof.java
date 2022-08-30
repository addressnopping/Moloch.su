package me.thediamondsword5.moloch.module.modules.other;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "NameSpoof", category = Category.OTHER, description = "Changes all instances of your name in chat, on player tab, and on nametags")
public class NameSpoof extends Module {

    public Setting<String> name = setting("Name", "").des("Your new name");

    public static NameSpoof INSTANCE;

    public NameSpoof() {
        INSTANCE = this;
    }
}
