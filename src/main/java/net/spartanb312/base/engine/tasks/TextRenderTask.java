package net.spartanb312.base.engine.tasks;

import net.spartanb312.base.client.FontManager;
import me.thediamondsword5.moloch.hud.huds.CustomHUDFont;
import net.spartanb312.base.engine.RenderTask;
import net.spartanb312.base.utils.graphics.font.CFontRenderer;
import org.lwjgl.opengl.GL11;

import static net.spartanb312.base.command.Command.mc;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class TextRenderTask implements RenderTask {

    String text;
    float x, y;
    int color;
    boolean centered, shadow, isIcon;
    CFontRenderer fontRenderer;
    FontManager fontManager;

    public TextRenderTask(String text, float x, float y, int color, boolean centered, boolean shadow) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.centered = centered;
        this.shadow = shadow;
        this.fontRenderer = FontManager.fontRenderer;
    }

    public TextRenderTask(String text, float x, float y, int color, boolean centered, boolean shadow, CFontRenderer fontRenderer) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.centered = centered;
        this.shadow = shadow;
        this.fontRenderer = fontRenderer;
    }

    public TextRenderTask(String text, float x, float y, int color, boolean isIcon) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.isIcon = isIcon;
        this.fontRenderer = FontManager.iconFont;
    }

    @Override
    public void onRender() {
        if (isIcon) {
            fontRenderer.drawString(text, x, y, color);
        }
        else {
            if (CustomHUDFont.instance.font.getValue() == CustomHUDFont.FontMode.Minecraft) {
                GL11.glEnable(GL_TEXTURE_2D);
                if (centered) fontManager.drawStringMcCentered(text, x, y, color, shadow);
                else mc.fontRenderer.drawString(text, (int)x, (int)y, color, shadow);
                GL11.glDisable(GL_TEXTURE_2D);
            }
            else {
                if (shadow) {
                    if (centered) fontManager.drawHUDShadowCentered(text, x, y, color);
                    else fontManager.drawHUDShadow(text, x, y, color);
                } else {
                    if (centered) fontManager.drawHUDCentered(text, x, y, color);
                    else fontManager.drawHUD(text, x, y, color);
                }
            }
        }
    }

}
