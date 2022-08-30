package me.thediamondsword5.moloch.module.modules.visuals;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "CameraClip", category = Category.VISUALS, description = "Allows your 3rd person camera to clip through blocks")
public class CameraClip extends Module {
    //see MixinEntityRenderer
    public Setting<Float> cameraDistance = setting("CameraDistance", 4.0f, 0.1f, 20.0f);

    public static CameraClip INSTANCE;

    public CameraClip() {
        INSTANCE = this;
    }
}
