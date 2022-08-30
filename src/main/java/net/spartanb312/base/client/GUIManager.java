package net.spartanb312.base.client;

import net.spartanb312.base.utils.ColorUtil;
import me.thediamondsword5.moloch.module.modules.client.Particles;
import net.spartanb312.base.module.modules.client.ClickGUI;

import java.awt.*;

public class GUIManager {

    public static ClickGUI ClickGUI;

    public static void init() {
        ClickGUI = (ClickGUI) ModuleManager.getModule(ClickGUI.class);
    }

    public static boolean isParticle() {
        return ModuleManager.getModule(Particles.class).isEnabled();
    }

    public static boolean isRainbow() {
        return ClickGUI.globalColor.getValue().getRainbow();
    }

    public static int getRed() {
        return ColorUtil.getRed(getColor3I());
    }

    public static int getGreen() {
        return ColorUtil.getGreen(getColor3I());
    }

    public static int getBlue() {
        return ColorUtil.getBlue(getColor3I());
    }

    public static int getAlpha() {
        return new Color(ClickGUI.globalColor.getValue().getColor()).getAlpha();
    }

    public static int getColor3I() {
        if (ClickGUI.globalColor.getValue().getRainbow()) {
            return getRainbowColor();
        } else {
            return ClickGUI.globalColor.getValue().getColor();
        }
    }

    public static int getColor4I() {
        if (ClickGUI.globalColor.getValue().getRainbow()) {
            int colorHex = getRainbowColor();
            return new Color(ColorUtil.getRed(colorHex), ColorUtil.getGreen(colorHex), ColorUtil.getBlue(colorHex), new Color(ClickGUI.globalColor.getValue().getColor()).getAlpha()).getRGB();
        } else {
            return ClickGUI.globalColor.getValue().getColor();
        }
    }

    public static int getRainbowColor() {
        final float[] hue = {(System.currentTimeMillis() % (360 * 32)) / (360f * 32) * ClickGUI.globalColor.getValue().getRainbowSpeed()};
        return Color.HSBtoRGB(hue[0], ClickGUI.globalColor.getValue().getRainbowSaturation(), ClickGUI.globalColor.getValue().getRainbowBrightness());
    }
}
