package me.thediamondsword5.moloch.module.modules.combat;

import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.event.events.player.PlayerAttackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

@Parallel
@ModuleInfo(name = "Criticals", category = Category.COMBAT, description = "Force attacks to be criticals")
public class Criticals extends Module {

    public boolean flag = false;
    private boolean flag2 = false;
    private Entity target;
    public static Criticals INSTANCE;

    public Setting<Boolean> packetMode = setting("Packet", true).des("Use packets to force attacks to be criticals instead of mini jumps");
    Setting<Float> jumpHeight = setting("JumpHeight", 0.3f, 0.1f, 0.5f).des("Height of jump for criticals").whenFalse(packetMode);
    Setting<Boolean> onlyWeapon = setting("OnlyWeapon", true).des("Only force criticals when holding a sword or axe");
    Setting<Boolean> checkRaytrace = setting("CheckRaytrace", false).des("Only force criticals when your mouse is over the target entity").whenFalse(packetMode);
    public Setting<Boolean> disableWhenAura = setting("AuraNoCrits", false).des("Disable criticals when aura is actively attacking an entity (if you want criticals but don't want it to spam jump when using aura)").whenFalse(packetMode);

    public Criticals() {
        INSTANCE = this;
    }

    @Override
    public void onRenderTick() {
        if (!packetMode.getValue()) {
            if (target == null) return;

            if (flag && mc.player.fallDistance > 0.1 && canCrit() && (!checkRaytrace.getValue() || mc.objectMouseOver.entityHit == target)) {
                flag = false;
                mc.player.connection.sendPacket(new CPacketUseEntity(target));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                mc.player.resetCooldown();
            }

            if (flag && mc.player.fallDistance > 0.0)
                flag2 = true;

            if (flag2 && mc.player.onGround) {
                flag = false;
                flag2 = false;
                mc.player.connection.sendPacket(new CPacketUseEntity(target));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                mc.player.resetCooldown();
            }
        }

        if (packetMode.getValue() || (flag2 && (mc.player.onGround || mc.player.isInWeb || mc.player.isOnLadder() || mc.player.isRiding() ||
                mc.player.isPotionActive(MobEffects.BLINDNESS) || mc.player.isInWater() || mc.player.isInLava()))) {
            flag = false;
            flag2 = false;
        }
    }

    @Listener
    public void onPlayerAttackPre(PlayerAttackEvent event) {
        if (!mc.gameSettings.keyBindJump.isKeyDown() && canCrit() && mc.player.onGround &&
                event.target instanceof EntityLivingBase && !flag) {
            if (packetMode.getValue()) {
                if (mc.player.getCooledAttackStrength(0.5f) > 0.9f) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                }
            }
            else if (!(disableWhenAura.getValue() && ModuleManager.getModule(Aura.class).isEnabled() &&
                    ((Aura.INSTANCE.checkPreferredWeapons() && !Aura.INSTANCE.autoSwitch.getValue()) || (Aura.INSTANCE.autoSwitch.getValue() && Aura.INSTANCE.preferredWeapon.getValue() != Aura.Weapon.None) || (Aura.INSTANCE.preferredWeapon.getValue() == Aura.Weapon.None)))) {
                doJumpCrit();
                target = event.target;
                flag = true;
                event.cancel();
            }
        }
    }

    public void doJumpCrit() {
        mc.player.jump();
        mc.player.motionY = jumpHeight.getValue();
    }

    public boolean canCrit() {
        return (!mc.player.isInWeb && !mc.player.isOnLadder() && !mc.player.isRiding() &&
        !mc.player.isPotionActive(MobEffects.BLINDNESS) && !mc.player.isInWater() && !mc.player.isInLava()
        && (!onlyWeapon.getValue() || (isHoldingWeapon())));
    }

    private boolean isHoldingWeapon() {
        return mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD
                || mc.player.getHeldItemMainhand().getItem() == Items.IRON_SWORD ||
                mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_SWORD ||
                mc.player.getHeldItemMainhand().getItem() == Items.STONE_SWORD ||
                mc.player.getHeldItemMainhand().getItem() == Items.WOODEN_SWORD ||
                mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_AXE ||
                mc.player.getHeldItemMainhand().getItem() == Items.IRON_SWORD ||
                mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_AXE ||
                mc.player.getHeldItemMainhand().getItem() == Items.STONE_AXE ||
                mc.player.getHeldItemMainhand().getItem() == Items.WOODEN_AXE;
    }
}
