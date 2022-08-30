package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    private void renderVignetterHook(CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.vignette.getValue())
            ci.cancel();
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.pumpkin.getValue())
            ci.cancel();
    }

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    private void renderPotionEffectsHook(ScaledResolution resolution, CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.potionIcons.getValue())
            ci.cancel();
    }
}
