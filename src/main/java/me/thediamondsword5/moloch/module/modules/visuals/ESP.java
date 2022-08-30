package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.module.modules.other.Freecam;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.spartanb312.base.core.concurrent.repeat.RepeatUnit;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.*;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.graphics.RenderHelper;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.event.events.render.RenderEntityInvokeEvent;
import me.thediamondsword5.moloch.event.events.render.RenderEntityLayersEvent;
import me.thediamondsword5.moloch.event.events.render.RenderWorldPostEventCenter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
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
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.utils.EntityUtil;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runRepeat;
import static net.spartanb312.base.utils.EntityUtil.*;
import static me.thediamondsword5.moloch.module.modules.visuals.ESP.Mode.*;
import static org.lwjgl.opengl.GL11.*;
//TODO: item spawn item fade in, add distance color
@Parallel(runnable = true)
@ModuleInfo(name = "ESP", category = Category.VISUALS, description = "Highlights entities")
public class ESP extends Module {

    public static ESP INSTANCE;
    public boolean renderOutlineFlag = false;
    private final List<RepeatUnit> repeatUnits = new ArrayList<>();
    public boolean renderProjectileFlag = false;
    public final HashMap<EntityPlayer, Float> skeletonFadeData = new HashMap<>();
    public final HashMap<EntityPlayer, float[][]> skeletonData = new HashMap<>();

    Setting<Page> page = setting("Page", Page.General);

