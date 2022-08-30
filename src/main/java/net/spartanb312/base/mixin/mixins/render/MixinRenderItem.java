package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {
    @Shadow protected abstract void renderModel(IBakedModel model, int color, ItemStack stack);

    @Inject(method = "renderEffect", at = @At("HEAD"), cancellable = true)
    public void renderEffectHook(IBakedModel model, CallbackInfo ci) {
        if (ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderProjectileFlag) ci.cancel();
    }

    @Inject(method = "renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void renderModelHook(IBakedModel model, ItemStack stack, CallbackInfo ci) {
        if (ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderProjectileFlag) {
            GL11.glEnable(GL_LINE_SMOOTH);
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GL11.glDisable(GL_LIGHTING);
            GL11.glLineWidth(ESP.INSTANCE.espProjectileWidth.getValue());
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

            renderModel(model, new Color(ESP.INSTANCE.espColorProjectiles.getValue().getColorColor().getRed(), ESP.INSTANCE.espColorProjectiles.getValue().getColorColor().getGreen(), ESP.INSTANCE.espColorProjectiles.getValue().getColorColor().getBlue(), ESP.INSTANCE.espColorProjectiles.getValue().getAlpha()).getRGB(), stack);

            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GL11.glEnable(GL_LIGHTING);
            GlStateManager.enableBlend();

            ci.cancel();
        }
    }
}
