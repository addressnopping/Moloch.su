package net.spartanb312.base.mixin.mixins.entity;

import com.mojang.authlib.GameProfile;
import me.thediamondsword5.moloch.event.events.player.OnUpdateWalkingPlayerEvent;
import me.thediamondsword5.moloch.event.events.player.PlayerMoveEvent;
import me.thediamondsword5.moloch.module.modules.other.Freecam;
import me.thediamondsword5.moloch.event.events.player.PlayerUpdateMoveEvent;
import me.thediamondsword5.moloch.module.modules.visuals.HeldModelTweaks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.event.events.client.ChatEvent;
import net.spartanb312.base.module.modules.movement.Velocity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.spartanb312.base.utils.ItemUtils.mc;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends EntityPlayer {
    @Shadow public MovementInput movementInput;

    public MixinEntityPlayerSP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Shadow
    protected abstract void updateAutoJump(float p_189810_1_, float p_189810_2_);

    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void sendChatPacket(String message, CallbackInfo ci) {
        ChatEvent event = new ChatEvent(message);
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At(value="HEAD")}, cancellable = true)
    private void pushHook(double x, double y, double z, CallbackInfoReturnable<Boolean> ci) {
        if (ModuleManager.getModule(Velocity.class).isEnabled() && Velocity.instance.pushing.getValue())
            ci.setReturnValue(false);
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V"), cancellable = true)
    private void onMoveStateUpdate(CallbackInfo ci) {
        PlayerUpdateMoveEvent event = new PlayerUpdateMoveEvent(movementInput);
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "isCurrentViewEntity", at = @At("HEAD"), cancellable = true)
    private void isCurrentViewEntityHook(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.getModule(Freecam.class).isEnabled() && Freecam.INSTANCE.camera != null)
            cir.setReturnValue(mc.getRenderViewEntity() == Freecam.INSTANCE.camera);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayerHook(CallbackInfo ci) {
        OnUpdateWalkingPlayerEvent event = new OnUpdateWalkingPlayerEvent(this.rotationYaw, this.rotationPitch);
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "move", at = @At("TAIL"), cancellable = true)
    public void moveHook(MoverType type, double x, double y, double z, CallbackInfo ci) {
        if (type == MoverType.SELF && mc.player != null) {
            PlayerMoveEvent event = new PlayerMoveEvent(mc.player);
            BaseCenter.EVENT_BUS.post(event);

            if (event.isCancelled()) {
                double prevX = posX;
                double prevZ = posZ;

                super.move(type, event.motionX, event.motionY, event.motionZ);
                updateAutoJump((float) (posX - prevX), (float) (posZ - prevZ));
                ci.cancel();
            }
        }
    }
}
