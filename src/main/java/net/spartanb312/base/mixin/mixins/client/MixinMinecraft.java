package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.event.events.player.LeftClickBlockEvent;
import me.thediamondsword5.moloch.event.events.player.MultiTaskEvent;
import me.thediamondsword5.moloch.event.events.player.RightClickDelayEvent;
import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import me.thediamondsword5.moloch.module.modules.combat.FastUse;
import me.thediamondsword5.moloch.module.modules.combat.MultiTask;
import me.thediamondsword5.moloch.module.modules.other.Freecam;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.common.MinecraftForge;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.event.events.client.*;
import me.thediamondsword5.moloch.event.events.player.PlayerAttackEvent;
import me.thediamondsword5.moloch.module.modules.combat.SelfBlock;
import net.minecraftforge.fml.common.FMLLog;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ConfigManager;
import net.spartanb312.base.event.decentraliized.DecentralizedClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CrashReport;
import net.spartanb312.base.mixin.mixins.accessor.AccessorPlayerControllerMP;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spartanb312.base.utils.ItemUtils.mc;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow public static Minecraft instance;

    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    public void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo info) {
        if (mc.currentScreen != null) {
            GuiScreenEvent.Closed screenEvent = new GuiScreenEvent.Closed(mc.currentScreen);
            BaseCenter.EVENT_BUS.post(screenEvent);
            GuiScreenEvent.Displayed screenEvent1 = new GuiScreenEvent.Displayed(guiScreenIn);
            BaseCenter.EVENT_BUS.post(screenEvent1);
        }
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    public void runGameLoop(CallbackInfo ci) {
        BaseCenter.EVENT_BUS.post(new GameLoopEvent());
    }

    @Inject(method = "runTickKeyboard", at = @At(value = "INVOKE_ASSIGN", target = "org/lwjgl/input/Keyboard.getEventKeyState()Z", remap = false))
    private void onKeyEvent(CallbackInfo ci) {
        if (mc.currentScreen != null)
            return;

        boolean down = Keyboard.getEventKeyState();
        int key = Keyboard.getEventKey();
        char ch = Keyboard.getEventCharacter();

        //Prevent from toggling all modules,when switching languages.
        if (key != Keyboard.KEY_NONE)
            BaseCenter.EVENT_BUS.post(down ? new KeyEvent(key, ch) : new InputUpdateEvent(key, ch));
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    public void onTick(CallbackInfo ci) {
        if (mc.player != null) {
            DecentralizedClientTickEvent.instance.post(null);
            BaseCenter.EVENT_BUS.post(new TickEvent());
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void onInitMinecraft(CallbackInfo ci) {
        BaseCenter.EVENT_BUS.register(BaseCenter.instance);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void onPreInit(CallbackInfo callbackInfo) {
        BaseCenter.EVENT_BUS.post(new InitializationEvent.PreInitialize());
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    public void onInit(CallbackInfo ci) {
        FMLLog.log.fatal("Loading moloch.su");
        BaseCenter.EVENT_BUS.post(new InitializationEvent.Initialize());
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void onPostInit(CallbackInfo ci) {
        BaseCenter.EVENT_BUS.post(new InitializationEvent.PostInitialize());
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        save();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        save();
    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    public void clickMouseHook(CallbackInfo ci) {
        if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.entityHit != null) {
                PlayerAttackEvent event = new PlayerAttackEvent(mc.objectMouseOver.entityHit);
                BaseCenter.EVENT_BUS.post(event);

                if (event.isCancelled())
                    ci.cancel();
            }
            else if (BlockUtil.isBlockPlaceable(mc.objectMouseOver.getBlockPos())) {
                LeftClickBlockEvent event = new LeftClickBlockEvent(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);
                BaseCenter.EVENT_BUS.post(event);

                if (event.isCancelled())
                    ci.cancel();
            }
        }
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActiveRedirect(EntityPlayerSP entityPlayerSP) {
        MultiTaskEvent event = new MultiTaskEvent();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            return false;
        }
        return entityPlayerSP.isHandActive();
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"))
    private void rightClickMouseHook(CallbackInfo ci) {
        MultiTaskEvent event = new MultiTaskEvent();
        BaseCenter.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            mc.playerController.isHittingBlock = false;
        }
    }

    @Inject(method = "rightClickMouse", at = @At(value = "RETURN", ordinal = 0))
    public void rightClickMouseHook1(CallbackInfo ci) {
        RightClickDelayEvent event = new RightClickDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }

    @Inject(method = "rightClickMouse", at = @At(value = "RETURN", ordinal = 1))
    public void rightClickMouseHook2(CallbackInfo ci) {
        RightClickDelayEvent event = new RightClickDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }

    @Inject(method = "rightClickMouse", at = @At(value = "RETURN", ordinal = 2))
    public void rightClickMouseHook3(CallbackInfo ci) {
        RightClickDelayEvent event = new RightClickDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }

    @Inject(method = "rightClickMouse", at = @At(value = "RETURN", ordinal = 3))
    public void rightClickMouseHook4(CallbackInfo ci) {
        RightClickDelayEvent event = new RightClickDelayEvent();
        BaseCenter.EVENT_BUS.post(event);
    }

    private void save() {
        if (ModuleManager.getModule(SelfBlock.class).isEnabled())
            ModuleManager.getModule(SelfBlock.class).disable();

        if (ModuleManager.getModule(Freecam.class).isEnabled()) {
            MinecraftForge.EVENT_BUS.unregister(this);
            Freecam.INSTANCE.resetFreecam();
            ModuleManager.getModule(Freecam.class).disable();
        }

        System.out.println("Shutting down: saving moloch.su configuration");
        ConfigManager.saveAll();
        System.out.println("Configuration saved.");
    }

}
