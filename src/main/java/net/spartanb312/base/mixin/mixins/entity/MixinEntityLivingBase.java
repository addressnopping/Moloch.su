package net.spartanb312.base.mixin.mixins.entity;

import me.thediamondsword5.moloch.event.events.entity.DeathEvent;
import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.module.modules.visuals.HeldModelTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.utils.RotationUtil.mc;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    @Shadow
    @Final
    private static DataParameter<Float> HEALTH;

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "notifyDataManagerChange", at = @At("RETURN"))
    public void notifyDataManagerChangeHook(DataParameter<?> key, CallbackInfo ci) {
        if (key.equals(HEALTH) && dataManager.get(HEALTH) <= 0.0 && mc.world != null && mc.world.isRemote) {
            DeathEvent event = new DeathEvent(this);
            BaseCenter.EVENT_BUS.post(event);
        }
    }

    @Inject(method = "swingArm", at = @At("HEAD"))
    public void swingArmHook(EnumHand hand, CallbackInfo ci) {
        if (this == mc.renderViewEntity && ModuleManager.getModule(HeldModelTweaks.class).isEnabled() &&HeldModelTweaks.INSTANCE.hitModify.getValue()
                && (!HeldModelTweaks.INSTANCE.isSwingInProgress || HeldModelTweaks.INSTANCE.swingProgressInt >= HeldModelTweaks.INSTANCE.swingAnimationMax() / 2 || HeldModelTweaks.INSTANCE.swingProgressInt < 0)) {
            HeldModelTweaks.INSTANCE.swingProgressInt = 0;
            HeldModelTweaks.INSTANCE.isSwingInProgress = true;
            HeldModelTweaks.INSTANCE.canEquipOffset = false;
        }
    }
}
