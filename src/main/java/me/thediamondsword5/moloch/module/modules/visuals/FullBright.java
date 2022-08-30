package me.thediamondsword5.moloch.module.modules.visuals;

import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.module.Category;

@Parallel
@ModuleInfo(name = "FullBright", category = Category.VISUALS, description = "No shadows")
public class FullBright extends Module {
    public static FullBright INSTANCE;
    Setting<BrightMode> brightMode = setting("BrightMode", BrightMode.Gamma);

    public FullBright() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        if (brightMode.getValue() == BrightMode.Gamma)
            mc.gameSettings.gammaSetting = 1.0f;

        if (brightMode.getValue() == BrightMode.Potion)
            mc.player.removeActivePotionEffect(MobEffects.NIGHT_VISION);

        moduleDisableFlag = true;
    }


    @Override
    public void onTick() {
        if (brightMode.getValue() == BrightMode.Potion)
            mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 42069));

        if (brightMode.getValue() == BrightMode.Gamma) {
            if (mc.player.isPotionActive(MobEffects.NIGHT_VISION))
                mc.player.removeActivePotionEffect(MobEffects.NIGHT_VISION);

            if (mc.gameSettings.gammaSetting != 1000.0f)
                mc.gameSettings.gammaSetting = 1000.0f;
        }
    }

    enum BrightMode {
        Gamma, Potion
    }

}