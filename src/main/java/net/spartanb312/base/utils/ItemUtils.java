package net.spartanb312.base.utils;

import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.spartanb312.base.utils.math.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static int getItemCount(Item item) {
        int count = mc.player.inventory.mainInventory.stream()
                .filter(itemStack -> itemStack.getItem() == item)
                .mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == item) {
            count += mc.player.getHeldItemOffhand().getCount();
        }
        return count;
    }

    public static int findItemInHotBar(Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static void switchToSlot(int slot) {
        if (mc.player.inventory.currentItem == slot
                || slot == -1) {
            return;
        }
        //Send packet to server
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    //from earthheck
    public static void switchToSlotButBetter(int slot) {
        if (mc.player.inventory.currentItem == slot) return;
        mc.playerController.pickItem(slot);
    }

    public static int findBlockInHotBar(Block block) {
        return findItemInHotBar(Item.getItemFromBlock(block));
    }

    public static boolean isItemInHotbar(Item item) {
        boolean isItemPresent = false;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == item) {
                isItemPresent = true;
            }
        }
        return isItemPresent;
    }

    public static int fastestMiningTool(Block toMineBlockMaterial) {
        float fastestSpeed = 1.0f;
        int theSlot = mc.player.inventory.currentItem;

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack.isEmpty || !(itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemHoe || itemStack.getItem() instanceof ItemShears))
                continue;

            float mineSpeed = BlockUtil.blockBreakSpeed(toMineBlockMaterial.getDefaultState(), itemStack);

            if (mineSpeed > fastestSpeed) {
                fastestSpeed = mineSpeed;
                theSlot = i;
            }
        }

        return theSlot;
    }

    public static boolean isItemInInventory(Item item) {
        for (Slot slot : mc.player.inventoryContainer.inventorySlots) {
            if (slot.getStack().getItem() == item)
                return true;
        }
        return false;
    }

    public static int itemSlotIDinInventory(Item item) {
        for (int i = 0; i < 45; i++) {
            if (mc.player.inventoryContainer.inventorySlots.get(i).getStack().getItem() == item)
                return i;
        }
        return 99999;
    }

    public static void swapItemFromInvToHotBar(Item item, int hotBarSlot) {
        int slotID = itemSlotIDinInventory(item);

        if (slotID != 99999) {
            mc.playerController.windowClick(0, slotID, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, hotBarSlot + 36, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slotID, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();
        }
    }

    public static int findItemInInv(Item item) {
        for (Pair<Integer, ItemStack> data : getInventoryAndHotbarSlots()) {
            if (data.b.getItem() == item) {
                return data.a;
            }
        }
        return -999;
    }

    public static List<Pair<Integer, ItemStack>> getInventoryAndHotbarSlots() {
        return getInventorySlots(9, 44);
    }

    private static List<Pair<Integer, ItemStack>> getInventorySlots(int current, int last) {
        List<Pair<Integer, ItemStack>> invSlots = new ArrayList<>();
        while (current <= last) {
            invSlots.add(new Pair<>(current, mc.player.inventoryContainer.getInventory().get(current)));
            current++;
        }
        return invSlots;
    }
}
