package me.thediamondsword5.moloch.module.modules.other;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.ItemUtils;

@Parallel
@ModuleInfo(name = "ElytraSwap", category = Category.OTHER, description = "If currently wearing chestplate, replaces it with elytra. If currently wearing elytra, replaces it with chestplate")
public class ElytraSwap extends Module {

    Setting<Boolean> spoofNoMove = setting("SpoofNoMotion", true).des("Spoof 0 movement while switching items");

    private double prevMotionX;
    private double prevMotionY;
    private double prevMotionZ;

    @Override
    public void onRenderTick() {
        if (mc.player == null
        || !(ItemUtils.isItemInInventory(Items.ELYTRA) && (ItemUtils.isItemInInventory(Items.DIAMOND_CHESTPLATE) || ItemUtils.isItemInInventory(Items.IRON_CHESTPLATE) || ItemUtils.isItemInInventory(Items.GOLDEN_CHESTPLATE) || ItemUtils.isItemInInventory(Items.CHAINMAIL_CHESTPLATE) || ItemUtils.isItemInInventory(Items.LEATHER_CHESTPLATE)))) {
            return;
        }

        int slotID;
        if (mc.player.inventory.armorInventory.get(2).getItem() == Items.DIAMOND_CHESTPLATE
                    || mc.player.inventory.armorInventory.get(2).getItem() == Items.IRON_CHESTPLATE
                    || mc.player.inventory.armorInventory.get(2).getItem() == Items.GOLDEN_CHESTPLATE
                    || mc.player.inventory.armorInventory.get(2).getItem() == Items.CHAINMAIL_CHESTPLATE
                    || mc.player.inventory.armorInventory.get(2).getItem() == Items.LEATHER_CHESTPLATE) {
            slotID = ItemUtils.itemSlotIDinInventory(Items.ELYTRA);
        }
        else {
            slotID = findArmorSlotInInv();
        }

        if (slotID != 99999) {
            if (spoofNoMove.getValue()) {
                prevMotionX = mc.player.motionX;
                prevMotionY = mc.player.motionY;
                prevMotionZ = mc.player.motionZ;
                mc.player.motionX = 0.0d;
                mc.player.motionY = 0.0d;
                mc.player.motionZ = 0.0d;
            }

            mc.playerController.windowClick(0, slotID, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slotID, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();

            if (spoofNoMove.getValue()) {
                mc.player.motionX = prevMotionX;
                mc.player.motionY = prevMotionY;
                mc.player.motionZ = prevMotionZ;
            }
        }

        toggle();
    }

    private int findArmorSlotInInv() {
        for (int i = 0; i < 45; i++) {
            if (mc.player.inventoryContainer.inventorySlots.get(i).getStack().getItem() == Items.DIAMOND_CHESTPLATE
                    || mc.player.inventoryContainer.inventorySlots.get(i).getStack().getItem() == Items.IRON_CHESTPLATE
                    || mc.player.inventoryContainer.inventorySlots.get(i).getStack().getItem() == Items.GOLDEN_CHESTPLATE
                    || mc.player.inventoryContainer.inventorySlots.get(i).getStack().getItem() == Items.CHAINMAIL_CHESTPLATE
                    || mc.player.inventoryContainer.inventorySlots.get(i).getStack().getItem() == Items.LEATHER_CHESTPLATE)
                return i;
        }
        return 99999;
    }
}
