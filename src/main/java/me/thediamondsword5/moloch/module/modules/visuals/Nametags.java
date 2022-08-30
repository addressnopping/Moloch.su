package me.thediamondsword5.moloch.module.modules.visuals;

import me.thediamondsword5.moloch.client.EnemyManager;
import me.thediamondsword5.moloch.client.PopManager;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.hud.huds.ArmorDisplay;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.client.FriendManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.RenderHelper;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static net.spartanb312.base.command.Command.mc;
import static org.lwjgl.opengl.GL11.*;
//TODO: add total inv totems to items display,   add blacklistable enchantments for items display,   add multiline support,   add multiline icons,    TURN INTO 2D (u need to make 3d to 2d work correct first)
@Parallel
@ModuleInfo(name = "Nametags", category = Category.VISUALS, description = "Draw nametags with information on top of entities")
public class Nametags extends Module {

    public static Nametags INSTANCE;
    private final HashMap<Entity, Float> prevHealthMap = new HashMap<>();
    private final HashMap<Entity, Integer> interpProgressMap = new HashMap<>();
    private final HashMap<Entity, Float> prevHealthAbsorptionMap = new HashMap<>();
    private final HashMap<Entity, Integer> interpProgressAbsorptionMap = new HashMap<>();
    private final Timer timer = new Timer();

    Setting<Float> size = setting("GlobalSize", 0.4f, 0.1f, 3.0f).des("Scales everything in the nametag at once");
    Setting<Page> page = setting("Page", Page.Distance);

    Setting<Float> range = setting("Range", 256.0f, 1.0f, 256.0f).des("Distance to render nametags in on entities").whenAtMode(page, Page.Distance);
    float theFuckingScaleIllFixThisLator = 0.188f;
    //Setting<Float> scaleFactor = setting("ScaleFactor", 0.18f, 0.0f, 1.0f).des("Distance based scale factor of nametag").whenAtMode(page, Page.Distance);
    Setting<Float> innerLockRange = setting("InnerLockRange", 2.5f, 0.0f, 10.0f).des("Stops scaling nametags down when you reach within this distance of that nametag").whenAtMode(page, Page.Distance);
    Setting<Float> outerLockRange = setting("OuterLockRange", 25.5f, 0.0f, 50.0f).des("Stops scaling nametags up when you reach beyond this distance of that nametag").whenAtMode(page, Page.Distance);

    Setting<Float> yOffset = setting("YOffset", 0.8f, 0.0f, 5.0f).whenAtMode(page, Page.Rect);
    Setting<Float> rectHeight = setting("RectHeight", 13.3f, 0.1f, 50.0f).des("Height of BG rect").whenAtMode(page, Page.Rect);
    Setting<Float> rectWidth = setting("RectWidth", 4.9f, -20.0f, 20.0f).des("Extra width from the ends of the nametag text").whenAtMode(page, Page.Rect);
    Setting<Boolean> roundedRect = setting("RoundedRect", false).des("Rounded BG rect").whenAtMode(page, Page.Rect);
    Setting<Float> roundedRectRadius = setting("RoundedRectRadius", 0.5f, 0.0f, 1.0f).des("Radius of rounded BG rect corners").whenTrue(roundedRect).whenAtMode(page, Page.Rect);
    Setting<Boolean> roundedRectTopRight = setting("RoundedTopRight", true).whenTrue(roundedRect).whenAtMode(page, Page.Rect);
    Setting<Boolean> roundedRectTopLeft = setting("RoundedTopLeft", true).whenTrue(roundedRect).whenAtMode(page, Page.Rect);
    Setting<Boolean> roundedRectDownRight = setting("RoundedDownRight", true).whenTrue(roundedRect).whenAtMode(page, Page.Rect);
    Setting<Boolean> roundedRectDownLeft = setting("RoundedDownLeft", true).whenTrue(roundedRect).whenAtMode(page, Page.Rect);
    Setting<Boolean> rectBorder = setting("RectBorder", false).des("Draw a border around the BG rect").whenAtMode(page, Page.Rect);
    Setting<Float> rectBorderWidth = setting("RectBorderWidth", 1.0f, 1.0f, 2.0f).des("BG rect border thickness").whenTrue(rectBorder).whenAtMode(page, Page.Rect);
    Setting<Float> rectBorderOffset = setting("RectBorderOffset", 2.0f, 0.0f, 5.0f).des("BG rect border offset from rect").whenTrue(rectBorder).whenAtMode(page, Page.Rect);

