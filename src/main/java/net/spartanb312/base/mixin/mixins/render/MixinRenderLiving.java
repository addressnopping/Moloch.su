package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.client.renderer.entity.RenderLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLiving.class)
public class MixinRenderLiving {
    @Inject(method = "renderLeash", at = @At(value = "HEAD"), cancellable = true)
    private void renderLeashHook(CallbackInfo ci) {
        if (ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderOutlineFlag) ci.cancel();
    }
}
