package net.spartanb312.base.mixin.mixins.entity;

import me.thediamondsword5.moloch.event.events.player.TravelEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.spartanb312.base.BaseCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travelHook(float strafe, float vertical, float forward, CallbackInfo ci) {
        TravelEvent event = new TravelEvent();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            ci.cancel();
        }
    }
}
