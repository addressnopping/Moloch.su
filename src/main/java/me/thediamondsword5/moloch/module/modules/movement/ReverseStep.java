package me.thediamondsword5.moloch.module.modules.movement;

import me.thediamondsword5.moloch.event.events.player.UpdateTimerEvent;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.EntityUtil;

@Parallel
@ModuleInfo(name = "ReverseStep", category = Category.MOVEMENT, description = "Allows you fall down blocks fast")
public class ReverseStep extends Module {

    Setting<Boolean> timerModify = setting("Timer", true).des("Use timer to boost client side tick speed while falling (bypasses 2b2t anti reversestep i think, but will rubberband u if u go down blocks too quickly, so don't use on stairs)");
    Setting<Float> timerSpeed = setting("TimerSpeed", 2.0f, 1.0f, 5.0f).des("Timer speed as you are falling").whenTrue(timerModify);
    Setting<Float> speed = setting("Speed", 5.0f, 0.0f, 5.0f).des("Speed that you fall in");
    Setting<Float> height = setting("Height", 2.5f, 1.0f, 2.5f).des("Max height that you will fall fast");

    private double prevXMotion;
    private double prevZMotion;
    private boolean continueMotionFlag = false;
    private boolean doReverseStepFlag = true;

    @Override
    public void onTick() {
        if (mc.player.motionY > 0.0f) {
            doReverseStepFlag = false;
        }
        if (mc.player.onGround) {
            doReverseStepFlag = true;
        }

        if (!EntityUtil.canStep() || !mc.player.onGround) return;

        for (double d = 0.0; d < height.getValue() + 0.5; d += 0.01) {
            if (EntityUtil.isOnGround(d)) {
                mc.player.motionY = -speed.getValue();
                break;
            }
        }
    }

    @Override
    public String getModuleInfo() {
        if (timerModify.getValue()) {
            return "Timer";
        }
        else {
            return "Normal";
        }
    }

    @Listener
    public void onUpdateTimer(UpdateTimerEvent event) {
        if (!timerModify.getValue()) return;

        if (EntityUtil.canStep()) {
            if (!mc.player.onGround && doReverseStepFlag) {
                for (double d = 0.0; d < height.getValue() + 0.5; d += 0.01) {
                    if (EntityUtil.isOnGround(d) && mc.player.motionY < 0.0f) {
                        prevXMotion = mc.player.motionX;
                        prevZMotion = mc.player.motionZ;
                        continueMotionFlag = true;
                        mc.player.motionX = 0.0;
                        mc.player.motionZ = 0.0;
                        mc.player.setVelocity(0.0, mc.player.motionY, 0.0);
                        event.timerSpeed = timerSpeed.getValue();
                        break;
                    }
                }
            }
            else if (continueMotionFlag) {
                mc.player.motionX = prevXMotion;
                mc.player.motionZ = prevZMotion;
                mc.player.setVelocity(prevXMotion, mc.player.motionY, prevZMotion);
                continueMotionFlag = false;
            }
        }
    }
}
