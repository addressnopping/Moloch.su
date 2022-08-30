package me.thediamondsword5.moloch.engine.tasks;

import net.spartanb312.base.engine.RenderTask;
import net.spartanb312.base.utils.graphics.RenderUtils2D;

public class RectOutlineRenderTask implements RenderTask {
    float x, y, endX, endY, width;
    int color;

    public RectOutlineRenderTask(float x, float y, float endX, float endY, float width, int color) {
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
        this.width = width;
        this.color = color;
    }

    @Override
    public void onRender() {
        RenderUtils2D.drawRectOutline(x, y, endX, endY, width, color, false, false);
    }
}
