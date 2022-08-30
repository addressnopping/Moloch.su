package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.module.modules.other.NameSpoof;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.spartanb312.base.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.spartanb312.base.utils.CrystalUtil.mc;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerNameHook(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        if (ModuleManager.getModule(NameSpoof.class).isEnabled()) {
            cir.setReturnValue((networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName()))
                    .replaceAll(mc.player.getName(), NameSpoof.INSTANCE.name.getValue()));
        }
    }
}
