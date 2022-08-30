package me.thediamondsword5.moloch.module.modules.combat;

import me.thediamondsword5.moloch.event.events.player.BlockBreakDelayEvent;
import me.thediamondsword5.moloch.event.events.player.RightClickDelayEvent;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.mixin.mixins.accessor.AccessorMinecraft;
import net.spartanb312.base.mixin.mixins.accessor.AccessorPlayerControllerMP;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel(runnable = true)
@ModuleInfo(name = "FastUse", category = Category.COMBAT, description = "Allows you to remove delay from using some items")
public class FastUse extends Module {

    Setting<Boolean> fastPlace = setting("FastPlace", false).des("Remove block placing delay");
    Setting<Boolean> fastBreak = setting("FastBreak", false).des("Remove block breaking delay");
    Setting<Boolean> crystals = setting("Crystals", false).des("Remove delay when placing end crystals");
    Setting<Boolean> fireworks = setting("Fireworks", false).des("Remove delay when using fireworks");
    Setting<Boolean> bow = setting("Bow", false).des("Modify auto release charge amt for bows");
    Setting<Integer> bowChargeThreshold = setting("BowChargeThreshold", 1, 0, 20).des("Amount of bow charged to auto release").whenTrue(bow);
    Setting<Boolean> xp = setting("Xp", false).des("Modify throwing experience bottle delay");
    Setting<Integer> xpDelay = setting("XpDelay", 0, 0, 10).des("Delay of throwing experience bottle").whenTrue(xp);

    @Override
    public void onTick() {
        if (bow.getValue() && mc.player.isHandActive() && mc.player.getActiveItemStack().getItem() == Items.BOW && mc.player.getItemInUseMaxCount() >= bowChargeThreshold.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.stopActiveHand();
        }
    }

    @Listener
    public void blockBreakDelaySet(BlockBreakDelayEvent event) {
        if (fastBreak.getValue()) {
            ((AccessorPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
        }
    }

    @Listener
    public void rightClickDelaySet(RightClickDelayEvent event) {
        Item heldItem = mc.player.getHeldItemMainhand().getItem();
        if (heldItem instanceof ItemBlock && fastPlace.getValue()) {
            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
        } else if (heldItem instanceof ItemEndCrystal && crystals.getValue()) {
            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
        } else if (heldItem instanceof ItemFirework && fireworks.getValue()) {
            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
        } else if (heldItem instanceof ItemExpBottle && xp.getValue()) {
            ((AccessorMinecraft) mc).setRightClickDelayTimer(xpDelay.getValue());
        }
    }
}
