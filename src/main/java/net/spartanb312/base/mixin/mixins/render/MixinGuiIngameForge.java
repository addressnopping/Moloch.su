package net.spartanb312.base.mixin.mixins.render;

import me.thediamondsword5.moloch.event.events.render.RenderViewEntityGuiEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.spartanb312.base.BaseCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {
    @ModifyVariable(method = "renderAir", at = @At(value = "STORE", ordinal = 0))
    private EntityPlayer renderAirModify(EntityPlayer renderViewEntity) {
        RenderViewEntityGuiEvent event = new RenderViewEntityGuiEvent(renderViewEntity);
        BaseCenter.EVENT_BUS.post(event);

        return event.entityPlayer;
    }

    @ModifyVariable(method = "renderHealth", at = @At(value = "STORE", ordinal = 0))
    private EntityPlayer renderHealthModify(EntityPlayer renderViewEntity) {
        RenderViewEntityGuiEvent event = new RenderViewEntityGuiEvent(renderViewEntity);
        BaseCenter.EVENT_BUS.post(event);

        return event.entityPlayer;
    }

    @ModifyVariable(method = "renderFood", at = @At(value = "STORE", ordinal = 0))
    private EntityPlayer renderFoodModify(EntityPlayer renderViewEntity) {
        RenderViewEntityGuiEvent event = new RenderViewEntityGuiEvent(renderViewEntity);
        BaseCenter.EVENT_BUS.post(event);

        return event.entityPlayer;
    }

    @ModifyVariable(method = "renderHealthMount", at = @At(value = "STORE", ordinal = 0))
    private EntityPlayer renderHealthMountModify(EntityPlayer renderViewEntity) {
        RenderViewEntityGuiEvent event = new RenderViewEntityGuiEvent(renderViewEntity);
        BaseCenter.EVENT_BUS.post(event);

        return event.entityPlayer;
    }
}
