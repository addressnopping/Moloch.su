package me.thediamondsword5.moloch.module.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.ItemUtils;
//TODO: improve this somehow, add check for have xp in hotbar
@Parallel
@ModuleInfo(name = "SilentXP", category = Category.COMBAT, description = "Uses experience through packets")
public class SilentXP extends Module {
    public Setting<Integer> lookPitch = setting("Look Pitch", 90, 0, 100).des("How much you look down");
    public Setting<Boolean> silentRotate = setting("Silent Rotate", false);
    public Setting<Integer> delay = setting("Delay", 0, 0, 5);

    private int delay_count;
    int prvSlot;

    @Override
    public void onEnable() {
        delay_count = 0;
    }

    @Override
    public void onTick() {
        int oldPitch = (int)mc.player.rotationPitch;
        prvSlot = mc.player.inventory.currentItem; //TODO add better rotations
        mc.player.connection.sendPacket(new CPacketHeldItemChange(ItemUtils.findItemInHotBar(Items.EXPERIENCE_BOTTLE)));
        if(!silentRotate.getValue()) {
            mc.player.rotationPitch = lookPitch.getValue();
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, lookPitch.getValue(), true));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if(!silentRotate.getValue()) {
            mc.player.rotationPitch = oldPitch;
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, oldPitch, true));
        mc.player.inventory.currentItem = prvSlot;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(prvSlot));
    }
}
