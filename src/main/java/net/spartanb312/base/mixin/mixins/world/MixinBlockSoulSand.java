package net.spartanb312.base.mixin.mixins.world;

import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.movement.NoSlow;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSoulSand.class)
public class MixinBlockSoulSand {
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void entityBlockCollisionHook(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo info) {
        if (ModuleManager.getModule(NoSlow.class).isEnabled() && NoSlow.instance.soulSand.getValue()) info.cancel();
    }
}
