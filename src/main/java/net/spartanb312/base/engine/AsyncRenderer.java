package net.spartanb312.base.engine;

import me.thediamondsword5.moloch.engine.tasks.FadeRectRenderTask;
import me.thediamondsword5.moloch.engine.tasks.RectOutlineRenderTask;
import me.thediamondsword5.moloch.engine.tasks.RoundedRectOutlineRenderTask;
import me.thediamondsword5.moloch.engine.tasks.RoundedRectRenderTask;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.engine.tasks.TextRenderTask;
import net.spartanb312.base.engine.tasks.RectRenderTask;
import net.spartanb312.base.utils.graphics.font.CFontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AsyncRenderer {

    private final List<RenderTask> renderTasks = new ArrayList<>();
    private final List<RenderTask> tempTasks = new ArrayList<>();
    private final static int white = new Color(255, 255, 255, 255).getRGB();

    public abstract void onUpdate(ScaledResolution resolution, int mouseX, int mouseY);

    public void onUpdate0(ScaledResolution resolution, int mouseX, int mouseY) {
        tempTasks.clear();
        onUpdate(resolution, mouseX, mouseY);
        synchronized (renderTasks) {
            renderTasks.clear();
            renderTasks.addAll(tempTasks);
        }
    }

    public void onRender() {
        List<RenderTask> copiedTasks;
        synchronized (renderTasks) {
            copiedTasks = new ArrayList<>(renderTasks);
        }
        copiedTasks.forEach(RenderTask::onRender);
    }

    public void drawAsyncString(String text, float x, float y, boolean shadow) {
        tempTasks.add(new TextRenderTask(text, x, y, white, false, shadow));
    }

    public void drawAsyncString(String text, float x, float y, int color, boolean shadows) {
        tempTasks.add(new TextRenderTask(text, x, y, color, false, shadows));
    }

    public void drawAsyncCenteredString(String text, float x, float y, int color) {
        tempTasks.add(new TextRenderTask(text, x, y, color, true, false));
    }

    public void drawAsyncString(String text, float x, float y, int color, CFontRenderer fontRenderer) {
        tempTasks.add(new TextRenderTask(text, x, y, color, false, false, fontRenderer));
    }

    public void drawAsyncIcon(String icon, float x, float y, int color) {
        tempTasks.add(new TextRenderTask(icon, x, y, color, true));
    }

    public void drawAsyncCenteredString(String text, float x, float y, int color, boolean shadow) {
        tempTasks.add(new TextRenderTask(text, x, y, color, true, shadow));
    }

    public void drawAsyncRect(float x, float y, float endX, float endY, int color) {
        tempTasks.add(new RectRenderTask(x, y, endX, endY, color));
    }

    public void drawAsyncRectOutline(float x, float y, float endX, float endY, float width, int color) {
        tempTasks.add(new RectOutlineRenderTask(x, y, endX, endY, width, color));
    }

    public void drawAsyncRoundedRect(float x, float y, float radius, float endX, float endY, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, int color) {
        tempTasks.add(new RoundedRectRenderTask(x, y, radius, endX, endY, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, color));
    }

    public void drawAsyncRoundedRectOutline(float x, float y, float radius, float endX, float endY, float width, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, int color) {
        tempTasks.add(new RoundedRectOutlineRenderTask(x, y, radius, endX, endY, width, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, color));
    }

    public void drawAsyncFadeRect(float x, float y, float endX, float endY, float sizeFactor, float sizeMax, boolean fadeCenterRect, int color) {
        tempTasks.add(new FadeRectRenderTask(x, y, endX, endY, sizeFactor, sizeMax, fadeCenterRect, color));
    }

    public void drawAsyncRect(float x, float y, float endX, float endY, int color1, int color2, int color3, int color4) {
        tempTasks.add(new RectRenderTask(x, y, endX, endY, color1, color2, color3, color4));
    }
}
