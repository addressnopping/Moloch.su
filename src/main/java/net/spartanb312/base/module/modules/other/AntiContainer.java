package net.spartanb312.base.module.modules.other;

import me.thediamondsword5.moloch.utils.BlockUtil;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;

@Parallel
@ModuleInfo(name = "AntiContainer", category = Category.OTHER, description = "Avoiding opening containers")
public class AntiContainer extends Module {

    Setting<Boolean> Chest = setting("Chest", true);
    Setting<Boolean> EnderChest = setting("EnderChest", true);
    Setting<Boolean> Trapped_Chest = setting("TrappedChest", true);
    Setting<Boolean> Hopper = setting("Hopper", true);
    Setting<Boolean> Dispenser = setting("Dispenser", true);
    Setting<Boolean> Furnace = setting("Furnace", true);
    Setting<Boolean> Beacon = setting("Beacon", true);
    Setting<Boolean> Crafting_Table = setting("CraftingTable", true);
    Setting<Boolean> Anvil = setting("Anvil", true);
    Setting<Boolean> Enchanting_table = setting("Enchantingtable", true);
    Setting<Boolean> Brewing_Stand = setting("BrewingStand", true);
    Setting<Boolean> ShulkerBox = setting("ShulkerBox", true);

    @Override
    public void onPacketSend(PacketEvent.Send packet) {
        if (packet.packet instanceof CPacketPlayerTryUseItemOnBlock && !BlockUtil.isPlacing) {
            BlockPos pos = ((CPacketPlayerTryUseItemOnBlock) packet.packet).getPos();
            if (check(pos)) packet.cancel();
        }
    }

    public boolean check(BlockPos pos) {
        return ((mc.world.getBlockState(pos).getBlock() == Blocks.CHEST && Chest.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST && EnderChest.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.TRAPPED_CHEST && Trapped_Chest.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.HOPPER && Hopper.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.DISPENSER && Dispenser.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.FURNACE && Furnace.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.BEACON && Beacon.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE && Crafting_Table.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.ANVIL && Anvil.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.ENCHANTING_TABLE && Enchanting_table.getValue())
                || (mc.world.getBlockState(pos).getBlock() == Blocks.BREWING_STAND && Brewing_Stand.getValue())
                || (mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) && ShulkerBox.getValue());
    }
}
