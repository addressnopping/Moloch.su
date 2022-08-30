package me.thediamondsword5.moloch.hud.huds;

import me.thediamondsword5.moloch.core.common.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.engine.AsyncRenderer;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.utils.ColorUtil;
import net.spartanb312.base.utils.Timer;

import java.net.InetSocketAddress;
import java.net.Socket;

@ModuleInfo(name = "LagNotify", category = Category.HUD, description = "Alerts you when your internet is down or when the server is lagging")
public class LagNotify extends HUDModule {

    Setting<Integer> timeout = setting("Timeout", 500, 1, 1500).des("Amount of milliseconds passed from last packet sent before you are considered to be lagging");
    Setting<Integer> checkInternetDelay = setting("CheckInternetDelay", 250, 1, 2000).des("Amount of milliseconds delay between every attempt to check if internet is out");
    Setting<Boolean> rubberband = setting("Rubberband", false).des("Detects if you have recently rubberbanded");
    Setting<Integer> rubberbandTimeout = setting("RubberbandTimeout", 1500, 1, 10000).des("Amount of milliseconds passed from last rubberband to stop showing warning").whenTrue(rubberband);
    Setting<Boolean> textShadow = setting("TextShadow", true).des("Draws shadow under text");
    Setting<Boolean> fade = setting("Fade", true).des("Warnings fade in and out");
    Setting<Float> fadeInSpeed = setting("FadeInSpeed", 1.5f, 0.1f, 3.0f).des("Fade speed when rendering warning in").whenTrue(fade);
    Setting<Float> fadeOutSpeed = setting("FadeOutSpeed", 0.7f, 0.1f, 3.0f).des("Fade speed on stopping rendering waring").whenTrue(fade);
    Setting<Color> color = setting("Color", new Color(new java.awt.Color(255, 100, 100, 255).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 100, 100, 255));

    private final Timer internetTimer = new Timer();
    private final Timer packetTimer = new Timer();
    private final Timer rubberbandTimer = new Timer();
    private final Timer alphaTimer = new Timer();
    private float alphaFactor = 5.0f;
    private boolean isInternetDown = false;
    private String lagStr = "";

    public LagNotify() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                width = FontManager.getWidthHUD("Server hasn't responded for 9999999 ms");
                height = FontManager.getHeight();

                if ((fade.getValue() && alphaFactor > 5.0f) || isInternetDown || packetTimer.passed(timeout.getValue()) || (rubberband.getValue() && !rubberbandTimer.passed(rubberbandTimeout.getValue()))) {
                    drawAsyncCenteredString(lagStr, x + (width * 0.5f), y, new java.awt.Color(color.getValue().getColorColor().getRed(), color.getValue().getColorColor().getGreen(), color.getValue().getColorColor().getBlue(), (int)(color.getValue().getAlpha() * alphaFactor / 300.0f)).getRGB(), textShadow.getValue());
                }

                if ((!fade.getValue() || alphaFactor > 5.0f) && isInternetDown) {
                    java.awt.Color color1 = new java.awt.Color(color.getValue().getColorColor().getRed(), color.getValue().getColorColor().getGreen(), color.getValue().getColorColor().getBlue(), (int)(color.getValue().getAlpha() * alphaFactor / 300.0f));
                    java.awt.Color color2 = ColorUtil.colorHSBChange(color1, 0.5f, ColorUtil.ColorHSBMode.Brightness);
                    java.awt.Color color3 = ColorUtil.colorShift(color1, color2, 150.0f + (float) (150.0f * Math.sin(((System.currentTimeMillis() / 2.0) % 300.0) * (Math.PI / 150.0))));

                    drawAsyncIcon("*", x + (width * 0.5f) - (FontManager.getWidthHUD(lagStr) * 0.5f) - FontManager.iconFont.getStringWidth("*") - 5.0f, y, new java.awt.Color(color3.getRed(), color3.getGreen(), color3.getBlue(), (int)(color.getValue().getAlpha() * alphaFactor/ 300.0f)).getRGB());
                }
            }
        };
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        packetTimer.reset();

        //from trollheck
        if (rubberband.getValue() && event.getPacket() instanceof SPacketPlayerPosLook && mc.player.ticksExisted >= 20.0f) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            double rubberbandDist = new Vec3d(packet.x, packet.y, packet.z).subtract(mc.player.getPositionVector()).length();
            Vec2f rubberbandRotate = new Vec2f(packet.yaw - mc.player.rotationYaw, packet.pitch - mc.player.rotationPitch);

            if (rubberbandDist > 0.5 || (Math.sqrt(rubberbandRotate.x * rubberbandRotate.x + rubberbandRotate.y * rubberbandRotate.y) > 1.0)) {
                rubberbandTimer.reset();
            }
        }
    }

    @Override
    public void onTick() {
        if (fade.getValue()) {
            int passedms = (int) alphaTimer.hasPassed();
            alphaTimer.reset();
            if (passedms < 1000) {
                if (isInternetDown || packetTimer.passed(timeout.getValue() ) || (rubberband.getValue() && !rubberbandTimer.passed(rubberbandTimeout.getValue()))) {
                    alphaFactor += passedms * fadeInSpeed.getValue();
                    if (alphaFactor > 300.0f) {
                        alphaFactor = 300.0f;
                    }
                }
                else {
                    alphaFactor -= passedms * fadeOutSpeed.getValue();
                    if (alphaFactor < 5.0f) {
                        alphaFactor = 5.0f;
                    }
                }
            }
        }

        if (internetTimer.passed(checkInternetDelay.getValue())) {
            updateInternet();
            internetTimer.reset();
        }

        if (rubberband.getValue() && !rubberbandTimer.passed(rubberbandTimeout.getValue())) {
            lagStr = "Rubberbanded " + rubberbandTimer.hasPassed() + " ms ago";
        }

        if (isInternetDown) {
            lagStr = "Ur internet's fucked";
        }
        else if (packetTimer.passed(timeout.getValue())) {
            lagStr = "Server hasn't responded for " + packetTimer.hasPassed() + " ms";
        }
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

    private void updateInternet() {
        isInternetDown = true;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("1.1.1.1", 80), 300);
            socket.close();
            isInternetDown = false;
        } catch (Exception ignored) {}
    }
}
