package me.thediamondsword5.moloch.module.modules.client;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.ClickGUIFinal;
import net.spartanb312.base.gui.HUDEditorFinal;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.event.events.render.DrawScreenEvent;
import me.thediamondsword5.moloch.utils.graphics.ParticleUtil;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainer;

@Parallel
@ModuleInfo(name = "Particles", category = Category.CLIENT, description = "Display particles and lines on background")
public class Particles extends Module {

    public static Particles INSTANCE;

    public Setting<Boolean> particlesChatGUI = setting("ChatEffect", true).des("Render particles in chat");
    public Setting<Boolean> particlesOtherGUI = setting("GUIEffect", true).des("Render particles in GUIs");
    public Setting<Float> fadeInSpeed = setting("FadeInSpeed", 1.0f, 0.1f, 5.0f).des("Particles fade in speed");

    public Setting<Page> page = setting("Page", Page.Particles);
    public Setting<ParticlesPage> particlesPage = setting("ParticlesPage", ParticlesPage.Particles).whenAtMode(page, Page.Particles);

    public Setting<ParticlesSpawnMode> particlesSpawnMode = setting("ParticlesSpawnMode", ParticlesSpawnMode.Corners).des("Where to spawn particles").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Spawn);
    public Setting<ParticlesSpawnSideMode> particlesSpawnSideMode = setting("ParticlesSpawnSide", ParticlesSpawnSideMode.Both).des("What sides of screen particles spawn in").whenAtMode(particlesSpawnMode, ParticlesSpawnMode.Sides).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Spawn);
    public Setting<Boolean> particlesSpawnUpLeftCorner = setting("ParticlesSpawnUpLeft", true).des("Particles spawn in top left corner of screen").whenAtMode(particlesSpawnMode, ParticlesSpawnMode.Corners).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Spawn);
    public Setting<Boolean> particlesSpawnDownLeftCorner = setting("ParticlesSpawnDownLeft", true).des("Particles spawn in bottom left corner of screen").whenAtMode(particlesSpawnMode, ParticlesSpawnMode.Corners).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Spawn);
    public Setting<Boolean> particlesSpawnUpRightCorner = setting("ParticlesSpawnUpRight", true).des("Particles spawn in top right corner of screen").whenAtMode(particlesSpawnMode, ParticlesSpawnMode.Corners).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Spawn);
    public Setting<Boolean> particlesSpawnDownRightCorner = setting("ParticlesSpawnDownRight", true).des("Particles spawn in bottom right corner of screen").whenAtMode(particlesSpawnMode, ParticlesSpawnMode.Corners).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Spawn);

    public Setting<ParticlesShape> particlesShape = setting("ParticlesShape", ParticlesShape.Triangle).des("Shape of particles").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> maxParticleSpinSpeed = setting("MaxSpinSpeed", 3.5f, 0.0f, 5.0f).des("Max particles spin speed").only(v -> particlesShape.getValue() != ParticlesShape.Circle).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> minParticleSpinSpeed = setting("MinSpinSpeed", 1.0f, 0.0f, 5.0f).des("Min particles spin speed").only(v -> particlesShape.getValue() != ParticlesShape.Circle).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Boolean> particleSpeedAlpha = setting("SpeedAlpha", true).des("Lowers alpha the faster a particle is going").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> particlesSpeedAlphaFactor = setting("SpeedAlphaFactor", 1.0f, 1.0f, 2.0f).des("Multiply final speed alpha to make alpha difference more noticeable").whenTrue(particleSpeedAlpha).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Boolean> randomParticleSize = setting("RandomSize", true).des("Randomizes particle size").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> particleSize = setting("Size", 5.0f, 0.0f, 5.0f).des("Particle size").whenFalse(randomParticleSize).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> maxParticleSize = setting("MaxSize", 3.0f, 0.0f, 5.0f).des("Max particle random size").whenTrue(randomParticleSize).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> minParticleSize = setting("MinSize", 2.0f, 0.0f, 5.0f).des("Min particle random size").whenTrue(randomParticleSize).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> maxParticleSpeed = setting("MaxSpeed", 1.0f, 0.0f, 5.0f).des("Max particles speed").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> minParticleSpeed = setting("MinSpeed", 0.2f, 0.0f, 5.0f).des("Min particles speed").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Integer> particleAmount = setting("Amount", 75, 0, 400).des("Amount of particles").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Boolean> particleRollColor = setting("ParticleRollColor", true).des("Particles will roll between 2 colors").whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Color> particleRollColor1 = setting("ParticleRollColor1", new Color(new java.awt.Color(81, 43, 170, 204).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 81, 43, 170, 204)).whenTrue(particleRollColor).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Color> particleRollColor2 = setting("ParticleRollColor2", new Color(new java.awt.Color(216, 180, 255, 204).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 216, 180, 255, 204)).whenTrue(particleRollColor).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> particleRollColorSpeed = setting("ParticleRollColorSpeed", 0.5f, 0.1f, 3.0f).whenTrue(particleRollColor).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> particleRollColorSize = setting("ParticleRollColorSize", 1.5f, 0.1f, 2.0f).whenTrue(particleRollColor).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Color> particleColor = setting("ParticleColor", new Color(new java.awt.Color(255, 255, 255, 199).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 199)).des("Particle Color").whenFalse(particleRollColor).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Boolean> particleRainbowRoll = setting("ParticleRainbowRoll", true).des("Rolling particles rainbow").whenFalse(particleRollColor).only(v -> particleColor.getValue().getRainbow()).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);
    public Setting<Float> particleRainbowRollSize = setting("PRainbowRollSize", 1.0f, 0.1f, 2.0f).des("Size of particle rainbow roll").whenTrue(particleRainbowRoll).whenFalse(particleRollColor).only(v -> particleColor.getValue().getRainbow()).whenAtMode(page, Page.Particles).whenAtMode(particlesPage, ParticlesPage.Particles);

    public Setting<Integer> connectRange = setting("LineRange", 92, 0, 700).des("Range when particles will connect").whenAtMode(page, Page.Lines);
    public Setting<Boolean> onlyConnectOne = setting("ConnectOne", false).des("One line to closest point").whenAtMode(page, Page.Lines);
    public Setting<Boolean> restrictToAroundMouseLines = setting("LinesAroundMouse", false).des("Only lines around mouse").whenAtMode(page, Page.Lines);
    public Setting<Float> restrictToAroundMouseLinesRange = setting("LinesAroundMouseRange", 114.0f, 1.0f, 500.0f).des("Distance from mouse to connect lines with particles").whenTrue(restrictToAroundMouseLines).whenAtMode(page, Page.Lines);
    public Setting<Boolean> linesFadeIn = setting("LinesFadeIn", true).des("Fade in lines as points get closer").whenAtMode(page, Page.Lines);
    public Setting<Float> linesFadeInFactor = setting("LinesFadeInFactor", 1.0f, 0.1f, 1.0f).des("Fraction of range to stop fading in").whenTrue(linesFadeIn).whenAtMode(page, Page.Lines);
    public Setting<Float> linesWidth = setting("LinesWidth", 1.5f, 1.0f, 3.0f).des("Lines Width").whenAtMode(page, Page.Lines);
    public Setting<Boolean> lineRollColor = setting("LineRollColor", true).des("Lines will roll between 2 colors").whenAtMode(page, Page.Lines);
    public Setting<Color> lineRollColor1 = setting("LineRollColor1", new Color(new java.awt.Color(81, 43, 170, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 81, 43, 170, 40)).whenTrue(lineRollColor).whenAtMode(page, Page.Lines);
    public Setting<Color> lineRollColor2 = setting("LineRollColor2", new Color(new java.awt.Color(216, 180, 255, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 216, 180, 255, 40)).whenTrue(lineRollColor).whenAtMode(page, Page.Lines);
    public Setting<Float> lineRollColorSpeed = setting("LineRollColorSpeed", 0.5f, 0.1f, 3.0f).whenTrue(lineRollColor).whenAtMode(page, Page.Lines);
    public Setting<Float> lineRollColorSize = setting("LineRollColorSize", 1.4f, 0.1f, 2.0f).whenTrue(lineRollColor).whenAtMode(page, Page.Lines);
    public Setting<Color> lineColor = setting("LineColor", new Color(new java.awt.Color(255, 255, 255, 40).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 255, 255, 40)).des("Lines Color").whenFalse(lineRollColor).whenAtMode(page, Page.Lines);
    public Setting<Boolean> lineRainbowRoll = setting("LineRainbowRoll", true).des("Rolling lines rainbow").whenFalse(lineRollColor).only(v -> lineColor.getValue().getRainbow()).whenAtMode(page, Page.Lines);
    public Setting<Float> lineRainbowRollSize = setting("LRainbowRollSize", 1.0f, 0.1f, 2.0f).des("Size of line rainbow roll").whenTrue(lineRainbowRoll).whenFalse(lineRollColor).only(v -> lineColor.getValue().getRainbow()).whenAtMode(page, Page.Lines);

    public Setting<Boolean> mouseInteract = setting("MouseInteract", true).des("Do stuff with particles when mouse gets near them").whenAtMode(page, Page.MouseInteract);
    public Setting<Boolean> mouseInteractBounce = setting("MouseInteractBounce", true).des("Bounce particles off of mouse range").whenTrue(mouseInteract).whenAtMode(page, Page.MouseInteract);
    public Setting<Float> mouseInteractPlowStrength = setting("ParticlePlowStrength", 3.0f, 0.1f, 3.0f).whenTrue(mouseInteract).whenAtMode(page, Page.MouseInteract);
    public Setting<Boolean> mouseInterectPlowSpeedReduce = setting("ParticleSpeedReduce", true).des("Particles slow down on nearing mouse").whenTrue(mouseInteract).whenAtMode(page, Page.MouseInteract);
    public Setting<Float> mouseInteractPlowFractionOfSpeed = setting("ParticleFractionOfSpeed", 0.3f, 0.1f, 1.0f).whenTrue(mouseInterectPlowSpeedReduce).whenTrue(mouseInteract).whenAtMode(page, Page.MouseInteract);
    public Setting<Float> mouseInteractPlowSpeedRegenFactor = setting("ParticlesSpeedRegenFactor", 0.5f, 0.1f, 3.0f).whenTrue(mouseInterectPlowSpeedReduce).whenTrue(mouseInteract).whenAtMode(page, Page.MouseInteract);
    public Setting<Float> mouseInteractRange = setting("MouseInteractRange", 66.9f, 1.0f, 500.0f).des("Distance from mouse for particles to start interacting with mouse").whenTrue(mouseInteract).whenAtMode(page, Page.MouseInteract);

    public Particles() {
        INSTANCE = this;
    }

    @Override
    public void onRenderTick() {
        doParticles(true);
    }

    @Listener
    public void onDrawScreenChat(DrawScreenEvent.Layer2 event) {
        doParticles(false);
    }

    private void doParticles(boolean chatMode) {
        if (!(mc.currentScreen instanceof ClickGUIFinal || mc.currentScreen instanceof HUDEditorFinal)) {
            if ((isOtherGUIOpen() && !mc.ingameGUI.getChatGUI().getChatOpen() && particlesOtherGUI.getValue() && !chatMode) || (chatMode && particlesChatGUI.getValue() && mc.ingameGUI.getChatGUI().getChatOpen())) {
                ParticleUtil.render();
            }

            if (!isOtherGUIOpen() && !mc.ingameGUI.getChatGUI().getChatOpen() && !ParticleUtil.particlesClearedFlag) {
                ParticleUtil.clearParticles();
            }
        }
    }

    public static boolean isOtherGUIOpen() {
        return mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu;
    }

    enum Page {
        Particles,
        Lines,
        MouseInteract
    }

    enum ParticlesPage {
        Particles,
        Spawn
    }

    public enum ParticlesShape {
        Circle,
        Triangle,
        Square
    }

    public enum ParticlesSpawnMode {
        Sides,
        Corners
    }

    public enum ParticlesSpawnSideMode {
        Vertical,
        Horizontal,
        Both,
        None
    }
}
