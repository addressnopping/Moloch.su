package net.spartanb312.base.client;

import com.google.gson.*;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.gui.ClickGUIFinal;
import net.spartanb312.base.gui.Panel;
import net.spartanb312.base.gui.renderers.ClickGUIRenderer;
import net.spartanb312.base.gui.renderers.HUDEditorRenderer;
import me.thediamondsword5.moloch.client.EnemyManager;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.ListUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigManager {
    public String skidName;

    public static final String CONFIG_PATH = "moloch.su/config/";
    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser jsonParser = new JsonParser();

    private final File CLIENT_FILE = new File(CONFIG_PATH + "moloch_Client_Stuff.json");
    private final File GUI_FILE = new File(CONFIG_PATH + "moloch_GUI_Stuff.json");

    private final List<File> configList = ListUtil.listOf(CLIENT_FILE, GUI_FILE);

    boolean shouldSave = false;

    public void shouldSave() {
        shouldSave = true;
    }

    public void onInit() {
        configList.forEach(it -> {
            if (!it.exists()) {
                shouldSave();
            }
        });
        if (shouldSave) saveAll();
    }

    public void saveGUI() {
        try {
            if (!GUI_FILE.exists()) {
                GUI_FILE.getParentFile().mkdirs();
                try {
                    GUI_FILE.createNewFile();
                } catch (Exception ignored) {
                }
            }
            JsonObject father = new JsonObject();
            List<Panel> panels = new ArrayList<>(ClickGUIRenderer.instance.panels);
            panels.addAll(HUDEditorRenderer.instance.panels);
            for (Panel panel : panels) {
                JsonObject jsonGui = new JsonObject();
                jsonGui.addProperty("X", panel.x);
                jsonGui.addProperty("Y", panel.y);
                jsonGui.addProperty("Extended", panel.extended);
                father.add(panel.category.categoryName, jsonGui);
            }
            JsonObject jsonDesGui = new JsonObject();
            jsonDesGui.addProperty("X", ClickGUIFinal.descriptionHubX);
            jsonDesGui.addProperty("Y", ClickGUIFinal.descriptionHubY);
            father.add("DescriptionHub", jsonDesGui);

            PrintWriter saveJSon = new PrintWriter(new FileWriter(GUI_FILE));
            saveJSon.println(gsonPretty.toJson(father));
            saveJSon.close();
        } catch (Exception e) {
            BaseCenter.log.error("Error while saving GUI config!");
            e.printStackTrace();
        }
    }

    public void loadGUI() {
        if (GUI_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(GUI_FILE));
                JsonObject guiJson = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry<String, JsonElement> entry : guiJson.entrySet()) {
                    Panel panel = ClickGUIRenderer.instance.getPanelByName(entry.getKey());
                    if (panel == null) panel = HUDEditorRenderer.instance.getPanelByName(entry.getKey());
                    JsonObject jsonGui = (JsonObject) entry.getValue();
                    if (panel != null) {
                        panel.x = jsonGui.get("X").getAsInt();
                        panel.y = jsonGui.get("Y").getAsInt();
                        panel.extended = jsonGui.get("Extended").getAsBoolean();
                    }
                    if (Objects.equals(entry.getKey(), "DescriptionHub")) {
                        ClickGUIFinal.descriptionHubX = jsonGui.get("X").getAsInt();
                        ClickGUIFinal.descriptionHubY = jsonGui.get("Y").getAsInt();
                    }
                }
            } catch (IOException e) {
                BaseCenter.log.error("Error while loading GUI config!");
                e.printStackTrace();
            }
        }
    }

    public void saveClient() {
        try {
            if (!CLIENT_FILE.exists()) {
                CLIENT_FILE.getParentFile().mkdirs();
                try {
                    CLIENT_FILE.createNewFile();
                } catch (Exception ignored) {
                }
            }

            JsonObject father = new JsonObject();

            saveFriend(father);
            saveEnemy(father);

            PrintWriter saveJSon = new PrintWriter(new FileWriter(CLIENT_FILE));
            saveJSon.println(gsonPretty.toJson(father));
            saveJSon.close();
        } catch (Exception e) {
            BaseCenter.log.error("Error while saving client stuff!");
            e.printStackTrace();
        }
    }

    private void loadClient() {
        if (CLIENT_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(CLIENT_FILE));
                JsonObject guiJason = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry<String, JsonElement> entry : guiJason.entrySet()) {
                    if (entry.getKey().equals("Friends")) {
                        JsonArray array = (JsonArray) entry.getValue();
                        array.forEach(it -> FriendManager.getInstance().friends.add(it.getAsString()));
                    } else if (entry.getKey().equals("Enemies")) {
                        JsonArray array = (JsonArray) entry.getValue();
                        array.forEach(it -> EnemyManager.getInstance().enemies.add(it.getAsString()));
                    }

                }
            } catch (IOException e) {
                BaseCenter.log.error("Error while loading client stuff!");
                e.printStackTrace();
            }
        }
    }

    private void saveFriend(JsonObject father) {
        JsonArray array = new JsonArray();
        FriendManager.getInstance().friends.forEach(array::add);
        father.add("Friends", array);
    }
    private void saveEnemy(JsonObject father) {
        JsonArray array = new JsonArray();
        EnemyManager.getInstance().enemies.forEach(array::add);
        father.add("Enemies", array);
    }

    private void loadModule() {
        ModuleManager.getModules().forEach(Module::onLoad);
    }

    private void saveModule() {
        ModuleManager.getModules().forEach(Module::onSave);
    }

    public static void loadAll() {
        getInstance().loadClient();
        getInstance().loadGUI();
        getInstance().loadModule();
    }

    public static void saveAll() {
        getInstance().saveClient();
        getInstance().saveGUI();
        getInstance().saveModule();
    }

    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    public static void init() {
        instance = new ConfigManager();
        instance.onInit();
        loadAll();
    }
}
