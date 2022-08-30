package net.spartanb312.base.mixin.mixins.world;

import me.thediamondsword5.moloch.event.events.entity.ChorusEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.movement.Velocity;
import net.spartanb312.base.module.modules.visuals.NoRender;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {
    @Inject(method = {"handleMaterialAcceleration"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isPushedByWater()Z"), cancellable = true)
    public void pushedByWaterHook(AxisAlignedBB bb, Material materialIn, Entity entityIn, CallbackInfoReturnable<Boolean> ci) {
        if (ModuleManager.getModule(Velocity.class).isEnabled() && Velocity.instance.liquid.getValue())
            ci.setReturnValue(false);
    }

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void checkLightForHook(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.skyLightUpdate.getValue()) {
            cir.setReturnValue(false);
        }
    }
}
