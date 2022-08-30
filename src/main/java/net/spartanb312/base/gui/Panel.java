package net.spartanb312.base.gui;

import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.gui.components.ModuleButton;
import net.spartanb312.base.gui.renderers.ClickGUIRenderer;
import net.spartanb312.base.gui.renderers.HUDEditorRenderer;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.Timer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.graphics.font.CFontRenderer;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import net.spartanb312.base.utils.math.Pair;
import net.spartanb312.base.utils.math.Vec2I;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static net.spartanb312.base.command.Command.mc;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Panel {

    public int x, y, width, height;
    public Category category;

    public boolean extended;
    boolean dragging;

    int x2, y2;

    public static int staticY;

    public static Panel instance;

    CFontRenderer font;

    FontManager fontManager;

    public int startY;


    public List<ModuleButton> elements = new ArrayList<>();

    static boolean firstModuleButton = false;
    static boolean lastModuleButton = false;

    public static HashMap<Integer, Vector2f> categoryRectHoverParticlesList = new HashMap<>();
    public static HashMap<Integer, Float> categoryRectHoverParticlesOriginalYs = new HashMap<>();
    public static HashMap<Integer, Float> categoryRectHoverParticlesSpeed = new HashMap<>();
    public static HashMap<Integer, Float> categoryRectHoverParticlesTriAngle = new HashMap<>();
    public static HashMap<Integer, Float> categoryRectHoverParticlesTriSpinSpeed = new HashMap<>();
    public static HashMap<Integer, Float> categoryRectHoverParticlesSize = new HashMap<>();
    static int categoryRectHoverParticlesId = 0;
    static int renderLoopsCategoryRectHoverParticles = 0;

    public static HashMap<String, Integer> storedCategoryHoverLoops = new HashMap<>();
    public static HashMap<String, Float> storedCategoryTextScaleLoops = new HashMap<>();
    public static HashMap<String, Integer> storedCategoryShadowGradientHoverLoops = new HashMap<>();
    public static HashMap<String, Integer> storedHornsHoverLoops = new HashMap<>();

    public static ResourceLocation HORN = new ResourceLocation("moloch:textures/horn.png");

    static int preExtendStartY = 0;


    static String staticString = "";
    static int staticInteger = 0;

    public Panel(Category category, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.extended = true;
        this.dragging = false;
        this.category = category;
        font = FontManager.fontRenderer;
        instance = this;
        setup();
    }

    net.spartanb312.base.utils.Timer panelTimer = new Timer();

    public void setup() {
        for (Module m : ModuleManager.getModules()) {
            if (m.category == category) {
                elements.add(new ModuleButton(m, width - 10, height - 2, this));
            }
        }
    }

    private int outlineTopColor() {
        if (ClickGUI.instance.outlineColorGradient.getValue()) {
            return ClickGUI.instance.outlineTopColor.getValue().getColor();
        }
        else if (!ClickGUI.instance.outlineColorGradient.getValue()) {
            return ClickGUI.instance.outlineColor.getValue().getColor();
        }
        return 0;
    }

    private int outlineDownColor() {
        if (ClickGUI.instance.outlineColorGradient.getValue()) {
            return ClickGUI.instance.outlineDownColor.getValue().getColor();
        }
        else if (!ClickGUI.instance.outlineColorGradient.getValue()) {
            return ClickGUI.instance.outlineColor.getValue().getColor();
        }
        return 0;
    }

    private void moduleSeparators(ModuleButton button) {
        GlStateManager.disableAlpha();

        if (ClickGUI.instance.moduleSeparatorFadeMode.getValue() == ClickGUI.ModuleSeparatorFadeMode.Left) {
            if (ClickGUI.instance.moduleSeparatorGlow.getValue()) {
                GlStateManager.disableAlpha();
                RenderUtils2D.drawCustomRect(button.x + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), (button.x + ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB());
                RenderUtils2D.drawCustomRect((button.x + ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue() - ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(),  new Color(0, 0, 0, 0).getRGB());

                RenderUtils2D.drawCustomRect(button.x + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), (button.x + ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue()), (button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue()) + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                RenderUtils2D.drawCustomRect((button.x + ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue() - ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                GlStateManager.enableAlpha();
            }
            RenderUtils2D.drawCustomLine(button.x + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), (button.x + ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor());
            RenderUtils2D.drawCustomLine((button.x + ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue() - ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
        }
        else if (ClickGUI.instance.moduleSeparatorFadeMode.getValue() == ClickGUI.ModuleSeparatorFadeMode.Right) {
            if (ClickGUI.instance.moduleSeparatorGlow.getValue()) {
                GlStateManager.disableAlpha();
                RenderUtils2D.drawCustomRect(button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), (button.x + (Component.instance.width - 1) + ClickGUI.instance.moduleSeparatorX.getValue()) - ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB());
                RenderUtils2D.drawCustomRect((button.x + (Component.instance.width - 1) + ClickGUI.instance.moduleSeparatorX.getValue()) - ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), button.x + ClickGUI.instance.moduleSeparatorX.getValue() + ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(),  new Color(0, 0, 0, 0).getRGB());

                RenderUtils2D.drawCustomRect(button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), (button.x + (Component.instance.width - 1) + ClickGUI.instance.moduleSeparatorX.getValue()) - ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                RenderUtils2D.drawCustomRect((button.x + (Component.instance.width - 1) + ClickGUI.instance.moduleSeparatorX.getValue()) - ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + ClickGUI.instance.moduleSeparatorX.getValue() + ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                GlStateManager.enableAlpha();
            }
            RenderUtils2D.drawCustomLine( button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), (button.x + (Component.instance.width - 1) + ClickGUI.instance.moduleSeparatorX.getValue()) - ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor());
            RenderUtils2D.drawCustomLine((button.x + (Component.instance.width - 1) + ClickGUI.instance.moduleSeparatorX.getValue()) - ((Component.instance.width - 1) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + ClickGUI.instance.moduleSeparatorX.getValue() + ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
        }
        else if (ClickGUI.instance.moduleSeparatorFadeMode.getValue() == ClickGUI.ModuleSeparatorFadeMode.Both) {
            if (ClickGUI.instance.moduleSeparatorGlow.getValue()) {
                GlStateManager.disableAlpha();
                RenderUtils2D.drawCustomRect(button.x + ClickGUI.instance.moduleSeparatorX.getValue() + ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), button.x + (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB());
                RenderUtils2D.drawCustomRect(button.x + ClickGUI.instance.moduleSeparatorX.getValue() + ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());

                RenderUtils2D.drawCustomRect(button.x + (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), button.x + Component.instance.width - 1 - (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB());
                RenderUtils2D.drawCustomRect(button.x + (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 - (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());

                RenderUtils2D.drawCustomRect(button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue() - ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), button.x + Component.instance.width - 1 - (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB());
                RenderUtils2D.drawCustomRect(button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue() - ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 - (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                GlStateManager.enableAlpha();
            }
            RenderUtils2D.drawCustomLine( button.x + ClickGUI.instance.moduleSeparatorX.getValue() + ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor());
            RenderUtils2D.drawCustomLine(button.x + (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 - (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor());
            RenderUtils2D.drawCustomLine( button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue() - ClickGUI.instance.moduleSeparatorWidth.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 - (((Component.instance.width - 1) / 2.0f) * ClickGUI.instance.moduleSeparatorFadeLength.getValue()) + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor());
        }
        GlStateManager.enableAlpha();
        if (ClickGUI.instance.moduleSeparatorFadeMode.getValue() == ClickGUI.ModuleSeparatorFadeMode.None) {
            if (ClickGUI.instance.moduleSeparatorGlow.getValue()) {
                GlStateManager.disableAlpha();
                RenderUtils2D.drawCustomRect(button.x, button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() - ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB());
                RenderUtils2D.drawCustomRect(button.x, button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue() + ClickGUI.instance.moduleSeparatorGlowHeight.getValue(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSeparatorColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSeparatorGlowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                GlStateManager.enableAlpha();
            }
            RenderUtils2D.drawCustomLine(button.x, button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), button.x + Component.instance.width - 1 + ClickGUI.instance.moduleSeparatorX.getValue(), button.y + Component.instance.height + ClickGUI.instance.moduleSeparatorY.getValue(), ClickGUI.instance.moduleSeparatorHeight.getValue(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor(), ClickGUI.instance.moduleSeparatorColor.getValue().getColor());
        }
    }

    public void panelSideGlow(ModuleButton button) {
        if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
            GlStateManager.disableAlpha();
            if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                RenderUtils2D.drawCustomRect(button.x, button.y - 1, button.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), button.y + Component.instance.height + 1, new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(),  ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
            }
            if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                RenderUtils2D.drawCustomRect(button.x + Component.instance.width - ClickGUI.instance.moduleSideGlowWidth.getValue(), button.y - 1, button.x + Component.instance.width, button.y + Component.instance.height + 1, ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor());
            }
            GlStateManager.enableAlpha();
        }
    }

    public void panelSideGlowBottomExtensions() {
        if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
            GlStateManager.disableAlpha();
            if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                RenderUtils2D.drawCustomRect(x + 5, startY - Component.instance.height - 1, x + 5 + ClickGUI.instance.moduleSideGlowWidth.getValue(), startY + 1 + ClickGUI.instance.panelExtensionsHeight.getValue() - 13, new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(),  ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
            }
            if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                RenderUtils2D.drawCustomRect(x + 5 + Component.instance.width - ClickGUI.instance.moduleSideGlowWidth.getValue(), startY - Component.instance.height - 1, x + 5 + Component.instance.width, startY + 1 + ClickGUI.instance.panelExtensionsHeight.getValue() - 13, ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor());
            }
            GlStateManager.enableAlpha();
        }
    }

    private void calcHeight() {
        int index = 0;
        if (!elements.isEmpty()) {
            startY = y + height + 2;
            int step = 0;
            for (ModuleButton button : elements) {
                index++;


                if (extended) {
                    if (!panelTimer.passed(index * 25 / ClickGUI.instance.panelOpenSpeed.getValue())) continue;
                }
                else {
                    if (panelTimer.passed((elements.size() - index) * 25 / ClickGUI.instance.panelOpenSpeed.getValue())) continue;
                }

                firstModuleButton = index >= 1;
                lastModuleButton = index == elements.size();

                if (step == 0 && firstModuleButton) {
                    startY += (((ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Top || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both)) ? ClickGUI.instance.panelExtensionsHeight.getValue() + 2 : 0);
                    step += 1;
                }

                button.y = startY;


                startY += height - 1;

                List<Component> visibleSettings = button.settings.stream().filter(Component::isVisible).collect(Collectors.toList());
                int settingIndex = -1;
                for (Component component : visibleSettings) {
                    settingIndex++;
                    if (button.isExtended) {
                        if (!button.buttonTimer.passed(settingIndex * 25 / ClickGUI.instance.moduleOpenSpeed.getValue())) continue;
                    } else {
                        if (button.buttonTimer.passed((visibleSettings.size() - settingIndex) * 25 / ClickGUI.instance.moduleOpenSpeed.getValue())) continue;
                    }
                    component.y = startY;
                    if ((settingIndex == visibleSettings.size() - 1) && ClickGUI.instance.extendedBottomExtensions.getValue()) {
                        startY += ClickGUI.instance.extendedBottomExtensionsHeight.getValue();
                    }

                    startY += component.height;
                }

                startY += 1;

            }
        }
    }




    private void basePatternTrianglesDouble(int red, int green, int blue, int alpha, boolean outlineMode, float size) {
        Color color = new Color(red, green, blue, alpha);

        for (int i = 0; i <= ClickGUI.instance.baseRectPatternAmount.getValue(); ++i) {
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), (x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }
            RenderUtils2D.drawEquilateralTriangle(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), !ClickGUI.instance.baseRectPatternReflect.getValue(), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue()) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawEquilateralTriangle(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue()) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), ClickGUI.instance.baseRectPatternReflect.getValue(), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawEquilateralTriangle(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), !ClickGUI.instance.baseRectPatternReflect.getValue(), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue()) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawEquilateralTriangle(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue()) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), ClickGUI.instance.baseRectPatternReflect.getValue(), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
        }
    }

    private void basePatternTrianglesSingle(int rotationMode, int red, int green, int blue, int alpha, float size, boolean outlineMode) {
        /*
        rotationMode 1 = up
        rotationMode 2 = down
        rotationMode 3 = left
        rotationMode 4 = right
         */
        Color color = new Color(red, green, blue, alpha);


        for (int i = 0; i <= ClickGUI.instance.baseRectPatternAmount.getValue(); ++i) {

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            if (rotationMode == 2) {
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 3) {
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);

            }
            else if (rotationMode == 4) {
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }

            RenderUtils2D.drawEquilateralTriangle(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), false, size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (rotationMode == 2) {
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(-180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 3) {
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 4) {
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }



            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            if (rotationMode == 2) {
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 3) {
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 4) {
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }

            RenderUtils2D.drawEquilateralTriangle(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), false, size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (rotationMode == 2) {
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(-180.0f, 0.0f, 0.0f, 0.0f);
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 3) {
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }
            else if (rotationMode == 4) {
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)), 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f)) * -1.0f, 0.0f);
            }

        }
    }

    private void basePatternCirclesSingle(int red, int green, int blue, int alpha, float size, boolean outlineMode) {
        Color color = new Color(red, green, blue, alpha);

        for (int i = 0; i <= ClickGUI.instance.baseRectPatternAmount.getValue(); ++i) {
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawCircle(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawCircle(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
        }
    }

    private void basePatternCirclesDouble(int red, int green, int blue, int alpha, float size, boolean outlineMode) {
        Color color = new Color(red, green, blue, alpha);

        if (ClickGUI.instance.baseRectPatternReflect.getValue()) {
            GL11.glTranslatef(x + (width / 2.0f), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()), 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef((x + (width / 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()) * -1.0f, 0.0f);
        }
        for (int i = 0; i <= ClickGUI.instance.baseRectPatternAmount.getValue(); ++i) {
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawCircle(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue()) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawCircle(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue()) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawCircle(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue()) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawCircle(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue()) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
        }
        if (ClickGUI.instance.baseRectPatternReflect.getValue()) {
            GL11.glTranslatef(x + (width / 2.0f), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()), 0.0f);
            GL11.glRotatef(-180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef((x + (width / 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()) * -1.0f, 0.0f);
        }
    }

    private void basePatternDiamondsSingle(int red, int green, int blue, int alpha, float size, boolean outlineMode) {
        Color color = new Color(red, green, blue, alpha);

        for (int i = 0; i <= ClickGUI.instance.baseRectPatternAmount.getValue(); ++i) {
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawRhombus(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawRhombus(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
        }
    }

    private void basePatternDiamondsDouble(int red, int green, int blue, int alpha, float size, boolean outlineMode) {
        Color color = new Color(red, green, blue, alpha);

        if (ClickGUI.instance.baseRectPatternReflect.getValue()) {
            GL11.glTranslatef(x + (width / 2.0f), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()), 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef((x + (width / 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()) * -1.0f, 0.0f);
        }
        for (int i = 0; i <= ClickGUI.instance.baseRectPatternAmount.getValue(); ++i) {
            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawRhombus(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue()) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawRhombus(x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternGap.getValue()) + (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawRhombus(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());

            if (outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRoll.getValue() : ClickGUI.instance.baseRectPatternBrightnessRoll.getValue()) {
                color = ColorUtil.rolledBrightness(color, outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMaxBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMaxBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollMinBright.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollMinBright.getValue(), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollSpeed.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollSpeed.getValue(), x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue()) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), outlineMode ? ClickGUI.instance.baseRectPatternOutlineBrightnessRollLength.getValue() : ClickGUI.instance.baseRectPatternBrightnessRollLength.getValue(), outlineMode ? (ClickGUI.instance.baseRectPatternOutlineBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternOutlineBrightnessRollDirection.Right) : (ClickGUI.instance.baseRectPatternBrightnessRollDirection.getValue() == ClickGUI.BaseRectPatternBrightnessRollDirection.Right), false);
            }

            RenderUtils2D.drawRhombus(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternGap.getValue()) - (ClickGUI.instance.baseRectPatternGap.getValue() * i * 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternDoubleYGap.getValue() / 2.0f), size, new Color(color.getRed(), color.getGreen(), color.getBlue(), (outlineMode ? ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha() : ClickGUI.instance.baseRectPatternColor.getValue().getAlpha())).getRGB());
        }
        if (ClickGUI.instance.baseRectPatternReflect.getValue()) {
            GL11.glTranslatef(x + (width / 2.0f), (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()), 0.0f);
            GL11.glRotatef(-180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef((x + (width / 2.0f)) * -1.0f, (startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue()) * -1.0f, 0.0f);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float translateDelta, float partialTicks) {

        if (mc.currentScreen != null) {

            if (this.dragging) {
                x = x2 + mouseX;
                y = y2 + mouseY;
            }

            int moduleRectColor = ClickGUI.instance.moduleColor.getValue().getColor();

            //horns render
            if (ClickGUI.instance.rectHorns.getValue()) {
                Color hornColor = new Color(ClickGUI.instance.rectHornsColor.getValue().getColorColor().getRed(), ClickGUI.instance.rectHornsColor.getValue().getColorColor().getGreen(), ClickGUI.instance.rectHornsColor.getValue().getColorColor().getBlue(), ClickGUI.instance.rectHornsColor.getValue().getAlpha());

                //horns hover
                if (ClickGUI.instance.rectHornsHoverDifColor.getValue()) {
                    if (isHovered(mouseX, mouseY)) {

                        if (!ClickGUI.instance.categoryRectHoverColorSmooth.getValue()) {
                            hornColor = new Color(ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getRed(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getGreen(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getBlue(), ClickGUI.instance.rectHornsHoverColor.getValue().getAlpha());
                        }
                        else {
                            storedHornsHoverLoops.putIfAbsent(category.categoryName, 0);
                            int hoverLoops = storedHornsHoverLoops.get(category.categoryName);
                            if (hoverLoops >= 300) {
                                hoverLoops = 300;
                            }
                            if (hoverLoops <= 0) {
                                hoverLoops = 0;
                            }
                            int nonHoveredToHoveredRed = (int)(MathUtilFuckYou.linearInterp(hornColor.getRed(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getRed(), hoverLoops));
                            int nonHoveredToHoveredGreen = (int)(MathUtilFuckYou.linearInterp(hornColor.getGreen(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getGreen(), hoverLoops));
                            int nonHoveredToHoveredBlue = (int)(MathUtilFuckYou.linearInterp(hornColor.getBlue(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getBlue(), hoverLoops));
                            int nonHoveredToHoveredAlpha = (int)(MathUtilFuckYou.linearInterp(hornColor.getAlpha(), ClickGUI.instance.rectHornsHoverColor.getValue().getAlpha(), hoverLoops));

                            hornColor = new Color(nonHoveredToHoveredRed, nonHoveredToHoveredGreen, nonHoveredToHoveredBlue, nonHoveredToHoveredAlpha);
                            hoverLoops += ClickGUI.instance.rectHornsHoverColorSmoothFactorIn.getValue() * 10.0f;
                            storedHornsHoverLoops.put(category.categoryName, hoverLoops);
                        }
                    }

                    if (ClickGUI.instance.rectHornsHoverColorSmooth.getValue() && storedHornsHoverLoops.containsKey(category.categoryName) && !isHovered(mouseX, mouseY)) {

                        int hoverLoops = storedHornsHoverLoops.get(category.categoryName);
                        if (hoverLoops <= 0) {
                            hoverLoops = 0;
                        }
                        if (hoverLoops >= 300) {
                            hoverLoops = 300;
                        }
                        int nonHoveredToHoveredRed = (int)(MathUtilFuckYou.linearInterp(hornColor.getRed(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getRed(), hoverLoops));
                        int nonHoveredToHoveredGreen = (int)(MathUtilFuckYou.linearInterp(hornColor.getGreen(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getGreen(), hoverLoops));
                        int nonHoveredToHoveredBlue = (int)(MathUtilFuckYou.linearInterp(hornColor.getBlue(), ClickGUI.instance.rectHornsHoverColor.getValue().getColorColor().getBlue(), hoverLoops));
                        int nonHoveredToHoveredAlpha = (int)(MathUtilFuckYou.linearInterp(hornColor.getAlpha(), ClickGUI.instance.rectHornsHoverColor.getValue().getAlpha(), hoverLoops));

                        hornColor = new Color(nonHoveredToHoveredRed, nonHoveredToHoveredGreen, nonHoveredToHoveredBlue, nonHoveredToHoveredAlpha);
                        hoverLoops -= ClickGUI.instance.rectHornsHoverColorSmoothFactorOut.getValue() * 10.0f;
                        storedHornsHoverLoops.put(category.categoryName, hoverLoops);
                    }
                }

                //horn rendering
                GL11.glEnable(GL_TEXTURE_2D);

                GL11.glColor4f(hornColor.getRed() / 255.0f, hornColor.getGreen() / 255.0f, hornColor.getBlue() / 255.0f, hornColor.getAlpha() / 255.0f);

                //left
                mc.getTextureManager().bindTexture(HORN);
                Gui.drawScaledCustomSizeModalRect((int)(x + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.rectWidth.getValue() / 2) + ClickGUI.instance.rectHornsX.getValue() - ClickGUI.instance.rectHornsScale.getValue()), (int)(y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue() + ClickGUI.instance.rectHornsY.getValue() - ClickGUI.instance.rectHornsScale.getValue()), 0, 0, ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue());

                //right
                mc.getTextureManager().bindTexture(HORN);
                Gui.drawScaledCustomSizeModalRect((int)(x + (width * ClickGUI.instance.rectHornsGap.getValue()) + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.rectWidth.getValue() / 2) + ClickGUI.instance.rectHornsX.getValue() - ClickGUI.instance.rectHornsScale.getValue()), (int)(y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue() + ClickGUI.instance.rectHornsY.getValue() - ClickGUI.instance.rectHornsScale.getValue()), 0, 0, ClickGUI.instance.rectHornsScale.getValue() * -1, ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue(), ClickGUI.instance.rectHornsScale.getValue());

                GL11.glDisable(GL_TEXTURE_2D);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }


            int index = 0;
            if (!elements.isEmpty()) {
                startY = y + height + 2;
                int step = 0;
                int step2 = 0;
                int step3 = 0;
                for (ModuleButton button : elements) {
                    index++;

                    if (extended) {
                        if (!panelTimer.passed(index * 25 / ClickGUI.instance.panelOpenSpeed.getValue())) continue;
                    }
                    else {
                        if (panelTimer.passed((elements.size() - index) * 25 / ClickGUI.instance.panelOpenSpeed.getValue())) continue;
                    }

                    firstModuleButton = index >= 1;
                    lastModuleButton = index == elements.size();

                    if (step2 == 0 && firstModuleButton) {
                        startY += (((ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Top || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both)) ? ClickGUI.instance.panelExtensionsHeight.getValue() + 2 : 0);
                        step2 += 1;
                    }

                    button.solvePos(true);
                    button.y = startY;

                    startY += height - 1;

                    if (button.isHovered(mouseX, mouseY) && !button.getDescription().equals("")) ClickGUIFinal.description = new Pair<>(button.getDescription(), new Vec2I(mouseX, mouseY));

                    int settingIndex = -1;
                    int settingIndex2 = -1;
                    List<Component> visibleSettings = button.settings.stream().filter(Component::isVisible).collect(Collectors.toList());

                    //panel side glow
                    if (!ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                        panelSideGlow(button);
                    }

                    //module separator bars
                    if (ClickGUI.instance.moduleSeparators.getValue() && !ClickGUI.instance.moduleSeparatorsOnTop.getValue()) {
                        moduleSeparators(button);
                    }

                    //panel extensions top
                    if (step == 0 && firstModuleButton) {
                        if (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Top || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both) {
                            if (!ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                                GL11.glTranslatef(0.0f, -ClickGUI.instance.panelExtensionsHeight.getValue(), 0.0f);

                                if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                    GlStateManager.disableAlpha();
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(button.x, button.y - 1, button.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), button.y - 1 + ClickGUI.instance.panelExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(),  ClickGUI.instance.moduleSideGlowColor.getValue().getColor(),new Color(0, 0, 0, 0).getRGB());
                                    }
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(button.x + Component.instance.width - ClickGUI.instance.moduleSideGlowWidth.getValue(), button.y - 1, button.x + Component.instance.width, button.y - 1 + ClickGUI.instance.panelExtensionsHeight.getValue(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor());
                                    }
                                    GlStateManager.enableAlpha();
                                }

                                GL11.glTranslatef(0.0f, ClickGUI.instance.panelExtensionsHeight.getValue(), 0.0f);
                            }
                            if (ClickGUI.instance.moduleSeparators.getValue() && !ClickGUI.instance.moduleSeparatorsOnTop.getValue()) {
                                GL11.glTranslatef(0.0f, -ClickGUI.instance.panelExtensionsHeight.getValue() - 2.0f, 0.0f);
                                moduleSeparators(button);
                                GL11.glTranslatef(0.0f, ClickGUI.instance.panelExtensionsHeight.getValue() + 2.0f, 0.0f);
                            }

                            RenderUtils2D.drawRect(button.x, y + height - 2, button.x + Component.instance.width, (y + height) + Component.instance.height + 1 - 12.0f + ClickGUI.instance.panelExtensionsHeight.getValue(), ClickGUI.instance.moduleBGColor.getValue().getColor());
                            RenderUtils2D.drawRect(button.x + 1, y + height - 2, button.x + Component.instance.width - 1, (y + height) + Component.instance.height - ClickGUI.instance.moduleGap.getValue() - 12.0f + ClickGUI.instance.panelExtensionsHeight.getValue(), moduleRectColor);

                            if (ClickGUI.instance.moduleSeparators.getValue() && ClickGUI.instance.moduleSeparatorsOnTop.getValue()) {
                                GL11.glTranslatef(0.0f, -ClickGUI.instance.panelExtensionsHeight.getValue() - 2.0f, 0.0f);
                                moduleSeparators(button);
                                GL11.glTranslatef(0.0f, ClickGUI.instance.panelExtensionsHeight.getValue() + 2.0f, 0.0f);
                            }
                            if (ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                                GL11.glTranslatef(0.0f, -ClickGUI.instance.panelExtensionsHeight.getValue(), 0.0f);

                                if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                    GlStateManager.disableAlpha();
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(button.x, button.y - 1, button.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), button.y - 1 + ClickGUI.instance.panelExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(),  ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
                                    }
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(button.x + Component.instance.width - ClickGUI.instance.moduleSideGlowWidth.getValue(), button.y - 1, button.x + Component.instance.width, button.y - 1 + ClickGUI.instance.panelExtensionsHeight.getValue(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor());
                                    }
                                    GlStateManager.enableAlpha();
                                }

                                GL11.glTranslatef(0.0f, ClickGUI.instance.panelExtensionsHeight.getValue(), 0.0f);
                            }
                            if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                GL11.glTranslatef(0.0f, -ClickGUI.instance.panelExtensionsHeight.getValue() - 2.0f, 0.0f);
                                GlStateManager.disableAlpha();
                                if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Left) {
                                    RenderUtils2D.drawCustomRect(button.x, button.y - 1, button.x + ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), button.y + Component.instance.height, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(),  new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                                }
                                else if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Right) {
                                    RenderUtils2D.drawCustomRect(button.x + Component.instance.width - ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), button.y - 1, button.x + Component.instance.width, button.y + Component.instance.height + 1, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB());
                                }
                                GlStateManager.enableAlpha();
                                GL11.glTranslatef(0.0f, ClickGUI.instance.panelExtensionsHeight.getValue() + 2.0f, 0.0f);
                            }
                        }
                        step += 1;
                    }

                    //module bg rects
                    RenderUtils2D.drawRect(button.x, button.y - 1, button.x + Component.instance.width, button.y + Component.instance.height + 1, ClickGUI.instance.moduleBGColor.getValue().getColor());
                    if (ClickGUI.instance.moduleRectRounded.getValue()) {
                        RenderUtils2D.drawRoundedRect(button.x + 1, button.y - 1, ClickGUI.instance.moduleRectRoundedRadius.getValue(),button.x + Component.instance.width - 1, (button.y + Component.instance.height) - ClickGUI.instance.moduleGap.getValue(), false, ClickGUI.instance.moduleRoundedTopRight.getValue(), ClickGUI.instance.moduleRoundedTopLeft.getValue(), ClickGUI.instance.moduleRoundedBottomRight.getValue(), ClickGUI.instance.moduleRoundedBottomLeft.getValue(), moduleRectColor);
                    }
                    else {
                        RenderUtils2D.drawRect(button.x + 1, button.y - 1, button.x + Component.instance.width - 1, (button.y + Component.instance.height) - ClickGUI.instance.moduleGap.getValue(), moduleRectColor);
                    }

                    //module config components
                    preExtendStartY = startY;
                    for (Component component : visibleSettings) {
                        settingIndex2++;
                        if (button.isExtended) {
                            if (!button.buttonTimer.passed(settingIndex2 * 25 / ClickGUI.instance.moduleOpenSpeed.getValue())) continue;
                        } else {
                            if (button.buttonTimer.passed((visibleSettings.size() - settingIndex2) * 25 / ClickGUI.instance.moduleOpenSpeed.getValue())) continue;
                        }
                        component.solvePos(true);
                        component.y = preExtendStartY;
                        component.bottomRender(mouseX, mouseY, (settingIndex2 == visibleSettings.size() - 1), settingIndex2 == 1, partialTicks);
                        //extended panel extensions
                        if ((settingIndex2 == visibleSettings.size() - 1) && ClickGUI.instance.extendedBottomExtensions.getValue()) {
                            float theY = component.y + component.height;
                            //glow under layer
                            if (!ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                                if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                    GlStateManager.disableAlpha();
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(component.x, theY, component.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
                                    }
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(component.x + component.width - ClickGUI.instance.moduleSideGlowWidth.getValue(), theY, component.x + component.width, theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor());
                                    }
                                    GlStateManager.enableAlpha();
                                }
                            }

                            RenderUtils2D.drawRect(component.x, theY, component.x + component.width, theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), ClickGUI.instance.moduleBGColor.getValue().getColor());
                            RenderUtils2D.drawRect(component.x + 1, theY, component.x + component.width - 1 - ClickGUI.instance.extendedRectGap.getValue(), theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue() - 1, ClickGUI.instance.extendedRectColor.getValue().getColor());

                            //glow above layer
                            if (ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                                if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                    GlStateManager.disableAlpha();
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Left || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(component.x, theY, component.x + ClickGUI.instance.moduleSideGlowWidth.getValue(), theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB());
                                    }
                                    if (ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Right || ClickGUI.instance.moduleSideGlow.getValue() == ClickGUI.ModuleSideGlow.Both) {
                                        RenderUtils2D.drawCustomRect(component.x + component.width - ClickGUI.instance.moduleSideGlowWidth.getValue(), theY, component.x + component.width, theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), ClickGUI.instance.moduleSideGlowColor.getValue().getColor());
                                    }
                                    GlStateManager.enableAlpha();
                                }
                            }

                            //double glow
                            if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                GlStateManager.disableAlpha();
                                if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Left) {
                                    RenderUtils2D.drawCustomRect(component.x, theY, component.x + ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(),  new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                                }
                                else if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Right) {
                                    RenderUtils2D.drawCustomRect(component.x + component.width - ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), theY, component.x + component.width, theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB());
                                }
                                GlStateManager.enableAlpha();
                            }

                            //extended gradient
                            int extendedGradientColor = ClickGUI.instance.extendedGradientColor.getValue().getColor();
                            if (ClickGUI.instance.extendedVerticalGradient.getValue()) {
                                GlStateManager.disableAlpha();
                                RenderUtils2D.drawCustomRect(component.x, theY, component.x + ClickGUI.instance.extendedGradientWidth.getValue(), theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue() - 1, new Color(0, 0, 0, 0).getRGB(), extendedGradientColor, extendedGradientColor, new Color(0, 0, 0, 0).getRGB());
                                GlStateManager.enableAlpha();
                            }

                            //extended line
                            RenderUtils2D.drawCustomLine(component.x + (ClickGUI.instance.extendedWidth.getValue() / 2), theY, component.x + (ClickGUI.instance.extendedWidth.getValue() / 2), theY + ClickGUI.instance.extendedBottomExtensionsHeight.getValue() - 1, ClickGUI.instance.extendedWidth.getValue(), ClickGUI.instance.extendedColor.getValue().getColor(), ClickGUI.instance.extendedColor.getValue().getColor());

                            preExtendStartY += ClickGUI.instance.extendedBottomExtensionsHeight.getValue();
                        }

                        preExtendStartY += component.height;
                    }


                    for (Component component : visibleSettings) {
                        settingIndex++;
                        if (button.isExtended) {
                            if (!button.buttonTimer.passed(settingIndex * 25 / ClickGUI.instance.moduleOpenSpeed.getValue())) continue;
                        } else {
                            if (button.buttonTimer.passed((visibleSettings.size() - settingIndex) * 25 / ClickGUI.instance.moduleOpenSpeed.getValue())) continue;
                        }
                        component.solvePos(true);
                        component.y = startY;

                        int extendedTopDownGradientColor = ClickGUI.instance.extendedTopDownGradientColor.getValue().getColor();

                        if (settingIndex == 0 && ClickGUI.instance.extendedCategoryGradient.getValue() && ClickGUI.instance.extendedTopBars.getValue()) {
                            GlStateManager.disableAlpha();
                            RenderUtils2D.drawCustomRect(component.x, component.y, component.x + component.width, component.y + (ClickGUI.instance.extendedCategoryGradientHeight.getValue()), extendedTopDownGradientColor, extendedTopDownGradientColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                            GlStateManager.enableAlpha();
                        }
                        if ((settingIndex == visibleSettings.size() - 1) && ClickGUI.instance.extendedGradientBottom.getValue()) {
                            GlStateManager.disableAlpha();
                            RenderUtils2D.drawCustomRect(component.x, component.y + component.height - 1 + (ClickGUI.instance.extendedBottomExtensions.getValue() ? ClickGUI.instance.extendedBottomExtensionsHeight.getValue() : 0) - ClickGUI.instance.extendedGradientBottomHeight.getValue(), component.x + component.width, component.y + component.height - 1 + (ClickGUI.instance.extendedBottomExtensions.getValue() ? ClickGUI.instance.extendedBottomExtensionsHeight.getValue() : 0), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), extendedTopDownGradientColor, extendedTopDownGradientColor);
                            GlStateManager.enableAlpha();
                        }

                        component.render(mouseX, mouseY, translateDelta, partialTicks);
                        if (component.isHovered(mouseX, mouseY) && !component.getDescription().equals(""))
                            ClickGUIFinal.description = new Pair<>(component.getDescription(), new Vec2I(mouseX, mouseY));

                        if ((settingIndex == visibleSettings.size() - 1) && ClickGUI.instance.extendedBottomExtensions.getValue()) {
                            startY += ClickGUI.instance.extendedBottomExtensionsHeight.getValue();
                        }

                        startY += component.height;
                    }


                    //module rect outlines
                    int moduleRectOutlineColor = ClickGUI.instance.moduleRectOutlineColor.getValue().getColor();
                    if (ClickGUI.instance.moduleRectOutline.getValue() && ClickGUI.instance.moduleRectRounded.getValue()) {
                        RenderUtils2D.drawCustomRoundedRectOutline(button.x + 1, button.y - 1, button.x + Component.instance.width - 1, (button.y + Component.instance.height) - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.moduleRectRoundedRadius.getValue(), ClickGUI.instance.moduleRectOutlineLineWidth.getValue(), ClickGUI.instance.moduleRoundedTopRight.getValue(), ClickGUI.instance.moduleRoundedTopLeft.getValue(), ClickGUI.instance.moduleRoundedBottomRight.getValue(), ClickGUI.instance.moduleRoundedBottomLeft.getValue(), false, false, moduleRectOutlineColor);
                    }
                    else if (ClickGUI.instance.moduleRectOutline.getValue()) {
                        RenderUtils2D.drawRectOutline(button.x + 1, button.y - 1, button.x + Component.instance.width - 1, (button.y + Component.instance.height) - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.moduleRectOutlineLineWidth.getValue(), moduleRectOutlineColor, false, false);
                    }


                    //panel extensions bottom
                    if (lastModuleButton && extended) {
                        if (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both) {
                            GL11.glTranslatef(0.0f, 15.0f, 0.0f);

                            if (!ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                                panelSideGlowBottomExtensions();
                            }

                            RenderUtils2D.drawRect(x + 5, startY - Component.instance.height - 1, x + width - 5, startY + 1 + ClickGUI.instance.panelExtensionsHeight.getValue() - 13, ClickGUI.instance.moduleBGColor.getValue().getColor());
                            RenderUtils2D.drawRect(x + 6, startY - Component.instance.height - 1, x + width - 6, startY - ClickGUI.instance.moduleGap.getValue() + ClickGUI.instance.panelExtensionsHeight.getValue() - 13, moduleRectColor);

                            if (ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                                panelSideGlowBottomExtensions();
                            }
                            if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                                GlStateManager.disableAlpha();
                                if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Left) {
                                    RenderUtils2D.drawCustomRect(x + 5, startY - Component.instance.height - 1, x + 5 + ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), startY + 1 + ClickGUI.instance.panelExtensionsHeight.getValue() - 13, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                                }
                                else if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Right) {
                                    RenderUtils2D.drawCustomRect(x + 5 + Component.instance.width - ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), startY - Component.instance.height - 1, x + 5 + Component.instance.width, startY + 1 + ClickGUI.instance.panelExtensionsHeight.getValue() - 13, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB());
                                }
                                GlStateManager.enableAlpha();
                            }

                            GL11.glTranslatef(0.0f, -15.0f, 0.0f);
                        }
                    }

                    //panel side glow
                    if (ClickGUI.instance.moduleSideGlowLayer.getValue()) {
                        panelSideGlow(button);
                    }

                    //panel side glow double
                    if (ClickGUI.instance.moduleSideGlow.getValue() != ClickGUI.ModuleSideGlow.None) {
                        GlStateManager.disableAlpha();
                        if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Left) {
                            RenderUtils2D.drawCustomRect(button.x, button.y - 1, button.x + ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), button.y + Component.instance.height, new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                        }
                        else if (ClickGUI.instance.moduleSideGlowDouble.getValue() == ClickGUI.ModuleSideGlowDouble.Right) {
                            RenderUtils2D.drawCustomRect(button.x + Component.instance.width - ClickGUI.instance.moduleSideGlowDoubleWidth.getValue(), button.y - 1, button.x + Component.instance.width, button.y + Component.instance.height + 1, new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), new Color(ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleSideGlowDoubleAlpha.getValue()).getRGB());
                        }
                        GlStateManager.enableAlpha();
                    }

                    //category top gradient
                    if (ClickGUI.instance.categoryGradient.getValue() && (step3 == 0 && firstModuleButton)) {
                        int categoryGradientColor = ClickGUI.instance.gradientBarColor.getValue().getColor();
                        GlStateManager.disableAlpha();
                        RenderUtils2D.drawCustomRect(x + 4 - (ClickGUI.instance.categoryGradientXScale.getValue() / 2) + (ClickGUI.instance.categoryGradientX.getValue()), y + height - (ClickGUI.instance.categoryGradientYScale.getValue() / 2) + (ClickGUI.instance.categoryGradientY.getValue()), x + width - 4 + (ClickGUI.instance.categoryGradientXScale.getValue() / 2) + (ClickGUI.instance.categoryGradientX.getValue()), y + height + 1 + (ClickGUI.instance.categoryGradientYScale.getValue() / 2) + (ClickGUI.instance.categoryGradientY.getValue()), categoryGradientColor, categoryGradientColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                        GlStateManager.enableAlpha();
                        step3 += 1;
                    }

                    //panel bottom gradient
                    if (ClickGUI.instance.bottomGradient.getValue() && lastModuleButton) {
                        if (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both) {
                            GL11.glTranslatef(0.0f, ClickGUI.instance.panelExtensionsHeight.getValue() + 4.0f, 0.0f);
                        }
                        int panelBottomGradientColor = ClickGUI.instance.panelBottomGradientColor.getValue().getColor();
                        GlStateManager.disableAlpha();
                        RenderUtils2D.drawCustomRect(x + 5, startY + 1 - ClickGUI.instance.bottomGradientWidth.getValue(), x + width - 5, startY - 1, new Color(0,0, 0, 0).getRGB(), new Color(0,0, 0, 0).getRGB(), panelBottomGradientColor, panelBottomGradientColor);
                        GlStateManager.enableAlpha();
                        if (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both) {
                            GL11.glTranslatef(0.0f, (ClickGUI.instance.panelExtensionsHeight.getValue() + 4.0f) * -1.0f, 0.0f);
                        }
                    }


                    startY += 1;
                }

                //panel extensions startY adjust
                if ((lastModuleButton && extended) && (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both)) {
                    startY += ClickGUI.instance.panelExtensionsHeight.getValue() + 4;
                }


                //draw module button stuff
                int index2 = 0;
                for (ModuleButton button : elements) {
                    index2++;
                    if (extended) {
                        if (!panelTimer.passed(index2 * 25 / ClickGUI.instance.panelOpenSpeed.getValue())) continue;
                    } else {
                        if (panelTimer.passed((elements.size() - index2) * 25 / ClickGUI.instance.panelOpenSpeed.getValue())) continue;
                    }
                    button.render(mouseX, mouseY, translateDelta, partialTicks);

                    //module separator bars
                    if (ClickGUI.instance.moduleSeparators.getValue() && ClickGUI.instance.moduleSeparatorsOnTop.getValue()) {
                        moduleSeparators(button);
                    }
                }


                //panel outline
                if (ClickGUI.instance.outline.getValue()) {
                    RenderUtils2D.drawCustomLine(x + 4, y + height, x + 4, startY, ClickGUI.instance.outlineWidth.getValue(), outlineTopColor(), outlineDownColor());
                    RenderUtils2D.drawCustomLine(x + width - 4, y + height, x + width - 4, startY, ClickGUI.instance.outlineWidth.getValue(), outlineTopColor(), outlineDownColor());
                    if (!ClickGUI.instance.outlineDownToggle.getValue()) {
                        RenderUtils2D.drawCustomLine(x + 4, startY, x + width - 4, startY, ClickGUI.instance.outlineWidth.getValue(), outlineDownColor(), outlineDownColor());
                    }
                }

                //panel bottom fade extend
                if (ClickGUI.instance.guiCategoryPanelFadeDownExtend.getValue()) {
                    GlStateManager.disableAlpha();
                    RenderUtils2D.drawCustomRect(x + 5, startY - 2, x + width - 5, startY - 1 + ClickGUI.instance.panelFadeDownExtendHeight.getValue(), ClickGUI.instance.moduleBGColor.getValue().getColor(), ClickGUI.instance.moduleBGColor.getValue().getColor(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                    GlStateManager.enableAlpha();
                    if (ClickGUI.instance.panelFadeDownExtendOutline.getValue()) {
                        RenderUtils2D.drawCustomLine(x + 4, startY, x + 4, startY - 1 + ClickGUI.instance.panelFadeDownExtendHeight.getValue(), ClickGUI.instance.outlineWidth.getValue(), outlineDownColor(), new Color(0, 0, 0, 0).getRGB());
                        RenderUtils2D.drawCustomLine(x + width - 4, startY, x + width - 4, startY - 1 + ClickGUI.instance.panelFadeDownExtendHeight.getValue(), ClickGUI.instance.outlineWidth.getValue(), outlineDownColor(), new Color(0, 0, 0, 0).getRGB());
                    }
                }

                //panel base rect
                if (ClickGUI.instance.guiCategoryBase.getValue()) {
                    Color baseColor = new Color(outlineDownColor());
                    if (ClickGUI.instance.guiCategoryBaseRound.getValue()) {
                        RenderUtils2D.drawRoundedRect(x + 4 - (ClickGUI.instance.widthBase.getValue() / 2.0f), startY - 1, ClickGUI.instance.radiusBase.getValue(), x + width - 4 + (ClickGUI.instance.widthBase.getValue() / 2.0f), startY + ClickGUI.instance.heightBase.getValue(), false, ClickGUI.instance.arcTopRightBase.getValue(), ClickGUI.instance.arcTopLeftBase.getValue(), ClickGUI.instance.arcDownRightBase.getValue(), ClickGUI.instance.arcDownLeftBase.getValue(), new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), ClickGUI.instance.baseAlpha.getValue()).getRGB());
                    }
                    else {
                        RenderUtils2D.drawRect(x + 4 - (ClickGUI.instance.widthBase.getValue() / 2.0f), startY - 1, x + width - 4 + (ClickGUI.instance.widthBase.getValue() / 2.0f), startY + ClickGUI.instance.heightBase.getValue(), new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), ClickGUI.instance.baseAlpha.getValue()).getRGB());
                    }

                    //panel base outline
                    if (ClickGUI.instance.baseOutline.getValue()) {
                        if (ClickGUI.instance.guiCategoryBaseRound.getValue()) {
                            RenderUtils2D.drawCustomRoundedRectOutline(x + 4 - (ClickGUI.instance.widthBase.getValue() / 2.0f), startY - 1, x + width - 4 + (ClickGUI.instance.widthBase.getValue() / 2.0f), startY + ClickGUI.instance.heightBase.getValue(), ClickGUI.instance.radiusBase.getValue(), ClickGUI.instance.baseOutlineWidth.getValue(), ClickGUI.instance.arcTopRightBase.getValue(), ClickGUI.instance.arcTopLeftBase.getValue(), ClickGUI.instance.arcDownRightBase.getValue(), ClickGUI.instance.arcDownLeftBase.getValue(), ClickGUI.instance.baseOutlineTopToggle.getValue(), false, ClickGUI.instance.baseOutlineColor.getValue().getColor());
                        }
                        else {
                            RenderUtils2D.drawRectOutline(x + 4 - (ClickGUI.instance.widthBase.getValue() / 2.0f), startY - 1, x + width - 4 + (ClickGUI.instance.widthBase.getValue() / 2.0f), startY + ClickGUI.instance.heightBase.getValue(), ClickGUI.instance.baseOutlineColor.getValue().getColor(), ClickGUI.instance.baseOutlineTopToggle.getValue(), false);
                        }
                    }

                    //panel base rect glow
                    if (ClickGUI.instance.baseGlow.getValue()) {
                        RenderUtils2D.drawRoundedRectFade(x + 4 - (ClickGUI.instance.widthBase.getValue() / 2.0f) - (ClickGUI.instance.baseGlowWidth.getValue() / 2), startY - 1 - (ClickGUI.instance.baseGlowHeight.getValue() / 2), 1.0f, true, false,x + width - 4 + (ClickGUI.instance.widthBase.getValue() / 2.0f)+ (ClickGUI.instance.baseGlowWidth.getValue() / 2), startY + ClickGUI.instance.heightBase.getValue() + (ClickGUI.instance.baseGlowHeight.getValue() / 2), ClickGUI.instance.baseGlowColor.getValue().getColor());
                    }

                    //panel base patterns
                    if (ClickGUI.instance.baseRectPattern.getValue() != ClickGUI.BaseRectPattern.None) {
                        Color basePatternColor = new Color(ClickGUI.instance.baseRectPatternColor.getValue().getColorColor().getRed(), ClickGUI.instance.baseRectPatternColor.getValue().getColorColor().getGreen(), ClickGUI.instance.baseRectPatternColor.getValue().getColorColor().getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha());
                        Color basePatternOutlineColor = new Color(ClickGUI.instance.baseRectPatternOutlineColor.getValue().getColorColor().getRed(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getColorColor().getGreen(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getColorColor().getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha());
                        
                        if (ClickGUI.instance.baseRectPatternShadow.getValue()) {
                            RenderUtils2D.drawRoundedRectFade(x + (width / 2.0f) - (ClickGUI.instance.baseRectPatternShadowWidth.getValue() / 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() - (ClickGUI.instance.baseRectPatternShadowHeight.getValue() / 2.0f) + (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Single ? ClickGUI.instance.baseRectPatternShadowY.getValue() : 0), ClickGUI.instance.baseRectPatternShadowRadius.getValue(), true, false, x + (width / 2.0f) + (ClickGUI.instance.baseRectPatternShadowWidth.getValue() / 2.0f), startY + (ClickGUI.instance.heightBase.getValue() / 2.0f) - ClickGUI.instance.baseRectPatternYOffset.getValue() + (ClickGUI.instance.baseRectPatternShadowHeight.getValue() / 2.0f) + (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Single ? ClickGUI.instance.baseRectPatternShadowY.getValue() : 0), new Color(0, 0, 0, ClickGUI.instance.baseRectPatternShadowAlpha.getValue()).getRGB());
                        }

                        if (ClickGUI.instance.baseRectPattern.getValue() == ClickGUI.BaseRectPattern.Triangles) {
                            if (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Single) {
                                if (ClickGUI.instance.baseRectPatternSingleTrianglesExtra.getValue() == ClickGUI.BaseRectPatternTrianglesSingleExtra.Up) {
                                    if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                        basePatternTrianglesSingle(1, basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                    }
                                    basePatternTrianglesSingle(1, basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                                }

                                else if (ClickGUI.instance.baseRectPatternSingleTrianglesExtra.getValue() == ClickGUI.BaseRectPatternTrianglesSingleExtra.Down) {
                                    if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                        basePatternTrianglesSingle(2, basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                    }
                                    basePatternTrianglesSingle(2, basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                                }

                                else if (ClickGUI.instance.baseRectPatternSingleTrianglesExtra.getValue() == ClickGUI.BaseRectPatternTrianglesSingleExtra.Left) {
                                    if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                        basePatternTrianglesSingle(3, basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                    }
                                    basePatternTrianglesSingle(3, basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                                }

                                else {
                                    if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                        basePatternTrianglesSingle(4, basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                    }
                                    basePatternTrianglesSingle(4, basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                                }
                            }
                            else if (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Double) {
                                if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                    basePatternTrianglesDouble(basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), true, ClickGUI.instance.baseRectPatternSize.getValue());
                                }
                                basePatternTrianglesDouble(basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), false, ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()));
                            }
                        }

                        else if (ClickGUI.instance.baseRectPattern.getValue() == ClickGUI.BaseRectPattern.Circles) {
                            if (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Single) {
                                if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                    basePatternCirclesSingle(basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                }
                                basePatternCirclesSingle(basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                            }
                            else if (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Double) {
                                if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                    basePatternCirclesDouble(basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                }
                                basePatternCirclesDouble(basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                            }
                        }

                        else if (ClickGUI.instance.baseRectPattern.getValue() == ClickGUI.BaseRectPattern.Diamonds) {
                            if (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Single) {
                                if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                    basePatternDiamondsSingle(basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                }
                                basePatternDiamondsSingle(basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                            }
                            else if (ClickGUI.instance.baseRectPatternExtra.getValue() == ClickGUI.BaseRectPatternExtra.Double) {
                                if (ClickGUI.instance.baseRectPatternOutline.getValue()) {
                                    basePatternDiamondsDouble(basePatternOutlineColor.getRed(), basePatternOutlineColor.getGreen(), basePatternOutlineColor.getBlue(), ClickGUI.instance.baseRectPatternOutlineColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue(), true);
                                }
                                basePatternDiamondsDouble(basePatternColor.getRed(), basePatternColor.getGreen(), basePatternColor.getBlue(), ClickGUI.instance.baseRectPatternColor.getValue().getAlpha(), ClickGUI.instance.baseRectPatternSize.getValue() * (1.0f - ClickGUI.instance.baseRectPatternOutlineWidth.getValue()), false);
                            }
                        }
                    }
                }


                //panel extensions startY adjust
                if ((lastModuleButton && extended) && (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both)) {
                    startY -= ClickGUI.instance.panelExtensionsHeight.getValue() + 4;
                }
            }

            //category bar stuff
            float categoryRectStartX = x + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.rectWidth.getValue() / 2);
            float categoryRectEndX = x + width + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.rectWidth.getValue() / 2);

            float categoryRectStartY = y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue();
            float categoryRectEndY = y + (height + (ClickGUI.instance.rectHeight.getValue() / 2)) + ClickGUI.instance.rectY.getValue();

            Color categoryRectColor = new Color(ClickGUI.instance.categoryRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.categoryRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.categoryRectColor.getValue().getColorColor().getBlue(), ClickGUI.instance.categoryRectColor.getValue().getAlpha());

            //category bar hovered color
            if (isHovered(mouseX, mouseY) && ClickGUI.instance.categoryRectHoverDifColor.getValue()) {

                if (!ClickGUI.instance.categoryRectHoverColorSmooth.getValue()) {
                    categoryRectColor = new Color(ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getRed(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getGreen(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getBlue(), ClickGUI.instance.categoryRectHoverColor.getValue().getAlpha());
                }
                else {
                    storedCategoryHoverLoops.putIfAbsent(category.categoryName, 0);
                    int hoverLoops = storedCategoryHoverLoops.get(category.categoryName);
                    if (hoverLoops >= 300) {
                        hoverLoops = 300;
                    }
                    if (hoverLoops <= 0) {
                        hoverLoops = 0;
                    }
                    int nonHoveredToHoveredRed = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getRed(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getRed(), hoverLoops));
                    int nonHoveredToHoveredGreen = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getGreen(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getGreen(), hoverLoops));
                    int nonHoveredToHoveredBlue = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getBlue(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getBlue(), hoverLoops));
                    int nonHoveredToHoveredAlpha = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getAlpha(), ClickGUI.instance.categoryRectHoverColor.getValue().getAlpha(), hoverLoops));

                    categoryRectColor = new Color(nonHoveredToHoveredRed, nonHoveredToHoveredGreen, nonHoveredToHoveredBlue, nonHoveredToHoveredAlpha);
                    hoverLoops += ClickGUI.instance.categoryRectHoverColorSmoothFactorIn.getValue() * 10.0f;
                    storedCategoryHoverLoops.put(category.categoryName, hoverLoops);
                }
            }

            if (ClickGUI.instance.categoryRectHoverDifColor.getValue() && ClickGUI.instance.categoryRectHoverColorSmooth.getValue() && storedCategoryHoverLoops.containsKey(category.categoryName) && !isHovered(mouseX, mouseY)) {

                int hoverLoops = storedCategoryHoverLoops.get(category.categoryName);
                if (hoverLoops <= 0) {
                    hoverLoops = 0;
                }
                if (hoverLoops >= 300) {
                    hoverLoops = 300;
                }
                int nonHoveredToHoveredRed = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getRed(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getRed(), hoverLoops));
                int nonHoveredToHoveredGreen = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getGreen(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getGreen(), hoverLoops));
                int nonHoveredToHoveredBlue = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getBlue(), ClickGUI.instance.categoryRectHoverColor.getValue().getColorColor().getBlue(), hoverLoops));
                int nonHoveredToHoveredAlpha = (int)(MathUtilFuckYou.linearInterp(categoryRectColor.getAlpha(), ClickGUI.instance.categoryRectHoverColor.getValue().getAlpha(), hoverLoops));

                categoryRectColor = new Color(nonHoveredToHoveredRed, nonHoveredToHoveredGreen, nonHoveredToHoveredBlue, nonHoveredToHoveredAlpha);
                hoverLoops -= ClickGUI.instance.categoryRectHoverColorSmoothFactorOut.getValue() * 10.0f;
                storedCategoryHoverLoops.put(category.categoryName, hoverLoops);
            }

            //category bar render
            if (ClickGUI.instance.guiRoundRect.getValue()) {
                RenderUtils2D.drawRoundedRect(categoryRectStartX, categoryRectStartY, ClickGUI.instance.radius.getValue(),categoryRectEndX, categoryRectEndY, false, ClickGUI.instance.arcTopRight.getValue(), ClickGUI.instance.arcTopLeft.getValue(), ClickGUI.instance.arcDownRight.getValue(), ClickGUI.instance.arcDownLeft.getValue(), categoryRectColor.getRGB());
            }
            else {
                RenderUtils2D.drawRect(categoryRectStartX, categoryRectStartY, categoryRectEndX, categoryRectEndY, categoryRectColor.getRGB());
            }

            //category bottom bar
            if (ClickGUI.instance.categoryBar.getValue()) {
                RenderUtils2D.drawRect(x + 4 - (ClickGUI.instance.categoryBarXScale.getValue() / 2) + (ClickGUI.instance.categoryBarX.getValue()), y + height - (ClickGUI.instance.categoryBarYScale.getValue() / 2) + (ClickGUI.instance.categoryBarY.getValue()), x + width - 4 + (ClickGUI.instance.categoryBarXScale.getValue() / 2) + (ClickGUI.instance.categoryBarX.getValue()), y + height + 1 + (ClickGUI.instance.categoryBarYScale.getValue() / 2) + (ClickGUI.instance.categoryBarY.getValue()), ClickGUI.instance.barColor.getValue().getColor());
            }

            //category glow
            if (ClickGUI.instance.categoryGlow.getValue()) {
                RenderUtils2D.drawRoundedRectFade(x + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.categoryGlowWidth.getValue() / 2), y - (ClickGUI.instance.categoryGlowHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue(), 1.0f, true, false,x + width + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.categoryGlowWidth.getValue() / 2), y + (height + (ClickGUI.instance.categoryGlowHeight.getValue() / 2)) + ClickGUI.instance.rectY.getValue(), ClickGUI.instance.categoryGlowColor.getValue().getColor());
            }

            //category icons
            if (ClickGUI.instance.categoryIcons.getValue()) {
                if (ClickGUI.instance.categoryIconsBG.getValue()) {

                    int categoryIconsBGColor = ClickGUI.instance.categoryIconsBGColor.getValue().getColor();

                    if (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Left) {
                        float endX = ((categoryRectStartX) + 10.0f + ClickGUI.instance.categoryIconsBGSideX.getValue());

                        GL11.glTranslatef(((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (endX - (categoryRectStartX))) / 2.0f, ((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (categoryRectEndY - categoryRectStartY)) / 2.0f, 0.0f);

                        GL11.glTranslatef(((categoryRectStartX)) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()), (categoryRectStartY) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), ClickGUI.instance.categoryIconsBGScaleOutside.getValue());

                        RenderUtils2D.drawCustomCategoryRoundedRect((categoryRectStartX), categoryRectStartY, endX, categoryRectEndY, ClickGUI.instance.radius.getValue(), false, ClickGUI.instance.guiRoundRect.getValue() ? ClickGUI.instance.arcTopLeft.getValue() : false, false, ClickGUI.instance.guiRoundRect.getValue() ? ClickGUI.instance.arcDownLeft.getValue() : false, ClickGUI.instance.categoryIconsBGSideFade.getValue(), false, ClickGUI.instance.categoryIconsBGSideFadeSize.getValue(), categoryIconsBGColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), 1.0f / ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), 1.0f / ClickGUI.instance.categoryIconsBGScaleOutside.getValue());
                        GL11.glTranslatef(((categoryRectStartX)) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * -1.0f, (categoryRectStartY) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * -1.0f, 0.0f);

                        GL11.glTranslatef((((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (endX - (categoryRectStartX))) / 2.0f) * -1.0f, (((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (categoryRectEndY - categoryRectStartY)) / 2.0f) * -1.0f, 0.0f);
                    }
                    else {
                        float startX = categoryRectStartX - 10.0f - ClickGUI.instance.categoryIconsBGSideX.getValue();

                        GL11.glTranslatef(((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (categoryRectEndX - startX)) / 2.0f, ((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (categoryRectEndY - categoryRectStartY)) / 2.0f, 0.0f);

                        GL11.glTranslatef((startX) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()), (categoryRectStartY) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), ClickGUI.instance.categoryIconsBGScaleOutside.getValue());

                        RenderUtils2D.drawCustomCategoryRoundedRect(startX, categoryRectStartY, categoryRectEndX, categoryRectEndY, ClickGUI.instance.radius.getValue(), ClickGUI.instance.guiRoundRect.getValue() ? ClickGUI.instance.arcTopRight.getValue() : false, false, ClickGUI.instance.guiRoundRect.getValue() ? ClickGUI.instance.arcDownRight.getValue() : false, false, false, ClickGUI.instance.categoryIconsBGSideFade.getValue(), ClickGUI.instance.categoryIconsBGSideFadeSize.getValue(), categoryIconsBGColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), 1.0f / ClickGUI.instance.categoryIconsBGScaleOutside.getValue(), 1.0f / ClickGUI.instance.categoryIconsBGScaleOutside.getValue());
                        GL11.glTranslatef((startX) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * -1.0f, (categoryRectStartY) * (1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * -1.0f, 0.0f);

                        GL11.glTranslatef((((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (categoryRectEndX - startX)) / 2.0f) * -1.0f, (((1.0f - ClickGUI.instance.categoryIconsBGScaleOutside.getValue()) * (categoryRectEndY - categoryRectStartY)) / 2.0f) * -1.0f, 0.0f);
                    }
                }


                String categoryIconString = "";
                if (category.categoryName == "Client") {
                    categoryIconString = "0";
                }
                else if (category.categoryName == "Visuals") {
                    categoryIconString = "1";
                }
                else if (category.categoryName == "Movement") {
                    categoryIconString = "2";
                }
                else if (category.categoryName == "Other") {
                    categoryIconString = "3";
                }
                else if (category.categoryName == "Combat") {
                    categoryIconString = "4";
                }
                else if (category.categoryName == "HUD") {
                    categoryIconString = "5";
                }

                Color categoryIconsColor = new Color(ClickGUI.instance.categoryIconsColor.getValue().getColorColor().getRed(), ClickGUI.instance.categoryIconsColor.getValue().getColorColor().getGreen(), ClickGUI.instance.categoryIconsColor.getValue().getColorColor().getBlue(), ClickGUI.instance.categoryIconsColor.getValue().getAlpha());

                if (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Left) {
                    GL11.glTranslatef((x + 3 + ClickGUI.instance.categoryIconsX.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()), (y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()), 0.0f);
                    GL11.glScalef(ClickGUI.instance.categoryIconsScale.getValue(), ClickGUI.instance.categoryIconsScale.getValue(), ClickGUI.instance.categoryIconsScale.getValue());

                    FontManager.drawModuleMiniIcon(categoryIconString, (int)(x + 3 + ClickGUI.instance.categoryIconsX.getValue()), (int)(y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()), categoryIconsColor);

                    GL11.glScalef(1.0f / ClickGUI.instance.categoryIconsScale.getValue(), 1.0f / ClickGUI.instance.categoryIconsScale.getValue(), 1.0f / ClickGUI.instance.categoryIconsScale.getValue());
                    GL11.glTranslatef((x + 3 + ClickGUI.instance.categoryIconsX.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()) * -1.0f, (y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()) * -1.0f, 0.0f);
                }
                else {
                    GL11.glTranslatef((x + width - 13 - ClickGUI.instance.categoryIconsX.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()), (y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()), 0.0f);
                    GL11.glScalef(ClickGUI.instance.categoryIconsScale.getValue(), ClickGUI.instance.categoryIconsScale.getValue(), ClickGUI.instance.categoryIconsScale.getValue());

                    FontManager.drawModuleMiniIcon(categoryIconString, (int)(x + width - 13 - ClickGUI.instance.categoryIconsX.getValue()), (int)(y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()), categoryIconsColor);

                    GL11.glScalef(1.0f / ClickGUI.instance.categoryIconsScale.getValue(), 1.0f / ClickGUI.instance.categoryIconsScale.getValue(), 1.0f / ClickGUI.instance.categoryIconsScale.getValue());
                    GL11.glTranslatef((x + width - 13 - ClickGUI.instance.categoryIconsX.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()) * -1.0f, (y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()) * (1.0f - ClickGUI.instance.categoryIconsScale.getValue()) * -1.0f, 0.0f);
                }

                //category icons glow
                if (ClickGUI.instance.categoryIconsGlow.getValue()) {
                    int categoryIconsGlowColor = ClickGUI.instance.categoryIconsGlowColor.getValue().getColor();

                    GlStateManager.disableAlpha();
                    if (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Left) {
                        RenderUtils2D.drawCustomCircle((x + 3 + ClickGUI.instance.categoryIconsX.getValue()) + (FontManager.getIconWidth() / 2.0f), (y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()), ClickGUI.instance.categoryIconsGlowSize.getValue(), categoryIconsGlowColor, new Color(0, 0, 0, 0).getRGB());
                    }
                    else {
                        RenderUtils2D.drawCustomCircle((x + width - 13 - ClickGUI.instance.categoryIconsX.getValue()) + (FontManager.getIconWidth() / 2.0f), (y + ClickGUI.instance.rectY.getValue() + height / 2f + 2 + ClickGUI.instance.categoryIconsY.getValue()), ClickGUI.instance.categoryIconsGlowSize.getValue(), categoryIconsGlowColor, new Color(0, 0, 0, 0).getRGB());
                    }
                    GlStateManager.enableAlpha();
                }
            }


            //category bar outlines
            if (ClickGUI.instance.categoryRectOutline.getValue()) {
                int categoryRectOutlineColor = ClickGUI.instance.categoryRectOutlineColor.getValue().getColor();

                if (ClickGUI.instance.guiRoundRect.getValue()) {
                    RenderUtils2D.drawCustomRoundedRectOutline(categoryRectStartX, categoryRectStartY, categoryRectEndX, categoryRectEndY, ClickGUI.instance.radius.getValue(), ClickGUI.instance.categoryRectOutlineWidth.getValue(), ClickGUI.instance.arcTopRight.getValue(), ClickGUI.instance.arcTopLeft.getValue(), ClickGUI.instance.arcDownRight.getValue(), ClickGUI.instance.arcDownLeft.getValue(), false, ClickGUI.instance.categoryRectOutlineBottomLineToggle.getValue(), categoryRectOutlineColor);
                }
                else {
                    RenderUtils2D.drawRectOutline(categoryRectStartX, categoryRectStartY, categoryRectEndX, categoryRectEndY, ClickGUI.instance.categoryRectOutlineWidth.getValue(), categoryRectOutlineColor, false, ClickGUI.instance.categoryRectOutlineBottomLineToggle.getValue());
                }
            }

            //category names setup stuff
            float categoryNameTextX = CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Center ? (x + ClickGUI.instance.rectX.getValue() + ((width / 2.0f) - (mc.fontRenderer.getStringWidth(category.categoryName) / 2.0f))) : (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Left ? (x + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Left ? (((ClickGUI.instance.rectWidth.getValue() / 2) * -1.0f) + 10.0f + ClickGUI.instance.categoryIconsBGSideX.getValue()) : 0)) : (x + ClickGUI.instance.rectX.getValue() + width - mc.fontRenderer.getStringWidth(category.categoryName) + (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Right ? ((ClickGUI.instance.rectWidth.getValue() / 2) - 10.0f - ClickGUI.instance.categoryIconsBGSideX.getValue()) : 0)));
            float categoryNameTextY = y + ClickGUI.instance.rectY.getValue() + height / 2f - font.getHeight() / 2f - 1;

            float categoryNameCustomTextX = CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Center ? (x + ClickGUI.instance.rectX.getValue() + ((width / 2.0f) - (FontManager.fontRenderer.getStringWidth(category.categoryName) / 2.0f))) : (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Left ? (x + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Left ? (((ClickGUI.instance.rectWidth.getValue() / 2) * -1.0f) + 10.0f + ClickGUI.instance.categoryIconsBGSideX.getValue()) : 0)) : (x + ClickGUI.instance.rectX.getValue() + width - font.getStringWidth(category.categoryName) + (ClickGUI.instance.categoryIconsSide.getValue() == ClickGUI.CategoryIconsSides.Right ? ((ClickGUI.instance.rectWidth.getValue() / 2) - 10.0f - ClickGUI.instance.categoryIconsBGSideX.getValue()) : 0)));
            float categoryNameCustomTextY = y + ClickGUI.instance.rectY.getValue() + height / 2f - font.getHeight() / 2f + 2;

            int categoryTextColor = CustomFont.instance.categoryTextColor.getValue().getColor();

            //category name shadow gradient hover effect
            Color categoryShadowGradientColor = new Color(0, 0, 0, CustomFont.instance.categoryTextShadowGradientAlpha.getValue());
            if (isHovered(mouseX, mouseY) && ClickGUI.instance.categoryRectHoverShadowGradientAlpha.getValue()) {
                if (!ClickGUI.instance.categoryRectHoverShadowGradientAlphaSmooth.getValue()) {
                    categoryShadowGradientColor = new Color(0, 0, 0, ClickGUI.instance.categoryRectHoverShadowGradientNewAlpha.getValue());
                }
                else {
                    storedCategoryShadowGradientHoverLoops.putIfAbsent(category.categoryName, 0);
                    int hoverLoops = storedCategoryShadowGradientHoverLoops.get(category.categoryName);
                    if (hoverLoops >= 300) {
                        hoverLoops = 300;
                    }
                    if (hoverLoops <= 0) {
                        hoverLoops = 0;
                    }

                    int nonHoveredToHoveredAlpha = (int)(MathUtilFuckYou.linearInterp(categoryShadowGradientColor.getAlpha(), ClickGUI.instance.categoryRectHoverShadowGradientNewAlpha.getValue(), hoverLoops));

                    categoryShadowGradientColor = new Color(0, 0, 0, nonHoveredToHoveredAlpha);
                    hoverLoops += ClickGUI.instance.categoryRectHoverShadowGradientFactorIn.getValue() * 10.0f;
                    storedCategoryShadowGradientHoverLoops.put(category.categoryName, hoverLoops);
                }
            }

            if (ClickGUI.instance.categoryRectHoverShadowGradientAlpha.getValue() && ClickGUI.instance.categoryRectHoverColorSmooth.getValue() && storedCategoryShadowGradientHoverLoops.containsKey(category.categoryName) && !isHovered(mouseX, mouseY)) {
                int hoverLoops = storedCategoryShadowGradientHoverLoops.get(category.categoryName);
                if (hoverLoops <= 0) {
                    hoverLoops = 0;
                }
                if (hoverLoops >= 300) {
                    hoverLoops = 300;
                }
                int nonHoveredToHoveredAlpha = (int)(MathUtilFuckYou.linearInterp(categoryShadowGradientColor.getAlpha(), ClickGUI.instance.categoryRectHoverShadowGradientNewAlpha.getValue(), hoverLoops));

                categoryShadowGradientColor = new Color(0, 0, 0, nonHoveredToHoveredAlpha);
                hoverLoops -= ClickGUI.instance.categoryRectHoverShadowGradientFactorIn.getValue() * 10.0f;
                storedCategoryShadowGradientHoverLoops.put(category.categoryName, hoverLoops);
            }

            //category names
            if (CustomFont.instance.categoryFont.getValue() == CustomFont.FontMode.Minecraft) {
                //category names gradient shadow mc font
                if (CustomFont.instance.categoryTextShadowGradient.getValue()) {
                    RenderUtils2D.drawBetterRoundRectFade(categoryNameTextX + 3.0f + CustomFont.instance.categoryTextShadowGradientX.getValue() + CustomFont.instance.categoryTextX.getValue(), categoryNameTextY + (font.getHeight() / 2.0f) + CustomFont.instance.categoryTextShadowGradientY.getValue() + CustomFont.instance.categoryTextY.getValue(), categoryNameTextX + mc.fontRenderer.getStringWidth(category.categoryName) + 3.0f + CustomFont.instance.categoryTextShadowGradientX.getValue() + CustomFont.instance.categoryTextX.getValue(), categoryNameTextY + (font.getHeight() / 2.0f) + CustomFont.instance.categoryTextShadowGradientY.getValue() + CustomFont.instance.categoryTextY.getValue(), CustomFont.instance.categoryTextShadowGradientSize.getValue(), 70.0f,false, true, false, categoryShadowGradientColor.getRGB());
                }


                if (ClickGUI.instance.categoryRectHoverTextScale.getValue()) {
                    storedCategoryTextScaleLoops.putIfAbsent(category.categoryName, CustomFont.instance.categoryTextScale.getValue());
                    if (isHovered(mouseX, mouseY)) {
                        float hoverCategoryTextScaleLoops = storedCategoryTextScaleLoops.get(category.categoryName);
                        hoverCategoryTextScaleLoops += 0.1f * ClickGUI.instance.categoryRectHoverShadowGradientFactorOut.getValue();
                        storedCategoryTextScaleLoops.put(category.categoryName, hoverCategoryTextScaleLoops);
                    }
                    if (storedCategoryTextScaleLoops.containsKey(category.categoryName)) {
                        float hoverCategoryTextScaleLoops = storedCategoryTextScaleLoops.get(category.categoryName);
                        if (hoverCategoryTextScaleLoops <= CustomFont.instance.categoryTextScale.getValue()) {
                            hoverCategoryTextScaleLoops = CustomFont.instance.categoryTextScale.getValue();
                        }
                        if (hoverCategoryTextScaleLoops >= ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() / 2.0f))) {
                            hoverCategoryTextScaleLoops = ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() / 2.0f));
                        }
                        storedCategoryTextScaleLoops.put(category.categoryName, hoverCategoryTextScaleLoops);
                    }
                }


                if (CustomFont.instance.categoryTextPos.getValue() != CustomFont.TextPos.Left) {
                    if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Center) {
                        GL11.glTranslatef(((1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * mc.fontRenderer.getStringWidth(category.categoryName)) / 2.0f, 0.0f, 0.0f);
                    }
                    else if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Right) {
                        GL11.glTranslatef(((1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * mc.fontRenderer.getStringWidth(category.categoryName)), 0.0f, 0.0f);
                    }
                }

                GL11.glTranslatef((categoryNameTextX) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())), (categoryNameTextY) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())), 0.0f);
                if (ClickGUI.instance.categoryRectHoverTextScale.getValue() && storedCategoryTextScaleLoops.containsKey(category.categoryName)) {
                    GL11.glScalef(storedCategoryTextScaleLoops.get(category.categoryName), storedCategoryTextScaleLoops.get(category.categoryName), storedCategoryTextScaleLoops.get(category.categoryName));
                }
                else {
                    GL11.glScalef(CustomFont.instance.categoryTextScale.getValue(), CustomFont.instance.categoryTextScale.getValue(), CustomFont.instance.categoryTextScale.getValue());
                }

                GL11.glEnable(GL_TEXTURE_2D);
                mc.fontRenderer.drawString(category.categoryName, categoryNameTextX + CustomFont.instance.categoryTextX.getValue(), categoryNameTextY + CustomFont.instance.categoryTextY.getValue(), categoryTextColor, CustomFont.instance.textShadow.getValue());
                GL11.glDisable(GL_TEXTURE_2D);

                if (ClickGUI.instance.categoryRectHoverTextScale.getValue()) {
                    GL11.glScalef(1.0f / storedCategoryTextScaleLoops.get(category.categoryName), 1.0f / storedCategoryTextScaleLoops.get(category.categoryName), 1.0f / storedCategoryTextScaleLoops.get(category.categoryName));
                }
                else {
                    GL11.glScalef(1.0f / (CustomFont.instance.categoryTextScale.getValue()), 1.0f / (CustomFont.instance.categoryTextScale.getValue()), 1.0f / (CustomFont.instance.categoryTextScale.getValue()));
                }
                GL11.glTranslatef((categoryNameTextX) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * -1.0f, (categoryNameTextY) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * -1.0f, 0.0f);

                if (CustomFont.instance.categoryTextPos.getValue() != CustomFont.TextPos.Left) {
                    if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Center) {
                        GL11.glTranslatef((((1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * mc.fontRenderer.getStringWidth(category.categoryName)) / 2.0f) * -1.0f, 0.0f, 0.0f);
                    }
                    else if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Right) {
                        GL11.glTranslatef(((1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * mc.fontRenderer.getStringWidth(category.categoryName)) * -1.0f, 0.0f, 0.0f);
                    }
                }

                if (ClickGUI.instance.categoryRectHoverTextScale.getValue() && !isHovered(mouseX, mouseY)) {
                    float hoverCategoryTextScaleLoops = storedCategoryTextScaleLoops.get(category.categoryName);
                    hoverCategoryTextScaleLoops -= 0.1f * ClickGUI.instance.categoryRectHoverTextScaleFactorOut.getValue();
                    storedCategoryTextScaleLoops.put(category.categoryName, hoverCategoryTextScaleLoops);
                }
            }
            else {
                //category names gradient shadow custom font
                if (CustomFont.instance.categoryTextShadowGradient.getValue()) {
                    RenderUtils2D.drawBetterRoundRectFade(categoryNameTextX + 3.0f + CustomFont.instance.categoryTextShadowGradientX.getValue() + CustomFont.instance.categoryTextX.getValue(), categoryNameTextY + (font.getHeight() / 2.0f) + CustomFont.instance.categoryTextShadowGradientY.getValue() + CustomFont.instance.categoryTextY.getValue(), categoryNameTextX + font.getStringWidth(category.categoryName) + 3.0f + CustomFont.instance.categoryTextShadowGradientX.getValue() + CustomFont.instance.categoryTextX.getValue(), categoryNameTextY + (font.getHeight() / 2.0f) + CustomFont.instance.categoryTextShadowGradientY.getValue() + CustomFont.instance.categoryTextY.getValue(), CustomFont.instance.categoryTextShadowGradientSize.getValue(), 70.0f,false, true, false, categoryShadowGradientColor.getRGB());
                }


                if (ClickGUI.instance.categoryRectHoverTextScale.getValue()) {
                    storedCategoryTextScaleLoops.putIfAbsent(category.categoryName, CustomFont.instance.categoryTextScale.getValue());
                    if (isHovered(mouseX, mouseY)) {
                        float hoverCategoryTextScaleLoops = storedCategoryTextScaleLoops.get(category.categoryName);
                        hoverCategoryTextScaleLoops += 0.1f * ClickGUI.instance.categoryRectHoverShadowGradientFactorOut.getValue();
                        storedCategoryTextScaleLoops.put(category.categoryName, hoverCategoryTextScaleLoops);
                    }
                    if (storedCategoryTextScaleLoops.containsKey(category.categoryName)) {
                        float hoverCategoryTextScaleLoops = storedCategoryTextScaleLoops.get(category.categoryName);
                        if (hoverCategoryTextScaleLoops <= CustomFont.instance.categoryTextScale.getValue()) {
                            hoverCategoryTextScaleLoops = CustomFont.instance.categoryTextScale.getValue();
                        }
                        if (hoverCategoryTextScaleLoops >= ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() / 2.0f))) {
                            hoverCategoryTextScaleLoops = ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.categoryRectHoverTextScaleNewScale.getValue() / 2.0f));
                        }
                        storedCategoryTextScaleLoops.put(category.categoryName, hoverCategoryTextScaleLoops);
                    }
                }


                if (CustomFont.instance.categoryTextPos.getValue() != CustomFont.TextPos.Left) {
                    if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Center) {
                        GL11.glTranslatef(((1.0f - (ClickGUI.instance.categoryRectHoverTextScale.getValue() ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * FontManager.getWidth(category.categoryName)) / 2.0f, 0.0f, 0.0f);
                    }
                    else if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Right) {
                        GL11.glTranslatef(((1.0f - (ClickGUI.instance.categoryRectHoverTextScale.getValue() ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * FontManager.getWidth(category.categoryName)), 0.0f, 0.0f);
                    }
                }

                GL11.glTranslatef((categoryNameCustomTextX) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())), (categoryNameCustomTextY) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())), 0.0f);
                if (ClickGUI.instance.categoryRectHoverTextScale.getValue() && storedCategoryTextScaleLoops.containsKey(category.categoryName)) {
                    GL11.glScalef(storedCategoryTextScaleLoops.get(category.categoryName), storedCategoryTextScaleLoops.get(category.categoryName), storedCategoryTextScaleLoops.get(category.categoryName));
                }
                else {
                    GL11.glScalef(CustomFont.instance.categoryTextScale.getValue(), CustomFont.instance.categoryTextScale.getValue(), CustomFont.instance.categoryTextScale.getValue());
                }

                if (CustomFont.instance.categoryTextShadow.getValue()) {
                    fontManager.drawShadowCategory(category.categoryName, categoryNameCustomTextX + CustomFont.instance.categoryTextX.getValue(), categoryNameCustomTextY + CustomFont.instance.categoryTextY.getValue(), categoryTextColor);
                }
                else {
                    fontManager.drawCategory(category.categoryName, categoryNameCustomTextX + CustomFont.instance.categoryTextX.getValue(), categoryNameCustomTextY + CustomFont.instance.categoryTextY.getValue(), categoryTextColor);
                }

                if (ClickGUI.instance.categoryRectHoverTextScale.getValue()) {
                    GL11.glScalef(1.0f / storedCategoryTextScaleLoops.get(category.categoryName), 1.0f / storedCategoryTextScaleLoops.get(category.categoryName), 1.0f / storedCategoryTextScaleLoops.get(category.categoryName));
                }
                else {
                    GL11.glScalef(1.0f / (CustomFont.instance.categoryTextScale.getValue()), 1.0f / (CustomFont.instance.categoryTextScale.getValue()), 1.0f / (CustomFont.instance.categoryTextScale.getValue()));
                }
                GL11.glTranslatef((categoryNameCustomTextX) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * -1.0f, (categoryNameCustomTextY) * (1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * -1.0f, 0.0f);

                if (CustomFont.instance.categoryTextPos.getValue() != CustomFont.TextPos.Left) {
                    if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Center) {
                        GL11.glTranslatef((((1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * FontManager.getWidth(category.categoryName)) / 2.0f) * -1.0f, 0.0f, 0.0f);
                    }
                    else if (CustomFont.instance.categoryTextPos.getValue() == CustomFont.TextPos.Right) {
                        GL11.glTranslatef(((1.0f - ((ClickGUI.instance.categoryRectHoverTextScale.getValue()) ? storedCategoryTextScaleLoops.get(category.categoryName) : CustomFont.instance.categoryTextScale.getValue())) * FontManager.getWidth(category.categoryName)) * -1.0f, 0.0f, 0.0f);
                    }
                }

                if (ClickGUI.instance.categoryRectHoverTextScale.getValue() && !isHovered(mouseX, mouseY)) {
                    float hoverCategoryTextScaleLoops = storedCategoryTextScaleLoops.get(category.categoryName);
                    hoverCategoryTextScaleLoops -= 0.1f * ClickGUI.instance.categoryRectHoverTextScaleFactorOut.getValue();
                    storedCategoryTextScaleLoops.put(category.categoryName, hoverCategoryTextScaleLoops);
                }
            }


            staticY = y;

            if (staticY < 0) {
                staticY = 500;
            }
        }
    }
    public void drawShadowsAndGlow(int mouseX, int mouseY) {
        if (this.dragging) {
            x = x2 + mouseX;
            y = y2 + mouseY;
        }

        calcHeight();

        //shadows
        if (ClickGUI.instance.guiCategoryShadow.getValue()) {
            RenderUtils2D.drawRoundedRectFade(x - (((width * ClickGUI.instance.shadowSizeFactorX.getValue()) - width) / 2), y - (((height * ClickGUI.instance.shadowSizeFactorY.getValue()) - height) / 2), ClickGUI.instance.shadowRadiusCategory.getValue(), true, false,(x + (width * ClickGUI.instance.shadowSizeFactorX.getValue())) - (((width * ClickGUI.instance.shadowSizeFactorX.getValue()) - width) / 2), (y + (height * ClickGUI.instance.shadowSizeFactorY.getValue())) - (((height * ClickGUI.instance.shadowSizeFactorY.getValue()) - height) / 2), new Color(0, 0, 0, ClickGUI.instance.shadowAlpha.getValue()).getRGB());
        }

        if ((lastModuleButton && extended) && (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both)) {
            startY += ClickGUI.instance.panelExtensionsHeight.getValue() + 2;
        }

        if (ClickGUI.instance.guiModuleShadow.getValue()) {
            RenderUtils2D.drawBetterRoundRectFade(x + 4, y + height, x + width - 4, startY - 2, ClickGUI.instance.moduleShadowSizeFactor.getValue(), 70.0f,true, ClickGUI.instance.guiModuleShadowFilled.getValue(), ClickGUI.instance.guiCategoryPanelFadeDownExtend.getValue(), new Color(0, 0, 0, ClickGUI.instance.shadowAlphaModules.getValue()).getRGB());
        }

        if (ClickGUI.instance.panelBaseShadow.getValue() && ClickGUI.instance.guiCategoryBase.getValue()) {
            RenderUtils2D.drawRoundedRectFade((x + 4 - (ClickGUI.instance.outlineWidth.getValue() / 3) - (ClickGUI.instance.widthBase.getValue() / 2.0f)) - (ClickGUI.instance.panelBaseShadowWidth.getValue() / 2.0f), (startY - 1) - (ClickGUI.instance.panelBaseShadowHeight.getValue() / 2.0f), ClickGUI.instance.panelBaseShadowRadius.getValue(), ClickGUI.instance.panelBaseShadowFilled.getValue(), false, (x + width - 4 + (ClickGUI.instance.outlineWidth.getValue() / 3) + (ClickGUI.instance.widthBase.getValue() / 2.0f)) + (ClickGUI.instance.panelBaseShadowWidth.getValue() / 2.0f), (startY + ClickGUI.instance.heightBase.getValue()) + (ClickGUI.instance.panelBaseShadowHeight.getValue() / 2.0f), new Color(0, 0, 0, ClickGUI.instance.panelBaseShadowAlpha.getValue()).getRGB());
        }

        if ((lastModuleButton && extended) && (ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Bottom || ClickGUI.instance.panelExtensions.getValue() == ClickGUI.PanelExtensions.Both)) {
            startY -= ClickGUI.instance.panelExtensionsHeight.getValue() + 2;
        }


        //category bar hover effects
        if (ClickGUI.instance.categoryRectHoverEffect.getValue()) {
            if (ClickGUI.instance.categoryRectHoverParticlesMode.getValue() != ClickGUI.CategoryRectHoverParticlesMode.None) {

                if (renderLoopsCategoryRectHoverParticles >= 1000) {
                    renderLoopsCategoryRectHoverParticles = 0;
                }

                if ((renderLoopsCategoryRectHoverParticles % (int)(1.0f / (ClickGUI.instance.categoryRectHoverParticlesGenerateRate.getValue() / 3.0f)) == 0) && isHovered(mouseX, mouseY)) {
                    categoryRectHoverParticlesList.put(categoryRectHoverParticlesId, new Vector2f((float)(((x + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.rectWidth.getValue() / 2)) + ClickGUI.instance.categoryRectHoverParticlesStartXOffset.getValue()) + (((width + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.rectWidth.getValue() / 2)) - ClickGUI.instance.categoryRectHoverParticlesEndXOffset.getValue() - ClickGUI.instance.categoryRectHoverParticlesStartXOffset.getValue()) * Math.random())), (y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue())));
                    categoryRectHoverParticlesOriginalYs.put(categoryRectHoverParticlesId, y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue());
                    float randomSpeed = 0.0f;
                    if (ClickGUI.instance.categoryRectHoverParticlesRiseSpeedRandom.getValue()) {
                        randomSpeed = (float)(Math.random() * ClickGUI.instance.categoryRectHoverParticlesRiseSpeedRandomMax.getValue());
                        if (randomSpeed < ClickGUI.instance.categoryRectHoverParticlesRiseSpeedRandomMin.getValue()) {
                            randomSpeed = ClickGUI.instance.categoryRectHoverParticlesRiseSpeedRandomMin.getValue();
                        }
                    }
                    categoryRectHoverParticlesSpeed.put(categoryRectHoverParticlesId, ClickGUI.instance.categoryRectHoverParticlesRiseSpeedRandom.getValue() ? (randomSpeed) : (ClickGUI.instance.categoryRectHoverParticlesRiseSpeed.getValue()));

                    float randomSize = 0.0f;
                    if (ClickGUI.instance.categoryRectHoverParticlesRandomSize.getValue()) {
                        randomSize = (float)(Math.random() * ClickGUI.instance.categoryRectHoverParticlesRandomSizeMax.getValue());
                        if (randomSize < ClickGUI.instance.categoryRectHoverParticlesRandomSizeMin.getValue()) {
                            randomSize = ClickGUI.instance.categoryRectHoverParticlesRandomSizeMin.getValue();
                        }
                    }
                    categoryRectHoverParticlesSize.put(categoryRectHoverParticlesId, ClickGUI.instance.categoryRectHoverParticlesRandomSize.getValue() ? (randomSize) : ClickGUI.instance.categoryRectHoverParticlesSize.getValue());

                    float triAngle;
                    if (ClickGUI.instance.categoryRectHoverParticlesTriangleRandomAngle.getValue()) {
                        triAngle = (float)(Math.random() * 360.0f);
                    }
                    else {
                        triAngle = ClickGUI.instance.categoryRectHoverParticlesTrianglesAngle.getValue();
                    }
                    categoryRectHoverParticlesTriAngle.put(categoryRectHoverParticlesId, triAngle);
                    float triSpinSpeed;
                    if (ClickGUI.instance.categoryRectHoverParticlesRandomTriangleSpinSpeed.getValue()) {
                        triSpinSpeed = (float)(Math.random() * ClickGUI.instance.categoryRectHoverParticlesRandomTriangleSpinSpeedMax.getValue());
                        if (triSpinSpeed < ClickGUI.instance.categoryRectHoverParticlesRandomTriangleSpinSpeedMin.getValue()) {
                            triSpinSpeed = ClickGUI.instance.categoryRectHoverParticlesRandomTriangleSpinSpeedMin.getValue();
                        }
                    }
                    else {
                        triSpinSpeed = ClickGUI.instance.categoryRectHoverParticlesTrianglesSpinSpeed.getValue();
                    }
                    categoryRectHoverParticlesTriSpinSpeed.put(categoryRectHoverParticlesId, triSpinSpeed);
                    categoryRectHoverParticlesId += 1;
                    if (categoryRectHoverParticlesId >= 500) {
                        categoryRectHoverParticlesId = 0;
                    }
                }


                for (Map.Entry<Integer, Vector2f> entry : new HashMap<>(categoryRectHoverParticlesList).entrySet()) {

                    float threader = ((categoryRectHoverParticlesOriginalYs.get(entry.getKey()) - entry.getValue().y) / (categoryRectHoverParticlesOriginalYs.get(entry.getKey()) - (categoryRectHoverParticlesOriginalYs.get(entry.getKey()) - ClickGUI.instance.categoryRectHoverParticlesHeightCap.getValue())));
                    int theAlpha = (ClickGUI.instance.categoryRectHoverParticlesScaleFadeMode.getValue() == ClickGUI.CategoryRectHoverParticlesScaleFadeMode.Alpha || ClickGUI.instance.categoryRectHoverParticlesScaleFadeMode.getValue() == ClickGUI.CategoryRectHoverParticlesScaleFadeMode.Both) ? ((int)((ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getAlpha() * -1.0f * Math.pow(threader, ClickGUI.instance.categoryRectHoverParticlesAlphaFadeFactor.getValue() / 10.0f)) + ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getAlpha())) : (ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getAlpha());
                    if (theAlpha > ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getAlpha()) {
                        theAlpha = ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getAlpha();
                    }
                    if (theAlpha <= 0) {
                        theAlpha = 0;
                    }

                    float theScale = (ClickGUI.instance.categoryRectHoverParticlesScaleFadeMode.getValue() == ClickGUI.CategoryRectHoverParticlesScaleFadeMode.Scale || ClickGUI.instance.categoryRectHoverParticlesScaleFadeMode.getValue() == ClickGUI.CategoryRectHoverParticlesScaleFadeMode.Both) ? ((float)((categoryRectHoverParticlesSize.get(entry.getKey()) * -1.0f * Math.pow(threader, ClickGUI.instance.categoryRectHoverParticlesScaleFadeFactor.getValue() / 10.0f)) + categoryRectHoverParticlesSize.get(entry.getKey()))) : (categoryRectHoverParticlesSize.get(entry.getKey()));
                    if (theScale > categoryRectHoverParticlesSize.get(entry.getKey())) {
                        theScale = categoryRectHoverParticlesSize.get(entry.getKey());
                    }
                    if (theScale <= 0.0f) {
                        theScale = 0.0f;
                    }

                    GlStateManager.disableAlpha();
                    Color hoverParticleColor = new Color(ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getColorColor().getRed(), ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getColorColor().getGreen(), ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getColorColor().getBlue(), ClickGUI.instance.categoryRectHoverParticlesColor.getValue().getAlpha());
                    int realHoverParticleColor = new Color(hoverParticleColor.getRed(), hoverParticleColor.getGreen(), hoverParticleColor.getBlue(), theAlpha).getRGB();
                    if (ClickGUI.instance.categoryRectHoverParticlesMode.getValue() == ClickGUI.CategoryRectHoverParticlesMode.Circles) {
                        RenderUtils2D.drawCircle(entry.getValue().x, entry.getValue().y, theScale, realHoverParticleColor);
                    }
                    else if (ClickGUI.instance.categoryRectHoverParticlesMode.getValue() == ClickGUI.CategoryRectHoverParticlesMode.Diamonds) {
                        RenderUtils2D.drawRhombus(entry.getValue().x, entry.getValue().y, theScale, realHoverParticleColor);
                    }
                    else if (ClickGUI.instance.categoryRectHoverParticlesMode.getValue() == ClickGUI.CategoryRectHoverParticlesMode.Triangles) {
                        float theTriAngle = categoryRectHoverParticlesTriAngle.get(entry.getKey());
                        if (ClickGUI.instance.categoryRectHoverParticlesTrianglesSpin.getValue()) theTriAngle += categoryRectHoverParticlesTriSpinSpeed.get(entry.getKey());
                        if (theTriAngle >= 360.0f) theTriAngle = 0.0f;

                        GL11.glTranslatef(entry.getValue().x, entry.getValue().y, 0.0f);
                        GL11.glRotatef(theTriAngle, 0.0f, 0.0f, 1.0f);
                        GL11.glTranslatef(entry.getValue().x * -1.0f, entry.getValue().y * -1.0f, 0.0f);

                        RenderUtils2D.drawEquilateralTriangle(entry.getValue().x, entry.getValue().y, false, theScale, realHoverParticleColor);

                        GL11.glTranslatef(entry.getValue().x, entry.getValue().y, 0.0f);
                        GL11.glRotatef((theTriAngle) * -1.0f, 0.0f, 0.0f, 1.0f);
                        GL11.glTranslatef(entry.getValue().x * -1.0f, entry.getValue().y * -1.0f, 0.0f);

                        if (ClickGUI.instance.categoryRectHoverParticlesTrianglesSpin.getValue()) categoryRectHoverParticlesTriAngle.put(entry.getKey(), theTriAngle);
                    }
                    GlStateManager.enableAlpha();

                    entry.getValue().y -= categoryRectHoverParticlesSpeed.get(entry.getKey());

                    if (entry.getValue().y <= categoryRectHoverParticlesOriginalYs.get(entry.getKey()) - ClickGUI.instance.categoryRectHoverParticlesHeightCap.getValue())  {
                        categoryRectHoverParticlesList.remove(entry.getKey());
                        categoryRectHoverParticlesOriginalYs.remove(entry.getKey());
                        categoryRectHoverParticlesSpeed.remove(entry.getKey());
                        categoryRectHoverParticlesSize.remove(entry.getKey());
                        categoryRectHoverParticlesTriAngle.remove(entry.getKey());
                        categoryRectHoverParticlesTriSpinSpeed.remove(entry.getKey());
                    }
                }

                renderLoopsCategoryRectHoverParticles += 1;
            }
        }

        //horn shadows
        if (ClickGUI.instance.rectHornsShadow.getValue() && ClickGUI.instance.rectHorns.getValue()) {
            GlStateManager.disableAlpha();
            RenderUtils2D.drawCustomCircle((x + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.rectWidth.getValue() / 2) + ClickGUI.instance.rectHornsX.getValue() + ClickGUI.instance.rectHornsShadowXOffsetLeft.getValue() - ClickGUI.instance.rectHornsScale.getValue() / 2.0f), (y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue() + ClickGUI.instance.rectHornsY.getValue() + ClickGUI.instance.rectHornsSHadowsYOffset.getValue() - ClickGUI.instance.rectHornsScale.getValue() / 2.0f), ClickGUI.instance.rectHornsShadowSize.getValue() * 3.0f, new Color(0, 0, 0, ClickGUI.instance.rectHornsShadowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
            RenderUtils2D.drawCustomCircle((x + width + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.rectWidth.getValue() / 2) + ClickGUI.instance.rectHornsX.getValue() - ClickGUI.instance.rectHornsShadowXOffsetRight.getValue() - ClickGUI.instance.rectHornsScale.getValue() / 2.0f), (y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue() + ClickGUI.instance.rectHornsY.getValue() + ClickGUI.instance.rectHornsSHadowsYOffset.getValue() - ClickGUI.instance.rectHornsScale.getValue() / 2.0f), ClickGUI.instance.rectHornsShadowSize.getValue() * 3.0f, new Color(0, 0, 0, ClickGUI.instance.rectHornsShadowAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
            GlStateManager.enableAlpha();
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
            x2 = this.x - mouseX;
            y2 = (this.y) - mouseY;
            dragging = true;
            if (category.isHUD)
                Collections.swap(HUDEditorRenderer.instance.panels, 0, HUDEditorRenderer.instance.panels.indexOf(this));
            else Collections.swap(ClickGUIRenderer.instance.panels, 0, ClickGUIRenderer.instance.panels.indexOf(this));
            return true;
        }
        if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
            extended = !extended;
            panelTimer.reset();
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
        for (Component part : elements) {
            part.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Component part : elements) {
            part.keyTyped(typedChar, keyCode);
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        if (!Component.anyExpanded) {
            int startX = (int)(x + ClickGUI.instance.rectX.getValue() - (ClickGUI.instance.rectWidth.getValue() / 2));
            int endX = (int)(x + width + ClickGUI.instance.rectX.getValue() + (ClickGUI.instance.rectWidth.getValue() / 2));
            int startY = (int)(y - (ClickGUI.instance.rectHeight.getValue() / 2) + ClickGUI.instance.rectY.getValue());
            int endY = (int)(y + (height + (ClickGUI.instance.rectHeight.getValue() / 2)) + ClickGUI.instance.rectY.getValue());
            return mouseX >= Math.min(startX, endX) && mouseX <= Math.max(startX, endX) && mouseY >= Math.min(startY, endY) && mouseY <= Math.max(startY, endY);
        }
        else {
            return false;
        }
    }

}
