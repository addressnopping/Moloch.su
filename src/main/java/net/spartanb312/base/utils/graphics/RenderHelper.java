package net.spartanb312.base.utils.graphics;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.hud.huds.ActiveModuleList;
import net.spartanb312.base.utils.EntityUtil;
import net.spartanb312.base.utils.math.Vec2I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

import static net.spartanb312.base.utils.RotationUtil.mc;

public class RenderHelper {
    private static final Frustum frustrum = new Frustum();

    public static Vec2I getStart(ScaledResolution scaledResolution, ActiveModuleList.ListPos caseIn) {
        switch (caseIn) {
            case RightDown: {
                return new Vec2I(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            }
            case LeftTop: {
                return new Vec2I(0, 0);
            }
            case LeftDown: {
                return new Vec2I(0, scaledResolution.getScaledHeight());
            }
            default: {
                return new Vec2I(scaledResolution.getScaledWidth(), 0);
            }
        }
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }
}
