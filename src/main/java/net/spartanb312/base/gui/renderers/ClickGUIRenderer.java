package net.spartanb312.base.gui.renderers;

import net.spartanb312.base.gui.Component;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.gui.components.ModuleButton;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.modules.client.ClickGUI;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_UP;

public class ClickGUIRenderer {

    public ArrayList<Panel> panels = new ArrayList<>();
    public static ClickGUIRenderer instance = new ClickGUIRenderer();
    public static float scrolledY = 0;

    public ClickGUIRenderer() {
        int startX = 5;
        for (Category category : Category.values()) {
            if (category == Category.HIDDEN || category.isHUD) continue;
            panels.add(new Panel(category, startX, 20, 120, 16));
            startX += 133;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float translateDelta, float partialTicks) {
        scrollGUI();

        for (int i = panels.size() - 1; i >= 0; i--) {
            panels.get(i).drawShadowsAndGlow(mouseX, mouseY);
        }

        for (int i = panels.size() - 1; i >= 0; i--) {
            panels.get(i).drawScreen(mouseX, mouseY, translateDelta, partialTicks);
        }
    }

    public Panel getPanelByName(String name) {
        Panel getPane = null;
        if (panels != null)
            for (Panel panel : panels) {
                if (!panel.category.categoryName.equals(name)) {
                    continue;
                }
                getPane = panel;
            }
        return getPane;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, mouseButton)) return;
            if (!panel.extended) continue;
            for (ModuleButton part : panel.elements) {
                if (part.mouseClicked(mouseX, mouseY, mouseButton)) return;
                if (!part.isExtended) continue;
                for (Component component : part.settings) {
                    if (!component.isVisible()) continue;
                    if (component.mouseClicked(mouseX, mouseY, mouseButton)) return;
                }
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            ModuleManager.getModule(ClickGUI.class).disable();
        }
        for (Panel panel : panels) {
            panel.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void scrollGUI() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0 || (ClickGUI.instance.arrowScroll.getValue() && Keyboard.isKeyDown(KEY_UP))) {
            panels.forEach(component -> component.y -= ClickGUI.instance.scrollSpeed.getValue());
            scrolledY += ClickGUI.instance.scrollSpeed.getValue();
        }

        if (dWheel > 0 || (ClickGUI.instance.arrowScroll.getValue() && Keyboard.isKeyDown(KEY_DOWN))) {
            panels.forEach(component -> component.y += ClickGUI.instance.scrollSpeed.getValue());
            scrolledY -= ClickGUI.instance.scrollSpeed.getValue();
        }
    }
}
