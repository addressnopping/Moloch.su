package me.thediamondsword5.moloch.module.modules.other;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ConfigManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.core.setting.settings.StringSetting;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.*;
import net.spartanb312.base.utils.Timer;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.*;

@Parallel
@ModuleInfo(name = "EntityAlerter", category = Category.OTHER, description = "Alerts you when an entity comes into or leaves render distance")
public class EntityAlerter extends Module {

    Setting<Boolean> anyPlayers = setting("AnyPlayers", true).des("Do notify on any player entering visual range");
    Setting<Boolean> notificationsMarked = setting("MessageMarked", true).des("Put client name in front of chat alert text");
    Setting<ChatSettings.StringColorsNoRainbow> enterMessageColor = setting("EnterMessageColor", ChatSettings.StringColorsNoRainbow.Green).des("Color of chat alert text on enter entity");
    Setting<ChatSettings.StringColorsNoRainbow> exitMessageColor = setting("ExitMessageColor", ChatSettings.StringColorsNoRainbow.Red).des("Color of chat alert text on exit entity");
    Setting<ChatSettings.Effects> messageEffect = setting("MessageEffect", ChatSettings.Effects.Bold).des("Effects for chat alert text");
    Setting<Boolean> tracerPulse = setting("TracerPulse", true).des("Renders a temporary tracer to the entity");
    Setting<Float> tracerWidth = setting("TracerWidth", 1.0f, 1.0f, 5.0f).des("Thickness of tracer line").whenTrue(tracerPulse);
    Setting<Boolean> tracerSpine = setting("TracerSpine", true).des("Draw a line going up the entity's bounding box").whenTrue(tracerPulse);
    Setting<Float> tracerPulseFactor = setting("TracerPulseFactor", 1.0f, 0.1f, 10.0f).des("Speed of how fast tracer pulse fades away").whenTrue(tracerPulse);
    Setting<Color> tracerEnterColor = setting("TracerEnterColor", new Color(new java.awt.Color(50, 255, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 50, 255, 50, 175)).whenTrue(tracerPulse);
    Setting<Color> tracerExitColor = setting("TracerExitColor", new Color(new java.awt.Color(255, 50, 50, 175).getRGB(), false, false, 1.0f, 0.75f, 0.9f, 255, 50, 50, 175)).whenTrue(tracerPulse);
    Setting<String> entitiesToWhitelist = setting("WhitelistEntities", "").des("Type the name of entity to find");
    Setting<String> entitiesToRemoveFromWhitelist = setting("UnWhitelistEntities", "").des("Type the name entity to remove from whitelist");
    //Setting<Boolean> hudNotifications = setting("HUDNotifications", false).des("Make entity found notifications appear as HUD notifications").only(v -> ModuleManager.getModule(Notifications.class).isEnabled());

    private final List<String> cachedEntityWhitelist = new ArrayList<>();
    private final HashMap<Map.Entry<Boolean, Entity>, Float> tracerMap = new HashMap<>();
    private final List<Entity> localEntityList = new ArrayList<>();
    private final Timer tracerTimer = new Timer();
    private final File WHITELIST_FILE = new File(ConfigManager.CONFIG_PATH + "moloch_EntityAlerter_Whitelist.json");

    public EntityAlerter() {
        if (WHITELIST_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(WHITELIST_FILE));
                JsonObject json = (JsonObject) (new JsonParser()).parse(loadJson);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    cachedEntityWhitelist.add(entry.getKey());
                }
            }
            catch (IOException e) {
                BaseCenter.log.error("Smt went wrong while loading entity whitelist");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTick() {
        EntityUtil.entitiesListFlag = true;
        for (Entity entity : EntityUtil.entitiesList()) {
            if (!localEntityList.contains(entity)){
                localEntityList.add(entity);
                onEntityAdd(entity);
            }
        }
        EntityUtil.entitiesListFlag = false;

        for (Entity entity : new ArrayList<>(localEntityList)) {
            if (!EntityUtil.entitiesList().contains(entity)) {
                localEntityList.remove(entity);
                onEntityRemoved(entity);
            }
        }

        if (!((StringSetting) entitiesToWhitelist).listening && !Objects.equals(entitiesToWhitelist.getValue(), "")) {
            writeToWhitelist(entitiesToWhitelist.getValue());
            entitiesToWhitelist.setValue("");
        }

        if (!((StringSetting) entitiesToRemoveFromWhitelist).listening && !Objects.equals(entitiesToRemoveFromWhitelist.getValue(), "")) {
            removeFromWhiteList(entitiesToRemoveFromWhitelist.getValue());
            entitiesToRemoveFromWhitelist.setValue("");
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (tracerPulse.getValue()) {

            int passedms = (int) tracerTimer.hasPassed();
            tracerTimer.reset();

            GL11.glPushMatrix();
            for (Map.Entry<Map.Entry<Boolean, Entity>, Float> entry : new HashMap<>(tracerMap).entrySet()) {
                float alphaThreader = MathUtilFuckYou.clamp(entry.getValue(), 0.0f, 300.0f);

                SpartanTessellator.drawTracer(entry.getKey().getValue(), tracerWidth.getValue(), tracerSpine.getValue(),
                        entry.getKey().getKey() ? new java.awt.Color(tracerEnterColor.getValue().getColorColor().getRed(), tracerEnterColor.getValue().getColorColor().getGreen(), tracerEnterColor.getValue().getColorColor().getBlue(), (int)(tracerEnterColor.getValue().getAlpha() * alphaThreader / 300.0f)).getRGB()
                                : new java.awt.Color(tracerExitColor.getValue().getColorColor().getRed(), tracerExitColor.getValue().getColorColor().getGreen(), tracerExitColor.getValue().getColorColor().getBlue(), (int)(tracerExitColor.getValue().getAlpha() * alphaThreader / 300.0f)).getRGB());

                if (passedms < 1000) {
                    alphaThreader -= (tracerPulseFactor.getValue() / 10.0f) * passedms;
                    if (alphaThreader < 0) {
                        tracerMap.remove(entry.getKey());
                    }
                    else {
                        tracerMap.put(entry.getKey(), alphaThreader);
                    }
                }
            }
            GL11.glPopMatrix();
        }
    }

    private void onEntityAdd(Entity entity) {
        if (mc.renderViewEntity != null && entity != null && !entity.getName().equals(mc.renderViewEntity.getName()) && (cachedEntityWhitelist.contains(entity.getName()) || (entity instanceof EntityPlayer && anyPlayers.getValue()))) {
            if (tracerPulse.getValue()) tracerMap.put(new AbstractMap.SimpleEntry<>(true, entity), 300.0f);

            if (notificationsMarked.getValue()) {
                ChatUtil.printChatMessage(entity.getName() + " " + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(enterMessageColor) + ChatUtil.effectString(messageEffect) + "has entered visual range");
            }
            else {
                ChatUtil.printRawChatMessage(entity.getName() + " " + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(enterMessageColor) + ChatUtil.effectString(messageEffect) + "has entered visual range");
            }
        }
    }

    private void onEntityRemoved(Entity entity) {
        if (mc.renderViewEntity != null && entity != null && !entity.getName().equals(mc.renderViewEntity.getName()) && (cachedEntityWhitelist.contains(entity.getName()) || (entity instanceof EntityPlayer && anyPlayers.getValue()))) {
            if (tracerPulse.getValue()) tracerMap.put(new AbstractMap.SimpleEntry<>(false, entity), 300.0f);

            if (notificationsMarked.getValue()) {
                ChatUtil.printChatMessage(entity.getName() + " " + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(exitMessageColor) + ChatUtil.effectString(messageEffect) + "has left visual range");
            }
            else {
                ChatUtil.printRawChatMessage(entity.getName() + " " + ChatUtil.SECTIONSIGN + ChatSettings.INSTANCE.colorString(exitMessageColor) + ChatUtil.effectString(messageEffect) + "has left visual range");
            }
        }
    }

    private void updateJSon() throws IOException {
        JsonObject json = new JsonObject();

        for (String str : cachedEntityWhitelist) {
            json.addProperty(str, "");
        }

        PrintWriter saveJSon = new PrintWriter(new FileWriter(WHITELIST_FILE));
        saveJSon.println((new GsonBuilder().setPrettyPrinting().create()).toJson(json));
        saveJSon.close();
    }

    private void writeToWhitelist(String entityName) {
        try {
            if (!WHITELIST_FILE.exists()) {
                WHITELIST_FILE.getParentFile().mkdirs();
                try {
                    WHITELIST_FILE.createNewFile();
                } catch (Exception ignored) {}
            }

            cachedEntityWhitelist.add(entityName);
            updateJSon();
        }
        catch (Exception e) {
            BaseCenter.log.error("Smt went wrong while trying to save entity name to whitelist");
            e.printStackTrace();
        }
    }

    private void removeFromWhiteList(String entityName) {
        try {
            if (!WHITELIST_FILE.exists()) {
                BaseCenter.log.error("Whitelist file doesn't exist");
            }
            else {
                cachedEntityWhitelist.remove(entityName);
                updateJSon();
            }
        }
        catch (Exception e) {
            BaseCenter.log.error("Smt went wrong while trying to save entity name to whitelist");
            e.printStackTrace();
        }
    }
}