    public Setting<Boolean> ignoreInvisible = setting("IgnoreInvisible", false).des("Doesn't render ESP for invisible entities").whenAtMode(page, Page.General);
    public Setting<Boolean> espCull = setting("CullFaces", false).des("Stop rendering sides not visible").whenAtMode(page, Page.General);
    public Setting<Boolean> espRangeLimit = setting("LimitRange", false).des("ESP range limit").whenAtMode(page, Page.General);
    public Setting<Float> espRange = setting("Range", 30.0f, 10.0f, 64.0f).des("ESP range").whenTrue(espRangeLimit).whenAtMode(page, Page.General);
    public Setting<Float> espGlowWidth = setting("GlowWidth", 1.0f, 0.1f, 10.0f).des("!! TURN OFF FAST RENDER IN OPTIFINE !!  Glow ESP width").whenAtMode(page, Page.General);
    public Setting<Float> espOutlineWidth = setting("OutlineWidth", 1.5f, 1.0f, 5.0f).des("Non shader outline ESP width").whenAtMode(page, Page.General);
    public Setting<Boolean> espShaderOutlineFastMode = setting("ShaderOutlineFast", false).des("Shader outline faster render one color").whenAtMode(page, Page.General);
    public Setting<Float> espShaderOutlineFastModeWidth = setting("ShaderOutlineFastWidth", 1.5f, 1.0f, 5.0f).des("Shader outline faster render width").whenTrue(espShaderOutlineFastMode).whenAtMode(page, Page.General);
    public Setting<Color> espShaderOutlineFastModeColor = setting("ShaderOutlineFastColor", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, true, 3.4f, 0.50f, 0.9f, 255, 100, 100, 255)).des("Shader outline faster render color").whenTrue(espShaderOutlineFastMode).whenAtMode(page, Page.General);

    public Setting<Boolean> espTargetSelf = setting("Self", false).des("ESP target self").whenAtMode(page, Page.Self);
    public Setting<ModeSelf> espModeSelf = setting("ModeSelf", ModeSelf.Outline).des("Mode of ESP for self (Turn off Fast Render In Optifine For Glow)").whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Float> espSelfWidth = setting("SelfOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP self line width").only(v -> espModeSelf.getValue() == ModeSelf.Wireframe).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Boolean> selfCancelVanillaRender = setting("SelfNoVanillaRender", false).des("Cancels normal minecraft self player rendering").whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    public Setting<Boolean> espWireframeWallEffectSelf = setting("SelfWireframeWallEffect", false).des("ESP wireframe wall effects for self ESP").whenTrue(espTargetSelf).whenAtMode(espModeSelf, ModeSelf.Wireframe).whenAtMode(page, Page.Self);
    public Setting<Boolean> espWireframeOnlyWallSelf = setting("SelfWireframeOnlyWall", false).des("ESP wireframe only wall render for self ESP").whenTrue(espWireframeWallEffectSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Boolean> espSkeletonSelf = setting("SelfSkeleton", false).des("Skelly rendering on yourself in 3rd person").whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Float> espSkeletonSelfWidth = setting("SelfSkeletonWidth", 1.0f, 1.0f, 5.0f).des("Self skelly line width").whenTrue(espSkeletonSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Boolean> espSkeletonGradientSelfEnds = setting("SelfSkeletonFadeLimbs", false).des("Make the limbs and head lines of self skelly fade out in a gradient").whenTrue(espSkeletonSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Boolean> espSkeletonRollingSelfColor = setting("SSkeletonRollColor", false).des("Roll colors on player skelly from up to down").whenTrue(espSkeletonSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Color> espSkeletonRollingSelfColor1 = setting("SSkeletonRollColor1", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).whenTrue(espSkeletonRollingSelfColor).whenTrue(espSkeletonSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Color> espSkeletonRollingSelfColor2 = setting("SSkeletonRollColor2", new Color(new java.awt.Color(50, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 50, 50, 255)).whenTrue(espSkeletonRollingSelfColor).whenTrue(espSkeletonSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    public Setting<Color> espColorSelf = setting("SelfColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Self ESP color").whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Color> espWireframeWallColorSelf = setting("SWireframeWallColor", new Color(new java.awt.Color(255, 255, 255, 125).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 125)).des("Self wall only ESP Wireframe color").whenTrue(espWireframeWallEffectSelf).only(v -> espModeSelf.getValue() == ModeSelf.Wireframe).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);
    Setting<Color> espColorSkeletonSelf = setting("SelfSkeletonColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Players skeleton color").whenTrue(espSkeletonSelf).whenTrue(espTargetSelf).whenAtMode(page, Page.Self);

    public Setting<Boolean> espTargetPlayers = setting("Players", false).des("ESP target players").whenAtMode(page, Page.Players);
    public Setting<Mode> espModePlayers = setting("ModePlayers", Outline).des("Mode of ESP for players (Turn off Fast Render In Optifine For Glow)").whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Float> espPlayerWidth = setting("PlayerOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP player line width").only(v -> espModePlayers.getValue() == Wireframe || espModePlayers.getValue() == Shader).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Boolean> playersCancelVanillaRender = setting("PNoVanillaRender", false).des("Cancels normal minecraft player rendering").whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Boolean> espWireframeWallEffectPlayer = setting("PlayerWireframeWallEffect", false).des("ESP wireframe wall effects for player ESP").whenTrue(espTargetPlayers).whenAtMode(espModePlayers, Wireframe).whenAtMode(page, Page.Players);
    public Setting<Boolean> espWireframeOnlyWallPlayer = setting("PlayerWireframeOnlyWall", false).des("ESP wireframe only wall render for player ESP").whenTrue(espWireframeWallEffectPlayer).whenTrue(espTargetPlayers).whenAtMode(espModePlayers, Wireframe).whenAtMode(page, Page.Players);
    Setting<Boolean> playerNoHurt = setting("PlayerNoHurt", true).des("Don't render hurt effect when player is damaged").whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Boolean> espSkeletonPlayers = setting("PlayersSkeleton", false).des("Skelly rendering in players").whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Float> espSkeletonPlayersWidth = setting("PlayersSkeletonWidth", 1.0f, 1.0f, 5.0f).des("Players skelly line width").whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Boolean> espSkeletonGradientPlayerEnds = setting("PlayersSkeletonFadeLimbs", false).des("Make the limbs and head lines of player skelly fade out in a gradient").whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Boolean> espSkeletonDeathFade = setting("PlayersSkeletonDeathFade", false).des("Make skellys fade out when player dies").whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Float> espSkeletonDeathFadeFactor = setting("PSkeletonDeathFadeSpeed", 2.0f, 1.0f, 5.0f).des("Skelly death fade time").whenTrue(espSkeletonDeathFade).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Boolean> espSkeletonRollingPlayerColor = setting("PSkeletonRollColor", false).des("Roll colors on player skelly from up to down").whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espSkeletonRollingPlayerColor1 = setting("PSkeletonRollColor1", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 255)).whenTrue(espSkeletonRollingPlayerColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espSkeletonRollingPlayerColor2 = setting("PSkeletonRollColor2", new Color(new java.awt.Color(100, 25, 25, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 25, 25, 255)).whenTrue(espSkeletonRollingPlayerColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Boolean> espSkeletonRollingFriendColor = setting("FSkeletonRollColor", false).des("Roll colors on friend skelly from up to down").whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espSkeletonRollingFriendColor1 = setting("FSkeletonRollColor1", new Color(new java.awt.Color(100, 200, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 200, 255, 255)).whenTrue(espSkeletonRollingFriendColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espSkeletonRollingFriendColor2 = setting("FSkeletonRollColor2", new Color(new java.awt.Color(25, 50, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 25, 50, 100, 255)).whenTrue(espSkeletonRollingFriendColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Boolean> espSkeletonRollingEnemyColor = setting("ESkeletonRollColor", false).des("Roll colors on enemy skelly from up to down").whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espSkeletonRollingEnemyColor1 = setting("ESkeletonRollColor1", new Color(new java.awt.Color(255, 0, 0, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 0, 0, 255)).whenTrue(espSkeletonRollingEnemyColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espSkeletonRollingEnemyColor2 = setting("ESkeletonRollColor2", new Color(new java.awt.Color(100, 0, 0, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 0, 0, 255)).whenTrue(espSkeletonRollingEnemyColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Float> espSkeletonRollingColorSpeed = setting("PSkeletonRollSpeed", 0.5f, 0.1f, 2.0f).des("Speed of skelly roll color").only(v -> espSkeletonRollingPlayerColor.getValue() || espSkeletonRollingFriendColor.getValue() || espSkeletonRollingEnemyColor.getValue()).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<BoxMode> espBoxModePlayers = setting("PlayersBoxMode", BoxMode.Both).des("Players ESP box render mode").only(v -> espModePlayers.getValue() == Box).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Float> espBoxLineWidthPlayers = setting("PlayersBoxLineWidth", 1.0f, 1.0f, 5.0f).des("Players ESP box line width").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Lines || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Color> espColorPlayers = setting("PlayerColor", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 255)).des("Player ESP color").only(v -> espModePlayers.getValue() != Box).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorSkeletonPlayer = setting("PlayerSkeletonColor", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 255)).des("Players skeleton color").whenFalse(espSkeletonRollingPlayerColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorPlayersSolid = setting("PlayerColorSolid", new Color(new java.awt.Color(255, 100, 100, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 120)).des("Players ESP box solid color").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Solid || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorPlayersLines = setting("PlayerColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Players ESP Box lines color").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Lines || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espWireframeWallColorPlayers = setting("PWireframeWallColor", new Color(new java.awt.Color(255, 175, 175, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 175, 175, 255)).des("Players wall only ESP Wireframe color").whenTrue(espWireframeWallEffectPlayer).only(v -> espModePlayers.getValue() == Wireframe).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Color> espColorPlayersFriend = setting("PlayerFriendColor", new Color(new java.awt.Color(100, 200, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 200, 255, 255)).des("Player friend ESP color").only(v -> espModePlayers.getValue() != Box).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorSkeletonFriend = setting("PFriendSkeletonColor", new Color(new java.awt.Color(100, 200, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 200, 255, 255)).des("Players friend skeleton color").whenFalse(espSkeletonRollingFriendColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorPlayersSolidFriend = setting("PFriendColorSolid", new Color(new java.awt.Color(100, 200, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 200, 255, 120)).des("Players friend ESP box solid color").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Solid || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorPlayersLinesFriend = setting("PFriendColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Players friend ESP Box lines color").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Lines || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espWireframeWallColorPlayersFriend = setting("PFWireframeWallColor", new Color(new java.awt.Color(150, 190, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 150, 190, 255, 255)).des("Players friend wall only ESP wireframe color").whenTrue(espWireframeWallEffectPlayer).only(v -> espModePlayers.getValue() == Wireframe).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    public Setting<Color> espColorPlayersEnemy = setting("PEnemyColor", new Color(new java.awt.Color(255, 0, 0, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 0, 0, 255)).des("Player enemy ESP color").only(v -> espModePlayers.getValue() != Box).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorSkeletonEnemy = setting("PEnemySkeletonColor", new Color(new java.awt.Color(255, 0, 0, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 0, 0, 255)).des("Players enemy skeleton color").whenFalse(espSkeletonRollingEnemyColor).whenTrue(espSkeletonPlayers).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorPlayersSolidEnemy = setting("PEnemyColorSolid", new Color(new java.awt.Color(255, 0, 0, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 0, 0, 120)).des("Players enemy ESP box solid color").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Solid || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espColorPlayersLinesEnemy = setting("PEnemyColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Players enemy ESP box lines color").only(v -> espModePlayers.getValue() == Box && (espBoxModePlayers.getValue() == BoxMode.Lines || espBoxModePlayers.getValue() == BoxMode.Both)).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);
    Setting<Color> espWireframeWallColorPlayersEnemy = setting("PEWireframeWallColor", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 255)).des("Players friend wall only ESP wireframe color").whenTrue(espWireframeWallEffectPlayer).only(v -> espModePlayers.getValue() == Wireframe).whenTrue(espTargetPlayers).whenAtMode(page, Page.Players);

    public Setting<Boolean> espTargetMobs = setting("Mobs", false).des("ESP target mobs").whenAtMode(page, Page.Mobs);
    public Setting<Mode> espModeMobs = setting("ModeMobs", Outline).des("Mode of ESP for mobs (Turn off Fast Render in Optifine for glow)").whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<Float> espMobWidth = setting("MobOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP mob line width").only(v -> espModeMobs.getValue() == Wireframe || espModeMobs.getValue() != Shader).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobsCancelVanillaRender = setting("MNoVanillaRender", false).des("Cancels normal minecraft mob rendering").whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> espWireframeWallEffectMob = setting("MobWireframeWallEffect", false).des("ESP wireframe wall effects for mob ESP").whenTrue(espTargetMobs).whenAtMode(espModeMobs, Wireframe).whenAtMode(page, Page.Mobs);
    public Setting<Boolean> espWireframeOnlyWallMob = setting("MobWireframeOnlyWall", false).des("ESP wireframe only wall render for mob ESP").whenTrue(espWireframeWallEffectMob).whenTrue(espTargetMobs).whenAtMode(espModeMobs, Wireframe).whenAtMode(page, Page.Mobs);
    Setting<Boolean> mobNoHurt = setting("MobNoHurt", true).des("Don't render hurt effect when mob is damaged").whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<BoxMode> espBoxModeMobs = setting("MobsBoxMode", BoxMode.Both).des("Mobs ESP box render mode").only(v -> espModeMobs.getValue() == Box).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<Float> espBoxLineWidthMobs = setting("MobsBoxLineWidth", 1.0f, 1.0f, 5.0f).des("Mobs ESP box line width").only(v -> espModeMobs.getValue() == Box && (espBoxModeMobs.getValue() == BoxMode.Lines || espBoxModeMobs.getValue() == BoxMode.Both)).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    public Setting<Color> espColorMobs = setting("MobColor", new Color(new java.awt.Color(255, 255, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 100, 255)).des("Mob ESP color").only(v -> espModeMobs.getValue() != Box).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<Color> espColorMobsSolid = setting("MobColorSolid", new Color(new java.awt.Color(255, 255, 100, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 100, 120)).des("Mobs ESP box solid color").only(v -> espModeMobs.getValue() == Box && (espBoxModeMobs.getValue() == BoxMode.Solid || espBoxModeMobs.getValue() == BoxMode.Both)).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<Color> espColorMobsLines = setting("MobColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Mobs ESP box lines color").only(v -> espModeMobs.getValue() == Box && (espBoxModeMobs.getValue() == BoxMode.Lines || espBoxModeMobs.getValue() == BoxMode.Both)).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);
    Setting<Color> espWireframeWallColorMobs = setting("MWireframeWallColor", new Color(new java.awt.Color(255, 255, 175, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 175, 255)).des("Mobs wall only ESP wireframe color").whenTrue(espWireframeWallEffectMob).only(v -> espModeMobs.getValue() == Wireframe).whenTrue(espTargetMobs).whenAtMode(page, Page.Mobs);

    public Setting<Boolean> espTargetAnimals = setting("Animals", false).des("ESP target animals").whenAtMode(page, Page.Animals);
    public Setting<Mode> espModeAnimals = setting("ModeAnimals", Outline).des("Mode of ESP for animals (Turn off Fast Render In Optifine For Glow)").whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    Setting<Float> espAnimalWidth = setting("AnimalOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP animal line width").only(v -> espModeAnimals.getValue() == Wireframe || espModeAnimals.getValue() == Shader).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalsCancelVanillaRender = setting("ANoVanillaRender", false).des("Cancels normal minecraft animal rendering").whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    public Setting<Boolean> espWireframeWallEffectAnimal = setting("AnimalWireframeWallEffect", false).des("ESP wireframe wall effects for animal ESP").whenTrue(espTargetAnimals).whenAtMode(espModeAnimals, Wireframe).whenAtMode(page, Page.Animals);
    public Setting<Boolean> espWireframeOnlyWallAnimal = setting("AnimalWireframeOnlyWall", false).des("ESP wireframe only wall render for animal ESP").whenTrue(espWireframeWallEffectAnimal).whenTrue(espTargetAnimals).whenAtMode(espModeAnimals, Wireframe).whenAtMode(page, Page.Animals);
    Setting<Boolean> animalNoHurt = setting("AnimalNoHurt", true).des("Don't render hurt effect when animal is damaged").whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    Setting<BoxMode> espBoxModeAnimals = setting("AnimalsBoxMode", BoxMode.Both).des("Animals ESP box render mode").only(v -> espModeAnimals.getValue() == Box).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    Setting<Float> espBoxLineWidthAnimals = setting("AnimalsBoxLineWidth", 1.0f, 1.0f, 5.0f).des("Animals ESP box line width").only(v -> espModeAnimals.getValue() == Box && (espBoxModeAnimals.getValue() == BoxMode.Lines || espBoxModeAnimals.getValue() == BoxMode.Both)).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    public Setting<Color> espColorAnimals = setting("AnimalColor", new Color(new java.awt.Color(100, 255, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 255, 100, 255)).des("Animal ESP color").only(v -> espModeAnimals.getValue() != Box).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    Setting<Color> espColorAnimalsSolid = setting("AnimalColorSolid", new Color(new java.awt.Color(100, 255, 100, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 255, 100, 120)).des("Animals ESP box solid color").only(v -> espModeAnimals.getValue() == Box && (espBoxModeAnimals.getValue() == BoxMode.Solid || espBoxModeAnimals.getValue() == BoxMode.Both)).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    Setting<Color> espColorAnimalsLines = setting("AnimalColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Animals ESP box lines color").only(v -> espModeAnimals.getValue() == Box && (espBoxModeAnimals.getValue() == BoxMode.Lines || espBoxModeAnimals.getValue() == BoxMode.Both)).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);
    public Setting<Color> espWireframeWallColorAnimals = setting("AWireframeWallColor", new Color(new java.awt.Color(175, 255, 175, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 175, 255, 175, 255)).des("Animals wall only ESP wireframe color").whenTrue(espWireframeWallEffectAnimal).only(v -> espModeAnimals.getValue() == Wireframe).whenTrue(espTargetAnimals).whenAtMode(page, Page.Animals);

    public Setting<Boolean> espTargetCrystals = setting("Crystals", false).des("ESP target crystals").whenAtMode(page, Page.Crystals);
    public Setting<Mode> espModeCrystals = setting("ModeCrystals", Outline).des("Mode of ESP for crystals (Turn off Fast Render In Optifine For Glow)").whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> espWireframeWallEffectCrystal = setting("CrystalWireframeWallEffect", false).des("ESP wireframe wall effects for crystal ESP").whenTrue(espTargetCrystals).whenAtMode(espModeCrystals, Wireframe).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> espWireframeOnlyWallCrystal = setting("CrystalWireframeOnlyWall", false).des("ESP wireframe only wall render for crystal ESP").whenTrue(espWireframeWallEffectCrystal).whenTrue(espTargetCrystals).whenAtMode(espModeCrystals, Wireframe).whenAtMode(page, Page.Crystals);
    public Setting<Float> espCrystalWidth = setting("CrystalOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP crystal line width").only(v -> espModeCrystals.getValue() == Wireframe || espModeCrystals.getValue() == Shader).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    public Setting<Boolean> crystalsCancelVanillaRender = setting("CNoVanillaRender", false).des("Cancels normal minecraft crystal rendering").whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    Setting<BoxMode> espBoxModeCrystals = setting("CrystalsBoxMode", BoxMode.Both).des("Crystals ESP box render mode").only(v -> espModeCrystals.getValue() == Box).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    Setting<Float> espBoxLineWidthCrystals = setting("CrystalsBoxLineWidth", 1.0f, 1.0f, 5.0f).des("Crystals ESP box line width").only(v -> espModeCrystals.getValue() == Box && (espBoxModeCrystals.getValue() == BoxMode.Lines || espBoxModeCrystals.getValue() == BoxMode.Both)).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    public Setting<Color> espColorCrystals = setting("CrystalColor", new Color(new java.awt.Color(255, 100, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 255, 255)).des("Crystal ESP color").only(v -> espModeCrystals.getValue() != Box).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    Setting<Color> espColorCrystalsSolid = setting("CrystalColorSolid", new Color(new java.awt.Color(255, 100, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 255, 120)).des("Crystals ESP box solid color").only(v -> espModeCrystals.getValue() == Box && (espBoxModeCrystals.getValue() == BoxMode.Solid || espBoxModeCrystals.getValue() == BoxMode.Both)).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    Setting<Color> espColorCrystalsLines = setting("CrystalColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Crystals ESP box lines color").only(v -> espModeCrystals.getValue() == Box && (espBoxModeCrystals.getValue() == BoxMode.Lines || espBoxModeCrystals.getValue() == BoxMode.Both)).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    public Setting<Color> espWireframeWallColorCrystals = setting("CWireframeWallColor", new Color(new java.awt.Color(255, 175, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 175, 255, 255)).des("Crystals wall only ESP wireframe color").whenTrue(espWireframeWallEffectCrystal).only(v -> espModeCrystals.getValue() == Wireframe).whenTrue(espTargetCrystals).whenAtMode(page, Page.Crystals);
    //see MixinRenderEnderCrystal for outline and wireframe

    public Setting<Boolean> espTargetItems = setting("Items", false).des("ESP target items").whenAtMode(page, Page.Items);
    public Setting<ModeItems> espModeItems = setting("ModeItems", ModeItems.Box).des("Mode of ESP for items (Turn off Fast Render in Optifine for glow)").whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    public Setting<Float> espItemWidth = setting("ItemOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP item line width").only(v -> espModeItems.getValue() == ModeItems.Wireframe || espModeItems.getValue() == ModeItems.Shader).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    public Setting<Boolean> espRangeLimitItems = setting("LimitRangeItems", false).des("ESP range limit items").whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    public Setting<Float> espRangeItems = setting("RangeItems", 30.0f, 10.0f, 64.0f).des("ESP range items").whenTrue(espRangeLimitItems).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    Setting<BoxMode> espBoxModeItems = setting("ItemsBoxMode", BoxMode.Both).des("Items ESP box render mode").only(v -> espModeItems.getValue() == ModeItems.Box).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    Setting<Float> espBoxLineWidthItems = setting("ItemsBoxLineWidth", 1.0f, 1.0f, 5.0f).des("Items ESP box line width").only(v -> espModeItems.getValue() == ModeItems.Box && (espBoxModeItems.getValue() == BoxMode.Lines || espBoxModeItems.getValue() == BoxMode.Both)).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    public Setting<Color> espColorItems = setting("ItemColor", new Color(new java.awt.Color(100, 100, 255, 255).getRGB(), true, false, 1.0f, 0.75f, 0.9f, 100, 100, 255, 255)).des("Items ESP color").only(v -> espModeItems.getValue() != ModeItems.Box).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    Setting<Color> espColorItemsSolid = setting("ItemColorSolid", new Color(new java.awt.Color(100, 100, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 255, 120)).des("Items ESP box solid color").only(v -> espModeItems.getValue() == ModeItems.Box && (espBoxModeItems.getValue() == BoxMode.Solid || espBoxModeItems.getValue() == BoxMode.Both)).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    Setting<Color> espColorItemsLines = setting("ItemColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Items ESP box lines color").only(v -> espModeItems.getValue() == ModeItems.Box && (espBoxModeItems.getValue() == BoxMode.Lines || espBoxModeItems.getValue() == BoxMode.Both)).whenTrue(espTargetItems).whenAtMode(page, Page.Items);
    //see MixinRenderEntityItem for wireframe

    public Setting<Boolean> espTargetProjectiles = setting("Projectiles", false).des("ESP target projectiles").whenAtMode(page, Page.Projectiles);
    public Setting<ModeItems> espModeProjectiles = setting("ModeProjectiles", ModeItems.Box).des("Mode of ESP for projectiles (Turn off Fast Render in Optifine for glow)").whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);
    public Setting<Float> espProjectileWidth = setting("ProjectileOutlineWidth", 1.5f, 0.1f, 5.0f).des("ESP projectile line width").only(v -> espModeProjectiles.getValue() == ModeItems.Wireframe || espModeProjectiles.getValue() == ModeItems.Shader).whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);
    Setting<BoxMode> espBoxModeProjectiles = setting("ProjectilesBoxMode", BoxMode.Both).des("Projectiles ESP box render mode").only(v -> espModeProjectiles.getValue() == ModeItems.Box).whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);
    Setting<Float> espBoxLineWidthProjectiles = setting("ProjectilesBoxLineWidth", 1.0f, 1.0f, 5.0f).des("Projectiles ESP box line width").only(v -> espModeProjectiles.getValue() == ModeItems.Box && (espBoxModeProjectiles.getValue() == BoxMode.Lines || espBoxModeProjectiles.getValue() == BoxMode.Both)).whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);
    public Setting<Color> espColorProjectiles = setting("ProjectileColor", new Color(new java.awt.Color(100, 100, 255, 255).getRGB(), true, false, 1.0f, 0.75f, 0.9f, 100, 100, 255, 255)).des("Projectiles ESP color").only(v -> espModeProjectiles.getValue() != ModeItems.Box).whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);
    Setting<Color> espColorProjectilesSolid = setting("ProjectileColorSolid", new Color(new java.awt.Color(100, 100, 255, 120).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 255, 120)).des("Projectiles ESP box solid color").only(v -> espModeProjectiles.getValue() == ModeItems.Box && (espBoxModeProjectiles.getValue() == BoxMode.Solid || espBoxModeProjectiles.getValue() == BoxMode.Both)).whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);
    Setting<Color> espColorProjectilesLines = setting("ProjectileColorLines", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).des("Projectiles ESP box lines color").only(v -> espModeProjectiles.getValue() == ModeItems.Box && (espBoxModeProjectiles.getValue() == BoxMode.Lines || espBoxModeProjectiles.getValue() == BoxMode.Both)).whenTrue(espTargetProjectiles).whenAtMode(page, Page.Projectiles);

    public ESP() {
        INSTANCE = this;
        repeatUnits.add(noHurt);
        repeatUnits.forEach(it -> {
            it.suspend();
            runRepeat(it);
        });
    }

    RepeatUnit noHurt = new RepeatUnit(() -> 1, () -> {
        if (mc.world == null) return;
        for (Entity entity : EntityUtil.entitiesList()) {
            entitiesListFlag = true;
            if (entity instanceof EntityPlayer && playerNoHurt.getValue() && espTargetPlayers.getValue() || (EntityUtil.isEntityMob(entity)) && mobNoHurt.getValue() && espTargetMobs.getValue() || (EntityUtil.isEntityAnimal(entity)) && animalNoHurt.getValue() && espTargetAnimals.getValue())
                ((EntityLivingBase) entity).hurtTime = 0;
        }
        entitiesListFlag = false;
    });

    @Override
    public void onEnable() {
        repeatUnits.forEach(RepeatUnit::resume);
        moduleEnableFlag = true;
    }

    @Listener
    public void renderEntity(RenderEntityInvokeEvent event) {
        if (RenderHelper.isInViewFrustrum(event.entityIn) && (!ignoreInvisible.getValue() || !event.entityIn.isInvisible())) {
            if ((espTargetPlayers.getValue() && espSkeletonPlayers.getValue() && event.entityIn instanceof EntityPlayer) || (espSkeletonSelf.getValue() && espTargetSelf.getValue() && event.entityIn == mc.player)) {
                float[][] rotations = SpartanTessellator.getRotationsFromModel((ModelBiped) event.modelBase);
                skeletonData.put((EntityPlayer) event.entityIn, rotations);
            }

            if (espCull.getValue()) GlStateManager.enableCull();

            if ((espWireframeWallEffectSelf.getValue() || espWireframeWallEffectPlayer.getValue() || espWireframeWallEffectMob.getValue() || espWireframeWallEffectAnimal.getValue()) && !(espRangeLimit.getValue() && mc.player.getDistance(event.entityIn) > espRange.getValue())) {
                if (event.entityIn instanceof EntityPlayer && espTargetPlayers.getValue() && espModePlayers.getValue() == Wireframe && event.entityIn != mc.player && espWireframeWallEffectPlayer.getValue())
                    renderWireframeXQZ(event, espTargetPlayers, espPlayerWidth.getValue());
                if (event.entityIn == mc.player && espTargetSelf.getValue() && espModeSelf.getValue() == ModeSelf.Wireframe && espWireframeWallEffectSelf.getValue())
                    renderWireframeXQZ(event, espTargetSelf, espSelfWidth.getValue());
                if ((EntityUtil.isEntityMob(event.entityIn) || event.entityIn instanceof EntityDragon) && espTargetMobs.getValue() && espModeMobs.getValue() == Wireframe && espWireframeWallEffectMob.getValue())
                    renderWireframeXQZ(event, espTargetMobs, espMobWidth.getValue());
                if ((EntityUtil.isEntityAnimal(event.entityIn)) && espTargetAnimals.getValue() && espModeAnimals.getValue() == Wireframe && espWireframeWallEffectAnimal.getValue())
                    renderWireframeXQZ(event, espTargetAnimals, espAnimalWidth.getValue());
            }

            if ((event.entityIn == mc.player && espTargetSelf.getValue() && selfCancelVanillaRender.getValue()) || (event.entityIn instanceof EntityPlayer && espTargetPlayers.getValue() && playersCancelVanillaRender.getValue()) || ((EntityUtil.isEntityMob(event.entityIn) || event.entityIn instanceof EntityDragon) && espTargetMobs.getValue() && mobsCancelVanillaRender.getValue()) || ((EntityUtil.isEntityAnimal(event.entityIn)) && espTargetAnimals.getValue() && animalsCancelVanillaRender.getValue()))
                event.cancel();
        }
    }

    @Listener
    public void renderPost(RenderEntityLayersEvent event) {
        if (RenderHelper.isInViewFrustrum(event.entityIn) && (!ignoreInvisible.getValue() || !event.entityIn.isInvisible())) {
            if (espCull.getValue()) GlStateManager.enableCull();
            if (mc.player.getDistance(event.entityIn) > espRange.getValue() && espRangeLimit.getValue() && ((espTargetPlayers.getValue() && espModePlayers.getValue() == Glow) || (espTargetMobs.getValue() && espModeMobs.getValue() == Glow) || (espTargetAnimals.getValue() && espModeAnimals.getValue() == Glow) || (espTargetCrystals.getValue() && espModeCrystals.getValue() == Glow) || (espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Glow))) {
                unGlow();
            }

            if ((!(espWireframeWallEffectSelf.getValue() && espWireframeOnlyWallSelf.getValue()) || !(espWireframeWallEffectPlayer.getValue() && espWireframeOnlyWallPlayer.getValue()) || !(espWireframeWallEffectMob.getValue() && espWireframeOnlyWallMob.getValue()) || !(espWireframeWallEffectAnimal.getValue() && espWireframeOnlyWallAnimal.getValue())) && !(espRangeLimit.getValue() && mc.player.getDistance(event.entityIn) > espRange.getValue())) {
                if (event.entityIn instanceof EntityPlayer && espTargetPlayers.getValue() && espModePlayers.getValue() == Wireframe && event.entityIn != mc.player && !(espWireframeWallEffectPlayer.getValue() && espWireframeOnlyWallPlayer.getValue()))
                    renderWireframe(event, espTargetPlayers, espWireframeWallEffectPlayer, espPlayerWidth.getValue());
                if (event.entityIn == mc.player && espTargetSelf.getValue() && espModeSelf.getValue() == ModeSelf.Wireframe && !(espWireframeWallEffectSelf.getValue() && espWireframeOnlyWallSelf.getValue()))
                    renderWireframe(event, espTargetSelf, espWireframeWallEffectSelf, espSelfWidth.getValue());
                if ((EntityUtil.isEntityMob(event.entityIn) || event.entityIn instanceof EntityDragon) && espTargetMobs.getValue() && espModeMobs.getValue() == Wireframe && !(espWireframeWallEffectMob.getValue() && espWireframeOnlyWallMob.getValue()))
                    renderWireframe(event, espTargetMobs, espWireframeWallEffectMob, espMobWidth.getValue());
                if ((EntityUtil.isEntityAnimal(event.entityIn)) && espTargetAnimals.getValue() && espModeAnimals.getValue() == Wireframe && !(espWireframeWallEffectAnimal.getValue() && espWireframeOnlyWallAnimal.getValue()))
                    renderWireframe(event, espTargetAnimals, espWireframeWallEffectAnimal, espAnimalWidth.getValue());
            }
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if ((espTargetPlayers.getValue() && espSkeletonPlayers.getValue()) || (espTargetSelf.getValue() && espSkeletonSelf.getValue())) {

            List<EntityPlayer> list = new ArrayList<>(skeletonData.keySet());
            list.removeAll(mc.world.playerEntities);
            list.forEach(skeletonData.keySet()::remove);

            if (espSkeletonDeathFade.getValue()) {
                List<EntityPlayer> list2 = new ArrayList<>(skeletonFadeData.keySet());
                list2.removeAll(mc.world.playerEntities);
                list2.forEach(skeletonFadeData.keySet()::remove);
            }

            for (Entity entity : entitiesList()) {
                entitiesListFlag = true;
                if (!(entity instanceof EntityPlayer) || ((EntityPlayer) entity).isElytraFlying() || ((EntityPlayer) entity).isSpectator()) continue;
                if (mc.gameSettings.thirdPersonView == 0 && espSkeletonSelf.getValue() && espTargetSelf.getValue() && entity == mc.player && Freecam.INSTANCE.camera == null)
                    continue;
                if (!RenderHelper.isInViewFrustrum(entity))
                    continue;

                if (!entitiesList().contains(entity) || ((EntityPlayer) entity).isPlayerSleeping() || (entity.isDead && (!espSkeletonDeathFade.getValue() || (skeletonFadeData.get(entity) != null && skeletonFadeData.get(entity) >= 300.0f))))
                    skeletonData.remove(entity);

                if (!skeletonData.containsKey(entity)) continue;
                if (((EntityPlayer) entity).deathTime > 0 && !espSkeletonDeathFade.getValue()) continue;

                int skellyPlayerColor = espColorSkeletonPlayer.getValue().getColor();
                boolean skellyRollColor = espSkeletonRollingPlayerColor.getValue();
                java.awt.Color skellyRollColor1 = espSkeletonRollingPlayerColor1.getValue().getColorColor();
                java.awt.Color skellyRollColor2 = espSkeletonRollingPlayerColor2.getValue().getColorColor();

                if (FriendManager.isFriend(entity)) {
                    skellyPlayerColor = espColorSkeletonFriend.getValue().getColor();
                    skellyRollColor = espSkeletonRollingFriendColor.getValue();
                    skellyRollColor1 = espSkeletonRollingFriendColor1.getValue().getColorColor();
                    skellyRollColor2 = espSkeletonRollingFriendColor2.getValue().getColorColor();
                }

                if (EnemyManager.isEnemy(entity)) {
                    skellyPlayerColor = espColorSkeletonEnemy.getValue().getColor();
                    skellyRollColor = espSkeletonRollingEnemyColor.getValue();
                    skellyRollColor1 = espSkeletonRollingEnemyColor1.getValue().getColorColor();
                    skellyRollColor2 = espSkeletonRollingEnemyColor2.getValue().getColorColor();
                }

                if ((entity == mc.player && espTargetSelf.getValue() && espSkeletonSelf.getValue()) || (espTargetPlayers.getValue() && espSkeletonPlayers.getValue() && entity != mc.player)) {
                    if (entity == mc.player) {
                        SpartanTessellator.drawSkeleton((EntityPlayer) entity, skeletonData.get(entity), espSkeletonSelfWidth.getValue(), espSkeletonGradientSelfEnds.getValue(), espSkeletonRollingSelfColor.getValue(), espSkeletonRollingSelfColor1.getValue().getColorColor(), espSkeletonRollingSelfColor2.getValue().getColorColor(), espColorSkeletonSelf.getValue().getColor());
                    }
                    else {
                        SpartanTessellator.drawSkeleton((EntityPlayer) entity, skeletonData.get(entity), espSkeletonPlayersWidth.getValue(), espSkeletonGradientPlayerEnds.getValue(), skellyRollColor, skellyRollColor1, skellyRollColor2, skellyPlayerColor);
                    }
                }
            }
            entitiesListFlag = false;
        }

        if (espTargetPlayers.getValue() && espModePlayers.getValue() == Box) renderBox(espTargetPlayers, espBoxModePlayers, espBoxLineWidthPlayers, espColorPlayersLines, espColorPlayersSolid);
        if (espTargetMobs.getValue() && espModeMobs.getValue() == Box) renderBox(espTargetMobs, espBoxModeMobs, espBoxLineWidthMobs, espColorMobsLines, espColorMobsSolid);
        if (espTargetAnimals.getValue() && espModeAnimals.getValue() == Box) renderBox(espTargetAnimals, espBoxModeAnimals, espBoxLineWidthAnimals, espColorAnimalsLines, espColorAnimalsSolid);
        if (espTargetCrystals.getValue() && espModeCrystals.getValue() == Box) renderBox(espTargetCrystals, espBoxModeCrystals, espBoxLineWidthCrystals, espColorCrystalsLines, espColorCrystalsSolid);
        if (espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Box) renderBox(espTargetProjectiles, espBoxModeProjectiles, espBoxLineWidthProjectiles, espColorProjectilesLines, espColorProjectilesSolid);
        if (espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Box) {
            if (espModeItems.getValue() == ModeItems.Box) {
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (RenderHelper.isInViewFrustrum(entity)) {
                        if (entity instanceof EntityItem && espTargetItems.getValue() && !(espRangeLimitItems.getValue() && mc.player.getDistance(entity) > espRangeItems.getValue())) {
                            if (espBoxModeItems.getValue() == BoxMode.Solid || espBoxModeItems.getValue() == BoxMode.Both)
                                SpartanTessellator.drawBBFullBox(entity, espColorItemsSolid.getValue().getColor());
                            if (espBoxModeItems.getValue() == BoxMode.Lines || espBoxModeItems.getValue() == BoxMode.Both)
                                SpartanTessellator.drawBBLineBox(entity, espBoxLineWidthItems.getValue(), espColorItemsLines.getValue().getColor());
                        }
                    }
                }
                entitiesListFlag = false;
            }
        }
    }

    @Listener
    public void onRenderWorld1(RenderEvent.Extra1 event) {
        if (espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Wireframe) {
            EntityUtil.runEntityCheck();

            if (isEntityProjectileLoaded) {

                renderProjectileFlag = true;

                GL11.glEnable(GL_LINE_SMOOTH);
                GL11.glLineWidth(espProjectileWidth.getValue());
                GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

                for (Entity entity : entitiesList()) {
                    entitiesListFlag = true;
                    if (!RenderHelper.isInViewFrustrum(entity) || !(entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye)) continue;
                    GL11.glColor4f(espColorProjectiles.getValue().getColorColor().getRed() / 255.0f, espColorProjectiles.getValue().getColorColor().getGreen() / 255.0f, espColorProjectiles.getValue().getColorColor().getBlue() / 255.0f, espColorProjectiles.getValue().getAlpha() / 255.0f);
                    renderEntityInWorld(entity);
                }
                entitiesListFlag = false;

                GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                GL11.glDisable(GL_LINE_SMOOTH);
                GL11.glDisable(GL_LIGHTING);
                renderProjectileFlag = false;
            }
        }

        if ((espTargetSelf.getValue() && espModeSelf.getValue() == ModeSelf.Outline) || (espTargetPlayers.getValue() && espModePlayers.getValue() == Outline) || (espTargetMobs.getValue() && espModeMobs.getValue() == Outline) || (espTargetAnimals.getValue() && espModeAnimals.getValue() == Outline) || (espTargetCrystals.getValue() && espModeCrystals.getValue() == Outline)) {
            EntityUtil.runEntityCheck();

            if (isEntityPlayerLoaded || isEntityMobLoaded || isEntityAnimalLoaded || isEntityCrystalLoaded) {
                outline1();
                doRenderVanillaEntitiesAgain(false);
                outline2();
                doRenderVanillaEntitiesAgain(false);
                outline3();
                doRenderVanillaEntitiesAgain(true);
                outline4();
            }
        }
    }

    @Listener
    public void onRenderWorldPost(RenderWorldPostEventCenter event) {
        if ((espTargetPlayers.getValue() && espModePlayers.getValue() == Shader) || (espTargetMobs.getValue() && espModeMobs.getValue() == Shader) || (espTargetAnimals.getValue() && espModeAnimals.getValue() == Shader) || (espTargetCrystals.getValue() && espModeCrystals.getValue() == Shader) || (espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Shader) || (espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Shader)) {
            renderOutlineFlag = true;
            renderShaderOutline();
            renderOutlineFlag = false;
        }
    }


    @Override
    public void onRenderTick() {
        if (!(playerNoHurt.getValue() && espTargetPlayers.getValue() || mobNoHurt.getValue() && espTargetMobs.getValue() || animalNoHurt.getValue() && espTargetAnimals.getValue()))
            repeatUnits.forEach(RepeatUnit::suspend);
        else
            repeatUnits.forEach(RepeatUnit::resume);

        if ((espTargetSelf.getValue() && espModeSelf.getValue() == ModeSelf.Glow) || (espTargetPlayers.getValue() && espModePlayers.getValue() == Glow) || (espTargetMobs.getValue() && espModeMobs.getValue() == Glow) || (espTargetAnimals.getValue() && espModeAnimals.getValue() == Glow) || (espTargetCrystals.getValue() && espModeCrystals.getValue() == Glow) || (espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Glow) || (espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Glow)) {
            for (Shader shader : mc.renderGlobal.entityOutlineShader.listShaders) {
                if (shader.getShaderManager().getShaderUniform("Radius") != null)
                    shader.getShaderManager().getShaderUniform("Radius").set(espGlowWidth.getValue());
            }
            for (Entity entity : EntityUtil.entitiesList()) {
                entitiesListFlag = true;
                if ((!ignoreInvisible.getValue() || !entity.isInvisible()) && ((entity == mc.player && espTargetSelf.getValue() && espModeSelf.getValue() == ModeSelf.Glow) || (entity instanceof EntityPlayer && espTargetPlayers.getValue() && espModePlayers.getValue() == Glow && entity != mc.player) || ((EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) && espTargetMobs.getValue() && espModeMobs.getValue() == Glow) || ((EntityUtil.isEntityAnimal(entity)) && espTargetAnimals.getValue() && espModeAnimals.getValue() == Glow) || (entity instanceof EntityEnderCrystal && espTargetCrystals.getValue() && espModeCrystals.getValue() == Glow) || (entity instanceof EntityItem && espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Glow) || ((entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye) && espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Glow)))
                    entity.setGlowing(true);
                else unGlow(entity);
            }
            entitiesListFlag = false;
        }
        else {
            unGlow();
        }
    }

    @Override
    public void onDisable() {
        unGlow();
        repeatUnits.forEach(RepeatUnit::suspend);
        moduleDisableFlag = true;
    }

    private void renderEntityInWorld(Entity entity) {
        Vec3d renderPos = EntityUtil.interpolateEntityRender(entity, mc.getRenderPartialTicks());
        Render<Entity> entityRenderObj = mc.getRenderManager().getEntityRenderObject(entity);
        if (entityRenderObj != null) entityRenderObj.doRender(entity, renderPos.x, renderPos.y, renderPos.z, entity.rotationYaw, mc.getRenderPartialTicks());
    }

    private void renderShaderOutline() {
        if (espShaderOutlineFastMode.getValue()) {
            boolean isEntityLoaded = false;
            for (Entity entity : EntityUtil.entitiesList()) {
                entitiesListFlag = true;
                if (entity != null) {
                    isEntityLoaded = true;
                    break;
                }
            }
            entitiesListFlag = false;
            if (isEntityLoaded) {
                java.awt.Color color = new java.awt.Color(espShaderOutlineFastModeColor.getValue().getColorColor().getRed(), espShaderOutlineFastModeColor.getValue().getColorColor().getGreen(), espShaderOutlineFastModeColor.getValue().getColorColor().getBlue(), espShaderOutlineFastModeColor.getValue().getAlpha());
                int alpha = espShaderOutlineFastModeColor.getValue().getAlpha();
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);

                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (ignoreInvisible.getValue() && entity.isInvisible()) continue;
                    if (!(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue())) {
                        if (entity instanceof EntityPlayer && espTargetPlayers.getValue() && espModePlayers.getValue() == Shader && entity != mc.player)
                            renderEntityInWorld(entity);
                        if ((EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) && espTargetMobs.getValue() && espModeMobs.getValue() == Shader)
                            renderEntityInWorld(entity);
                        if ((EntityUtil.isEntityAnimal(entity)) && espTargetAnimals.getValue() && espModeAnimals.getValue() == Shader)
                            renderEntityInWorld(entity);
                        if (entity instanceof EntityEnderCrystal && espTargetCrystals.getValue() && espModeCrystals.getValue() == Shader)
                            renderEntityInWorld(entity);
                        if ((entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye) && espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Shader)
                            renderEntityInWorld(entity);
                    }
                    if ((!(espRangeLimitItems.getValue() && mc.player.getDistance(entity) > espRangeItems.getValue())) && (entity instanceof EntityItem && espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Shader))
                        renderEntityInWorld(entity);
                }
                entitiesListFlag = false;

                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color, alpha, espShaderOutlineFastModeWidth.getValue(), 1.0f);
            }
        }
        else {
            boolean isEntityPlayerLoaded = false;
            boolean isEntityMobLoaded = false;
            boolean isEntityAnimalLoaded = false;
            boolean isEntityCrystalLoaded = false;
            boolean isEntityItemLoaded = false;
            boolean isEntityProjectileLoaded = false;
            for (Entity entity : EntityUtil.entitiesList()) {
                entitiesListFlag = true;
                if (!RenderHelper.isInViewFrustrum(entity)) continue;
                if (entity instanceof EntityPlayer) isEntityPlayerLoaded = true;
                if (EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) isEntityMobLoaded = true;
                if (EntityUtil.isEntityAnimal(entity)) isEntityAnimalLoaded = true;
                if (entity instanceof EntityEnderCrystal) isEntityCrystalLoaded = true;
                if (entity instanceof EntityItem) isEntityItemLoaded = true;
                if (entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye) isEntityProjectileLoaded = true;
            }
            entitiesListFlag = false;

            if (espTargetMobs.getValue() && espModeMobs.getValue() == Shader && isEntityMobLoaded) {
                java.awt.Color color1 = new java.awt.Color(0, 0, 0, 0);
                int alpha1 = 0;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (ignoreInvisible.getValue() && entity.isInvisible()) continue;
                    if (!(EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon)) continue;
                    if ((!(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue()))) {
                        color1 = espColorMobs.getValue().getColorColor();
                        alpha1 = espColorMobs.getValue().getAlpha();
                        renderEntityInWorld(entity);
                    }
                }
                entitiesListFlag = false;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color1, alpha1, espMobWidth.getValue(), 1.0f);
            }


            if (espTargetAnimals.getValue() && espModeAnimals.getValue() == Shader && isEntityAnimalLoaded) {
                java.awt.Color color2 = new java.awt.Color(0, 0, 0, 0);
                int alpha2 = 0;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (ignoreInvisible.getValue() && entity.isInvisible()) continue;
                    if (!(EntityUtil.isEntityAnimal(entity))) continue;
                    if ((!(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue()))) {
                        color2 = espColorAnimals.getValue().getColorColor();
                        alpha2 = espColorAnimals.getValue().getAlpha();
                        renderEntityInWorld(entity);
                    }
                }
                entitiesListFlag = false;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color2, alpha2, espAnimalWidth.getValue(), 1.0f);
            }


            if (espTargetCrystals.getValue() && espModeCrystals.getValue() == Shader && isEntityCrystalLoaded) {
                java.awt.Color color3 = new java.awt.Color(0, 0, 0, 0);
                int alpha3 = 0;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    if ((!(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue()))) {
                        color3 = espColorCrystals.getValue().getColorColor();
                        alpha3 = espColorCrystals.getValue().getAlpha();
                        renderEntityInWorld(entity);
                    }
                }
                entitiesListFlag = false;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color3, alpha3, espCrystalWidth.getValue(), 1.0f);
            }


            if (espTargetItems.getValue() && espModeItems.getValue() == ModeItems.Shader && isEntityItemLoaded) {
                java.awt.Color color4 = new java.awt.Color(0, 0, 0, 0);
                int alpha4 = 0;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (!(entity instanceof EntityItem)) continue;
                    if ((!(espRangeLimitItems.getValue() && mc.player.getDistance(entity) > espRangeItems.getValue()))) {
                        color4 = espColorItems.getValue().getColorColor();
                        alpha4 = espColorItems.getValue().getAlpha();
                        renderEntityInWorld(entity);
                    }
                }
                entitiesListFlag = false;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color4, alpha4, espItemWidth.getValue(), 1.0f);
            }

            if (espTargetProjectiles.getValue() && espModeProjectiles.getValue() == ModeItems.Shader && isEntityProjectileLoaded) {
                java.awt.Color color69 = new java.awt.Color(0, 0, 0, 0);
                int alpha69 = 0;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (!(entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye)) continue;
                    if ((!(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue()))) {
                        color69 = espColorProjectiles.getValue().getColorColor();
                        alpha69 = espColorProjectiles.getValue().getAlpha();
                        renderEntityInWorld(entity);
                    }
                }
                entitiesListFlag = false;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color69, alpha69, espProjectileWidth.getValue(), 1.0f);
            }


            if (espTargetPlayers.getValue() && espModePlayers.getValue() == Shader && isEntityPlayerLoaded) {
                java.awt.Color color5 = new java.awt.Color(0, 0, 0, 0);
                int alpha5 = 0;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.startDraw(mc.getRenderPartialTicks(), false);
                for (Entity entity : EntityUtil.entitiesList()) {
                    entitiesListFlag = true;
                    if (ignoreInvisible.getValue() && entity.isInvisible()) continue;
                    if (!(entity instanceof EntityPlayer)) continue;
                    if (entity != mc.player && (!(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue()))) {
                        if (FriendManager.isFriend(entity)) {
                            color5 = espColorPlayersFriend.getValue().getColorColor();
                            alpha5 = espColorPlayersFriend.getValue().getAlpha();
                        }
                        else if (EnemyManager.isEnemy(entity)) {
                            color5 = espColorPlayersEnemy.getValue().getColorColor();
                            alpha5 = espColorPlayersEnemy.getValue().getAlpha();
                        }
                        else {
                            color5 = espColorPlayers.getValue().getColorColor();
                            alpha5 = espColorPlayers.getValue().getAlpha();
                        }
                        renderEntityInWorld(entity);
                    }
                }
                entitiesListFlag = false;
                me.thediamondsword5.moloch.utils.graphics.shaders.sexy.Outline.SHADER_OUTLINE.stopDraw(color5, alpha5, espPlayerWidth.getValue(), 1.0f);
            }
        }
    }

    private void renderWireframeXQZ(RenderEntityInvokeEvent event, Setting<Boolean> settingTarget, float espWidth) {
        java.awt.Color wallColor;
        if (settingTarget == espTargetPlayers) {
            if (FriendManager.isFriend(event.entityIn)) wallColor = espWireframeWallColorPlayersFriend.getValue().getColorColor();
            else if (EnemyManager.isEnemy(event.entityIn)) wallColor = espWireframeWallColorPlayersEnemy.getValue().getColorColor();
            else wallColor = espWireframeWallColorPlayers.getValue().getColorColor();
        }
        else if (settingTarget == espTargetSelf) wallColor = espWireframeWallColorSelf.getValue().getColorColor();
        else if (settingTarget == espTargetMobs) wallColor = espWireframeWallColorMobs.getValue().getColorColor();
        else wallColor = espWireframeWallColorAnimals.getValue().getColorColor();

        int wallAlpha;
        if (settingTarget == espTargetPlayers) {
            if (FriendManager.isFriend(event.entityIn)) wallAlpha = espWireframeWallColorPlayersFriend.getValue().getAlpha();
            else if (EnemyManager.isEnemy(event.entityIn)) wallAlpha = espWireframeWallColorPlayersEnemy.getValue().getAlpha();
            else wallAlpha = espWireframeWallColorPlayers.getValue().getAlpha();
        }
        else if (settingTarget == espTargetSelf) wallAlpha = espWireframeWallColorSelf.getValue().getAlpha();
        else if (settingTarget == espTargetMobs) wallAlpha = espWireframeWallColorMobs.getValue().getAlpha();
        else wallAlpha = espWireframeWallColorAnimals.getValue().getAlpha();

        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glEnable(GL_LINE_SMOOTH);
        SpartanTessellator.prepareGL();
        GL11.glDisable(GL_LIGHTING);

        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        GL11.glLineWidth(espWidth);

        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthFunc(GL_GREATER);
        GL11.glColor4f(wallColor.getRed() / 255.0f, wallColor.getGreen() / 255.0f, wallColor.getBlue() / 255.0f, wallAlpha / 255.0f);
        event.modelBase.render(event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        GL11.glDepthFunc(GL_LESS);

        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        SpartanTessellator.releaseGL();
    }

    private void renderWireframe(RenderEntityLayersEvent event, Setting<Boolean> settingTarget, Setting<Boolean> settingespWireframeWallEffect, float espWidth) {
        java.awt.Color color;
        if (settingTarget == espTargetPlayers) {
            if (FriendManager.isFriend(event.entityIn)) color = espColorPlayersFriend.getValue().getColorColor();
            else if (EnemyManager.isEnemy(event.entityIn)) color = espColorPlayersEnemy.getValue().getColorColor();
            else color = espColorPlayers.getValue().getColorColor();
        }
        else if (settingTarget == espTargetSelf) color = espColorSelf.getValue().getColorColor();
        else if (settingTarget == espTargetMobs) color = espColorMobs.getValue().getColorColor();
        else color = espColorAnimals.getValue().getColorColor();

        int alpha;
        if (settingTarget == espTargetPlayers) {
            if (FriendManager.isFriend(event.entityIn)) alpha = espColorPlayersFriend.getValue().getAlpha();
            else if (EnemyManager.isEnemy(event.entityIn)) alpha = espColorPlayersEnemy.getValue().getAlpha();
            else alpha = espColorPlayers.getValue().getAlpha();
        }
        else if (settingTarget == espTargetSelf) alpha = espColorSelf.getValue().getAlpha();
        else if (settingTarget == espTargetMobs) alpha = espColorMobs.getValue().getAlpha();
        else alpha = espColorAnimals.getValue().getAlpha();

        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glEnable(GL_LINE_SMOOTH);
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL_LIGHTING);
        GL11.glLineWidth(espWidth);
        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        if (settingespWireframeWallEffect.getValue()) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha / 255.0f);
        event.modelBase.render(event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);

        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        SpartanTessellator.releaseGL();
    }

    private void unGlow() {
        for (Entity entity : EntityUtil.entitiesList()) {
            entitiesListFlag = true;
            entity.setGlowing(false);
        }
        entitiesListFlag = false;
    }

    private void unGlow(Entity entityIn) {
        for (Entity entity : EntityUtil.entitiesList()) {
            entitiesListFlag = true;
            if (entity == entityIn) entity.setGlowing(false);
        }
        entitiesListFlag = false;
    }

    private void renderBox(Setting<Boolean> settingESPTarget, Setting<BoxMode> settingESPBoxMode, Setting<Float> settingESPBoxLineWidth, Setting<Color> settingESPColorLines, Setting<Color> settingESPColorSolid) {
        for (Entity entity : EntityUtil.entitiesList()) {
            entitiesListFlag = true;
            if (RenderHelper.isInViewFrustrum(entity) && (!ignoreInvisible.getValue() || !entity.isInvisible())) {
                boolean entityCheck;
                if (settingESPTarget == espTargetPlayers) {
                    entityCheck = (entity instanceof EntityPlayer);

                    if (FriendManager.isFriend(entity)) settingESPColorLines = espColorPlayersLinesFriend;
                    else if (EnemyManager.isEnemy(entity)) settingESPColorLines = espColorPlayersLinesEnemy;
                    else settingESPColorLines = espColorPlayersLines;

                    if (FriendManager.isFriend(entity)) settingESPColorSolid = espColorPlayersSolidFriend;
                    else if (EnemyManager.isEnemy(entity)) settingESPColorSolid = espColorPlayersSolidEnemy;
                    else settingESPColorSolid = espColorPlayersSolid;
                }
                else if (settingESPTarget == espTargetMobs) entityCheck = (EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon);
                else if (settingESPTarget == espTargetAnimals) entityCheck = (EntityUtil.isEntityAnimal(entity));
                else if (settingESPTarget == espTargetCrystals) entityCheck = (entity instanceof EntityEnderCrystal);
                else if (settingESPTarget == espTargetItems) entityCheck = (entity instanceof EntityItem);
                else entityCheck = (entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye);


                if (entityCheck && settingESPTarget.getValue() && entity != mc.player && !(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue())) {
                    if (settingESPBoxMode.getValue() == BoxMode.Solid || settingESPBoxMode.getValue() == BoxMode.Both)
                        SpartanTessellator.drawBBFullBox(entity, settingESPColorSolid.getValue().getColor());
                    if (settingESPBoxMode.getValue() == BoxMode.Lines || settingESPBoxMode.getValue() == BoxMode.Both)
                        SpartanTessellator.drawBBLineBox(entity, settingESPBoxLineWidth.getValue(), settingESPColorLines.getValue().getColor());
                }
            }
        }
        entitiesListFlag = false;
    }

    private void outline1() {
        renderOutlineFlag = true;

        Framebuffer fbo = mc.framebuffer;
        if (fbo != null) {
            if (fbo.depthBuffer > -1) {
                EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
                int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
                EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
                EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
                EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
                EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
                fbo.depthBuffer = -1;
            }
        }

        GL11.glLineWidth(espOutlineWidth.getValue());
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glEnable(GL_STENCIL_TEST);
        GL11.glStencilFunc(GL_NEVER, GL_LINES, 0xF);
        GL11.glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    private void outline2() {
        GL11.glStencilFunc(GL_NEVER, GL_POINTS, 0xF);
        GL11.glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    private void outline3() {
        GL11.glStencilFunc(GL_EQUAL, GL_LINES, 0xF);
        GL11.glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        GL11.glDepthRange(0.0, 0.001);
    }

    private void outline4() {
        GL11.glDepthRange(0.0, 1.0);
        GL11.glDisable(GL_STENCIL_TEST);
        GL11.glDisable(GL_LINE_SMOOTH);
        GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        renderOutlineFlag = false;
    }

    private void doRenderVanillaEntitiesAgain(boolean flag) {
        for (Entity entity : EntityUtil.entitiesList()) {
            entitiesListFlag = true;
            if (RenderHelper.isInViewFrustrum(entity) && (!ignoreInvisible.getValue() || !entity.isInvisible()) && !(espRangeLimit.getValue() && mc.player.getDistance(entity) > espRange.getValue()) && ((entity instanceof EntityPlayer && espTargetPlayers.getValue() && espModePlayers.getValue() == Outline && entity != mc.player && isEntityPlayerLoaded) || (entity == mc.player && espTargetSelf.getValue() && espModeSelf.getValue() == ModeSelf.Outline) || ((EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) && espTargetMobs.getValue() && espModeMobs.getValue() == Outline && isEntityMobLoaded) || ((EntityUtil.isEntityAnimal(entity)) && espTargetAnimals.getValue() && espModeAnimals.getValue() == Outline && isEntityAnimalLoaded) || (entity instanceof EntityEnderCrystal && espTargetCrystals.getValue() && espModeCrystals.getValue() == Outline && isEntityCrystalLoaded))) {
                if (flag) {
                    outLineColor(entity);
                    GL11.glDisable(GL_TEXTURE_2D);
                    GL11.glDisable(GL_LIGHTING);
                }
                renderEntityInWorld(entity);
            }
        }
        entitiesListFlag = false;
    }

    private void outLineColor(Entity entity) {
        //color stuff (separate alpha bc .getAlpha() from color always returns 255 for some reason)
        java.awt.Color theColor = new java.awt.Color(255, 255, 255, 255);
        if (entity instanceof EntityPlayer && espTargetPlayers.getValue() && entity != mc.player) {
            if (FriendManager.isFriend(entity)) theColor = espColorPlayersFriend.getValue().getColorColor();
            else if (EnemyManager.isEnemy(entity)) theColor = espColorPlayersEnemy.getValue().getColorColor();
            else theColor = espColorPlayers.getValue().getColorColor();
        }
        if (entity == mc.player && espTargetSelf.getValue())
            theColor = espColorSelf.getValue().getColorColor();
        if ((EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) && espTargetMobs.getValue())
            theColor = espColorMobs.getValue().getColorColor();
        if ((EntityUtil.isEntityAnimal(entity)) && espTargetAnimals.getValue())
            theColor = espColorAnimals.getValue().getColorColor();
        if (entity instanceof EntityEnderCrystal && espTargetCrystals.getValue())
            theColor = espColorCrystals.getValue().getColorColor();

        int theAlpha = 255;
        if (entity instanceof EntityPlayer && espTargetPlayers.getValue() && entity != mc.player) {
            if (FriendManager.isFriend(entity)) theAlpha = espColorPlayersFriend.getValue().getAlpha();
            else if (EnemyManager.isEnemy(entity)) theAlpha = espColorPlayersEnemy.getValue().getAlpha();
            else theAlpha = espColorPlayers.getValue().getAlpha();
        }
        if (entity == mc.player && espTargetSelf.getValue())
            theAlpha = espColorSelf.getValue().getAlpha();
        if ((EntityUtil.isEntityMob(entity) || entity instanceof EntityDragon) && espTargetMobs.getValue())
            theAlpha = espColorMobs.getValue().getAlpha();
        if ((EntityUtil.isEntityAnimal(entity)) && espTargetAnimals.getValue())
            theAlpha = espColorAnimals.getValue().getAlpha();
        if (entity instanceof EntityEnderCrystal && espTargetCrystals.getValue())
            theAlpha = espColorCrystals.getValue().getAlpha();

        GL11.glColor4f(theColor.getRed() / 255.0f, theColor.getGreen() / 255.0f, theColor.getBlue() / 255.0f, theAlpha / 255.0f);
    }

    enum Page {
        General, Self, Players, Mobs, Animals, Crystals, Items, Projectiles
    }

    public enum Mode {
        Outline, Glow, Shader, Box, Wireframe, None
    }

    public enum ModeSelf {
        Outline, Glow, Wireframe, None
    }

    public enum ModeItems {
        Glow, Shader, Box, Wireframe
    }

    enum BoxMode {
        Lines, Solid, Both
    }
}
