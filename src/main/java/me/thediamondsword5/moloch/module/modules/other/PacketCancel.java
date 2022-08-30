package me.thediamondsword5.moloch.module.modules.other;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;

@Parallel
@ModuleInfo(name = "PacketCancel", category = Category.OTHER, description = "Cancels certain client sent packets")
public class PacketCancel extends Module {

    Setting<Boolean> input = setting("Input", false).des("Cancels CPacketInput");
    Setting<Boolean> position = setting("Position", false).des("Cancels CPacketPlayer.Position");
    Setting<Boolean> rotate = setting("Rotation", false).des("Cancels CPacketPlayer.Rotation");
    Setting<Boolean> positionRotate = setting("PosRotate", false).des("Cancels CPacketPlayer.PositionRotation");
    Setting<Boolean> playerAbilities = setting("PlayerAbilities", false).des("Cancels CPacketPlayerAbilities");
    Setting<Boolean> digging = setting("Digging", false).des("Cancels CPacketPlayerDigging");
    Setting<Boolean> useItem = setting("UseItem", false).des("Cancels CPacketPlayerTryUseItem");
    Setting<Boolean> useItemOnBlock = setting("UseItemOnBlock", false).des("Cancels CPacketPlayerTryUseItemOnBlock");
    Setting<Boolean> useItemOnEntity = setting("UseItemOnEntity", false).des("Cancels CPacketUseEntity");
    Setting<Boolean> moveVehicle = setting("MoveVehicle", false).des("Cancels CPacketVehicleMove");
    Setting<Boolean> steerBoat = setting("SteerBoat", false).des("Cancels CPacketSteerBoat");
    Setting<Boolean> serverRemoveEntities = setting("ServerRemoveEntities", false).des("Cancels SPacketDestroyEntities");

    private int canceledIndex = 0;

    @Override
    public String getModuleInfo() {
        return canceledIndex + "";
    }

    @Override
    public void onDisable() {
        moduleDisableFlag = true;
        canceledIndex = 0;
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketInput && input.getValue()
                || event.getPacket() instanceof CPacketPlayer.Position && position.getValue()
                || event.getPacket() instanceof CPacketPlayer.Rotation && rotate.getValue()
                || event.getPacket() instanceof CPacketPlayer.PositionRotation && positionRotate.getValue()
                || event.getPacket() instanceof CPacketPlayerAbilities && playerAbilities.getValue()
                || event.getPacket() instanceof CPacketPlayerDigging && digging.getValue()
                || event.getPacket() instanceof CPacketPlayerTryUseItem && useItem.getValue()
                || event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && useItemOnBlock.getValue()
                || event.getPacket() instanceof CPacketUseEntity && useItemOnEntity.getValue()
                || event.getPacket() instanceof CPacketVehicleMove && moveVehicle.getValue()
                || event.getPacket() instanceof CPacketSteerBoat && steerBoat.getValue()) {
            canceledIndex++;
            event.cancel();
        }
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities && serverRemoveEntities.getValue()) {
            canceledIndex++;
            event.cancel();
        }
    }
}
