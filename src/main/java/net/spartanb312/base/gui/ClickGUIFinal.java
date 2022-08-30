package net.spartanb312.base.gui;

import me.thediamondsword5.moloch.gui.components.StringInput;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.client.GUIManager;
import net.spartanb312.base.gui.renderers.ClickGUIRenderer;
import net.spartanb312.base.utils.Timer;
import me.thediamondsword5.moloch.module.modules.client.Blur;
import me.thediamondsword5.moloch.module.modules.client.Particles;
import me.thediamondsword5.moloch.utils.graphics.ParticleUtil;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import net.spartanb312.base.utils.graphics.VertexBuffer;
import net.spartanb312.base.utils.math.Pair;
import net.spartanb312.base.utils.math.Vec2I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import me.thediamondsword5.moloch.module.modules.client.MoreClickGUI;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("ALL")
public class ClickGUIFinal extends GuiScreen
{
    private static Minecraft mc = Minecraft.getMinecraft();
    public int flag = 0;

    public static ClickGUIFinal instance;

    public static Pair<String, Vec2I> description = null;
    public static int white = new Color(255, 255, 255, 255).getRGB();
    public static boolean descriptionHubDragging = false;
    public static int descriptionHubHeight;
    public static int descriptionHubX;
    public static int descriptionHubY;
    public static int descriptionHubX2;
    public static int descriptionHubY2;
    static boolean descriptionBoxAnimationFlag = false;
    static Timer descriptionBoxAnimationTimer = new Timer();
    static float descriptionBoxAnimationThreader = 0.0f;
    static int lastIndex = 0;
    public static int previousIndex = 0;
    static Timer descriptionTextAnimationTimer = new Timer();
    public static float descriptionTextAnimationThreader = 0.0f;
    static boolean isTransitioningOutTextFlag = false;
    static boolean noAlphaTextFlag = false;
    static String lastText = "";
    public static String previousText = "";
    public static String staticString = "";
    Timer guiAnimateTimer = new Timer();
    float delta = 0;

    static Panel panel;

    public ClickGUIFinal()
    {
        instance = this;
    }

