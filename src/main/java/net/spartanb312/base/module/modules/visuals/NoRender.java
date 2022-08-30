package net.spartanb312.base.module.modules.visuals;

import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.event.events.render.RenderEntityPreEvent;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.spartanb312.base.utils.EntityUtil;

@Parallel(runnable = true)
@ModuleInfo(name = "NoRender", category = Category.VISUALS, description = "Stop rendering certain things")
public class NoRender extends Module {

    public static NoRender INSTANCE;

    Setting<Page> page = setting("Page", Page.Overlays);

    Setting<Boolean> blindness = setting("Blindness", true).whenAtMode(page, Page.Overlays);
    Setting<Boolean> nausea = setting("Nausea", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> netherPortal = setting("NetherPortal", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> fire = setting("Fire", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> blockOverlay = setting("BlockOverlay", false).whenAtMode(page, Page.Overlays);
    public Setting<BossBarMode> bossBar = setting("BossBar", BossBarMode.None).whenAtMode(page, Page.Overlays);
    public Setting<Float> bossBarSize = setting("BossBarScale", 0.5f, 0.1f, 1.0f).only(v -> bossBar.getValue() == BossBarMode.Stack).whenAtMode(page, Page.Overlays);
    public Setting<TotemMode> totemPop = setting("TotemPop", TotemMode.None).whenAtMode(page, Page.Overlays);
    public Setting<Float> totemSize = setting("TotemSize", 0.5f, 0.1f, 1.0f).whenAtMode(totemPop, TotemMode.Scale).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> waterOverlay = setting("WaterOverlay", false).whenAtMode(page, Page.Overlays);
    Setting<Boolean> tutorial = setting("Tutorial", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> potionIcons = setting("PotionIcons", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> pumpkin = setting("PumpkinOverlay", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> vignette = setting("Vignette", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> hurtCam = setting("HurtCam", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> chat = setting("Chat", false).whenAtMode(page, Page.Overlays);
    public Setting<Boolean> backgrounds = setting("Backgrounds", false).whenAtMode(page, Page.Overlays);

    public Setting<Boolean> players = setting("Players", false).whenAtMode(page, Page.World);
    public Setting<Boolean> mobs = setting("Mobs", false).whenAtMode(page, Page.World);
    public Setting<Boolean> animals = setting("Animals", false).whenAtMode(page, Page.World);
    Setting<Boolean> items = setting("Items", false).whenAtMode(page, Page.World);
    public Setting<Boolean> armor = setting("Armor", false).whenAtMode(page, Page.World);
    Setting<Boolean> projectiles = setting("Projectiles", false).whenAtMode(page, Page.World);
    Setting<Boolean> xp = setting("XP", false).whenAtMode(page, Page.World);
    Setting<Boolean> explosion = setting("Explosions", true).whenAtMode(page, Page.World);
    public Setting<Boolean> fog = setting("Fog", false).des("Also disables the orange effect inside of lava").whenAtMode(page, Page.World);
    Setting<Boolean> paint = setting("Paintings", false).whenAtMode(page, Page.World);
    public Setting<Boolean> chests = setting("Chests", false).whenAtMode(page, Page.World);
    public Setting<Boolean> enderChests = setting("EnderChests", false).whenAtMode(page, Page.World);
    public Setting<Boolean> enchantingTableBook = setting("EnchantTableBook", false).whenAtMode(page, Page.World);
    public Setting<Boolean> maps = setting("Maps", false).whenAtMode(page, Page.World);
    public Setting<Boolean> signText = setting("SignText", false).whenAtMode(page, Page.World);
    public Setting<Boolean> skyLightUpdate = setting("SkyLightUpdate", false).whenAtMode(page, Page.World);
    Setting<Boolean> fallingBlocks = setting("FallingBlocks", false).whenAtMode(page, Page.World);

    public NoRender() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (blindness.getValue())
            mc.player.removeActivePotionEffect(MobEffects.BLINDNESS);

        if (nausea.getValue() || netherPortal.getValue())
            mc.player.removeActivePotionEffect(MobEffects.NAUSEA);

        if (tutorial.getValue())
            mc.gameSettings.tutorialStep = TutorialSteps.NONE;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        Packet<?> packet = event.packet;
        if ((packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) || (packet instanceof SPacketExplosion && explosion.getValue()) || (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.cancel();
    }

    @Listener
    public void onRenderEntityPre(RenderEntityPreEvent event) {
        if ((event.entityIn instanceof EntityPlayer && players.getValue()) || ((EntityUtil.isEntityMob(event.entityIn) || event.entityIn instanceof EntityDragon) && mobs.getValue()) || ((EntityUtil.isEntityAnimal(event.entityIn)) && animals.getValue()) || (event.entityIn instanceof EntityItem && items.getValue()) || ((event.entityIn instanceof IProjectile || event.entityIn instanceof EntityShulkerBullet || event.entityIn instanceof EntityFireball || event.entityIn instanceof EntityEnderEye) && projectiles.getValue()) || (event.entityIn instanceof EntityFallingBlock && fallingBlocks.getValue()))
            event.cancel();
    }

    enum Page {
        Overlays,
        World
    }

    public enum BossBarMode {
        Stack,
        NoRender,
        None
    }

    public enum TotemMode {
        Scale,
        NoRender,
        None
    }
}