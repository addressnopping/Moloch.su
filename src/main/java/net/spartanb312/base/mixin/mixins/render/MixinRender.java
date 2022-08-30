package net.spartanb312.base.mixin.mixins.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.client.FriendManager;
import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.entity.projectile.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.spartanb312.base.utils.ItemUtils.mc;
import static me.thediamondsword5.moloch.module.modules.visuals.ESP.Mode.Glow;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    public void getTeamColorHook(T entityIn, CallbackInfoReturnable<Integer> ci) {
        if ((ESP.INSTANCE.espTargetSelf.getValue() && ESP.INSTANCE.espModeSelf.getValue() == ESP.ModeSelf.Glow) || (ESP.INSTANCE.espTargetPlayers.getValue() && ESP.INSTANCE.espModePlayers.getValue() == Glow) || (ESP.INSTANCE.espTargetMobs.getValue() && ESP.INSTANCE.espModeMobs.getValue() == Glow) || (ESP.INSTANCE.espTargetAnimals.getValue() && ESP.INSTANCE.espModeAnimals.getValue() == Glow) || (ESP.INSTANCE.espTargetCrystals.getValue() && ESP.INSTANCE.espModeCrystals.getValue() == Glow) || (ESP.INSTANCE.espTargetItems.getValue() && ESP.INSTANCE.espModeItems.getValue() == ESP.ModeItems.Glow)) {
            int color;
            if (entityIn instanceof EntityPlayer && ESP.INSTANCE.espTargetPlayers.getValue() && entityIn != mc.player) {
                if (FriendManager.isFriend(entityIn)) color = ESP.INSTANCE.espColorPlayersFriend.getValue().getColor();
                else if (EnemyManager.isEnemy(entityIn)) color = ESP.INSTANCE.espColorPlayersEnemy.getValue().getColor();
                else color = ESP.INSTANCE.espColorPlayers.getValue().getColor();
            }
            else if (entityIn == mc.player && ESP.INSTANCE.espTargetSelf.getValue()) color = ESP.INSTANCE.espColorSelf.getValue().getColor();
            else if ((entityIn instanceof EntityMob || entityIn instanceof EntitySlime || entityIn instanceof EntityGhast || entityIn instanceof EntityDragon) && ESP.INSTANCE.espTargetMobs.getValue()) color = ESP.INSTANCE.espColorMobs.getValue().getColor();
            else if ((entityIn instanceof EntityAnimal || entityIn instanceof EntitySquid) && ESP.INSTANCE.espTargetAnimals.getValue()) color = ESP.INSTANCE.espColorAnimals.getValue().getColor();
            else if (entityIn instanceof EntityEnderCrystal && ESP.INSTANCE.espTargetCrystals.getValue()) color = ESP.INSTANCE.espColorCrystals.getValue().getColor();
            else if (entityIn instanceof IProjectile || entityIn instanceof EntityShulkerBullet || entityIn instanceof EntityFireball || entityIn instanceof EntityEnderEye) color = ESP.INSTANCE.espColorProjectiles.getValue().getColor();
            else color = ESP.INSTANCE.espColorItems.getValue().getColor();
            ci.setReturnValue(color);
        }
    }
}
