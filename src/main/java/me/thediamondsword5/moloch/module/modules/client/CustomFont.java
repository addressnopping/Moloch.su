package me.thediamondsword5.moloch.module.modules.client;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.core.common.Color;

@Parallel
@ModuleInfo(name = "GUIFont", category = Category.CLIENT, description = "GUI Font Settings")
public class CustomFont extends Module {
    public static CustomFont instance;

    Setting<Page> page = setting("Page", Page.EverythingElse);

    public Setting<FontMode> font = setting("GeneralFont", FontMode.Comfortaa).des("General Font").whenAtMode(page, Page.EverythingElse);
    public Setting<Boolean> textShadow = setting("TextShadows", false).des("Draw Shadow Under GUI Text").whenAtMode(page, Page.EverythingElse);
    public Setting<TextPos> moduleTextPos = setting("ModuleTextPos", TextPos.Center).des("Module Text Pos").whenAtMode(page, Page.EverythingElse);
    public Setting<Integer> moduleTextOffsetX = setting("TextOffsetX", 0, 0, 50).des("Module Name X Offset").whenAtMode(page, Page.EverythingElse);
    public Setting<Integer> textOffset = setting("TextOffsetY", 0, -13, 13).des("Module Name Y Offset").whenAtMode(page, Page.EverythingElse);
    public Setting<Float> textScale = setting("TextScale", 0.8f, 0.1f, 1.5f).des("Size Of Text").whenAtMode(page, Page.EverythingElse);
    public Setting<Float> componentTextScale = setting("SettingsTextScale", 0.8f, 0.1f, 1.5f).des("Size Of Module Config Settings Text").whenAtMode(page, Page.EverythingElse);
    public Setting<Color> moduleTextColor = setting("ModuleTextColor", new Color(new java.awt.Color(72, 72, 72, 206).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 72, 72, 72, 206)).des("Module Text Color").whenAtMode(page, Page.EverythingElse);

    public Setting<FontMode> categoryFont = setting("CategoryFont", FontMode.Objectivity).des("Category Text Font").whenAtMode(page, Page.CategoryText);
    public Setting<TextPos> categoryTextPos = setting("CategoryTextPos", TextPos.Center).des("Category Text Pos").whenAtMode(page, Page.CategoryText);
    public Setting<Float> categoryTextX = setting("CategoryTextX", 1.7f, -30.0f, 30.0f).des("Category Text X").whenAtMode(page, Page.CategoryText);
    public Setting<Float> categoryTextY = setting("CategoryTextY", 0.0f, -15.0f, 15.0f).des("Category Text Y").whenAtMode(page, Page.CategoryText);
    public Setting<Float> categoryTextScale = setting("CategoryTextScale", 1.3f, 0.1f, 2.0f).des("Size Of Category Text").whenAtMode(page, Page.CategoryText);
    public Setting<Boolean> categoryTextShadow = setting("CategoryTextShadow", false).des("Draw Shadow Under Category Text").whenAtMode(page, Page.CategoryText);
    public Setting<Boolean> categoryTextShadowGradient = setting("CTextShadowGradient", true).des("Draw Gradient Shadow Under Category Text").whenAtMode(page, Page.CategoryText);
    public Setting<Float> categoryTextShadowGradientX = setting("CTextShadowGradientX", 0.0f, -30.0f, 30.0f).des("Category Text Gradient Shadow X").whenTrue(categoryTextShadowGradient).whenAtMode(page, Page.CategoryText);
    public Setting<Float> categoryTextShadowGradientY = setting("CTextShadowGradientY", 0.0f, -15.0f, 15.0f).des("Category Text Gradient Shadow Y").whenTrue(categoryTextShadowGradient).whenAtMode(page, Page.CategoryText);
    public Setting<Float> categoryTextShadowGradientSize = setting("CTShadowGradientSize", 0.1f, 0.0f, 1.0f).des("Category Text Gradient Shadow Size").whenTrue(categoryTextShadowGradient).whenAtMode(page, Page.CategoryText);
    public Setting<Integer> categoryTextShadowGradientAlpha = setting("CTShadowGradientAlpha", 81, 0, 255).des("Category Text Gradient Shadow Alpha").whenTrue(categoryTextShadowGradient).whenAtMode(page, Page.CategoryText);
    public Setting<Color> categoryTextColor = setting("CategoryTextColor", new Color(new java.awt.Color(255, 255, 255, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 255)).des("Category Text Color").whenAtMode(page, Page.CategoryText);


    public CustomFont() {
        instance = this;
    }

    enum Page {
        CategoryText , EverythingElse
    }

    public enum FontMode {
        Comfortaa, Arial, Objectivity, Minecraft
    }

    public enum TextPos {
        Left, Right, Center
    }

    @Override
    public void onDisable() {
        enable();
    }
}
