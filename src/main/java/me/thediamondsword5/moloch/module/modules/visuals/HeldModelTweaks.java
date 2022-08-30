package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.event.events.render.ItemModelEvent;
import me.thediamondsword5.moloch.hud.huds.DebugThing;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.client.GameLoopEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.MathUtilFuckYou;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

@Parallel
@ModuleInfo(name = "HeldModelTweaks", category = Category.VISUALS, description = "Changes stuff about your held items in your viewmodel")
public class HeldModelTweaks extends Module {

    Setting<Page> page = setting("Page", Page.ItemModel);

    Setting<ItemModelPage> itemModelPage = setting("ItemModelPage", ItemModelPage.Main).whenAtMode(page, Page.ItemModel);
    Setting<Boolean> hitProgressMain = setting("HitProgressMain", false).des("Modifies hit progress of mainhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> hitProgressMainOffset = setting("HitProgressMainOffset", 0.3f, 0.0f, 1.0f).des("Hit offset of mainhand item").whenTrue(hitProgressMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainX = setting("MainX", 0.0f, -1.0f, 1.0f).des("X offset of mainhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainY = setting("MainY", 0.0f, -1.0f, 1.0f).des("Y offset of mainhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainZ = setting("MainZ", 0.0f, -1.0f, 1.0f).des("Z offset of mainhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);

    Setting<Float> mainRotateX = setting("MainRotateX", 0.0f, -180.0f, 180.0f).des("Rotation of mainhand item on X axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainRotateY = setting("MainRotateY", 0.0f, -180.0f, 180.0f).des("Rotation of mainhand item on Y axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainRotateZ = setting("MainRotateZ", 0.0f, -180.0f, 180.0f).des("Rotation of mainhand item on Z axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);

    Setting<Float> mainScaleX = setting("MainScaleX", 1.0f, 0.1f, 4.0f).des("Scale of mainhand item on X axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainScaleY = setting("MainScaleY", 1.0f, 0.1f, 4.0f).des("Scale of mainhand item on Y axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainScaleZ = setting("MainScaleZ", 1.0f, 0.1f, 4.0f).des("Scale of mainhand item on Z axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);

    Setting<Boolean> eatingModifyMain = setting("EatingModifyMain", false).des("Move mainhand item while you are eating from it").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);

    Setting<Float> mainEatingX = setting("MainEatingX", 0.0f, -5.0f, 5.0f).des("X offset of mainhand item while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainEatingY = setting("MainEatingY", 0.0f, -5.0f, 5.0f).des("Y offset of mainhand item while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainEatingZ = setting("MainEatingZ", 0.0f, -5.0f, 5.0f).des("Z offset of mainhand item while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);

    Setting<Float> mainEatingRotateX = setting("MainEatingRotateX", 0.0f, -180.0f, 180.0f).des("Rotation of mainhand item on X axis while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainEatingRotateY = setting("MainEatingRotateY", 0.0f, -180.0f, 180.0f).des("Rotation of mainhand item on Y axis while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainEatingRotateZ = setting("MainEatingRotateZ", 0.0f, -180.0f, 180.0f).des("Rotation of mainhand item on Z axis while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);

    Setting<Float> mainEatingScaleX = setting("MainEatingScaleX", 1.0f, 0.1f, 4.0f).des("Scale of mainhand item on X axis while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainEatingScaleY = setting("MainEatingScaleY", 1.0f, 0.1f, 4.0f).des("Scale of mainhand item on Y axis while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);
    Setting<Float> mainEatingScaleZ = setting("MainEatingScaleZ", 1.0f, 0.1f, 4.0f).des("Scale of mainhand item on Z axis while eating").whenTrue(eatingModifyMain).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Main);



    Setting<Boolean> hitProgressOff = setting("HitProgressOff", false).des("Modifies hit progress of offhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> hitProgressOffOffset = setting("HitProgressOffOffset", 0.7f, 0.0f, 1.0f).des("Hit offset of offhand item").whenTrue(hitProgressOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offX = setting("OffX", 0.0f, -1.0f, 1.0f).des("X offset of offhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offY = setting("OffY", 0.0f, -1.0f, 1.0f).des("Y offset of offhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offZ = setting("OffZ", 0.0f, -1.0f, 1.0f).des("Z offset of offhand item").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);

    Setting<Float> offRotateX = setting("OffRotateX", 0.0f, -180.0f, 180.0f).des("Rotation of offhand item on X axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offRotateY = setting("OffRotateY", 0.0f, -180.0f, 180.0f).des("Rotation of offhand item on Y axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offRotateZ = setting("OffRotateZ", 0.0f, -180.0f, 180.0f).des("Rotation of offhand item on Z axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);

    Setting<Float> offScaleX = setting("OffScaleX", 1.0f, 0.1f, 4.0f).des("Scale of offhand item on X axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offScaleY = setting("OffScaleY", 1.0f, 0.1f, 4.0f).des("Scale of offhand item on Y axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offScaleZ = setting("OffScaleZ", 1.0f, 0.1f, 4.0f).des("Scale of offhand item on Z axis").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);

    Setting<Boolean> eatingModifyOff = setting("EatingModifyOff", false).des("Move offhand while you are eating from it").whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);

    Setting<Float> offEatingX = setting("OffEatingX", 0.0f, -5.0f, 5.0f).des("X offset of offhand item while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offEatingY = setting("OffEatingY", 0.0f, -5.0f, 5.0f).des("Y offset of offhand item while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offEatingZ = setting("OffEatingZ", 0.0f, -5.0f, 5.0f).des("Z offset of offhand item while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);

    Setting<Float> offEatingRotateX = setting("OffEatingRotateX", 0.0f, -180.0f, 180.0f).des("Rotation of offhand item on X axis while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offEatingRotateY = setting("OffEatingRotateY", 0.0f, -180.0f, 180.0f).des("Rotation of offhand item on Y axis while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offEatingRotateZ = setting("OffEatingRotateZ", 0.0f, -180.0f, 180.0f).des("Rotation of offhand item on Z axis while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);

    Setting<Float> offEatingScaleX = setting("OffEatingScaleX", 1.0f, 0.1f, 4.0f).des("Scale of offhand item on X axis while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offEatingScaleY = setting("OffEatingScaleY", 1.0f, 0.1f, 4.0f).des("Scale of offhand item on Y axis while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);
    Setting<Float> offEatingScaleZ = setting("OffEatingScaleZ", 1.0f, 0.1f, 4.0f).des("Scale of offhand item on Z axis while eating").whenTrue(eatingModifyOff).whenAtMode(page, Page.ItemModel).whenAtMode(itemModelPage, ItemModelPage.Off);


    Setting<Float> switchAnimationThreshold = setting("SwitchAnimationThreshold", 0.0f, 0.0f, 1.0f).des("Switch progress to start stop switch animation from progressing").whenAtMode(page, Page.Animations);
    public Setting<Boolean> noSway = setting("NoSway", false).des("Instantly rotates items in heldmodel when you rotate instead of after a delay").whenAtMode(page, Page.Animations);
    //See MixinItemRenderer

    public Setting<Boolean> hitModify = setting("HitModify", false).des("Modifies your hit animation").whenAtMode(page, Page.Animations);
    Setting<Boolean> reverseSwingProgress = setting("ReverseSwingProgress", false).des("Make swing go backwards").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Axis> swingRotateAxis = setting("SwingRotateAxis", Axis.X).des("Axis on which the swing animation rotates item around").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> swingDepth = setting("SwingDepth", 80.0f, 0.0f, 180.0f).des("Degrees to rotate item on swing axis").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> hitX = setting("HitX", 0.0f, -1.0f, 1.0f).des("X offset of swinging hand item").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> hitY = setting("HitY", 0.0f, -1.0f, 1.0f).des("Y offset of swinging hand item").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> hitZ = setting("HitZ", 0.0f, -1.0f, 1.0f).des("Z offset of swinging hand item").whenTrue(hitModify).whenAtMode(page, Page.Animations);

    Setting<Float> hitRotateX = setting("HitRotateX", 0.0f, -180.0f, 180.0f).des("Rotation of swinging hand item on X axis").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> hitRotateY = setting("HitRotateY", 0.0f, -180.0f, 180.0f).des("Rotation of swinging hand item on Y axis").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> hitRotateZ = setting("HitRotateZ", 0.0f, -180.0f, 180.0f).des("Rotation of swinging hand item on Z axis").whenTrue(hitModify).whenAtMode(page, Page.Animations);

    Setting<Boolean> swordModifyDiff = setting("SwordModifyDiff", false).des("Different modifications to sword hit animation").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Axis> swingSwordRotateAxis = setting("SwingSwordRotateAxis", Axis.X).des("Axis on which the swing animation rotates sword around").whenTrue(swordModifyDiff).whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> swingSwordDepth = setting("SwingSwordDepth", 80.0f, 0.0f, 180.0f).des("Degrees to rotate sword on swing axis").whenTrue(swordModifyDiff).whenTrue(hitModify).whenAtMode(page, Page.Animations);
    Setting<Float> swordHitX = setting("SwordHitX", 0.0f, -1.0f, 1.0f).des("X offset of sword when swinging").whenTrue(hitModify).whenTrue(swordModifyDiff).whenAtMode(page, Page.Animations);
    Setting<Float> swordHitY = setting("SwordHitY", 0.0f, -1.0f, 1.0f).des("Y offset of sword when swinging").whenTrue(hitModify).whenTrue(swordModifyDiff).whenAtMode(page, Page.Animations);
    Setting<Float> swordHitZ = setting("SwordHitZ", 0.0f, -1.0f, 1.0f).des("Z offset of sword when swinging").whenTrue(hitModify).whenTrue(swordModifyDiff).whenAtMode(page, Page.Animations);

    Setting<Float> swordHitRotateX = setting("SwordHitRotateX", 0.0f, -180.0f, 180.0f).des("Rotation of sword when swinging on X axis").whenTrue(hitModify).whenTrue(swordModifyDiff).whenAtMode(page, Page.Animations);
    Setting<Float> swordHitRotateY = setting("SwordHitRotateY", 0.0f, -180.0f, 180.0f).des("Rotation of sword when swinging on Y axis").whenTrue(hitModify).whenTrue(swordModifyDiff).whenAtMode(page, Page.Animations);
    Setting<Float> swordHitRotateZ = setting("SwordHitRotateZ", 0.0f, -180.0f, 180.0f).des("Rotation of sword when swinging on Z axis").whenTrue(hitModify).whenTrue(swordModifyDiff).whenAtMode(page, Page.Animations);

    public Setting<Float> swingTranslateFactor = setting("SwingTranslateFactor", 1.0f, 0.0f, 2.0f).des("Factor to multiply the translation of hit animation").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    //See MixinItemRenderer
    public Setting<Boolean> equipProgressKeep = setting("EquipProgressKeep", false).des("Don't reset equip progress on swinging item").whenTrue(hitModify).whenAtMode(page, Page.Animations);
    //See MixinItemRenderer
    Setting<Float> swingSpeed = setting("SwingSpeed", 10.0f, 0.1f, 10.0f).des("Speed of swing animation").whenTrue(hitModify).whenAtMode(page, Page.Animations);

    public static HeldModelTweaks INSTANCE;
    public final Timer swingTimer = new Timer(20.0f);
    private float swingProgress = 0.0f;
    private float prevSwingProgress = 0.0f;
    public int swingProgressInt = 0;
    public boolean isSwingInProgress = false;
    public boolean canEquipOffset = false;


    public HeldModelTweaks() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {

        if (hitModify.getValue() && (mc.entityRenderer.itemRenderer.itemStackMainHand != mc.player.getHeldItemMainhand()
                || mc.entityRenderer.itemRenderer.itemStackOffHand != mc.player.getHeldItemOffhand())) {
            canEquipOffset = true;
        }

        if (hitModify.getValue() && equipProgressKeep.getValue() && !canEquipOffset) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.equippedProgressOffHand = 1.0f;
        }

        mc.entityRenderer.itemRenderer.equippedProgressMainHand = MathUtilFuckYou.clamp(mc.entityRenderer.itemRenderer.equippedProgressMainHand, switchAnimationThreshold.getValue(), 1.0f);
        mc.entityRenderer.itemRenderer.equippedProgressOffHand = MathUtilFuckYou.clamp(mc.entityRenderer.itemRenderer.equippedProgressOffHand, switchAnimationThreshold.getValue(), 1.0f);

        if (mc.entityRenderer.itemRenderer.equippedProgressMainHand == switchAnimationThreshold.getValue() && mc.entityRenderer.itemRenderer.itemStackMainHand != mc.player.getHeldItemMainhand()) {
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }

        if (mc.entityRenderer.itemRenderer.equippedProgressOffHand == switchAnimationThreshold.getValue() && mc.entityRenderer.itemRenderer.itemStackOffHand != mc.player.getHeldItemOffhand()) {
            mc.entityRenderer.itemRenderer.itemStackOffHand = mc.player.getHeldItemOffhand();
        }
    }

    @Listener
    public void onGameLoop(GameLoopEvent event) {
        if (hitModify.getValue()) {
            long l = Minecraft.getSystemTime();
            swingTimer.elapsedPartialTicks = ((float)(l - swingTimer.lastSyncSysClock) / swingTimer.tickLength) * swingSpeed.getValue() / 10.0f;
            swingTimer.lastSyncSysClock = l;
            swingTimer.renderPartialTicks += swingTimer.elapsedPartialTicks;
            swingTimer.elapsedTicks = (int)swingTimer.renderPartialTicks;
            swingTimer.renderPartialTicks -= (float)swingTimer.elapsedTicks;

            for (int i = 0; i < Math.min(10, swingTimer.elapsedTicks); ++i) {
                updateSwingProgress();
            }
        }
    }

    @Listener
    public void modifyModelTransMatrix(ItemModelEvent.Normal event) {
        boolean flag = event.hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? mc.player.getPrimaryHand() : mc.player.getPrimaryHand().opposite();

        if (!(mc.player.isHandActive() && mc.player.getItemInUseCount() > 0 && mc.player.getActiveHand() == event.hand)) {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (hitProgressMain.getValue() && flag1) {
                float e = -0.4f * MathHelper.sin(MathHelper.sqrt(hitProgressMainOffset.getValue()) * (float)Math.PI);
                float e1 = 0.2f * MathHelper.sin(MathHelper.sqrt(hitProgressMainOffset.getValue()) * ((float)Math.PI * 2f));
                float e2 = -0.2f * MathHelper.sin(hitProgressMainOffset.getValue() * (float)Math.PI);
                GL11.glTranslatef(e, e1, e2);
            }

            if (hitProgressOff.getValue() && !flag1) {
                float e = -0.4f * MathHelper.sin(MathHelper.sqrt(hitProgressOffOffset.getValue()) * (float)Math.PI);
                float e1 = 0.2f * MathHelper.sin(MathHelper.sqrt(hitProgressOffOffset.getValue()) * ((float)Math.PI * 2f));
                float e2 = -0.2f * MathHelper.sin(hitProgressOffOffset.getValue() * (float)Math.PI);
                GL11.glTranslatef(-e, e1, e2);
            }
        }

        if (event.hand == EnumHand.MAIN_HAND) {
            if (mc.player.getActiveHand() == EnumHand.MAIN_HAND && eatingModifyMain.getValue() && mc.player.isHandActive()) {
                GL11.glTranslatef(mainEatingX.getValue(), mainEatingY.getValue(), -mainEatingZ.getValue());
                GL11.glScalef(mainEatingScaleX.getValue(), mainEatingScaleY.getValue(), mainEatingScaleZ.getValue());
            }
            else {
                GL11.glTranslatef(mainX.getValue(), mainY.getValue(), -mainZ.getValue());
                GL11.glScalef(mainScaleX.getValue(), mainScaleY.getValue(), mainScaleZ.getValue());
            }
        }

        if (event.hand == EnumHand.OFF_HAND) {
            if (mc.player.getActiveHand() == EnumHand.OFF_HAND && eatingModifyOff.getValue() && mc.player.isHandActive()) {
                GL11.glTranslatef(-offEatingX.getValue(), offEatingY.getValue(), -offEatingZ.getValue());
                GL11.glScalef(offEatingScaleX.getValue(), offEatingScaleY.getValue(), offEatingScaleZ.getValue());
            }
            else {
                GL11.glTranslatef(-offX.getValue(), offY.getValue(), -offZ.getValue());
                GL11.glScalef(offScaleX.getValue(), offScaleY.getValue(), offScaleZ.getValue());
            }
        }

        if (hitModify.getValue() && isSwingInProgress && mc.player.swingingHand == event.hand) {
            int i = enumhandside == EnumHandSide.RIGHT ? 1 : -1;
            boolean shouldSword = swordModifyDiff.getValue() && (mc.player.swingingHand == EnumHand.MAIN_HAND ? mc.player.getHeldItemMainhand().getItem() instanceof ItemSword : mc.player.getHeldItemOffhand().getItem() instanceof ItemSword);
            float e = MathHelper.sin(interpSwingProgress() * interpSwingProgress() * (float)Math.PI);
            GL11.glRotatef(i * (((shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()) * (0.5625f)) + e * -((shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()) * (0.25f))), 0.0f, 1.0f, 0.0f);
            float e1 = MathHelper.sin(MathHelper.sqrt(interpSwingProgress()) * (float)Math.PI);
            GL11.glRotatef(i * e1 * -(shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()) * (0.25f), 0.0f, 0.0f, 1.0f);
            switch (shouldSword ? swingSwordRotateAxis.getValue() : swingRotateAxis.getValue()) {
                case X: {
                    GL11.glRotatef(e1 * -(shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()), reverseSwingProgress.getValue() ? -1.0f : 1.0f, 0.0f, 0.0f);
                    break;
                }

                case Y: {
                    GL11.glRotatef(e1 * -(shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()), 0.0f, reverseSwingProgress.getValue() ? -1.0f : 1.0f, 0.0f);
                    break;
                }

                case Z: {
                    GL11.glRotatef(e1 * -(shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()), 0.0f, 0.0f, reverseSwingProgress.getValue() ? -1.0f : 1.0f);
                    break;
                }
            }
            GL11.glRotatef(i * -(shouldSword ? swingSwordDepth.getValue() : swingDepth.getValue()) * (0.5625f), 0.0f, 1.0f, 0.0f);

            if (swordModifyDiff.getValue() && (mc.player.swingingHand == EnumHand.MAIN_HAND ? mc.player.getHeldItemMainhand().getItem() instanceof ItemSword : mc.player.getHeldItemOffhand().getItem() instanceof ItemSword)) {
                if (event.hand == EnumHand.MAIN_HAND) {
                    GL11.glRotatef(swordHitRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef(swordHitRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(swordHitRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
                }

                if (event.hand == EnumHand.OFF_HAND) {
                    GL11.glRotatef(swordHitRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef(-swordHitRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(-swordHitRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
                }
            }
            else {
                if (event.hand == EnumHand.MAIN_HAND) {
                    GL11.glRotatef(hitRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef(hitRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(hitRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
                }

                if (event.hand == EnumHand.OFF_HAND) {
                    GL11.glRotatef(hitRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef(-hitRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(-hitRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
                }
            }
        }

        if (!(mc.player.isHandActive() && mc.player.getItemInUseCount() > 0 && mc.player.getActiveHand() == event.hand)) {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (hitProgressMain.getValue() && flag1) {
                float f = MathHelper.sin(hitProgressMainOffset.getValue() * hitProgressMainOffset.getValue() * (float)Math.PI);
                GL11.glRotatef(45.0f + f * -20.0f, 0.0f, 1.0f, 0.0f);
                float f1 = MathHelper.sin(MathHelper.sqrt(hitProgressMainOffset.getValue()) * (float)Math.PI);
                GL11.glRotatef(f1 * -20.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(f1 * -80.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(-45.0f, 0.0f, 1.0f, 0.0f);
            }

            if (hitProgressOff.getValue() && !flag1) {
                float f = MathHelper.sin(hitProgressOffOffset.getValue() * hitProgressOffOffset.getValue() * (float)Math.PI);
                GL11.glRotatef(-(45.0f + f * -20.0f), 0.0f, 1.0f, 0.0f);
                float f1 = MathHelper.sin(MathHelper.sqrt(hitProgressOffOffset.getValue()) * (float)Math.PI);
                GL11.glRotatef(-f1 * -20.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(f1 * -80.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            }
        }

        if (event.hand == EnumHand.MAIN_HAND) {
            if (mc.player.getActiveHand() == EnumHand.MAIN_HAND && eatingModifyMain.getValue() && mc.player.isHandActive()) {
                GL11.glRotatef(mainEatingRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(mainEatingRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(mainEatingRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
            }
            else {
                GL11.glRotatef(mainRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(mainRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(mainRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
            }
        }

        if (event.hand == EnumHand.OFF_HAND) {
            if (mc.player.getActiveHand() == EnumHand.OFF_HAND && eatingModifyOff.getValue() && mc.player.isHandActive()) {
                GL11.glRotatef(offEatingRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(-offEatingRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(-offEatingRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
            }
            else {
                GL11.glRotatef(offRotateX.getValue(), 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(-offRotateY.getValue(), 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(-offRotateZ.getValue(), 0.0f, 0.0f, 1.0f);
            }
        }
    }

    @Listener
    public void modifyModelTransMatrixPre(ItemModelEvent.Pre event) {
        if (hitModify.getValue() && isSwingInProgress && mc.player.swingingHand == event.hand) {
            if (swordModifyDiff.getValue() && (mc.player.swingingHand == EnumHand.MAIN_HAND ? mc.player.getHeldItemMainhand().getItem() instanceof ItemSword : mc.player.getHeldItemOffhand().getItem() instanceof ItemSword)) {
                if (event.hand == EnumHand.MAIN_HAND) {
                    GL11.glTranslatef(swordHitX.getValue(), swordHitY.getValue(), -swordHitZ.getValue());
                }

                if (event.hand == EnumHand.OFF_HAND) {
                    GL11.glTranslatef(-swordHitX.getValue(), swordHitY.getValue(), -swordHitZ.getValue());
                }
            }
            else {
                if (event.hand == EnumHand.MAIN_HAND) {
                    GL11.glTranslatef(hitX.getValue(), hitY.getValue(), -hitZ.getValue());
                }

                if (event.hand == EnumHand.OFF_HAND) {
                    GL11.glTranslatef(-hitX.getValue(), hitY.getValue(), -hitZ.getValue());
                }
            }
        }
    }

    @Listener
    public void onTransformHit(ItemModelEvent.Hit event) {
        if (hitModify.getValue()) {
            event.cancel();
        }
    }

    private void updateSwingProgress() {
        if (mc.world == null || mc.player == null) {
            return;
        }

        int i = swingAnimationMax();

        if (isSwingInProgress) {
            ++swingProgressInt;

            if (swingProgressInt >= i) {
                swingProgressInt = 0;
                isSwingInProgress = false;
            }
        }

        prevSwingProgress = swingProgress;
        swingProgress = (float)swingProgressInt / i;
    }

    private float interpSwingProgress() {
        float f = swingProgress - prevSwingProgress;
        if (f < 0.0f) ++f;
        return prevSwingProgress + f * swingTimer.renderPartialTicks;
    }

    public int swingAnimationMax() {
        return mc.player.isPotionActive(MobEffects.HASTE) ? (6 - (1 + Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.HASTE)).getAmplifier()))
                : (mc.player.isPotionActive(MobEffects.MINING_FATIGUE) ? (6 + (1 + Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE)).getAmplifier()) * 2)
                : 6);
    }

    enum Page {
        ItemModel,
        Animations
    }

    enum ItemModelPage {
        Main,
        Off
    }

    public enum Axis {
        X,
        Y,
        Z
    }
}
