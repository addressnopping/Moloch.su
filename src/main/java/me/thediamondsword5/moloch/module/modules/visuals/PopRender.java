package me.thediamondsword5.moloch.module.modules.visuals;

import com.mojang.authlib.GameProfile;
import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.EnumHand;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.RenderHelper;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import net.spartanb312.base.utils.math.Pair;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.spartanb312.base.utils.ItemUtils.mc;

//TODO: Add deathmode
@Parallel
@ModuleInfo(name = "PopRender", category = Category.VISUALS, description = "Renders their player model when someone pops a totem")
public class PopRender extends Module {

    Setting<Page> page = setting("Page", Page.General);
    Setting<Boolean> self = setting("Self", false).des("Render yourself popping").whenAtMode(page, Page.General);
    Setting<Boolean> points = setting("Points", false).des("Renders vertices of model").whenAtMode(page, Page.General);
    Setting<Float> pointSize = setting("PointSize", 1.0f, 0.0f, 10.0f).des("Size of points on model").whenTrue(points).whenAtMode(page, Page.General);
    Setting<Boolean> solid = setting("Solid", true).des("Render filled model").whenAtMode(page, Page.General);
    Setting<Boolean> texture = setting("Texture", false).des("Renders player texture on filled model").whenTrue(solid).whenAtMode(page, Page.General);
    Setting<Boolean> lines = setting("Lines", true).des("Render wireframe model").whenAtMode(page, Page.General);
    Setting<Float> linesWidth = setting("LinesWidth", 1.0f, 1.0f, 5.0f).des("Width of wireframe model lines").whenTrue(lines).whenAtMode(page, Page.General);
    Setting<Boolean> skeleton = setting("Skeleton", false).des("Renders skeleton of player").whenAtMode(page, Page.General);
    Setting<Float> skeletonLinesWidth = setting("SkeletonLinesWidth", 1.0f, 1.0f, 5.0f).des("Width of lines in skeleton").whenTrue(skeleton).whenAtMode(page, Page.General);
    Setting<Boolean> skeletonFadeLimbs = setting("SkeletonFadeLimbs", false).des("Fade out the ends of skeleton's limbs").whenTrue(skeleton).whenAtMode(page, Page.General);
    Setting<Boolean> limbRotations = setting("LimbRotations", true).des("Records limb rotations when the player is popped onto pop render").whenAtMode(page, Page.General);
    Setting<Boolean> multiRender = setting("MultiRender", true).des("Allows client to render multiple models for one player when they chain pop").whenAtMode(page, Page.General);
    Setting<Movement> movement = setting("Movement", Movement.None).des("Movement of pop model").whenAtMode(page, Page.General);
    Setting<Float> moveSpeed = setting("MovementSpeed", 1.0f, 0.1f, 3.0f).only(v -> movement.getValue() != Movement.None).whenAtMode(page, Page.General);
    Setting<Float> fadeSpeed = setting("FadeSpeed", 2.0f, 0.1f, 3.0f).des("Speed of pop render fading away").whenAtMode(page, Page.General);

