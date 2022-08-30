package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.module.modules.visuals.Chams;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.command.Command.mc;
import static org.lwjgl.opengl.GL11.*;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem {
    @Inject(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"))
    public void doRenderHook(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        ItemStack itemstack = entity.getItem();
        IBakedModel ibakedmodel = mc.getItemRenderer().itemRenderer.getItemModelWithOverrides(itemstack, entity.world, null);

        if ((ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.items.getValue() && !(Chams.instance.itemsRangeLimit.getValue() && mc.player.getDistance(entity) > Chams.instance.itemsRange.getValue())) || (ESP.INSTANCE.espTargetItems.getValue() && ESP.INSTANCE.espModeItems.getValue() == ESP.ModeItems.Wireframe && ModuleManager.getModule(ESP.class).isEnabled() && !(ESP.INSTANCE.espRangeLimitItems.getValue() && mc.player.getDistance(entity) > ESP.INSTANCE.espRangeItems.getValue()))) {
            renderItem(itemstack, ibakedmodel, (ESP.INSTANCE.espTargetItems.getValue() && ESP.INSTANCE.espModeItems.getValue() == ESP.ModeItems.Wireframe && ModuleManager.getModule(ESP.class).isEnabled() && !(ESP.INSTANCE.espRangeLimitItems.getValue() && mc.player.getDistance(entity) > ESP.INSTANCE.espRangeItems.getValue())), (ModuleManager.getModule(Chams.class).isEnabled() && Chams.instance.items.getValue() && !(Chams.instance.itemsRangeLimit.getValue() && mc.player.getDistance(entity) > Chams.instance.itemsRange.getValue())));
        }
    }

    private void newRenderModel(IBakedModel model, ItemStack stack, boolean lines, boolean chams) {
        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glEnable(GL_LINE_SMOOTH);
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        if (Chams.instance.itemBlend.getValue() && chams) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
        else GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (chams) {
            if (Chams.instance.itemTexture.getValue()) GlStateManager.enableTexture2D();
            if (!Chams.instance.itemLighting.getValue()) GlStateManager.disableLighting();
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            renderModel(model, Chams.instance.itemColor.getValue().getColor(), stack);
            if (Chams.instance.itemTexture.getValue()) GlStateManager.disableTexture2D();
        }


        if (lines) {
            GlStateManager.disableLighting();
            GL11.glLineWidth(ESP.INSTANCE.espItemWidth.getValue());
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            renderModel(model, ESP.INSTANCE.espColorItems.getValue().getColor(), stack);
        }

        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GL11.glEnable(GL_LIGHTING);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
    }

    private void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            mc.getRenderItem().renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), color, stack);
        }

        mc.getRenderItem().renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, stack);
        tessellator.draw();
    }

    public void renderItem(ItemStack stack, IBakedModel model, boolean lines, boolean chams)
    {
        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer())
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            }
            else
            {
                this.newRenderModel(model, stack, lines, chams);

            }
            GlStateManager.popMatrix();
        }
    }
}
