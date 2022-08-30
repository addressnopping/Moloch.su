package me.thediamondsword5.moloch.module.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import org.lwjgl.input.Mouse;

@Parallel
@ModuleInfo(name = "Offhand", category = Category.COMBAT, description = "Manages items in offhand")
public class Offhand extends Module {

    Setting<Boolean> crystal = setting("Crystal", true).des("Automatically switches offhand to crystals when conditions are met");
    Setting<Boolean> gapple = setting("Gapple", false).des("Automatically switches offhand to golden apples when conditions are met");
    Setting<Boolean> gappleRightClickSword = setting("GapRightClickSword", true).des("Switches offhand to gapple when right clicking with sword").whenTrue(gapple);
    Setting<Boolean> noMotion = setting("NoMotion", false).des("Stops movement while switching items");
    Setting<Integer> delay = setting("Delay", 50, 0, 1000).des("Delay in switching items in milliseconds");
    Setting<Float> totemHealth = setting("TotemHealth", 11.0f, 0.0f, 36.0f).des("Amount of health to switch back to totem from whatever else you are holding in offhand");
    Setting<Boolean> checkElytra = setting("CheckElytra", true).des("Switches to totem as long as you are using an elytra");
    Setting<Boolean> checkCrystalDamage = setting("CheckCrystalDamage", true).des("Switches to totem if you are about to take certain amount of damage from crystal");
    Setting<Float> maxCrystalDamage = setting("MaxCrystalDamage", 26.0f, 0.0f, 36.0f).des("Maximum amount of potential damage to switch offhand back to totem").whenTrue(checkCrystalDamage);
    Setting<Boolean> checkFallDistance = setting("CheckFallDistance", true).des("Switches to totem if fall distance is beyond a certain threshold");
    Setting<Float> maxFallDistance = setting("MaxFallDistance", 5.0f, 0.0f, 50.0f).des("Max amount of blocks that you can fall and not have offhand switch to totem").whenTrue(checkFallDistance);

    private final Timer timer = new Timer();
    private int toSwitchSlot = 0;
    private Item currentItem = Items.AIR;

    @Override
    public String getModuleInfo() {
        return ItemUtils.getItemCount(mc.player.getHeldItemOffhand().getItem()) + "";
    }

    @Override
    public void onRenderTick() {
        if (timer.passed(delay.getValue()) && mc.player != null) {
            toSwitchSlot = -999;

            if (ItemUtils.isItemInInventory(Items.TOTEM_OF_UNDYING) && ((mc.player.getHealth() + mc.player.getAbsorptionAmount() <= totemHealth.getValue()) ||
                    (checkElytra.getValue() && mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) ||
                    (checkCrystalDamage.getValue() && CrystalUtil.getDmgSelf() > maxCrystalDamage.getValue()) ||
                    (checkFallDistance.getValue() && mc.player.fallDistance >= maxFallDistance.getValue()))) {

                setOffhandItem(Items.TOTEM_OF_UNDYING);
            }
            else if (ItemUtils.isItemInInventory(Items.GOLDEN_APPLE) && gapple.getValue() &&
                    (Mouse.isButtonDown(1) && (!gappleRightClickSword.getValue() || mc.player.getHeldItemMainhand().getItem() instanceof ItemSword))) {

                setOffhandItem(Items.GOLDEN_APPLE);
            }
            else if (ItemUtils.isItemInInventory(Items.END_CRYSTAL) && crystal.getValue()) {

                setOffhandItem(Items.END_CRYSTAL);
            }
            else {
                if (ItemUtils.isItemInInventory(Items.TOTEM_OF_UNDYING)) {
                    setOffhandItem(Items.TOTEM_OF_UNDYING);
                } else if (ItemUtils.isItemInInventory(Items.END_CRYSTAL) && crystal.getValue()) {
                    setOffhandItem(Items.END_CRYSTAL);
                } else if (ItemUtils.isItemInInventory(Items.GOLDEN_APPLE) && gapple.getValue()) {
                    setOffhandItem(Items.GOLDEN_APPLE);
                }
            }

            if (mc.player.getHeldItemOffhand().getItem() != currentItem && toSwitchSlot != -999) {
                double prevMotionX = mc.player.motionX;
                double prevMotionZ = mc.player.motionZ;
                if (noMotion.getValue()) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    mc.player.setVelocity(0.0, mc.player.motionY, 0.0);
                }

                boolean preSwitchIsEmpty = mc.player.getHeldItemOffhand().getItem() == Items.AIR;

                mc.playerController.windowClick(0, toSwitchSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                if (!preSwitchIsEmpty) {
                    mc.playerController.windowClick(0, toSwitchSlot, 0, ClickType.PICKUP, mc.player);
                }
                mc.playerController.updateController();

                if (noMotion.getValue()) {
                    mc.player.motionX = prevMotionX;
                    mc.player.motionZ = prevMotionZ;
                    mc.player.setVelocity(prevMotionX, mc.player.motionY, prevMotionZ);
                }
                timer.reset();
            }
        }
    }

    private void setOffhandItem(Item item) {
        currentItem = item;

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int switchSlot = ItemUtils.findItemInInv(item);
            if (switchSlot != -999) {
                toSwitchSlot = switchSlot;
            }
        }
    }
}
