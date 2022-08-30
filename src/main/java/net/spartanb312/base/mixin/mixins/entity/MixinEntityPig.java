package net.spartanb312.base.mixin.mixins.entity;

import me.thediamondsword5.moloch.event.events.entity.EntityControlEvent;
import net.minecraft.entity.passive.EntityPig;
import net.spartanb312.base.BaseCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPig.class)
public class MixinEntityPig {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteeredHook(CallbackInfoReturnable<Boolean> cir) {
        EntityControlEvent event = new EntityControlEvent();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getSaddled", at = @At("HEAD"), cancellable = true)
    public void getSaddledHook(CallbackInfoReturnable<Boolean> cir) {
        EntityControlEvent event = new EntityControlEvent();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
    }
}
