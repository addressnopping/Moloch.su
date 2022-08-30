package net.spartanb312.base.utils.graphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import me.thediamondsword5.moloch.mixinotherstuff.AccessorInterfaceShaderGroup;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static net.spartanb312.base.utils.ItemUtils.mc;
import static org.lwjgl.opengl.GL11.*;

/**
 * Author B_312
 * last update on Sep 12th 2021
 *
 * Updated by TheDiamondSword5 11/24/21
 */
public class RenderUtils2D {
    public static Framebuffer framebuffer;
    private static ShaderGroup shaderGroup;

    private static int prevScaleFactor;
    private static int prevScaleWidth;
    private static int prevScaleHeight;

    public static void prepareGl() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(GL_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableCull();
    }

    public static void releaseGl() {
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL_FLAT);
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    public static void drawRectOutline(float x, float y, float endX, float endY, int color, boolean topToggle, boolean bottomToggle) {
        drawCustomRectOutline(x, y, endX, endY, 1.0F, color, color, color, color, topToggle, bottomToggle);
    }

    public static void drawRectOutline(float x, float y, float endX, float endY, float lineWidth, int color, boolean topToggle, boolean bottomToggle) {
        drawCustomRectOutline(x, y, endX, endY, lineWidth, color, color, color, color, topToggle, bottomToggle);
    }

    public static void drawCustomRectOutline(float x, float y, float endX, float endY, int rightTop, int leftTop, int leftDown, int rightDown, boolean topToggle, boolean bottomToggle) {
        drawCustomRectOutline(x, y, endX, endY, 1.0F, rightTop, leftTop, leftDown, rightDown, topToggle, bottomToggle);
    }

    public static void drawCustomRectOutline(float x, float y, float endX, float endY, float lineWidth, int rightTop, int leftTop, int leftDown, int rightDown, boolean topToggle, boolean bottomToggle) {
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL_LINE_SMOOTH);

        VertexBuffer.begin(GL_LINES);
        if (!topToggle) {
            VertexBuffer.put(endX, y, rightTop);
            VertexBuffer.put(x, y, leftTop);
        }

        VertexBuffer.put(x, y, leftTop);
        VertexBuffer.put(x, endY, leftDown);

        if (!bottomToggle) {
            VertexBuffer.put(x, endY, leftDown);
            VertexBuffer.put(endX, endY, rightDown);
        }

        VertexBuffer.put(endX, endY, rightDown);
        VertexBuffer.put(endX, y, rightTop);
        VertexBuffer.end();

        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawRect(float x, float y, float endX, float endY, int color) {
        drawCustomRect(x, y, endX, endY, color, color, color, color);
    }

    public static void drawCustomRect(float x, float y, float endX, float endY, int rightTop, int leftTop, int leftDown, int rightDown) {
        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(endX, y, rightTop);
        VertexBuffer.put(x, y, leftTop);
        VertexBuffer.put(x, endY, leftDown);
        VertexBuffer.put(endX, endY, rightDown);
        VertexBuffer.end();
    }

    public static void drawCustomLine(float startX, float startY, float endX, float endY, float lineWidth, int startColor, int endColor) {
        glLineWidth(lineWidth);

        VertexBuffer.begin(GL_LINES);
        VertexBuffer.put(startX, startY, startColor);
        VertexBuffer.put(endX, endY, endColor);
        VertexBuffer.end();

        glLineWidth(1F);
    }

    public static void drawRoundedRect(float x, float y, float radius, float endX, float endY, boolean onlyTall, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, int color) {
        drawCustomRoundedRect(x, y, radius, endX, endY, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, false, false, onlyTall, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color);
    }

    public static void drawRoundedRectFade(float x, float y, float radius, boolean fadeCenterRect, boolean onlyTall, float endX, float endY, int color) {
        GlStateManager.disableAlpha();
        drawCustomRoundedRect(x, y, radius, endX, endY, true, true, true, true, true, fadeCenterRect, onlyTall, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color);
        GlStateManager.enableAlpha();
    }