    Setting<Boolean> healthBar = setting("HealthBar", true).des("Draws a rect showing how much health the nametagged entity has").whenAtMode(page, Page.HealthBar);
    Setting<Float> healthBarThickness = setting("HealthBarThickness", 1.5f, 0.1f, 4.0f).des("Thickness of health bar").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Float> healthBarOffset = setting("HealthBarYOffset", 0.0f, -20.0f, 20.0f).des("Y to offset the health bar").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Float> healthBarAbsorptionOffset = setting("HBAbsorptionYOffset", -1.1f, -20.0f, 20.0f).des("Y to offset the absorption bar from the health bar").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> healthBarColorShift = setting("HealthBarColorShift", true).des("Change health bar color as health goes down").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> healthBarAbsorptionColorShift = setting("HBAbsorptionColorShift", false).des("Change absorption bar color as absorption goes down").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> healthBarInterp = setting("HealthBarSmooth", true).des("Interpolates the health bar width as health changes instead of having the bar instantly snap to a new width when health changes").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Float> healthBarInterpFactor = setting("HBSmoothFactor", 20.0f, 0.1f, 30.0f).des("Speed of interpolation of health bar width").whenTrue(healthBarInterp).whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBar = setting("RoundedHB", false).des("Rounded health bar").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Float> roundedHealthBarRadius = setting("RoundedHBRadius", 1.0f, 0.0f, 1.0f).des("Radius of rounded health bar corners").whenTrue(healthBar).whenTrue(roundedHealthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarTopRight = setting("RoundedHBTopRight", true).whenTrue(healthBar).whenTrue(roundedHealthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarTopLeft = setting("RoundedHBTopLeft", true).whenTrue(healthBar).whenTrue(roundedHealthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarDownRight = setting("RoundedHBDownRight", true).whenTrue(healthBar).whenTrue(roundedHealthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarDownLeft = setting("RoundedHBDownLeft", true).whenTrue(healthBar).whenTrue(roundedHealthBar).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarAbsorption = setting("RoundedHBA", false).des("Rounded absorption bar").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Float> roundedHealthBarAbsorptionRadius = setting("RoundedHBARadius", 1.0f, 0.0f, 1.0f).des("Radius of rounded absorption bar corners").whenTrue(healthBar).whenTrue(roundedHealthBarAbsorption).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarAbsorptionTopRight = setting("RoundedHBATopRight", true).whenTrue(healthBar).whenTrue(roundedHealthBarAbsorption).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarAbsorptionTopLeft = setting("RoundedHBATopLeft", true).whenTrue(healthBar).whenTrue(roundedHealthBarAbsorption).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarAbsorptionDownRight = setting("RoundedHBADownRight", true).whenTrue(healthBar).whenTrue(roundedHealthBarAbsorption).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> roundedHealthBarAbsorptionDownLeft = setting("RoundedHBADownLeft", true).whenTrue(healthBar).whenTrue(roundedHealthBarAbsorption).whenAtMode(page, Page.HealthBar);
    Setting<Boolean> healthBarBorder = setting("HBBorder", false).des("Draw a border around the health bar").whenTrue(healthBar).whenAtMode(page, Page.HealthBar);
    Setting<Float> healthBarBorderWidth = setting("HBBorderWidth", 1.0f, 1.0f, 2.0f).des("BG health bar thickness").whenTrue(healthBar).whenTrue(healthBarBorder).whenAtMode(page, Page.HealthBar);
    Setting<Float> healthBarBorderOffset = setting("HBBorderOffset", 2.0f, 0.0f, 5.0f).des("BG health bar offset from rect").whenTrue(healthBar).whenTrue(healthBarBorder).whenAtMode(page, Page.HealthBar);

    Setting<Boolean> shadow = setting("RectShadow", true).des("Gradient BG rect shadow").whenAtMode(page, Page.RectEffects);
    Setting<Boolean> shadowCenterRect = setting("RectShadowCenterRect", false).des("Fill in the center of the rect shadow with a rect to match the outside gradient").whenTrue(shadow).whenAtMode(page, Page.RectEffects);
    Setting<Float> shadowSize = setting("RectShadowSize", 0.18f, 0.0f, 1.0f).des("Size of outside shadow gradient").whenTrue(shadow).whenAtMode(page, Page.RectEffects);
    Setting<Integer> shadowAlpha = setting("RectShadowAlpha", 33, 0, 255).des("Alpha of BG rect shadow").whenTrue(shadow).whenAtMode(page, Page.RectEffects);
    // * I WILL WORK ON THIS WHEN NAMETAGS GET REWRITTEN TO BE 2D
    //Setting<Boolean> blur = setting("Blur", false).des("!! TURN OFF FAST RENDER IN OPTIFINE !! Blurs the stuff rendered behind nametags").whenAtMode(page, Page.RectEffects);
    //Setting<Float> blurFactor = setting("BlurFactor", 1.0f, 0.0f, 2.0f).des("Blur intensity").whenTrue(blur).whenAtMode(page, Page.RectEffects);

    Setting<Boolean> players = setting("Players", true).des("Draw nametags on players").whenAtMode(page, Page.Entities);
    Setting<Boolean> mobs = setting("Mobs", false).des("Draw nametags on mobs").whenAtMode(page, Page.Entities);
    Setting<Boolean> animals = setting("Animals", false).des("Draw nametags on animals").whenAtMode(page, Page.Entities);
    Setting<Boolean> items = setting("Items", false).des("Draw nametags on dropped items").whenAtMode(page, Page.Entities);

    Setting<CustomFont.FontMode> font = setting("Font", CustomFont.FontMode.Comfortaa).whenAtMode(page, Page.Text);
    //Setting<Boolean> multiLineText = setting("MultilineText", false).des("Renders text better").whenAtMode(page, Page.Text);
    Setting<Float> textYOffset = setting("TextYOffset", 1.3f, 0.0f, 20.0f).des("Amount to offset the text vertically").whenAtMode(page, Page.Text);
    Setting<Float> textSpace = setting("TextSpace", 6.5f, 0.0f, 10.0f).des("Spaces between text").whenAtMode(page, Page.Text);
    Setting<Boolean> textShadow = setting("TextShadow", true).des("Draw shadow below text").whenAtMode(page, Page.Text);
    Setting<TextMode> ping = setting("Ping", TextMode.Left).des("Display ping on nametags").whenAtMode(page, Page.Text);
    Setting<TextMode> health = setting("Health", TextMode.Right).des("Display health on nametags").whenAtMode(page, Page.Text);
    public Setting<TextMode> popCount = setting("PopCount", TextMode.Right).des("Display recently popped totem count on nametags").whenAtMode(page, Page.Text);
    Setting<TextMode> itemCount = setting("ItemCount", TextMode.Left).whenTrue(items).whenAtMode(page, Page.Text);

    Setting<Integer> itemsOffsetY = setting("ItemsY", 33, 0, 50).des("Y offset of items").whenAtMode(page, Page.Items);
    Setting<Float> itemsScale = setting("ItemsScale", 0.6f, 0.1f, 2.0f).des("Scale of items").whenAtMode(page, Page.Items);
    Setting<Integer> separationDistItems = setting("ItemsSeparationDist", 20, 0, 50).des("Distance between each items").whenAtMode(page, Page.Items);
    Setting<Boolean> duraBar = setting("DurabilityBar", true).des("Draws a bar showing remaining durability for an item").whenAtMode(page, Page.Items);
    Setting<Float> duraBarOffsetY = setting("DuraBarY", -16.2f, -20.0f, 20.0f).des("Durability bar y offset").whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraColorShift = setting("DuraColorShift", true).des("Changes durability bar color as durability of item goes down").whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Integer> duraBarHeight = setting("DuraBarHeight", 1, 1, 20).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraBarRounded = setting("DuraBarRounded", false).des("Rounded rect for durability bar").whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Float> duraBarRoundedRadius = setting("DuraBarRoundedRadius", 0.6f, 0.0f, 1.0f).des("Radius of rounded durability bar").whenTrue(duraBarRounded).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraBarRoundedTopRight = setting("DuraBarRoundedTopRight", true).des("Rounded corner for rounded durability bar top right").whenTrue(duraBarRounded).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraBarRoundedTopLeft = setting("DuraBarRoundedTopLeft", true).des("Rounded corner for rounded durability bar top left").whenTrue(duraBarRounded).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraBarRoundedDownRight = setting("DuraBarRoundedDownRight", true).des("Rounded corner for rounded durability bar bottom right").whenTrue(duraBarRounded).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraBarRoundedDownLeft = setting("DuraBarRoundedDownLeft", true).des("Rounded corner for rounded durability bar bottom left").whenTrue(duraBarRounded).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Boolean> duraBarBordered = setting("DuraBarBordered", false).des("Bordered rect for durability bar").whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Float> duraBarBorderOffset = setting("DuraBarBorderOffset", 2.0f, 0.0f, 5.0f).des("Bordered durability bar outline offset").whenTrue(duraBarBordered).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<Float> duraBarBorderWidth = setting("DuraBarBorderWidth", 1.2f, 1.0f, 2.0f).des("Bordered durability bar outline width").whenTrue(duraBarBordered).whenTrue(duraBar).whenAtMode(page, Page.Items);
    Setting<ArmorDisplay.RenderMode> armorMode = setting("ArmorMode", ArmorDisplay.RenderMode.Image).des("Ways to render armor on nametags").whenAtMode(page, Page.Items);
    Setting<Integer> itemRectsWidth = setting("ItemRectsWidth", 16, 1, 30).des("Width of simplified rect render and damage bar for items").only(v -> armorMode.getValue() != ArmorDisplay.RenderMode.None || duraBar.getValue()).whenAtMode(page, Page.Items);
    Setting<Integer> armorRectHeight = setting("ArmorRectHeight", 7, 1, 20).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> armorRoundedRect = setting("ArmorRoundedRect", false).des("Rounded rect for non image render mode").whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Float> armorRoundedRectRadius = setting("ArmorRoundedRectRadius", 0.6f, 0.0f, 1.0f).des("Radius of rounded rect").whenTrue(armorRoundedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> armorRoundedRectTopRight = setting("ArmorRoundedRectTopRight", true).des("Rounded corner for rounded rect top right").whenTrue(armorRoundedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> armorRoundedRectTopLeft = setting("ArmorRoundedRectTopLeft", true).des("Rounded corner for rounded rect top left").whenTrue(armorRoundedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> armorRoundedRectDownRight = setting("ArmorRoundedRectDownRight", true).des("Rounded corner for rounded rect bottom right").whenTrue(armorRoundedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> armorRoundedRectDownLeft = setting("ArmorRoundedRectDownLeft", true).des("Rounded corner for rounded rect bottom left").whenTrue(armorRoundedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> armorBorderedRect = setting("ArmorBorderedRect", true).des("Bordered rect for non image render mode").whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Float> armorBorderedRectOffset = setting("ArmorBorderedRectOffset", 2.0f, 0.0f, 5.0f).des("Bordered rect outline offset").whenTrue(armorBorderedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Float> armorBorderedRectWidth = setting("ArmorBorderedRectWidth", 1.2f, 1.0f, 2.0f).des("Bordered rect outline width").whenTrue(armorBorderedRect).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Items);
    Setting<Boolean> heldItems = setting("HeldItems", true).des("Renders the items that the entity is holding").whenAtMode(page, Page.Items);
    Setting<Boolean> flipHeldItems = setting("FlipHeldItems", false).des("Makes offhand and mainhand render on opposite sides").whenTrue(heldItems).whenAtMode(page, Page.Items);
    Setting<Float> heldItemsOffsetX = setting("HeldItemsX", -0.75f, -20.0f, 20.0f).des("X offset of held items").whenTrue(heldItems).whenAtMode(page, Page.Items);
    Setting<Float> heldItemsOffsetY = setting("HeldItemsY", 0.75f, -40.0f, 40.0f).des("Y offset of held items").whenTrue(heldItems).whenAtMode(page, Page.Items);
    Setting<Boolean> heldItemsStackSize = setting("HeldItemsAmt", true).des("Shows the amount of items in the currently held stack").whenTrue(heldItems).whenAtMode(page, Page.Items);
    Setting<Float> heldItemsStackSizeOffsetX = setting("HeldItemsAmtX", 0.0f, -20.0f, 20.0f).des("X offset of held items stack size").whenTrue(heldItemsStackSize).whenTrue(heldItems).whenAtMode(page, Page.Items);
    Setting<Float> heldItemsStackSizeOffsetY = setting("HeldItemsAmtY", 0.0f, -40.0f, 40.0f).des("Y offset of held items stack size").whenTrue(heldItemsStackSize).whenTrue(heldItems).whenAtMode(page, Page.Items);
    Setting<Boolean> duraPercent = setting("DuraPercent", false).des("Renders the remaining durability of items as a percent").whenAtMode(page, Page.Items);
    Setting<Float> duraPercentOffsetX = setting("DuraPercentX", 4.5f, -20.0f, 20.0f).des("X offset of durability percentage").whenTrue(duraPercent).whenAtMode(page, Page.Items);
    Setting<Float> duraPercentOffsetY = setting("DuraPercentY", -4.5f, -20.0f, 20.0f).des("Y offset of durability percentage").whenTrue(duraPercent).whenAtMode(page, Page.Items);
    Setting<Float> duraPercentScale = setting("DuraPercentScale", 0.7f, 0.1f, 2.0f).des("Scale of durability percentage").whenTrue(duraPercent).whenAtMode(page, Page.Items);
    Setting<Boolean> enchants = setting("Enchantments", true).des("Renders all enchantments on each item").whenAtMode(page, Page.Items);
    Setting<Boolean> enchantRenderUp = setting("EnchantRenderUp", true).des("Renders multiple enchantments above each other").whenTrue(enchants).whenAtMode(page, Page.Items);
    Setting<Float> enchantSeparationOffset = setting("EnchantSeparationOffset", 7.6f, 0.1f, 20.0f).des("Distance between each enchantment").whenTrue(enchants).whenAtMode(page, Page.Items);
    Setting<Float> enchantScale = setting("EnchantScale", 0.78f, 0.1f, 2.0f).des("Size of item enchantments").whenTrue(enchants).whenAtMode(page, Page.Items);
    Setting<Float> enchantOffsetX = setting("EnchantmentsX", 1.9f, -20.0f, 20.0f).des("X offset of item enchantments").whenTrue(enchants).whenAtMode(page, Page.Items);
    Setting<Float> enchantOffsetY = setting("EnchantmentsY", 3.4f, -20.0f, 20.0f).des("Y offset of item enchantments").whenTrue(enchants).whenAtMode(page, Page.Items);
    Setting<Boolean> heldItemName = setting("HeldItemName", true).des("Draw the name of held item as a string").whenAtMode(page, Page.Items);
    Setting<Float> heldItemNameOffsetX = setting("HeldItemNameX", 1.9f, -50.0f, 50.0f).des("X offset of held item name").whenTrue(heldItemName).whenAtMode(page, Page.Items);
    Setting<Float> heldItemNameOffsetY = setting("HeldItemNameY", -22.6f, -40.0f, 40.0f).des("Y offset of held item name").whenTrue(heldItemName).whenAtMode(page, Page.Items);
    Setting<Float> heldItemNameScale = setting("HeldItemNameScale", 0.8f, 0.1f, 2.0f).des("Scale of held item name").whenTrue(heldItemName).whenAtMode(page, Page.Items);
    Setting<Boolean> itemsTextShadow = setting("ItemsTextShadow", true).des("Draw text shadow on items text").only(v -> enchants.getValue() || duraPercent.getValue() || heldItemName.getValue()).whenAtMode(page, Page.Items);

    Setting<Color> bgRectColor = setting("BGRectColor", new Color(new java.awt.Color(0, 0, 0, 50).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 0, 0, 0, 50)).whenAtMode(page, Page.Colors);
    Setting<Color> bgRectBorderColor = setting("BGRectBorderColor", new Color(new java.awt.Color(20, 20, 150, 100).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 20, 20, 150, 100)).whenTrue(rectBorder).whenAtMode(page, Page.Colors);
    Setting<Color> nameColor = setting("NameColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).whenAtMode(page, Page.Colors);
    Setting<Color> friendColor = setting("FriendColor", new Color(new java.awt.Color(50, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 255, 255)).whenAtMode(page, Page.Colors);
    Setting<Color> enemyColor = setting("EnemyColor", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).whenAtMode(page, Page.Colors);
    Setting<Color> crouchColor = setting("CrouchColor", new Color(new java.awt.Color(255, 150, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 150, 50, 255)).whenAtMode(page, Page.Colors);
    Setting<Color> pingColorGood = setting("PingColorGood", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> ping.getValue() != TextMode.None).whenAtMode(page, Page.Colors);
    Setting<Color> pingColorBad = setting("PingColorBad", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> ping.getValue() != TextMode.None).whenAtMode(page, Page.Colors);
    Setting<Color> healthColorMax = setting("HealthColorMax", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> health.getValue() != TextMode.None).whenAtMode(page, Page.Colors);
    Setting<Color> healthColorDead = setting("HealthColorDead", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> health.getValue() != TextMode.None).whenAtMode(page, Page.Colors);
    Setting<Color> popColor = setting("PopColor", new Color(new java.awt.Color(175, 80, 80, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 175, 80, 80, 255)).only(v -> popCount.getValue() != TextMode.None).whenAtMode(page, Page.Colors);
    Setting<Color> itemCountColor = setting("ItemCountColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).whenTrue(items).only(v -> itemCount.getValue() != TextMode.None).whenAtMode(page, Page.Colors);
    Setting<Color> healthBarColor = setting("HealthBarColor", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).whenTrue(healthBar).whenAtMode(page, Page.Colors);
    Setting<Color> healthBar2Color = setting("HealthBar2Color", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).whenTrue(healthBarColorShift).whenTrue(healthBar).whenAtMode(page, Page.Colors);
    Setting<Color> healthBarAbsorptionColor = setting("HBAbsorptionColor", new Color(new java.awt.Color(255, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 50, 255)).whenTrue(healthBar).whenAtMode(page, Page.Colors);
    Setting<Color> healthBar2AbsorptionColor = setting("HBAbsorption2Color", new Color(new java.awt.Color(100, 100, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 100, 50, 255)).whenTrue(healthBarAbsorptionColorShift).whenTrue(healthBar).whenAtMode(page, Page.Colors);
    Setting<Color> duraColor = setting("DuraColor", new Color(new java.awt.Color(50, 255, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 255)).only(v -> duraBar.getValue() || duraPercent.getValue()).whenAtMode(page, Page.Colors);
    Setting<Color> duraColor2 = setting("DuraColor2", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).only(v -> duraBar.getValue() || duraPercent.getValue()).whenTrue(duraColorShift).whenAtMode(page, Page.Colors);
    Setting<Color> duraBarBGColor = setting("DuraBarBGColor", new Color(new java.awt.Color(20, 20, 20, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 20, 20, 20, 255)).whenTrue(duraBar).whenAtMode(page, Page.Colors);
    Setting<Color> heldItemNameColor = setting("HeldItemNameColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).whenTrue(heldItemName).whenAtMode(page, Page.Colors);
    Setting<Color> enchantmentColor = setting("EnchantmentTextColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).whenTrue(enchants).whenAtMode(page, Page.Colors);
    Setting<Color> enchantmentCurseColor = setting("EnchantmentCurseTextColor", new Color(new java.awt.Color(255, 50, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 255)).whenTrue(enchants).whenAtMode(page, Page.Colors);
    Setting<Color> heldItemStackSizeColor = setting("HeldItemAmtColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).whenTrue(heldItems).whenTrue(heldItemsStackSize).whenAtMode(page, Page.Colors);

    Setting<Color> elytraColor = setting("ElytraColor", new Color(new java.awt.Color(150, 100, 150, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 150, 100, 150, 255)).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Colors);
    Setting<Color> diamondColor = setting("DiamondColor", new Color(new java.awt.Color(53, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 53, 175, 175, 255)).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Colors);
    Setting<Color> ironColor = setting("IronColor", new Color(new java.awt.Color(200, 200, 200, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 200, 255)).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Colors);
    Setting<Color> goldColor = setting("GoldColor", new Color(new java.awt.Color(200, 200, 50, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 200, 200, 50, 255)).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Colors);
    Setting<Color> chainMailColor = setting("ChainmailColor", new Color(new java.awt.Color(150, 150, 150, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 150, 150, 150, 255)).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Colors);
    Setting<Color> leatherColor = setting("LeatherColor", new Color(new java.awt.Color(110, 75, 0, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 110, 75, 0, 255)).whenAtMode(armorMode, ArmorDisplay.RenderMode.Simplified).whenAtMode(page, Page.Colors);


    //see MixinRenderPlayer for canceling vanilla nametags rendering

    public Nametags() {
        INSTANCE = this;
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        EntityUtil.entitiesListFlag = true;
        mc.world.loadedEntityList.stream()
                .filter(e -> !(e == mc.player || e.getName().equals(mc.player.getName())))
                .filter(e -> !(EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, e) > range.getValue()))
                .filter(e -> !e.isDead)
                .filter(e -> RenderHelper.isInViewFrustrum(e.getEntityBoundingBox().offset(0.0, yOffset.getValue(), 0.0)))
                .sorted(Comparator.comparing(entity -> -mc.getRenderViewEntity().getDistance(entity)))
                .forEach(entity -> {
                    if (healthBar.getValue() && healthBarInterp.getValue() && (float) EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.player, entity) > range.getValue()) {
                        prevHealthMap.remove(entity);
                        interpProgressMap.remove(entity);
                        prevHealthAbsorptionMap.remove(entity);
                        interpProgressAbsorptionMap.remove(entity);
                    }

                    if (entity instanceof EntityPlayer && players.getValue()) {
                        Integer pops = ((EntityPlayer) entity).getHealth() <= 0.0f ? PopManager.deathPopMap.get(entity) : PopManager.popMap.get(entity);

                        //idfk why intellij says thats its never null but when using nametags on fakeplayer it returns null so :shrug:
                        drawNametag(entity,
                                entity.getName(),
                                mc.getConnection().getPlayerInfo(entity.getUniqueID()) == null ? 0
                                        : mc.getConnection().getPlayerInfo(entity.getUniqueID()).getResponseTime(),
                                ((EntityLivingBase) entity).getHealth(),
                                pops == null ? 0 : pops,
                                ((EntityPlayer) entity).getAbsorptionAmount(),
                                false,
                                false);
                    }

                    if ((EntityUtil.isEntityMob(entity) && mobs.getValue()) || (EntityUtil.isEntityAnimal(entity) && animals.getValue())) {
                        drawNametag(entity,
                                entity.getName(),
                                0,
                                ((EntityLivingBase) entity).getHealth(),
                                0,
                                0.0f,
                                true,
                                false);
                    }

                    if (entity instanceof EntityItem && items.getValue()) {
                        drawNametag(entity,
                                entity.getName(),
                                0,
                                0.0f,
                                0,
                                0.0f,
                                false,
                                true);
                    }
                });
        EntityUtil.entitiesListFlag = false;

        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glDisable(GL_ALPHA_TEST);

        if (healthBar.getValue() && healthBarInterp.getValue() && !interpProgressMap.isEmpty()) {
            int passedms = (int) timer.hasPassed();
            if (passedms < 1000) {
                for (Map.Entry<Entity, Integer> entry : interpProgressMap.entrySet()) {
                    int i = entry.getValue();
                    i += (healthBarInterpFactor.getValue() / 10.0f) * passedms;

                    if (i > 300) {
                        i = 300;
                    }

                    interpProgressMap.put(entry.getKey(), i);
                }

                for (Map.Entry<Entity, Integer> entry : interpProgressAbsorptionMap.entrySet()) {
                    int i = entry.getValue();
                    i += (healthBarInterpFactor.getValue() / 10.0f) * passedms;

                    if (i > 300) {
                        i = 300;
                    }

                    interpProgressAbsorptionMap.put(entry.getKey(), i);
                }
            }
            timer.reset();
        }
    }

    private void drawNametag(Entity entity, String name, int ping, float health, int pops, float absorption, boolean isNonPlayerLiving, boolean isItem) {
        Vec3d entityVec = EntityUtil.getInterpolatedEntityPos(entity, mc.getRenderPartialTicks());
        float dist = (float) EntityUtil.getInterpDistance(mc.getRenderPartialTicks(), mc.renderViewEntity, entity);
        float scale = 0.0018f + size.getValue() + dist * theFuckingScaleIllFixThisLator;
        float roundedHealth = (float) (Math.ceil((health + absorption) * 2.0f) / 2.0f);

        if (dist < innerLockRange.getValue()) {
            scale = 0.0018f + size.getValue() + innerLockRange.getValue() * theFuckingScaleIllFixThisLator;
        }
        else if (dist > outerLockRange.getValue()) {
            scale = 0.0018f + size.getValue() + outerLockRange.getValue() * theFuckingScaleIllFixThisLator;
        }

        float spaceIndex = 0.0f;

        if (this.ping.getValue() != TextMode.None) spaceIndex++;
        if (this.health.getValue() != TextMode.None) spaceIndex++;
        if (this.popCount.getValue() != TextMode.None) spaceIndex++;

        float width = (spaceIndex * textSpace.getValue()) + getWidthNametags(name + (this.ping.getValue() != TextMode.None ? ping + " ms" : "") + (this.health.getValue() != TextMode.None ? (roundedHealth + "") : "") + (this.popCount.getValue() != TextMode.None ? "[" + pops + "]" : ""));

        if (isNonPlayerLiving) {
            width = (this.health.getValue() != TextMode.None ? textSpace.getValue() : 0) + getWidthNametags(name + (this.health.getValue() != TextMode.None ? (roundedHealth + "") : ""));
        }

        if (isItem) {
            width = (this.itemCount.getValue() != TextMode.None ? textSpace.getValue() : 0) + getWidthNametags(((EntityItem) entity).getItem().getDisplayName() + (this.itemCount.getValue() != TextMode.None ? ("x" + ((EntityItem) entity).getItem().stackSize) : ""));
        }

        float xOffsetRect = -width / 2.0f;

        GL11.glPushMatrix();
        CrystalUtil.glBillboard((float) entityVec.x, (float)((entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) + entityVec.y + yOffset.getValue()), (float) entityVec.z);
        GL11.glTranslatef(0.0f, rectHeight.getValue() * (1.0f - scale), 0.0f);
        GL11.glScalef(scale, scale, scale);

        RenderUtils2D.prepareGl();
        if (shadow.getValue()) {
            RenderUtils2D.drawBetterRoundRectFade(xOffsetRect - rectWidth.getValue(), 0, xOffsetRect + width + rectWidth.getValue(), rectHeight.getValue(), shadowSize.getValue(), 30.0f, false, shadowCenterRect.getValue(), false, new java.awt.Color(0, 0, 0, shadowAlpha.getValue()).getRGB());
        }

        drawRects(entity, xOffsetRect, width, ping, health, pops, absorption);
        RenderUtils2D.releaseGl();

        java.awt.Color color = FriendManager.isFriend(entity) ? friendColor.getValue().getColorColor() : EnemyManager.isEnemy(entity) ? enemyColor.getValue().getColorColor() : nameColor.getValue().getColorColor();

        if (entity.isSneaking()) {
            color = crouchColor.getValue().getColorColor();
        }

        float yOffset = (rectHeight.getValue() / 2.0f) - textYOffset.getValue();
        float xOffset = ((this.popCount.getValue() == TextMode.Left && !isNonPlayerLiving && !isItem ? getWidthNametags("[" + pops + "]") + textSpace.getValue() : 0) + (this.health.getValue() == TextMode.Left && !isItem ? getWidthNametags(roundedHealth + "") + textSpace.getValue() : 0) + (this.ping.getValue() == TextMode.Left && !isNonPlayerLiving && !isItem ? getWidthNametags(ping + " ms") + textSpace.getValue() : 0) + (itemCount.getValue() == TextMode.Left && isItem ? getWidthNametags("x" + ((EntityItem) entity).getItem().stackSize) + textSpace.getValue() : 0));
        xOffset -= ((isNonPlayerLiving ? (this.health.getValue() != TextMode.None ? textSpace.getValue() : 0) : isItem ? (itemCount.getValue() != TextMode.None ? textSpace.getValue() : 0) : (spaceIndex * textSpace.getValue())) + getWidthNametags((isItem ? ((EntityItem) entity).getItem().getDisplayName() : name) + (this.ping.getValue() != TextMode.None && !isNonPlayerLiving && !isItem ? ping + " ms" : "") + (this.health.getValue() != TextMode.None && !isItem ? (roundedHealth + "") : "") + (this.popCount.getValue() != TextMode.None && !isNonPlayerLiving && !isItem ? "[" + pops + "]" : "") + (itemCount.getValue() != TextMode.None && isItem ? "x" + ((EntityItem) entity).getItem().stackSize : ""))) / 2.0f;

        drawItems(entity, isItem);

        if (itemCount.getValue() == TextMode.Left && isItem) drawString("x" + ((EntityItem) entity).getItem().stackSize, xOffset - textSpace.getValue() - getWidthNametags("x" + ((EntityItem) entity).getItem().stackSize), yOffset, textShadow.getValue(), itemCountColor.getValue().getColor());
        if (this.popCount.getValue() == TextMode.Left && !isNonPlayerLiving && !isItem) drawString("[" + pops + "]", xOffset + (this.health.getValue() == TextMode.Left ? -getWidthNametags(roundedHealth + "") - textSpace.getValue() : 0) - (this.ping.getValue() == TextMode.Left ? getWidthNametags(ping + " ms") + textSpace.getValue() : 0) - textSpace.getValue() - getWidthNametags("[" + pops + "]"), yOffset, textShadow.getValue(), popColor.getValue().getColor());
        if (this.health.getValue() == TextMode.Left && !isItem) drawString(roundedHealth + "", xOffset + (this.ping.getValue() == TextMode.Left ? -getWidthNametags(ping + " ms") - textSpace.getValue() : 0) - textSpace.getValue() - getWidthNametags(roundedHealth + ""), yOffset, textShadow.getValue(), ColorUtil.colorShift(healthColorDead.getValue().getColorColor(), healthColorMax.getValue().getColorColor(), (health / ((EntityLivingBase) entity).getMaxHealth()) * 300.0f).getRGB());
        if (this.ping.getValue() == TextMode.Left && !isNonPlayerLiving && !isItem) drawString(ping + " ms", xOffset - textSpace.getValue() - getWidthNametags(ping + " ms"), yOffset, textShadow.getValue(), ColorUtil.colorShift(pingColorGood.getValue().getColorColor(), pingColorBad.getValue().getColorColor(), MathUtilFuckYou.clamp(ping / 250.0f, 0.0f, 1.0f) * 300.0f).getRGB());
        drawString(isItem ? ((EntityItem) entity).getItem().getDisplayName() : name, xOffset, yOffset, textShadow.getValue(), color.getRGB());
        if (this.ping.getValue() == TextMode.Right && !isNonPlayerLiving && !isItem) drawString(ping + " ms", xOffset + getWidthNametags(name) + textSpace.getValue(), yOffset, textShadow.getValue(), ColorUtil.colorShift(pingColorGood.getValue().getColorColor(), pingColorBad.getValue().getColorColor(), MathUtilFuckYou.clamp(ping / 250.0f, 0.0f, 1.0f) * 300.0f).getRGB());
        if (this.health.getValue() == TextMode.Right && !isItem) drawString(roundedHealth + "", xOffset + (this.ping.getValue() == TextMode.Right ? getWidthNametags(ping + " ms") + textSpace.getValue() : 0) + getWidthNametags(name) + textSpace.getValue(), yOffset, textShadow.getValue(), ColorUtil.colorShift(healthColorDead.getValue().getColorColor(), healthColorMax.getValue().getColorColor(), (health / ((EntityLivingBase) entity).getMaxHealth()) * 300.0f).getRGB());
        if (this.popCount.getValue() == TextMode.Right && !isNonPlayerLiving && !isItem) drawString("[" + pops + "]", xOffset + (this.health.getValue() == TextMode.Right ? getWidthNametags(roundedHealth + "") + textSpace.getValue() : 0) + (this.ping.getValue() == TextMode.Right ? getWidthNametags(ping + " ms") + textSpace.getValue() : 0) + getWidthNametags(name) + textSpace.getValue(), yOffset, textShadow.getValue(), popColor.getValue().getColor());
        if (itemCount.getValue() == TextMode.Right && isItem) drawString("x" + ((EntityItem) entity).getItem().stackSize, xOffset + getWidthNametags(((EntityItem) entity).getItem().getDisplayName()) + textSpace.getValue(), yOffset, textShadow.getValue(), itemCountColor.getValue().getColor());

        GL11.glScalef(1.0f / scale, 1.0f / scale, 1.0f / scale);
        GL11.glTranslatef(0.0f, rectHeight.getValue() * -(1.0f - scale), 0.0f);
        GL11.glPopMatrix();
        GlStateManager.enableBlend();
    }

    private void drawRects(Entity entity, float startX, float width, float ping, float health, int pops, float absorption) {
        drawRect(roundedRect.getValue(), roundedRectTopRight.getValue(), roundedRectTopLeft.getValue(), roundedRectDownRight.getValue(), roundedRectDownLeft.getValue(), roundedRectRadius.getValue(),
                rectBorder.getValue(), rectBorderWidth.getValue(), rectBorderOffset.getValue(),
                startX - rectWidth.getValue(), 0, width + (rectWidth.getValue() * 2.0f), rectHeight.getValue(), bgRectColor.getValue().getColor(), bgRectBorderColor.getValue().getColor());

        if (healthBar.getValue() && entity instanceof EntityLivingBase) {
            java.awt.Color hbColor = healthBarColor.getValue().getColorColor();
            java.awt.Color hbAbsorptionColor = healthBarAbsorptionColor.getValue().getColorColor();
            float healthBarFactor = health / ((EntityLivingBase) entity).getMaxHealth();
            float absorptionBarFactor = MathUtilFuckYou.clamp(absorption / 16.0f, 0.0f, 1.0f);

            if (healthBarInterp.getValue()) {
                if (interpProgressMap.get(entity) != null && prevHealthMap.get(entity) != null) {
                    healthBarFactor = MathUtilFuckYou.clamp(prevHealthMap.get(entity) + (interpProgressMap.get(entity) * ((healthBarFactor - prevHealthMap.get(entity)) / 300.0f)),
                            0.0f,
                            1.0f);
                }

                if (interpProgressAbsorptionMap.get(entity) != null && prevHealthAbsorptionMap.get(entity) != null) {
                    absorptionBarFactor = MathUtilFuckYou.clamp(prevHealthAbsorptionMap.get(entity) + (interpProgressAbsorptionMap.get(entity) * ((absorptionBarFactor - prevHealthAbsorptionMap.get(entity)) / 300.0f)),
                        0.0f,
                        1.0f);
                }

                if ((healthBarFactor * (width + (rectWidth.getValue() * 2.0f))) != (prevHealthMap.get(entity) == null ? 99999 : prevHealthMap.get(entity))) {
                    interpProgressMap.put(entity, 0);
                    prevHealthMap.put(entity, healthBarFactor);
                }

                if ((absorptionBarFactor * (width + (rectWidth.getValue() * 2.0f))) != (prevHealthAbsorptionMap.get(entity) == null ? 99999 : prevHealthAbsorptionMap.get(entity))) {
                    interpProgressAbsorptionMap.put(entity, 0);
                    prevHealthAbsorptionMap.put(entity, absorptionBarFactor);
                }
            }

            float healthBarWidth = healthBarFactor * (width + (rectWidth.getValue() * 2.0f));
            float absorptionBarWidth = absorptionBarFactor * (width + (rectWidth.getValue() * 2.0f));

            if (healthBarColorShift.getValue()) {
                hbColor = ColorUtil.colorShift(healthBar2Color.getValue().getColorColor(), hbColor, healthBarFactor * 300.0f);
            }

            if (healthBarAbsorptionColorShift.getValue()) {
                hbAbsorptionColor = ColorUtil.colorShift(healthBar2AbsorptionColor.getValue().getColorColor(), hbAbsorptionColor, absorptionBarFactor * 300.0f);
            }

            if (absorptionBarWidth > 0.0f) {
                drawRect(roundedHealthBarAbsorption.getValue(), roundedHealthBarAbsorptionTopRight.getValue(), roundedHealthBarAbsorptionTopLeft.getValue(), roundedHealthBarAbsorptionDownRight.getValue(), roundedHealthBarAbsorptionDownLeft.getValue(), roundedHealthBarAbsorptionRadius.getValue(), healthBarBorder.getValue(), healthBarBorderWidth.getValue(), healthBarBorderOffset.getValue(), startX - rectWidth.getValue(), rectHeight.getValue() - healthBarOffset.getValue() - healthBarAbsorptionOffset.getValue(), absorptionBarWidth, healthBarThickness.getValue(), hbAbsorptionColor.getRGB(), hbAbsorptionColor.getRGB());
            }
            drawRect(roundedHealthBar.getValue(), roundedHealthBarTopRight.getValue(), roundedHealthBarTopLeft.getValue(), roundedHealthBarDownRight.getValue(), roundedHealthBarDownLeft.getValue(), roundedHealthBarRadius.getValue(), healthBarBorder.getValue(), healthBarBorderWidth.getValue(), healthBarBorderOffset.getValue(), startX - rectWidth.getValue(), rectHeight.getValue() - healthBarOffset.getValue(), healthBarWidth, healthBarThickness.getValue(), hbColor.getRGB(), hbColor.getRGB());
        }
    }

    private void drawRect(boolean rounded, boolean arcTopRight, boolean arcTopLeft, boolean arcDownRight, boolean arcDownLeft, float roundedRadiusFactor,
                          boolean bordered, float borderWidth, float borderOffset,
                          float x, float y, float width, float height, int color, int borderColor) {
        if (bordered) {
            if (rounded) {
                RenderUtils2D.drawCustomRoundedRectOutline(x, y, x + width, y + height, roundedRadiusFactor, borderWidth, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, false, false, borderColor);
                RenderUtils2D.drawRoundedRect(x + (borderOffset / 2.0f), y + (borderOffset / 2.0f), roundedRadiusFactor, x + width - (borderOffset / 2.0f), y + height - (borderOffset / 2.0f), false, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, color);
            }
            else {
                RenderUtils2D.drawRectOutline(x, y, x + width, y + height, borderWidth, borderColor, false, false);
                RenderUtils2D.drawRect(x + (borderOffset / 2.0f), y + (borderOffset / 2.0f), x + width - (borderOffset / 2.0f), y + height - (borderOffset / 2.0f), color);
            }
        }
        else {
            if (rounded) {
                RenderUtils2D.drawRoundedRect(x, y, roundedRadiusFactor, x + width, y + height, false, arcTopRight, arcTopLeft, arcDownRight, arcDownLeft, color);
            }
            else {
                RenderUtils2D.drawRect(x, y, x + width, y + height, color);
            }
        }
    }

    private void drawString(String str, float x, float y, boolean shadow, int color) {
        if (font.getValue() == CustomFont.FontMode.Minecraft) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GL11.glEnable(GL_TEXTURE_2D);
            mc.fontRenderer.drawString(str, x, y, color, shadow);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            if (shadow) {
                switch (font.getValue()) {
                    case Objectivity: {
                        FontManager.fontObjectivityRenderer.drawStringWithShadow(str, x, y, color);
                        break;
                    }

                    case Comfortaa: {
                        FontManager.fontRenderer.drawStringWithShadow(str, x, y, color);
                        break;
                    }

                    case Arial: {
                        FontManager.fontArialRenderer.drawStringWithShadow(str, x, y - 2.0f, color);
                        break;
                    }
                }
            }
            else {
                switch (font.getValue()) {
                    case Objectivity: {
                        FontManager.fontObjectivityRenderer.drawString(str, x, y, color);
                        break;
                    }

                    case Comfortaa: {
                        FontManager.fontRenderer.drawString(str, x, y, color);
                        break;
                    }

                    case Arial: {
                        FontManager.fontArialRenderer.drawString(str, x, y - 2.0f, color);
                        break;
                    }
                }
            }
        }
    }

    private void drawItems(Entity entity, boolean isItem) {
        if (!(entity instanceof EntityLivingBase) || isItem || (armorMode.getValue() == ArmorDisplay.RenderMode.None && !heldItems.getValue() && !enchants.getValue() && !duraPercent.getValue() && !duraBar.getValue()))
            return;

        GlStateManager.depthMask(true);
        GL11.glScalef(itemsScale.getValue(), itemsScale.getValue(), itemsScale.getValue());

        GL11.glTranslatef(-separationDistItems.getValue() * 2.0f, 0.0f, 0.0f);
        int x = separationDistItems.getValue() * 3;
        for (ItemStack armorItem : entity.getArmorInventoryList()) {
            if (armorItem.getItem() != Items.AIR) {
                switch (armorMode.getValue()) {
                    case Image: {
                        GL11.glEnable(GL_TEXTURE_2D);
                        GlStateManager.enableDepth();
                        GL11.glTranslatef(0.0f, 0.0f, -150.5f);
                        GL11.glDepthRange(0.0, 0.01);
                        mc.getRenderItem().renderItemAndEffectIntoGUI(armorItem, x, -itemsOffsetY.getValue());
                        GL11.glDepthRange(0.0, 1.0);
                        GL11.glTranslatef(0.0f, 0.0f, 150.5f);
                        GlStateManager.disableDepth();
                        GL11.glDisable(GL_TEXTURE_2D);
                        break;
                    }

                    case Simplified: {
                        java.awt.Color color = ArmorDisplay.INSTANCE.getSimplifiedArmorColor(armorItem.getItem(), leatherColor.getValue(), chainMailColor.getValue(), goldColor.getValue(), ironColor.getValue(), diamondColor.getValue(), elytraColor.getValue());
                        GL11.glDisable(GL_TEXTURE_2D);

                        RenderUtils2D.prepareGl();
                        drawRect(armorRoundedRect.getValue(), armorRoundedRectTopRight.getValue(), armorRoundedRectTopLeft.getValue(), armorRoundedRectDownRight.getValue(), armorRoundedRectDownLeft.getValue(), armorRoundedRectRadius.getValue(), armorBorderedRect.getValue(), armorBorderedRectWidth.getValue(), armorBorderedRectOffset.getValue(),
                                x, -itemsOffsetY.getValue(), itemRectsWidth.getValue(), armorRectHeight.getValue(), color.getRGB(), color.getRGB());
                        RenderUtils2D.releaseGl();
                        break;
                    }
                }

                if (duraBar.getValue()) {
                    renderDMGBar(armorItem, x, -duraBarOffsetY.getValue() - itemsOffsetY.getValue());
                }

                if (duraPercent.getValue()) {
                    renderDMGPercentage(armorItem, x + duraPercentOffsetX.getValue(), -itemsOffsetY.getValue() - duraPercentOffsetY.getValue());
                }

                if (enchants.getValue()) {
                    renderEnchantments(armorItem, x + enchantOffsetX.getValue(), -itemsOffsetY.getValue() - enchantOffsetY.getValue(), enchantRenderUp.getValue());
                }
            }
            x -= separationDistItems.getValue();
        }

        if (heldItems.getValue()) {
            ItemStack mainItem = ((EntityLivingBase) entity).getHeldItemMainhand();
            ItemStack offItem = ((EntityLivingBase) entity).getHeldItemOffhand();

            GL11.glEnable(GL_TEXTURE_2D);
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = -150.5f;
            GL11.glDepthRange(0.0, 0.01);
            if (flipHeldItems.getValue()) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(mainItem, (int)(separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()), (int)(-itemsOffsetY.getValue() - heldItemsOffsetY.getValue()));
                mc.getRenderItem().renderItemAndEffectIntoGUI(offItem, (int)(x - heldItemsOffsetX.getValue()), (int)(-itemsOffsetY.getValue() - heldItemsOffsetY.getValue()));
            }
            else {
                mc.getRenderItem().renderItemAndEffectIntoGUI(mainItem, (int)(x - heldItemsOffsetX.getValue()), (int)(-itemsOffsetY.getValue() - heldItemsOffsetY.getValue()));
                mc.getRenderItem().renderItemAndEffectIntoGUI(offItem, (int)(separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()), (int)(-itemsOffsetY.getValue() - heldItemsOffsetY.getValue()));
            }
            GL11.glDepthRange(0.0, 1.0);
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.disableDepth();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL_TEXTURE_2D);

            if (duraBar.getValue()) {
                if (mainItem.isItemStackDamageable()) {
                    renderDMGBar(mainItem, flipHeldItems.getValue() ? (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()) : (x - heldItemsOffsetX.getValue()), -duraBarOffsetY.getValue() - itemsOffsetY.getValue() - heldItemsOffsetY.getValue());
                }

                if (offItem.isItemStackDamageable()) {
                    renderDMGBar(offItem, flipHeldItems.getValue() ? (x - heldItemsOffsetX.getValue()) : (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()), -duraBarOffsetY.getValue() - itemsOffsetY.getValue() - heldItemsOffsetY.getValue());
                }
            }

            if (duraPercent.getValue()) {
                if (mainItem.isItemStackDamageable()) {
                    renderDMGPercentage(mainItem, (flipHeldItems.getValue() ? (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()) : (x - heldItemsOffsetX.getValue())) + duraPercentOffsetX.getValue(), -itemsOffsetY.getValue() - duraPercentOffsetY.getValue() - heldItemsOffsetY.getValue());
                }

                if (offItem.isItemStackDamageable()) {
                    renderDMGPercentage(offItem, (flipHeldItems.getValue() ? (x - heldItemsOffsetX.getValue()) : (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue())) + duraPercentOffsetX.getValue(), -itemsOffsetY.getValue() - duraPercentOffsetY.getValue() - heldItemsOffsetY.getValue());
                }
            }

            if (enchants.getValue()) {
                renderEnchantments(mainItem, (flipHeldItems.getValue() ? (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()) : (x - heldItemsOffsetX.getValue())) + enchantOffsetX.getValue(), -itemsOffsetY.getValue() - enchantOffsetY.getValue() - heldItemsOffsetY.getValue(), enchantRenderUp.getValue());
                renderEnchantments(offItem, (flipHeldItems.getValue() ? (x - heldItemsOffsetX.getValue()) : (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue())) + enchantOffsetX.getValue(), -itemsOffsetY.getValue() - enchantOffsetY.getValue() - heldItemsOffsetY.getValue(), enchantRenderUp.getValue());
            }

            if (heldItemsStackSize.getValue()) {
                if (mainItem.stackSize > 1 && mainItem.getItem() != Items.AIR) {
                    drawString(mainItem.stackSize + "", (flipHeldItems.getValue() ? (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue()) : (x - heldItemsOffsetX.getValue())) + heldItemsStackSizeOffsetX.getValue(), -itemsOffsetY.getValue() - heldItemsStackSizeOffsetY.getValue(), itemsTextShadow.getValue(), heldItemStackSizeColor.getValue().getColor());
                }

                if (offItem.stackSize > 1 && offItem.getItem() != Items.AIR) {
                    drawString(offItem.stackSize + "", (flipHeldItems.getValue() ? (x - heldItemsOffsetX.getValue()) : (separationDistItems.getValue() * 4 + heldItemsOffsetX.getValue())) + heldItemsStackSizeOffsetX.getValue(), -itemsOffsetY.getValue() - heldItemsStackSizeOffsetY.getValue(), itemsTextShadow.getValue(), heldItemStackSizeColor.getValue().getColor());
                }
            }
        }

        GL11.glTranslatef(separationDistItems.getValue() * 2.0f, 0.0f, 0.0f);

        if (heldItemName.getValue()) {
            GL11.glTranslatef((heldItemNameOffsetX.getValue() - (getWidthNametags(((EntityLivingBase) entity).getHeldItemMainhand().getDisplayName()) * heldItemNameScale.getValue() / 2.0f)) * (1.0f - heldItemNameScale.getValue()), (-itemsOffsetY.getValue() - heldItemNameOffsetY.getValue()) * (1.0f - heldItemNameScale.getValue()), 0.0f);
            GL11.glScalef(heldItemNameScale.getValue(), heldItemNameScale.getValue(), heldItemNameScale.getValue());
            drawString((((EntityLivingBase) entity).getHeldItemMainhand().getItem() != Items.AIR) ? ((EntityLivingBase) entity).getHeldItemMainhand().getDisplayName() : "", heldItemNameOffsetX.getValue() - (getWidthNametags(((EntityLivingBase) entity).getHeldItemMainhand().getDisplayName()) * heldItemNameScale.getValue() / 2.0f), -itemsOffsetY.getValue() - heldItemNameOffsetY.getValue(), itemsTextShadow.getValue(), heldItemNameColor.getValue().getColor());
            GL11.glScalef(1.0f / heldItemNameScale.getValue(), 1.0f / heldItemNameScale.getValue(), 1.0f / heldItemNameScale.getValue());
            GL11.glTranslatef((heldItemNameOffsetX.getValue() - (getWidthNametags(((EntityLivingBase) entity).getHeldItemMainhand().getDisplayName()) * heldItemNameScale.getValue() / 2.0f)) * -(1.0f - heldItemNameScale.getValue()), (-itemsOffsetY.getValue() - heldItemNameOffsetY.getValue()) * -(1.0f - heldItemNameScale.getValue()), 0.0f);
        }

        GL11.glScalef(1.0f / itemsScale.getValue(), 1.0f / itemsScale.getValue(), 1.0f / itemsScale.getValue());
        GlStateManager.depthMask(false);
    }

    private void renderDMGPercentage(ItemStack itemStack, float x, float y) {
        java.awt.Color dBColor = duraColor.getValue().getColorColor();
        if (duraColorShift.getValue()) {
            dBColor = ColorUtil.colorShift(duraColor2.getValue().getColorColor(), duraColor.getValue().getColorColor(), ArmorDisplay.INSTANCE.getItemDMG(itemStack) * 300.0f);
        }

        GL11.glTranslatef(x * (1.0f - duraPercentScale.getValue()), y * (1.0f - duraPercentScale.getValue()), 0.0f);
        GL11.glScalef(duraPercentScale.getValue(), duraPercentScale.getValue(), duraPercentScale.getValue());
        drawString((int)(ArmorDisplay.INSTANCE.getItemDMG(itemStack) * 100) + "", x, y, itemsTextShadow.getValue(), dBColor.getRGB());
        GL11.glScalef(1.0f / duraPercentScale.getValue(), 1.0f / duraPercentScale.getValue(), 1.0f / duraPercentScale.getValue());
        GL11.glTranslatef(x * -(1.0f - duraPercentScale.getValue()), y * -(1.0f - duraPercentScale.getValue()), 0.0f);
    }
    
    private void renderDMGBar(ItemStack itemStack, float x, float y) {
        java.awt.Color dBColor = duraColor.getValue().getColorColor();
        float damageFactor = ArmorDisplay.INSTANCE.getItemDMG(itemStack) * itemRectsWidth.getValue();
        GL11.glDisable(GL_TEXTURE_2D);
        RenderUtils2D.prepareGl();

        if (duraColorShift.getValue()) {
            dBColor = ColorUtil.colorShift(duraColor2.getValue().getColorColor(), duraColor.getValue().getColorColor(), MathUtilFuckYou.clamp(ArmorDisplay.INSTANCE.getItemDMG(itemStack) * 300.0f, 0.0f, 300.0f));
        }

        if (duraBarBordered.getValue()) {
            if (duraBarRounded.getValue()) {
                RenderUtils2D.drawRoundedRect(x + (duraBarBorderOffset.getValue() / 2.0f), y + (duraBarBorderOffset.getValue() / 2.0f), duraBarRoundedRadius.getValue(), x + itemRectsWidth.getValue() - (duraBarBorderOffset.getValue() / 2.0f), y + duraBarHeight.getValue() - (duraBarBorderOffset.getValue() / 2.0f), false, duraBarRoundedTopRight.getValue(), duraBarRoundedTopLeft.getValue(), duraBarRoundedDownRight.getValue(), duraBarRoundedDownLeft.getValue(), duraBarBGColor.getValue().getColor());
            }
            else {
                RenderUtils2D.drawRect(x + (duraBarBorderOffset.getValue() / 2.0f), y + (duraBarBorderOffset.getValue() / 2.0f), x + itemRectsWidth.getValue() - (duraBarBorderOffset.getValue() / 2.0f), y + duraBarHeight.getValue() - (duraBarBorderOffset.getValue() / 2.0f), duraBarBGColor.getValue().getColor());
            }
        }
        else {
            if (duraBarRounded.getValue()) {
                RenderUtils2D.drawRoundedRect(x, y, duraBarRoundedRadius.getValue(), x + itemRectsWidth.getValue(), y + duraBarHeight.getValue(), false, duraBarRoundedTopRight.getValue(), duraBarRoundedTopLeft.getValue(), duraBarRoundedDownRight.getValue(), duraBarRoundedDownLeft.getValue(), duraBarBGColor.getValue().getColor());
            }
            else {
                RenderUtils2D.drawRect(x, y, x + itemRectsWidth.getValue(), y + duraBarHeight.getValue(), duraBarBGColor.getValue().getColor());
            }
        }

        if (duraBarBordered.getValue()) {
            if (duraBarRounded.getValue()) {
                RenderUtils2D.drawCustomRoundedRectOutline(x, y, x + itemRectsWidth.getValue(), y + duraBarHeight.getValue(), duraBarRoundedRadius.getValue(), duraBarBorderWidth.getValue(), duraBarRoundedTopRight.getValue(), duraBarRoundedTopLeft.getValue(), duraBarRoundedDownRight.getValue(), duraBarRoundedDownLeft.getValue(), false, false, dBColor.getRGB());
                RenderUtils2D.drawRoundedRect(x + (duraBarBorderOffset.getValue() / 2.0f), y + (duraBarBorderOffset.getValue() / 2.0f), duraBarRoundedRadius.getValue(), x + damageFactor - (duraBarBorderOffset.getValue() / 2.0f), y + duraBarHeight.getValue() - (duraBarBorderOffset.getValue() / 2.0f), false, duraBarRoundedTopRight.getValue(), duraBarRoundedTopLeft.getValue(), duraBarRoundedDownRight.getValue(), duraBarRoundedDownLeft.getValue(), dBColor.getRGB());
            }
            else {
                RenderUtils2D.drawRectOutline(x, y, x + itemRectsWidth.getValue(), y + duraBarHeight.getValue(), duraBarBorderWidth.getValue(), dBColor.getRGB(), false, false);
                RenderUtils2D.drawRect(x + (duraBarBorderOffset.getValue() / 2.0f), y + (duraBarBorderOffset.getValue() / 2.0f), x + damageFactor - (duraBarBorderOffset.getValue() / 2.0f), y + duraBarHeight.getValue() - (duraBarBorderOffset.getValue() / 2.0f), dBColor.getRGB());
            }
        }
        else {
            if (duraBarRounded.getValue()) {
                RenderUtils2D.drawRoundedRect(x, y, duraBarRoundedRadius.getValue(), x + damageFactor, y + duraBarHeight.getValue(), false, duraBarRoundedTopRight.getValue(), duraBarRoundedTopLeft.getValue(), duraBarRoundedDownRight.getValue(), duraBarRoundedDownLeft.getValue(), dBColor.getRGB());
            }
            else {
                RenderUtils2D.drawRect(x, y, x + damageFactor, y + duraBarHeight.getValue(), dBColor.getRGB());
            }
        }
        RenderUtils2D.releaseGl();
    }

    private void renderEnchantments(ItemStack itemStack, float x, float y, boolean drawUp) {
        float tempY = y;

        GL11.glTranslatef(x * (1.0f - enchantScale.getValue()), y * (1.0f - enchantScale.getValue()), 0.0f);
        GL11.glScalef(enchantScale.getValue(), enchantScale.getValue(), enchantScale.getValue());

        NBTTagList enchants = itemStack.getEnchantmentTagList();
        for (int i = 0; i < enchants.tagCount(); ++i) {
            Enchantment enchantment = Enchantment.getEnchantmentByID(enchants.getCompoundTagAt(i).getShort("id"));
            short lvl = enchants.getCompoundTagAt(i).getShort("lvl");

            if (enchantment != null) {
                    drawString((enchantment.isCurse() ? enchantment.getTranslatedName(lvl).substring(11).substring(0, 2) :
                            enchantment.getTranslatedName(lvl).substring(0, 2)) + lvl,
                            x, tempY, itemsTextShadow.getValue(), enchantment.isCurse() ? enchantmentCurseColor.getValue().getColor() : enchantmentColor.getValue().getColor());

                tempY -= drawUp ? enchantSeparationOffset.getValue() : -enchantSeparationOffset.getValue();
            }
        }

        GL11.glScalef(1.0f / enchantScale.getValue(), 1.0f / enchantScale.getValue(), 1.0f / enchantScale.getValue());
        GL11.glTranslatef(x * -(1.0f - enchantScale.getValue()), y * -(1.0f - enchantScale.getValue()), 0.0f);
    }

    private int getWidthNametags(String str){
        if (font.getValue() == CustomFont.FontMode.Comfortaa) {
            return FontManager.fontRenderer.getStringWidth(str);
        }
        else if (font.getValue() == CustomFont.FontMode.Arial) {
            return FontManager.fontArialRenderer.getStringWidth(str);
        }
        else if (font.getValue() == CustomFont.FontMode.Objectivity) {
            return FontManager.fontObjectivityRenderer.getStringWidth(str);
        }
        else {
            return mc.fontRenderer.getStringWidth(str);
        }
    }
    enum Page {
        Distance,
        Rect,
        RectEffects,
        HealthBar,
        Items,
        Text,
        Entities,
        Colors
    }

    public enum TextMode {
        None,
        Left,
        Right
    }
}
