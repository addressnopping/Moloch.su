package net.spartanb312.base.mixin.mixins.render;


import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.Chams;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
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
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

@Mixin(LayerElytra.class)
public class MixinLayerElytra {
    @Mutable
    @Final
    @Shadow
    protected final RenderLivingBase<?> renderPlayer;
    @Final
    @Shadow
    private final ModelElytra modelElytra = new ModelElytra();
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    private float newAlpha;

    public MixinLayerElytra(RenderLivingBase<?> p_i47185_1_) {
        this.renderPlayer = p_i47185_1_;
    }

    @Inject(method = "doRenderLayer", at = @At(value = "HEAD"), cancellable = true)
    public void doRenderLayerHook(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (ModuleManager.getModule(Chams.class).isEnabled() && ((Chams.instance.playerChangeArmorAlpha.getValue() && Chams.instance.self.getValue() && Chams.instance.selfChangeArmorAlpha.getValue() && entitylivingbaseIn == mc.player) || (Chams.instance.playerChangeArmorAlpha.getValue() && Chams.instance.otherPlayers.getValue() && entitylivingbaseIn instanceof EntityPlayer && entitylivingbaseIn != mc.player) || (Chams.instance.mobChangeArmorAlpha.getValue() && Chams.instance.mobs.getValue() && entitylivingbaseIn instanceof EntityMob))) {
            doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            ci.cancel();
        }
    }

    private void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (Chams.instance.playerChangeArmorAlpha.getValue() && entitylivingbaseIn instanceof EntityPlayer) newAlpha = Chams.instance.playerArmorAlpha.getValue() / 255.0f;
        if (Chams.instance.mobChangeArmorAlpha.getValue() && entitylivingbaseIn instanceof EntityMob) newAlpha = Chams.instance.mobArmorAlpha.getValue() / 255.0f;


        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, newAlpha);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            if (entitylivingbaseIn instanceof AbstractClientPlayer)
            {
                AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)entitylivingbaseIn;

                if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null)
                {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationElytra());
                }
                else if (abstractclientplayer.hasPlayerInfo() && abstractclientplayer.getLocationCape() != null && abstractclientplayer.isWearing(EnumPlayerModelParts.CAPE))
                {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationCape());
                }
                else
                {
                    this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                }
            }
            else
            {
                this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
            this.modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (itemstack.isItemEnchanted())
            {
                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entitylivingbaseIn, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

}

