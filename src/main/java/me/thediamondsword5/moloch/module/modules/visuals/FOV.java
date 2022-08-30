package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.event.events.render.FOVItemModifyEvent;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.common.KeyBind;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import org.lwjgl.input.Keyboard;

@Parallel
@ModuleInfo(name = "FOV", category = Category.VISUALS, description = "Change FOV beyond what minecraft normally allows")
public class FOV extends Module {

    Setting<Boolean> modifyItemFOV = setting("ModifyItemFOV", false).des("Modifies your heldmodel with your FOV");
    Setting<KeyBind> zoomBind = setting("ZoomBind", subscribeKey(new KeyBind(0, null))).des("Keybind for FOV zoom since the item FOV overrides the zoom fov").whenTrue(modifyItemFOV);
    Setting<Float> fov = setting("FOV", 120.0f, 0.0f, 180.0f);

    @Override
    public void onDisable() {
        mc.gameSettings.fovSetting = 100.0f;
        moduleDisableFlag = true;
    }

    @Override
    public void onTick() {
        if (mc.gameSettings.fovSetting != fov.getValue()) {
            mc.gameSettings.fovSetting = fov.getValue();
        }
    }

    @Listener
    public void onFOVModifyItems(FOVItemModifyEvent event) {
        if (!modifyItemFOV.getValue() || (Keyboard.isKeyDown(zoomBind.getValue().getKeyCode()))) {
            return;
        }

        event.cancel();
        event.fov = fov.getValue();
    }
}
