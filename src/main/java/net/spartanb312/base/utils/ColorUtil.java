package net.spartanb312.base.utils;

import net.spartanb312.base.module.modules.client.HUDEditor;
import net.spartanb312.base.gui.renderers.ClickGUIRenderer;
import net.spartanb312.base.gui.renderers.HUDEditorRenderer;

import java.awt.*;

public class ColorUtil {
    public static Color getColor(int hex) {
        return new Color(hex);
    }

    public static int getAlpha(int hex) {
        return hex >> 24 & 255;
    }

    public static int getRed(int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(int hex) {
        return hex & 255;
    }

    public static int getHoovered(int color, boolean isHoovered) {
        return isHoovered ? (color & 0x7F7F7F) << 1 : color;
    }

    public static int getBetterRainbow(float rainbowSpeed, float saturation, float brightness) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + 100) / 20.0) * rainbowSpeed;
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), saturation, brightness).getRGB();
    }

    public static int rainbow(int delay, float speed, float size, float saturation, float brightness) {
        double rainbowState = Math.ceil(((System.currentTimeMillis() * (double)speed) / 20.0) + (delay / 20.0)) / size;
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), saturation, brightness).getRGB();
    }

    /*
     * progress has to be from 0.0f to 300.0f
     */
    public static Color colorShift(Color startColor, Color endColor, float progressCounter) {
        int red = (int)MathUtilFuckYou.linearInterp(startColor.getRed(), endColor.getRed(), progressCounter);
        int green = (int)MathUtilFuckYou.linearInterp(startColor.getGreen(), endColor.getGreen(), progressCounter);
        int blue = (int)MathUtilFuckYou.linearInterp(startColor.getBlue(), endColor.getBlue(), progressCounter);

        return new Color(red, green, blue);
    }

    /*
     * value is from 0.0f - 1.0f
     */
    public static Color colorHSBChange(Color colorIn, float value, ColorHSBMode mode) {
        value = MathUtilFuckYou.clamp(value, 0.0f, 1.0f);
        float[] hsbVals = new float[3];
        Color.RGBtoHSB(colorIn.getRed(), colorIn.getGreen(), colorIn.getBlue(), hsbVals);

        if (mode == ColorHSBMode.Saturation) {
            return Color.getHSBColor(hsbVals[0], value, hsbVals[2]);
        }
        else {
            return Color.getHSBColor(hsbVals[0], hsbVals[1], value);
        }
    }

    public static Color rolledBrightness(Color color, float maxBrightness, float minBrightness, float speed, float offset, float size, boolean rightFlow, boolean useScrollCorrection) {
        float[] hsbVals = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbVals);

        float scrollCorrection = HUDEditor.instance.isEnabled() ? HUDEditorRenderer.scrolledY : ClickGUIRenderer.scrolledY;
        double brightnessState = (System.currentTimeMillis() * (double)speed / 37.0) + ((offset + (useScrollCorrection ? scrollCorrection : 0.0f)) * (rightFlow ? 1.0f : -1.0f));
        brightnessState %= (300.0 * size);
        brightnessState = (150.0f * Math.sin(((brightnessState - (75.0f * size)) * Math.PI) / (150.0f * size))) + 150.0f;
        float brightness = maxBrightness + ((float)brightnessState * ((minBrightness - maxBrightness) / 300.0f));

        return Color.getHSBColor(hsbVals[0], hsbVals[1], brightness);
    }


    public static Color rolledColor(Color color1, Color color2, int offset, float speed, float size) {
        double colorState = Math.ceil(((System.currentTimeMillis() * (double)speed) + offset) / 20.0) / (double)size;
        colorState %= 300;
        colorState = (float)((150.0f * Math.sin(((colorState - 75.0f) * Math.PI) / 150.0f)) + 150.0f);
        return colorShift(color1, color2, (float)colorState);
    }

    public static int rolledRainbowCircular(int angle, float speed, float saturation, float brightness) {
        double colorState = ((System.currentTimeMillis() * (double)speed) % 360.0) + angle;
        colorState %= 360.0;
        return java.awt.Color.getHSBColor((float)colorState / 360.0f, saturation, brightness).getRGB();
    }

    public enum ColorHSBMode {
        Saturation,
        Brightness
    }
}
