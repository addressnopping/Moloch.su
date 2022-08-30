package net.spartanb312.base.mixin.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.util.ITickable;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.event.decentraliized.DecentralizedPacketEvent;
import net.spartanb312.base.event.events.network.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.utils.ItemUtils.mc;

@Mixin(value = NetworkManager.class, priority = 312312)
public abstract class MixinNetWork {
    @Shadow
    protected abstract void flushOutboundQueue();

    @Shadow
    public INetHandler packetListener;

    @Shadow
    public Channel channel;

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void packetReceived(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        if (mc.player != null && mc.world != null) {
            final PacketEvent.Receive event = new PacketEvent.Receive(packet);
            DecentralizedPacketEvent.Receive.instance.post(event);
            BaseCenter.EVENT_BUS.post(event);
            if (event.isCancelled() && callbackInfo.isCancellable()) {
                callbackInfo.cancel();
            }
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet<?> packetIn, CallbackInfo callbackInfo) {
        if (mc.player != null && mc.world != null) {
            final PacketEvent.Send event = new PacketEvent.Send(packetIn);
            DecentralizedPacketEvent.Send.instance.post(event);
            BaseCenter.EVENT_BUS.post(event);
            if (event.isCancelled() && callbackInfo.isCancellable()) {
                callbackInfo.cancel();
            }
        }
    }

    //idk any non chinese way to fix guimultiplayer crashing randomly when trying to ping servers
    @Overwrite
    public void processReceivedPackets() {
        try {
            flushOutboundQueue();

            if (packetListener instanceof ITickable) {
                ((ITickable)packetListener).update();
            }

            if (channel != null) {
                channel.flush();
            }
        }
        catch (Exception ignored) {}
    }
}
