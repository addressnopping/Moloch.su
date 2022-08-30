package me.thediamondsword5.moloch.utils.graphics;

import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import me.thediamondsword5.moloch.module.modules.client.Particles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.util.*;
import java.util.List;

import static net.spartanb312.base.module.Module.mc;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class ParticleUtil
{
    private static final HashMap<Integer, Vector2f> particlesList = new HashMap<>();
    private static final HashMap<Integer, Vector2f> particlesSpeed = new HashMap<>();
    private static final HashMap<Integer, Float> particlesSize = new HashMap<>();
    private static final HashMap<Integer, Vector2f> particlesSpinSpeed = new HashMap<>();
    private static final HashMap<Integer, Float> particlesSpeedAlpha = new HashMap<>();
    private static final HashMap<Integer, Float> particlesSpeedFactor = new HashMap<>();
    private static final List<Vector2f> cornerList = new ArrayList<>();
    public static int particlesId = 0;
    public static boolean particlesClearedFlag = true;
    private static final Timer particlesTimer = new Timer();
    private static float alphaThreader = 0.0f;

    public static void render()
    {
        particlesClearedFlag = false;

        Color particleColor = Particles.INSTANCE.particleColor.getValue().getColorColor();

        if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Circle)
        {
            GL11.glPushMatrix();
            GL11.glEnable(GL_BLEND);
            GL11.glDisable(GL_TEXTURE_2D);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL_DEPTH_TEST);
            GL11.glDepthMask(false);
        }

        GlStateManager.disableAlpha();

        if (mc.currentScreen == null)
            return;

        if (particlesId >= Integer.MAX_VALUE - 4000)
            particlesId = 0;

        if (Particles.INSTANCE.particleAmount.getValue() - particlesList.size() > 0)
            genParticles();

        if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Circle)
        {
            GL11.glEnable(GL_POINT_SMOOTH);
            GL11.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        }

        if (Particles.INSTANCE.particlesShape.getValue() != Particles.ParticlesShape.Circle)
        {
            GL11.glPushMatrix();
            GL11.glEnable(GL_BLEND);
            GL11.glDisable(GL_TEXTURE_2D);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        }

        for (Map.Entry<Integer, Vector2f> entry : new HashMap<>(particlesList).entrySet())
        {
            if (Particles.INSTANCE.mouseInteract.getValue() && Particles.INSTANCE.mouseInteractBounce.getValue())
            {
                Vector2f inVec = new Vector2f(particlesSpeed.get(entry.getKey()).x, particlesSpeed.get(entry.getKey()).y);
                Vector2f normalVec = new Vector2f(entry.getValue().x - (Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth), entry.getValue().y - (Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1));

                if (MathUtilFuckYou.dotProduct(inVec, normalVec) < 0 && getParticleDist(new Vector2f(Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth, Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1), entry.getValue()) < Particles.INSTANCE.mouseInteractRange.getValue())
                {
                    Vector2f reflectVec = MathUtilFuckYou.reflectVector2f(inVec, normalVec);
                    particlesSpeed.put(entry.getKey(), new Vector2f(reflectVec.x, reflectVec.y));
                }
            }

            if (Particles.INSTANCE.mouseInteract.getValue() && Particles.INSTANCE.mouseInterectPlowSpeedReduce.getValue() && getParticleDist(new Vector2f(Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth, Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1), entry.getValue()) < Particles.INSTANCE.mouseInteractRange.getValue())
            {
                particlesSpeedFactor.put(entry.getKey(), Particles.INSTANCE.mouseInteractPlowFractionOfSpeed.getValue());
            }

            drawLines(entry, (1.0f / 300.0f) * alphaThreader);
        }

        if (Particles.INSTANCE.particlesShape.getValue() != Particles.ParticlesShape.Circle)
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);
            GL11.glEnable(GL_CULL_FACE);
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glEnable(GL_DEPTH_TEST);
            GlStateManager.enableAlpha();
            GL11.glPopMatrix();
        }

        for (Map.Entry<Integer, Vector2f> entry : new HashMap<>(particlesList).entrySet())
        {
            float alpha = (Particles.INSTANCE.particleColor.getValue().getAlpha() / 300.0f) * alphaThreader;

            if (Particles.INSTANCE.particleRainbowRoll.getValue() && !Particles.INSTANCE.particleRollColor.getValue() && Particles.INSTANCE.particleColor.getValue().getRainbow())
            {
                particleColor = new Color(ColorUtil.rainbow((int)(entry.getValue().x), Particles.INSTANCE.particleColor.getValue().getRainbowSpeed(), Particles.INSTANCE.particleRainbowRollSize.getValue() / 10.0f, Particles.INSTANCE.particleColor.getValue().getRainbowSaturation(), Particles.INSTANCE.particleColor.getValue().getRainbowBrightness()));
            }

            if (Particles.INSTANCE.particleRollColor.getValue())
            {
                particleColor = ColorUtil.rolledColor(Particles.INSTANCE.particleRollColor1.getValue().getColorColor(), Particles.INSTANCE.particleRollColor2.getValue().getColorColor(), (int)entry.getValue().x, Particles.INSTANCE.particleRollColorSpeed.getValue(), Particles.INSTANCE.particleRollColorSize.getValue() / 10.0f);
                alpha = MathUtilFuckYou.rolledLinearInterp((int)((Particles.INSTANCE.particleRollColor1.getValue().getAlpha() / 300.0f) * alphaThreader), (int)((Particles.INSTANCE.particleRollColor2.getValue().getAlpha() / 300.0f) * alphaThreader), (int)entry.getValue().x, Particles.INSTANCE.particleRollColorSpeed.getValue(), Particles.INSTANCE.particleRollColorSize.getValue() / 10.0f);
            }

            if (Particles.INSTANCE.particleSpeedAlpha.getValue())
            {
                alpha *= particlesSpeedAlpha.get(entry.getKey());
            }

            if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Circle)
            {
                GL11.glColor4f(particleColor.getRed() / 255.0f, particleColor.getGreen() / 255.0f , particleColor.getBlue() / 255.0f, alpha / 255.0f);
                GL11.glPointSize(particlesSize.get(entry.getKey()) * 3.0f);
                GL11.glBegin(GL_POINTS);
                GL11.glVertex2f(entry.getValue().x, entry.getValue().y);
                GL11.glEnd();
            }
            else
            {
                RenderUtils2D.prepareGl();
                GL11.glTranslatef(entry.getValue().x, entry.getValue().y, 0.0f);
                GL11.glRotatef(particlesSpinSpeed.get(entry.getKey()).y, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(entry.getValue().x * -1.0f, entry.getValue().y * -1.0f, 0.0f);

                if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Triangle)
                    RenderUtils2D.drawEquilateralTriangle(entry.getValue().x, entry.getValue().y, false, particlesSize.get(entry.getKey()), new Color(particleColor.getRed(), particleColor.getGreen(), particleColor.getBlue(), (int)alpha).getRGB());

                if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Square)
                    RenderUtils2D.drawRhombus(entry.getValue().x, entry.getValue().y, particlesSize.get(entry.getKey()), new Color(particleColor.getRed(), particleColor.getGreen(), particleColor.getBlue(), (int)alpha).getRGB());

                GL11.glTranslatef(entry.getValue().x, entry.getValue().y, 0.0f);
                GL11.glRotatef(particlesSpinSpeed.get(entry.getKey()).y * -1.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(entry.getValue().x * -1.0f, entry.getValue().y * -1.0f, 0.0f);
                RenderUtils2D.releaseGl();
            }

            if (entry.getValue().x < 0 || entry.getValue().x > mc.currentScreen.width || entry.getValue().y < 0 || entry.getValue().y > mc.currentScreen.height)
            {
                particlesList.remove(entry.getKey());
                particlesSpeed.remove(entry.getKey());
                particlesSize.remove(entry.getKey());
                particlesSpinSpeed.remove(entry.getKey());
                particlesSpeedAlpha.remove(entry.getKey());
                particlesSpeedFactor.remove(entry.getKey());
            }
        }

        if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Circle)
            GL11.glDisable(GL_POINT_SMOOTH);

        //better lag free animation
        int passedms = (int) particlesTimer.hasPassed();
        if (passedms < 1000)
        {
            for (int i = 0; i <= passedms; i++)
            {
                alphaThreader += Particles.INSTANCE.fadeInSpeed.getValue() / 10.0f;

                if (alphaThreader >= 300)
                    alphaThreader = 300;

                for (Map.Entry<Integer, Vector2f> entry : new HashMap<>(particlesList).entrySet())
                {
                    particlesSpinSpeed.get(entry.getKey()).y += (particlesSpinSpeed.get(entry.getKey()).x / 10.0f);

                    if (Particles.INSTANCE.mouseInteract.getValue())
                    {
                        if (getParticleDist(new Vector2f(Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth, Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1), entry.getValue()) < Particles.INSTANCE.mouseInteractRange.getValue())
                        {
                            entry.getValue().x += (Particles.INSTANCE.mouseInteractPlowStrength.getValue() / 10.0f) * (entry.getValue().x - (Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth) < 0 ? -1.0f : 1.0f);
                            entry.getValue().y += (Particles.INSTANCE.mouseInteractPlowStrength.getValue() / 10.0f) * (entry.getValue().y - (Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1) < 0 ? -1.0f : 1.0f);
                        }

                        if (Particles.INSTANCE.mouseInterectPlowSpeedReduce.getValue())
                        {
                            float speedFactor = particlesSpeedFactor.get(entry.getKey());
                            entry.getValue().x += (particlesSpeed.get(entry.getKey()).x * speedFactor) / 10.0f;
                            entry.getValue().y += (particlesSpeed.get(entry.getKey()).y * speedFactor) / 10.0f;
                            speedFactor += 0.001f * Particles.INSTANCE.mouseInteractPlowSpeedRegenFactor.getValue();

                            if (speedFactor >= 1.0f)
                            {
                                speedFactor = 1.0f;
                            }

                            particlesSpeedFactor.put(entry.getKey(), speedFactor);
                        }
                        else
                        {
                            entry.getValue().x += (particlesSpeed.get(entry.getKey()).x) / 10.0f;
                            entry.getValue().y += (particlesSpeed.get(entry.getKey()).y) / 10.0f;
                        }
                    }
                    else
                    {
                        entry.getValue().x += (particlesSpeed.get(entry.getKey()).x) / 10.0f;
                        entry.getValue().y += (particlesSpeed.get(entry.getKey()).y) / 10.0f;
                    }
                }
            }
        }
        particlesTimer.reset();

        if (Particles.INSTANCE.particlesShape.getValue() == Particles.ParticlesShape.Circle)
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);
            GL11.glDisable(GL_CULL_FACE);
            GL11.glDisable(GL_TEXTURE_2D);
            GL11.glDisable(GL_DEPTH_TEST);
            GlStateManager.enableAlpha();
            GL11.glPopMatrix();
        }
    }

    private static void genParticles()
    {
        if (mc.currentScreen != null)
        {
            for (int i = 0; i < Particles.INSTANCE.particleAmount.getValue() - particlesList.size(); ++i)
            {
                Vector2f spawnPoint = new Vector2f(0, 0);
                Vector4f speed = new Vector4f(0, 0, 0, 0);
                float size;
                float alpha = 1.0f;

                float spinSpeed = (float)(Math.random() * Particles.INSTANCE.maxParticleSpinSpeed.getValue());
                if (spinSpeed <= Particles.INSTANCE.minParticleSpinSpeed.getValue())
                {
                    spinSpeed = Particles.INSTANCE.minParticleSpinSpeed.getValue();
                }

                speed.x = (float)(Math.random() * Particles.INSTANCE.maxParticleSpeed.getValue());
                speed.y = (float)(Math.random() * Particles.INSTANCE.maxParticleSpeed.getValue());
                if (speed.x <= Particles.INSTANCE.minParticleSpeed.getValue())
                {
                    speed.x = Particles.INSTANCE.minParticleSpeed.getValue();
                }

                if (speed.y <= Particles.INSTANCE.minParticleSpeed.getValue())
                {
                    speed.y = Particles.INSTANCE.minParticleSpeed.getValue();
                }

                if (Particles.INSTANCE.particlesSpawnMode.getValue() == Particles.ParticlesSpawnMode.Sides)
                {
                    switch (Particles.INSTANCE.particlesSpawnSideMode.getValue())
                    {
                        case Horizontal:
                        {
                            speed.w = coinFlip() ? -1.0f : 1.0f;
                            if (coinFlip())
                            {
                                spawnPoint = new Vector2f(0, (float)(Math.random() * mc.currentScreen.height));
                                speed.z = 1.0f;
                            }
                            else
                            {
                                spawnPoint = new Vector2f(mc.currentScreen.width, (float)(Math.random() * mc.currentScreen.height));
                                speed.z = -1.0f;
                            }
                            break;
                        }

                        case Vertical:
                        {
                            speed.z = coinFlip() ? -1.0f : 1.0f;
                            if (coinFlip())
                            {
                                spawnPoint = new Vector2f((float)(Math.random() * mc.currentScreen.width), 0);
                                speed.w = 1.0f;
                            }
                            else
                            {
                                spawnPoint = new Vector2f((float)(Math.random() * mc.currentScreen.width), mc.currentScreen.height);
                                speed.w = -1.0f;
                            }
                            break;
                        }

                        case Both:
                        {
                            if (coinFlip())
                            {
                                speed.w = coinFlip() ? -1.0f : 1.0f;
                                if (coinFlip())
                                {
                                    spawnPoint = new Vector2f(0, (float)(Math.random() * mc.currentScreen.height));
                                    speed.z = 1.0f;
                                }
                                else
                                {
                                    spawnPoint = new Vector2f(mc.currentScreen.width, (float)(Math.random() * mc.currentScreen.height));
                                    speed.z = -1.0f;
                                }
                            }
                            else
                            {
                                speed.z = coinFlip() ? -1.0f : 1.0f;
                                if (coinFlip())
                                {
                                    spawnPoint = new Vector2f((float)(Math.random() * mc.currentScreen.width), 0);
                                    speed.w = 1.0f;
                                }
                                else
                                {
                                    spawnPoint = new Vector2f((float)(Math.random() * mc.currentScreen.width), mc.currentScreen.height);
                                    speed.w = -1.0f;
                                }
                            }

                            break;
                        }
                    }
                }
                else
                {
                    if (Particles.INSTANCE.particlesSpawnUpLeftCorner.getValue())
                        cornerList.add(new Vector2f(0, 0));

                    if (Particles.INSTANCE.particlesSpawnDownLeftCorner.getValue())
                        cornerList.add(new Vector2f(0, mc.currentScreen.height));

                    if (Particles.INSTANCE.particlesSpawnUpRightCorner.getValue())
                        cornerList.add(new Vector2f(mc.currentScreen.width, 0));

                    if (Particles.INSTANCE.particlesSpawnDownRightCorner.getValue())
                        cornerList.add(new Vector2f(mc.currentScreen.width, mc.currentScreen.height));

                    spawnPoint = cornerList.get((int)(Math.random() * cornerList.size()));

                    if (Objects.equals(spawnPoint, new Vector2f(0, 0)))
                    {
                        speed.z = 1.0f;
                        speed.w = 1.0f;
                    }

                    if (Objects.equals(spawnPoint, new Vector2f(0, mc.currentScreen.height)))
                    {
                        speed.z = 1.0f;
                        speed.w = -1.0f;
                    }

                    if (Objects.equals(spawnPoint, new Vector2f(mc.currentScreen.width, 0)))
                    {
                        speed.z = -1.0f;
                        speed.w = 1.0f;
                    }

                    if (Objects.equals(spawnPoint, new Vector2f(mc.currentScreen.width, mc.currentScreen.height)))
                    {
                        speed.z = -1.0f;
                        speed.w = -1.0f;
                    }

                    cornerList.clear();
                }


                if (Particles.INSTANCE.randomParticleSize.getValue())
                {
                    size = (float)(Math.random() * Particles.INSTANCE.maxParticleSize.getValue());
                    if (size <= Particles.INSTANCE.minParticleSize.getValue())
                    {
                        size = Particles.INSTANCE.minParticleSize.getValue();
                    }
                }
                else
                {
                    size = Particles.INSTANCE.particleSize.getValue();
                }

                if (Particles.INSTANCE.particleSpeedAlpha.getValue())
                {
                    alpha *= (Math.sqrt((speed.x * speed.x) + (speed.y * speed.y)) - Math.sqrt(Particles.INSTANCE.minParticleSpeed.getValue() * Particles.INSTANCE.minParticleSpeed.getValue() * 2.0f)) / (Math.sqrt(Particles.INSTANCE.maxParticleSpeed.getValue() * Particles.INSTANCE.maxParticleSpeed.getValue() * 2.0f) - Math.sqrt(Particles.INSTANCE.minParticleSpeed.getValue() * Particles.INSTANCE.minParticleSpeed.getValue() * 2.0f));

                    if (alpha >= 0.5f)
                    {
                        alpha *= Particles.INSTANCE.particlesSpeedAlphaFactor.getValue();
                    }
                    else
                    {
                        alpha /= Particles.INSTANCE.particlesSpeedAlphaFactor.getValue();
                    }

                    if (alpha >= 1.0f)
                        alpha = 1.0f;
                }

                particlesList.put(particlesId, spawnPoint);
                particlesSpeed.put(particlesId, new Vector2f(speed.x * speed.z, speed.y * speed.w));
                particlesSize.put(particlesId, size);
                particlesSpinSpeed.put(particlesId, new Vector2f(spinSpeed, (float)(Math.random() * 360.0f)));
                particlesSpeedAlpha.put(particlesId, alpha);
                particlesSpeedFactor.put(particlesId, 1.0f);

                particlesId += 1;
            }
        }
    }

    private static void drawLines(Map.Entry<Integer, Vector2f> particle, float alphaFactor)
    {
        Color lineColor = Particles.INSTANCE.lineColor.getValue().getColorColor();

        //dumbass code will draw line twice but its more trouble fixing than its worth
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth(Particles.INSTANCE.linesWidth.getValue());
        for (Map.Entry<Integer, Vector2f> entry : new HashMap<>(particlesList).entrySet())
        {
            int alpha = Particles.INSTANCE.lineColor.getValue().getAlpha();

            if (Objects.equals(entry.getKey(), particle.getKey()))
                continue;

            if (Particles.INSTANCE.restrictToAroundMouseLines.getValue() && getParticleDist(new Vector2f(Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth, Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1), entry.getValue()) > Particles.INSTANCE.restrictToAroundMouseLinesRange.getValue())
                continue;

            if (getParticleDist(particle.getValue(), entry.getValue()) <= Particles.INSTANCE.connectRange.getValue())
            {
                if (Particles.INSTANCE.lineRainbowRoll.getValue() && !Particles.INSTANCE.lineRollColor.getValue() && Particles.INSTANCE.lineColor.getValue().getRainbow())
                {
                    lineColor = new Color(ColorUtil.rainbow((int)(entry.getValue().x), Particles.INSTANCE.lineColor.getValue().getRainbowSpeed(), Particles.INSTANCE.lineRainbowRollSize.getValue() / 10.0f, Particles.INSTANCE.lineColor.getValue().getRainbowSaturation(), Particles.INSTANCE.lineColor.getValue().getRainbowBrightness()));
                }

                if (Particles.INSTANCE.lineRollColor.getValue())
                {
                    lineColor = ColorUtil.rolledColor(Particles.INSTANCE.lineRollColor1.getValue().getColorColor(), Particles.INSTANCE.lineRollColor2.getValue().getColorColor(), (int)entry.getValue().x, Particles.INSTANCE.lineRollColorSpeed.getValue(), Particles.INSTANCE.lineRollColorSize.getValue() / 10.0f);
                    alpha = (int)MathUtilFuckYou.rolledLinearInterp(Particles.INSTANCE.lineRollColor1.getValue().getAlpha(), Particles.INSTANCE.lineRollColor2.getValue().getAlpha(), (int)entry.getValue().x, Particles.INSTANCE.lineRollColorSpeed.getValue(), Particles.INSTANCE.lineRollColorSize.getValue() / 10.0f);
                }

                GL11.glColor4f(lineColor.getRed() / 255.0f, lineColor.getGreen() / 255.0f, lineColor.getBlue() / 255.0f, (alpha / 255.0f) * alphaFactor * (Particles.INSTANCE.linesFadeIn.getValue() ? ((Particles.INSTANCE.connectRange.getValue() - getParticleDist(particle.getValue(), entry.getValue())) / (Particles.INSTANCE.connectRange.getValue() / Particles.INSTANCE.linesFadeInFactor.getValue())) : 1.0f));

                if (Particles.INSTANCE.onlyConnectOne.getValue())
                {
                    GL11.glBegin(GL_LINES);
                    GL11.glVertex2f(particle.getValue().x, particle.getValue().y);
                    GL11.glVertex2f(entry.getValue().x, entry.getValue().y);
                    GL11.glEnd();
                    break;
                }
                else
                {
                    GL11.glBegin(GL_LINES);
                    GL11.glVertex2f(particle.getValue().x, particle.getValue().y);
                    GL11.glVertex2f(entry.getValue().x, entry.getValue().y);
                    GL11.glEnd();
                }
            }
        }

        GL11.glDisable(GL_LINE_SMOOTH);
    }

    private static float getParticleDist(Vector2f particle1, Vector2f particle2)
    {
        float x = particle1.x - particle2.x;
        float y = particle1.y - particle2.y;
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public static void clearParticles()
    {
        particlesList.clear();
        particlesSpeed.clear();
        particlesSize.clear();
        particlesSpinSpeed.clear();
        particlesSpeedAlpha.clear();
        particlesSpeedFactor.clear();
        particlesId = 0;
        alphaThreader = 0;
        particlesClearedFlag = true;
    }

    private static boolean coinFlip()
    {
        return Math.random() > 0.5f;
    }
}
