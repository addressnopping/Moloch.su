package me.thediamondsword5.moloch.module.modules.movement;

import me.thediamondsword5.moloch.client.ServerManager;
import me.thediamondsword5.moloch.event.events.player.UpdateTimerEvent;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "Timer", category = Category.MOVEMENT, description = "Modifies client side tick speed")
public class Timer extends Module {

    Setting<Boolean> tpsSync = setting("TPSSync", false).des("Syncs client side tick speed with server side tick speed");
    Setting<Float> speed = setting("Speed", 10.0f, 0.1f, 20.0f).des("Speed of ticks").whenFalse(tpsSync);

    @Listener
    public void onUpdateTimer(UpdateTimerEvent event) {
        if (tpsSync.getValue()) {
            event.timerSpeed *= ServerManager.getTPS() / 20.0f;
        }
        else {
            event.timerSpeed *= speed.getValue();
        }
    }
}
