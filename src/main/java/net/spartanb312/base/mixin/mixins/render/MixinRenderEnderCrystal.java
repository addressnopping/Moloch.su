package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.utils.EntityUtil;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.module.modules.visuals.Chams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.command.Command.mc;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;

@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {
    @Final
    @Shadow
    private ModelBase modelEnderCrystalNoBase;
    private final ResourceLocation crystal = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");

    @Inject(method = {"doRender"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void doRenderHookPre(EntityEnderCrystal entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        float floatTicks = MathHelper.sin((entityIn.innerRotation + partialTicks) * 0.2f) / 2.0f + 0.5f;
        floatTicks = ((floatTicks * floatTicks) + floatTicks) * (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystalBobModify.getValue() ? Chams.instance.crystalBob.getValue() : 1.0f);

        if ((ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue()) || (ESP.INSTANCE.espTargetCrystals.getValue() && ESP.INSTANCE.espModeCrystals.getValue() == ESP.Mode.Wireframe && ModuleManager.getModule(ESP.class).isEnabled() && (ESP.INSTANCE.espWireframeWallEffectCrystal.getValue() || ESP.INSTANCE.crystalsCancelVanillaRender.getValue()) && !(ESP.INSTANCE.espRangeLimit.getValue() && mc.player.getDistance(entityIn) > ESP.INSTANCE.espRange.getValue()))) {

            GL11.glPushMatrix();
            GL11.glTranslated(x, y + (ModuleManager.getModule(Chams.class).isEnabled() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystals.getValue() ? Chams.instance.crystalYOffset.getValue() : 0.0f), z);
            GL11.glEnable(GL_BLEND);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL_LINE_SMOOTH);
            GL11.glEnable(GL_POLYGON_SMOOTH);
            GlStateManager.disableAlpha();

            if (Chams.instance.crystalScaleModify.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue()) GL11.glScalef(Chams.instance.crystalScale.getValue(), Chams.instance.crystalScale.getValue(), Chams.instance.crystalScale.getValue());

            if (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue()) {
                float alphaCrowdFactor = 1.0f;
                if (Chams.instance.crystalCrowdAlpha.getValue() && (EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, entityIn) <= Chams.instance.crystalCrowdAlphaRadius.getValue())) alphaCrowdFactor = Chams.instance.crystalCrowdEndAlpha.getValue() + ((1.0f - Chams.instance.crystalCrowdEndAlpha.getValue()) * (float)(EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, entityIn) / Chams.instance.crystalCrowdAlphaRadius.getValue()));

                java.awt.Color colorChams = Chams.instance.crystalColor.getValue().getColorColor();
                int alphaChams = Chams.instance.crystalColor.getValue().getAlpha();

                GlStateManager.depthMask(Chams.instance.crystalDepthMask.getValue());
                GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                if (Chams.instance.crystalBlend.getValue()) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);

                if (Chams.instance.crystalCull.getValue()) GL11.glEnable(GL_CULL_FACE);
                else GL11.glDisable(GL_CULL_FACE);

                if (Chams.instance.crystalWall.getValue() && !Chams.instance.crystalWallEffect.getValue()) GL11.glDepthRange(0.0, 0.01);

                if (Chams.instance.crystalLighting.getValue()) GL11.glEnable(GL_LIGHTING);
                else GL11.glDisable(GL_LIGHTING);

                if (Chams.instance.crystalTexture.getValue()) {
                    GlStateManager.enableAlpha();
                    GL11.glEnable(GL_TEXTURE_2D);
                    mc.getTextureManager().bindTexture(crystal);
                }
                else GL11.glDisable(GL_TEXTURE_2D);

                GL11.glColor4f(colorChams.getRed() / 255.0f, colorChams.getGreen() / 255.0f, colorChams.getBlue() / 255.0f, (alphaChams / 255.0f) * alphaCrowdFactor);
                modelEnderCrystalNoBase.render(entityIn, 0.0f, (entityIn.innerRotation + partialTicks) * 3.0f * (Chams.instance.crystalSpinModify.getValue() ? Chams.instance.crystalSpinSpeed.getValue() : 1), floatTicks * 0.2f, 0.0f, 0.0f, 0.0625f);

                if (Chams.instance.crystalWallEffect.getValue())
                    renderWallEffect(entityIn, alphaCrowdFactor, partialTicks, floatTicks);
                if (Chams.instance.crystalGlint.getValue())
                    renderGlint(entityIn, partialTicks, alphaCrowdFactor, floatTicks, Chams.instance.crystalGlintColor.getValue());

                if (Chams.instance.crystalWall.getValue() && !Chams.instance.crystalWallEffect.getValue()) GL11.glDepthRange(0.0, 1.0);
                if (Chams.instance.crystalTexture.getValue()) GlStateManager.disableAlpha();
                if (Chams.instance.crystalBlend.getValue()) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            if (ESP.INSTANCE.espTargetCrystals.getValue() && ESP.INSTANCE.espModeCrystals.getValue() == ESP.Mode.Wireframe && ModuleManager.getModule(ESP.class).isEnabled() && !(ESP.INSTANCE.espRangeLimit.getValue() && mc.player.getDistance(entityIn) > ESP.INSTANCE.espRange.getValue())) {
                GlStateManager.depthMask(false);
                GL11.glEnable(GL_ALPHA_TEST);
                GL11.glDisable(GL_TEXTURE_2D);
                GL11.glDisable(GL_LIGHTING);
                GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                GL11.glEnable(GL_CULL_FACE);
                GL11.glLineWidth(ESP.INSTANCE.espCrystalWidth.getValue());

                if (ESP.INSTANCE.crystalsCancelVanillaRender.getValue() || (((ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystalCancelVanillaRender.getValue()) || (ESP.INSTANCE.crystalsCancelVanillaRender.getValue()) && !ESP.INSTANCE.espWireframeWallEffectCrystal.getValue())) || (!ESP.INSTANCE.espWireframeOnlyWallCrystal.getValue() && ESP.INSTANCE.espWireframeWallEffectCrystal.getValue() && (ESP.INSTANCE.crystalsCancelVanillaRender.getValue() || ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystalCancelVanillaRender.getValue()))) {
                    java.awt.Color color = ESP.INSTANCE.espColorCrystals.getValue().getColorColor();
                    int alpha = ESP.INSTANCE.espColorCrystals.getValue().getAlpha();

                    if (Chams.instance.crystalCancelVanillaRender.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && !ESP.INSTANCE.espWireframeWallEffectCrystal.getValue()) GlStateManager.depthMask(false);
                    if (!ESP.INSTANCE.espWireframeWallEffectCrystal.getValue()) GL11.glDisable(GL_DEPTH_TEST);


                    GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha / 255.0f);
                    modelEnderCrystalNoBase.render(entityIn, 0.0f, (entityIn.innerRotation + partialTicks) * 3.0f * (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystalSpinModify.getValue() ? Chams.instance.crystalSpinSpeed.getValue() : 1), floatTicks * 0.2f, 0.0f, 0.0f, (Chams.instance.crystalScaleModify.getValue() && ModuleManager.getModule(Chams.class).isEnabled() ? Chams.instance.crystalScale.getValue() * 0.0625f : 0.0625f));

                    GlStateManager.depthMask(false);
                }

                if (ESP.INSTANCE.espWireframeWallEffectCrystal.getValue()) {
                    java.awt.Color wallColor = ESP.INSTANCE.espWireframeWallColorCrystals.getValue().getColorColor();
                    int wallAlpha = ESP.INSTANCE.espWireframeWallColorCrystals.getValue().getAlpha();


                    GL11.glEnable(GL_DEPTH_TEST);
                    GL11.glDepthFunc(GL_GREATER);
                    GL11.glColor4f(wallColor.getRed() / 255.0f, wallColor.getGreen() / 255.0f, wallColor.getBlue() / 255.0f, wallAlpha / 255.0f);
                    modelEnderCrystalNoBase.render(entityIn, 0.0f, (entityIn.innerRotation + partialTicks) * 3.0f * (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystalSpinModify.getValue() ? Chams.instance.crystalSpinSpeed.getValue() : 1), floatTicks * 0.2f, 0.0f, 0.0f, 0.0625f);
                    GL11.glDepthFunc(GL_LESS);
                    GL11.glDisable(GL_DEPTH_TEST);
                }

            }

            if (Chams.instance.crystalScaleModify.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue()) GL11.glScalef(1.0f / Chams.instance.crystalScale.getValue(), 1.0f / Chams.instance.crystalScale.getValue(), 1.0f / Chams.instance.crystalScale.getValue());

            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            GL11.glEnable(GL_LIGHTING);
            GL11.glDepthFunc(GL_LEQUAL);
            SpartanTessellator.releaseGL();
            GL11.glPopMatrix();
        }

        if ((ModuleManager.getModule(ESP.class).isEnabled() && ESP.INSTANCE.espTargetCrystals.getValue() && ESP.INSTANCE.crystalsCancelVanillaRender.getValue()) || (ModuleManager.getModule(Chams.class).isEnabled() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystals.getValue() && Chams.instance.crystalCancelVanillaRender.getValue()))
            ci.cancel();
    }


    @Inject(method = "doRender", at = @At(value = "RETURN"))
    public void doRenderHook2(EntityEnderCrystal entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        float floatTicks = MathHelper.sin((entityIn.innerRotation + partialTicks) * 0.2f) / 2.0f + 0.5f;
        floatTicks = ((floatTicks * floatTicks) + floatTicks) * (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystalBobModify.getValue() ? Chams.instance.crystalBob.getValue() : 1.0f);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y + (ModuleManager.getModule(Chams.class).isEnabled() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystals.getValue() ? Chams.instance.crystalYOffset.getValue() : 0.0f), z);

        if (!(ESP.INSTANCE.espWireframeOnlyWallCrystal.getValue() && ESP.INSTANCE.espWireframeWallEffectCrystal.getValue()) && ESP.INSTANCE.espTargetCrystals.getValue() && ESP.INSTANCE.espModeCrystals.getValue() == ESP.Mode.Wireframe && ModuleManager.getModule(ESP.class).isEnabled() && !(ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystalCancelVanillaRender.getValue())) {

            java.awt.Color color = ESP.INSTANCE.espColorCrystals.getValue().getColorColor();
            int alpha = ESP.INSTANCE.espColorCrystals.getValue().getAlpha();

            GL11.glEnable(GL_BLEND);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL_POLYGON_SMOOTH);
            GL11.glEnable(GL_LINE_SMOOTH);
            GL11.glEnable(GL_ALPHA_TEST);
            GL11.glDisable(GL_TEXTURE_2D);
            GL11.glDisable(GL_LIGHTING);
            GL11.glDisable(GL_DEPTH_TEST);
            GlStateManager.enableCull();
            if (Chams.instance.crystalScaleModify.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue()) GL11.glScalef(Chams.instance.crystalScale.getValue(), Chams.instance.crystalScale.getValue(), Chams.instance.crystalScale.getValue());

            GL11.glLineWidth(ESP.INSTANCE.espCrystalWidth.getValue());
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

            if (!ESP.INSTANCE.espWireframeOnlyWallCrystal.getValue() && ESP.INSTANCE.espWireframeWallEffectCrystal.getValue() && !ESP.INSTANCE.crystalsCancelVanillaRender.getValue() && !(ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystalCancelVanillaRender.getValue())) {
                GL11.glEnable(GL_DEPTH_TEST);
                GlStateManager.depthMask(false);
            }

            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha / 255.0f);
            modelEnderCrystalNoBase.render(entityIn, 0.0f, (entityIn.innerRotation + partialTicks) * 3.0f * (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && Chams.instance.crystalSpinModify.getValue() ? Chams.instance.crystalSpinSpeed.getValue() : 1), floatTicks * 0.2f, 0.0f, 0.0f, (Chams.instance.crystalScaleModify.getValue() && ModuleManager.getModule(Chams.class).isEnabled() ? Chams.instance.crystalScale.getValue() * 0.0625f : 0.0625f));

            if (Chams.instance.crystalScaleModify.getValue() && !Chams.instance.fixCrystalOutlineESP.getValue() && ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.crystals.getValue()) GL11.glScalef(1.0f / Chams.instance.crystalScale.getValue(), 1.0f / Chams.instance.crystalScale.getValue(), 1.0f / Chams.instance.crystalScale.getValue());

            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            SpartanTessellator.releaseGL();
            GL11.glEnable(GL_LIGHTING);
            GL11.glEnable(GL_DEPTH_TEST);
            GL11.glEnable(GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }


    private void renderWallEffect(EntityEnderCrystal entityIn, float alphaCrowdFactor, float partialTicks, float floatTicks) {
        java.awt.Color color = Chams.instance.crystalWallColor.getValue().getColorColor();
        Color color2 = Chams.instance.crystalWallColor.getValue();
        int alpha = Chams.instance.crystalWallColor.getValue().getAlpha();

        GlStateManager.depthMask(false);

        if (Chams.instance.crystalWallTexture.getValue()) {
            GlStateManager.enableAlpha();
            GL11.glEnable(GL_TEXTURE_2D);
            mc.getTextureManager().bindTexture(crystal);
        }
        else GL11.glDisable(GL_TEXTURE_2D);

        if (Chams.instance.crystalWallBlend.getValue()) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
        else GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthFunc(GL_GREATER);

        if (Chams.instance.crystalWallGlint.getValue())
            renderGlint(entityIn, partialTicks, alphaCrowdFactor, floatTicks, color2);
        else {
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, (alpha / 255.0f) * alphaCrowdFactor);
            modelEnderCrystalNoBase.render(entityIn, 0.0f, (entityIn.innerRotation + partialTicks) * 3.0f * (Chams.instance.crystalSpinModify.getValue() ? Chams.instance.crystalSpinSpeed.getValue() : 1), floatTicks * 0.2f, 0.0f, 0.0f, 0.0625f);
        }

        GL11.glDepthFunc(GL_LESS);
    }

    private void renderGlint(EntityEnderCrystal entityIn, float partialTicks, float alphaCrowdFactor, float floatTicks, Color color) {
        ResourceLocation glintTexture = null;

        switch (Chams.instance.crystalGlintMode.getValue()) {
            case LoadedPack: {
                glintTexture = Chams.instance.loadedTexturePackGlint;
                break;
            }

            case Gradient: {
                glintTexture = Chams.instance.gradientGlint;
                break;
            }

            case Lightning: {
                glintTexture = Chams.instance.lightningGlint;
                break;
            }

            case Swirls: {
                glintTexture = Chams.instance.swirlsGlint;
                break;
            }

            case Lines: {
                glintTexture = Chams.instance.linesGlint;
                break;
            }
        }

        if (glintTexture != null) mc.getTextureManager().bindTexture(glintTexture);
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glEnable(GL_BLEND);

        //alpha seems to be broken somehow so ig this would work :shrug:
        float alpha = color.getAlpha() / 255.0f;
        GL11.glColor4f((color.getColorColor().getRed() / 255.0f) * alpha * alphaCrowdFactor, (color.getColorColor().getGreen() / 255.0f) * alpha * alphaCrowdFactor, (color.getColorColor().getBlue() / 255.0f) * alpha * alphaCrowdFactor, 1.0f);

        GL11.glBlendFunc(GL_SRC_COLOR, GL_ONE);

        for (int i = 0; i < 2; ++i) {
            GL11.glMatrixMode(GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glScalef(Chams.instance.crystalGlintScale.getValue(), Chams.instance.crystalGlintScale.getValue(), Chams.instance.crystalGlintScale.getValue());
            if (Chams.instance.crystalGlintMove.getValue()) {
                GL11.glTranslatef(entityIn.ticksExisted * 0.01f * Chams.instance.crystalGlintMoveSpeed.getValue(), 0.0f, 0.0f);
            }
            GL11.glRotatef(30.0f - (i * 60.0f), 0.0f, 0.0f, 1.0f);
            GL11.glMatrixMode(GL_MODELVIEW);

            modelEnderCrystalNoBase.render(entityIn, 0.0f, (entityIn.innerRotation + partialTicks) * 3.0f * (Chams.instance.crystalSpinModify.getValue() ? Chams.instance.crystalSpinSpeed.getValue() : 1), floatTicks * 0.2f, 0.0f, 0.0f, 0.0625f);
        }

        GL11.glMatrixMode(GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL_MODELVIEW);

        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}
