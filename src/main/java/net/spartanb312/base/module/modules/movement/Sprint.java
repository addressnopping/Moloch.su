package net.spartanb312.base.module.modules.movement;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel(runnable = true)
@ModuleInfo(name = "Sprint", category = Category.MOVEMENT, description = "Automatically sprint")
public class Sprint extends Module {

    Setting<Boolean> allDirections = setting("MultiDirectional", true).des("Sprint in all directions");
    Setting<Boolean> collideStop = setting("CollideStop", true).des("Stops sprinting when you are against a block");

    @Override
    public void onRenderTick() {
        if (mc.player == null) return;
        mc.player.setSprinting((allDirections.getValue() ? (mc.player.moveStrafing != 0 || mc.player.moveForward != 0) : mc.player.moveForward != 0) && !mc.player.isSneaking() && (!collideStop.getValue() || !mc.player.collidedHorizontally) && mc.player.getFoodStats().getFoodLevel() > 6.0f);
    }
}
