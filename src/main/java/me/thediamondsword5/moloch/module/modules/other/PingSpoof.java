package me.thediamondsword5.moloch.module.modules.other;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.RotationUtil;
import net.spartanb312.base.utils.Timer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Parallel
@ModuleInfo(name = "PingBypast", category = Category.OTHER, description = "Ping bypass??????!11!!1!!!1!! awwwwwwwa")
public class PingSpoof extends Module {

    Setting<Boolean> cancelTransactionPackets = setting("CancelTransactionPackets", false).des("Cancels CPacketConfirmTransaction because that bypasses some server's patches or smt");
    Setting<Integer> ping = setting("Ping", 666, 0, 2000).des("Ping to spoof in milliseconds");

    private boolean flag = true;
    private final Timer timer = new Timer();
    private final List<Packet<?>> packetList = new ArrayList<>();

    @Override
    public void onTick() {
        if (!mc.isSingleplayer() && timer.passed(ping.getValue())) {
            flag = false;
            packetList.stream()
                    .filter(Objects::nonNull)
                    .forEach(packet -> mc.player.connection.sendPacket(packet));
            flag = true;
            packetList.clear();
            timer.reset();
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if ((event.getPacket() instanceof CPacketKeepAlive || (cancelTransactionPackets.getValue() && event.getPacket() instanceof CPacketConfirmTransaction)) && flag && mc.player != null && !mc.isSingleplayer()) {
            packetList.add(event.getPacket());
            event.cancel();
        }
    }
}
