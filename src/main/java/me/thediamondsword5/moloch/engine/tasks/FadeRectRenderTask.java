package me.thediamondsword5.moloch.engine.tasks;

import net.spartanb312.base.engine.RenderTask;
import net.spartanb312.base.utils.graphics.RenderUtils2D;

public class FadeRectRenderTask implements RenderTask {
    float x, y, endX, endY, sizeFactor, sizeMax;
    boolean fadeCenterRect;
    int color;

    public FadeRectRenderTask(float x, float y, float endX, float endY, float sizeFactor, float sizeMax, boolean fadeCenterRect, int color) {
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
        this.sizeFactor = sizeFactor;
        this.sizeMax = sizeMax;
        this.fadeCenterRect = fadeCenterRect;
        this.color = color;
    }

    @Override
    public void onRender() {
        RenderUtils2D.drawBetterRoundRectFade(x, y, endX, endY, sizeFactor, sizeMax, false, fadeCenterRect, false, color);
    }
}
