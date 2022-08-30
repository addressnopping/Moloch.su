package me.thediamondsword5.moloch.engine.tasks;

import net.spartanb312.base.engine.RenderTask;
import net.spartanb312.base.utils.graphics.RenderUtils2D;

public class RoundedRectOutlineRenderTask implements RenderTask {
    float x, y, radius, endX, endY, width;
    boolean arcTopRight, arcTopLeft, arcDownRight, arcDownLeft;
    int color;

    public RoundedRectOutlineRenderTask(float x, float y, float radius, float endX, float endY, float width, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.endX = endX;
        this.endY = endY;
        this.width = width;
        this.arcTopRight = arcTopRight;
        this.arcTopLeft = arcTopLeft;
        this.arcDownRight = arcDownRight;
        this.arcDownLeft = arcDownLeft;
        this.color = color;
    }

    @Override
    public void onRender() {
        RenderUtils2D.drawCustomRoundedRectOutline(x, y, endX, endY, radius, width, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, false, false, color);
    }
}
