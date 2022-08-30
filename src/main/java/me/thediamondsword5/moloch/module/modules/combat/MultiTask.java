package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.event.events.player.MultiTaskEvent;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "MultiTask", category = Category.COMBAT, description = "Allows you to use items while mining")
public class MultiTask extends Module {

    @Listener
    public void usingItemSet(MultiTaskEvent event) {
        event.cancel();
    }
}
