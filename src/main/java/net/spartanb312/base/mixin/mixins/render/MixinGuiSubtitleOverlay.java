package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.event.decentraliized.DecentralizedRenderTickEvent;
import net.spartanb312.base.event.events.render.RenderOverlayEvent;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSubtitleOverlay.class)
public class MixinGuiSubtitleOverlay {

    @Inject(method = "renderSubtitles", at = @At("HEAD"))
    public void onRender2D(ScaledResolution resolution, CallbackInfo ci) {
        RenderUtils2D.prepareGl();
        RenderOverlayEvent event = new RenderOverlayEvent();
        DecentralizedRenderTickEvent.instance.post(event);
        BaseCenter.EVENT_BUS.post(event);
        RenderUtils2D.releaseGl();
    }

}
