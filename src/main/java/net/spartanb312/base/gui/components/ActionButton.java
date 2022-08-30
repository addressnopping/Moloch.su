package net.spartanb312.base.gui.components;

import net.spartanb312.base.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;
import net.spartanb312.base.client.GUIManager;
import net.spartanb312.base.core.concurrent.task.VoidTask;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Component;
import net.spartanb312.base.gui.Panel;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class ActionButton extends Component {

    Setting<VoidTask> setting;
    String moduleName;

    public ActionButton(Setting<VoidTask> setting, int width, int height, Panel father, Module module) {
        this.moduleName = module.name;
        this.setting = setting;
        this.width = width;
        this.height = height;
        this.father = father;
    }

    @Override
    public void render(int mouseX, int mouseY, float translateDelta, float partialTicks) {

        if (CustomFont.instance.font.getValue() == CustomFont.FontMode.Minecraft) {
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()),0.0f);
            GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

            mc.fontRenderer.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f), GUIManager.getColor3I(), CustomFont.instance.textShadow.getValue());

            GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
            GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f)) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f,0.0f);
            GL11.glDisable(GL_TEXTURE_2D);
        }
        else {
            if (CustomFont.instance.textShadow.getValue()) {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.drawShadow(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, GUIManager.getColor3I());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);
            }
            else {
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()), ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()), 0.0f);
                GL11.glScalef(CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue(), CustomFont.instance.componentTextScale.getValue());

                fontManager.draw(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 3, GUIManager.getColor3I());

                GL11.glScalef(1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()), 1.0f / (CustomFont.instance.componentTextScale.getValue()));
                GL11.glTranslatef((x + 5) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, ((int) (y + height / 2 - font.getHeight() / 2f) + 3) * (1.0f - CustomFont.instance.componentTextScale.getValue()) * -1.0f, 0.0f);

            }
        }
    }

    @Override
    public void bottomRender(int mouseX, int mouseY, boolean lastSetting, boolean firstSetting, float partialTicks) {
        GlStateManager.disableAlpha();
        drawSettingRects(lastSetting, false);

        drawExtendedGradient(lastSetting, false);
        drawExtendedLine(lastSetting);

        renderHoverRect(moduleName + setting.getName(), mouseX, mouseY, 2.0f, -1.0f, false);

        GlStateManager.enableAlpha();
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        if (!anyExpanded) {
            return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height);
        }
        else {
            return false;
        }
    }


    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY))
            return false;

        if (mouseButton == 0) {
            setting.getValue().invoke();
        }
        return true;

    }

    @Override
    public String getDescription() {
        return setting.getDescription();
    }

}
