package net.spartanb312.base.hud.huds;

import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.engine.AsyncRenderer;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.spartanb312.base.utils.ColorUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "ActiveModuleList", category = Category.HUD)
public class ActiveModuleList extends HUDModule {

    public Setting<Boolean> shadow = setting("Shadow", true).des("Draw Text Shadow Under Module List");
    Setting<ListPos> listPos = setting("ListPos", ListPos.RightTop).des("The position of list");
    Setting<Boolean> potionMove = setting("MoveOnPotion", true).whenAtMode(listPos, ListPos.RightTop).des("Move List When Potions Are Active");
    Setting<Color> listColor = setting("ListColor", new Color(new java.awt.Color(100, 61, 255, 200).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 100, 61, 255, 200));
    Setting<Boolean> listRainbowRoll = setting("ListRainbowRoll", false).des("Rolling list rainbow").only(v -> listColor.getValue().getRainbow());
    Setting<Float> listRainbowRollSize = setting("ListRainbowRollSize", 1.0f, 0.1f, 2.0f).whenTrue(listRainbowRoll).only(v -> listColor.getValue().getRainbow());

    public ActiveModuleList() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                int startX = x;
                int startY = y;

                if (mc.player.getActivePotionEffects().size() > 0 && listPos.getValue().equals(ListPos.RightTop) && potionMove.getValue()) {
                    startY += 26;
                }

                int index = 0;

                List<Module> moduleList = BaseCenter.MODULE_BUS.getModules().stream().sorted(Comparator.comparing(it -> -FontManager.getWidthHUD(it.getHudSuffix()))).collect(Collectors.toList());

                for (Module module : moduleList) {
                    if (!module.visibleSetting.getValue().getVisible()) continue;
                    int color;
                    if (listColor.getValue().getRainbow() && listRainbowRoll.getValue()) {
                        color = ColorUtil.rainbow(index * 100, listColor.getValue().getRainbowSpeed(), listRainbowRollSize.getValue(), listColor.getValue().getRainbowSaturation(), listColor.getValue().getRainbowBrightness());
                    }
                    else {
                        color = listColor.getValue().getColor();
                    }

                    index++;
                    String information = module.getHudSuffix();
                    switch (listPos.getValue()) {
                        case RightDown: {
                            drawAsyncString(information, (startX - FontManager.getWidthHUD(information)) + width, (startY - FontManager.getHeightHUD() * index) + height, color, shadow.getValue());
                            break;
                        }
                        case LeftTop: {
                            drawAsyncString(information, startX, startY + 3 + FontManager.getHeightHUD() * (index - 1), color, shadow.getValue());
                            break;
                        }
                        case LeftDown: {
                            drawAsyncString(information, startX, (startY - FontManager.getHeightHUD() * index) + height, color, shadow.getValue());
                            break;
                        }
                        default: {
                            drawAsyncString(information, (startX - FontManager.getWidthHUD(information)) + width, startY + 3 + FontManager.getHeightHUD() * (index - 1), color, shadow.getValue());
                            break;
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

    public enum ListPos {
        RightTop, RightDown, LeftTop, LeftDown
    }

}
