package net.spartanb312.base.event.events.client;

import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.EventCenter;

public class SettingUpdateEvent extends EventCenter {

    private final Setting<?> setting;

    public SettingUpdateEvent(Setting<?> setting) {
        this.setting = setting;
    }

    public Setting<?> getSetting() {
        return setting;
    }
}
