package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {
    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At("HEAD"), cancellable = true)
    public void renderHookPre(TileEntity tileEntity, float partialTicks, int destroyStage, CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled()) {
            if (NoRender.INSTANCE.chests.getValue() && (tileEntity.getBlockType() == Blocks.CHEST || tileEntity.getBlockType() == Blocks.TRAPPED_CHEST))
                ci.cancel();

            if (NoRender.INSTANCE.enderChests.getValue() && tileEntity.getBlockType() == Blocks.ENDER_CHEST)
                ci.cancel();

            if (NoRender.INSTANCE.enchantingTableBook.getValue() && tileEntity.getBlockType() == Blocks.ENCHANTING_TABLE)
                ci.cancel();
        }
    }
}