    public static void drawCustomRoundedRect(float x, float y, float radiusFactor, float endX, float endY, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, boolean fadeMode, boolean fadeCenterRect, boolean onlyTall, int arcColorTopRight, int arcColorTopLeft, int arcColorDownRight, int arcColorDownLeft, int rightTopLeft, int leftTopLeft, int leftDownLeft, int rightDownLeft, int rightTopMid, int leftTopMid, int leftDownMid, int rightDownMid, int rightTopRight, int leftTopRight, int leftDownRight, int rightDownRight) {
        float radiusMax;

        if (endX - x > endY - y) {
            radiusMax = onlyTall ? (endX - x) / 2 : (endY - y) / 2;
        } else {
            radiusMax = (endX - x) / 2;
        }

        float radius = (radiusFactor * radiusMax);


        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(x + radius, y + radius, rightTopLeft);
        VertexBuffer.put(x, y + radius, fadeMode ? new Color(0, 0, 0, 0).getRGB() : leftTopLeft);
        VertexBuffer.put(x, endY - radius, fadeMode ? new Color(0, 0, 0, 0).getRGB() : leftDownLeft);
        VertexBuffer.put(x + radius, endY - radius, rightDownLeft);

        if (fadeMode) {
            if (fadeCenterRect) {
                VertexBuffer.put(endX - radius, y + radius, arcColorTopRight);
                VertexBuffer.put(x + radius, y + radius, arcColorTopLeft);
                VertexBuffer.put(x + radius, endY - radius, arcColorDownLeft);
                VertexBuffer.put(endX - radius, endY - radius, arcColorDownRight);
            }


            VertexBuffer.put(endX - radius, y, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(x + radius, y, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(x + radius, y + radius, arcColorTopLeft);
            VertexBuffer.put(endX - radius, y + radius, arcColorTopRight);

            VertexBuffer.put(endX - radius, endY - radius, arcColorDownRight);
            VertexBuffer.put(x + radius, endY - radius, arcColorDownLeft);
            VertexBuffer.put(x + radius, endY, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(endX - radius, endY, new Color(0, 0, 0, 0).getRGB());
        } else {
            VertexBuffer.put(endX - radius, y, rightTopMid);
            VertexBuffer.put(x + radius, y, leftTopMid);
            VertexBuffer.put(x + radius, endY, leftDownMid);
            VertexBuffer.put(endX - radius, endY, rightDownMid);
        }

        VertexBuffer.put(endX, y + radius, fadeMode ? new Color(0, 0, 0, 0).getRGB() : rightTopRight);
        VertexBuffer.put(endX - radius, y + radius, leftTopRight);
        VertexBuffer.put(endX - radius, endY - radius, leftDownRight);
        VertexBuffer.put(endX, endY - radius, fadeMode ? new Color(0, 0, 0, 0).getRGB() : rightDownRight);
        VertexBuffer.end();

        if (arcTopRight) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(endX - radius, y + radius, arcColorTopRight);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((endX - radius) + (radius * cos(i * (Math.PI / 40))), ((y + radius) - (radius * sin(i * (Math.PI / 40)))), fadeMode ? new Color(0, 0, 0, 0).getRGB() : arcColorTopRight);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX, y, rightTopRight);
            VertexBuffer.put(endX - radius, y, rightTopRight);
            VertexBuffer.put(endX - radius, y + radius, rightTopRight);
            VertexBuffer.put(endX, y + radius, rightTopRight);
            VertexBuffer.end();
        }

        if (arcTopLeft) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(x + radius, y + radius, arcColorTopLeft);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((x + radius) - (radius * cos(i * (Math.PI / 40))), ((y + radius) - (radius * sin(i * (Math.PI / 40)))), fadeMode ? new Color(0, 0, 0, 0).getRGB() : arcColorTopLeft);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(x + radius, y, rightTopRight);
            VertexBuffer.put(x, y, rightTopRight);
            VertexBuffer.put(x, y + radius, rightTopRight);
            VertexBuffer.put(x + radius, y + radius, rightTopRight);
            VertexBuffer.end();
        }

        if (arcDownLeft) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(x + radius, endY - radius, arcColorDownLeft);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((x + radius) - (radius * cos(i * (Math.PI / 40))), ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), fadeMode ? new Color(0, 0, 0, 0).getRGB() : arcColorDownLeft);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(x + radius, endY - radius, rightTopRight);
            VertexBuffer.put(x, endY - radius, rightTopRight);
            VertexBuffer.put(x, endY, rightTopRight);
            VertexBuffer.put(x + radius, endY, rightTopRight);
            VertexBuffer.end();
        }

        if (arcDownRight) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(endX - radius, endY - radius, arcColorDownRight);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((endX - radius) + (radius * cos(i * (Math.PI / 40))), ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), fadeMode ? new Color(0, 0, 0, 0).getRGB() : arcColorDownRight);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX, endY - radius, rightTopRight);
            VertexBuffer.put(endX - radius, endY - radius, rightTopRight);
            VertexBuffer.put(endX - radius, endY, rightTopRight);
            VertexBuffer.put(endX, endY, rightTopRight);
            VertexBuffer.end();
        }
    }

    public static void drawBetterRoundRectFade(float x, float y, float endX, float endY, float sizeFactor, float sizeMax, boolean onlyTall, boolean fadeCenterRect, boolean bottomToggle, int color) {
        GlStateManager.disableAlpha();
        float size = (sizeFactor * sizeMax);

        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(x, y, color);
        VertexBuffer.put(x - size, y, new Color(0, 0, 0, 0).getRGB());
        VertexBuffer.put(x - size, endY, new Color(0, 0, 0, 0).getRGB());
        VertexBuffer.put(x, endY, color);

        if (fadeCenterRect) {
            VertexBuffer.put(endX, y, color);
            VertexBuffer.put(x, y, color);
            VertexBuffer.put(x, endY, color);
            VertexBuffer.put(endX, endY, color);
        }


        VertexBuffer.put(endX, y - size, new Color(0, 0, 0, 0).getRGB());
        VertexBuffer.put(x, y - size, new Color(0, 0, 0, 0).getRGB());
        VertexBuffer.put(x, y, color);
        VertexBuffer.put(endX, y, color);

        if (!bottomToggle) {
            VertexBuffer.put(endX, endY, color);
            VertexBuffer.put(x, endY, color);
            VertexBuffer.put(x, endY + size, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(endX, endY + size, new Color(0, 0, 0, 0).getRGB());
        }

        VertexBuffer.put(endX + size, y, new Color(0, 0, 0, 0).getRGB());
        VertexBuffer.put(endX, y, color);
        VertexBuffer.put(endX, endY, color);
        VertexBuffer.put(endX + size, endY, new Color(0, 0, 0, 0).getRGB());
        VertexBuffer.end();

        //top right
        VertexBuffer.begin(GL_TRIANGLE_FAN);
        VertexBuffer.put(endX, y, color);
        for (int i = 0; i <= 20; ++i) {
            VertexBuffer.put((endX) + (size * cos(i * (Math.PI / 40))), ((y) - (size * sin(i * (Math.PI / 40)))), new Color(0, 0, 0, 0).getRGB());
        }
        VertexBuffer.end();

        //top left
        VertexBuffer.begin(GL_TRIANGLE_FAN);
        VertexBuffer.put(x, y, color);
        for (int i = 0; i <= 20; ++i) {
            VertexBuffer.put((x) - (size * cos(i * (Math.PI / 40))), ((y) - (size * sin(i * (Math.PI / 40)))), new Color(0, 0, 0, 0).getRGB());
        }
        VertexBuffer.end();

        //bottom left
        VertexBuffer.begin(GL_TRIANGLE_FAN);
        VertexBuffer.put(x, endY, color);
        for (int i = 0; i <= 20; ++i) {
            VertexBuffer.put((x) - (size * cos(i * (Math.PI / 40))), ((endY) + (size * sin(i * (Math.PI / 40)))), new Color(0, 0, 0, 0).getRGB());
        }
        VertexBuffer.end();

        //bottom right
        VertexBuffer.begin(GL_TRIANGLE_FAN);
        VertexBuffer.put(endX, endY, color);
        for (int i = 0; i <= 20; ++i) {
            VertexBuffer.put((endX) + (size * cos(i * (Math.PI / 40))), ((endY) + (size * sin(i * (Math.PI / 40)))), new Color(0, 0, 0, 0).getRGB());
        }
        VertexBuffer.end();
        GlStateManager.enableAlpha();
    }

    public static void drawCustomRoundedRectModuleEnableMode(float x, float y, float endX, float endY, float radius, boolean right, int color) {
        drawCustomGradientRoundedRectModuleEnableMode(x, y, endX, endY, radius, right, color, color);
    }

    public static void drawCustomGradientRoundedRectModuleEnableMode(float x, float y, float endX, float endY, float radius, boolean right, int innerColor, int outerColor) {
        float radiuss = radius * (endX - x);

        //mid
        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(endX, y + radiuss, right ? innerColor : outerColor);
        VertexBuffer.put(x, y + radiuss, right ? outerColor : innerColor);
        VertexBuffer.put(x, endY - radiuss, right ? outerColor : innerColor);
        VertexBuffer.put(endX, endY - radiuss, right ? innerColor : outerColor);

        if (right) {
            //top
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX, y, outerColor);
            VertexBuffer.put(x + radiuss, y, outerColor);
            VertexBuffer.put(x + radiuss, y + radiuss, innerColor);
            VertexBuffer.put(endX, y + radiuss, innerColor);

            //bottom
            VertexBuffer.put(endX, endY - radiuss, innerColor);
            VertexBuffer.put(x + radiuss, endY - radiuss, innerColor);
            VertexBuffer.put(x + radiuss, endY, outerColor);
            VertexBuffer.put(endX, endY, outerColor);
            VertexBuffer.end();

            //top
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(x + radiuss, y + radiuss, innerColor);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put(((x + radiuss) - (radiuss * cos(i * (Math.PI / 40)))), ((y + radiuss) - (radiuss * sin(i * (Math.PI / 40)))), outerColor);
            }
            VertexBuffer.end();

            //bottom
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(x + radiuss, endY - radiuss, innerColor);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((x + radiuss) - (radiuss * cos(i * (Math.PI / 40))), ((endY - radiuss) + (radiuss * sin(i * (Math.PI / 40)))), outerColor);
            }
            VertexBuffer.end();

        } else {
            //top
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX - radiuss, y, outerColor);
            VertexBuffer.put(x, y, outerColor);
            VertexBuffer.put(x, y + radiuss, innerColor);
            VertexBuffer.put(endX - radiuss, y + radiuss, innerColor);
            //VertexBuffer.end();

            //bottom
            //VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX - radiuss, endY - radiuss, innerColor);
            VertexBuffer.put(x, endY - radiuss, innerColor);
            VertexBuffer.put(x, endY, outerColor);
            VertexBuffer.put(endX - radiuss, endY, outerColor);
            VertexBuffer.end();

            //top
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(endX - radiuss, y + radiuss, innerColor);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put(((endX - radiuss) + (radiuss * cos(i * (Math.PI / 40)))), ((y + radiuss) - (radiuss * sin(i * (Math.PI / 40)))), outerColor);
            }
            VertexBuffer.end();

            //bottom
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(endX - radiuss, endY - radiuss, innerColor);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((endX - radiuss) + (radiuss * cos(i * (Math.PI / 40))), ((endY - radiuss) + (radiuss * sin(i * (Math.PI / 40)))), outerColor);
            }
            VertexBuffer.end();

        }
    }

    public static void drawCustomCategoryRoundedRect(float x, float y, float endX, float endY, float radiusFactor, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, boolean fadeRight, boolean fadeLeft, float fadeSize, int color) {
        float radiusMax;

        if (endX - x > endY - y) {
            radiusMax = (endY - y) / 2;
        } else {
            radiusMax = (endX - x) / 2;
        }

        float radius = (radiusFactor * radiusMax);

        //mid rect
        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(endX - radius, y, color);
        VertexBuffer.put(x + radius, y, color);
        VertexBuffer.put(x + radius, endY, color);
        VertexBuffer.put(endX - radius, endY, color);
        VertexBuffer.end();

        //left rect
        if (fadeLeft) {
            GlStateManager.disableAlpha();
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(x + radius, y + radius, color);
            VertexBuffer.put(x - fadeSize, y + radius, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(x - fadeSize, endY - radius, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(x + radius, endY - radius, color);
            VertexBuffer.end();
            GlStateManager.enableAlpha();
        } else {
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(x + radius, y + radius, color);
            VertexBuffer.put(x, y + radius, color);
            VertexBuffer.put(x, endY - radius, color);
            VertexBuffer.put(x + radius, endY - radius, color);
            VertexBuffer.end();
        }

        //right rect
        if (fadeRight) {
            GlStateManager.disableAlpha();
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX + fadeSize, y + radius, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.put(endX - radius, y + radius, color);
            VertexBuffer.put(endX - radius, endY - radius, color);
            VertexBuffer.put(endX + fadeSize, endY - radius, new Color(0, 0, 0, 0).getRGB());
            VertexBuffer.end();
            GlStateManager.enableAlpha();
        } else {
            VertexBuffer.begin(GL_QUADS);
            VertexBuffer.put(endX, y + radius, color);
            VertexBuffer.put(endX - radius, y + radius, color);
            VertexBuffer.put(endX - radius, endY - radius, color);
            VertexBuffer.put(endX, endY - radius, color);
            VertexBuffer.end();
        }


        if (arcTopRight) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(endX - radius, y + radius, color);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((endX - radius) + (radius * cos(i * (Math.PI / 40))), ((y + radius) - (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            if (fadeRight) {
                GlStateManager.disableAlpha();
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(endX + fadeSize, y, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.put(endX - radius, y, color);
                VertexBuffer.put(endX - radius, y + radius, color);
                VertexBuffer.put(endX + fadeSize, y + radius, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.end();
                GlStateManager.enableAlpha();
            }
            else {
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(endX, y, color);
                VertexBuffer.put(endX - radius, y, color);
                VertexBuffer.put(endX - radius, y + radius, color);
                VertexBuffer.put(endX, y + radius, color);
                VertexBuffer.end();
            }
        }

        if (arcTopLeft) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(x + radius, y + radius, color);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((x + radius) - (radius * cos(i * (Math.PI / 40))), ((y + radius) - (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            if (fadeLeft) {
                GlStateManager.disableAlpha();
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(x + radius, y, color);
                VertexBuffer.put(x - fadeSize, y, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.put(x - fadeSize, y + radius, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.put(x + radius, y + radius, color);
                VertexBuffer.end();
                GlStateManager.enableAlpha();
            }
            else {
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(x + radius, y, color);
                VertexBuffer.put(x, y, color);
                VertexBuffer.put(x, y + radius, color);
                VertexBuffer.put(x + radius, y + radius, color);
                VertexBuffer.end();
            }
        }

        if (arcDownLeft) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(x + radius, endY - radius, color);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((x + radius) - (radius * cos(i * (Math.PI / 40))), ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            if (fadeLeft) {
                GlStateManager.disableAlpha();
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(x + radius, endY - radius, color);
                VertexBuffer.put(x - fadeSize, endY - radius, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.put(x - fadeSize, endY, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.put(x + radius, endY, color);
                VertexBuffer.end();
                GlStateManager.enableAlpha();
            }
            else {
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(x + radius, endY - radius, color);
                VertexBuffer.put(x, endY - radius, color);
                VertexBuffer.put(x, endY, color);
                VertexBuffer.put(x + radius, endY, color);
                VertexBuffer.end();
            }
        }

        if (arcDownRight) {
            VertexBuffer.begin(GL_TRIANGLE_FAN);
            VertexBuffer.put(endX - radius, endY - radius, color);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((endX - radius) + (radius * cos(i * (Math.PI / 40))), ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            if (fadeRight) {
                GlStateManager.disableAlpha();
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(endX + fadeSize, endY - radius, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.put(endX - radius, endY - radius, color);
                VertexBuffer.put(endX - radius, endY, color);
                VertexBuffer.put(endX + fadeSize, endY, new Color(0, 0, 0, 0).getRGB());
                VertexBuffer.end();
                GlStateManager.enableAlpha();
            }
            else {
                VertexBuffer.begin(GL_QUADS);
                VertexBuffer.put(endX, endY - radius, color);
                VertexBuffer.put(endX - radius, endY - radius, color);
                VertexBuffer.put(endX - radius, endY, color);
                VertexBuffer.put(endX, endY, color);
                VertexBuffer.end();
            }
        }
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        drawCustomCircle(x, y, radius, color, color);
    }


    public static void drawCustomCircle(float x, float y, float radius, int innerColor, int outerColor) {
        VertexBuffer.begin(GL_TRIANGLE_FAN);
        VertexBuffer.put(x, y, innerColor);
        for (int i = 0; i <= 40; ++i) {
            VertexBuffer.put(x + (radius * cos(i * (Math.PI / 20))), y + (radius * sin(i * (Math.PI / 20))), outerColor);
        }
        VertexBuffer.end();
    }

    public static void drawCircleOutline(float x, float y, float radius, float lineWidth, int color) {
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL_LINE_SMOOTH);

        VertexBuffer.begin(GL_LINE_STRIP);
        for (int i = 0; i <= 40; ++i) {
            VertexBuffer.put((float)(x + (radius * cos(i * (Math.PI / 20)))), (float)(y + (radius * sin(i * (Math.PI / 20)))), color);
            VertexBuffer.put((float)(x + (radius * cos((i + 1) * (Math.PI / 20)))), (float)(y + (radius * sin((i + 1) * (Math.PI / 20)))), color);
        }
        VertexBuffer.end();

        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawCustomRoundedRectOutline(float x, float y, float endX, float endY, float radiusFactor, float lineWidth, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, boolean topToggle, boolean bottomToggle, int color) {
        float radiusMax;

        if (endX - x > endY - y) {
            radiusMax = (endY - y) / 2;
        } else {
            radiusMax = (endX - x) / 2;
        }

        float radius = (radiusFactor * radiusMax);

        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL_LINE_SMOOTH);

        if (arcTopRight) {
            VertexBuffer.begin(GL_LINE_STRIP);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((float) ((endX - radius) + (radius * cos(i * (Math.PI / 40)))), (float) ((y + radius) - (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_LINES);
            VertexBuffer.put(endX, y + radius, color);
            VertexBuffer.put(endX, y, color);
            if (!topToggle) {
                VertexBuffer.put(endX, y, color);
                VertexBuffer.put(endX - radius, y, color);
            }
            VertexBuffer.end();
        }

        if (!topToggle) {
            VertexBuffer.begin(GL_LINES);
            VertexBuffer.put(endX - radius, y, color);
            VertexBuffer.put(x + radius, y, color);
            VertexBuffer.end();
        }

        if (arcTopLeft) {
            VertexBuffer.begin(GL_LINE_STRIP);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((float) ((x + radius) - (radius * cos(i * (Math.PI / 40)))), (float) ((y + radius) - (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_LINES);
            if (!topToggle) {
                VertexBuffer.put(x + radius, y, color);
                VertexBuffer.put(x, y, color);
            }
            VertexBuffer.put(x, y, color);
            VertexBuffer.put(x, y + radius, color);
            VertexBuffer.end();
        }

        VertexBuffer.begin(GL_LINES);
        VertexBuffer.put(x, y + radius, color);
        VertexBuffer.put(x, endY - radius, color);
        VertexBuffer.end();

        if (arcDownLeft) {
            VertexBuffer.begin(GL_LINE_STRIP);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((float) ((x + radius) - (radius * cos(i * (Math.PI / 40)))), (float) ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_LINES);
            VertexBuffer.put(x, endY - radius, color);
            VertexBuffer.put(x, endY, color);
            if (!bottomToggle) {
                VertexBuffer.put(x, endY, color);
                VertexBuffer.put(x + radius, endY, color);
            }
            VertexBuffer.end();
        }

        if (!bottomToggle) {
            VertexBuffer.begin(GL_LINES);
            VertexBuffer.put(x + radius, endY, color);
            VertexBuffer.put(endX - radius, endY, color);
            VertexBuffer.end();
        }

        if (arcDownRight) {
            VertexBuffer.begin(GL_LINE_STRIP);
            for (int i = 0; i <= 20; ++i) {
                VertexBuffer.put((float) ((endX - radius) + (radius * cos(i * (Math.PI / 40)))), (float) ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), color);
            }
            VertexBuffer.end();
        } else {
            VertexBuffer.begin(GL_LINES);
            if (!bottomToggle) {
                VertexBuffer.put(endX - radius, endY, color);
                VertexBuffer.put(endX, endY, color);
            }
            VertexBuffer.put(endX, endY, color);
            VertexBuffer.put(endX, endY - radius, color);
            VertexBuffer.end();
        }

        VertexBuffer.begin(GL_LINES);
        VertexBuffer.put(endX, endY - radius, color);
        VertexBuffer.put(endX, y + radius, color);
        VertexBuffer.end();

        GL11.glDisable(GL_LINE_SMOOTH);
    }


    public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        drawCustomTriangle(x1, y1, x2, y2, x3, y3, color, color, color);
    }

    public static void drawTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float lineWidth, int color) {
        drawCustomTriangleOutline(x1, y1, x2, y2, x3, y3, lineWidth, color, color, color);
    }


    public static void drawCustomTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color1, int color2, int color3) {
        VertexBuffer.begin(GL_TRIANGLES);
        VertexBuffer.put(x1, y1, color1);
        VertexBuffer.put(x2, y2, color2);
        VertexBuffer.put(x3, y3, color3);
        VertexBuffer.end();
    }


    public static void drawCustomTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float lineWidth, int color1, int color2, int color3) {
        glLineWidth(lineWidth);

        VertexBuffer.begin(GL_LINE_STRIP);
        VertexBuffer.put(x1, y1, color1);
        VertexBuffer.put(x2, y2, color2);
        VertexBuffer.put(x3, y3, color3);
        VertexBuffer.put(x1, y1, color1);
        VertexBuffer.end();

        glLineWidth(1f);
    }


    public static void drawEquilateralTriangle(float x, float y, boolean upsideDown, float size, int color) {
        drawCustomEquilateralTriangle(x, y, size, upsideDown, color, color, color);
    }


    public static void drawCustomEquilateralTriangle(float x, float y, float size, boolean upsideDown, int top, int left, int right) {
        VertexBuffer.begin(GL_TRIANGLES);
        VertexBuffer.put(x, upsideDown ? (y + size) : (y - size), top);
        VertexBuffer.put(x + (size * cos(Math.PI + (Math.PI / 6.0f))), upsideDown ? (y + (size * sin(Math.PI + (Math.PI / 6.0f)))) : (y - (size * sin(Math.PI + (Math.PI / 6.0f)))), left);
        VertexBuffer.put(x + (size * cos((Math.PI / 6.0f) * -1.0f)), upsideDown ? (y + (size * sin(Math.PI + (Math.PI / 6.0f)))) : (y - (size * sin(Math.PI + (Math.PI / 6.0f)))), right);
        VertexBuffer.end();
    }


    public static void drawRhombus(float x, float y, float size, int color) {
        drawCustomRhombus(x, y, size, color, color, color, color);
    }


    public static void drawCustomRhombus(float x, float y, float size, int topColor, int bottomColor, int leftColor, int rightColor) {
        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(x, y - size, topColor);
        VertexBuffer.put(x - size, y, leftColor);
        VertexBuffer.put(x, y + size, bottomColor);
        VertexBuffer.put(x + size, y, rightColor);
        VertexBuffer.end();
    }

    public static void drawBlurAreaPre(float factor, float partialTicks) {
        //(x, y) is the bottom left corner
        ScaledResolution scale = new ScaledResolution(mc);

        int scaleFactor = scale.getScaleFactor();
        int scaleWidth = scale.getScaledWidth();
        int scaleHeight = scale.getScaledHeight();


        if (prevScaleFactor != scaleFactor || prevScaleWidth != scaleWidth || prevScaleHeight != scaleHeight || framebuffer == null || shaderGroup == null) {
            try {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }

                shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("minecraft:shaders/post/kawase_blur_.json"));
                shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
                framebuffer = ((AccessorInterfaceShaderGroup) shaderGroup).getListFramebuffers().get(0);
            }
            catch (Exception e) {}
        }

        prevScaleFactor = scaleFactor;
        prevScaleWidth = scaleWidth;
        prevScaleHeight = scaleHeight;

        for (Shader shader : ((AccessorInterfaceShaderGroup) shaderGroup).getListShaders()) {
            shader.getShaderManager().getShaderUniform("multiplier").set(factor);
        }

        shaderGroup.render(partialTicks);
        mc.getFramebuffer().bindFramebuffer(true);

        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, mc.displayWidth, mc.displayHeight, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);

        framebuffer.bindFramebufferTexture();
    }

    public static void drawBlurAreaPost() {
        framebuffer.unbindFramebufferTexture();
        ScaledResolution scale = new ScaledResolution(mc);
        GL11.glScalef(scale.getScaleFactor(), scale.getScaleFactor(), 1.0f);
    }

    public static void drawBlurRect(Tessellator tessellator, BufferBuilder bufferBuilder, float x, float y, float endX, float endY) {
        float normalTexX = x / mc.displayWidth;
        float normalTexY = 1.0f - (y / mc.displayHeight);
        float normalEndTexX = endX / mc.displayWidth;
        float normalEndTexY = 1.0f - (endY / mc.displayHeight);
        float f2 = (float)framebuffer.framebufferWidth / framebuffer.framebufferTextureWidth;
        float f3 = (float)framebuffer.framebufferHeight / framebuffer.framebufferTextureHeight;

        bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferBuilder.pos(endX, endY, 0.0f).tex(f2 * normalEndTexX, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(endX, y, 0.0f).tex(f2 * normalEndTexX, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x, y, 0.0f).tex(f2 * normalTexX, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x, endY, 0.0f).tex(f2 * normalTexX, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
    }

    public static void drawBlurRoundedRect(Tessellator tessellator, BufferBuilder bufferBuilder, float x, float y, float radiusFactor, float endX, float endY, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, boolean onlyTall) {
        float radiusMax;

        if (endX - x > endY - y) {
            radiusMax = onlyTall ? (endX - x) / 2 : (endY - y) / 2;
        } else {
            radiusMax = (endX - x) / 2;
        }

        float radius = (radiusFactor * radiusMax);

        float normalTexX = x / mc.displayWidth;
        float normalTexY = 1.0f - (y / mc.displayHeight);
        float normalEndTexX = endX / mc.displayWidth;
        float normalEndTexY = 1.0f - (endY / mc.displayHeight);
        float normalTexXPlusRad = (x + radius) / mc.displayWidth;
        float normalTexYPlusRad = 1.0f - ((y + radius) / mc.displayHeight);
        float normalEndTexXMinusRad = (endX - radius) / mc.displayWidth;
        float normalEndTexYMinusRad = 1.0f - ((endY - radius) / mc.displayHeight);

        float f2 = (float)framebuffer.framebufferWidth / framebuffer.framebufferTextureWidth;
        float f3 = (float)framebuffer.framebufferHeight / framebuffer.framebufferTextureHeight;

        bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferBuilder.pos(x + radius, y + radius, 0).tex(f2 * normalTexXPlusRad, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x, y + radius, 0).tex(f2 * normalTexX, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x, endY - radius, 0).tex(f2 * normalTexX, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x + radius, endY - radius, 0).tex(f2 * normalTexXPlusRad, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();

        bufferBuilder.pos(endX - radius, y, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x + radius, y, 0).tex(f2 * normalTexXPlusRad, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(x + radius, endY, 0).tex(f2 * normalTexXPlusRad, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(endX - radius, endY, 0).tex(f2 *normalEndTexXMinusRad , f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();

        bufferBuilder.pos(endX, y + radius, 0).tex(f2 * normalEndTexX, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(endX - radius, y + radius, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(endX - radius, endY - radius, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
        bufferBuilder.pos(endX, endY - radius, 0).tex(f2 * normalEndTexX, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
        tessellator.draw();

        if (arcTopRight) {
            bufferBuilder.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(endX - radius, y + radius, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
            for (int i = 0; i <= 20; ++i) {
                bufferBuilder.pos((endX - radius) + (radius * cos(i * (Math.PI / 40))), ((y + radius) - (radius * sin(i * (Math.PI / 40)))), 0).tex(f2 * (((endX - radius) + (radius * cos(i * (Math.PI / 40)))) / mc.displayWidth), f3 * (1.0f - (((y + radius) - (radius * sin(i * (Math.PI / 40)))) / mc.displayHeight))).color(255, 255, 255, 255).endVertex();
            }
        } else {
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(endX, y, 0).tex(f2 * normalEndTexX, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(endX - radius, y, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(endX - radius, y + radius, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(endX, y + radius, 0).tex(f2 * normalEndTexX, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
        }
        tessellator.draw();

        if (arcTopLeft) {
            bufferBuilder.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(x + radius, y + radius, 0).tex(f2 * normalTexXPlusRad, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
            for (int i = 0; i <= 20; ++i) {
                bufferBuilder.pos((x + radius) - (radius * cos((20 - i) * (Math.PI / 40))), ((y + radius) - (radius * sin((20 - i) * (Math.PI / 40)))), 0).tex(f2 * (((x + radius) - (radius * cos((20 - i) * (Math.PI / 40)))) / mc.displayWidth), f3 * (1.0f - (((y + radius) - (radius * sin((20 - i) * (Math.PI / 40)))) / mc.displayHeight))).color(255, 255, 255, 255).endVertex();
            }
        } else {
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(x + radius, y, 0).tex(f2 * normalTexXPlusRad, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(x, y, 0).tex(f2 * normalTexX, f3 * normalTexY).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(x, y + radius, 0).tex(f2 * normalTexX, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(x + radius, y + radius, 0).tex(f2 * normalTexXPlusRad, f3 * normalTexYPlusRad).color(255, 255, 255, 255).endVertex();
        }
        tessellator.draw();

        if (arcDownLeft) {
            bufferBuilder.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(x + radius, endY - radius, 0).tex(f2 * normalTexXPlusRad, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
            for (int i = 0; i <= 20; ++i) {
                bufferBuilder.pos((x + radius) - (radius * cos(i * (Math.PI / 40))), ((endY - radius) + (radius * sin(i * (Math.PI / 40)))), 0).tex(f2 * (((x + radius) - (radius * cos(i * (Math.PI / 40)))) / mc.displayWidth), f3 * (1.0f - (((endY - radius) + (radius * sin(i * (Math.PI / 40)))) / mc.displayHeight))).color(255, 255, 255, 255).endVertex();
            }
        } else {
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(x + radius, endY - radius, 0).tex(f2 * normalTexXPlusRad, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(x, endY - radius, 0).tex(f2 * normalTexX, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(x, endY, 0).tex(f2 * normalTexX, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(x + radius, endY, 0).tex(f2 * normalTexXPlusRad, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
        }
        tessellator.draw();

        if (arcDownRight) {
            bufferBuilder.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(endX - radius, endY - radius, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
            for (int i = 0; i <= 20; ++i) {
                bufferBuilder.pos((endX - radius) + (radius * cos((20 - i) * (Math.PI / 40))), ((endY - radius) + (radius * sin((20 - i) * (Math.PI / 40)))), 0).tex(f2 * (((endX - radius) + (radius * cos((20 - i) * (Math.PI / 40)))) / mc.displayWidth), f3 * (1.0f - (((endY - radius) + (radius * sin((20 - i) * (Math.PI / 40)))) / mc.displayHeight))).color(255, 255, 255, 255).endVertex();
            }
        } else {
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(endX, endY - radius, 0).tex(f2 * normalEndTexX, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(endX - radius, endY - radius, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalEndTexYMinusRad).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(endX - radius, endY, 0).tex(f2 * normalEndTexXMinusRad, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
            bufferBuilder.pos(endX, endY, 0).tex(f2 * normalEndTexX, f3 * normalEndTexY).color(255, 255, 255, 255).endVertex();
        }
        tessellator.draw();
    }
}