    public boolean isClicked(int mouseX, int mouseY, int mouseButton, int startX, int startY, int endX, int endY)
    {
        if (Mouse.getEventButton() == mouseButton && Mouse.isButtonDown(mouseButton) && mouseX >= Math.min(startX, endX) && mouseX <= Math.max(startX, endX) && mouseY >= Math.min(startY, endY) && mouseY <= Math.max(startY, endY))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();

        if (ModuleManager.getModule(ClickGUI.class).isEnabled())
            ModuleManager.getModule(ClickGUI.class).disable();
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

        if (ModuleManager.getModule(Blur.class).isEnabled() && OpenGlHelper.isFramebufferEnabled() && Blur.INSTANCE.blurClickGUI.getValue())
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
                RenderUtils2D.drawCustomRect(0, 0, width, height, new Color(trColor.getRed(), trColor.getGreen(), trColor.getBlue(), ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.trColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.trColor.getValue().getAlpha()).getRGB(),  new Color(tlColor.getRed(), tlColor.getGreen(), tlColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.tlColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.tlColor.getValue().getAlpha()).getRGB(),  new Color(blColor.getRed(), blColor.getGreen(), blColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.blColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.blColor.getValue().getAlpha()).getRGB(),  new Color(brColor.getRed(), brColor.getGreen(), brColor.getBlue() ,ClickGUI.instance.guiMove.getValue() ? (int)(ClickGUI.instance.brColor.getValue().getAlpha() * alphaFactor()) : ClickGUI.instance.brColor.getValue().getAlpha()).getRGB());
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

        if (ClickGUI.instance.guiMove.getValue())
        {

            if (ModuleManager.getModule(ClickGUI.class).isDisabled())
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

        ClickGUIRenderer.instance.drawScreen(mouseX, mouseY, delta, partialTicks);

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
        {
           drawDescriptionHub(mouseX, mouseY, descriptionHubDragging);
        }

        if (ClickGUI.instance.guiMove.getValue())
            GL11.glTranslatef(0.0f,delta - mc.currentScreen.height, 0.0f);

        if (GUIManager.isParticle())
            GL11.glDisable(GL_BLEND);

        if (ModuleManager.getModule(ClickGUI.class).isDisabled() && ClickGUI.instance.guiMove.getValue())
        {
            if (mc.currentScreen instanceof ClickGUIFinal && delta <= 0)
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
        ClickGUIRenderer.instance.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= Math.min(descriptionHubX, descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue()) && mouseX <= Math.max(descriptionHubX, descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue()) && mouseY >= Math.min(descriptionHubY, descriptionHubY + descriptionHubHeight) && mouseY <= Math.max(descriptionHubY, descriptionHubY + descriptionHubHeight))
        {
            descriptionHubDragging = true;
            descriptionHubX2 = descriptionHubX - mouseX;
            descriptionHubY2 = descriptionHubY - mouseY;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        ClickGUIRenderer.instance.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        ClickGUIRenderer.instance.mouseReleased(mouseX, mouseY, state);
        descriptionHubDragging = false;
    }

    public static void drawDescriptionHub(int mouseX, int mouseY, boolean isDescriptionHubDragging)
    {
        descriptionHubHeight = MoreClickGUI.instance.descriptionModeHubInitialHeight.getValue();
        Color descriptionHubColor = new Color(MoreClickGUI.instance.descriptionModeHubColor.getValue().getColorColor().getRed(), MoreClickGUI.instance.descriptionModeHubColor.getValue().getColorColor().getGreen(), MoreClickGUI.instance.descriptionModeHubColor.getValue().getColorColor().getBlue(), MoreClickGUI.instance.descriptionModeHubColor.getValue().getAlpha());

        if (isDescriptionHubDragging)
        {
            descriptionHubX = descriptionHubX2 + mouseX;
            descriptionHubY = descriptionHubY2 + mouseY;
        }

        float extraRectHeight = 0;
        ArrayList<ITextComponent> lineList = new ArrayList<>();
        int index = 0;

        if (MoreClickGUI.instance.descriptionModeHubDescriptionAnimation.getValue())
        {
            String currentDescription = description != null ? description.a : "";

            if (lastText != currentDescription)
            {
                if (!isTransitioningOutTextFlag)
                    previousText = lastText;

                isTransitioningOutTextFlag = true;
            }


            if (description != null)
                lastText = description.a;
            else if (descriptionTextAnimationThreader > 0)
                isTransitioningOutTextFlag = true;
        }

        if (descriptionTextAnimationThreader <= 0)
            isTransitioningOutTextFlag = false;

        ITextComponent descriptionTextComponent = descriptionTextComponent = new TextComponentString("");

        if (isTransitioningOutTextFlag && MoreClickGUI.instance.descriptionModeHubDescriptionAnimation.getValue())
            descriptionTextComponent = new TextComponentString(previousText);

        if (description != null && (!isTransitioningOutTextFlag || !MoreClickGUI.instance.descriptionModeHubDescriptionAnimation.getValue()))
            descriptionTextComponent = new TextComponentString(description.a);

        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() != MoreClickGUI.DescriptionModeHubDesTextFont.Minecraft)
            lineList = (ArrayList)FontManager.splitTextCFont(descriptionTextComponent, (int)((MoreClickGUI.instance.descriptionModeHubLength.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXBoundingOffset.getValue()) / MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()), FontManager.descriptionHubDesTextFontRenderer());
        else
            lineList = (ArrayList)GuiUtilRenderComponents.splitText(descriptionTextComponent, (int)((MoreClickGUI.instance.descriptionModeHubLength.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXBoundingOffset.getValue()) / MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()), Minecraft.getMinecraft().fontRenderer, false, false);

        //set up rect stuff
        index = lineList.size();

        if (MoreClickGUI.instance.descriptionModeHubAnimation.getValue())
        {
            if (lastIndex != index) previousIndex = lastIndex;

            if (description != null) lastIndex = index;

            int passedms = (int) descriptionBoxAnimationTimer.hasPassed();
            if (passedms < 1000)
            {
                for (int i = 0; i <= passedms; i++)
                {
                    descriptionBoxAnimationThreader += ((((description == null && (MoreClickGUI.instance.descriptionModeHubDescriptionAnimation.getValue() ? descriptionTextAnimationThreader <= 0 : true)) || lastIndex < previousIndex) ? -1.0f : 1.0f) * MoreClickGUI.instance.descriptionModeHubAnimationSpeed.getValue()) / 200.0f;

                    if ((descriptionBoxAnimationThreader >= (FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (lastIndex - 1) && lastIndex >= previousIndex)
                    || (descriptionBoxAnimationThreader <= (FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (lastIndex - 1) && lastIndex < previousIndex))
                        descriptionBoxAnimationThreader = (FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (lastIndex - 1);

                    if (descriptionBoxAnimationThreader <= 0)
                        descriptionBoxAnimationThreader = 0;
                }
            }
            descriptionBoxAnimationTimer.reset();

            extraRectHeight = descriptionBoxAnimationThreader;
        }
        else if (description != null || (MoreClickGUI.instance.descriptionModeHubDescriptionAnimation.getValue() ? descriptionTextAnimationThreader >= 0 : true))
            extraRectHeight = (FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (index - 1);

        //draw rect stuff
        if (MoreClickGUI.instance.descriptionModeHubShadow.getValue())
            RenderUtils2D.drawBetterRoundRectFade(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + descriptionHubHeight + (MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) + (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? 0 : extraRectHeight), MoreClickGUI.instance.descriptionModeHubShadowSize.getValue(), 70.0f, false, MoreClickGUI.instance.descriptionModeHubShadowCenterRect.getValue(), false, new Color(0, 0, 0, MoreClickGUI.instance.descriptionModeHubShadowAlpha.getValue()).getRGB());

        if (MoreClickGUI.instance.descriptionModeHubRounded.getValue())
            RenderUtils2D.drawRoundedRect(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), MoreClickGUI.instance.descriptionModeHubRoundedRadius.getValue(), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + descriptionHubHeight + (MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) + (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? 0 : extraRectHeight), false, MoreClickGUI.instance.descriptionModeHubRoundedTopRight.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedTopLeft.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedBottomRight.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedBottomLeft.getValue(), descriptionHubColor.getRGB());
        else
            RenderUtils2D.drawRect(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + descriptionHubHeight + (MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) + (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? 0 : extraRectHeight), descriptionHubColor.getRGB());

        if (MoreClickGUI.instance.descriptionModeHubOutline.getValue())
        {
            if (MoreClickGUI.instance.descriptionModeHubRounded.getValue())
                RenderUtils2D.drawCustomRoundedRectOutline(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + descriptionHubHeight + (MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) + (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? 0 : extraRectHeight), MoreClickGUI.instance.descriptionModeHubRoundedRadius.getValue(), MoreClickGUI.instance.descriptionModeHubOutlineWidth.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedTopRight.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedTopLeft.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedBottomRight.getValue(), MoreClickGUI.instance.descriptionModeHubRoundedBottomLeft.getValue(), false, false, MoreClickGUI.instance.descriptionModeHubOutlineColor.getValue().getColor());
            else
                RenderUtils2D.drawRectOutline(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + descriptionHubHeight + (MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) + (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? 0 : extraRectHeight), MoreClickGUI.instance.descriptionModeHubOutlineWidth.getValue(), MoreClickGUI.instance.descriptionModeHubOutlineColor.getValue().getColor(), false, false);
        }

        if (MoreClickGUI.instance.descriptionModeHubBar.getValue())
        {
            if (MoreClickGUI.instance.descriptionModeHubBarRound.getValue())
                RenderUtils2D.drawRoundedRect(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), MoreClickGUI.instance.descriptionModeHubBarRoundRadius.getValue(), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + MoreClickGUI.instance.descriptionModeHubBarHeight.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), false, MoreClickGUI.instance.descriptionModeHubBarRoundTopRight.getValue(), MoreClickGUI.instance.descriptionModeHubBarRoundTopLeft.getValue(), MoreClickGUI.instance.descriptionModeHubBarRoundBottomRight.getValue(), MoreClickGUI.instance.descriptionModeHubBarRoundBottomLeft.getValue(), MoreClickGUI.instance.descriptionModeHubBarColor.getValue().getColor());
            else
                RenderUtils2D.drawRect(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue(), descriptionHubY + MoreClickGUI.instance.descriptionModeHubBarHeight.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), MoreClickGUI.instance.descriptionModeHubBarColor.getValue().getColor());

            if (MoreClickGUI.instance.descriptionModeHubBarGlow.getValue())
                RenderUtils2D.drawRoundedRectFade(descriptionHubX + MoreClickGUI.instance.descriptionModeHubBarGlowXOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubBarGlowWidth.getValue() / 2.0f), descriptionHubY - (MoreClickGUI.instance.descriptionModeHubBarGlowHeight.getValue() / 2.0f) + MoreClickGUI.instance.descriptionModeHubBarGlowYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), 1.0f, true, false, descriptionHubX + MoreClickGUI.instance.descriptionModeHubLength.getValue() + (MoreClickGUI.instance.descriptionModeHubBarGlowWidth.getValue() / 2.0f) + MoreClickGUI.instance.descriptionModeHubBarGlowXOffset.getValue(), descriptionHubY + MoreClickGUI.instance.descriptionModeHubBarHeight.getValue() + (MoreClickGUI.instance.descriptionModeHubBarGlowHeight.getValue() / 2.0f) + MoreClickGUI.instance.descriptionModeHubBarGlowYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), MoreClickGUI.instance.descriptionModeHubBarGlowColor.getValue().getColor());
        }

        //set up text stuff
        int desHubTextAlpha = MoreClickGUI.instance.descriptionModeHubDescriptionColor.getValue().getAlpha();

        if (MoreClickGUI.instance.descriptionModeHubDescriptionAnimation.getValue())
        {

            int passedms = (int) descriptionTextAnimationTimer.hasPassed();
            if (passedms < 1000)
            {
                for (int i = 0; i <= passedms; i++)
                {
                    descriptionTextAnimationThreader += (((description == null || isTransitioningOutTextFlag || extraRectHeight < (FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (index - 1)) ? -1.0f : 1.0f) * MoreClickGUI.instance.descriptionModeHubDescriptionAnimationSpeed.getValue()) / 4.0f;

                    if (descriptionTextAnimationThreader >= 300.0f)
                        descriptionTextAnimationThreader = 300.0f;

                    if (descriptionTextAnimationThreader <= 0)
                    {
                        isTransitioningOutTextFlag = false;
                        descriptionTextAnimationThreader = 0;
                    }
                }
            }
            descriptionTextAnimationTimer.reset();

            desHubTextAlpha = (int)((desHubTextAlpha / 300.0f) * descriptionTextAnimationThreader);

            if (desHubTextAlpha <= 4)
                desHubTextAlpha = 4;
        }


        int desHubTextColor = new Color(MoreClickGUI.instance.descriptionModeHubDescriptionColor.getValue().getColorColor().getRed(), MoreClickGUI.instance.descriptionModeHubDescriptionColor.getValue().getColorColor().getGreen(), MoreClickGUI.instance.descriptionModeHubDescriptionColor.getValue().getColorColor().getBlue(), desHubTextAlpha).getRGB();


        if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() != MoreClickGUI.DescriptionModeHubDesTextFont.Minecraft)
            lineList = (ArrayList)FontManager.splitTextCFont(descriptionTextComponent, (int)((MoreClickGUI.instance.descriptionModeHubLength.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXBoundingOffset.getValue()) / MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()), FontManager.descriptionHubDesTextFontRenderer());
        else
            lineList = (ArrayList)GuiUtilRenderComponents.splitText(descriptionTextComponent, (int)((MoreClickGUI.instance.descriptionModeHubLength.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue() - MoreClickGUI.instance.descriptionModeHubDescriptionXBoundingOffset.getValue()) / MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()), Minecraft.getMinecraft().fontRenderer, false, false);

        int index2 = -1;

        //draw text stuff
        if (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue())
            GL11.glTranslatef(0.0f, ((FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (lineList.size() - 1)) * -1.0f, 0.0f);

        GL11.glTranslatef((descriptionHubX + MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue()) * (1.0f - MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()), (descriptionHubY + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubDescriptionYOffset.getValue() + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue())) * (1.0f - MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()), 0.0f);
        GL11.glScalef(MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue(), MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue(), 1.0f);

        for (ITextComponent lines : lineList)
        {
            index2++;

            String currentLineStr = lines.getFormattedText();
            if (currentLineStr != null && currentLineStr.length() > 0)
            {
                if (String.valueOf(currentLineStr.charAt(0)).equals(" "))
                    currentLineStr = currentLineStr.substring(1);
            }

            if (MoreClickGUI.instance.descriptionModeHubDescriptionFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Minecraft)
            {
                GL11.glEnable(GL_TEXTURE_2D);
                mc.fontRenderer.drawString(currentLineStr, descriptionHubX + MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue(), descriptionHubY + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubDescriptionYOffset.getValue() + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + (FontManager.getHeightDescriptionHubDesText() * index2), desHubTextColor, MoreClickGUI.instance.descriptionModeHubDescriptionShadow.getValue());
                GL11.glDisable(GL_TEXTURE_2D);
            }
            else {
                if (MoreClickGUI.instance.descriptionModeHubDescriptionShadow.getValue())
                    FontManager.drawDesTextShadow(currentLineStr, descriptionHubX + MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue(), descriptionHubY + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubDescriptionYOffset.getValue() + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + (FontManager.getHeightDescriptionHubDesText() * index2), desHubTextColor);
                else
                    FontManager.drawDesText(currentLineStr, descriptionHubX + MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue(), descriptionHubY + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubDescriptionYOffset.getValue() + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + (FontManager.getHeightDescriptionHubDesText() * index2), desHubTextColor);
            }
        }

        GL11.glScalef(1.0f / MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue(), 1.0f / MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue(), 1.0f);
        GL11.glTranslatef((descriptionHubX + MoreClickGUI.instance.descriptionModeHubDescriptionXOffset.getValue()) * (1.0f - MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * -1.0f, ((descriptionHubY + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue()) + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubDescriptionYOffset.getValue() + (FontManager.getHeightDescriptionHubDesText() - MoreClickGUI.instance.descriptionModeHubHeightBetweenRowsOfText.getValue())) * (1.0f - MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue())) * -1.0f, 0.0f);

        if (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue())
            GL11.glTranslatef(0.0f, (FontManager.getHeightDescriptionHubDesText() * MoreClickGUI.instance.descriptionModeHubDescriptionSize.getValue()) * (lineList.size() - 1), 0.0f);

        if (MoreClickGUI.instance.descriptionModeHubBarIconBG.getValue())
        {
            float endX = descriptionHubX + 10.0f + MoreClickGUI.instance.descriptionModeHubBarIconBGSideX.getValue();

            GL11.glTranslatef(((1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()) * (endX - descriptionHubX)) / 2.0f, ((1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()) * MoreClickGUI.instance.descriptionModeHubBarHeight.getValue()) / 2.0f, 0.0f);

            GL11.glTranslatef(descriptionHubX * (1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()), (descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0)) * (1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()), 0.0f);
            GL11.glScalef(MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue(), MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue(), MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue());

            RenderUtils2D.drawCustomCategoryRoundedRect(descriptionHubX, descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), endX, descriptionHubY + MoreClickGUI.instance.descriptionModeHubBarHeight.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), MoreClickGUI.instance.descriptionModeHubBarRoundRadius.getValue(), false, MoreClickGUI.instance.descriptionModeHubBarRound.getValue() ? MoreClickGUI.instance.descriptionModeHubBarRoundTopLeft.getValue() : false, false, MoreClickGUI.instance.descriptionModeHubBarRound.getValue() ? MoreClickGUI.instance.descriptionModeHubBarRoundBottomLeft.getValue() : false, MoreClickGUI.instance.descriptionModeHubBarIconBGFade.getValue(), false, MoreClickGUI.instance.descriptionModeHubBarIconBGFadeSize.getValue(), MoreClickGUI.instance.descriptionModeHubBarIconBGColor.getValue().getColor());

            GL11.glScalef(1.0f / MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue(), 1.0f / MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue(), 1.0f / MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue());
            GL11.glTranslatef(descriptionHubX * (1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()) * -1.0f, (descriptionHubY - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0)) * (1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()) * -1.0f, 0.0f);

            GL11.glTranslatef((((1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()) * (endX - descriptionHubX)) / 2.0f) * -1.0f, (((1.0f - MoreClickGUI.instance.descriptionModeHubBarIconBGScaleOutside.getValue()) * MoreClickGUI.instance.descriptionModeHubBarHeight.getValue()) / 2.0f) * -1.0f, 0.0f);
        }

        if (MoreClickGUI.instance.descriptionModeHubHeaderText.getValue())
        {
            GL11.glTranslatef((descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue()) * (1.0f - MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue()), (descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0)) * (1.0f - MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue()), 0.0f);
            GL11.glScalef(MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue(), MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue(), 1.0f);

            int desHubHeaderTextColor = MoreClickGUI.instance.descriptionModeHubHeaderTextColor.getValue().getColor();
            if (MoreClickGUI.instance.descriptionModeHubDesTextFont.getValue() == MoreClickGUI.DescriptionModeHubDesTextFont.Minecraft)
            {
                if (MoreClickGUI.instance.descriptionModeHubBarTextShadow.getValue())
                {
                    RenderUtils2D.drawBetterRoundRectFade((float)(descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubBarTextShadowWidth.getValue() / 2.0)), (float)(descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0) - (MoreClickGUI.instance.descriptionModeHubBarTextShadowHeight.getValue() / 2.0)), (float)(descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue() + mc.fontRenderer.getStringWidth("Description") + (MoreClickGUI.instance.descriptionModeHubBarTextShadowWidth.getValue() / 2.0)), (float)(descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0) + (MoreClickGUI.instance.descriptionModeHubBarTextShadowHeight.getValue() / 2.0)), MoreClickGUI.instance.descriptionModeHubBarTextShadowSize.getValue(), 70.0f, false, true, false, new Color(0, 0, 0, MoreClickGUI.instance.descriptionModeHubBarTextShadowAlpha.getValue()).getRGB());
                }

                Minecraft.getMinecraft().fontRenderer.drawString("Description", descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue(), descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), desHubHeaderTextColor, MoreClickGUI.instance.descriptionModeHubDesTextShadow.getValue());
            }
            else
            {
                if (MoreClickGUI.instance.descriptionModeHubBarTextShadow.getValue())
                {
                    RenderUtils2D.drawBetterRoundRectFade((float)(descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubBarTextShadowWidth.getValue() / 2.0)), (float)(descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0) - (MoreClickGUI.instance.descriptionModeHubBarTextShadowHeight.getValue() / 2.0)), (float)(descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue() + FontManager.fontRenderer.getStringWidth("Description") + (MoreClickGUI.instance.descriptionModeHubBarTextShadowWidth.getValue() / 2.0)), (float)(descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0) + (MoreClickGUI.instance.descriptionModeHubBarTextShadowHeight.getValue() / 2.0)), MoreClickGUI.instance.descriptionModeHubBarTextShadowSize.getValue(), 70.0f, false, true, false, new Color(0, 0, 0, MoreClickGUI.instance.descriptionModeHubBarTextShadowAlpha.getValue()).getRGB());
                }

                if (MoreClickGUI.instance.descriptionModeHubDesTextShadow.getValue())
                    FontManager.drawHeaderTextShadow("Description", descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue(), descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), desHubHeaderTextColor);
                else
                    FontManager.drawHeaderText("Description", descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue(), descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0), desHubHeaderTextColor);
            }

            GL11.glScalef(1.0f / MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue(), 1.0f / MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue(), 1.0f);
            GL11.glTranslatef((descriptionHubX + (FontManager.getIconWidth() * MoreClickGUI.instance.descriptionModeHubIconSize.getValue()) + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue() + MoreClickGUI.instance.descriptionModeHubHeaderTextXOffset.getValue()) * (1.0f - MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue()) * -1.0f, (descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? extraRectHeight : 0)) * (1.0f - MoreClickGUI.instance.descriptionModeHubHeaderTextSize.getValue()) * -1.0f, 0.0f);
        }

        if (MoreClickGUI.instance.descriptionModeHubIcon.getValue())
        {
            GL11.glTranslatef((descriptionHubX + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue()) * (1.0f - MoreClickGUI.instance.descriptionModeHubIconSize.getValue()), (descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubIconYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? (int)extraRectHeight : 0)) * (1.0f - MoreClickGUI.instance.descriptionModeHubIconSize.getValue()), 0.0f);
            GL11.glScalef(MoreClickGUI.instance.descriptionModeHubIconSize.getValue(), MoreClickGUI.instance.descriptionModeHubIconSize.getValue(), 1.0f);

            Color desHubIconColor = new Color(MoreClickGUI.instance.descriptionModeHubIconColor.getValue().getColorColor().getRed(), MoreClickGUI.instance.descriptionModeHubIconColor.getValue().getColorColor().getGreen(), MoreClickGUI.instance.descriptionModeHubIconColor.getValue().getColorColor().getBlue(), MoreClickGUI.instance.descriptionModeHubIconColor.getValue().getAlpha());
            FontManager.drawModuleMiniIcon("6", descriptionHubX + MoreClickGUI.instance.descriptionModeHubIconXOffset.getValue(), descriptionHubY + FontManager.getHeightDescriptionHubHeaderText() + MoreClickGUI.instance.descriptionModeHubHeaderTextYOffset.getValue() + MoreClickGUI.instance.descriptionModeHubIconYOffset.getValue() - (MoreClickGUI.instance.descriptionModeHubExpandUp.getValue() ? (int)extraRectHeight : 0), desHubIconColor);

            GL11.glScalef(1.0f / MoreClickGUI.instance.descriptionModeHubIconSize.getValue(), 1.0f / MoreClickGUI.instance.descriptionModeHubIconSize.getValue(), 1.0f);
            GL11.glScalef(MoreClickGUI.instance.descriptionModeHubIconSize.getValue() * -1.0f, MoreClickGUI.instance.descriptionModeHubIconSize.getValue() * -1.0f, 1.0f);
        }
    }
}
