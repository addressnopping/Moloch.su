package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.event.events.render.RenderEntityEvent;
import me.thediamondsword5.moloch.event.events.render.RenderEntityInvokeEvent;
import me.thediamondsword5.moloch.event.events.render.RenderEntityLayersEvent;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = RenderLivingBase.class, priority = 2147483596)
public class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render<T> {
    @Shadow
    protected ModelBase mainModel;

    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
        this.mainModel = modelBaseIn;
        this.shadowSize = shadowSizeIn;
    }

    @Inject(method = {"renderModel"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void doRender(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        RenderEntityEvent event = new RenderEntityEvent(this.mainModel, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (!this.bindEntityTexture(entityIn)) {
            return;
        }
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderModel"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V")}, cancellable = true)
    public void doRenderInvoke(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        RenderEntityInvokeEvent event = new RenderEntityInvokeEvent(this.mainModel, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (!this.bindEntityTexture(entityIn)) {
            return;
        }
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;doRenderLayer(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V"), cancellable = true)
    public void renderLayersHook(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        if (ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderOutlineFlag) ci.cancel();
    }

    @Inject(method = {"renderLayers"}, at = {@At("RETURN")}, cancellable = true)
    public void renderLayersAndShitINeedToSleep(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        RenderEntityLayersEvent event = new RenderEntityLayersEvent(RenderLivingBase.class.cast(this), this.mainModel, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
        BaseCenter.EVENT_BUS.post(event);
    }



    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableCull()V"))
    public void doRenderDisableCullHook(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderOutlineFlag)) GlStateManager.disableCull();
    }

    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableCull()V"))
    public void doRenderEnableCullHook(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderOutlineFlag)) GlStateManager.enableCull();
    }

    @Nullable
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}

