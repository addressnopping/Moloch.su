package me.thediamondsword5.moloch.mixinotherstuff;


import net.minecraft.client.gui.ChatLine;

import java.util.HashMap;

public interface IChatLine
{
    String getTime();
    HashMap<ChatLine, String> storedTime = new HashMap<>();
}

