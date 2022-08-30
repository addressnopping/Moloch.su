package net.spartanb312.base.utils.graphics;

import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.module.modules.visuals.ESP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.command.Command;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.utils.MathUtilFuckYou;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.spartanb312.base.utils.EntityUtil.mc;
import static org.lwjgl.opengl.GL11.*;

public class SpartanTessellator extends Tessellator {

    public static SpartanTessellator INSTANCE = new SpartanTessellator();

    public SpartanTessellator() {
        super(2097152);
    }

    public static void prepareGL() {
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GL11.glDisable(GL_TEXTURE_2D);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL_BLEND);
        GlStateManager.disableDepth();
        GL11.glDisable(GL_ALPHA_TEST);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
    }

    public static void releaseGL() {
        GlStateManager.depthMask(true);
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glEnable(GL_BLEND);
        GlStateManager.enableDepth();
        GL11.glEnable(GL_ALPHA_TEST);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void begin(int mode) {
        INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void render() {
        INSTANCE.draw();
    }

    public static void drawFlatFullBox(Vec3d pos, boolean useDepth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glDisable(GL_CULL_FACE);
        drawFlatFilledBox(INSTANCE.getBuffer(), useDepth, (float)pos.x, (float)pos.y, (float)pos.z, 1.0f, 1.0f, r, g, b, a);
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawFlatLineBox(Vec3d pos, boolean useDepth, float width, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(width);
        drawFlatLineBox(INSTANCE.getBuffer(), useDepth, (float)pos.x, (float)pos.y, (float)pos.z, 1.0f, 1.0f, r, g, b, a);
    }

    public static void drawBBFullBox(Entity entity, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        Vec3d entityPos = EntityUtil.getInterpolatedEntityPos(entity, mc.getRenderPartialTicks());
        drawFilledBox(INSTANCE.getBuffer(), false, (float)(entityPos.x - ((bb.maxX - bb.minX + 0.05) / 2.0f)), (float)(entityPos.y), (float)(entityPos.z - ((bb.maxZ - bb.minZ + 0.05) / 2.0f)), (float)(bb.maxX - bb.minX + 0.05), (float)(bb.maxY - bb.minY), (float)(bb.maxZ - bb.minZ + 0.05), r, g, b, a);
    }

    public static void drawBBLineBox(Entity entity, float width, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        Vec3d entityPos = EntityUtil.getInterpolatedEntityPos(entity, mc.getRenderPartialTicks());
        GL11.glLineWidth(width);
        drawLineBox(INSTANCE.getBuffer(), false, (float)(entityPos.x - ((bb.maxX - bb.minX + 0.05) / 2.0f)), (float)(entityPos.y), (float)(entityPos.z - ((bb.maxZ - bb.minZ + 0.05) / 2.0f)), (float)(bb.maxX - bb.minX + 0.05), (float)(bb.maxY - bb.minY), (float)(bb.maxZ - bb.minZ + 0.05), r, g, b, a);
    }

    public static void drawBlockBBFullBox(BlockPos blockPos, float scale, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        AxisAlignedBB bb = getBoundingFromPos(blockPos);
        drawBetterBoundingBoxFilled(INSTANCE.getBuffer(), bb, new Vec3d(blockPos.x + 0.5f, blockPos.y + 0.5f, blockPos.z + 0.5f), scale, r, g, b, a);
    }

    public static void drawBlockBBLineBox(BlockPos blockPos, float scale, float width, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        AxisAlignedBB bb = getBoundingFromPos(blockPos);
        GL11.glLineWidth(width);
        drawBetterBoundingBoxLines(INSTANCE.getBuffer(), bb, new Vec3d(blockPos.x + 0.5f, blockPos.y + 0.5f, blockPos.z + 0.5f), scale, r, g, b, a);
    }

    public static void drawBlockFullBox(Vec3d vec, boolean useDepth, float height, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        drawFilledBox(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r, g, b, a);
    }

    public static void drawGradientBlockFullBox(Vec3d vec, boolean useDepth, boolean sidesOnly, float height, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        drawGradientFilledBox(INSTANCE.getBuffer(), useDepth, sidesOnly, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawBlockLineBox(Vec3d vec, boolean useDepth, float height, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawLineBox(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r, g, b, a);
    }

    public static void drawGradientBlockLineBox(Vec3d vec, boolean useDepth, float height, float lineWidth, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        GL11.glLineWidth(lineWidth);
        drawGradientLineBox(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawDoubleBlockFullBox(Vec3d vec1, Vec3d vec2, boolean useDepth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        drawTwoPointFilledBox(INSTANCE.getBuffer(), useDepth, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawDoubleBlockFullPyramid(Vec3d vec1, Vec3d vec2, boolean useDepth, boolean flagx, boolean flagz, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        drawTwoPointFilledPyramid(INSTANCE.getBuffer(), useDepth, flagx, flagz, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawGradientDoubleBlockFullPyramid(Vec3d vec1, Vec3d vec2, boolean useDepth, boolean flagx, boolean flagz, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        drawGradientTwoPointFilledPyramid(INSTANCE.getBuffer(), useDepth, flagx, flagz, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawGradientDoubleBlockFullBox(Vec3d vec1, Vec3d vec2, boolean useDepth, boolean sidesOnly, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        drawGradientTwoPointFilledBox(INSTANCE.getBuffer(), useDepth, sidesOnly, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawDoubleBlockLineBox(Vec3d vec1, Vec3d vec2, boolean useDepth, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawTwoPointLineBox(INSTANCE.getBuffer(), useDepth, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawDoubleBlockLinePyramid(Vec3d vec1, Vec3d vec2, boolean useDepth, float lineWidth, boolean flagx, boolean flagz, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawTwoPointLinePyramid(INSTANCE.getBuffer(), useDepth, flagx, flagz, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawGradientDoubleBlockLinePyramid(Vec3d vec1, Vec3d vec2, boolean useDepth, float lineWidth, boolean flagx, boolean flagz, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        GL11.glLineWidth(lineWidth);
        drawGradientTwoPointLinePyramid(INSTANCE.getBuffer(), useDepth, flagx, flagz, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawGradientDoubleBlockLineBox(Vec3d vec1, Vec3d vec2, boolean useDepth, float lineWidth, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        GL11.glLineWidth(lineWidth);
        drawGradientTwoPointLineBox(INSTANCE.getBuffer(), useDepth, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawDoubleBlockFlatFullBox(Vec3d vec1, Vec3d vec2, boolean useDepth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glDisable(GL_CULL_FACE);
        drawDoublePointFlatFilledBox(INSTANCE.getBuffer(), useDepth, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.z + 0.5), r, g, b, a);
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawDoubleBlockFlatLineBox(Vec3d vec1, Vec3d vec2, boolean useDepth, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawDoublePointFlatLineBox(INSTANCE.getBuffer(), useDepth, (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawXCross(Vec3d vec, float height, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawXCross(INSTANCE.getBuffer(), (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r, g, b, a);
    }

    public static void drawGradientXCross(Vec3d vec, float height, float lineWidth, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        GL11.glLineWidth(lineWidth);
        drawGradientXCross(INSTANCE.getBuffer(), (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawFlatXCross(Vec3d vec, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawFlatXCross(INSTANCE.getBuffer(), (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, 1.0f, r, g, b, a);
    }

    public static void drawDoublePointXCross(Vec3d vec1, Vec3d vec2, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawDoublePointXCross(INSTANCE.getBuffer(), (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawGradientDoublePointXCross(Vec3d vec1, Vec3d vec2, float lineWidth, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        GL11.glLineWidth(lineWidth);
        drawGradientDoublePointXCross(INSTANCE.getBuffer(), (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.y), (float)(vec2.z + 0.5), r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawDoublePointFlatXCross(Vec3d vec1, Vec3d vec2, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawDoublePointFlatXCross(INSTANCE.getBuffer(), (float)(vec1.x + 0.5), (float)(vec1.y), (float)(vec1.z + 0.5), (float)(vec2.x + 0.5), (float)(vec2.z + 0.5), r, g, b, a);
    }

    public static void drawPyramidFullBox(Vec3d vec, boolean useDepth, float height, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        drawFilledPyramid(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r, g, b, a);
    }

    public static void drawGradientPyramidFullBox(Vec3d vec, boolean useDepth, float height, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        drawGradientFilledPyramid(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public static void drawPyramidLineBox(Vec3d vec, boolean useDepth, float height, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawLinePyramid(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r, g, b, a);
    }

    public static void drawGradientPyramidLineBox(Vec3d vec, boolean useDepth, float height, float lineWidth, int color1, int color2) {
        int a1 = color1 >>> 24 & 255;
        int r1 = color1 >>> 16 & 255;
        int g1 = color1 >>> 8 & 255;
        int b1 = color1 & 255;

        int a2 = color2 >>> 24 & 255;
        int r2 = color2 >>> 16 & 255;
        int g2 = color2 >>> 8 & 255;
        int b2 = color2 & 255;

        GL11.glLineWidth(lineWidth);
        drawGradientLinePyramid(INSTANCE.getBuffer(), useDepth, (float)(vec.x), (float)(vec.y), (float)(vec.z), 1.0f, height, 1.0f, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    //from earthhack
    public static float[] getRotations(ModelRenderer model) {
        return new float[]{model.rotateAngleX, model.rotateAngleY, model.rotateAngleZ};
    }

    public static float[][] getRotationsFromModel(ModelBiped modelBiped) {
        float[][] rotations = new float[5][3];
        rotations[0] = getRotations(modelBiped.bipedHead);
        rotations[1] = getRotations(modelBiped.bipedRightArm);
        rotations[2] = getRotations(modelBiped.bipedLeftArm);
        rotations[3] = getRotations(modelBiped.bipedRightLeg);
        rotations[4] = getRotations(modelBiped.bipedLeftLeg);
        return rotations;
    }

    public static void drawSkeleton(EntityPlayer entity, float[][] rotations, float width, boolean fadeLimbs, boolean rollingColor, Color rollColor1, Color rollColor2, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        BufferBuilder buffer = INSTANCE.getBuffer();
        float xOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * mc.getRenderPartialTicks();
        float yOffset = entity.isSneaking() ? 0.6f : 0.75f;
        float yOffset2 = entity.isSneaking() ? 0.45f : 0.75f;
        Vec3d entityPos = EntityUtil.getInterpolatedEntityPos(entity, mc.getRenderPartialTicks());

        if (ESP.INSTANCE.espSkeletonDeathFade.getValue()) {
            if (entity.deathTime > 0 && ESP.INSTANCE.skeletonFadeData.containsKey(entity)) {
                float f = ESP.INSTANCE.skeletonFadeData.get(entity);
                f += ESP.INSTANCE.espSkeletonDeathFadeFactor.getValue() / 10.0f;
                if (f >= 300.0f)
                    f = 300.0f;

                a /= f;
                ESP.INSTANCE.skeletonFadeData.put(entity, f);
            }
            else {
                ESP.INSTANCE.skeletonFadeData.put(entity, 0.1f);
                a = color >>> 24 & 255;
            }
        }

        //* legs area
        //top
        double[] ld1 = MathUtilFuckYou.rotationAroundAxis3d(-0.125f, yOffset, entity.isSneaking() ? -0.235 : 0.0, -xOffset * (Math.PI / 180.0f), "y");
        double[] ld2 = MathUtilFuckYou.rotationAroundAxis3d(0.125f, yOffset, entity.isSneaking() ? -0.235 : 0.0, -xOffset * (Math.PI / 180.0f), "y");

        //bottom
        double[] ld6 = MathUtilFuckYou.rotationAroundAxis3d(-0.125f, -yOffset, entity.isSneaking() ? -0.235 : 0.0, rotations[3][0], "x");
        double[] ld9 = MathUtilFuckYou.rotationAroundAxis3d(ld6[0], ld6[1], ld6[2], -xOffset * (Math.PI / 180.0f), "y");
        double[] ld7 = MathUtilFuckYou.rotationAroundAxis3d(0.125f, -yOffset, entity.isSneaking() ? -0.235 : 0.0, rotations[4][0], "x");
        double[] ld8 = MathUtilFuckYou.rotationAroundAxis3d(ld7[0], ld7[1], ld7[2], -xOffset * (Math.PI / 180.0f), "y");

        //* torso && head
        double[] td1 = MathUtilFuckYou.rotationAroundAxis3d(0.0, yOffset, entity.isSneaking() ? -0.235 : 0.0, -xOffset * (Math.PI / 180.0f), "y");
        double[] td2 = MathUtilFuckYou.rotationAroundAxis3d(0.0, yOffset2 + 0.55 + (entity.isSneaking() ? -0.05 : 0.0), entity.isSneaking() ? -0.0025 : 0.0, -xOffset * (Math.PI / 180.0f), "y");
        double[] td3 = MathUtilFuckYou.rotationAroundAxis3d(0.0,  0.3, entity.isSneaking() ? -0.0035 : 0.0, rotations[0][0], "x");
        double[] td4 = MathUtilFuckYou.rotationAroundAxis3d(td3[0], td3[1], td3[2], -(entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * mc.getRenderPartialTicks()) * (Math.PI / 180.0f), "y");

        //* arms
        //mid
        double[] ad1 = MathUtilFuckYou.rotationAroundAxis3d(-0.375, yOffset2 + 0.55 + (entity.isSneaking() ? -0.05 : 0.0), entity.isSneaking() ? -0.0025 : 0.0, -xOffset * (Math.PI / 180.0f), "y");
        double[] ad2 = MathUtilFuckYou.rotationAroundAxis3d(0.375, yOffset2 + 0.55 + (entity.isSneaking() ? -0.05 : 0.0), entity.isSneaking() ? -0.0025 : 0.0, -xOffset * (Math.PI / 180.0f), "y");

        //actual arms
        //right
        double[] ad3 = MathUtilFuckYou.rotationAroundAxis3d( 0.0, -0.55, 0.0, rotations[1][0], "x");
        double[] ad31 = MathUtilFuckYou.rotationAroundAxis3d( ad3[0], ad3[1], ad3[2], -rotations[1][1], "y");
        double[] ad5 = MathUtilFuckYou.rotationAroundAxis3d(ad31[0], ad31[1], ad31[2], -rotations[1][2], "z");
        double[] ad6 = MathUtilFuckYou.rotationAroundAxis3d(ad5[0] - 0.375, ad5[1] + yOffset2 + 0.55f, ad5[2] + (entity.isSneaking() ? 0.02 : 0.0), -xOffset * (Math.PI / 180.0f), "y");
        //left
        double[] ad7 = MathUtilFuckYou.rotationAroundAxis3d( 0.0, -0.55, 0.0, rotations[2][0], "x");
        double[] ad71 = MathUtilFuckYou.rotationAroundAxis3d(ad7[0], ad7[1], ad7[2], -rotations[2][1], "y");
        double[] ad8 = MathUtilFuckYou.rotationAroundAxis3d(ad71[0], ad71[1], ad71[2], -rotations[2][2], "z");
        double[] ad9 = MathUtilFuckYou.rotationAroundAxis3d(ad8[0] + 0.375, ad8[1] + yOffset2    + 0.55f, ad8[2] + (entity.isSneaking() ? 0.02 : 0.0), -xOffset * (Math.PI / 180.0f), "y");

        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glLineWidth(width);
        begin(3);

        if (rollingColor) {
            Color rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 0, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            if (fadeLimbs) buffer.pos(entityPos.x + ld9[0], entityPos.y + yOffset + ld9[1], entityPos.z + ld9[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ld9[0], entityPos.y + yOffset + ld9[1], entityPos.z + ld9[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 1000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            buffer.pos(entityPos.x + ld1[0], entityPos.y + ld1[1], entityPos.z + ld1[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 1000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            buffer.pos(entityPos.x + ld2[0], entityPos.y + ld2[1], entityPos.z + ld2[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 0, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            if (fadeLimbs) buffer.pos(entityPos.x + ld8[0], entityPos.y + yOffset + ld8[1], entityPos.z + ld8[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ld8[0], entityPos.y + yOffset + ld8[1], entityPos.z + ld8[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            render();
            begin(3);

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 1000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            buffer.pos(entityPos.x + td1[0], entityPos.y + td1[1], entityPos.z + td1[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();
            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 2000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            buffer.pos(entityPos.x + td2[0], entityPos.y + td2[1], entityPos.z + td2[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();
            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 3000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            if (fadeLimbs) buffer.pos(entityPos.x + td4[0], entityPos.y + yOffset2 + 0.55f + (entity.isSneaking() ? -0.05 : 0.0) + td4[1], entityPos.z + td4[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + td4[0], entityPos.y + yOffset2 + 0.55f + (entity.isSneaking() ? -0.05 : 0.0) + td4[1], entityPos.z + td4[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            render();
            begin(3);

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 1000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            if (fadeLimbs) buffer.pos(entityPos.x + ad6[0], entityPos.y + ad6[1], entityPos.z + ad6[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ad6[0], entityPos.y + ad6[1], entityPos.z + ad6[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 2000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            buffer.pos(entityPos.x + ad1[0], entityPos.y + ad1[1], entityPos.z + ad1[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();
            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 2000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            buffer.pos(entityPos.x + ad2[0], entityPos.y + ad2[1], entityPos.z + ad2[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();

            rollColor = ColorUtil.rolledColor(rollColor1, rollColor2, 1000, ESP.INSTANCE.espSkeletonRollingColorSpeed.getValue(), 0.1f);
            if (fadeLimbs) buffer.pos(entityPos.x + ad9[0], entityPos.y + ad9[1], entityPos.z + ad9[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ad9[0], entityPos.y + ad9[1], entityPos.z + ad9[2]).color(rollColor.getRed(), rollColor.getGreen(), rollColor.getBlue(), a).endVertex();
        }
        else {
            if (fadeLimbs) buffer.pos(entityPos.x + ld9[0], entityPos.y + yOffset + ld9[1], entityPos.z + ld9[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ld9[0], entityPos.y + yOffset + ld9[1], entityPos.z + ld9[2]).color(r, g, b, a).endVertex();
            buffer.pos(entityPos.x + ld1[0], entityPos.y + ld1[1], entityPos.z + ld1[2]).color(r, g, b, a).endVertex();

            buffer.pos(entityPos.x + ld2[0], entityPos.y + ld2[1], entityPos.z + ld2[2]).color(r, g, b, a).endVertex();
            if (fadeLimbs) buffer.pos(entityPos.x + ld8[0], entityPos.y + yOffset + ld8[1], entityPos.z + ld8[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ld8[0], entityPos.y + yOffset + ld8[1], entityPos.z + ld8[2]).color(r, g, b, a).endVertex();

            render();
            begin(3);

            buffer.pos(entityPos.x + td1[0], entityPos.y + td1[1], entityPos.z + td1[2]).color(r, g, b, a).endVertex();
            buffer.pos(entityPos.x + td2[0], entityPos.y + td2[1], entityPos.z + td2[2]).color(r, g, b, a).endVertex();
            if (fadeLimbs) buffer.pos(entityPos.x + td4[0], entityPos.y + yOffset2 + 0.55f + (entity.isSneaking() ? -0.05 : 0.0) + td4[1], entityPos.z + td4[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + td4[0], entityPos.y + yOffset2 + 0.55f + (entity.isSneaking() ? -0.05 : 0.0) + td4[1], entityPos.z + td4[2]).color(r, g, b, a).endVertex();

            render();
            begin(3);

            if (fadeLimbs) buffer.pos(entityPos.x + ad6[0], entityPos.y + ad6[1], entityPos.z + ad6[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ad6[0], entityPos.y + ad6[1], entityPos.z + ad6[2]).color(r, g, b, a).endVertex();
            buffer.pos(entityPos.x + ad1[0], entityPos.y + ad1[1], entityPos.z + ad1[2]).color(r, g, b, a).endVertex();
            buffer.pos(entityPos.x + ad2[0], entityPos.y + ad2[1], entityPos.z + ad2[2]).color(r, g, b, a).endVertex();
            if (fadeLimbs) buffer.pos(entityPos.x + ad9[0], entityPos.y + ad9[1], entityPos.z + ad9[2]).color(0, 0, 0, 0.0f).endVertex();
            else buffer.pos(entityPos.x + ad9[0], entityPos.y + ad9[1], entityPos.z + ad9[2]).color(r, g, b, a).endVertex();
        }
        render();

        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawXCross(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a) {
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(1);
        buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();

        buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();

        buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();

        buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientXCross(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        GL11.glEnable(GL_LINE_SMOOTH);
        
        begin(1);
        buffer.pos(x, y, z).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w, y + h, z + d).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x, y, z + d).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w, y + h, z).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + w, y, z).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x, y + h, z + d).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + w, y, z + d).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x, y + h, z).color(r2, g2, b2, a2).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawFlatXCross(BufferBuilder buffer, float x, float y, float z, float w, float d, int r, int g, int b, int a) {
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(1);
        buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();

        buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawDoublePointXCross(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(1);
        buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z2).color(r, g, b, a).endVertex();

        buffer.pos(x1, y1, z2).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z1).color(r, g, b, a).endVertex();

        buffer.pos(x2, y1, z1).color(r, g, b, a).endVertex();
        buffer.pos(x1, y2, z2).color(r, g, b, a).endVertex();

        buffer.pos(x1, y2, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2, y1, z2).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientDoublePointXCross(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(1);
        buffer.pos(x1, y1, z1).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2, y2, z2).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x1, y1, z2).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2, y2, z1).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x2, y1, z1).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1, y2, z2).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x2, y1, z2).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1, y2, z1).color(r2, g2, b2, a2).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawDoublePointFlatXCross(BufferBuilder buffer, float x1, float y, float z1, float x2, float z2, int r, int g, int b, int a) {
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(1);
        buffer.pos(x1, y, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2, y, z2).color(r, g, b, a).endVertex();

        buffer.pos(x1, y, z2).color(r, g, b, a).endVertex();
        buffer.pos(x2, y, z1).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawFlatLineBox(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float d, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawFlatFilledBox(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float d, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        begin(7);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawDoublePointFlatLineBox(BufferBuilder buffer, boolean useDepth, float x1, float y, float z1, float x2, float z2, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x1 + offset, y + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y + offset, z1 + offset).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawDoublePointFlatFilledBox(BufferBuilder buffer, boolean useDepth, float x1, float y, float z1, float x2, float z2, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        begin(7);
        buffer.pos(x1 + offset, y + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y + offset, z2 - offset).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawLineBox(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r, g, b, a).endVertex();
        render();

        begin(1);
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + d - offset).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawLinePyramid(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();

        buffer.pos(x + (w * 0.5f), y + h, z + (d * 0.5f)).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        render();

        begin(3);
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + (w * 0.5f), y + h, z + (d * 0.5f)).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientLinePyramid(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();

        buffer.pos(x + (w * 0.5f), y + h, z + (d * 0.5f)).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        render();

        begin(3);
        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + (w * 0.5f), y + h, z + (d * 0.5f)).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientLineBox(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
        render();

        begin(1);
        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawFilledBox(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        begin(8);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r, g, b, a).endVertex();

        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r, g, b, a).endVertex();

        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r, g, b, a).endVertex();

        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + d - offset).color(r, g, b, a).endVertex();

        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r, g, b, a).endVertex();
        render();

        begin(7);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();

        buffer.pos(x + offset, y + h, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawFilledPyramid(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glFrontFace(GL_CW);
        begin(6);
        buffer.pos(x + (w * 0.5f), y + h, z + (d * 0.5f)).color(r, g, b, a).endVertex();

        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        render();
        GL11.glFrontFace(GL_CCW);

        begin(7);
        buffer.pos(x + offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r, g, b, a).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawGradientFilledPyramid(BufferBuilder buffer, boolean useDepth, float x, float y, float z, float w, float h, float d, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glDisable(GL_CULL_FACE);
        begin(6);
        buffer.pos(x + (w * 0.5f), y + h, z + (d * 0.5f)).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        render();

        begin(7);
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        render();
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawGradientFilledBox(BufferBuilder buffer, boolean useDepth, boolean sidesOnly, float x, float y, float z, float w, float h, float d, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glDisable(GL_CULL_FACE);
        begin(8);
        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + w - offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x + offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
        render();

        if (!sidesOnly) {
            begin(7);
            buffer.pos(x + offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + w - offset, y + offset, z + offset).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + w - offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x + offset, y + offset, z + d - offset).color(r1, g1, b1, a1).endVertex();

            buffer.pos(x + offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x + w - offset, y + h, z + d - offset).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x + w - offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x + offset, y + h, z + offset).color(r2, g2, b2, a2).endVertex();
            render();
        }
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawTwoPointLineBox(BufferBuilder buffer, boolean useDepth, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        render();

        begin(1);
        buffer.pos(x1 + offset, y2, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawTwoPointLinePyramid(BufferBuilder buffer, boolean useDepth, boolean flagx, boolean flagz, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        float w = Math.abs(x1 - x2) * 0.5f;
        float d = Math.abs(z1 - z2) * 0.5f;

        if (flagx) {
            w *= -1.0f;
        }

        if (flagz) {
            d *= -1.0f;
        }

        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();

        buffer.pos(x1 + w, y2, z1 + d).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        render();

        begin(3);
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + w, y2, z1 + d).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientTwoPointLinePyramid(BufferBuilder buffer, boolean useDepth, boolean flagx, boolean flagz, float x1, float y1, float z1, float x2, float y2, float z2, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        float w = Math.abs(x1 - x2) * 0.5f;
        float d = Math.abs(z1 - z2) * 0.5f;

        if (flagx) {
            w *= -1.0f;
        }

        if (flagz) {
            d *= -1.0f;
        }

        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();

        buffer.pos(x1 + w, y2, z1 + d).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        render();

        begin(3);
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + w, y2, z1 + d).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientTwoPointLineBox(BufferBuilder buffer, boolean useDepth, float x1, float y1, float z1, float x2, float y2, float z2, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x1 + offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
        render();

        begin(1);
        buffer.pos(x1 + offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawTwoPointFilledBox(BufferBuilder buffer, boolean useDepth, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        begin(8);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r, g, b, a).endVertex();

        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r, g, b, a).endVertex();

        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r, g, b, a).endVertex();

        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z2 - offset).color(r, g, b, a).endVertex();

        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        render();

        begin(7);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();

        buffer.pos(x1 + offset, y2, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawTwoPointFilledPyramid(BufferBuilder buffer, boolean useDepth, boolean flagx, boolean flagz, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        double offset = useDepth ? 0.003 : 0.0;
        float w = Math.abs(x1 - x2) * 0.5f;
        float d = Math.abs(z1 - z2) * 0.5f;

        if (flagx) {
            w *= -1.0f;
        }

        if (flagz) {
            d *= -1.0f;
        }


        GL11.glFrontFace(GL_CW);
        begin(6);
        buffer.pos(x1 + w, y2, z1 + d).color(r, g, b, a).endVertex();

        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        render();
        GL11.glFrontFace(GL_CCW);

        begin(7);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r, g, b, a).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawGradientTwoPointFilledPyramid(BufferBuilder buffer, boolean useDepth, boolean flagx, boolean flagz, float x1, float y1, float z1, float x2, float y2, float z2, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        float w = Math.abs(x1 - x2) * 0.5f;
        float d = Math.abs(z1 - z2) * 0.5f;

        if (flagx) {
            w *= -1.0f;
        }

        if (flagz) {
            d *= -1.0f;
        }


        GL11.glDisable(GL_CULL_FACE);
        begin(6);
        buffer.pos(x1 + w, y2, z1 + d).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        render();

        begin(7);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        render();
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawGradientTwoPointFilledBox(BufferBuilder buffer, boolean useDepth, boolean sidesOnly, float x1, float y1, float z1, float x2, float y2, float z2, int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        double offset = useDepth ? 0.003 : 0.0;
        GL11.glDisable(GL_CULL_FACE);
        begin(8);
        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x2 - offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();

        buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1 + offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
        render();

        if (!sidesOnly) {
            begin(7);
            buffer.pos(x1 + offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x2 - offset, y1 + offset, z1 + offset).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x2 - offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();
            buffer.pos(x1 + offset, y1 + offset, z2 - offset).color(r1, g1, b1, a1).endVertex();

            buffer.pos(x1 + offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x2 - offset, y2, z2 - offset).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x2 - offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
            buffer.pos(x1 + offset, y2, z1 + offset).color(r2, g2, b2, a2).endVertex();
            render();
        }
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawBetterBoundingBoxLines(BufferBuilder buffer, AxisAlignedBB boundingBox, Vec3d vec, float scale, int r, int g, int b, int a) {
        boundingBox = EntityUtil.scaleBB(vec, boundingBox, scale);

        GL11.glEnable(GL_LINE_SMOOTH);
        begin(3);
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();
        render();

        begin(1);
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        render();
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawBetterBoundingBoxFilled(BufferBuilder buffer, AxisAlignedBB boundingBox, Vec3d vec, float scale, int r, int g, int b, int a) {
        boundingBox = EntityUtil.scaleBB(vec, boundingBox, scale);

        begin(8);
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();

        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();

        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();

        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();

        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();
        render();

        begin(7);
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(r, g, b, a).endVertex();

        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(r, g, b, a).endVertex();

        render();
    }

    public static AxisAlignedBB getBoundingFromPos(BlockPos pos) {
        IBlockState iBlockState = mc.world.getBlockState(pos);
        return iBlockState.getSelectedBoundingBox(mc.world, pos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D);
    }

    public static Vec3d[] verticesFromBlockFace(BlockPos pos, EnumFacing face) {
        AxisAlignedBB bb = getBoundingFromPos(pos);

        switch (face) {
            case UP: {
                return new Vec3d[] {
                        new Vec3d(bb.minX, bb.maxY, bb.minZ),
                        new Vec3d(bb.maxX, bb.maxY, bb.minZ),
                        new Vec3d(bb.maxX, bb.maxY, bb.maxZ),
                        new Vec3d(bb.minX, bb.maxY, bb.maxZ)
                };
            }

            case DOWN: {
                return new Vec3d[] {
                        new Vec3d(bb.minX, bb.minY, bb.minZ),
                        new Vec3d(bb.maxX, bb.minY, bb.minZ),
                        new Vec3d(bb.maxX, bb.minY, bb.maxZ),
                        new Vec3d(bb.minX, bb.minY, bb.maxZ)
                };
            }

            case NORTH: {
                return new Vec3d[] {
                        new Vec3d(bb.minX, bb.maxY, bb.minZ),
                        new Vec3d(bb.maxX, bb.maxY, bb.minZ),
                        new Vec3d(bb.maxX, bb.minY, bb.minZ),
                        new Vec3d(bb.minX, bb.minY, bb.minZ)
                };
            }

            case SOUTH: {
                return new Vec3d[] {
                        new Vec3d(bb.minX, bb.minY, bb.maxZ),
                        new Vec3d(bb.maxX, bb.minY, bb.maxZ),
                        new Vec3d(bb.maxX, bb.maxY, bb.maxZ),
                        new Vec3d(bb.minX, bb.maxY, bb.maxZ)
                };
            }

            case EAST: {
                return new Vec3d[] {
                        new Vec3d(bb.maxX, bb.minY, bb.minZ),
                        new Vec3d(bb.maxX, bb.minY, bb.maxZ),
                        new Vec3d(bb.maxX, bb.maxY, bb.maxZ),
                        new Vec3d(bb.maxX, bb.maxY, bb.minZ)
                };
            }

            case WEST: {
                return new Vec3d[] {
                        new Vec3d(bb.minX, bb.minY, bb.minZ),
                        new Vec3d(bb.minX, bb.minY, bb.maxZ),
                        new Vec3d(bb.minX, bb.maxY, bb.maxZ),
                        new Vec3d(bb.minX, bb.maxY, bb.minZ)
                };
            }
        }

        return new Vec3d[] {new Vec3d(0.0, 0.0, 0.0),
                new Vec3d(0.0, 0.0, 0.0),
                new Vec3d(0.0, 0.0, 0.0),
                new Vec3d(0.0, 0.0, 0.0)};
    }

    public static void drawBlockFaceFilledBB(BlockPos pos, EnumFacing face, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glDisable(GL_CULL_FACE);
        drawBlockFaceFilledBB(INSTANCE.getBuffer(), pos, face, r, g, b, a);
        GL11.glEnable(GL_CULL_FACE);
    }

    public static void drawBlockFaceFilledBB(BufferBuilder buffer, BlockPos pos, EnumFacing face, int r, int g, int b, int a) {
        Vec3d[] vertices = verticesFromBlockFace(pos, face);

        begin(7);
        buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[1].x, vertices[1].y, vertices[1].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[2].x, vertices[2].y, vertices[2].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[3].x, vertices[3].y, vertices[3].z).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawBlockFaceLinesBB(BlockPos pos, EnumFacing face, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GL11.glLineWidth(lineWidth);
        drawBlockFaceLinesBB(INSTANCE.getBuffer(), pos, face, r, g, b, a);
    }

    public static void drawBlockFaceLinesBB(BufferBuilder buffer, BlockPos pos, EnumFacing face, int r, int g, int b, int a) {
        Vec3d[] vertices = verticesFromBlockFace(pos, face);

        begin(3);
        buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[1].x, vertices[1].y, vertices[1].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[2].x, vertices[2].y, vertices[2].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[3].x, vertices[3].y, vertices[3].z).color(r, g, b, a).endVertex();
        buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).color(r, g, b, a).endVertex();
        render();
    }

    public static void drawLineToVec(Vec3d vec1, Vec3d vec2, float lineWidth, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        BufferBuilder buffer = INSTANCE.getBuffer();

        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL_LINE_SMOOTH);

        begin(1);
        buffer.pos(vec1.x, vec1.y, vec1.z).color(r, g, b, a).endVertex();
        buffer.pos(vec2.x, vec2.y, vec2.z).color(r, g, b, a).endVertex();
        render();

        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawTracer(Entity entity, float lineWidth, boolean spine, int color) {
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        Vec3d entityPos = EntityUtil.interpolateEntityRender(entity, mc.getRenderPartialTicks());
        assert mc.renderViewEntity != null;
        Vec3d selfPos = EntityUtil.interpolateEntityRender(mc.renderViewEntity, mc.getRenderPartialTicks());
        double[] rotations = MathUtilFuckYou.rotationAroundAxis3d(0.0f, 0.0f, 1.0f, mc.renderViewEntity.rotationPitch * (float)(Math.PI / 180.0f), "x");
        rotations = MathUtilFuckYou.rotationAroundAxis3d(rotations[0], rotations[1], rotations[2], -mc.renderViewEntity.rotationYaw * (float)(Math.PI / 180.0f), "y");
        selfPos = new Vec3d(selfPos.x + rotations[0], selfPos.y + mc.renderViewEntity.getEyeHeight() + rotations[1], selfPos.z + rotations[2]);

        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);

        if (mc.gameSettings.viewBobbing) {
            GL11.glLoadIdentity();
            mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
        }

        //doesnt use bufferbuilder bc viewbobbing is gay and it fucks up the tracers centering on the crosshairs >:(
        GL11.glBegin(1);
        if (spine) {
            GL11.glVertex3d(entityPos.x, entityPos.y, entityPos.z);
            GL11.glVertex3d(entityPos.x, entityPos.y + entity.height, entityPos.z);
        }

        GL11.glVertex3d(entityPos.x, entityPos.y, entityPos.z);
        GL11.glVertex3d(selfPos.x, selfPos.y, selfPos.z);

        GL11.glEnd();
        GL11.glColor4f(1, 1, 1, 1);

        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void drawPlayer(EntityOtherPlayerMP entityPlayer, ModelPlayer model, float limbSwing, float limbSwingAmount, float headYaw, float headPitch, boolean solid, boolean lines, boolean points, float lineWidth, float pointSize, float alphaFactor, boolean texture, float swingProgress,
                                  int solidColorFriend, int lineColorFriend, int pointColorFriend,
                                  int solidColorEnemy, int lineColorEnemy, int pointColorEnemy,
                                  int solidColorSelf, int lineColorSelf, int pointColorSelf,
                                  int solidColor, int lineColor, int pointColor) {
        int sr, sg, sb, sa, lr, lg, lb, la, pr, pg, pb, pa;

        if (entityPlayer.getName().equals(mc.player.getName())) {
            sa = (int)((solidColorSelf >>> 24 & 255) * alphaFactor / 300.0f);
            sr = solidColorSelf >>> 16 & 255;
            sg = solidColorSelf >>> 8 & 255;
            sb = solidColorSelf & 255;

            la = (int)((lineColorSelf >>> 24 & 255) * alphaFactor / 300.0f);
            lr = lineColorSelf >>> 16 & 255;
            lg = lineColorSelf >>> 8 & 255;
            lb = lineColorSelf & 255;

            pa = (int)((pointColorSelf >>> 24 & 255) * alphaFactor / 300.0f);
            pr = pointColorSelf >>> 16 & 255;
            pg = pointColorSelf >>> 8 & 255;
            pb = pointColorSelf & 255;
        }
        else {
            if (FriendManager.isFriend(entityPlayer)) {
                sa = (int)((solidColorFriend >>> 24 & 255) * alphaFactor / 300.0f);
                sr = solidColorFriend >>> 16 & 255;
                sg = solidColorFriend >>> 8 & 255;
                sb = solidColorFriend & 255;

                la = (int)((lineColorFriend >>> 24 & 255) * alphaFactor / 300.0f);
                lr = lineColorFriend >>> 16 & 255;
                lg = lineColorFriend >>> 8 & 255;
                lb = lineColorFriend & 255;

                pa = (int)((pointColorFriend >>> 24 & 255) * alphaFactor / 300.0f);
                pr = pointColorFriend >>> 16 & 255;
                pg = pointColorFriend >>> 8 & 255;
                pb = pointColorFriend & 255;
            } else if (EnemyManager.isEnemy(entityPlayer)) {
                sa = (int)((solidColorEnemy >>> 24 & 255) * alphaFactor / 300.0f);
                sr = solidColorEnemy >>> 16 & 255;
                sg = solidColorEnemy >>> 8 & 255;
                sb = solidColorEnemy & 255;

                la = (int)((lineColorEnemy >>> 24 & 255) * alphaFactor / 300.0f);
                lr = lineColorEnemy >>> 16 & 255;
                lg = lineColorEnemy >>> 8 & 255;
                lb = lineColorEnemy & 255;

                pa = (int)((pointColorEnemy >>> 24 & 255) * alphaFactor / 300.0f);
                pr = pointColorEnemy >>> 16 & 255;
                pg = pointColorEnemy >>> 8 & 255;
                pb = pointColorEnemy & 255;
            } else {
                sa = (int)((solidColor >>> 24 & 255) * alphaFactor / 300.0f);
                sr = solidColor >>> 16 & 255;
                sg = solidColor >>> 8 & 255;
                sb = solidColor & 255;

                la = (int)((lineColor >>> 24 & 255) * alphaFactor / 300.0f);
                lr = lineColor >>> 16 & 255;
                lg = lineColor >>> 8 & 255;
                lb = lineColor & 255;

                pa = (int)((pointColor >>> 24 & 255) * alphaFactor / 300.0f);
                pr = pointColor >>> 16 & 255;
                pg = pointColor >>> 8 & 255;
                pb = pointColor & 255;
            }
        }

        GL11.glPushMatrix();
        GL11.glTranslated(EntityUtil.interpolateEntityRender(entityPlayer, mc.getRenderPartialTicks()).x,
                            EntityUtil.interpolateEntityRender(entityPlayer, mc.getRenderPartialTicks()).y,
                            EntityUtil.interpolateEntityRender(entityPlayer, mc.getRenderPartialTicks()).z);
        GlStateManager.enableRescaleNormal();
        GL11.glRotatef(180.0f - entityPlayer.rotationYaw, 0.0f, 1.0f, 0.0f);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
        GL11.glTranslatef(0.0f, -1.501f, 0.0f);

        if (solid) {
            if (texture) {
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GL11.glDepthRange(0.0, 0.01);
                GL11.glEnable(GL_TEXTURE_2D);
                Command.mc.getTextureManager().bindTexture(entityPlayer.getLocationSkin());
            }
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            GL11.glColor4f(sr / 255.0f, sg / 255.0f, sb / 255.0f, sa / 255.0f);
            model.render(entityPlayer, limbSwing, limbSwingAmount, entityPlayer.ticksExisted, headYaw, headPitch, 0.0625f);
            if (texture) {
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glDisable(GL_TEXTURE_2D);
            }
        }

        if (lines) {
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            GL11.glLineWidth(lineWidth);
            GL11.glEnable(GL_LINE_SMOOTH);
            GL11.glColor4f(lr / 255.0f, lg / 255.0f, lb / 255.0f, la / 255.0f);
            model.render(entityPlayer, limbSwing, limbSwingAmount, entityPlayer.ticksExisted, headYaw, headPitch, 0.0625f);
            GL11.glDisable(GL_LINE_SMOOTH);
        }

        if (points) {
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
            GL11.glPointSize(pointSize);
            GL11.glEnable(GL_POINT_SMOOTH);
            GL11.glColor4f(pr / 255.0f, pg / 255.0f, pb / 255.0f, pa / 255.0f);
            model.render(entityPlayer, limbSwing, limbSwingAmount, entityPlayer.ticksExisted, headYaw, headPitch, 0.0625f);
            GL11.glDisable(GL_POINT_SMOOTH);
        }

        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
}