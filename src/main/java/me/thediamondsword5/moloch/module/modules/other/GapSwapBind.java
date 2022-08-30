package me.thediamondsword5.moloch.module.modules.other;

import net.minecraft.init.Items;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.common.KeyBind;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.ItemUtils;
import org.lwjgl.input.Keyboard;

@Parallel
@ModuleInfo(name = "GapSwapBind", category = Category.OTHER, description = "Bind a key to force you to hold a gap as long as it's down (this is useless for everyone except me bc the ca i use is stupid af and doesn't have a swap back option)")
public class GapSwapBind extends Module {

    Setting<KeyBind> bind = setting("GapBind", subscribeKey(new KeyBind(0, null))).des("Key to bind to switch to gap");

    @Override
    public void onTick() {
        if (!mc.ingameGUI.getChatGUI().getChatOpen() && mc.currentScreen == null && Keyboard.isKeyDown(bind.getValue().getKeyCode()) && mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE) {
            ItemUtils.switchToSlot(ItemUtils.findItemInHotBar(Items.GOLDEN_APPLE));
        }
    }
}
