package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.event.events.render.RenderEntityPreEvent;
import me.thediamondsword5.moloch.module.modules.visuals.Chams;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.utils.EntityUtil;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.utils.ItemUtils.mc;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void renderEntity(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        RenderEntityPreEvent event = new RenderEntityPreEvent(entity);
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V", shift = At.Shift.BEFORE), cancellable = true)
    public void renderEntity1(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        if (ModuleManager.getModule(Chams.class).isEnabled() && (!Chams.instance.ignoreInvisible.getValue() || !entity.isInvisible()) && ((entity instanceof EntityPlayer && ((entity != mc.player && Chams.instance.otherPlayers.getValue()) || (entity == mc.player && Chams.instance.self.getValue())) && Chams.instance.players.getValue() && (Chams.instance.fixPlayerOutlineESP.getValue() || (Chams.instance.playerWall.getValue() && !Chams.instance.playerBypassArmor.getValue() && !Chams.instance.playerWallEffect.getValue()))) || (entity instanceof EntityMob && Chams.instance.mobs.getValue() && (Chams.instance.fixMobOutlineESP.getValue() || (Chams.instance.mobWall.getValue() && !Chams.instance.mobBypassArmor.getValue() && !Chams.instance.mobWallEffect.getValue()))) || ((EntityUtil.isEntityAnimal(entity)) && Chams.instance.animals.getValue() && Chams.instance.fixAnimalOutlineESP.getValue() && !Chams.instance.animalWallEffect.getValue()) || (entity instanceof EntityEnderCrystal && Chams.instance.crystals.getValue() && Chams.instance.fixCrystalOutlineESP.getValue() && !Chams.instance.crystalWallEffect.getValue())))
            GL11.glDepthRange(0.0, 0.01);
    }

    @Inject(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V", shift = At.Shift.AFTER))
    public void renderEntity2(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        if (ModuleManager.getModule(Chams.class).isEnabled() && (!Chams.instance.ignoreInvisible.getValue() || !entity.isInvisible()) && ((entity instanceof EntityPlayer && ((entity != mc.player && Chams.instance.otherPlayers.getValue()) || (entity == mc.player && Chams.instance.self.getValue())) && Chams.instance.players.getValue() && Chams.instance.fixPlayerOutlineESP.getValue() || (Chams.instance.playerWall.getValue() && !Chams.instance.playerBypassArmor.getValue() && !Chams.instance.playerWallEffect.getValue()))) || (entity instanceof EntityMob && Chams.instance.mobs.getValue() && (Chams.instance.fixMobOutlineESP.getValue() || (Chams.instance.mobWall.getValue() && !Chams.instance.mobBypassArmor.getValue() && !Chams.instance.mobWallEffect.getValue()))) || ((EntityUtil.isEntityAnimal(entity)) && Chams.instance.animals.getValue() && Chams.instance.fixAnimalOutlineESP.getValue() && !Chams.instance.animalWallEffect.getValue()) || (entity instanceof EntityEnderCrystal && Chams.instance.crystals.getValue() && Chams.instance.fixCrystalOutlineESP.getValue() && !Chams.instance.crystalWallEffect.getValue()))
            GL11.glDepthRange(0.0, 1.0);
    }
}
