package me.thediamondsword5.moloch.client;

import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.core.config.ListenableContainer;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.event.events.network.PacketEvent;

import java.util.Arrays;

public class ServerManager extends ListenableContainer {

    private static long prevTime;
    private static int currentTick;
    private static final float[] ticks = new float[20];

    public static void init() {
        prevTime = -1;
        Arrays.fill(ticks, 0.0f);
        BaseCenter.EVENT_BUS.register(new ServerManager());
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            if (prevTime != -1) {
                ticks[currentTick % ticks.length] = MathHelper.clamp((20.0f / ((float) (System.currentTimeMillis() - prevTime) / 1000.0f)), 0.0f, 20.0f);
                currentTick += 1.0f;
            }

            prevTime = System.currentTimeMillis();
        }
    }

    public static float getTPS() {
        int numTicks = 0;
        float tickRate = 0.0f;

        for (float tick : ticks) {
            if (tick > 0.0f) {
                tickRate += tick;
                numTicks += 1;
            }
        }

        return MathHelper.clamp((tickRate / numTicks), 0.0f, 20.0f);
    }

}
