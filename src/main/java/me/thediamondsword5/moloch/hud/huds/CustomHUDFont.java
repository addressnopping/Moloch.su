package me.thediamondsword5.moloch.hud.huds;

import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.hud.HUDModule;
import net.minecraft.client.gui.ScaledResolution;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.module.Category;

@ModuleInfo(name = "HUDFont", category = Category.HUD, description = "HUD Font Settings")
public class CustomHUDFont extends HUDModule {
    public static CustomHUDFont instance;
    public Setting<FontMode> font = setting("Font", FontMode.Comfortaa).des("Font");

    public CustomHUDFont() {
        instance = this;
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {

    }

    public enum FontMode {
        Comfortaa, Arial, Objectivity, Minecraft
    }


    @Override
    public void onDisable() {
        enable();
    }
}
