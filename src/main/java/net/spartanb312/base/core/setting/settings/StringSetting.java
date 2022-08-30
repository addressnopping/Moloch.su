package net.spartanb312.base.core.setting.settings;

import net.spartanb312.base.core.setting.Setting;

public class StringSetting extends Setting<String> {
    public boolean listening = false;
    public StringSetting(String name, String defaultValue) {
        super(name, defaultValue);
    }
}
