package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.event.events.player.*;
import me.thediamondsword5.moloch.module.modules.combat.MinePlus;
import me.thediamondsword5.moloch.module.modules.combat.MultiTask;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.spartanb312.base.BaseCenter;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.client.ModuleManager;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.spartanb312.base.utils.ItemUtils.mc;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Shadow
    public boolean isHittingBlock;

    @Shadow
    public GameType currentGameType;

    @Inject(method = "attackEntity", at = @At(value = "HEAD"), cancellable = true)
    public void attackEntityHook(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        if (targetEntity != null) {
            PlayerAttackEvent event = new PlayerAttackEvent(targetEntity);
            BaseCenter.EVENT_BUS.post(event);

            if (event.isCancelled())
                ci.cancel();
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDamageBlockHook(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        DamageBlockEvent event = new DamageBlockEvent(pos, side);
        BaseCenter.EVENT_BUS.post(event);

        if (event.isCancelled())
            cir.setReturnValue(false);
    }

    @Inject(method = "getIsHittingBlock", at = @At("HEAD"), cancellable = true)
    private void getIsHittingBlockHook(CallbackInfoReturnable<Boolean> cir) {
        MultiTaskEvent event = new MultiTaskEvent();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        else cir.setReturnValue(isHittingBlock);
    }

    @Inject(method = "clickBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;blockHitDelay:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    public void clickBlockHook(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakDelayEvent event = new BlockBreakDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }

    @Inject(method = "onPlayerDamageBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;blockHitDelay:I", opcode = Opcodes.PUTFIELD, ordinal = 1, shift = At.Shift.AFTER))
    public void onPlayerDamageBlockHook1(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakDelayEvent event = new BlockBreakDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }

    @Inject(method = "onPlayerDamageBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;blockHitDelay:I", opcode = Opcodes.PUTFIELD, ordinal = 2, shift = At.Shift.AFTER))
    public void onPlayerDamageBlockHook2(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakDelayEvent event = new BlockBreakDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }
}