    Setting<Color> selfPointColor = setting("SelfPointColor", new Color(new java.awt.Color(255, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 120)).whenTrue(points).whenTrue(self).whenAtMode(page, Page.Colors);
    Setting<Color> selfSolidColor = setting("SelfSolidColor", new Color(new java.awt.Color(255, 255, 255, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 40)).whenTrue(solid).whenTrue(self).whenAtMode(page, Page.Colors);
    Setting<Color> selfLinesColor = setting("SelfLinesColor", new Color(new java.awt.Color(255, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 120)).whenTrue(lines).whenTrue(self).whenAtMode(page, Page.Colors);
    Setting<Color> selfSkeletonColor = setting("SelfSkeletonColor", new Color(new java.awt.Color(255, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 120)).whenTrue(skeleton).whenTrue(self).whenAtMode(page, Page.Colors);

    Setting<Color> playerPointColor = setting("PlayerPointColor", new Color(new java.awt.Color(180, 255, 180, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 180, 255, 180, 120)).whenTrue(points).whenAtMode(page, Page.Colors);
    Setting<Color> playerSolidColor = setting("PlayerSolidColor", new Color(new java.awt.Color(180, 255, 180, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 180, 255, 180, 40)).whenTrue(solid).whenAtMode(page, Page.Colors);
    Setting<Color> playerLinesColor = setting("PlayerLinesColor", new Color(new java.awt.Color(180, 255, 180, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 180, 255, 180, 120)).whenTrue(lines).whenAtMode(page, Page.Colors);
    Setting<Color> playerSkeletonColor = setting("PlayerSkeletonColor", new Color(new java.awt.Color(180, 255, 180, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 180, 255, 180, 120)).whenTrue(skeleton).whenAtMode(page, Page.Colors);

    Setting<Color> friendPointColor = setting("FriendPointColor", new Color(new java.awt.Color(100, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 255, 255, 120)).whenTrue(points).whenAtMode(page, Page.Colors);
    Setting<Color> friendSolidColor = setting("FriendSolidColor", new Color(new java.awt.Color(100, 255, 255, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 255, 255, 40)).whenTrue(solid).whenAtMode(page, Page.Colors);
    Setting<Color> friendLinesColor = setting("FriendLinesColor", new Color(new java.awt.Color(100, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 255, 255, 120)).whenTrue(lines).whenAtMode(page, Page.Colors);
    Setting<Color> friendSkeletonColor = setting("FriendSkeletonColor", new Color(new java.awt.Color(100, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 255, 255, 120)).whenTrue(skeleton).whenAtMode(page, Page.Colors);

    Setting<Color> enemyPointColor = setting("EnemyPointColor", new Color(new java.awt.Color(255, 100, 100, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 120)).whenTrue(points).whenAtMode(page, Page.Colors);
    Setting<Color> enemySolidColor = setting("EnemySolidColor", new Color(new java.awt.Color(255, 100, 100, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 40)).whenTrue(solid).whenAtMode(page, Page.Colors);
    Setting<Color> enemyLinesColor = setting("EnemyLinesColor", new Color(new java.awt.Color(255, 100, 100, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 120)).whenTrue(lines).whenAtMode(page, Page.Colors);
    Setting<Color> enemySkeletonColor = setting("EnemySkeletonColor", new Color(new java.awt.Color(255, 100, 100, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 120)).whenTrue(skeleton).whenAtMode(page, Page.Colors);

    private final ModelPlayer model = new ModelPlayer(0.0f, false);;
    private final Timer timer = new Timer();
    private final HashMap<Integer, Pair<EntityOtherPlayerMP, Float>> popMapSingle = new HashMap<>();
    private final HashMap<Integer, Float> popMoveMapSingle = new HashMap<>();
    private final HashMap<EntityOtherPlayerMP, Float> popMapMulti = new HashMap<>();
    private final HashMap<EntityOtherPlayerMP, Float> popMoveMapMulti = new HashMap<>();

    public PopRender() {
        model.isChild = false;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.world != null && mc.player != null && event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            if (!(((SPacketEntityStatus) event.getPacket()).getEntity(mc.world) instanceof EntityPlayer)) {
                return;
            }

            EntityPlayer player1 = (EntityPlayer) ((SPacketEntityStatus) event.getPacket()).getEntity(mc.world);

            if (player1 == mc.player && !self.getValue()) {
                return;
            }

            EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), player1.getName()));
            player.copyLocationAndAnglesFrom(player1);

            if (player1.isSneaking()) player.setSneaking(true);
            player.swingProgress = player1.swingProgress;
            player.limbSwing = player1.limbSwing;
            player.limbSwingAmount = player1.limbSwingAmount;

            if (multiRender.getValue()) {
                popMapMulti.put(player, 300.0f);
            }
            else {
                popMapSingle.put(player1.getEntityId(), new Pair<>(player, 300.0f));
            }
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        int passedms = (int) timer.hasPassed();
        timer.reset();

        if (multiRender.getValue()) {
            for (Map.Entry<EntityOtherPlayerMP, Float> entry : new HashMap<>(popMapMulti).entrySet()) {
                if (entry.getValue() <= 0.0f) {
                    popMapMulti.remove(entry.getKey());
                    popMoveMapMulti.remove(entry.getKey());
                    continue;
                }

                render(entry.getKey(), limbRotations.getValue() ? entry.getKey().limbSwing : 0.0f,
                        limbRotations.getValue() ? entry.getKey().limbSwingAmount : 0.0f,
                        limbRotations.getValue() ? entry.getKey().swingProgress : 0.0f,
                        entry.getValue(),
                        popMoveMapMulti.get(entry.getKey()) == null ? 0.0f : popMoveMapMulti.get(entry.getKey()));

                if (passedms < 1000) {
                    popMapMulti.put(entry.getKey(), entry.getValue() - (passedms * (fadeSpeed.getValue() / 5.0f)));

                    if (movement.getValue() != Movement.None) {
                        popMoveMapMulti.put(entry.getKey(), (popMoveMapMulti.get(entry.getKey()) == null ? 0.0f : popMoveMapMulti.get(entry.getKey())) + (passedms * (moveSpeed.getValue() / 1400.0f)));
                    }
                }
            }
        }
        else {
            for (Map.Entry<Integer, Pair<EntityOtherPlayerMP, Float>> entry : new HashMap<>(popMapSingle).entrySet()) {
                if (entry.getValue().b <= 0.0f) {
                    popMapSingle.remove(entry.getKey());
                    popMoveMapSingle.remove(entry.getKey());
                    continue;
                }

                render(entry.getValue().a, limbRotations.getValue() ? entry.getValue().a.limbSwing : 0.0f,
                        limbRotations.getValue() ? entry.getValue().a.limbSwingAmount : 0.0f,
                        limbRotations.getValue() ? entry.getValue().a.swingProgress : 0.0f,
                        entry.getValue().b,
                        popMoveMapSingle.get(entry.getKey()) == null ? 0.0f : popMoveMapSingle.get(entry.getKey()));

                if (passedms < 1000) {
                    popMapSingle.put(entry.getKey(), new Pair<>(entry.getValue().a, entry.getValue().b - (passedms * (fadeSpeed.getValue() / 5.0f))));

                    if (movement.getValue() != Movement.None) {
                        popMoveMapSingle.put(entry.getKey(), (popMoveMapSingle.get(entry.getKey()) == null ? 0.0f : popMoveMapSingle.get(entry.getKey())) + (passedms * (moveSpeed.getValue() / 1400.0f)));
                    }
                }
            }
        }
    }

    private void render(EntityOtherPlayerMP entityPlayer, float limbSwing, float limbSwingAmount, float swingProgress, float alphaFactor, float moveFactor) {
        EntityOtherPlayerMP tempPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), ""));
        tempPlayer.copyLocationAndAnglesFrom(entityPlayer);

        if (movement.getValue() != Movement.None) {
            tempPlayer.setLocationAndAngles(entityPlayer.posX, entityPlayer.posY + (moveFactor * (movement.getValue() == Movement.Rising ? 1.0f : -1.0f)), entityPlayer.posZ, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
        }

        if (RenderHelper.isInViewFrustrum(tempPlayer)) {

            model.bipedHead.showModel = solid.getValue() && texture.getValue();
            model.bipedBody.showModel = solid.getValue() && texture.getValue();
            model.bipedLeftArmwear.showModel = solid.getValue() && texture.getValue();
            model.bipedLeftLegwear.showModel = solid.getValue() && texture.getValue();
            model.bipedRightArmwear.showModel = solid.getValue() && texture.getValue();
            model.bipedRightLegwear.showModel = solid.getValue() && texture.getValue();
            entityPlayer.swingingHand = EnumHand.MAIN_HAND;
            entityPlayer.renderYawOffset = entityPlayer.rotationYaw;
            entityPlayer.prevRenderYawOffset = entityPlayer.rotationYaw;
            model.isSneak = entityPlayer.isSneaking();
            model.swingProgress = swingProgress;

            if (movement.getValue() != Movement.None) {
                GL11.glTranslatef(0.0f, moveFactor * (movement.getValue() == Movement.Rising ? 1.0f : -1.0f), 0.0f);
            }

            if (solid.getValue() || lines.getValue() || points.getValue()) {
                SpartanTessellator.drawPlayer(entityPlayer, model, limbSwing, limbSwingAmount, entityPlayer.rotationYawHead, entityPlayer.rotationPitch,
                        solid.getValue(), lines.getValue(), points.getValue(), linesWidth.getValue(), pointSize.getValue(), alphaFactor, texture.getValue(), swingProgress,
                        friendSolidColor.getValue().getColor(), friendLinesColor.getValue().getColor(), friendPointColor.getValue().getColor(),
                        enemySolidColor.getValue().getColor(), enemyLinesColor.getValue().getColor(), enemyPointColor.getValue().getColor(),
                        selfSolidColor.getValue().getColor(), selfLinesColor.getValue().getColor(), selfPointColor.getValue().getColor(),
                        playerSolidColor.getValue().getColor(), playerLinesColor.getValue().getColor(), playerPointColor.getValue().getColor());
            }

            if (skeleton.getValue()) {
                model.setRotationAngles(limbSwing, limbSwingAmount, 0.0f, entityPlayer.rotationYawHead, entityPlayer.rotationPitch, 0.0625f, entityPlayer);
                float[][] rotations = new float[5][3];
                rotations[0] = SpartanTessellator.getRotations(model.bipedHead);
                int color = entityPlayer.getName().equals(mc.player.getName()) ? selfSkeletonColor.getValue().getColor() : FriendManager.isFriend(entityPlayer) ? friendSkeletonColor.getValue().getColor() : EnemyManager.isEnemy(entityPlayer) ? enemySkeletonColor.getValue().getColor() : playerSkeletonColor.getValue().getColor();

                SpartanTessellator.drawSkeleton(entityPlayer, limbRotations.getValue() ? SpartanTessellator.getRotationsFromModel(model) : rotations, skeletonLinesWidth.getValue(), skeletonFadeLimbs.getValue(), false, new java.awt.Color(0), new java.awt.Color(0),
                        new java.awt.Color(color >>> 16 & 255, color >>> 8 & 255, color & 255, (int)((color >>> 24 & 255) * alphaFactor / 300.0f)).getRGB());

            }

            if (movement.getValue() != Movement.None) {
                GL11.glTranslatef(0.0f, moveFactor * (movement.getValue() == Movement.Rising ? -1.0f : 1.0f), 0.0f);
            }
        }
    }

    enum Page {
        General,
        Colors
    }

    enum Movement {
        Rising,
        Falling,
        None
    }
}
