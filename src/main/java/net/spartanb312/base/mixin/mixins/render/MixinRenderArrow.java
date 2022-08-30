package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderArrow.class)
public class MixinRenderArrow<T extends EntityArrow> extends Render<T> {

    protected MixinRenderArrow(RenderManager renderManager) {
        super(renderManager);
    }

    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
    public void doRenderHook(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
        if (ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderProjectileFlag)
            GL11.glColor4f(ESP.INSTANCE.espColorProjectiles.getValue().getColorColor().getRed() / 255.0f, ESP.INSTANCE.espColorProjectiles.getValue().getColorColor().getGreen() / 255.0f, ESP.INSTANCE.espColorProjectiles.getValue().getColorColor().getBlue() / 255.0f, ESP.INSTANCE.espColorProjectiles.getValue().getAlpha() / 255.0f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
