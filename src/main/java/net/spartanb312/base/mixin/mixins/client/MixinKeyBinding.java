package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.event.events.player.KeyEvent;
import net.minecraft.client.settings.KeyBinding;
import net.spartanb312.base.BaseCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Shadow
    private boolean pressed;

    @Inject(method = "isKeyDown", at = @At("RETURN"), cancellable = true)
    private void isKeyDownHook(CallbackInfoReturnable<Boolean> cir) {
        KeyEvent event = new KeyEvent(cir.getReturnValue(), pressed);
        BaseCenter.EVENT_BUS.post(event);
        cir.setReturnValue(event.info);
    }
}
