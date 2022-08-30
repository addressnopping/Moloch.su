package me.thediamondsword5.moloch.hud.huds;

import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.engine.AsyncRenderer;
import net.spartanb312.base.utils.ChatUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.graphics.font.CFontRenderer;

import java.awt.*;

import static net.spartanb312.base.BaseCenter.colorUtil;


@ModuleInfo(name = "WaterMark", category = Category.HUD, description = "Client Name Display")
public class WaterMark extends HUDModule {
    public CFontRenderer font = FontManager.fontRenderer;
    public Setting<Boolean> version = setting("Version", true).des("Draw Version");
    public Setting<Boolean> watermarkShadow = setting("Shadow", true).des("Draw Shadow Under Watermark");
    public Setting<Boolean> rainbow = setting("Rainbow", false).des("Rainbow color");
    public Setting<Float> rainbowSpeed = setting("Rainbow Speed", 1.0f, 0.0f, 30.0f).des("Rainbow color change speed").whenTrue(rainbow);
    public Setting<Float> rainbowSaturation = setting("Saturation", 0.75f, 0.0f, 1.0f).des("Rainbow color saturation").whenTrue(rainbow);
    public Setting<Float> rainbowBrightness = setting("Brightness", 0.8f, 0.0f, 1.0f).des("Rainbow color brightness").whenTrue(rainbow);
    public Setting<Integer> red = setting("Red", 100, 0, 255).des("Red").whenFalse(rainbow);
    public Setting<Integer> green = setting("Green", 61, 0, 255).des("Green").whenFalse(rainbow);
    public Setting<Integer> blue = setting("Blue", 255, 0, 255).des("Blue").whenFalse(rainbow);
    public Setting<Integer> alpha = setting("Alpha", 255, 0, 255).des("Blue");


    public WaterMark() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                drawAsyncString(ClientInfo.INSTANCE.clientName.getValue() + " " + ChatUtil.SECTIONSIGN + "f" + (version.getValue() ? ClientInfo.INSTANCE.clientVersion.getValue() : ""), x, y, color(), watermarkShadow.getValue());
                width = FontManager.getWidthHUD(ClientInfo.INSTANCE.clientName.getValue() + " " + ChatUtil.SECTIONSIGN + "f" + (version.getValue() ? ClientInfo.INSTANCE.clientVersion.getValue() : ""));
                height = FontManager.getHeight();
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

    public int color() {
        if (rainbow.getValue()) {
            Color lgbtqColor = new Color(ColorUtil.getBetterRainbow(rainbowSpeed.getValue(), rainbowSaturation.getValue(), rainbowBrightness.getValue()));
            return new Color(lgbtqColor.getRed(), lgbtqColor.getGreen(), lgbtqColor.getBlue(), alpha.getValue()).getRGB();
        }
        else {
            return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()).getRGB();
        }
    }

}
