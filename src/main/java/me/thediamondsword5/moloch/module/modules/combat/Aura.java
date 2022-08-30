package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.module.modules.other.Freecam;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.concurrent.repeat.RepeatUnit;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.event.events.render.RenderModelEvent;
import net.spartanb312.base.mixin.mixins.accessor.AccessorCPacketPlayer;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.RenderHelper;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.*;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runRepeat;
import static net.spartanb312.base.utils.RotationUtil.*;

//TODO: add non-box render modes
@Parallel(runnable = true)
@ModuleInfo(name = "Aura", category = Category.COMBAT, description = "Attacks entities around you")
public class Aura extends Module {

    private final List<RepeatUnit> repeatUnits = new ArrayList<>();
    private static final HashMap<Entity, Float> targetData = new HashMap<>();
    public static final HashMap<Entity, Integer> moreTargetData = new HashMap<>();
    private static final HashMap<Entity, Integer> lastTargetData = new HashMap<>();
    private static final Timer attackTimer = new Timer();
    private int prevSlot;
    public static Entity target;
    private boolean flag;
    private boolean flag2;
    private boolean attackFlag;
    private long lastTime = -9999;
    public static final List<Vec3d> entityTriggerVecList = new ArrayList<>();
    public static Aura INSTANCE;

    Setting<Page> page = setting("Page", Page.Aura);

    Setting<Boolean> delay = setting("ModifyDelay", false).des("Don't use attack cooldown").whenAtMode(page, Page.Aura);
    Setting<Integer> attackDelay = setting("AttackDelay", 0, 0, 1000).des("Delay to attack target").whenTrue(delay).whenAtMode(page, Page.Aura);
    Setting<Float> range = setting("Range", 6.0f, 0.0f, 10.0f).des("Range to start attacking target").whenAtMode(page, Page.Aura);
    Setting<Boolean> targetPlayers = setting("TargetPlayers", true).des("Target players").whenAtMode(page, Page.Aura);
    Setting<Boolean> targetMobs = setting("TargetMobs", true).des("Target mobs").whenAtMode(page, Page.Aura);
    Setting<Boolean> targetAnimals = setting("TargetAnimals", false).des("Target animals").whenAtMode(page, Page.Aura);
    Setting<Boolean> targetMiscEntities = setting("TargetOtherEntities", false).des("Target other entities").whenAtMode(page, Page.Aura);
    Setting<Boolean> ignoreInvisible = setting("IgnoreInvisible", false).des("Doesn't target invisible entities").whenAtMode(page, Page.Aura);
    Setting<Boolean> legitMode = setting("LegitMode", false).des("Makes aura act more like actual player attack with left click").whenAtMode(page, Page.Aura);
    Setting<Boolean> packetAttack = setting("PacketAttack", false).des("Directsly sends CPacketUseEntity to hit entity").whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Boolean> stopSprint = setting("StopSprint", false).des("Stops sprinting during hit").whenAtMode(page, Page.Aura);
    Setting<Integer> randomClickPercent = setting("RandomClickPercent", 100, 1, 100).des("Chance of how likely aura will attack when aimed at target").whenTrue(legitMode).whenAtMode(page, Page.Aura);
    Setting<Boolean> checkWall = setting("CheckWall", false).des("Only attack target if they aren't behind wall").whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Float> wallRange = setting("WallRange", 3.0f, 0.0f, 10.0f).des("Range to start attacking target when target is behind a block").whenFalse(legitMode).whenFalse(checkWall).whenAtMode(page, Page.Aura);
    Setting<Boolean> rotate = setting("Rotate", false).des("Rotate to attack target").whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Boolean> slowRotate = setting("SlowRotate", false).des("Rotate more smoothly for strict servers").whenTrue(rotate).whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Integer> yawSpeed = setting("YawSpeed", 774, 1, 2000).des("Yaw speed i think").whenTrue(slowRotate).whenTrue(rotate).whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Float> attackYawRange = setting("YawHitRange", 11.8f, 0.0f, 90.0f).des("Yaw range in degrees to start attacking target").whenTrue(slowRotate).whenTrue(rotate).whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Boolean> triggerMode = setting("TriggerMode", false).des("Only attack target when facing it").whenFalse(legitMode).whenFalse(rotate).whenAtMode(page, Page.Aura);
    public Setting<Boolean> autoSwitch = setting("AutoSwitch", false).des("Automatically switch to weapon").whenFalse(legitMode).whenAtMode(page, Page.Aura);
    public Setting<Weapon> preferredWeapon = setting("PreferredWeapon", Weapon.Sword).des("Preferred weapon to switch to").whenFalse(legitMode).whenAtMode(page, Page.Aura);
    Setting<Boolean> switchBack = setting("SwitchBack", false).des("Switch back to previous slot on disable or when nothing is targeted").whenFalse(legitMode).whenTrue(autoSwitch).whenAtMode(page, Page.Aura);

