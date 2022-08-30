package me.thediamondsword5.moloch.module.modules.other;

import me.thediamondsword5.moloch.event.events.player.BlockInteractionEvent;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.RayTraceResult;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "NoEntityBlock", category = Category.OTHER, description = "Allows you to interact with blocks through entities")
public class NoEntityBlock extends Module {

    Setting<Boolean> onlyBlocks = setting("OnlyBlocks", true).des("Only interact with blocks through entities when you can hit a block");
    Setting<Boolean> everything = setting("Everything", false).des("Interact with blocks through entities while anything is in your hands");
    Setting<Boolean> pickaxe = setting("Pickaxe", true).des("Interact with blocks through entities while holding pickaxe").whenFalse(everything);
    Setting<Boolean> sword = setting("Sword", false).des("Interact with blocks through entities while holding sword").whenFalse(everything);
    Setting<Boolean> gapple = setting("Gapple", false).des("Interact with blocks through entities while holding gapple").whenFalse(everything);
    Setting<Boolean> crystal = setting("Crystal", false).des("Interact with blocks through entities while holding crystal").whenFalse(everything);

    @Listener
    public void onBlockInteract(BlockInteractionEvent event) {
        RayTraceResult mouseObject = mc.objectMouseOver;
        Item heldItem = mc.player.getHeldItemMainhand().getItem();

        if (!onlyBlocks.getValue() || mouseObject != null && mouseObject.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (everything.getValue() || ((pickaxe.getValue() && heldItem instanceof ItemPickaxe) || (sword.getValue() && heldItem instanceof ItemSword) || (gapple.getValue() && heldItem == Items.GOLDEN_APPLE) || (crystal.getValue() && heldItem == Items.END_CRYSTAL))) {
                event.cancel();
            }
        }
    }
}
