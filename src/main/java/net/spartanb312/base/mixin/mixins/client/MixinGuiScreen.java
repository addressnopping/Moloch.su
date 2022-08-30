package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.gui.components.StringInput;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import me.thediamondsword5.moloch.event.events.render.DrawScreenEvent;
import me.thediamondsword5.moloch.module.modules.client.Particles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.command.Command.mc;
import static org.lwjgl.opengl.GL11.*;

@Mixin(GuiScreen.class)
public class MixinGuiScreen extends Gui {
    @Inject(method = "drawDefaultBackground", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z"))
    private void drawDefaultBackgroundHook(CallbackInfo ci) {
        DrawScreenEvent event = new DrawScreenEvent.Layer1();
        BaseCenter.EVENT_BUS.post(event);

        DrawScreenEvent event2 = new DrawScreenEvent.Layer2();
        BaseCenter.EVENT_BUS.post(event2);

        if (mc.currentScreen instanceof GuiContainer) {
            if (!(Particles.INSTANCE.particlesOtherGUI.getValue() && Particles.INSTANCE.isEnabled()))
                GL11.glEnable(GL_BLEND);
        }
    }

    @Inject(method = "Lnet/minecraft/client/gui/GuiScreen;drawWorldBackground(I)V", at = @At("HEAD"), cancellable = true)
    private void drawWorldBackgroundHook(int tint, CallbackInfo ci) {
        if (mc.world != null && NoRender.INSTANCE.backgrounds.getValue() && ModuleManager.getModule(NoRender.class).isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    public void keyTypedHook(char typedChar, int keyCode, CallbackInfo ci) {
        StringInput.INSTANCE.keyTyped(typedChar, keyCode);
    }
}
