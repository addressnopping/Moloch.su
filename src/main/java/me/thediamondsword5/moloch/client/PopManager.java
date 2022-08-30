package me.thediamondsword5.moloch.client;

import me.thediamondsword5.moloch.event.events.entity.DeathEvent;
import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import me.thediamondsword5.moloch.module.modules.visuals.Nametags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.core.config.ListenableContainer;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.utils.ChatUtil;

import java.util.HashMap;

import static net.spartanb312.base.utils.ItemUtils.mc;

public class PopManager extends ListenableContainer {
    public static final HashMap<Entity, Integer> popMap = new HashMap<>();
    public static final HashMap<Entity, Integer> deathPopMap = new HashMap<>();

    public static void init() {
        BaseCenter.EVENT_BUS.register(new PopManager());
        MinecraftForge.EVENT_BUS.register(new PopManager());
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        popMap.clear();
        deathPopMap.clear();
    }

    @Listener
    public void onDeath(DeathEvent event) {
        if (event.entity instanceof EntityPlayer && (Nametags.INSTANCE.popCount.getValue() != Nametags.TextMode.None || ChatSettings.INSTANCE.popNotifications.getValue())) {
            if (ChatSettings.INSTANCE.popNotifications.getValue()) {
                if (ChatSettings.INSTANCE.popNotificationsMarked.getValue()) {
                    ChatUtil.printChatMessage(ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsDeathColor) + ChatUtil.effectString(ChatSettings.INSTANCE.popNotificationsDeathEffect) + event.entity.getName() + " just fucking died after popping " + (popMap.get(event.entity) == null ? 0 : popMap.get(event.entity)) + " totems" +"!");
                }
                else {
                    ChatUtil.printRawChatMessage(ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsDeathColor) + ChatUtil.effectString(ChatSettings.INSTANCE.popNotificationsDeathEffect) + event.entity.getName() + " just fucking died after popping " + (popMap.get(event.entity) == null ? 0 : popMap.get(event.entity)) + " totems" +"!");
                }
            }

            deathPopMap.put(event.entity, popMap.get(event.entity) == null ? 0 : popMap.get(event.entity));
            popMap.put(event.entity, 0);
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if ((Nametags.INSTANCE.popCount.getValue() != Nametags.TextMode.None || ChatSettings.INSTANCE.popNotifications.getValue())
                && event.getPacket() instanceof SPacketEntityStatus
                && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            Entity entity = ((SPacketEntityStatus) event.getPacket()).getEntity(mc.world);

            if (!(entity instanceof EntityPlayer)) return;

            Integer currentPops = popMap.get(entity);
            popMap.put(entity, currentPops == null ? 1 : currentPops + 1);

            if (ChatSettings.INSTANCE.popNotifications.getValue()) {
                if (ChatSettings.INSTANCE.popNotificationsMarked.getValue()) {
                    ChatUtil.printChatMessage(ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsColor) + ChatUtil.effectString(ChatSettings.INSTANCE.popNotificationsEffect) + entity.getName() + " popped " + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsPopNumColor) + popMap.get(entity) + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsColor) + " time" + (popMap.get(entity) > 1 ? "s" : "") + "!");
                }
                else {
                    ChatUtil.printRawChatMessage(ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsColor) + ChatUtil.effectString(ChatSettings.INSTANCE.popNotificationsEffect) + entity.getName() + " popped " + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsPopNumColor) + popMap.get(entity) + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(ChatSettings.INSTANCE.popNotificationsColor) + " time" + (popMap.get(entity) > 1 ? "s" : "") + "!");
                }
            }
        }
    }
}
