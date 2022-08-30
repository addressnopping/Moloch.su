package net.spartanb312.base.gui.components;

import me.thediamondsword5.moloch.gui.components.StringInput;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.core.setting.settings.*;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.client.ModuleManager;
import me.thediamondsword5.moloch.core.setting.settings.ColorSetting;
import me.thediamondsword5.moloch.core.setting.settings.VisibilitySetting;
import me.thediamondsword5.moloch.gui.components.ColorButton;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import me.thediamondsword5.moloch.gui.components.VisibilityButton;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.core.setting.NumberSetting;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.module.ListenerSetting;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.SoundUtil;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class ModuleButton extends net.spartanb312.base.gui.Component {

    public static ModuleButton instance;

    public List<net.spartanb312.base.gui.Component> settings = new ArrayList<>();
    public Module module;
    public Timer buttonTimer = new Timer();
    public static HashMap<String, Float> storedLocalLoops = new HashMap<>();
    public static HashMap<String, Integer> storedDecayFactors = new HashMap<>();


    public static HashMap<String, Float> storedScaleLoops = new HashMap<>();
    public static HashMap<String, Integer> storedScaleDecayFactors = new HashMap<>();

    public static HashMap<String, Float> storedAlphaLoops = new HashMap<>();
    public static HashMap<String, Integer> storedAlphaDecayFactors = new HashMap<>();

    public static HashMap<String, Float> storedHoverScaleLoops = new HashMap<>();

    public static HashMap<String, Integer> storedHovered = new HashMap<>();
    public static HashMap<String, Float> storedTextScaleHovered = new HashMap<>();

    public static HashMap<String, Float> storedTextEnableLoops = new HashMap<>();
    public static HashMap<String, Integer> storedTextEnableDecayFactors = new HashMap<>();

    public static HashMap<String, Float> storedDisableSideColorModeLoops = new HashMap<>();
    public static HashMap<String, Integer> storedDisableSideColorModeDecayFactors = new HashMap<>();

    public static HashMap<String, Float> storedSideIconColorLoops = new HashMap<>();
    public static HashMap<String, Integer> storedSideIconColorDecayFactors = new HashMap<>();

    static float moduleNameX;
    static float moduleNameXCentered;
    static float moduleNameXRight;
    static float moduleCustomFontNameY;
    static boolean bigIcons = ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.Dots && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None;


    public static String staticString = "";
    public static float currentTextWidth;


    public ModuleButton(Module module, int width, int height, Panel father) {
        this.module = module;
        this.width = width;
        this.height = height;
        this.father = father;
        instance = this;
        setup();
    }

    public void setup() {
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting)
                settings.add(new BooleanButton((BooleanSetting) setting, width, height, father, false, module));
            else if (setting instanceof VisibilitySetting)
                settings.add(new VisibilityButton((VisibilitySetting) setting, width, height, father, module));
            else if (setting instanceof ColorSetting)
                settings.add(new ColorButton((ColorSetting) setting, width, height, father, module));
            else if (setting instanceof IntSetting || setting instanceof FloatSetting || setting instanceof DoubleSetting)
                settings.add(new NumberSlider((NumberSetting<?>) setting, width, height, father, false, module));
            else if (setting instanceof EnumSetting)
                settings.add(new EnumButton((EnumSetting<?>) setting, width, height, father, module));
            else if (setting instanceof BindSetting)
                settings.add(new BindButton((BindSetting) setting, width, height, father, module));
            else if (setting instanceof ListenerSetting)
                settings.add(new ActionButton((ListenerSetting) setting, width, height, father, module));
            else if (setting instanceof StringSetting)
                settings.add(new StringInput((StringSetting) setting, width, height, father, module));
        }
    }

    private static int extendedMove () {
        return (ClickGUI.instance.moduleTextExtendedMove.getValue() ? 7 : 0);
    }


    public static float loopsAndShit(HashMap<String, Float> storage, HashMap<String, Integer> decay, Module module2, float factor) {
        float output;

        storage.putIfAbsent(module2.name, 300.0f);
        decay.putIfAbsent(module2.name, 169);
        float localLoops = storage.get(module2.name);
        int decayFactor = decay.get(module2.name);
        if (localLoops > 300) {
            localLoops = 300;
        }
        if (decayFactor > 169) {
            decayFactor = 169;
        }
        if (module2.moduleEnableFlag) {
            localLoops = 0.0f;
            decayFactor = 0;
        }
        if (module2.moduleDisableFlag) {
            localLoops = 0.0f;
            decayFactor = 0;
        }
        output = localLoops;
        localLoops += (30 * factor) * Math.pow(0.97f , decayFactor);
        decayFactor += 1;
        storage.put(module2.name, localLoops);
        decay.put(module2.name, decayFactor);
        return output;
    }

    private int SideIconXOffset() {
        return (ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? (ClickGUI.instance.sideIconXOffset.getValue() * -1) : (ClickGUI.instance.sideIconXOffset.getValue()));
    }

    private int moduleTextColor() {
        Color moduleTextColorcolor = new Color(CustomFont.instance.moduleTextColor.getValue().getColorColor().getRed(), CustomFont.instance.moduleTextColor.getValue().getColorColor().getGreen(), CustomFont.instance.moduleTextColor.getValue().getColorColor().getBlue(), CustomFont.instance.moduleTextColor.getValue().getAlpha());
        return moduleTextColorcolor.getRGB();
    }

    private int enabledTextColor(float loopsNumText, float loopsNumSide) {
        float enabledSideXTextOffset = (ClickGUI.instance.enabledSideMove.getValue() ? (module.isEnabled() ? (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)) : (ClickGUI.instance.enabledSideX.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (module.isEnabled() ? ClickGUI.instance.enabledSideX.getValue() : 0));

        Color enabledTextColorcolor = ClickGUI.instance.enabledTextSmooth.getValue() && ClickGUI.instance.enabledTextDifColor.getValue() ? (new Color((int)((new Color(moduleTextColor()).getRed()) + (loopsNumText * ((ClickGUI.instance.enabledTextColor.getValue().getColorColor().getRed() - new Color(moduleTextColor()).getRed()) / 300.0f))), (int)((new Color(moduleTextColor()).getGreen()) + (loopsNumText * ((ClickGUI.instance.enabledTextColor.getValue().getColorColor().getGreen() - new Color(moduleTextColor()).getGreen()) / 300.0f))), (int)((new Color(moduleTextColor()).getBlue()) + (loopsNumText * ((ClickGUI.instance.enabledTextColor.getValue().getColorColor().getBlue() - new Color(moduleTextColor()).getBlue()) / 300.0f))), (int)((CustomFont.instance.moduleTextColor.getValue().getAlpha()) + (loopsNumText * ((ClickGUI.instance.enabledTextColor.getValue().getAlpha() - CustomFont.instance.moduleTextColor.getValue().getAlpha()) / 300.0f))))) : (ClickGUI.instance.enabledTextColor.getValue().getColorColor());

        float minecraftTextX = CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset));
        float minecraftTextY = ((int) (y + height / 2 - font.getHeight() / 2f)) + CustomFont.instance.textOffset.getValue();

        float customTextX = CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)));
        float customTextY = ((int) (y + height / 2 - font.getHeight() / 2f) + 2) + CustomFont.instance.textOffset.getValue();

        if (ClickGUI.instance.enabledTextBrightnessRoll.getValue()) {
            enabledTextColorcolor = ColorUtil.rolledBrightness(enabledTextColorcolor, ClickGUI.instance.enabledTextBrightRollMax.getValue(), ClickGUI.instance.enabledTextBrightRollMin.getValue(), ClickGUI.instance.enabledTextBrightRollSpeed.getValue(), ClickGUI.instance.enabledTextBrightRollAxis.getValue() == ClickGUI.EnabledTextBrightRollAxis.X ? (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft ? (minecraftTextX) : (customTextX)) : (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft ? (minecraftTextY) : (customTextY)), ClickGUI.instance.enabledTextBrightRollLength.getValue(), ClickGUI.instance.enabledTextBrightRollAxis.getValue() == ClickGUI.EnabledTextBrightRollAxis.X ? (ClickGUI.instance.enabledTextBrightRollDirectionX.getValue() == ClickGUI.EnabledTextBrightRollDirectionX.Right) : (ClickGUI.instance.enabledTextBrightRollDirectionY.getValue() == ClickGUI.EnabledTextBrightRollDirectionY.Up), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.Y);
        }

        return enabledTextColorcolor.getRGB();
    }

    private int disabledTextColor(float loopsNumText) {
        return ClickGUI.instance.enabledTextSmooth.getValue() && ClickGUI.instance.enabledTextDifColor.getValue() ? (new Color((int)(ClickGUI.instance.enabledTextColor.getValue().getColorColor().getRed() + (loopsNumText * ((new Color(moduleTextColor()).getRed() - ClickGUI.instance.enabledTextColor.getValue().getColorColor().getRed()) / 300.0f))), (int)(ClickGUI.instance.enabledTextColor.getValue().getColorColor().getGreen() + (loopsNumText * ((new Color(moduleTextColor()).getGreen() - ClickGUI.instance.enabledTextColor.getValue().getColorColor().getGreen()) / 300.0f))), (int)(ClickGUI.instance.enabledTextColor.getValue().getColorColor().getBlue() + (loopsNumText * ((new Color(moduleTextColor()).getBlue() - ClickGUI.instance.enabledTextColor.getValue().getColorColor().getBlue()) / 300.0f))), (int)(ClickGUI.instance.enabledTextColor.getValue().getAlpha() + (loopsNumText * ((CustomFont.instance.moduleTextColor.getValue().getAlpha() - ClickGUI.instance.enabledTextColor.getValue().getAlpha()) / 300.0f)))).getRGB()) : moduleTextColor();
    }

    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        GlStateManager.disableAlpha();

        Color enabledSideColor = new Color(ClickGUI.instance.enabledSideColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledSideColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledSideColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enabledSideColor.getValue().getAlpha());
        Color disabledSideColor = new Color(ClickGUI.instance.disabledSideColorModeColor.getValue().getColorColor().getRed(), ClickGUI.instance.disabledSideColorModeColor.getValue().getColorColor().getGreen(), ClickGUI.instance.disabledSideColorModeColor.getValue().getColorColor().getBlue(), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha());

        //animation loops numbers
        float loopsNum = loopsAndShit(storedLocalLoops, storedDecayFactors, module, ClickGUI.instance.enabledRectAnimationFactor.getValue());
        float loopsNumSide = loopsAndShit(storedLocalLoops, storedDecayFactors, module, ClickGUI.instance.enabledSideMoveFactor.getValue());
        float loopsNumScale = loopsAndShit(storedScaleLoops, storedScaleDecayFactors, module, ClickGUI.instance.enabledRectScaleFactor.getValue());
        float loopsNumAlpha = loopsAndShit(storedAlphaLoops, storedAlphaDecayFactors, module, ClickGUI.instance.enabledAllGlowAlphaFactor.getValue());
        float loopsNumText = loopsAndShit(storedTextEnableLoops, storedTextEnableDecayFactors, module, ClickGUI.instance.enabledTextSmoothFactor.getValue());
        float loopsNumSideColorMode = loopsAndShit(storedDisableSideColorModeLoops, storedDisableSideColorModeDecayFactors, module, ClickGUI.instance.enabledSideColorModeAnimationFactor.getValue());
        float loopsNumSideIconColor = loopsAndShit(storedSideIconColorLoops, storedSideIconColorDecayFactors, module, ClickGUI.instance.enabledSideIconColorSmoothFactor.getValue());
        //module enable/disable scale rect
        int enabledRectScaleFadeColor = new Color(ClickGUI.instance.enabledColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledColor.getValue().getColorColor().getBlue(), (int)(ClickGUI.instance.enabledRectStartAlpha.getValue() - ((loopsNumScale * ClickGUI.instance.enabledRectStartAlpha.getValue()) / 300.0f))).getRGB();
        if (ClickGUI.instance.enabledRectScaleFade.getValue() && (ClickGUI.instance.enabledRectScaleOnWhat.getValue() == ClickGUI.EnableDisableScaleRect.Enable || ClickGUI.instance.enabledRectScaleOnWhat.getValue() == ClickGUI.EnableDisableScaleRect.Both)) {
            if (ClickGUI.instance.moduleRectRounded.getValue()) {
                RenderUtils2D.drawRoundedRect(x + 1 - (loopsNumScale * ((ClickGUI.instance.enabledRectScaleX.getValue() / 2) / 300.0f)), (y - 1 - (loopsNumScale * ((ClickGUI.instance.enabledRectScaleY.getValue() / 2) / 300.0f))), ClickGUI.instance.moduleRectRoundedRadius.getValue(), x + width - 1 + (loopsNumScale * ((ClickGUI.instance.enabledRectScaleX.getValue() / 2) / 300.0f)), (y + height + (loopsNumScale * ((ClickGUI.instance.enabledRectScaleY.getValue() / 2) / 300.0f))) - ClickGUI.instance.moduleGap.getValue(), false, ClickGUI.instance.moduleRoundedTopRight.getValue(), ClickGUI.instance.moduleRoundedTopLeft.getValue(), ClickGUI.instance.moduleRoundedBottomRight.getValue(), ClickGUI.instance.moduleRoundedBottomLeft.getValue(), enabledRectScaleFadeColor);
            }
            else {
                RenderUtils2D.drawRect(x + 1 - (loopsNumScale * ((ClickGUI.instance.enabledRectScaleX.getValue() / 2) / 300.0f)), (y - 1 - (loopsNumScale * ((ClickGUI.instance.enabledRectScaleY.getValue() / 2) / 300.0f))), x + width - 1 + (loopsNumScale * ((ClickGUI.instance.enabledRectScaleX.getValue() / 2) / 300.0f)), (y + height + (loopsNumScale * ((ClickGUI.instance.enabledRectScaleY.getValue() / 2) / 300.0f))) - ClickGUI.instance.moduleGap.getValue(), enabledRectScaleFadeColor);
            }
        }
        module.moduleEnableFlag = false;
        module.moduleDisableFlag = false;


        float enabledSideXTextOffset = (ClickGUI.instance.enabledSideMove.getValue() ? (module.isEnabled() ? (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)) : (ClickGUI.instance.enabledSideX.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (module.isEnabled() ? ClickGUI.instance.enabledSideX.getValue() : 0));
        float enabledGlowSideRectXOffset = (ClickGUI.instance.enabledGlowMoveSideXOffset.getValue() && ClickGUI.instance.enabledSide.getValue() ? enabledSideXTextOffset : 0);
        float enabledSideXOffset = (ClickGUI.instance.enabledSideMoveX.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)) : ClickGUI.instance.enabledSideX.getValue());

        int disabledSideColorToEnabledRed = (int)(MathUtilFuckYou.linearInterp(disabledSideColor.getRed(), enabledSideColor.getRed(), loopsNumSideColorMode));
        int disabledSideColorToEnabledGreen = (int)(MathUtilFuckYou.linearInterp(disabledSideColor.getGreen(), enabledSideColor.getGreen(), loopsNumSideColorMode));
        int disabledSideColorToEnabledBlue = (int)(MathUtilFuckYou.linearInterp(disabledSideColor.getBlue(), enabledSideColor.getBlue(), loopsNumSideColorMode));

        Color enabledRectColor = new Color(ClickGUI.instance.enabledRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledRectColor.getValue().getColorColor().getBlue(), (int)((ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Alpha || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (loopsNumAlpha * (ClickGUI.instance.enabledRectColor.getValue().getAlpha() / 300.0f)) : ClickGUI.instance.enabledRectColor.getValue().getAlpha()));
        Color enabledRectColorAlphaSave = enabledRectColor;
        float enabledRectStartX = (ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.Right) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? x + width - (loopsNum * ((width - 1) / 300.0f)) : ((ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + 1 + ((width) / 2.0f) - (loopsNum * (((width) / 2.0f) / 300.0f))) : (x + 1));
        float enabledRectStartY = ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (y - 1 + ((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) - (loopsNum * (((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) / 300.0f))) : (y - 1);
        Color disabledRectColor = new Color(ClickGUI.instance.enabledRectColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledRectColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledRectColor.getValue().getColorColor().getBlue(), (int)((ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Alpha || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (ClickGUI.instance.enabledRectColor.getValue().getAlpha() - (loopsNum * (ClickGUI.instance.enabledRectColor.getValue().getAlpha() / 300.0f))) : ClickGUI.instance.enabledRectColor.getValue().getAlpha()));
        Color disabledRectColorAlphaSave = disabledRectColor;
        float disabledRectStartX = (ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.Right) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? x + (loopsNum * ((width - 1) / 300.0f)) : ((ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + (loopsNum * (((width + 1) / 2.0f) / 300.0f))) : (x + 1));
        float disabledRectStartY = ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (y - 1 + (loopsNum * (((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) / 300.0f))) : (y - 1);

        if (ClickGUI.instance.enabledRectBrightnessRoll.getValue()) {
            enabledRectColor = ColorUtil.rolledBrightness(enabledRectColor, ClickGUI.instance.enabledRectBrightRollMax.getValue(), ClickGUI.instance.enabledRectBrightRollMin.getValue(), ClickGUI.instance.enabledRectBrightRollSpeed.getValue(), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.X ? (enabledRectStartX) : (enabledRectStartY), ClickGUI.instance.enabledRectBrightRollLength.getValue(), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.X ? (ClickGUI.instance.enabledRectBrightRollDirectionX.getValue() == ClickGUI.EnabledRectBrightRollDirectionX.Right) : (ClickGUI.instance.enabledRectBrightRollDirectionY.getValue() == ClickGUI.EnabledRectBrightRollDirectionY.Up), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.Y);
            disabledRectColor = ColorUtil.rolledBrightness(disabledRectColor, ClickGUI.instance.enabledRectBrightRollMax.getValue(), ClickGUI.instance.enabledRectBrightRollMin.getValue(), ClickGUI.instance.enabledRectBrightRollSpeed.getValue(), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.X ? (enabledRectStartX) : (enabledRectStartY), ClickGUI.instance.enabledRectBrightRollLength.getValue(), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.X ? (ClickGUI.instance.enabledRectBrightRollDirectionX.getValue() == ClickGUI.EnabledRectBrightRollDirectionX.Right) : (ClickGUI.instance.enabledRectBrightRollDirectionY.getValue() == ClickGUI.EnabledRectBrightRollDirectionY.Up), ClickGUI.instance.enabledRectBrightRollAxis.getValue() == ClickGUI.EnabledRectBrightRollAxis.Y);
        }

        //enabled effects
        if (module.isEnabled()) {
            //rect stuff

            if (ClickGUI.instance.enabledRect.getValue()) {
                //normal rect
                if (ClickGUI.instance.moduleRectRounded.getValue()) {
                    RenderUtils2D.drawRoundedRect(enabledRectStartX, enabledRectStartY, ClickGUI.instance.moduleRectRoundedRadius.getValue(), (ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.Left) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + (loopsNum * ((width - 1) / 300.0f))) : ((ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + width - 1 - (width / 2.0f) + (loopsNum * ((width / 2.0f) / 300.0f))) : (x + width - 1)), ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (y + height - ClickGUI.instance.moduleGap.getValue() - ((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) + (loopsNum * (((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) / 300.0f))) : (y + height - ClickGUI.instance.moduleGap.getValue()), false, ClickGUI.instance.moduleRoundedTopRight.getValue(), ClickGUI.instance.moduleRoundedTopLeft.getValue(), ClickGUI.instance.moduleRoundedBottomRight.getValue(), ClickGUI.instance.moduleRoundedBottomLeft.getValue(), new Color(enabledRectColor.getRed(), enabledRectColor.getGreen(), enabledRectColor.getBlue(), enabledRectColorAlphaSave.getAlpha()).getRGB());
                }
                else {
                    RenderUtils2D.drawRect(enabledRectStartX, enabledRectStartY, (ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.Left) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + (loopsNum * ((width - 1) / 300.0f))) : ((ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + width - 1 - (width / 2.0f) + (loopsNum * ((width / 2.0f) / 300.0f))) : (x + width - 1)), ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (y + height - ClickGUI.instance.moduleGap.getValue() - ((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) + (loopsNum * (((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) / 300.0f))) : (y + height - ClickGUI.instance.moduleGap.getValue()), new Color(enabledRectColor.getRed(), enabledRectColor.getGreen(), enabledRectColor.getBlue(), enabledRectColorAlphaSave.getAlpha()).getRGB());
                }
            }
            //side rect stuff
            if (ClickGUI.instance.enabledSide.getValue()) {
                if (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right) {
                    //color mode
                    if (ClickGUI.instance.enabledSideColorMode.getValue() && !ClickGUI.instance.enabledSideMove.getValue()) {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float colorX = (x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(colorX, (y - 1 + ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), true, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledRed : enabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledGreen : enabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledBlue : enabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float colorX = (x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(colorX, (y - 1 + ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledRed : enabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledGreen : enabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledBlue : enabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else {
                            float secondColorX = (x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(secondColorX, (y - 1 + ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (y + (height * ClickGUI.instance.enabledSideHeight.getValue()))+ ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledRed : enabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledGreen : enabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledBlue : enabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                    }
                    //move mode
                    else {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thisX = (ClickGUI.instance.enabledSideMove.getValue() ? x + width - (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) - enabledSideXOffset : x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(thisX, (ClickGUI.instance.enabledSideMove.getValue() ? ((y - 1 + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))) + ClickGUI.instance.enabledSideY.getValue()) : y - 1 + ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - (ClickGUI.instance.enabledSideMove.getValue() ? enabledSideXOffset : ClickGUI.instance.enabledSideX.getValue()), (ClickGUI.instance.enabledSideMove.getValue() ? ((y + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))) + ClickGUI.instance.enabledSideY.getValue()) : y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), true, enabledSideColor.getRGB());
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thisX = (ClickGUI.instance.enabledSideMove.getValue() ? x + width - (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) - enabledSideXOffset : x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(thisX, (ClickGUI.instance.enabledSideMove.getValue() ? ((y - 1 + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))) + ClickGUI.instance.enabledSideY.getValue()) : y - 1 + ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(),x + width - 1 - (ClickGUI.instance.enabledSideMove.getValue() ? enabledSideXOffset : ClickGUI.instance.enabledSideX.getValue()), (ClickGUI.instance.enabledSideMove.getValue() ? ((y + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))) + ClickGUI.instance.enabledSideY.getValue()) : y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, enabledSideColor.getRGB());
                        }
                        else {
                            float secondThisX = (ClickGUI.instance.enabledSideMove.getValue() ? x + width - (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) - enabledSideXOffset : x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(secondThisX, (ClickGUI.instance.enabledSideMove.getValue() ? ((y - 1 + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y - 1 + ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - (ClickGUI.instance.enabledSideMove.getValue() ? enabledSideXOffset : ClickGUI.instance.enabledSideX.getValue()), (ClickGUI.instance.enabledSideMove.getValue() ? ((y + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y + (height * ClickGUI.instance.enabledSideHeight.getValue()))+ ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), enabledSideColor.getRGB());
                        }
                    }
                }
                else {
                    //color mode
                    if (ClickGUI.instance.enabledSideColorMode.getValue() && !ClickGUI.instance.enabledSideMove.getValue()) {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thirdColorX = (x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(x + 1 + ClickGUI.instance.enabledSideX.getValue(), (y - 1 + ClickGUI.instance.enabledSideY.getValue()), thirdColorX, (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), false, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledRed : enabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledGreen : enabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledBlue : enabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thirdColorX = (x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(x + 1 + ClickGUI.instance.enabledSideX.getValue(), (y - 1 + ClickGUI.instance.enabledSideY.getValue()),ClickGUI.instance.enabledSideRadius.getValue(), thirdColorX, (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledRed : enabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledGreen : enabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledBlue : enabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else {
                            float fourthColorX = (x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(x + 1 + ClickGUI.instance.enabledSideX.getValue(), (y - 1 + ClickGUI.instance.enabledSideY.getValue()), fourthColorX, (y + (height * ClickGUI.instance.enabledSideHeight.getValue()))+ ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledRed : enabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledGreen : enabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? disabledSideColorToEnabledBlue : enabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                    }
                    //move mode
                    else {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thirdThisX = (ClickGUI.instance.enabledSideMove.getValue() ? x + (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) + enabledSideXOffset : x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(x + 1 + (ClickGUI.instance.enabledSideMove.getValue() ? enabledSideXOffset : ClickGUI.instance.enabledSideX.getValue()), (ClickGUI.instance.enabledSideMove.getValue() ? ((y - 1 + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y - 1 + ClickGUI.instance.enabledSideY.getValue()), thirdThisX, (ClickGUI.instance.enabledSideMove.getValue() ? ((y + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), false, enabledSideColor.getRGB());
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thirdThisX = (ClickGUI.instance.enabledSideMove.getValue() ? x + (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) + enabledSideXOffset : x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(x + 1 + (ClickGUI.instance.enabledSideMove.getValue() ? enabledSideXOffset : ClickGUI.instance.enabledSideX.getValue()), (ClickGUI.instance.enabledSideMove.getValue() ? ((y - 1 + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y - 1 + ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(), thirdThisX, (ClickGUI.instance.enabledSideMove.getValue() ? ((y + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, enabledSideColor.getRGB());
                        }
                        else {
                            float fourthThisX = (ClickGUI.instance.enabledSideMove.getValue() ? x + (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) + enabledSideXOffset : x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(x + 1 + (ClickGUI.instance.enabledSideMove.getValue() ? enabledSideXOffset : ClickGUI.instance.enabledSideX.getValue()), (ClickGUI.instance.enabledSideMove.getValue() ? ((y - 1 + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y - 1 + ClickGUI.instance.enabledSideY.getValue()), fourthThisX, (ClickGUI.instance.enabledSideMove.getValue() ? ((y + (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f))))+ ClickGUI.instance.enabledSideY.getValue()) : y + (height * ClickGUI.instance.enabledSideHeight.getValue()))+ ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), enabledSideColor.getRGB());
                        }
                    }
                }
            }
            //glow stuff
            if (ClickGUI.instance.enabledTextGlow.getValue()) {
                int enabledTextGlowColor = new Color(ClickGUI.instance.enabledTextGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledTextGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledTextGlowColor.getValue().getColorColor().getBlue(), (int)(ClickGUI.instance.enabledAllGlowFade.getValue() ? ((loopsNumAlpha * ClickGUI.instance.enabledTextGlowColor.getValue().getAlpha()) / 300.0f) : ClickGUI.instance.enabledTextGlowColor.getValue().getAlpha())).getRGB();
                
                if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Left) {
                    RenderUtils2D.drawRoundedRectFade(ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.moduleNameHardMoveLeftSide.getValue() && ClickGUI.instance.enabledSide.getValue() ? x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() : ((ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.enabledSide.getValue() ? (module.isEnabled() ? (x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())) : (x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue()))) : x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + enabledSideXTextOffset), (y + 1 - ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), ClickGUI.instance.enabledGlowTextRadius.getValue(), true, false, ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.moduleNameHardMoveLeftSide.getValue() && ClickGUI.instance.enabledSide.getValue() ? x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() : ((ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.enabledSide.getValue() ? (module.isEnabled() ? (x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())) : (x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue()))) : x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + enabledSideXTextOffset), (y + height + 1 + ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), enabledTextGlowColor);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center) {
                    RenderUtils2D.drawRoundedRectFade(((x + (net.spartanb312.base.gui.Component.instance.width / 2.0f)) - (FontManager.getWidth(module.name) / 2.0f)) - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f), (y + 1 - ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), ClickGUI.instance.enabledGlowTextRadius.getValue(), true, false,((x + (net.spartanb312.base.gui.Component.instance.width / 2.0f)) + (FontManager.getWidth(module.name) / 2.0f)) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f), (y + height + 1 + ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), enabledTextGlowColor);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right) {
                    RenderUtils2D.drawRoundedRectFade((ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right && ClickGUI.instance.moduleNameHardMoveRightSide.getValue() ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 20 - ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue()) : (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 17 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.enabledAllGlowFade.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())) : (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 17 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue())) - enabledSideXTextOffset), (y + 1 - ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), ClickGUI.instance.enabledGlowTextRadius.getValue(), true, false,(ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right && ClickGUI.instance.moduleNameHardMoveRightSide.getValue() ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) + 2 - ClickGUI.instance.enabledSideSize.getValue() + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue()) : (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) + 2 + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.enabledAllGlowFade.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())) : (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) + 2 + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue())) + FontManager.getWidth(module.name) - enabledSideXTextOffset), (y + height + 1 + ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), enabledTextGlowColor);
                }
            }
            else if (ClickGUI.instance.enabledSideGlow.getValue()) {
                int enabledSideGlowColor = new Color(ClickGUI.instance.enabledSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enabledAllGlowFade.getValue() ? (int)(loopsNumAlpha * (ClickGUI.instance.enabledSideGlowColor.getValue().getAlpha() / 300.0f)) : ClickGUI.instance.enabledSideGlowColor.getValue().getAlpha()).getRGB();
                
                GlStateManager.disableAlpha();
                if (ClickGUI.instance.enabledGlowSideTop.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1, y - 1, x + width - 1, (y + height - ClickGUI.instance.enabledGlowSideTopFactor.getValue()) - ClickGUI.instance.moduleGap.getValue(), enabledSideGlowColor,  enabledSideGlowColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                }
                if (ClickGUI.instance.enabledGlowSideBottom.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1, (y - 1 + ClickGUI.instance.enabledGlowSideBottomFactor.getValue()), x + width - 1, (y + height) - ClickGUI.instance.moduleGap.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), enabledSideGlowColor, enabledSideGlowColor);
                }
                if (ClickGUI.instance.enabledGlowSideRight.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1 + ClickGUI.instance.enabledGlowSideRightFactor.getValue(), y - 1, x + width - 1, (y + height) - ClickGUI.instance.moduleGap.getValue(), enabledSideGlowColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), enabledSideGlowColor);
                }
                if (ClickGUI.instance.enabledGlowSideLeft.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1, y - 1, x + width - 1 - ClickGUI.instance.enabledGlowSideLeftFactor.getValue(), (y + height) - ClickGUI.instance.moduleGap.getValue(), new Color(0, 0, 0, 0).getRGB(), enabledSideGlowColor, enabledSideGlowColor, new Color(0, 0, 0, 0).getRGB());
                }
                GlStateManager.enableAlpha();
            }
            //the other glow
            if (ClickGUI.instance.enabledGlow.getValue()) {
                RenderUtils2D.drawRoundedRectFade(x + 1 + ClickGUI.instance.enabledGlowX.getValue() - (ClickGUI.instance.enabledGlowXScale.getValue() / 2.0f) + (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (-enabledGlowSideRectXOffset) : (enabledGlowSideRectXOffset)), y - 1 + ClickGUI.instance.enabledGlowY.getValue() - (ClickGUI.instance.enabledGlowYScale.getValue() / 2.0f), ClickGUI.instance.enabledGlowRadius.getValue(), true, false, x + width - 1 + ClickGUI.instance.enabledGlowX.getValue() + (ClickGUI.instance.enabledGlowXScale.getValue() / 2.0f) + (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (-enabledGlowSideRectXOffset) : (enabledGlowSideRectXOffset)), (y + height) + ClickGUI.instance.enabledGlowY.getValue() + (ClickGUI.instance.enabledGlowYScale.getValue() / 2.0f), new Color(ClickGUI.instance.enabledGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enabledAllGlowFade.getValue() ? (int)(loopsNumAlpha * (ClickGUI.instance.enabledGlowColor.getValue().getAlpha() / 300.0f)) : ClickGUI.instance.enabledGlowColor.getValue().getAlpha()).getRGB());
            }
        }

        //disable effects
        int enabledSideColorToDisabledRed = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.enabledColor.getValue().getColorColor().getRed(), disabledSideColor.getRed(), loopsNumSideColorMode));
        int enabledSideColorToDisabledGreen = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.enabledColor.getValue().getColorColor().getGreen(), disabledSideColor.getGreen(), loopsNumSideColorMode));
        int enabledSideColorToDisabledBlue = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.enabledColor.getValue().getColorColor().getBlue(), disabledSideColor.getBlue(), loopsNumSideColorMode));
        if (module.isDisabled()) {
             //glow stuff
            if (ClickGUI.instance.enabledTextGlow.getValue() && ClickGUI.instance.enabledAllGlowFade.getValue()) {
                int disabledTextGlowColor = new Color(ClickGUI.instance.enabledTextGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledTextGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledTextGlowColor.getValue().getColorColor().getBlue(), (int)(ClickGUI.instance.enabledTextGlowColor.getValue().getAlpha() - (((loopsNumAlpha * ClickGUI.instance.enabledTextGlowColor.getValue().getAlpha()) / 300.0f)))).getRGB();
                
                if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Left) {
                    RenderUtils2D.drawRoundedRectFade(ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.moduleNameHardMoveLeftSide.getValue() && ClickGUI.instance.enabledSide.getValue() ? x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() : ((ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.enabledSide.getValue() ? (x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f))) : x + 1 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + enabledSideXTextOffset), (y + 1 - ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), ClickGUI.instance.enabledGlowTextRadius.getValue(), true, false, ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.moduleNameHardMoveLeftSide.getValue() && ClickGUI.instance.enabledSide.getValue() ? x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() : ((ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.enabledSide.getValue() ? (x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f))) : x + 1 + FontManager.getWidth(module.name) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) + (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + enabledSideXTextOffset), (y + height + 1 + ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), disabledTextGlowColor);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center) {
                    RenderUtils2D.drawRoundedRectFade(((x + (net.spartanb312.base.gui.Component.instance.width / 2.0f)) - (FontManager.getWidth(module.name) / 2.0f)) - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f), (y + 1 - ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), ClickGUI.instance.enabledGlowTextRadius.getValue(), true, false,((x + (net.spartanb312.base.gui.Component.instance.width / 2.0f)) + (FontManager.getWidth(module.name) / 2.0f)) + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f), (y + height + 1 + ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), disabledTextGlowColor);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right) {
                    RenderUtils2D.drawRoundedRectFade((ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right && ClickGUI.instance.moduleNameHardMoveRightSide.getValue() ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 25 - ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue()) : (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 23 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.enabledAllGlowFade.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())) : (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 23 - (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue())) - enabledSideXTextOffset), (y + 1 - ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), ClickGUI.instance.enabledGlowTextRadius.getValue(), true, false, (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right && ClickGUI.instance.moduleNameHardMoveRightSide.getValue() ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 5 - ClickGUI.instance.enabledSideSize.getValue() + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue()) : (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 3 + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.enabledAllGlowFade.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())) : (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 3 + (ClickGUI.instance.enabledTextGlowX.getValue() / 2.0f) - (isExtended ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue())) + FontManager.getWidth(module.name) - enabledSideXTextOffset), (y + height + 1 + ClickGUI.instance.enabledTextGlowY.getValue() / 2.0f) + CustomFont.instance.textOffset.getValue(), disabledTextGlowColor);
                }
            }
            else if (ClickGUI.instance.enabledSideGlow.getValue() && ClickGUI.instance.enabledAllGlowFade.getValue()) {
                int disabledSideGlowColor = new Color(ClickGUI.instance.enabledSideGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledSideGlowColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledSideGlowColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enabledAllGlowFade.getValue() ? (int)(ClickGUI.instance.enabledSideGlowColor.getValue().getAlpha() - (loopsNumAlpha * (ClickGUI.instance.enabledSideGlowColor.getValue().getAlpha() / 300.0f))) : ClickGUI.instance.enabledSideGlowColor.getValue().getAlpha()).getRGB();
                
                GlStateManager.disableAlpha();
                if (ClickGUI.instance.enabledGlowSideTop.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1, y - 1, x + width - 1, (y + height - ClickGUI.instance.enabledGlowSideTopFactor.getValue()) - ClickGUI.instance.moduleGap.getValue(), disabledSideGlowColor,  disabledSideGlowColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
                }
                if (ClickGUI.instance.enabledGlowSideBottom.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1, (y - 1 + ClickGUI.instance.enabledGlowSideBottomFactor.getValue()), x + width - 1, (y + height) - ClickGUI.instance.moduleGap.getValue(), new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), disabledSideGlowColor, disabledSideGlowColor);
                }
                if (ClickGUI.instance.enabledGlowSideRight.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1 + ClickGUI.instance.enabledGlowSideRightFactor.getValue(), y - 1, x + width - 1, (y + height) - ClickGUI.instance.moduleGap.getValue(), disabledSideGlowColor, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB(), disabledSideGlowColor);
                }
                if (ClickGUI.instance.enabledGlowSideLeft.getValue()) {
                    RenderUtils2D.drawCustomRect(x + 1, y - 1, x + width - 1 - ClickGUI.instance.enabledGlowSideLeftFactor.getValue(), (y + height) - ClickGUI.instance.moduleGap.getValue(), new Color(0, 0, 0, 0).getRGB(), disabledSideGlowColor, disabledSideGlowColor, new Color(0, 0, 0, 0).getRGB());
                }
                GlStateManager.enableAlpha();
            }

            if (ClickGUI.instance.enabledRect.getValue() && ClickGUI.instance.enabledRectAnimation.getValue() != ClickGUI.EnabledRectAnimation.None) {
                //normal rect
                if (ClickGUI.instance.moduleRectRounded.getValue()) {
                    RenderUtils2D.drawRoundedRect(disabledRectStartX, disabledRectStartY, ClickGUI.instance.moduleRectRoundedRadius.getValue(), (ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.Left) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + width - (loopsNum * ((width - 1) / 300.0f))) : ((ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + width - 1 - (loopsNum * ((width / 2.0f) / 300.0f))) : (x + width - 1)), ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (y + height - ClickGUI.instance.moduleGap.getValue() - (loopsNum * (((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) / 300.0f))) : (y + height - ClickGUI.instance.moduleGap.getValue()), false, ClickGUI.instance.moduleRoundedTopRight.getValue(), ClickGUI.instance.moduleRoundedTopLeft.getValue(), ClickGUI.instance.moduleRoundedBottomRight.getValue(), ClickGUI.instance.moduleRoundedBottomLeft.getValue(), new Color(disabledRectColor.getRed(), disabledRectColor.getGreen(), disabledRectColor.getBlue(), disabledRectColorAlphaSave.getAlpha()).getRGB());
                }
                else {
                    RenderUtils2D.drawRect(disabledRectStartX, disabledRectStartY, (ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.Left) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + width - (loopsNum * ((width - 1) / 300.0f))) : ((ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All) && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (x + width - 1 - (loopsNum * ((width / 2.0f) / 300.0f))) : (x + width - 1)), ClickGUI.instance.enabledRectMove.getValue() == ClickGUI.EnabledRectMoveMode.All && (ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Scale || ClickGUI.instance.enabledRectAnimation.getValue() == ClickGUI.EnabledRectAnimation.Both) ? (y + height - ClickGUI.instance.moduleGap.getValue() - (loopsNum * (((height - ClickGUI.instance.moduleGap.getValue()) / 2.0f) / 300.0f))) : (y + height - ClickGUI.instance.moduleGap.getValue()), new Color(disabledRectColor.getRed(), disabledRectColor.getGreen(), disabledRectColor.getBlue(), disabledRectColorAlphaSave.getAlpha()).getRGB());
                }
            }
            if (ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideMove.getValue() && loopsNumSide < 299.0f) {
                //side rect
                float disabledSideXOffset = (ClickGUI.instance.enabledSideMoveX.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)) : ClickGUI.instance.enabledSideX.getValue());
                int sideEnabledColor = ClickGUI.instance.enabledColor.getValue().getColor();
                if (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right) {
                    //move mode
                    if (!(ClickGUI.instance.enabledSideColorMode.getValue() && !ClickGUI.instance.enabledSideMove.getValue())) {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            GL11.glTranslatef(disabledSideXOffset, 0.0f, 0.0f);
                            float fifthThisX = (x + width - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(fifthThisX, ((y - 1 + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (((y + (height * ClickGUI.instance.enabledSideHeight.getValue())) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()) - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), true, sideEnabledColor);
                            GL11.glTranslatef(-disabledSideXOffset, 0.0f, 0.0f);
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            GL11.glTranslatef(disabledSideXOffset, 0.0f, 0.0f);
                            float fifthThisX = (x + width - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(fifthThisX, ((y - 1 + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (((y + (height * ClickGUI.instance.enabledSideHeight.getValue())) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()) - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, sideEnabledColor);
                            GL11.glTranslatef(-disabledSideXOffset, 0.0f, 0.0f);
                        }
                        else {
                            GL11.glTranslatef(disabledSideXOffset, 0.0f, 0.0f);
                            float sixthThisX = (x + width - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(sixthThisX, ((y - 1 + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (((y + (height * ClickGUI.instance.enabledSideHeight.getValue())) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()) - ClickGUI.instance.moduleGap.getValue(), sideEnabledColor);
                            GL11.glTranslatef(-disabledSideXOffset, 0.0f, 0.0f);
                        }
                    }
                }
                else {
                    //move mode
                    if (!(ClickGUI.instance.enabledSideColorMode.getValue() && !ClickGUI.instance.enabledSideMove.getValue())) {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            GL11.glTranslatef(-disabledSideXOffset, 0.0f, 0.0f);
                            float seventhThisX = (x + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(x + 1 + ClickGUI.instance.enabledSideX.getValue(), ((y - 1 + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()), seventhThisX, (((y + (height * ClickGUI.instance.enabledSideHeight.getValue())) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()) - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), false, sideEnabledColor);
                            GL11.glTranslatef(disabledSideXOffset, 0.0f, 0.0f);
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            GL11.glTranslatef(-disabledSideXOffset, 0.0f, 0.0f);
                            float seventhThisX = (x + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(x + 1 + ClickGUI.instance.enabledSideX.getValue(), ((y - 1 + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(), seventhThisX, (((y + (height * ClickGUI.instance.enabledSideHeight.getValue())) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()) - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, sideEnabledColor);
                            GL11.glTranslatef(disabledSideXOffset, 0.0f, 0.0f);
                        }
                        else {
                            GL11.glTranslatef(-disabledSideXOffset, 0.0f, 0.0f);
                            float eigthThisX = (x + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * ((ClickGUI.instance.enabledSideSize.getValue()) / 300.0f)) + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(x + 1 + ClickGUI.instance.enabledSideX.getValue(), ((y - 1 + (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()),  eigthThisX, (((y + (height * ClickGUI.instance.enabledSideHeight.getValue())) - (loopsNumSide * (((height * ClickGUI.instance.enabledSideHeight.getValue()) / 2.0f) / 300.0f)))+ ClickGUI.instance.enabledSideY.getValue()) - ClickGUI.instance.moduleGap.getValue(), sideEnabledColor);
                            GL11.glTranslatef(disabledSideXOffset, 0.0f, 0.0f);
                        }
                    }
                }
            }
            else if (ClickGUI.instance.enabledSide.getValue()) {
                if (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right) {
                    //color mode
                    if (ClickGUI.instance.enabledSideColorMode.getValue() && !ClickGUI.instance.enabledSideMove.getValue()) {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float colorX = (x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(colorX, (y - 1 + ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), true, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledRed : disabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledGreen : disabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledBlue : disabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float colorX = (x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(colorX, (y - 1 + ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledRed : disabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledGreen : disabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledBlue : disabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else {
                            float secondColorX = (x + width - ClickGUI.instance.enabledSideSize.getValue() - ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(secondColorX, (y - 1 + ClickGUI.instance.enabledSideY.getValue()), x + width - 1 - ClickGUI.instance.enabledSideX.getValue(), (y + (height * ClickGUI.instance.enabledSideHeight.getValue()))+ ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledRed : disabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledGreen : disabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledBlue : disabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                    }
                }
                else {
                    //color mode
                    if (ClickGUI.instance.enabledSideColorMode.getValue() && !ClickGUI.instance.enabledSideMove.getValue()) {
                        if (ClickGUI.instance.enabledSideRound.getValue() && !ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thirdColorX = (x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawCustomRoundedRectModuleEnableMode(x + 1 + ClickGUI.instance.enabledSideX.getValue(), (y - 1 + ClickGUI.instance.enabledSideY.getValue()), thirdColorX, (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), ClickGUI.instance.enabledSideRadius.getValue(), false, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledRed : disabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledGreen : disabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledBlue : disabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else if (ClickGUI.instance.enabledSideRound.getValue() && ClickGUI.instance.enabledSideRoundedFull.getValue()) {
                            float thirdColorX = (x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRoundedRect(x + 1 + ClickGUI.instance.enabledSideX.getValue(), (y - 1 + ClickGUI.instance.enabledSideY.getValue()), ClickGUI.instance.enabledSideRadius.getValue(), thirdColorX, (y + (height * ClickGUI.instance.enabledSideHeight.getValue())) + ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), false, true, true, true, true, new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledRed : disabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledGreen : disabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledBlue : disabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                        else {
                            float fourthColorX = (x + ClickGUI.instance.enabledSideSize.getValue() + ClickGUI.instance.enabledSideX.getValue());
                            RenderUtils2D.drawRect(x + 1 + ClickGUI.instance.enabledSideX.getValue(), (y - 1 + ClickGUI.instance.enabledSideY.getValue()), fourthColorX, (y + (height * ClickGUI.instance.enabledSideHeight.getValue()))+ ClickGUI.instance.enabledSideY.getValue() - ClickGUI.instance.moduleGap.getValue(), new Color((ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledRed : disabledSideColor.getRed()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledGreen : disabledSideColor.getGreen()), (ClickGUI.instance.enabledSideColorModeAnimation.getValue() ? enabledSideColorToDisabledBlue : disabledSideColor.getBlue()), ClickGUI.instance.disabledSideColorModeColor.getValue().getAlpha()).getRGB());
                        }
                    }
                }
            }
            //the other glow
            if (ClickGUI.instance.enabledGlow.getValue()) {
                RenderUtils2D.drawRoundedRectFade(x + 1 + ClickGUI.instance.enabledGlowX.getValue() - (ClickGUI.instance.enabledGlowXScale.getValue() / 2.0f) + (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (-enabledGlowSideRectXOffset) : (enabledGlowSideRectXOffset)), y - 1 + ClickGUI.instance.enabledGlowY.getValue() - (ClickGUI.instance.enabledGlowYScale.getValue() / 2.0f), ClickGUI.instance.enabledGlowRadius.getValue(), true, false, x + width - 1 + ClickGUI.instance.enabledGlowX.getValue() + (ClickGUI.instance.enabledGlowXScale.getValue() / 2.0f) + (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (-enabledGlowSideRectXOffset) : (enabledGlowSideRectXOffset)), (y + height) + ClickGUI.instance.enabledGlowY.getValue() + (ClickGUI.instance.enabledGlowYScale.getValue() / 2.0f), new Color(ClickGUI.instance.enabledGlowColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledGlowColor.getValue().getGreen(), ClickGUI.instance.enabledGlowColor.getValue().getBlue(), ClickGUI.instance.enabledAllGlowFade.getValue() ? (int)(ClickGUI.instance.enabledGlowColor.getValue().getAlpha() - (loopsNumAlpha * (ClickGUI.instance.enabledGlowColor.getValue().getAlpha() / 300.0f))) : ClickGUI.instance.enabledGlowColor.getValue().getAlpha()).getRGB());
            }
        }

        renderHoverRect(module.name, mouseX, mouseY, 0.0f, 0.0f, true);

        //text stuff
        moduleNameX = (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.moduleNameHardMoveLeftSide.getValue() ? x + 5 + ClickGUI.instance.enabledSideSize.getValue() : (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left && ClickGUI.instance.enabledSide.getValue() ? (module.isEnabled() ? (x + 3 + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue()))) : (x + 3 + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue()))))) : x + 3)) + 3;
        moduleNameXRight = (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right && ClickGUI.instance.moduleNameHardMoveRightSide.getValue() ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 5 - ClickGUI.instance.enabledSideSize.getValue()) : (ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right && ClickGUI.instance.enabledSide.getValue() ? (module.isEnabled() ? (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 3 - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : ((ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue())))) : (x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 3 - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) : ClickGUI.instance.enabledSideSize.getValue()))))) : x + net.spartanb312.base.gui.Component.instance.width - FontManager.getWidth(module.name) - 3)) - 3;
        moduleNameXCentered = ((x + (net.spartanb312.base.gui.Component.instance.width / 2.0f)) - (FontManager.getWidth(module.name) / 2.0f));
        moduleCustomFontNameY = ((int) (y + height / 2 - font.getHeight() / 2f) + 2) + CustomFont.instance.textOffset.getValue();
        currentTextWidth = FontManager.getWidth(module.name);
        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) {
                storedTextScaleHovered.putIfAbsent(module.name, CustomFont.instance.textScale.getValue());
                if (isHovered(mouseX, mouseY)) {
                    float hoverTextScaleLoops = storedTextScaleHovered.get(module.name);
                    hoverTextScaleLoops += 0.1f * ClickGUI.instance.moduleHoverTextScaleFactorIn.getValue();
                    storedTextScaleHovered.put(module.name, hoverTextScaleLoops);
                }
                if (storedTextScaleHovered.containsKey(module.name)) {
                    float hoverTextScaleLoops = storedTextScaleHovered.get(module.name);
                    if (hoverTextScaleLoops <= CustomFont.instance.textScale.getValue()) {
                        hoverTextScaleLoops = CustomFont.instance.textScale.getValue();
                    }
                    if (hoverTextScaleLoops >= ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() / 2.0f))) {
                        hoverTextScaleLoops = ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() / 2.0f));
                    }
                    storedTextScaleHovered.put(module.name, hoverTextScaleLoops);
                }
            }


            if (CustomFont.instance.moduleTextPos.getValue() != CustomFont.TextPos.Left) {
                if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center) {
                    GL11.glTranslatef(((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth) / 2.0f, 0.0f, 0.0f);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right) {
                    GL11.glTranslatef(((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth), 0.0f, 0.0f);
                }
            }

            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue() && storedTextScaleHovered.containsKey(module.name)) {
                GL11.glTranslatef((CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))) * (1.0f - storedTextScaleHovered.get(module.name)), (((y + height / 2.0f - font.getHeight() / 2.0f)) + CustomFont.instance.textOffset.getValue()) * (1.0f - storedTextScaleHovered.get(module.name)),0.0f);
                GL11.glScalef(storedTextScaleHovered.get(module.name), storedTextScaleHovered.get(module.name), storedTextScaleHovered.get(module.name));
            }
            else {
                GL11.glTranslatef((CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))) * (1.0f - CustomFont.instance.textScale.getValue()), (((y + height / 2.0f - font.getHeight() / 2.0f)) + CustomFont.instance.textOffset.getValue()) * (1.0f - CustomFont.instance.textScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.textScale.getValue(), CustomFont.instance.textScale.getValue(), CustomFont.instance.textScale.getValue());
            }


            GL11.glEnable(GL_TEXTURE_2D);
            if (ClickGUI.instance.enabledTextDifColor.getValue() && module.isEnabled()) {
                mc.fontRenderer.drawString(module.name, CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)), ((int) (y + height / 2 - font.getHeight() / 2f)) + CustomFont.instance.textOffset.getValue(), enabledTextColor(loopsNumText, loopsNumSide), CustomFont.instance.textShadow.getValue());
            }
            else {
                mc.fontRenderer.drawString(module.name, CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)), ((int) (y + height / 2 - font.getHeight() / 2f)) + CustomFont.instance.textOffset.getValue(), disabledTextColor(loopsNumText), CustomFont.instance.textShadow.getValue());
            }
            GL11.glDisable(GL_TEXTURE_2D);

            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) {
                GL11.glScalef(1.0f / storedTextScaleHovered.get(module.name), 1.0f / storedTextScaleHovered.get(module.name), 1.0f / storedTextScaleHovered.get(module.name));
                GL11.glTranslatef((CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))) * (1.0f - storedTextScaleHovered.get(module.name)) * -1.0f, (((y + height / 2.0f - font.getHeight() / 2.0f)) + CustomFont.instance.textOffset.getValue()) * (1.0f - storedTextScaleHovered.get(module.name)) * -1.0f,1.0f);
            }
            else {
                GL11.glScalef(1.0f / (CustomFont.instance.textScale.getValue()), 1.0f / (CustomFont.instance.textScale.getValue()), 1.0f / (CustomFont.instance.textScale.getValue()));
                GL11.glTranslatef((CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue()) + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))) * (1.0f - CustomFont.instance.textScale.getValue()) * -1.0f, (((y + height / 2.0f - font.getHeight() / 2.0f)) + CustomFont.instance.textOffset.getValue()) * (1.0f - CustomFont.instance.textScale.getValue()) * -1.0f,1.0f);
            }


            if (CustomFont.instance.moduleTextPos.getValue() != CustomFont.TextPos.Left) {
                if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center) {
                    GL11.glTranslatef((((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth) / 2.0f) * -1.0f, 0.0f, 0.0f);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right) {
                    GL11.glTranslatef(((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth) * -1.0f, 0.0f, 0.0f);
                }
            }

            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue() && !isHovered(mouseX, mouseY)) {
                float hoverTextScaleLoops = storedTextScaleHovered.get(module.name);
                hoverTextScaleLoops -= 0.1f * ClickGUI.instance.moduleHoverTextScaleFactorOut.getValue();
                storedTextScaleHovered.put(module.name, hoverTextScaleLoops);
            }
        }
        else {
            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) {
                storedTextScaleHovered.putIfAbsent(module.name, CustomFont.instance.textScale.getValue());
                if (isHovered(mouseX, mouseY)) {
                    float hoverTextScaleLoops = storedTextScaleHovered.get(module.name);
                    hoverTextScaleLoops += 0.1f * ClickGUI.instance.moduleHoverTextScaleFactorIn.getValue();
                    storedTextScaleHovered.put(module.name, hoverTextScaleLoops);
                }
                if (storedTextScaleHovered.containsKey(module.name)) {
                    float hoverTextScaleLoops = storedTextScaleHovered.get(module.name);
                    if (hoverTextScaleLoops <= CustomFont.instance.textScale.getValue()) {
                        hoverTextScaleLoops = CustomFont.instance.textScale.getValue();
                    }
                    if (hoverTextScaleLoops >= ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() / 2.0f))) {
                        hoverTextScaleLoops = ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() + (1.0f - (ClickGUI.instance.moduleHoverTextScaleNewScale.getValue() / 2.0f));
                    }
                    storedTextScaleHovered.put(module.name, hoverTextScaleLoops);
                }
            }


            float currentTextWidth = FontManager.getWidth(module.name);
            if (CustomFont.instance.moduleTextPos.getValue() != CustomFont.TextPos.Left) {
                if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center) {
                    GL11.glTranslatef(((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth) / 2.0f, 0.0f, 0.0f);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right) {
                    GL11.glTranslatef(((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth), 0.0f, 0.0f);
                }
            }


            GL11.glTranslatef((CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)))) * (1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())), moduleCustomFontNameY * (1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())), 0.0f);
            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue() && storedTextScaleHovered.containsKey(module.name)) {
                GL11.glScalef(storedTextScaleHovered.get(module.name), storedTextScaleHovered.get(module.name), storedTextScaleHovered.get(module.name));
            }
            else {
                GL11.glScalef(CustomFont.instance.textScale.getValue(), CustomFont.instance.textScale.getValue(), CustomFont.instance.textScale.getValue());
            }


            if (ClickGUI.instance.enabledTextDifColor.getValue() && module.isEnabled()) {
                if (CustomFont.instance.textShadow.getValue()) {
                    fontManager.drawShadow(module.name, CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))), moduleCustomFontNameY, enabledTextColor(loopsNumText, loopsNumSide));
                }
                else {
                    fontManager.draw(module.name, CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))), moduleCustomFontNameY, enabledTextColor(loopsNumText, loopsNumSide));
                }
            }
            else {
                if (CustomFont.instance.textShadow.getValue()) {
                    fontManager.drawShadow(module.name, CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))), moduleCustomFontNameY, disabledTextColor(loopsNumText));
                }
                else {
                    fontManager.draw(module.name, CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset))), moduleCustomFontNameY, disabledTextColor(loopsNumText));
                }
            }

            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) {
                GL11.glScalef(1.0f / storedTextScaleHovered.get(module.name), 1.0f / storedTextScaleHovered.get(module.name), 1.0f / storedTextScaleHovered.get(module.name));
            }
            else {
                GL11.glScalef(1.0f / (CustomFont.instance.textScale.getValue()), 1.0f / (CustomFont.instance.textScale.getValue()), 1.0f / (CustomFont.instance.textScale.getValue()));
            }

            GL11.glTranslatef((CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? moduleNameXCentered : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? moduleNameXRight - (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right ? extendedMove() : (bigIcons ? 4 : 1)) - CustomFont.instance.moduleTextOffsetX.getValue() - (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)) : moduleNameX + (isExtended && ClickGUI.instance.sideIconMode.getValue() != ClickGUI.SideIconMode.None && ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Left ? extendedMove() : (bigIcons ? -4 : 1)) + CustomFont.instance.moduleTextOffsetX.getValue() + (ClickGUI.instance.moduleTextNoMove.getValue() ? 0 : (enabledSideXTextOffset)))) * (1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * -1.0f, moduleCustomFontNameY * (1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * -1.0f, 0.0f);

            if (CustomFont.instance.moduleTextPos.getValue() != CustomFont.TextPos.Left) {
                if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center) {
                    GL11.glTranslatef((((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth) / 2.0f) * -1.0f, 0.0f, 0.0f);
                }
                else if (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right) {
                    GL11.glTranslatef(((1.0f - ((ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue()) ? storedTextScaleHovered.get(module.name) : CustomFont.instance.textScale.getValue())) * currentTextWidth) * -1.0f, 0.0f, 0.0f);
                }
            }

            if (ClickGUI.instance.moduleHoverTextScale.getValue() && ClickGUI.instance.moduleHoverStuff.getValue() && !isHovered(mouseX, mouseY)) {
                float hoverTextScaleLoops = storedTextScaleHovered.get(module.name);
                hoverTextScaleLoops -= 0.1f * ClickGUI.instance.moduleHoverTextScaleFactorOut.getValue();
                storedTextScaleHovered.put(module.name, hoverTextScaleLoops);
            }
        }

        //draw module mini icon
        if (ClickGUI.instance.moduleImageDescrip.getValue()) {
            Color miniIconColor = new Color(ClickGUI.instance.moduleImageDescripColor.getValue().getColorColor().getRed(), ClickGUI.instance.moduleImageDescripColor.getValue().getColorColor().getGreen(), ClickGUI.instance.moduleImageDescripColor.getValue().getColorColor().getBlue(), ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha());
            Color enabledMiniIconColor = new Color(ClickGUI.instance.enabledMiniIconColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledMiniIconColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledMiniIconColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enabledMiniIconColor.getValue().getAlpha());
            FontManager.drawModuleMiniIcon(ModuleManager.getModuleMiniIcons(module.getClass()), (ClickGUI.instance.moduleMiniIconSide.getValue() == ClickGUI.ModuleMiniIconSide.HardLeft ? (x + 4) : ClickGUI.instance.moduleMiniIconSide.getValue() == ClickGUI.ModuleMiniIconSide.HardRight ? (x + width - 11) : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? (int)(ClickGUI.instance.moduleMiniIconSide.getValue() == ClickGUI.ModuleMiniIconSide.Right ? (moduleNameXCentered + FontManager.getWidth(module.name) + 3) : (moduleNameXCentered - 9)) : CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? (int)(moduleNameXRight - 11) : ((int)(moduleNameX + FontManager.getWidth(module.name) + 3)))) + ClickGUI.instance.moduleMiniIconXOffset.getValue(), (int)moduleCustomFontNameY - 1, ClickGUI.instance.enabledMiniIconDifColor.getValue() && module.isEnabled() ? (ClickGUI.instance.enabledMiniIconColorSmooth.getValue() ? ((new Color((int)(miniIconColor.getRed() + (loopsNumAlpha * ((enabledMiniIconColor.getRed() - miniIconColor.getRed()) / 300.0f))), (int)(miniIconColor.getGreen() + (loopsNumAlpha * ((enabledMiniIconColor.getGreen() - miniIconColor.getGreen()) / 300.0f))), (int)(miniIconColor.getBlue() + (loopsNumAlpha * ((enabledMiniIconColor.getBlue() - miniIconColor.getBlue()) / 300.0f))), (ClickGUI.instance.moduleImageDescripDisableOnModuleEnable.getValue() && module.isEnabled()) ? (int)(ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha() + (loopsNumAlpha * ((5 - ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha()) / 300.0f))) : (int)(ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha() + (loopsNumAlpha * ((ClickGUI.instance.enabledMiniIconColor.getValue().getAlpha() - ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha()) / 300.0f)))))) : ((new Color(enabledMiniIconColor.getRed(), enabledMiniIconColor.getGreen(), enabledMiniIconColor.getBlue(), (ClickGUI.instance.moduleImageDescripDisableOnModuleEnable.getValue() && module.isEnabled()) ? 5 : ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha())))) : (ClickGUI.instance.enabledMiniIconDifColor.getValue() && module.isDisabled() && ClickGUI.instance.enabledMiniIconColorSmooth.getValue() ? ((new Color((int)(enabledMiniIconColor.getRed() - (loopsNumAlpha * ((enabledMiniIconColor.getRed() - miniIconColor.getRed()) / 300.0f))), (int)(enabledMiniIconColor.getGreen() - (loopsNumAlpha * ((enabledMiniIconColor.getGreen() - miniIconColor.getGreen()) / 300.0f))), (int)(enabledMiniIconColor.getBlue() - (loopsNumAlpha * ((enabledMiniIconColor.getBlue() - miniIconColor.getBlue()) / 300.0f))), (ClickGUI.instance.moduleImageDescripDisableOnModuleEnable.getValue() && module.isDisabled()) ? (int)(5 + (loopsNumAlpha * ((ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha() - 5) / 300.0f))) : (int)(ClickGUI.instance.enabledMiniIconColor.getValue().getAlpha() - (loopsNumAlpha * ((ClickGUI.instance.enabledMiniIconColor.getValue().getAlpha() - ClickGUI.instance.moduleImageDescripColor.getValue().getAlpha()) / 300.0f)))))) : miniIconColor));
            GlStateManager.disableAlpha();
            if (ClickGUI.instance.moduleImageDescripGlow.getValue() && !ModuleManager.getModuleMiniIcons(module.getClass()).equals("")) {
                RenderUtils2D.drawCustomCircle((ClickGUI.instance.moduleMiniIconSide.getValue() == ClickGUI.ModuleMiniIconSide.HardLeft ? (x + 7) : ClickGUI.instance.moduleMiniIconSide.getValue() == ClickGUI.ModuleMiniIconSide.HardRight ? (x + width - 8) : (CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Center ? (int)(ClickGUI.instance.moduleMiniIconSide.getValue() == ClickGUI.ModuleMiniIconSide.Right ? (moduleNameXCentered + FontManager.getWidth(module.name) + 6) : (moduleNameXCentered - 6)) : CustomFont.instance.moduleTextPos.getValue() == CustomFont.TextPos.Right ? (moduleNameXRight - 8) : (moduleNameX + FontManager.getWidth(module.name) + 6))) + ClickGUI.instance.moduleMiniIconXOffset.getValue(), moduleCustomFontNameY - 1, ClickGUI.instance.moduleImageDescripGlowRadius.getValue(), ClickGUI.instance.enabledMiniIconDifColor.getValue() && module.isEnabled() ? (ClickGUI.instance.enabledMiniIconColorSmooth.getValue() ? ((new Color((int)(miniIconColor.getRed() + (loopsNumAlpha * ((enabledMiniIconColor.getRed() - miniIconColor.getRed()) / 300.0f))), (int)(miniIconColor.getGreen() + (loopsNumAlpha * ((enabledMiniIconColor.getGreen() - miniIconColor.getGreen()) / 300.0f))), (int)(miniIconColor.getBlue() + (loopsNumAlpha * ((enabledMiniIconColor.getBlue() - miniIconColor.getBlue()) / 300.0f))), (ClickGUI.instance.moduleImageDescripDisableOnModuleEnable.getValue() && module.isEnabled()) ? (int)(ClickGUI.instance.moduleImageDescripGlowAlpha.getValue() + (loopsNumAlpha * ((5 - ClickGUI.instance.moduleImageDescripGlowAlpha.getValue()) / 300.0f))) : (int)(ClickGUI.instance.moduleImageDescripGlowAlpha.getValue() + (loopsNumAlpha * ((ClickGUI.instance.enabledMiniIconGlowAlpha.getValue() - ClickGUI.instance.moduleImageDescripGlowAlpha.getValue()) / 300.0f)))).getRGB())) : ((new Color(enabledMiniIconColor.getRed(), enabledMiniIconColor.getGreen(), enabledMiniIconColor.getBlue(), (ClickGUI.instance.moduleImageDescripDisableOnModuleEnable.getValue() && module.isEnabled()) ? 5 : ClickGUI.instance.moduleImageDescripGlowAlpha.getValue()).getRGB()))) : (ClickGUI.instance.enabledMiniIconDifColor.getValue() && module.isDisabled() && ClickGUI.instance.enabledMiniIconColorSmooth.getValue() ? ((new Color((int)(enabledMiniIconColor.getRed() - (loopsNumAlpha * ((enabledMiniIconColor.getRed() - miniIconColor.getRed()) / 300.0f))), (int)(enabledMiniIconColor.getGreen() - (loopsNumAlpha * ((enabledMiniIconColor.getGreen() - miniIconColor.getGreen()) / 300.0f))), (int)(enabledMiniIconColor.getBlue() - (loopsNumAlpha * ((enabledMiniIconColor.getBlue() - miniIconColor.getBlue()) / 300.0f))), (ClickGUI.instance.moduleImageDescripDisableOnModuleEnable.getValue() && module.isDisabled()) ? (int)(5 + (loopsNumAlpha * ((ClickGUI.instance.moduleImageDescripGlowAlpha.getValue() - 5) / 300.0f))) : (int)(ClickGUI.instance.enabledMiniIconGlowAlpha.getValue() - (loopsNumAlpha * ((ClickGUI.instance.enabledMiniIconGlowAlpha.getValue() - ClickGUI.instance.moduleImageDescripGlowAlpha.getValue()) / 300.0f)))).getRGB())) : new Color(miniIconColor.getRed(), miniIconColor.getGreen(), miniIconColor.getBlue(), ClickGUI.instance.moduleImageDescripGlowAlpha.getValue()).getRGB()), new Color(0, 0, 0, 0).getRGB());
            }
            GlStateManager.enableAlpha();
        }

        //draw side icon
        Color sideIconColor1 = new Color(ClickGUI.instance.sideIconColor.getValue().getColorColor().getRed(), ClickGUI.instance.sideIconColor.getValue().getColorColor().getGreen(), ClickGUI.instance.sideIconColor.getValue().getColorColor().getBlue(), ClickGUI.instance.sideIconColor.getValue().getAlpha());
        Color enabledSideIconColor = new Color(ClickGUI.instance.enabledSideIconColor.getValue().getColorColor().getRed(), ClickGUI.instance.enabledSideIconColor.getValue().getColorColor().getGreen(), ClickGUI.instance.enabledSideIconColor.getValue().getColorColor().getBlue(), ClickGUI.instance.enabledSideIconColor.getValue().getAlpha());

        int disabledSideIconColorToEnabledRed = (int)(MathUtilFuckYou.linearInterp(sideIconColor1.getRed(), enabledSideIconColor.getRed(), loopsNumSideIconColor));
        int disabledSideIconColorToEnabledGreen = (int)(MathUtilFuckYou.linearInterp(sideIconColor1.getGreen(), enabledSideIconColor.getGreen(), loopsNumSideIconColor));
        int disabledSideIconColorToEnabledBlue = (int)(MathUtilFuckYou.linearInterp(sideIconColor1.getBlue(), enabledSideIconColor.getBlue(), loopsNumSideIconColor));
        int disabledSideIconColorToEnabledAlpha = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.sideIconColor.getValue().getAlpha(), ClickGUI.instance.enabledSideIconColor.getValue().getAlpha(), loopsNumSideIconColor));

        int enabledSideIconColorToDisabledRed = (int)(MathUtilFuckYou.linearInterp(enabledSideIconColor.getRed(), sideIconColor1.getRed(), loopsNumSideIconColor));
        int enabledSideIconColorToDisabledGreen = (int)(MathUtilFuckYou.linearInterp(enabledSideIconColor.getGreen(), sideIconColor1.getGreen(), loopsNumSideIconColor));
        int enabledSideIconColorToDisabledBlue = (int)(MathUtilFuckYou.linearInterp(enabledSideIconColor.getBlue(), sideIconColor1.getBlue(), loopsNumSideIconColor));
        int enabledSideIconColorToDisabledAlpha = (int)(MathUtilFuckYou.linearInterp(ClickGUI.instance.enabledSideIconColor.getValue().getAlpha(), ClickGUI.instance.sideIconColor.getValue().getAlpha(), loopsNumSideIconColor));

        Color sideIconColor;
        if (ClickGUI.instance.enabledSideIconDifColor.getValue()) {
            sideIconColor = module.isEnabled() ? (new Color((ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? disabledSideIconColorToEnabledRed : enabledSideIconColor.getRed()), (ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? disabledSideIconColorToEnabledGreen : enabledSideIconColor.getGreen()), (ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? disabledSideIconColorToEnabledBlue : enabledSideIconColor.getBlue()), (ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? disabledSideIconColorToEnabledAlpha : ClickGUI.instance.enabledSideIconColor.getValue().getAlpha()))) : (new Color((ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? enabledSideIconColorToDisabledRed : sideIconColor1.getRed()), (ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? enabledSideIconColorToDisabledGreen : sideIconColor1.getGreen()), (ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? enabledSideIconColorToDisabledBlue : sideIconColor1.getBlue()), (ClickGUI.instance.enabledSideIconColorSmooth.getValue() ? enabledSideIconColorToDisabledAlpha : ClickGUI.instance.sideIconColor.getValue().getAlpha())));
        }
        else {
            sideIconColor = sideIconColor1;
        }

        if (!(ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.sideIconNoSideRectDraw.getValue() && module.isEnabled())) {
            if (ClickGUI.instance.sideIconSide.getValue() == ClickGUI.SideIconSide.Right) {
                if (isExtended) {
                    if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Plus || ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + width - 2 - FontManager.getIconWidth() + SideIconXOffset()) : ((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (module.isEnabled() ? (int)(x + width - 2 - FontManager.getIconWidth() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + width - 2 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (int)(x + width - 2 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : ClickGUI.instance.enabledSideX.getValue())) : x + width - 2 - FontManager.getIconWidth())) + SideIconXOffset());
                        theX -= module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()),(y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIconExtended((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f,(y + 5 + CustomFont.instance.textOffset.getValue())  * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                    else {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + width - 6 - FontManager.getIconWidth() + SideIconXOffset()) : ((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (module.isEnabled() ? (int)(x + width - 6 - FontManager.getIconWidth() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + width - 6 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (int)(x + width - 6 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : ClickGUI.instance.enabledSideX.getValue())) : x + width - 6 - FontManager.getIconWidth())) + SideIconXOffset());
                        theX -= module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) , 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIconExtended((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                }
                else {
                    if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + width - 6 - FontManager.getIconWidth() + SideIconXOffset()) : (((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (module.isEnabled() ? (int)(x + width - 2 - FontManager.getIconWidth() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (-ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + width - 2 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (int)(x + width - 2 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : -ClickGUI.instance.enabledSideX.getValue())) : x + width - 6 - FontManager.getIconWidth()))) + SideIconXOffset());
                        theX -= module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIcon((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                    else {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + width - 2 - FontManager.getIconWidth() + SideIconXOffset()) : ((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (module.isEnabled() ? (int)(x + width - 2 - FontManager.getIconWidth() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (-ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (int)(x + width - 2 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Right ? (int)(x + width - 2 - FontManager.getIconWidth() - ClickGUI.instance.enabledSideSize.getValue() - (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : -ClickGUI.instance.enabledSideX.getValue())) : x + width - 2 - FontManager.getIconWidth())) + SideIconXOffset());
                        theX -= module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIcon((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                }
            }
            else {
                if (isExtended) {
                    if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Plus || ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + 2 + SideIconXOffset()) : ((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (module.isEnabled() ? (int)(x + 2 + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + 2 + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (int)(x + 2 + ClickGUI.instance.enabledSideSize.getValue() + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : ClickGUI.instance.enabledSideX.getValue())) : x + 2)) + SideIconXOffset());
                        theX += module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIconExtended((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                    else {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + 6 + SideIconXOffset()) : ((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (module.isEnabled() ? (int)(x + 6 + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + 6 + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (int)(x + 6 + ClickGUI.instance.enabledSideSize.getValue() + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : ClickGUI.instance.enabledSideX.getValue())) : x + 6)) + SideIconXOffset());
                        theX += module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIconExtended((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                }
                else {
                    if (ClickGUI.instance.sideIconMode.getValue() == ClickGUI.SideIconMode.Future) {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + 6 + SideIconXOffset()) : (((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (module.isEnabled() ? (int)(x + 2 + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + 2 + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (int)(x + 2 + ClickGUI.instance.enabledSideSize.getValue() + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : ClickGUI.instance.enabledSideX.getValue())) : x + 6))) + SideIconXOffset());
                        theX += module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIcon((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                    else {
                        float theX = ClickGUI.instance.sideIconNoMove.getValue() ? (x + 2 + SideIconXOffset()) : ((ClickGUI.instance.enabledSideMove.getValue() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (module.isEnabled() ? (int)(x + 2 + (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f)))) : (int)(x + 2 + ClickGUI.instance.enabledSideSize.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideSize.getValue() / 300.0f)) + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : (ClickGUI.instance.enabledSideX.getValue()) - (loopsNumSide * (ClickGUI.instance.enabledSideX.getValue() / 300.0f))))) : (module.isEnabled() && ClickGUI.instance.enabledSide.getValue() && ClickGUI.instance.enabledSideSide.getValue() == ClickGUI.EnabledSideSide.Left ? (int)(x + 2 + ClickGUI.instance.enabledSideSize.getValue() + (ClickGUI.instance.sideIconNoMove.getValue() ? 0 : -ClickGUI.instance.enabledSideX.getValue())) : x + 2)) + SideIconXOffset());
                        theX += module.isEnabled() ? (ClickGUI.instance.enabledSideMove.getValue() ? (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f)) : ClickGUI.instance.enabledSideIconXOffset.getValue()) : (ClickGUI.instance.enabledSideMove.getValue() ? (ClickGUI.instance.enabledSideIconXOffset.getValue() - (loopsNumSide * (ClickGUI.instance.enabledSideIconXOffset.getValue() / 300.0f))) : 0);
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()), 0.0f);
                        GL11.glScalef(ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue(), ClickGUI.instance.moduleExtendedIconScale.getValue());

                        FontManager.drawIcon((int)(theX), y + 5 + CustomFont.instance.textOffset.getValue(), sideIconColor);

                        GL11.glScalef(1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue(), 1.0f / ClickGUI.instance.moduleExtendedIconScale.getValue());
                        GL11.glTranslatef((theX) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, (y + 5 + CustomFont.instance.textOffset.getValue()) * (1.0f - ClickGUI.instance.moduleExtendedIconScale.getValue()) * -1.0f, 0.0f);
                    }
                }
            }
        }
        GlStateManager.enableAlpha();
    }

    @Override
    public void bottomRender(int mouseX, int mouseY, boolean lastSetting, boolean firstSetting, float partialTicks) {}

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY))
            return false;
        if (mouseButton == 0) {
            module.toggle();
            SoundUtil.playButtonClick();
        } else if (mouseButton == 1) {
            buttonTimer.reset();
            isExtended = !isExtended;
            SoundUtil.playButtonClick();
        }
        return true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (net.spartanb312.base.gui.Component setting : settings) {
            setting.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (net.spartanb312.base.gui.Component setting : settings) {
            setting.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public String getDescription() {
        return module.description;
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        if (!anyExpanded) {
            return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height - ClickGUI.instance.moduleGap.getValue()) && mouseY <= Math.max(y, y + height - ClickGUI.instance.moduleGap.getValue());
        }
        else {
            return false;
        }
    }
}
