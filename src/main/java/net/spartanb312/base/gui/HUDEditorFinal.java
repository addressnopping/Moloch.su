package net.spartanb312.base.gui;

import me.thediamondsword5.moloch.gui.components.StringInput;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.spartanb312.base.client.ConfigManager;
import net.spartanb312.base.module.modules.client.HUDEditor;
import net.spartanb312.base.utils.Timer;
import me.thediamondsword5.moloch.module.modules.client.Blur;
import me.thediamondsword5.moloch.module.modules.client.MoreClickGUI;
import me.thediamondsword5.moloch.module.modules.client.Particles;
import me.thediamondsword5.moloch.utils.graphics.ParticleUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.client.GUIManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.gui.renderers.HUDEditorRenderer;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.spartanb312.base.gui.ClickGUIFinal.description;
import static net.spartanb312.base.gui.ClickGUIFinal.white;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@SuppressWarnings("ALL")
public class HUDEditorFinal extends GuiScreen
{
    public int flag = 0;
    public static HUDEditorFinal instance;

    static Panel panel;
    Timer guiAnimateTimer = new Timer();
    float delta = 0;

    public HUDEditorFinal ()
    {
        instance = this;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }


    @Override
    public void onGuiClosed()
    {
        if (mc.entityRenderer.getShaderGroup() != null)
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();

        if (ModuleManager.getModule(HUDEditor.class).isEnabled())
            ModuleManager.getModule(HUDEditor.class).disable();
    }

    private float alphaFactor()
    {
        float f = delta / mc.currentScreen.height;
        if (f > 1.0f) f = 1.0f;
        return f;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (ModuleManager.getModule(Blur.class).isEnabled() && Blur.INSTANCE.blurClickGUI.getValue())
        {
            RenderUtils2D.drawBlurAreaPre(ClickGUI.instance.guiMove.getValue() ? (Blur.INSTANCE.blurFactor.getValue() * alphaFactor()) : Blur.INSTANCE.blurFactor.getValue(), partialTicks);
            RenderUtils2D.drawBlurRect(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), 0, 0, mc.displayWidth, mc.displayHeight);
            RenderUtils2D.drawBlurAreaPost();
        }

        RenderUtils2D.prepareGl();
        if (ClickGUI.instance.backgroundColor.getValue())
        {
            if (ClickGUI.instance.gradient.getValue())
            {
                GlStateManager.disableAlpha();
                Color trColor = ClickGUI.instance.trColor.getValue().getColorColor();
                Color tlColor = ClickGUI.instance.tlColor.getValue().getColorColor();
                Color brColor = ClickGUI.instance.brColor.getValue().getColorColor();
                Color blColor = ClickGUI.instance.blColor.getValue().getColorColor();
                RenderUtils2D.drawCustomRect(0, 0, width, height, new Color(trColor.getRed(), trColor.getGreen(), trColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.trColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.trColor.getValue().getAlpha()).getRGB(),  new Color(tlColor.getRed(), tlColor.getGreen(), tlColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.tlColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.tlColor.getValue().getAlpha()).getRGB(),  new Color(blColor.getRed(), blColor.getGreen(), blColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.blColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.blColor.getValue().getAlpha()).getRGB(),  new Color(brColor.getRed(), brColor.getGreen(), brColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.brColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.brColor.getValue().getAlpha()).getRGB());
                GlStateManager.enableAlpha();
            }
            else
            {
                Color bgColor = ClickGUI.instance.bgColor.getValue().getColorColor();
                RenderUtils2D.drawRect(0, 0, width, height, new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.bgColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.bgColor.getValue().getAlpha()).getRGB());
            }
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
        }

        description = null;

        if (GUIManager.isParticle())
        {
            ParticleUtil.render();
            RenderUtils2D.prepareGl();
            GL11.glEnable(GL_BLEND);
        }

        HUDEditorRenderer.instance.drawHUDElements(mouseX, mouseY, partialTicks);

        if (ClickGUI.instance.guiMove.getValue())
        {

            if (ModuleManager.getModule(HUDEditor.class).isDisabled())
            {
                if (guiAnimateTimer.passed(1))
                {
                    delta -= (mc.currentScreen.height - delta + 1) * ClickGUI.instance.guiMoveSpeed.getValue();
                    guiAnimateTimer.reset();
                }

                if (delta <= 0)
                    delta = 0;
            }
            else
            {
                if (guiAnimateTimer.passed(1))
                {
                    delta += ((mc.currentScreen.height - delta) / 5.0f) * ClickGUI.instance.guiMoveSpeed.getValue();
                    guiAnimateTimer.reset();
                }

                if (delta >= mc.currentScreen.height)
                {
                    delta = mc.currentScreen.height;
                }
            }

            GL11.glTranslatef(0.0f, mc.currentScreen.height - delta, 0.0f);
        }

        RenderUtils2D.prepareGl();
        HUDEditorRenderer.instance.drawScreen(mouseX, mouseY, delta, partialTicks);

        flag = 1;

        if (description != null)
        {
            if (MoreClickGUI.instance.descriptionMode.getValue() == MoreClickGUI.DescriptionMode.MouseTag)
            {
                RenderUtils2D.drawRect(description.b.x + 10, description.b.y, description.b.x + 12 + FontManager.getWidth(description.a), description.b.y + FontManager.getHeight() + 4, 0x85000000);
                RenderUtils2D.drawRectOutline(description.b.x + 10, description.b.y, description.b.x + 12 + FontManager.getWidth(description.a), description.b.y + FontManager.getHeight() + 4, GUIManager.getColor4I(), false, false);
                FontManager.draw(description.a, description.b.x + 11, description.b.y + 4, white);
            }
        }

        if (MoreClickGUI.instance.descriptionMode.getValue() == MoreClickGUI.DescriptionMode.Hub)
            ClickGUIFinal.drawDescriptionHub(mouseX, mouseY, ClickGUIFinal.descriptionHubDragging);

        if (GUIManager.isParticle())
            GL11.glDisable(GL_BLEND);

        if (ModuleManager.getModule(HUDEditor.class).isDisabled() && ClickGUI.instance.guiMove.getValue())
        {
            if (mc.currentScreen instanceof HUDEditorFinal && delta <= 0)
            {
                if (Particles.INSTANCE.isEnabled())
                    ParticleUtil.clearParticles();

                mc.displayGuiScreen(null);
            }
        }
        RenderUtils2D.releaseGl();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        HUDEditorRenderer.instance.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= Math.min(ClickGUIFinal.descriptionHubX, ClickGUIFinal.descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue()) && mouseX <= Math.max(ClickGUIFinal.descriptionHubX, ClickGUIFinal.descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue()) && mouseY >= Math.min(ClickGUIFinal.descriptionHubY, ClickGUIFinal.descriptionHubY + ClickGUIFinal.descriptionHubHeight) && mouseY <= Math.max(ClickGUIFinal.descriptionHubY, ClickGUIFinal.descriptionHubY + ClickGUIFinal.descriptionHubHeight))
        {
            ClickGUIFinal.descriptionHubDragging = true;
            ClickGUIFinal.descriptionHubX2 = ClickGUIFinal.descriptionHubX - mouseX;
            ClickGUIFinal.descriptionHubY2 = ClickGUIFinal.descriptionHubY - mouseY;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        HUDEditorRenderer.instance.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        HUDEditorRenderer.instance.mouseReleased(mouseX, mouseY, state);
        ClickGUIFinal.descriptionHubDragging = false;
    }
}
