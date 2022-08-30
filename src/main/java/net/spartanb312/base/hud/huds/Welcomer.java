package net.spartanb312.base.hud.huds;

import me.thediamondsword5.moloch.module.modules.other.NameSpoof;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.client.GUIManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.engine.AsyncRenderer;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@ModuleInfo(name = "Welcomer", category = Category.HUD)
public class Welcomer extends HUDModule {

    public Setting<Boolean> shadow = setting("Shadow", true).des("Draw Shadow Under Welcome Message");

    public Welcomer() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                String text = "Welcome " +
                        (ModuleManager.getModule(NameSpoof.class).isEnabled() ? NameSpoof.INSTANCE.name.getValue() : mc.player.getName()) +
                        "! Have a nice day :)";
                drawAsyncString(text, x, y, GUIManager.getColor3I(), shadow.getValue());
                width = FontManager.getWidthHUD(text);
                height = FontManager.getHeight();
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

}
