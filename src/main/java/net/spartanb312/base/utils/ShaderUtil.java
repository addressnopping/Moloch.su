package net.spartanb312.base.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;

import static net.spartanb312.base.utils.ItemUtils.mc;

public class ShaderUtil {
    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    public static ShaderUtil instance;

    public ShaderUtil() {
        instance = this;
    }

    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (Display.isActive() || Display.isVisible())
        {
            if (frameBuffer != null)
            {
                frameBuffer.framebufferClear();
                ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
                int factor = scale.getScaleFactor();
                int factor2 = scale.getScaledWidth();
                int factor3 = scale.getScaledHeight();
                if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3) {
                    frameBuffer.deleteFramebuffer();
                    frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                    frameBuffer.framebufferClear();
                }
                lastScale = factor;
                lastScaleWidth = factor2;
                lastScaleHeight = factor3;
            }
            else
            {
                frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
            }
        }
        else
        {
            if (frameBuffer == null)
            {
                frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
            }
        }

        return frameBuffer;
    }
}
