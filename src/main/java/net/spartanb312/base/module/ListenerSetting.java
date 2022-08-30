package net.spartanb312.base.module;

import net.spartanb312.base.core.concurrent.task.VoidTask;
import net.spartanb312.base.core.setting.Setting;

public class ListenerSetting extends Setting<VoidTask> {
    public ListenerSetting(String name, VoidTask defaultValue) {
        super(name, defaultValue);
    }
}
