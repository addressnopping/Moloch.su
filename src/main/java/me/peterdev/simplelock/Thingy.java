package me.peterdev.simplelock;

import me.peterdev.simplelock.work.Checker;
import me.peterdev.simplelock.work.NoStackTrace;
import me.peterdev.simplelock.work.Generator;
import net.minecraft.client.Minecraft;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author PeterDev
 * @since 10/19/2021 at 4:24 p.m
 */

//TODO: make this less ugly

public class Thingy {
    public Thingy() {
        if (!Checker.doCheck()) {
            showMessage();
            throw new NoStackTrace("");
        }
    }
    public static void showMessage() {
        copyToClipboard();
        JOptionPane.showMessageDialog((Component)null, "HWID: " + Generator.getHWID(), "Copied to clipboard!", 0);
        throw new NoStackTrace("Verification was unsuccessful!");
    }
    public static void copyToClipboard() {
        StringSelection selection = new StringSelection(Generator.getHWID());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