    Setting<Boolean> offhandSwing = setting("OffhandSwing", false).des("Swing with offhand to attack").whenAtMode(page, Page.Render);
    Setting<RenderType> renderType = setting("RenderType", RenderType.Box).whenAtMode(page, Page.Render);
    Setting<BoxMode> renderBoxMode = setting("BoxMode", BoxMode.Solid).des("Mode of box render").whenAtMode(renderType, RenderType.Box).only(v -> renderType.getValue() != RenderType.None).whenAtMode(page, Page.Render);
    Setting<Float> boxLinesWidth = setting("LineWidth", 1.0f, 1.0f, 5.0f).des("Box render lines width").only(v -> renderType.getValue() == RenderType.Box && renderBoxMode.getValue() != BoxMode.Solid).whenAtMode(page, Page.Render);
    Setting<Color> color = setting("Color", new Color(new java.awt.Color(255, 100, 100, 125).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 125)).des("Aura target render color").only(v -> renderType.getValue() != RenderType.Box && renderType.getValue() != RenderType.None).whenAtMode(page, Page.Render);
    Setting<Color> solidColor = setting("SolidColor", new Color(new java.awt.Color(255, 100, 100, 14).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 14)).des("Aura target render fill color").only(v -> renderType.getValue() == RenderType.Box && renderBoxMode.getValue() != BoxMode.Lines).whenAtMode(page, Page.Render);
    Setting<Color> linesColor = setting("LinesColor", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).des("Aura target render outline color").only(v -> renderType.getValue() == RenderType.Box && renderBoxMode.getValue() != BoxMode.Solid).whenAtMode(page, Page.Render);
    Setting<Boolean> fadeOnTargetChange = setting("FadeOnTargetChange", true).des("Fade color when target is changed to another entity or nothing").only(v -> renderType.getValue() != RenderType.None).whenAtMode(page, Page.Render);
    Setting<Integer> fadeSpeedTargetChange = setting("FadeSpeedOnChange", 25, 2, 50).des("Render target change color fade speed").only(v -> renderType.getValue() != RenderType.None).whenTrue(fadeOnTargetChange).whenAtMode(page, Page.Render);
    Setting<Boolean> changeColorWhenHit = setting("ChangeColorOnHit", true).des("Change and fade color on a hit").only(v -> renderType.getValue() != RenderType.None).whenAtMode(page, Page.Render);
    Setting<Integer> fadeSpeedWhenHit = setting("FadeSpeedOnHit", 25, 2, 50).des("Render hit color fade speed").only(v -> renderType.getValue() != RenderType.None).whenTrue(changeColorWhenHit).whenAtMode(page, Page.Render);
    Setting<Color> hitColor = setting("HitColor", new Color(new java.awt.Color(255, 100, 100, 125).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 125)).des("Aura target render color").only(v -> renderType.getValue() != RenderType.Box && renderType.getValue() != RenderType.None).whenTrue(changeColorWhenHit).whenAtMode(page, Page.Render);
    Setting<Color> solidHitColor = setting("SolidHitColor", new Color(new java.awt.Color(255, 255, 255, 52).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 52)).des("Aura target render fill color").only(v -> renderType.getValue() == RenderType.Box && renderBoxMode.getValue() != BoxMode.Lines).whenTrue(changeColorWhenHit).whenAtMode(page, Page.Render);
    Setting<Color> linesHitColor = setting("LinesHitColor", new Color(new java.awt.Color(255, 255, 255, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 175)).des("Aura target render outline color").only(v -> renderType.getValue() == RenderType.Box && renderBoxMode.getValue() != BoxMode.Solid).whenTrue(changeColorWhenHit).whenAtMode(page, Page.Render);

    public Aura() {
        INSTANCE = this;
        repeatUnits.add(updateAura);
        repeatUnits.add(doRotate);
        repeatUnits.forEach(it -> {
            it.suspend();
            runRepeat(it);
        });
    }

    RepeatUnit doRotate = new RepeatUnit(() -> 1, () -> {
        if (rotate.getValue() && !legitMode.getValue()) {
            if (((checkPreferredWeapons() && !autoSwitch.getValue()) || (autoSwitch.getValue() && preferredWeapon.getValue() != Weapon.None) || (preferredWeapon.getValue() == Weapon.None))) {
                if (!targetData.isEmpty() && target != null) {
                    lookAtTarget(target, slowRotate.getValue(), yawSpeed.getValue());
                }
                else {
                    if (mc.player != null) {
                        resetRotation(slowRotate.getValue(), yawSpeed.getValue());
                    }
                }
            }
            else if (mc.player != null) {
                resetRotation(slowRotate.getValue(), yawSpeed.getValue());
            }
        }
    });


    RepeatUnit updateAura = new RepeatUnit(() -> 50, () -> {
        if (mc.world == null) return;

        if ((checkPreferredWeapons() && !autoSwitch.getValue()) || (autoSwitch.getValue() && preferredWeapon.getValue() != Weapon.None) || (preferredWeapon.getValue() == Weapon.None)) {
            if (fadeOnTargetChange.getValue()) flag2 = true;

            target = calcTarget();
            if (target == null) return;

            if (fadeOnTargetChange.getValue() && !target.isDead) lastTargetData.put(target, 0);
            if (changeColorWhenHit.getValue() && !target.isDead && !moreTargetData.containsKey(target)) moreTargetData.put(target, 300);

            attackTargets();
        }
        else if (fadeOnTargetChange.getValue() && renderType.getValue() != RenderType.None && !((checkPreferredWeapons() && !autoSwitch.getValue()) || (autoSwitch.getValue() && preferredWeapon.getValue() != Weapon.None) || (preferredWeapon.getValue() == Weapon.None))) {
            if (flag2) {
                target = calcTarget();
                if (target != null) lastTargetData.put(target, 0);
                flag2 = false;
            }
        }
        else {
            if (autoSwitch.getValue() && switchBack.getValue() && flag) {
                mc.player.inventory.currentItem = prevSlot;
                flag = false;
            }
        }
    });

    @Override
    public String getModuleInfo() {
        if (target != null) return target.getName();
        else return " ";
    }

    @Override
    public void onEnable() {
        repeatUnits.forEach(RepeatUnit::resume);
        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        repeatUnits.forEach(RepeatUnit::suspend);
        if (autoSwitch.getValue() && switchBack.getValue() && flag) {
            mc.player.inventory.currentItem = prevSlot;
        }
        if (rotate.getValue()) {
            newYaw = mc.player.rotationYawHead;
            shouldSpoofPacket = false;
            RotationUtil.flag = false;
        }
        moduleDisableFlag = true;
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (renderType.getValue() == RenderType.Box) {
            java.awt.Color theSolidColor = new java.awt.Color(solidColor.getValue().getColorColor().getRed(), solidColor.getValue().getColorColor().getGreen(), solidColor.getValue().getColorColor().getBlue(), solidColor.getValue().getAlpha());
            java.awt.Color theLinesColor = new java.awt.Color(linesColor.getValue().getColorColor().getRed(), linesColor.getValue().getColorColor().getGreen(), linesColor.getValue().getColorColor().getBlue(), linesColor.getValue().getAlpha());

            java.awt.Color theSolidColorLastTarget;
            java.awt.Color theLinesColorLastTarget;

            if (changeColorWhenHit.getValue() && !moreTargetData.isEmpty()) {

                for (Map.Entry<Entity, Integer> entry : new HashMap<>(moreTargetData).entrySet()) {
                    if (mc.player.getDistance(entry.getKey()) > range.getValue() || !fadeOnTargetChange.getValue()) {
                        moreTargetData.remove(entry.getKey());
                        continue;
                    }

                    int localValue = entry.getValue();

                    if (attackFlag && target != null && entry.getKey() == target) {
                        localValue = 0;
                        attackFlag = false;
                    }

                    if (lastTime == -9999) lastTime = System.currentTimeMillis() - 16;

                    for (int i = 0; i < (int) (((System.currentTimeMillis() - lastTime) / 50.0f) * fadeSpeedWhenHit.getValue()); i++) {
                        localValue += 1;
                    }
                    if (localValue >= 300) {
                        localValue = 300;
                    }

                    int red = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getColorColor().getRed(), theSolidColor.getRed(), localValue));
                    int green = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getColorColor().getGreen(), theSolidColor.getGreen(), localValue));
                    int blue = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getColorColor().getBlue(), theSolidColor.getBlue(), localValue));
                    int alpha = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getAlpha(), solidColor.getValue().getAlpha(), localValue));
                    java.awt.Color theNewSolidColor = new java.awt.Color(red, green, blue, alpha);

                    int red2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getColorColor().getRed(), theLinesColor.getRed(), localValue));
                    int green2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getColorColor().getGreen(), theLinesColor.getGreen(), localValue));
                    int blue2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getColorColor().getBlue(), theLinesColor.getBlue(), localValue));
                    int alpha2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getAlpha(), linesColor.getValue().getAlpha(), localValue));
                    java.awt.Color theNewLinesColor = new java.awt.Color(red2, green2, blue2, alpha2);

                    if (target != null && entry.getKey() == target) {
                        if (RenderHelper.isInViewFrustrum(target)) {
                            if (renderBoxMode.getValue() != BoxMode.Lines)
                                SpartanTessellator.drawBBFullBox(target, theNewSolidColor.getRGB());
                            if (renderBoxMode.getValue() != BoxMode.Solid)
                                SpartanTessellator.drawBBLineBox(target, boxLinesWidth.getValue(), theNewLinesColor.getRGB());
                        }
                    }

                    moreTargetData.put(entry.getKey(), localValue);
                }
            }
            else if (target != null) {
                if (RenderHelper.isInViewFrustrum(target)) {
                    if (renderBoxMode.getValue() != BoxMode.Lines)
                        SpartanTessellator.drawBBFullBox(target, theSolidColor.getRGB());
                    if (renderBoxMode.getValue() != BoxMode.Solid)
                        SpartanTessellator.drawBBLineBox(target, boxLinesWidth.getValue(), theLinesColor.getRGB());
                }
            }


            if (fadeOnTargetChange.getValue() && !lastTargetData.isEmpty()) {
                for (Map.Entry<Entity, Integer> entry : new HashMap<>(lastTargetData).entrySet()) {
                    if (entry.getKey() != target || (fadeOnTargetChange.getValue() && !((checkPreferredWeapons() && !autoSwitch.getValue()) || (autoSwitch.getValue() && preferredWeapon.getValue() != Weapon.None) || (preferredWeapon.getValue() == Weapon.None)))) {
                        if (!moreTargetData.containsKey(entry.getKey())) {
                            continue;
                        }

                        int localValue = entry.getValue();

                        if (lastTime == -9999) lastTime = System.currentTimeMillis() - 16;

                        for (int i = 0; i < (int) (((System.currentTimeMillis() - lastTime) / 50.0f) * fadeSpeedTargetChange.getValue()); i++) {
                            localValue += 1;
                        }

                        if (localValue >= 300) {
                            lastTargetData.remove(entry.getKey());
                            continue;
                        }


                        int alpha;
                        if (changeColorWhenHit.getValue()) {
                            int red = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getColorColor().getRed(), theSolidColor.getRed(), moreTargetData.get(entry.getKey())));
                            int green = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getColorColor().getGreen(), theSolidColor.getGreen(), moreTargetData.get(entry.getKey())));
                            int blue = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getColorColor().getBlue(), theSolidColor.getBlue(), moreTargetData.get(entry.getKey())));
                            int alpha5 = (int)(MathUtilFuckYou.linearInterp(solidHitColor.getValue().getAlpha(), solidColor.getValue().getAlpha(), moreTargetData.get(entry.getKey())));
                            alpha = (int)(MathUtilFuckYou.linearInterp(alpha5, 0, localValue));
                            theSolidColorLastTarget = new java.awt.Color(red, green, blue, alpha);
                        }
                        else {
                            alpha = (int)(MathUtilFuckYou.linearInterp(solidColor.getValue().getAlpha(), 0, localValue));
                            theSolidColorLastTarget = new java.awt.Color(theSolidColor.getRed(), theSolidColor.getGreen(), theSolidColor.getBlue(), alpha);
                        }

                        int alpha2;
                        if (changeColorWhenHit.getValue()) {
                            int red2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getColorColor().getRed(), theLinesColor.getRed(), moreTargetData.get(entry.getKey())));
                            int green2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getColorColor().getGreen(), theLinesColor.getGreen(), moreTargetData.get(entry.getKey())));
                            int blue2 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getColorColor().getBlue(), theLinesColor.getBlue(), moreTargetData.get(entry.getKey())));
                            int alpha6 = (int)(MathUtilFuckYou.linearInterp(linesHitColor.getValue().getAlpha(), linesColor.getValue().getAlpha(), moreTargetData.get(entry.getKey())));
                            alpha2 = (int)(MathUtilFuckYou.linearInterp(alpha6, 0, localValue));
                            theLinesColorLastTarget = new java.awt.Color(red2, green2, blue2, alpha2);
                        }
                        else {
                            alpha2 = (int)(MathUtilFuckYou.linearInterp(linesColor.getValue().getAlpha(), 0, localValue));
                            theLinesColorLastTarget = new java.awt.Color(theLinesColor.getRed(), theLinesColor.getGreen(), theLinesColor.getBlue(), alpha2);
                        }

                        if (entry.getKey() != null && RenderHelper.isInViewFrustrum(entry.getKey())) {
                            if (renderBoxMode.getValue() != BoxMode.Lines)
                                SpartanTessellator.drawBBFullBox(entry.getKey(), theSolidColorLastTarget.getRGB());
                            if (renderBoxMode.getValue() != BoxMode.Solid)
                                SpartanTessellator.drawBBLineBox(entry.getKey(), boxLinesWidth.getValue(), theLinesColorLastTarget.getRGB());
                        }

                        lastTargetData.put(entry.getKey(), localValue);
                    }
                }
            }

            if (changeColorWhenHit.getValue() || fadeOnTargetChange.getValue()) lastTime = System.currentTimeMillis();
        }
    }

    private void attackTargets() {
        if (target != null) {
            if (target.isDead) targetData.remove(target);
            if (!legitMode.getValue() && rotate.getValue() && slowRotate.getValue() && (calcNormalizedAngleDiff(normalizeAngle(getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), target.getPositionVector())[0]), newYaw) > attackYawRange.getValue())) return;
            if (autoSwitch.getValue()) {
                if (!flag) {
                    prevSlot = mc.player.inventory.currentItem;
                    flag = true;
                }
                ItemUtils.switchToSlot(ItemUtils.findItemInHotBar(preferredWeapon()));
            }
            if (!checkWall.getValue() || EntityUtil.isEntityVisible(target)) doAttack(target);
        }
    }

    private void doAttack(Entity entity) {
        if (ModuleManager.getModule(Criticals.class).isEnabled() && !Criticals.INSTANCE.packetMode.getValue() &&
                mc.player.onGround && target instanceof EntityLivingBase && Criticals.INSTANCE.canCrit() &&
        !Criticals.INSTANCE.disableWhenAura.getValue()) {

            Criticals.INSTANCE.doJumpCrit();

            if (mc.player.getCooledAttackStrength(0.5f) > 0.9f && mc.player.fallDistance > 0.1) {
                attackFlag = true;

                boolean sprinting = mc.player.isSprinting();

                if (stopSprint.getValue()) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }

                if (packetAttack.getValue()) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                }
                else {
                    mc.playerController.attackEntity(mc.player, entity);
                }
                mc.player.swingArm(offhandSwing.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                mc.player.resetCooldown();

                if (stopSprint.getValue() && sprinting) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }

                attackTimer.reset();
            }
        }
        else {
            if (attackTimer.passed(delay.getValue() ? attackDelay.getValue() : getWeaponCooldown())) {

                attackFlag = true;

                boolean sprinting = mc.player.isSprinting();

                if (stopSprint.getValue()) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }

                if (legitMode.getValue() && (Math.random() <= (randomClickPercent.getValue() / 100.0f))) {
                    mc.clickMouse();
                }
                else {
                    if (packetAttack.getValue()) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                    }
                    else {
                        mc.playerController.attackEntity(mc.player, entity);
                    }
                    mc.player.swingArm(offhandSwing.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    mc.player.resetCooldown();
                }

                if (stopSprint.getValue() && sprinting) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }

                attackTimer.reset();
            }
        }
    }

    private boolean checkEntity(Entity entity) {
        if (entity == null || mc.player == null) return false;
        else {
            return (!(FriendManager.isFriend(entity) || entity == mc.player) &&
                    ((targetPlayers.getValue() && entity instanceof EntityPlayer) ||
                            (targetMobs.getValue() && (EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon)) ||
                            (targetAnimals.getValue() && (EntityUtil.isEntityAnimal(entity))) ||
                            (targetMiscEntities.getValue() && !(entity instanceof EntityPlayer ||
                                    (EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) || EntityUtil.isEntityAnimal(entity) || entity instanceof EntityItem || entity instanceof IProjectile || entity instanceof EntityXPOrb))) &&
                    (mc.player.getDistance(entity) < range.getValue()) && (checkWall.getValue() ||
                    (EntityUtil.isEntityVisible(entity)
                            || mc.player.getDistance(entity) < wallRange.getValue())) &&
                    !(entity.isDead));
        }
    }

    private Entity calcTarget() {
        if (!rotate.getValue() && triggerMode.getValue()) {
            Vec3d startVec = mc.player.getPositionEyes(mc.getRenderPartialTicks());
            RayTraceResult ray = mc.player.rayTrace(6.0f, mc.getRenderPartialTicks());
            if (ray == null) return null;
            Vec3d raytracedVec = ray.hitVec;

            double[] extendVecHelper = MathUtilFuckYou.cartesianToPolar3d(raytracedVec.x - startVec.x, raytracedVec.y - startVec.y, raytracedVec.z - startVec.z);
            double[] extendVecHelper2 = MathUtilFuckYou.polarToCartesian3d(range.getValue(), extendVecHelper[1], extendVecHelper[2]);

            double extendFactorX = extendVecHelper2[0] / 200.0f;
            double extendFactorY = extendVecHelper2[1] / 200.0f;
            double extendFactorZ = extendVecHelper2[2] / 200.0f;

            entityTriggerVecList.clear();
            for (int i = 0; i < 200; i++) {
                extendFactorX += extendVecHelper2[0] / 200.0f;
                extendFactorY += extendVecHelper2[1] / 200.0f;
                extendFactorZ += extendVecHelper2[2] / 200.0f;
                Vec3d extendVec = new Vec3d(startVec.x + extendFactorX, startVec.y + extendFactorY, startVec.z + extendFactorZ);
                entityTriggerVecList.add(0, extendVec);
            }
        }

        for (Entity entity : EntityUtil.entitiesList()) {
            EntityUtil.entitiesListFlag = true;
            if (!checkEntity(entity)) {
                targetData.remove(entity);
                continue;
            }

            if (ModuleManager.getModule(Freecam.class).isEnabled() && Freecam.INSTANCE.camera == entity)
                continue;

            if (ignoreInvisible.getValue() && entity.isInvisible())
                continue;

            if (legitMode.getValue()) {
                if (mc.objectMouseOver.entityHit == entity) targetData.put(entity, mc.player.getDistance(entity));
                else targetData.remove(entity);
            }
            else {
                if (!rotate.getValue() && triggerMode.getValue()) {
                    if (!RenderHelper.isInViewFrustrum(entity)) {
                        targetData.remove(entity);
                        continue;
                    }
                    double collisionBorderSize = entity.getCollisionBorderSize();
                    AxisAlignedBB hitbox = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);

                    if (checkWall.getValue() && mc.objectMouseOver.entityHit != entity)
                        return null;

                    for (Vec3d vec : entityTriggerVecList) {
                        if (hitbox.contains(vec)) {
                            targetData.put(entity, mc.player.getDistance(entity));
                            break;
                        }
                        else targetData.remove(entity);
                    }
                }
                else targetData.put(entity, mc.player.getDistance(entity));
            }
        }
        EntityUtil.entitiesListFlag = false;
        
        if (mc.world.loadedEntityList.isEmpty() || !((checkPreferredWeapons() && !autoSwitch.getValue()) || (autoSwitch.getValue() && preferredWeapon.getValue() != Weapon.None) || (preferredWeapon.getValue() == Weapon.None))) targetData.clear();

        if (!targetData.isEmpty()) {
            if (legitMode.getValue()) {
                return mc.objectMouseOver.entityHit;
            }
            else {
                Entity minKey = null;
                float minValue = Float.MAX_VALUE;
                for (Map.Entry<Entity, Float> entry : new HashMap<>(targetData).entrySet()) {
                    float value = targetData.get(entry.getKey());
                    if (value < minValue) {
                        minValue = value;
                        minKey = entry.getKey();
                    }
                }

                return minKey;
            }
        }
        else {
            return null;
        }
    }

    private Item preferredWeapon() {
        switch (preferredWeapon.getValue()) {
            case Sword: {
                if (ItemUtils.isItemInHotbar(Items.DIAMOND_SWORD)) return Items.DIAMOND_SWORD;
                else {
                    if (ItemUtils.isItemInHotbar(Items.IRON_SWORD)) return Items.IRON_SWORD;
                    else {
                        if (ItemUtils.isItemInHotbar(Items.STONE_SWORD)) return Items.STONE_SWORD;
                        else {
                            if (ItemUtils.isItemInHotbar(Items.WOODEN_SWORD)) return Items.WOODEN_SWORD;
                            else if (ItemUtils.isItemInHotbar(Items.GOLDEN_SWORD)) return Items.GOLDEN_SWORD;
                        }
                    }
                }
            }

            case Axe: {
                if (ItemUtils.isItemInHotbar(Items.DIAMOND_AXE)) return Items.DIAMOND_AXE;
                else {
                    if (ItemUtils.isItemInHotbar(Items.IRON_AXE)) return Items.IRON_AXE;
                    else {
                        if (ItemUtils.isItemInHotbar(Items.STONE_AXE)) return Items.STONE_AXE;
                        else {
                            if (ItemUtils.isItemInHotbar(Items.WOODEN_AXE)) return Items.WOODEN_AXE;
                            else if (ItemUtils.isItemInHotbar(Items.GOLDEN_AXE)) return Items.GOLDEN_AXE;
                        }
                    }
                }
            }

            case PickAxe: {
                if (ItemUtils.isItemInHotbar(Items.DIAMOND_PICKAXE)) return Items.DIAMOND_PICKAXE;
                else {
                    if (ItemUtils.isItemInHotbar(Items.IRON_PICKAXE)) return Items.IRON_PICKAXE;
                    else {
                        if (ItemUtils.isItemInHotbar(Items.STONE_PICKAXE)) return Items.STONE_PICKAXE;
                        else {
                            if (ItemUtils.isItemInHotbar(Items.WOODEN_PICKAXE)) return Items.WOODEN_AXE;
                            else if (ItemUtils.isItemInHotbar(Items.GOLDEN_PICKAXE)) return Items.GOLDEN_PICKAXE;
                        }
                    }
                }
            }

            case Shovel: {
                if (ItemUtils.isItemInHotbar(Items.DIAMOND_SHOVEL)) return Items.DIAMOND_SHOVEL;
                else {
                    if (ItemUtils.isItemInHotbar(Items.IRON_SHOVEL)) return Items.IRON_SHOVEL;
                    else {
                        if (ItemUtils.isItemInHotbar(Items.STONE_SHOVEL)) return Items.STONE_SHOVEL;
                        else {
                            if (ItemUtils.isItemInHotbar(Items.WOODEN_SHOVEL)) return Items.WOODEN_AXE;
                            else if (ItemUtils.isItemInHotbar(Items.GOLDEN_SHOVEL)) return Items.GOLDEN_SHOVEL;
                        }
                    }
                }
            }
        }
        return Items.AIR;
    }

    public boolean checkPreferredWeapons() {
        if (mc.player != null) {
            switch (preferredWeapon.getValue()) {
                case Sword:
                    return (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD || mc.player.getHeldItemMainhand().getItem() == Items.IRON_SWORD || mc.player.getHeldItemMainhand().getItem() == Items.STONE_SWORD || mc.player.getHeldItemMainhand().getItem() == Items.WOODEN_SWORD || mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_SWORD);

                case Axe:
                    return (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_AXE || mc.player.getHeldItemMainhand().getItem() == Items.IRON_AXE || mc.player.getHeldItemMainhand().getItem() == Items.STONE_AXE || mc.player.getHeldItemMainhand().getItem() == Items.WOODEN_AXE || mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_AXE);

                case PickAxe:
                    return (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE || mc.player.getHeldItemMainhand().getItem() == Items.IRON_PICKAXE || mc.player.getHeldItemMainhand().getItem() == Items.STONE_PICKAXE || mc.player.getHeldItemMainhand().getItem() == Items.WOODEN_PICKAXE || mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_PICKAXE);

                case Shovel:
                    return (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SHOVEL || mc.player.getHeldItemMainhand().getItem() == Items.IRON_SHOVEL || mc.player.getHeldItemMainhand().getItem() == Items.STONE_SHOVEL || mc.player.getHeldItemMainhand().getItem() == Items.WOODEN_SHOVEL || mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_SHOVEL);
            }
        }
        return false;
    }

    //ty fobus
    private int getWeaponCooldown() {
        Item item = mc.player.getHeldItemMainhand().getItem();
        if (item instanceof ItemSword) {
            return 600;
        }
        if (item instanceof ItemPickaxe) {
            return 850;
        }
        if (item == Items.IRON_AXE) {
            return 1100;
        }
        if (item == Items.STONE_HOE) {
            return 500;
        }
        if (item == Items.IRON_HOE) {
            return 350;
        }
        if (item == Items.WOODEN_AXE || item == Items.STONE_AXE) {
            return 1250;
        }
        if (item instanceof ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE) {
            return 1000;
        }
        return 250;
    }

    enum Page {
        Aura,
        Render
    }

    enum Weapon {
        Sword,
        Axe,
        PickAxe,
        Shovel,
        None
    }

    enum RenderType {
        Box,
        Chams,
        Circle,
        None
    }

    enum BoxMode {
        Lines,
        Solid,
        Both
    }
}
