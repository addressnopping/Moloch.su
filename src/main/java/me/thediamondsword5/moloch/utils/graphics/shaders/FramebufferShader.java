package me.thediamondsword5.moloch.utils.graphics.shaders;

import me.thediamondsword5.moloch.mixinotherstuff.IEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public abstract class FramebufferShader extends Shader {
    public Minecraft mc = Minecraft.getMinecraft();
    public static Framebuffer frameBuffer;
    public boolean shadow;
    public float red;
    public float green;
    public float blue;
    public float alpha;
    public float radius = 2.0f;
    public float quality = 1.0f;
    public boolean entityShadows;
    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;

    public FramebufferShader(String fragmentShader, String vertextShader) {
        super(fragmentShader, vertextShader);
    }

    public void startDraw(float partialTicks, boolean singleFboMode) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        frameBuffer = setupFrameBuffer(frameBuffer, singleFboMode);
        frameBuffer.bindFramebuffer(true);
        entityShadows = mc.gameSettings.entityShadows;
        mc.gameSettings.entityShadows = false;
        ((IEntityRenderer) mc.entityRenderer).invokeSetupCameraTransform(partialTicks, 0);
    }


    public void stopDraw(Color color, int alpha, float radius, float quality) {
        mc.gameSettings.entityShadows = entityShadows;
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);

        red = color.getRed() / 255.0f;
        green = color.getGreen() / 255.0f;
        blue = color.getBlue() / 255.0f;
        this.alpha = alpha / 255.0f;
        this.radius = radius;
        this.quality = quality;

        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();

        startShader();
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(frameBuffer);
        stopShader();

        mc.entityRenderer.disableLightmap();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    /**
     * @author Megyn
     */
    //earthhack has least chinese shader stuff so here i go pasting again
    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer, boolean singleFboMode) {
        if (Display.isActive() || Display.isVisible())
        {
            if (frameBuffer != null)
            {
                if (singleFboMode) {
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
                else {
                    frameBuffer.deleteFramebuffer();
                    frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                }
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



    /**
     * @author TheSlowly
     */
    public void drawFramebuffer(Framebuffer framebuffer) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL_QUADS);
        GL11.glTexCoord2d(0.0, 1.0);
        GL11.glVertex2d(0.0,0.0);
        GL11.glTexCoord2d(0.0,0.0);
        GL11.glVertex2d(0.0, scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1.0, 0.0);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1.0,1.0);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), 0.0);
        GL11.glEnd();
    }
}
