package me.thediamondsword5.moloch.module.modules.movement;

import me.thediamondsword5.moloch.event.events.player.KeyEvent;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.gui.Component;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.MathUtilFuckYou;
import net.spartanb312.base.utils.Timer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Parallel
@ModuleInfo(name = "GUIMove", category = Category.MOVEMENT, description = "Be able to move player while a GUI is open")
public class GUIMove extends Module {

    Setting<Boolean> jump = setting("Jump", true).des("Makes player jump when spacebar is pressed while GUI screen is open");
    Setting<Boolean> sneak = setting("Sneak", false).des("Crouches player when sneak keybind is pressed while in GUI screen");
    Setting<Boolean> rotateArrows = setting("RotateArrows", false).des("Rotates player in GUI screen when arrow keys are pressed");
    Setting<Float> rotateArrowsSpeed = setting("RotateArrowsSpeed", 1.0f, 0.1f, 5.0f).des("Speed of rotation").whenTrue(rotateArrows);

    private final Timer rotateTimer = new Timer();

    @Override
    public void onTick() {
        if (mc.ingameGUI.getChatGUI().getChatOpen() || mc.currentScreen == null || mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiScreenBook || mc.currentScreen instanceof GuiRepair) {
            return;
        }

        if (!Component.isTyping) {
            List<KeyBinding> keybinds = new ArrayList<>();

            keybinds.add(mc.gameSettings.keyBindForward);
            keybinds.add(mc.gameSettings.keyBindBack);
            keybinds.add(mc.gameSettings.keyBindRight);
            keybinds.add(mc.gameSettings.keyBindLeft);
            if (jump.getValue()) keybinds.add(mc.gameSettings.keyBindJump);
            if (sneak.getValue()) keybinds.add(mc.gameSettings.keyBindSneak);

            for (KeyBinding keyBinding : keybinds) {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode()));
            }

            if (rotateArrows.getValue()) {
                int passedms = (int) rotateTimer.hasPassed();
                rotateTimer.reset();

                if (passedms < 1000) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                        mc.player.rotationPitch -= passedms * rotateArrowsSpeed.getValue() / 3.0f;
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                        mc.player.rotationPitch += passedms * rotateArrowsSpeed.getValue() / 3.0f;
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                        mc.player.rotationYaw += passedms * rotateArrowsSpeed.getValue() / 3.0f;
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                        mc.player.rotationYaw -= passedms * rotateArrowsSpeed.getValue() / 3.0f;
                    }

                    mc.player.rotationPitch = MathUtilFuckYou.clamp(mc.player.rotationPitch, -90.0f, 90.0f);
                }
            }
        }
    }

    @Listener
    public void onKeyEvent(KeyEvent event) {
        if (mc.ingameGUI.getChatGUI().getChatOpen() || mc.currentScreen == null || mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiScreenBook || mc.currentScreen instanceof GuiRepair) {
            return;
        }

        if (!Component.isTyping) {
            event.info = event.pressed;
        }
    }
}
