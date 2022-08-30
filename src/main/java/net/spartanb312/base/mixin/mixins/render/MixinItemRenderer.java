package net.spartanb312.base.mixin.mixins.render;

import com.google.common.base.MoreObjects;
import me.thediamondsword5.moloch.event.events.player.SwitchItemAnimationEvent;
import me.thediamondsword5.moloch.event.events.render.ItemModelEvent;
import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.module.modules.visuals.HeldModelTweaks;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.utils.RotationUtil.mc;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
    public void renderWaterOverlayTextureHook(float partialTicks, CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.waterOverlay.getValue())
            ci.cancel();
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void renderFireInFirstPersonHook(CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.fire.getValue())
            ci.cancel();
    }

    @Inject(method = "renderSuffocationOverlay", at = @At("HEAD"), cancellable = true)
    private void renderSuffocationOverlayHook(TextureAtlasSprite sprite, CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.blockOverlay.getValue())
            ci.cancel();
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    private void renderItemInFirstPersonHook1(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress, CallbackInfo ci) {
        ItemModelEvent event = new ItemModelEvent.Normal(hand, swingProgress);
        BaseCenter.EVENT_BUS.post(event);
    }

    @ModifyVariable(method = "rotateArm", at = @At("STORE"), ordinal = 1)
    public float rotateArm1(float value) {
        if (HeldModelTweaks.INSTANCE.noSway.getValue() && mc.renderViewEntity != null) {
            return mc.renderViewEntity.rotationPitch;
        }
        return value;
    }

    @ModifyVariable(method = "rotateArm", at = @At("STORE"), ordinal = 2)
    public float rotateArm2(float value) {
        if (HeldModelTweaks.INSTANCE.noSway.getValue() && mc.renderViewEntity != null) {
            return mc.renderViewEntity.rotationYaw;
        }
        return value;
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    public void renderItemInFirstPersonHook3(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress, CallbackInfo ci) {
        ItemModelEvent event = new ItemModelEvent.Pre(hand);
        BaseCenter.EVENT_BUS.post(event);
    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"), cancellable = true)
    public void transformFirstPersonHook(EnumHandSide hand, float p_187453_2_, CallbackInfo ci) {
        ItemModelEvent event = new ItemModelEvent.Hit();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V", shift = At.Shift.BEFORE))
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        if (ModuleManager.getModule(HeldModelTweaks.class).isEnabled() && HeldModelTweaks.INSTANCE.hitModify.getValue() && mc.player.swingingHand == hand && !(mc.player.isHandActive() && mc.player.getItemInUseCount() > 0 && mc.player.getActiveHand() == hand)) {
            float swingTranslateFactor = 1.0f - HeldModelTweaks.INSTANCE.swingTranslateFactor.getValue();
            float swingProgress = mc.player.getSwingProgress(mc.getRenderPartialTicks());
            boolean flag = hand == EnumHand.MAIN_HAND;
            EnumHandSide enumhandside = flag ? mc.player.getPrimaryHand() : mc.player.getPrimaryHand().opposite();
            int i = enumhandside == EnumHandSide.RIGHT ? 1 : -1;
            float f = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
            float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2.0f));
            float f2 = -0.2f * MathHelper.sin(swingProgress * (float) Math.PI);
            GL11.glTranslatef(i * -f * swingTranslateFactor, -f1 * swingTranslateFactor, -f2 * swingTranslateFactor);
        }
    }
}
