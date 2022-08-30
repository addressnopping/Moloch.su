package net.spartanb312.base.module.modules.client;

import net.spartanb312.base.client.ConfigManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.gui.HUDEditorFinal;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.module.modules.client.Particles;
import me.thediamondsword5.moloch.utils.graphics.ParticleUtil;
import org.lwjgl.input.Keyboard;

@Parallel
@ModuleInfo(name = "HUDEditor", category = Category.CLIENT, keyCode = Keyboard.KEY_GRAVE, description = "Move shit around in HUD")
public class HUDEditor extends Module {
    public int flag = 0;

    public static HUDEditor instance;

    public HUDEditor() {
        instance = this;
    }


    @Override
    public void onEnable() {
        if (mc.player != null) {
            if (!(mc.currentScreen instanceof HUDEditorFinal)) {
                mc.displayGuiScreen(new HUDEditorFinal());
                if (ClickGUI.instance.guiMove.getValue()) {
                    flag = 1;
                }

                moduleEnableFlag = true;
            }
        }
    }

    @Override
    public void onDisable() {
        moduleDisableFlag = true;

        if (!ClickGUI.instance.guiMove.getValue()) {
            if (Particles.INSTANCE.isEnabled())
                ParticleUtil.clearParticles();

            if (mc.currentScreen instanceof HUDEditorFinal)
                mc.displayGuiScreen(null);
        }

        ConfigManager.saveAll();
    }

}
