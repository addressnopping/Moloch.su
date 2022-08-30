package net.spartanb312.base.mixin.mixins.entity;

import me.thediamondsword5.moloch.event.events.entity.TurnEvent;
import me.thediamondsword5.moloch.event.events.player.GroundedStepEvent;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.EntityDataManager;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.movement.Velocity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow public double motionX;
    @Shadow public double motionY;
    @Shadow public double motionZ;
    @Shadow public float stepHeight;
    @Shadow
    public EntityDataManager dataManager;

    @Inject(method = {"addVelocity"}, at = @At(value = "RETURN"))
    public void entityPushHook(double x, double y, double z, CallbackInfo ci) {
        if (ModuleManager.getModule(Velocity.class).isEnabled() && Velocity.instance.pushing.getValue()) {
            motionX -= x;
            motionY -= y;
            motionZ -= z;
        }
    }

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void turn(float yaw, float pitch, CallbackInfo ci) {
        Entity entityTurning = (Entity)(Object) this;
        TurnEvent event = new TurnEvent(entityTurning, yaw, pitch);
        BaseCenter.EVENT_BUS.post(event);

        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = "move", at = @At(value = "FIELD", target = "net/minecraft/entity/Entity.onGround:Z", ordinal = 1))
    private void moveHook(MoverType type, double x, double y, double z, CallbackInfo ci) {
        if (type == MoverType.SELF) {
            GroundedStepEvent event = new GroundedStepEvent(stepHeight);
            BaseCenter.EVENT_BUS.post(event);
            stepHeight = event.height;
        }
    }
}
