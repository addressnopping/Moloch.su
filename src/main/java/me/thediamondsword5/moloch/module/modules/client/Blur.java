package me.thediamondsword5.moloch.module.modules.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.RotationUtil;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.RenderUtils2D;
import me.thediamondsword5.moloch.event.events.render.DrawScreenEvent;
import org.lwjgl.opengl.GL11;

import static net.spartanb312.base.utils.ItemUtils.mc;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

@Parallel
@ModuleInfo(name = "Blur", category = Category.CLIENT, description = "!! TURN OFF FAST RENDER IN OPTIFINE !! Blurs background of GUI")
public class Blur extends Module {

    public static Blur INSTANCE;
    private final Timer timer = new Timer();
    public int blurThreader = 0;

    public Setting<Boolean> blurClickGUI = setting("BlurClickGUI", true);
    public Setting<Float> blurFactor = setting("BlurFactor", 1.0f, 0.0f, 2.0f).des("Blur Intensity");
    Setting<Boolean> blurChat = setting("BlurChat", false).des("Blurs chat background when chat is open");
    Setting<Boolean> blurOtherGUI = setting("BlurMiscGUI", false).des("Blur When Opened Inventory Or Containers");
    Setting<Float> blurGUISpeed = setting("BlurGUISpeed", 1.0f, 0.1f, 5.0f).des("GUI Blur Speed").whenTrue(blurOtherGUI);

    public Blur() {
        INSTANCE = this;
    }

    @Listener
    public void onDrawScreenOther(DrawScreenEvent.Layer1 event) {
        if (!mc.ingameGUI.getChatGUI().getChatOpen()) {
            if (Particles.isOtherGUIOpen() && blurOtherGUI.getValue()) {
                RenderUtils2D.drawBlurAreaPre(blurFactor.getValue() * blurThreader / 300.0f, mc.getRenderPartialTicks());
                RenderUtils2D.drawBlurRect(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), 0, 0, mc.displayWidth, mc.displayHeight);
                RenderUtils2D.drawBlurAreaPost();

                GL11.glEnable(GL_DEPTH_TEST);
                GL11.glEnable(GL_BLEND);
            }
        }
    }

    @Listener
    public void onDrawScreenChat(DrawScreenEvent.Chat event) {
        if (mc.ingameGUI.getChatGUI().getChatOpen() && blurChat.getValue()) {
            GL11.glPushMatrix();
            RenderUtils2D.drawBlurAreaPre(blurFactor.getValue() * blurThreader / 300.0f, mc.getRenderPartialTicks());
            RenderUtils2D.drawBlurRect(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), 0, 0, mc.displayWidth, mc.displayHeight);
            RenderUtils2D.drawBlurAreaPost();
            GL11.glPopMatrix();
            ScaledResolution scale = new ScaledResolution(mc);
            GL11.glScalef(scale.getScaleFactor(), scale.getScaleFactor(), 1.0f);
        }
    }

    @Override
    public void onTick() {
        if (!Particles.isOtherGUIOpen() && !mc.ingameGUI.getChatGUI().getChatOpen() && blurThreader != 0) {
            blurThreader = 0;
        }

        if ((mc.ingameGUI.getChatGUI().getChatOpen() && blurChat.getValue()) || (!mc.ingameGUI.getChatGUI().getChatOpen() && Particles.isOtherGUIOpen() && blurOtherGUI.getValue())) {
            int passedms = (int) timer.hasPassed();
            timer.reset();
            if (passedms < 1000) {
                blurThreader += (blurGUISpeed.getValue() / 10.0f) * passedms;
                if (blurThreader > 300)
                    blurThreader = 300;
                if (blurThreader < 0)
                    blurThreader = 0;
            }
        }
    }
}
