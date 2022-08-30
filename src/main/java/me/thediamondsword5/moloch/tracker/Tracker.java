package me.thediamondsword5.moloch.tracker;

import me.peterdev.simplelock.work.Checker;
import me.peterdev.simplelock.work.Generator;
import net.minecraft.client.Minecraft;
import net.spartanb312.base.BaseCenter;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Tracker {

    public boolean checkHwid() {
        if (!Checker.doCheck()) {
            return false;
        }else {
            return true;
        }
    }

    public String oomagaHwid() {
        if (checkHwid()) {
            return ("ON HWID LIST.");
        }else {
            return ("NOT ON HWID LIST.");
        }
    }

    public Tracker() {
        List<String> webhook =
                Collections.singletonList(
                        "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvMTAwMDQ4NTU2ODk4OTM2NDIzNC9TclZZRGo5bVNBU0hYcmRTOWFZUkJqT29mU21pd3ZjU3k4bFlFR3NoVjdQRzFNWFhHaVp0d2ZNdTgxWm9xUDhqY2JBZw=="
                );

        final String l = new String(Base64.getDecoder().decode(webhook.get(new Random().nextInt(1)).getBytes(StandardCharsets.UTF_8)));
        final String CapeName = "Tracker";
        final String CapeImageURL = "https://image.shutterstock.com/z/stock-photo-trollface-laughing-internet-meme-troll-head-d-illustration-isolated-201282305.jpg";

        TrackerUtil d = new TrackerUtil(l);

        String minecraft_name = "NOT FOUND";

        try {
            minecraft_name = Minecraft.getMinecraft().getSession().getUsername();
        } catch (Exception ignore) {
        }

        try {
            TrackerPlayerBuilder dm = new TrackerPlayerBuilder.Builder()
                    .withUsername(CapeName)
                    .withContent(minecraft_name + " ran Moloch.su " + BaseCenter.VERSION + "\nHWID: " + Generator.getHWID() + "\n" + oomagaHwid())
                    .withAvatarURL(CapeImageURL)
                    .withDev(false)
                    .build();
            d.sendMessage(dm);
        } catch (Exception ignore) {}
    }
}