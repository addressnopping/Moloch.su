package me.thediamondsword5.moloch.module.modules.visuals;

import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.concurrent.repeat.RepeatUnit;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.EntityUtil;
import net.spartanb312.base.utils.graphics.RenderHelper;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.event.events.render.RenderEntityEvent;
import me.thediamondsword5.moloch.event.events.render.RenderEntityInvokeEvent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runRepeat;
import static org.lwjgl.opengl.GL11.*;
//TODO: add distance color
@Parallel(runnable = true)
@ModuleInfo(name = "Chams", category = Category.VISUALS, description = "Do weird stuff with entity rendering")
public class Chams extends Module {

    public static Chams instance;
    public final ResourceLocation loadedTexturePackGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public final ResourceLocation gradientGlint = new ResourceLocation("moloch:textures/glints/gradient.png");
    public final ResourceLocation lightningGlint = new ResourceLocation("moloch:textures/glints/lightning.png");
    public final ResourceLocation linesGlint = new ResourceLocation("moloch:textures/glints/lines.png");
    public final ResourceLocation swirlsGlint = new ResourceLocation("moloch:textures/glints/swirls.png");
    private final List<RepeatUnit> repeatUnits = new ArrayList<>();

    Setting<Page> page = setting("Page", Page.Players);

    public Setting<Boolean> ignoreInvisible = setting("IgnoreInvisible", false).des("Doesn't render chams for invisible entities").only(v -> page.getValue() == Page.Players || page.getValue() == Page.Mobs || page.getValue() == Page.Animals);
    Setting<Boolean> playerCrowdAlpha = setting("PlayerCrowdAlpha", true).des("Reduce alpha of player chams when close to you").whenAtMode(page, Page.Players);
    Setting<Float> playerCrowdAlphaRadius = setting("PlayerCrowdAlphaDist", 2.0f, 0.5f, 4.0f).des("Distance to start reducing alpha of player chams close to you").whenTrue(playerCrowdAlpha).whenAtMode(page, Page.Players);
    Setting<Float> playerCrowdEndAlpha = setting("PlayerCrowdEndAlpha", 0.3f, 0.0f, 1.0f).des("Percentage of alpha when player chams are close to you").whenTrue(playerCrowdAlpha).whenAtMode(page, Page.Players);
    public Setting<Boolean> players = setting("Players", true).des("Render player chams").whenAtMode(page, Page.Players);
    public Setting<Boolean> otherPlayers = setting("OtherPlayers", true).des("Render player chams for other players").whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> self = setting("Self", true).des("Render self chams").whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> fixPlayerOutlineESP = setting("PlayerFixOutlineESP", false).des("Renders the most basic player chams so outline ESP doesn't break").whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerWall = setting("PlayerWalls", true).des("Render players through wall").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerCancelVanillaRender = setting("PlayerNoVanillaRender", true).des("Cancels normal minecraft player rendering").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerTexture = setting("PlayerTexture", true).des("Render player texture on chams").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> selfTexture = setting("SelfTexture", false).des("Render self texture on chams").whenTrue(self).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerDepthMask = setting("PlayerDepthMask", true).des("Enable depth mask for players (stops entity layers and tile entities from rendering through chams)").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerLighting = setting("PlayerLighting", false).des("Render player chams with lighting").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerCull = setting("PlayerCull", true).des("Don't render sides of player chams that you can't see").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerNoHurt = setting("PlayerNoHurt", true).des("Don't render hurt effect when player is damaged").whenTrue(players).whenAtMode(page, Page.Players);
    Setting<Boolean> playerBlend = setting("PlayerBlend", false).des("Use additive blending on player chams").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerChangeCapeAlpha = setting("PChangeCapeAlpha", false).des("Change cape alpha of players").whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> selfChangeCapeAlpha = setting("SelfChangeCapeAlpha", false).des("Change your own cape alpha").whenTrue(playerChangeCapeAlpha).whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Integer> playerCapeAlpha = setting("PlayerCapeAlpha", 129, 0, 255).des("Alpha of player capes").whenTrue(playerChangeCapeAlpha).whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerChangeArmorAlpha = setting("PChangeArmorAlpha", false).des("Change armor alpha of players").whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> selfChangeArmorAlpha = setting("SelfChangeArmorAlpha", true).des("Change your own armor alpha").whenTrue(playerChangeArmorAlpha).whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Integer> playerArmorAlpha = setting("PlayerArmorAlpha", 150, 0, 255).des("Alpha of player armor").whenTrue(playerChangeArmorAlpha).whenTrue(players).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerWallEffect = setting("PlayerWallEffect", false).des("Render different chams when player is blocked by a wall").whenTrue(players).whenTrue(playerWall).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerWallTexture = setting("PlayerWallTexture", false).des("Render texture on player chams behind walls").whenTrue(playerWallEffect).whenTrue(players).whenTrue(playerWall).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerWallBlend = setting("PlayerWallBlend", false).des("Use additive blending for player chams behind walls").whenTrue(playerWallEffect).whenTrue(players).whenTrue(playerWall).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerWallGlint = setting("PlayerWallGlint", false).des("Render glint texture on player chams behind walls").whenTrue(playerWallEffect).whenTrue(players).whenTrue(playerWall).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> playerWallColor = setting("PlayerWallColor", new Color(new java.awt.Color(255, 100, 100, 113).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 113)).des("Player chams color behind walls").whenTrue(playerWallEffect).whenTrue(playerWall).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> friendWallColor = setting("FriendWallColor", new Color(new java.awt.Color(50, 100, 255, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 100, 255, 100)).des("Friend chams color behind walls").whenTrue(playerWallEffect).whenTrue(playerWall).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> enemyWallColor = setting("EnemyWallColor", new Color(new java.awt.Color(255, 100, 50, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 50, 100)).des("Enemy chams color behind walls").whenTrue(playerWallEffect).whenTrue(playerWall).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerGlint = setting("PlayerGlint", false).des("Render glint texture on player chams").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> selfGlint = setting("SelfGlint", false).des("Render glint texture on yourself").whenTrue(self).whenTrue(playerGlint).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<GlintMode> playerGlintMode = setting("PlayerGlintMode", GlintMode.Swirls).des("Texture of player chams glint").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Boolean> playerGlintMove = setting("PlayerGlintMove", true).des("Player chams glint move").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Float> playerGlintMoveSpeed = setting("PlayerGlintMoveSpeed", 0.4f, 0.1f, 1.0f).des("Player chams glint move speed").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(playerGlint).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Float> playerGlintScale = setting("PlayerGlintScale", 4.0f, 0.1f, 4.0f).des("Size of player chams glint texture").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> playerGlintColor = setting("PlayerGlintColor", new Color(new java.awt.Color(125, 40, 255, 144).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 125, 40, 255, 144)).des("Player chams glint color").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> friendGlintColor = setting("FriendGlintColor", new Color(new java.awt.Color(50, 200, 255, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 200, 255, 100)).des("Friend chams glint color").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> enemyGlintColor = setting("EnemyGlintColor", new Color(new java.awt.Color(255, 50, 50, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 100)).des("Enemy chams glint color").only(v -> playerGlint.getValue() || playerWallGlint.getValue()).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> selfGlintColor = setting("SelfGlintColor", new Color(new java.awt.Color(125, 50, 255, 84).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 125, 50, 255, 84)).des("Self chams glint color").whenTrue(self).whenTrue(selfGlint).whenTrue(playerGlint).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerBypassArmor = setting("PlayerThroughArmor", true).des("Render player chams through armor").whenTrue(playerWall).whenFalse(playerWallEffect).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    public Setting<Boolean> playerBypassArmorWall = setting("PlayerThroughArmorWall", false).des("Render player chams through armor only through wall").whenTrue(playerWall).whenFalse(playerWallEffect).whenTrue(playerBypassArmor).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> playerColor = setting("PlayerColor", new Color(new java.awt.Color(255, 255, 255, 153).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 153)).des("Player chams color").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> friendColor = setting("FriendColor", new Color(new java.awt.Color(100, 200, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Friend chams color").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> enemyColor = setting("EnemyColor", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Enemy chams color").whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);
    Setting<Color> selfColor = setting("SelfColor", new Color(new java.awt.Color(255, 255, 255, 156).getRGB(), false, true, 3.4f, 0.51f, 0.9f, 255, 255, 255, 156)).des("Self chams color").whenTrue(self).whenTrue(players).whenFalse(fixPlayerOutlineESP).whenAtMode(page, Page.Players);

    public Setting<Boolean> mobs = setting("Mobs", false).des("Render mob chams").whenAtMode(page, Page.Mobs);
    public Setting<Boolean> fixMobOutlineESP = setting("MobFixOutlineESP", false).des("Renders the most basic mob chams so outline ESP doesn't break").whenTrue(mobs).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> mobWall = setting("MobWalls", true).des("Render mobs through wall").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobCancelVanillaRender = setting("MobNoVanillaRender", false).des("Cancels normal minecraft mob rendering").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobTexture = setting("MobTexture", false).des("Render mob texture on chams").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobDepthMask = setting("MobDepthMask", false).des("Enable depth mask for mobs (stops entity layers and tile entities from rendering through chams)").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobLighting = setting("MobLighting", false).des("Render mob chams with lighting").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobCull = setting("MobCull", true).des("Don't render sides of mob chams that you can't see").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> mobNoHurt = setting("MobNoHurt", true).des("Don't render hurt effect when mob is damaged").whenTrue(mobs).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobBlend = setting("MobBlend", false).des("Use additive blending on mob chams").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobCrowdAlpha = setting("MobCrowdAlpha", false).des("Reduce alpha of mob chams when close to you").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> mobWallEffect = setting("MobWallEffect", false).des("Render different chams when mob is blocked by a wall").whenTrue(mobs).whenTrue(mobWall).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobWallTexture = setting("MobWallTexture", false).des("Render texture on mob chams behind walls").whenTrue(mobWallEffect).whenTrue(mobs).whenTrue(mobWall).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobWallBlend = setting("MobWallBlend", false).des("Use additive blending for mob chams behind walls").whenTrue(mobWallEffect).whenTrue(mobs).whenTrue(mobWall).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobWallGlint = setting("MobWallGlint", false).des("Render glint texture on mob chams behind walls").whenTrue(mobWallEffect).whenTrue(mobs).whenTrue(mobWall).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Color> mobWallColor = setting("MobWallColor", new Color(new java.awt.Color(100, 100, 100, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 100, 100)).des("Mob chams color behind walls").whenTrue(mobWallEffect).whenTrue(mobWall).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Float> mobCrowdAlphaRadius = setting("MobCrowdAlphaDist", 1.0f, 0.5f, 4.0f).des("Distance to start reducing alpha of mob chams close to you").whenTrue(mobCrowdAlpha).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Float> mobCrowdEndAlpha = setting("MobCrowdEndAlpha", 0.5f, 0.0f, 1.0f).des("Percentage of alpha when mob chams are close to you").whenTrue(mobCrowdAlpha).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> mobChangeArmorAlpha = setting("MChangeArmorAlpha", false).des("Change armor alpha of mobs").whenTrue(mobs).whenAtMode(page, Page.Mobs);
    public Setting<Integer> mobArmorAlpha = setting("MobArmorAlpha", 150, 0, 255).des("Alpha of mob armor").whenTrue(mobChangeArmorAlpha).whenTrue(mobs).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobGlint = setting("MobGlint", false).des("Render glint texture on mob chams").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<GlintMode> mobGlintMode = setting("MobGlintMode", GlintMode.Gradient).des("Texture of mob chams glint").only(v -> mobGlint.getValue() || mobWallGlint.getValue()).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobGlintMove = setting("MobGlintMove", false).des("Mob chams glint move").only(v -> mobGlint.getValue() || mobWallGlint.getValue()).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Float> mobGlintMoveSpeed = setting("MobGlintMoveSpeed", 0.4f, 0.1f, 1.0f).des("Mob chams glint move speed").whenTrue(mobGlintMove).only(v -> mobGlint.getValue() || mobWallGlint.getValue()).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Float> mobGlintScale = setting("MobGlintScale", 1.0f, 0.1f, 4.0f).des("Size of mob chams glint texture").only(v -> mobGlint.getValue() || mobWallGlint.getValue()).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Color> mobGlintColor = setting("MobGlintColor", new Color(new java.awt.Color(125, 50, 255, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 125, 50, 255, 100)).des("Mob chams glint color").only(v -> mobGlint.getValue() || mobWallGlint.getValue()).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> mobBypassArmor = setting("MobThroughArmor", false).des("Render mob chams through armor").whenTrue(mobWall).whenFalse(mobWallEffect).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> mobBypassArmorWall = setting("MobThroughArmorWall", false).des("Render mob chams through armor only through wall").whenTrue(mobWall).whenFalse(mobWallEffect).whenTrue(mobBypassArmor).whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);
    Setting<Color> mobColor = setting("MobColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Mob chams color").whenTrue(mobs).whenFalse(fixMobOutlineESP).whenAtMode(page, Page.Mobs);

    public Setting<Boolean> animals = setting("Animals", false).des("Render animal chams").whenAtMode(page, Page.Animals);
    public Setting<Boolean> fixAnimalOutlineESP = setting("AnimalFixOutlineESP", false).des("Renders the most basic animal chams so outline ESP doesn't break").whenTrue(animals).whenAtMode(page, Page.Animals);
    public Setting<Boolean> animalWall = setting("AnimalWalls", true).des("Render animals through wall").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalCancelVanillaRender = setting("AnimalNoVanillaRender", false).des("Cancels normal minecraft animal rendering").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalTexture = setting("AnimalTexture", false).des("Render animal texture on chams").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalDepthMask = setting("AnimalDepthMask", false).des("Enable depth mask for animals (stops entity layers and tile entities from rendering through chams)").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalLighting = setting("AnimalLighting", false).des("Render animal chams with lighting").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalCull = setting("AnimalCull", true).des("Don't render sides of animal chams that you can't see").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    public Setting<Boolean> animalNoHurt = setting("AnimalNoHurt", true).des("Don't render hurt effect when animal is damaged").whenTrue(animals).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalBlend = setting("AnimalBlend", false).des("Use additive blending on animal chams").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    public Setting<Boolean> animalWallEffect = setting("AnimalWallEffect", false).des("Render different chams when animal is blocked by a wall").whenTrue(animals).whenTrue(animalWall).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalWallTexture = setting("AnimalWallTexture", false).des("Render texture on animal chams behind walls").whenTrue(animalWallEffect).whenTrue(animals).whenTrue(animalWall).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalWallBlend = setting("AnimalWallBlend", false).des("Use additive blending for animal chams behind walls").whenTrue(animalWallEffect).whenTrue(animals).whenTrue(animalWall).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalWallGlint = setting("AnimalWallGlint", false).des("Render glint texture on animal chams behind walls").whenTrue(animalWallEffect).whenTrue(animals).whenTrue(animalWall).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Color> animalWallColor = setting("AnimalWallColor", new Color(new java.awt.Color(100, 100, 100, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 100, 100)).des("Animal chams color behind walls").whenTrue(animalWallEffect).whenTrue(animalWall).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalCrowdAlpha = setting("AnimalCrowdAlpha", false).des("Reduce alpha of animal chams when close to you").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Float> animalCrowdAlphaRadius = setting("AnimalCrowdAlphaDist", 1.0f, 0.5f, 4.0f).des("Distance to start reducing alpha of animal chams close to you").whenTrue(animalCrowdAlpha).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Float> animalCrowdEndAlpha = setting("AnimalCrowdEndAlpha", 0.5f, 0.0f, 1.0f).des("Percentage of alpha when animal chams are close to you").whenTrue(animalCrowdAlpha).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalGlint = setting("AnimalGlint", false).des("Render glint texture on animal chams").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<GlintMode> animalGlintMode = setting("AnimalGlintMode", GlintMode.Gradient).des("Texture of animal chams glint").only(v -> animalGlint.getValue() || animalWallGlint.getValue()).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalGlintMove = setting("AnimalGlintMove", false).des("Animal chams glint move").only(v -> animalGlint.getValue() || animalWallGlint.getValue()).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Float> animalGlintMoveSpeed = setting("AnimalGlintMoveSpeed", 0.4f, 0.1f, 1.0f).des("Animal chams glint move speed").whenTrue(animalGlintMove).only(v -> animalGlint.getValue() || animalWallGlint.getValue()).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Float> animalGlintScale = setting("AnimalGlintScale", 1.0f, 0.1f, 4.0f).des("Size of animal chams glint texture").only(v -> animalGlint.getValue() || animalWallGlint.getValue()).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Color> animalGlintColor = setting("AnimalGlintColor", new Color(new java.awt.Color(125, 50, 255, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 125, 50, 255, 100)).des("Animal chams glint color").only(v -> animalGlint.getValue() || animalWallGlint.getValue()).whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);
    Setting<Color> animalColor = setting("AnimalColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Animal chams color").whenTrue(animals).whenFalse(fixAnimalOutlineESP).whenAtMode(page, Page.Animals);

    public Setting<Boolean> crystals = setting("Crystals", false).des("Render crystal chams").whenAtMode(page, Page.Crystals);
    public Setting<Boolean> fixCrystalOutlineESP = setting("CrystalFixOutlineESP", false).des("Renders the most basic crystal chams so outline ESP doesn't break").whenTrue(crystals).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalWall = setting("CrystalWalls", true).des("Render crystals through wall").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalCancelVanillaRender = setting("CrystalNoVanillaRender", false).des("Cancels normal minecraft crystal rendering").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalTexture = setting("CrystalTexture", false).des("Render crystal texture on chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalDepthMask = setting("CrystalDepthMask", false).des("Enable depth mask for crystals (stops entity layers and tile entities from rendering through chams)").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalLighting = setting("CrystalLighting", false).des("Render crystal chams with lighting").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalCull = setting("CrystalCull", true).des("Don't render sides of crystal chams that you can't see").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalBlend = setting("CrystalBlend", false).des("Use additive blending on crystal chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalWallEffect = setting("CrystalWallEffect", false).des("Render different chams when crystal is blocked by a wall").whenTrue(crystals).whenTrue(crystalWall).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalWallTexture = setting("CrystalWallTexture", false).des("Render texture on crystal chams behind walls").whenTrue(crystalWallEffect).whenTrue(crystals).whenTrue(crystalWall).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalWallBlend = setting("CrystalWallBlend", false).des("Use additive blending for crystal chams behind walls").whenTrue(crystalWallEffect).whenTrue(crystals).whenTrue(crystalWall).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalWallGlint = setting("CrystalWallGlint", false).des("Render glint texture on crystal chams behind walls").whenTrue(crystalWallEffect).whenTrue(crystals).whenTrue(crystalWall).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Color> crystalWallColor = setting("CrystalWallColor", new Color(new java.awt.Color(100, 100, 100, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 100, 100)).des("Crystal chams color behind walls").whenTrue(crystalWallEffect).whenTrue(crystalWall).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalCrowdAlpha = setting("CrystalCrowdAlpha", false).des("Reduce alpha of crystal chams when close to you").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalCrowdAlphaRadius = setting("CrystalCrowdAlphaDist", 1.0f, 0.5f, 4.0f).des("Distance to start reducing alpha of crystal chams close to you").whenTrue(crystalCrowdAlpha).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalCrowdEndAlpha = setting("CrystalCrowdEndAlpha", 0.5f, 0.0f, 1.0f).des("Percentage of alpha when crystal chams are close to you").whenTrue(crystalCrowdAlpha).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalGlint = setting("CrystalGlint", false).des("Render glint texture on crystal chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<GlintMode> crystalGlintMode = setting("CrystalGlintMode", GlintMode.Gradient).des("Texture of crystal chams glint").only(v -> crystalGlint.getValue() || crystalWallGlint.getValue()).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalGlintMove = setting("CrystalGlintMove", false).des("Crystal chams glint move").only(v -> crystalGlint.getValue() || crystalWallGlint.getValue()).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalGlintMoveSpeed = setting("CrystalGlintMoveSpeed", 0.4f, 0.1f, 1.0f).des("Crystal chams glint move speed").whenTrue(crystalGlintMove).only(v -> crystalGlint.getValue() || crystalWallGlint.getValue()).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalGlintScale = setting("CrystalGlintScale", 1.0f, 0.1f, 4.0f).des("Size of crystal chams glint texture").only(v -> crystalGlint.getValue() || crystalWallGlint.getValue()).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Color> crystalGlintColor = setting("CrystalGlintColor", new Color(new java.awt.Color(125, 50, 255, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 125, 50, 255, 100)).des("Crystal chams glint color").only(v -> crystalGlint.getValue() || crystalWallGlint.getValue()).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalOneGlass = setting("CrystalOneGlass", false).des("Only render one glass cube around crystal").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalYOffset = setting("CrystalYOffset", 0.0f, 0.0f, 5.0f).des("Y offset of crystal chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalBobModify = setting("CrystalBobModify", false).des("Modify bob height of crystal chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalBob = setting("CrystalBob", 1.0f, 0.0f, 2.0f).des("Bob height of crystal chams").whenTrue(crystalBobModify).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalSpinModify = setting("CrystalSpinModify", false).des("Modify spin speed of crystal chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalSpinSpeed = setting("CrystalSpinSpeed", 1.0f, 0.0f, 4.0f).des("Speed of crystal chams spin").whenTrue(crystalSpinModify).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalScaleModify = setting("CrystalScaleModify", false).des("Modify size of crystal chams").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Float> crystalScale = setting("CrystalScale", 1.0f, 0.0f, 2.0f).des("Size of crystal chams").whenTrue(crystalScaleModify).whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    public Setting<Color> crystalColor = setting("CrystalColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Crystal chams color").whenTrue(crystals).whenFalse(fixCrystalOutlineESP).whenAtMode(page, Page.Crystals);
    //see MixinRenderEnderCrystal && MixinModelEnderCrystal (for CrystalOneGlass)

    public Setting<Boolean> items = setting("Items", false).des("Render item chams").whenAtMode(page, Page.Items);
    public Setting<Boolean> itemsRangeLimit = setting("ItemsRangeLimit", false).des("Item chams limit range").whenTrue(items).whenAtMode(page, Page.Items);
    public Setting<Float> itemsRange = setting("ItemsRange", 30.0f, 10.0f, 64.0f).des("Item chams range").whenTrue(itemsRangeLimit).whenTrue(items).whenAtMode(page, Page.Items);
    public Setting<Boolean> itemTexture = setting("ItemTexture", false).des("Render item texture on chams").whenTrue(items).whenAtMode(page, Page.Items);
    public Setting<Boolean> itemLighting = setting("ItemLighting", false).des("Render item chams with lighting").whenTrue(items).whenAtMode(page, Page.Items);
    public Setting<Boolean> itemBlend = setting("ItemBlend", false).des("Use additive blending on item chams").whenTrue(items).whenAtMode(page, Page.Items);
    public Setting<Color> itemColor = setting("ItemColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Item chams color").whenTrue(items).whenAtMode(page, Page.Items);
    //see MixinRenderEntityItem


    public Chams() {
        instance = this;
    }

    RepeatUnit noHurt = new RepeatUnit(() -> 1, () -> {
        if (mc.world == null) return;
        EntityUtil.entitiesListFlag = true;
        for (Entity entity : EntityUtil.entitiesList()) {
            if ((entity instanceof EntityPlayer && playerNoHurt.getValue() && players.getValue()) || ((EntityUtil.isEntityMob(entity)) && mobNoHurt.getValue() && mobs.getValue()) || ((EntityUtil.isEntityAnimal(entity)) && animalNoHurt.getValue() && animals.getValue()))
                ((EntityLivingBase) entity).hurtTime = 0;
        }
        EntityUtil.entitiesListFlag = false;
    });

    @Override
    public void onRenderTick() {
        if (!(playerNoHurt.getValue() && players.getValue() || mobNoHurt.getValue() && mobs.getValue() || animalNoHurt.getValue() && animals.getValue()))
            repeatUnits.forEach(RepeatUnit::suspend);
        else
            repeatUnits.forEach(RepeatUnit::resume);
    }

    @Override
    public void onEnable() {
        repeatUnits.forEach(RepeatUnit::resume);
        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        repeatUnits.forEach(RepeatUnit::suspend);
        moduleDisableFlag = true;
    }

    @Listener
    public void renderEntity(RenderEntityEvent event) {
        if (RenderHelper.isInViewFrustrum(event.entityIn) && (!ignoreInvisible.getValue() || !event.entityIn.isInvisible())) {

            if (event.entityIn instanceof EntityPlayer && players.getValue()) {

                if (event.entityIn == mc.player && !self.getValue()) return;
                if (event.entityIn != mc.player && !otherPlayers.getValue()) {
                    if (playerCrowdAlpha.getValue()) {
                        float alphaCrowdFactor = 1.0f;
                        if ((EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, event.entityIn) <= playerCrowdAlphaRadius.getValue()))
                            alphaCrowdFactor = playerCrowdEndAlpha.getValue() + ((1.0f - playerCrowdEndAlpha.getValue()) * (float)(EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, event.entityIn) / playerCrowdAlphaRadius.getValue()));

                        GL11.glColor4f(1, 1, 1, alphaCrowdFactor);
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        GlStateManager.alphaFunc(516, 0.003921569F);
                    }
                    return;
                }

                if (playerGlint.getValue()) {
                    Color glintColor;

                    if (event.entityIn == mc.player) {
                        glintColor = selfGlintColor.getValue();
                    } else {
                            if (FriendManager.isFriend(event.entityIn)) glintColor = friendGlintColor.getValue();
                            else if (EnemyManager.isEnemy(event.entityIn)) glintColor = enemyGlintColor.getValue();
                            else glintColor = playerGlintColor.getValue();
                    }

                    renderChams(event, players, playerTexture, playerLighting, playerWall, playerCull, playerBlend, playerCrowdAlpha, playerCrowdAlphaRadius, playerCrowdEndAlpha, fixPlayerOutlineESP, playerWallEffect, playerWallTexture, playerWallBlend, playerWallGlint, playerBypassArmor.getValue(), playerBypassArmorWall.getValue(), playerDepthMask.getValue(), playerGlint.getValue(), playerGlintMode.getValue(), playerGlintMove.getValue(), playerGlintMoveSpeed.getValue(), playerGlintScale.getValue(), glintColor);
                }

                else renderChams(event, players, playerTexture, playerLighting, playerWall, playerCull, playerBlend, playerCrowdAlpha, playerCrowdAlphaRadius, playerCrowdEndAlpha, fixPlayerOutlineESP, playerWallEffect, playerWallTexture, playerWallBlend, playerWallGlint, playerBypassArmor.getValue(), playerBypassArmorWall.getValue(), playerDepthMask.getValue(), playerGlint.getValue(), playerGlintMode.getValue(), playerGlintMove.getValue(), playerGlintMoveSpeed.getValue(), playerGlintScale.getValue(), new Color(0, false, false, 1.0f, 0.75f, 0.9f, 0, 0, 0, 0));
            }

            if ((EntityUtil.isEntityMob(event.entityIn)) && mobs.getValue())
                renderChams(event, mobs, mobTexture, mobLighting, mobWall, mobCull, mobBlend, mobCrowdAlpha, mobCrowdAlphaRadius, mobCrowdEndAlpha, fixMobOutlineESP, mobWallEffect, mobWallTexture, mobWallBlend, mobWallGlint, mobBypassArmor.getValue(), mobBypassArmorWall.getValue(), mobDepthMask.getValue(), mobGlint.getValue(), mobGlintMode.getValue(), mobGlintMove.getValue(), mobGlintMoveSpeed.getValue(), mobGlintScale.getValue(), mobGlintColor.getValue());
            if ((EntityUtil.isEntityAnimal(event.entityIn)) && animals.getValue())
                renderChams(event, animals, animalTexture, animalLighting, animalWall, animalCull, animalBlend, animalCrowdAlpha, animalCrowdAlphaRadius, animalCrowdEndAlpha, fixAnimalOutlineESP, animalWallEffect, animalWallTexture, animalWallBlend, animalWallGlint, true, false, animalDepthMask.getValue(), animalGlint.getValue(), animalGlintMode.getValue(), animalGlintMove.getValue(), animalGlintMoveSpeed.getValue(), animalGlintScale.getValue(), animalGlintColor.getValue());
        }
    }

    @Listener
    public void cancelEntity(RenderEntityInvokeEvent event) {
        if (RenderHelper.isInViewFrustrum(event.entityIn)) {
            if (event.entityIn instanceof EntityPlayer && players.getValue()) {

                if (event.entityIn == mc.player && !self.getValue()) return;
                if (event.entityIn != mc.player && !otherPlayers.getValue()) return;

                chamsCancelRender(event, playerCancelVanillaRender, fixPlayerOutlineESP);
            }
            if ((EntityUtil.isEntityMob(event.entityIn)) && mobs.getValue())
                chamsCancelRender(event, mobCancelVanillaRender, fixMobOutlineESP);
            if ((EntityUtil.isEntityAnimal(event.entityIn)) && animals.getValue())
                chamsCancelRender(event, animalCancelVanillaRender, fixAnimalOutlineESP);
        }
    }


    private void renderChams(RenderEntityEvent event, Setting<Boolean> settingTarget, Setting<Boolean> settingTexture, Setting<Boolean> settingLighting, Setting<Boolean> settingWalls, Setting<Boolean> settingCull, Setting<Boolean> settingBlend, Setting<Boolean> settingCrowdAlpha, Setting<Float> settingCrowdAlphaStartRadius, Setting<Float> settingCrowdEndAlpha, Setting<Boolean> settingFixOutlineEsp, Setting<Boolean> settingWallTarget, Setting<Boolean> settingWallTexture, Setting<Boolean> settingWallBlend, Setting<Boolean> settingWallGlint, boolean throughArmor, boolean throughArmorWall, boolean depthMask, boolean glint, GlintMode glintMode, boolean glintMove, float glintMoveSpeed, float glintScale, Color glintColor) {
        if (settingFixOutlineEsp.getValue()) return;

        float alphaCrowdFactor = 1.0f;
        if (settingCrowdAlpha.getValue() && event.entityIn != mc.player && (EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, event.entityIn) <= settingCrowdAlphaStartRadius.getValue()))
            alphaCrowdFactor = settingCrowdEndAlpha.getValue() + ((1.0f - settingCrowdEndAlpha.getValue()) * (float)(EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, event.entityIn) / settingCrowdAlphaStartRadius.getValue()));

        java.awt.Color color;
        if (settingTarget == players) {
            if (event.entityIn == mc.player) color = selfColor.getValue().getColorColor();
            else {
                if (FriendManager.isFriend(event.entityIn)) color = friendColor.getValue().getColorColor();
                else if (EnemyManager.isEnemy(event.entityIn)) color = enemyColor.getValue().getColorColor();
                else color = playerColor.getValue().getColorColor();
            }
        }
        else if (settingTarget == mobs) color = mobColor.getValue().getColorColor();
        else if (settingTarget == animals) color = animalColor.getValue().getColorColor();
        else color = itemColor.getValue().getColorColor();


        int alpha;
        if (settingTarget == players) {
            if (event.entityIn == mc.player) alpha = selfColor.getValue().getAlpha();
            else {
                if (FriendManager.isFriend(event.entityIn)) alpha = friendColor.getValue().getAlpha();
                else if (EnemyManager.isEnemy(event.entityIn)) alpha = enemyColor.getValue().getAlpha();
                else alpha = playerColor.getValue().getAlpha();
            }
        }
        else if (settingTarget == mobs) alpha = mobColor.getValue().getAlpha();
        else if (settingTarget == animals) alpha = animalColor.getValue().getAlpha();
        else alpha = itemColor.getValue().getAlpha();

        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glEnable(GL_BLEND);

        if (settingBlend.getValue()) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
        else GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (settingCull.getValue()) GlStateManager.enableCull();
        else GlStateManager.disableCull();

        GlStateManager.disableAlpha();
        GlStateManager.depthMask(depthMask);

        if (settingWalls.getValue() && throughArmor && !settingWallTarget.getValue()) {
            GL11.glDepthRange(0.0, 0.01);

            if (throughArmorWall)
                GlStateManager.depthMask(false);
        }

        if (settingLighting.getValue()) GL11.glEnable(GL_LIGHTING);
        else GL11.glDisable(GL_LIGHTING);
        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        if (settingTexture.getValue()) GL11.glEnable(GL_TEXTURE_2D);
        else GL11.glDisable(GL_TEXTURE_2D);

        if (event.entityIn == mc.player) {
            if (selfTexture.getValue()) GL11.glEnable(GL_TEXTURE_2D);
            else GL11.glDisable(GL_TEXTURE_2D);
        }

        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, (alpha / 255.0f) * alphaCrowdFactor);
        event.modelBase.render(event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);


        if (settingWalls.getValue() && throughArmor && throughArmorWall && !settingWallTarget.getValue()) {
            GlStateManager.depthMask(true);
            GL11.glDepthRange(0.0, 1.0);
            event.modelBase.render(event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        }

        if (settingWallTarget.getValue() && settingWalls.getValue())
            renderWallEffect(event, alphaCrowdFactor, settingWallTarget, settingWallTexture, settingWallBlend, settingWallGlint, glintMode, glintMove, glintMoveSpeed, glintScale);

        if (glint) {
            if (settingWallTarget.getValue())
                GlStateManager.depthMask(true);
            renderGlint(event, settingWalls.getValue(), alphaCrowdFactor, throughArmor, throughArmorWall, glintMode, glintMove, glintMoveSpeed, glintScale, glintColor, settingWallTarget);
        }

        if (settingWalls.getValue() && throughArmor && !settingWallTarget.getValue()) GL11.glDepthRange(0.0, 1.0);

        if (settingBlend.getValue()) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        SpartanTessellator.releaseGL();
        GL11.glEnable(GL_BLEND);
        GL11.glEnable(GL_LIGHTING);

        if (settingWallTarget.getValue() && settingWalls.getValue()) {
            GL11.glDepthFunc(GL_LEQUAL);
        }
        GL11.glEnable(GL_TEXTURE_2D);
    }

    private void renderWallEffect(RenderEntityEvent event, float alphaCrowdFactor, Setting<Boolean> settingWallTarget, Setting<Boolean> settingWallTexture, Setting<Boolean> settingWallBlend, Setting<Boolean> settingWallGlint, GlintMode glintMode, boolean glintMove, float glintMoveSpeed, float glintScale) {
        java.awt.Color color;
        if (settingWallTarget == playerWallEffect) {
            if (FriendManager.isFriend(event.entityIn)) color = friendWallColor.getValue().getColorColor();
            else if (EnemyManager.isEnemy(event.entityIn)) color = enemyWallColor.getValue().getColorColor();
            else color = playerWallColor.getValue().getColorColor();
        }
        else if (settingWallTarget == mobWallEffect) color = mobWallColor.getValue().getColorColor();
        else if (settingWallTarget == animalWallEffect) color = animalWallColor.getValue().getColorColor();
        else color = itemColor.getValue().getColorColor();

        Color color2;
        if (settingWallTarget == playerWallEffect) {
            if (FriendManager.isFriend(event.entityIn)) color2 = friendWallColor.getValue();
            else if (EnemyManager.isEnemy(event.entityIn)) color2 = enemyWallColor.getValue();
            else color2 = playerWallColor.getValue();
        }
        else if (settingWallTarget == mobWallEffect) color2 = mobWallColor.getValue();
        else if (settingWallTarget == animalWallEffect) color2 = animalWallColor.getValue();
        else color2 = itemColor.getValue();


        int alpha;
        if (settingWallTarget == playerWallEffect) {
            if (FriendManager.isFriend(event.entityIn)) alpha = friendWallColor.getValue().getAlpha();
            else if (EnemyManager.isEnemy(event.entityIn)) alpha = enemyWallColor.getValue().getAlpha();
            else alpha = playerWallColor.getValue().getAlpha();
        }
        else if (settingWallTarget == mobWallEffect) alpha = mobWallColor.getValue().getAlpha();
        else if (settingWallTarget == animalWallEffect) alpha = animalWallColor.getValue().getAlpha();
        else alpha = itemColor.getValue().getAlpha();

        GlStateManager.depthMask(false);

        if (settingWallTexture.getValue()) GL11.glEnable(GL_TEXTURE_2D);
        else GL11.glDisable(GL_TEXTURE_2D);

        if (settingWallBlend.getValue()) GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
        else GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthFunc(GL_GREATER);

        if (settingWallGlint.getValue())
            renderGlint(event, false, alphaCrowdFactor, false, false, glintMode, glintMove, glintMoveSpeed, glintScale, color2, settingWallTarget);
        else {
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, (alpha / 255.0f) * alphaCrowdFactor);
            event.modelBase.render(event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        }

        GL11.glDepthFunc(GL_EQUAL);
    }

    private void renderGlint(RenderEntityEvent event, boolean walls, float alphaCrowdFactor, boolean throughArmor, boolean throughArmorWall, GlintMode glintMode, boolean glintMove, float glintMoveSpeed, float glintScale, Color glintColor, Setting<Boolean> settingWallTarget) {
        if (event.entityIn == mc.player && self.getValue() && !selfGlint.getValue()) return;

        ResourceLocation glintTexture = null;

        switch (glintMode) {
            case LoadedPack: {
                glintTexture = loadedTexturePackGlint;
                break;
            }

            case Gradient: {
                glintTexture = gradientGlint;
                break;
            }

            case Lightning: {
                glintTexture = lightningGlint;
                break;
            }

            case Swirls: {
                glintTexture = swirlsGlint;
                break;
            }

            case Lines: {
                glintTexture = linesGlint;
                break;
            }
        }

        if (glintTexture != null) mc.getTextureManager().bindTexture(glintTexture);
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glDisable(GL_LIGHTING);
        GL11.glEnable(GL_BLEND);

        //alpha seems to be broken somehow so ig this would work :shrug:
        float alpha = (glintColor.getAlpha() / 255.0f) * alphaCrowdFactor;
        GL11.glColor4f((glintColor.getColorColor().getRed() / 255.0f) * alpha, (glintColor.getColorColor().getGreen() / 255.0f) * alpha, (glintColor.getColorColor().getBlue() / 255.0f) * alpha, 1.0f);

        GL11.glBlendFunc(GL_SRC_COLOR, GL_ONE);

        if (walls && throughArmor && throughArmorWall && !settingWallTarget.getValue()) {
            GL11.glDepthRange(0.0, 0.01);
            GlStateManager.depthMask(false);
        }

        doRenderGlint(event, glintMove, glintMoveSpeed, glintScale);

        if (walls && throughArmor && throughArmorWall && !settingWallTarget.getValue()) {
            GlStateManager.depthMask(true);
            GL11.glDepthRange(0.0, 1.0);

            doRenderGlint(event, glintMove, glintMoveSpeed, glintScale);
        }

        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void doRenderGlint(RenderEntityEvent event, boolean glintMove, float glintMoveSpeed, float glintScale) {
        for (int i = 0; i < 2; ++i) {
            GL11.glMatrixMode(GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glScalef(glintScale, glintScale, glintScale);
            if (glintMove) {
                GL11.glTranslatef(event.entityIn.ticksExisted * 0.01f * glintMoveSpeed, 0.0f, 0.0f);
            }
            GL11.glRotatef(30.0f - (i * 60.0f), 0.0f, 0.0f, 1.0f);
            GL11.glMatrixMode(GL_MODELVIEW);

            event.modelBase.render(event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        }
        GL11.glMatrixMode(GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL_MODELVIEW);
    }

    private void chamsCancelRender(RenderEntityInvokeEvent event, Setting<Boolean> settingCancel, Setting<Boolean> settingFixOutlineEsp) {
        if (settingFixOutlineEsp.getValue()) return;
        if (settingCancel.getValue()) event.cancel();
    }

    enum Page {
        Players,
        Mobs,
        Animals,
        Crystals,
        Items
    }

    public enum GlintMode {
        LoadedPack,
        Gradient,
        Lightning,
        Swirls,
        Lines
    }
}
