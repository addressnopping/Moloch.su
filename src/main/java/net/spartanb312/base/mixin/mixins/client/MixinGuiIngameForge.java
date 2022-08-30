package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.event.events.render.DrawScreenEvent;
import net.minecraftforge.client.GuiIngameForge;
import net.spartanb312.base.BaseCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderChat(II)V"))
    public void renderChatHook(float partialTicks, CallbackInfo ci) {
        DrawScreenEvent event = new DrawScreenEvent.Chat();
        BaseCenter.EVENT_BUS.post(event);
    }
}
