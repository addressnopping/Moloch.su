package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import me.thediamondsword5.moloch.module.modules.visuals.Chams;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.utils.ItemUtils.mc;
import static net.minecraft.client.renderer.entity.layers.LayerArmorBase.renderEnchantedGlint;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
    @Shadow
    public abstract T getModelFromSlot(EntityEquipmentSlot slotIn);
    @Shadow
    protected abstract T getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, T model);
    @Shadow
    protected abstract void setModelSlotVisible(T p_188359_1_, EntityEquipmentSlot slotIn);
    @Shadow
    public abstract ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type);
    @Shadow
    private boolean skipRenderGlint;
    private float newAlpha;
    @Mutable
    @Final
    @Shadow
    private final RenderLivingBase<?> renderer;

    public MixinLayerArmorBase(RenderLivingBase<?> rendererIn) {
        this.renderer = rendererIn;
    }

    @Inject(method = "renderEnchantedGlint", at = @At("HEAD"), cancellable = true)
    private static void renderEnchantedGlintHook(RenderLivingBase<?> p_188364_0_, EntityLivingBase p_188364_1_, ModelBase model, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_, CallbackInfo ci) {
        if (ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.renderOutlineFlag) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    private void renderArmorLayerHookPre(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        if (ModuleManager.getModule(Chams.class).isEnabled() && ((Chams.instance.playerChangeArmorAlpha.getValue()  && Chams.instance.self.getValue() && Chams.instance.selfChangeArmorAlpha.getValue() && entityLivingBaseIn == mc.player) || (Chams.instance.playerChangeArmorAlpha.getValue() && Chams.instance.otherPlayers.getValue() && entityLivingBaseIn instanceof EntityPlayer && entityLivingBaseIn != mc.player) || (Chams.instance.mobChangeArmorAlpha.getValue() && Chams.instance.mobs.getValue() && entityLivingBaseIn instanceof EntityMob))) {
            GlStateManager.enableBlend();
            renderArmorLayer(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, slotIn);
            GlStateManager.disableBlend();
            ci.cancel();
        }

        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.armor.getValue())
            ci.cancel();
    }

    private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn)
    {
        if (Chams.instance.playerChangeArmorAlpha.getValue() && entityLivingBaseIn instanceof EntityPlayer) newAlpha = Chams.instance.playerArmorAlpha.getValue() / 255.0f;
        if (Chams.instance.mobChangeArmorAlpha.getValue() && entityLivingBaseIn instanceof EntityMob) newAlpha = Chams.instance.mobArmorAlpha.getValue() / 255.0f;

        ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);

        if (itemstack.getItem() instanceof ItemArmor)
        {
            ItemArmor itemarmor = (ItemArmor)itemstack.getItem();

            if (itemarmor.getEquipmentSlot() == slotIn)
            {
                T t = getModelFromSlot(slotIn);
                t = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, t);
                t.setModelAttributes(this.renderer.getMainModel());
                t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
                this.setModelSlotVisible(t, slotIn);
                this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));

                {
                    if (itemarmor.hasOverlay(itemstack))
                    {
                        int i = itemarmor.getColor(itemstack);
                        float f = (float)(i >> 16 & 255) / 255.0F;
                        float f1 = (float)(i >> 8 & 255) / 255.0F;
                        float f2 = (float)(i & 255) / 255.0F;
                        GlStateManager.color(f, f1, f2, newAlpha);
                        t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                        this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
                    }
                    {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, newAlpha);
                        t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    }
                    if (!this.skipRenderGlint && itemstack.hasEffect())
                    {
                        renderEnchantedGlint(this.renderer, entityLivingBaseIn, t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    }
                }
            }
        }
    }
}

