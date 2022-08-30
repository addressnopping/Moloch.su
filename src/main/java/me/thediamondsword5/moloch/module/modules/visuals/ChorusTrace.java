package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.EntityUtil;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import net.spartanb312.base.utils.math.Pair;

import java.util.HashMap;
import java.util.Map;
//TODO: rewrite after making breadcrumbs
@Parallel
@ModuleInfo(name = "ChorusTrace", category = Category.VISUALS, description = "Renders direction of player chorus teleporting")
public class ChorusTrace extends Module {

    Setting<Float> width = setting("Width", 2.5f, 1.0f, 5.0f).des("Width of line");
    Setting<Float> fadeSpeed = setting("FadeSpeed", 2.0f, 0.1f, 3.0f).des("Speed of how fast the line fades");
    Setting<Color> color = setting("Color", new Color(new java.awt.Color(255, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 120));
    Setting<Color> friendColor = setting("FriendColor", new Color(new java.awt.Color(50, 255, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 255, 120));
    Setting<Color> enemyColor = setting("EnemyColor", new Color(new java.awt.Color(255, 50, 50, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 120));

    private final HashMap<EntityPlayer, Vec3d> startChorusMap = new HashMap<>();
    private final HashMap<Long, EntityPlayer> endChorusMap = new HashMap<>();
    private final HashMap<Pair<Vec3d, Vec3d>, Pair<EntityPlayer, Float>> alphaMap = new HashMap<>();
    private final HashMap<Pair<Vec3d, Vec3d>, EntityPlayer> cachedChorusMap = new HashMap<>();
    private final Timer timer = new Timer();

    @Override
    public void onTick() {
        for (EntityPlayer entityPlayer : new HashMap<>(startChorusMap).keySet()) {
            if (!EntityUtil.entitiesList().contains(entityPlayer)) {
                startChorusMap.remove(entityPlayer);
            }
        }

        EntityUtil.entitiesListFlag = true;
        for (Entity entity : EntityUtil.entitiesList()) {
            if (!(entity instanceof EntityPlayer))
                continue;

            if (((EntityPlayer)entity).getActiveItemStack().getItem() != Items.CHORUS_FRUIT) {
                continue;
            }

            startChorusMap.put((EntityPlayer) entity, entity.getPositionVector());
        }
        EntityUtil.entitiesListFlag = false;

        for (Map.Entry<Long, EntityPlayer> entry : new HashMap<>(endChorusMap).entrySet()) {
            if (System.currentTimeMillis() - entry.getKey() >= 100) {
                cachedChorusMap.put(new Pair<>(startChorusMap.get(entry.getValue()), entry.getValue().getPositionVector()), entry.getValue());
                endChorusMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            if (((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT || ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ENTITY_ENDERMEN_TELEPORT) {
                ((SPacketSoundEffect) event.getPacket()).soundVolume = 100.0f;
                Vec3d soundVec = new Vec3d(((SPacketSoundEffect) event.getPacket()).posX, ((SPacketSoundEffect) event.getPacket()).posY, ((SPacketSoundEffect) event.getPacket()).posZ);
                EntityPlayer closestPlayer = mc.player;
                double lowestDist = 99999.0f;

                for (EntityPlayer player : startChorusMap.keySet()) {
                    if (MathUtilFuckYou.getDistance(soundVec, player.getPositionVector()) < lowestDist) {
                        lowestDist = MathUtilFuckYou.getDistance(soundVec, player.getPositionVector());
                        closestPlayer = player;
                    }
                }

                if (startChorusMap.containsKey(closestPlayer)) {
                    endChorusMap.put(System.currentTimeMillis(), closestPlayer);
                }
            }
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        for (Map.Entry<Pair<Vec3d, Vec3d>, EntityPlayer> entry : new HashMap<>(cachedChorusMap).entrySet()) {
            java.awt.Color color;
            int alpha;

            if (FriendManager.isFriend(entry.getValue())) {
                color = friendColor.getValue().getColorColor();
                alpha = friendColor.getValue().getAlpha();
            } else if (EnemyManager.isEnemy(entry.getValue())) {
                color = enemyColor.getValue().getColorColor();
                alpha = enemyColor.getValue().getAlpha();
            } else {
                color = this.color.getValue().getColorColor();
                alpha = this.color.getValue().getAlpha();
            }

            alphaMap.putIfAbsent(entry.getKey(), new Pair<>(entry.getValue(), 300.0f));

            SpartanTessellator.drawLineToVec(entry.getKey().a, entry.getKey().b, width.getValue(), new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alphaMap.get(entry.getKey()).b / 300.0f * alpha)).getRGB());
        }

        int passedms = (int) timer.hasPassed();
        timer.reset();
        if (passedms < 1000) {
            for (Map.Entry<Pair<Vec3d, Vec3d>, Pair<EntityPlayer, Float>> entry : new HashMap<>(alphaMap).entrySet()) {
                if (entry.getValue().b - passedms * fadeSpeed.getValue() <= 0.0f) {
                    alphaMap.remove(entry.getKey());
                    cachedChorusMap.remove(entry.getKey());
                    continue;
                }

                alphaMap.put(entry.getKey(), new Pair<>(entry.getValue().a, entry.getValue().b - passedms * fadeSpeed.getValue() / 10.0f));
            }
        }
    }
}