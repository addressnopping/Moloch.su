package me.thediamondsword5.moloch.core.setting.settings;

import net.spartanb312.base.core.setting.Setting;
import me.thediamondsword5.moloch.core.common.Color;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(String name, Color defaultValue) {
        super(name, defaultValue);
    }

    public void setColor(int color) {
        value.setColor(color);
    }
}
