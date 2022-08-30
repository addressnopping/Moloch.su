package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.Chams;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelEnderCrystal.class)
public abstract class MixinModelEnderCrystal{
    @Shadow
    private ModelRenderer base;
    @Shadow
    private ModelRenderer glass;
    @Shadow
    private ModelRenderer cube;

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void renderModelHook(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && Chams.instance.crystalOneGlass.getValue()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.translate(0.0f, -0.5f, 0.0f);
            if (this.base != null) {
                this.base.render(scale);
            }

            GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, 0.8f + ageInTicks, 0.0f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            this.glass.render(scale);
            GlStateManager.scale(0.875f, 0.875f, 0.875f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(0.875f, 0.875f, 0.875f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
            this.cube.render(scale);
            GlStateManager.popMatrix();

            ci.cancel();
        }
    }
}
