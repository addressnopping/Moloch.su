package me.thediamondsword5.moloch.engine.tasks;

import net.spartanb312.base.engine.RenderTask;
import net.spartanb312.base.utils.graphics.RenderUtils2D;

public class RoundedRectRenderTask implements RenderTask {
    float x, y, radius, endX, endY;
    boolean arcTopRight, arcTopLeft, arcDownRight, arcDownLeft;
    int color;

    public RoundedRectRenderTask(float x, float y, float radius, float endX, float endY, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.endX = endX;
        this.endY = endY;
        this.arcTopRight = arcTopRight;
        this.arcTopLeft = arcTopLeft;
        this.arcDownRight = arcDownRight;
        this.arcDownLeft = arcDownLeft;
        this.color = color;
    }

    @Override
    public void onRender() {
        RenderUtils2D.drawRoundedRect(x, y, radius, endX, endY, false, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, color);
    }
}
