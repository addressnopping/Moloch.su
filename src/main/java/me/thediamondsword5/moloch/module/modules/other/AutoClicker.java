package me.thediamondsword5.moloch.module.modules.other;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.util.math.RayTraceResult;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.Timer;

@Parallel
@ModuleInfo(name = "AutoClicker", category = Category.OTHER, description = "Clicks automatically")
public class AutoClicker extends Module {

    Setting<Boolean> sixBMode = setting("6bMode", false).des("Only hits if your mouse is hovering over an item frame with an item in it (for duping in 6b6t.org)");
    //Setting<Boolean> sixBMultiFrame = setting("6bMultiFrame", false).des("Tries to dupe using multiple frames at once by clicking all of them").whenTrue(sixBMode);
    ///Setting<Float> sixBMultiFrameRange = setting("6bMultiFrameRange", 5.0f, 0.0f, 8.0f).des("Range to hit frames").whenTrue(sixBMode).whenTrue(sixBMultiFrame);
    Setting<Boolean> rightClick = setting("RightClick", true).des("Click right mouse button");
    Setting<Integer> rightClickDelay = setting("RightClickDelay", 1000, 1, 10000).des("Delay between right clicks in milliseconds").whenTrue(rightClick);
    Setting<Boolean> leftClick = setting("LeftClick", true).des("Click left mouse button");
    Setting<Integer> leftClickDelay = setting("LeftClickDelay", 1000, 1, 10000).des("Delay between left clicks in milliseconds").whenTrue(leftClick);

    private final Timer rightClickTimer = new Timer();
    private final Timer leftClickTimer = new Timer();

    @Override
    public void onRenderTick() {
        if (rightClick.getValue() && rightClickTimer.passed(rightClickDelay.getValue())) {
            mc.rightClickMouse();
            rightClickTimer.reset();
        }

        if (leftClick.getValue() && leftClickTimer.passed(leftClickDelay.getValue())
                && (!sixBMode.getValue() || (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit instanceof EntityItemFrame && ((EntityItemFrame) mc.objectMouseOver.entityHit).getDisplayedItem().getItem() != Items.AIR))) {
            mc.clickMouse();
            leftClickTimer.reset();
        }
    }
}
