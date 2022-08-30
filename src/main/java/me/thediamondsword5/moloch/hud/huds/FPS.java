package me.thediamondsword5.moloch.hud.huds;

import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.engine.AsyncRenderer;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.utils.ChatUtil;
import net.spartanb312.base.utils.graphics.font.CFontRenderer;

//made July 5, 2022 by cxmmmand

@ModuleInfo(name = "FPS", category = Category.HUD, description = "Displays Frames Per Second")
public class FPS extends HUDModule {
    public CFontRenderer font = FontManager.fontRenderer;
    Setting<Boolean> fpsShadow = setting("Shadow", true).des("Draw Shadow Under FPS");
    Setting<Color> color = setting("Color", new Color(new java.awt.Color(100, 61, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 61, 255, 255));

    public FPS() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                drawAsyncString("FPS " + Minecraft.getDebugFPS() + " " + ChatUtil.SECTIONSIGN + "f", x, y, color.getValue().getColor(), fpsShadow.getValue());
                width = FontManager.getWidthHUD("FPS " + Minecraft.getDebugFPS() + " " + ChatUtil.SECTIONSIGN + "f" + "");
                height = FontManager.getHeight();
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }
}
