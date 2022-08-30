package me.thediamondsword5.moloch.module.modules.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.GuiMainMenu;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ConfigManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.core.setting.settings.StringSetting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import java.io.*;
import java.util.*;

@Parallel
@ModuleInfo(name = "DiscordRPC", category = Category.CLIENT, description = "Show people how cool you are :3")
public class RPC extends Module {

    public Setting<Integer> rpcUpdateDelay = setting("UpdateDelay", 3000, 1000, 20000).des("Milliseconds it takes for RPC to change images or text");
    public Setting<Image> imageMode = setting("ImageMode", Image.Logo).des("What type of image to display on RPC");
    public Setting<Boolean> larryImageRandom = setting("LarryImageRandom", true).des("Larry image randomized").whenAtMode(imageMode, Image.Larry);
    public Setting<Larry> larryImage = setting("LarryImage", Larry.Loaf).des("Which larry image to display").whenFalse(larryImageRandom).whenAtMode(imageMode, Image.Larry);
    public Setting<String> imageText = setting("ImageText", "its not ratted i swear").des("Text to show when image is hovered");
    public Setting<Boolean> randomizedStatus = setting("RandomizedStatus", false).des("Randomizes status from json file");
    public Setting<String> randomizedStatusInput = setting("RandomizedStatusInput", "").des("Input stuff to randomized status json").whenTrue(randomizedStatus);
    public Setting<String> status = setting("Status", "I stuck my dick in a meat grinder").des("Shows some message in RPC above play time").whenFalse(randomizedStatus);
    public Setting<Boolean> serverDetails = setting("ServerDetails", true).des("Shows current server information in details section");
    public Setting<Boolean> showIP = setting("ShowIP", true).des("Shows the server IP in your Discord RPC").whenTrue(serverDetails);
    public Setting<String> details = setting("Details", "Staring at my wall and vividly hallucinating rn").whenFalse(serverDetails);

    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    public static final DiscordRichPresence presence = new DiscordRichPresence();
    private static Thread thread;
    private final File STATUS_RANDOM_FILE = new File(ConfigManager.CONFIG_PATH + "moloch_Random_Status.json");
    private final List<String> cachedStatusMessages = new ArrayList<>();

    private final String[] larryImagesKey = new String[]{
            "bread",
            "image0",
            "larry5",
            "larry4",
            "larry3",
            "larry2",
            "larrybgless"
    };

    public RPC() {
        if (STATUS_RANDOM_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(STATUS_RANDOM_FILE));
                JsonObject json = (JsonObject) (new JsonParser()).parse(loadJson);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    cachedStatusMessages.add(entry.getKey());
                }
            }
            catch (IOException e) {
                BaseCenter.log.error("Smt went wrong while loading RPC random status messages");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTick() {
        if (!((StringSetting) randomizedStatusInput).listening && !Objects.equals(randomizedStatusInput.getValue(), "")) {
            writeToRandomStatuslist(randomizedStatusInput.getValue());
            randomizedStatusInput.setValue("");
        }
    }

    @Override
    public void onEnable() {
        start();
        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        stop();
        moduleDisableFlag = true;
    }

    private void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("1000211386514800670", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        rpc.Discord_UpdatePresence(presence);

        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                updateRPC();
                try {
                    Thread.sleep(rpcUpdateDelay.getValue());
                } catch (Exception ignored) {}
            }
        });
        thread.start();
    }

    private static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    private void updateRPC() {
        rpc.Discord_RunCallbacks();
        if (serverDetails.getValue()) {
            presence.details = mc.currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (mc.currentServerData != null ? (showIP.getValue() ? "on " + mc.currentServerData.serverIP + "." : " multiplayer.") : " singleplayer.");
        }
        else {
            presence.details = details.getValue();
        }

        if (randomizedStatus.getValue() && cachedStatusMessages.size() > 0) {
            presence.state = cachedStatusMessages.get(new Random().nextInt(cachedStatusMessages.size()));
        }
        else {
            presence.state = status.getValue();
        }

        switch (imageMode.getValue()) {
            case Logo: {
                presence.largeImageKey = "moloch";
                break;
            }

            case Larry: {
                if (larryImageRandom.getValue()) {
                    presence.largeImageKey = larryImagesKey[new Random().nextInt(larryImagesKey.length)];
                }
                else {
                    presence.largeImageKey = larryImageKey();
                }
                break;
            }

            case NekoSquad: {
                presence.largeImageKey = "nekosquadlogo";
                break;
            }
        }

        presence.largeImageText = imageText.getValue();

        rpc.Discord_UpdatePresence(presence);
    }

    private void updateJSon() throws IOException {
        JsonObject json = new JsonObject();

        for (String str : cachedStatusMessages) {
            json.addProperty(str, "");
        }

        PrintWriter saveJSon = new PrintWriter(new FileWriter(STATUS_RANDOM_FILE));
        saveJSon.println((new GsonBuilder().setPrettyPrinting().create()).toJson(json));
        saveJSon.close();
    }

    private void writeToRandomStatuslist(String message) {
        try {
            if (!STATUS_RANDOM_FILE.exists()) {
                STATUS_RANDOM_FILE.getParentFile().mkdirs();
                try {
                    STATUS_RANDOM_FILE.createNewFile();
                } catch (Exception ignored) {}
            }

            cachedStatusMessages.add(message);
            updateJSon();
        }
        catch (Exception e) {
            BaseCenter.log.error("Smt went wrong while trying to save entity name to whitelist");
            e.printStackTrace();
        }
    }

    private String larryImageKey() {
        switch (larryImage.getValue()) {
            case Loaf: return "bread";
            case Squint: return "image0";
            case Hoodie: return "larry5";
            case Sleeping: return "larry4";
            case CloseStare: return "larry3";
            case FarStare: return "larry2";
            case Portrait: return "larrybgless";
        }
        return "";
    }

    enum Image {
        Logo,
        Larry,
        NekoSquad
    }

    enum Larry {
        Loaf,
        Squint,
        Hoodie,
        Sleeping,
        CloseStare,
        FarStare,
        Portrait
    }
}

