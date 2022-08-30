package net.spartanb312.base.module;

import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.ConfigManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.notification.NotificationManager;
import net.spartanb312.base.utils.ChatUtil;
import me.thediamondsword5.moloch.core.common.Visibility;
import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import me.thediamondsword5.moloch.module.modules.client.MoreClickGUI;
import net.minecraft.client.Minecraft;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.common.KeyBind;
import net.spartanb312.base.core.concurrent.task.Task;
import net.spartanb312.base.core.concurrent.task.VoidTask;
import net.spartanb312.base.core.config.ListenableContainer;
import net.spartanb312.base.core.event.decentralization.DecentralizedEvent;
import net.spartanb312.base.core.event.decentralization.EventData;
import net.spartanb312.base.event.events.client.InputUpdateEvent;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.event.events.render.RenderEvent;
import net.spartanb312.base.event.events.render.RenderOverlayEvent;
import me.thediamondsword5.moloch.hud.huds.CustomHUDFont;
import me.thediamondsword5.moloch.module.modules.client.CustomFont;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Module extends ListenableContainer {

    public final String name = getAnnotation().name();
    public final Category category = getAnnotation().category();
    public final Parallel annotation = getClass().getAnnotation(Parallel.class);
    public final boolean parallelRunnable = annotation != null && annotation.runnable();
    public final String description = getAnnotation().description();

    public Module() {
        configFile = new File(ConfigManager.CONFIG_PATH + "modules/" + category.categoryName + "/" + name + ".json");
    }

    public boolean enabled = false;
    private final ConcurrentHashMap<DecentralizedEvent<? extends EventData>, Task<? extends EventData>> listenerMap = new ConcurrentHashMap<>();

    public final List<KeyBind> keyBinds = new ArrayList<>();

    protected final Setting<Boolean> enabledSetting = setting("Enabled", false).when(() -> false);
    public final Setting<KeyBind> bindSetting = setting("Bind", subscribeKey(new KeyBind(getAnnotation().keyCode(), this::toggle))).des("The key bind of this module");
    public final Setting<Visibility> visibleSetting = setting("Visible", new Visibility(true)).des("Determine the visibility of the module");
    public final Setting<String> displayName = setting("DisplayName", name).des("Display name of module on arraylist and toggle notifications");


    public boolean moduleEnableFlag = false;
    public boolean moduleDisableFlag = false;

    public static Minecraft mc = Minecraft.getMinecraft();

    public void onSave() {
        enabledSetting.setValue(enabled);
        saveConfig();
    }

    public void onLoad() {
        readConfig();
        if (enabledSetting.getValue() && !enabled) enable();
        else if (!enabledSetting.getValue() && enabled) disable();

        if (ModuleManager.getModule(CustomHUDFont.class).isDisabled()) {
            ModuleManager.getModule(CustomHUDFont.class).enable();
        }
        if (ModuleManager.getModule(CustomFont.class).isDisabled()) {
            ModuleManager.getModule(CustomFont.class).enable();
        }
        if (ModuleManager.getModule(ChatSettings.class).isDisabled()) {
            ModuleManager.getModule(ChatSettings.class).enable();
        }
        if (ModuleManager.getModule(MoreClickGUI.class).isDisabled()) {
            ModuleManager.getModule(MoreClickGUI.class).enable();
        }
        if (ModuleManager.getModule(ClientInfo.class).isDisabled()) {
            ModuleManager.getModule(ClientInfo.class).enable();
        }
    }


    public KeyBind subscribeKey(KeyBind keyBind) {
        keyBinds.add(keyBind);
        return keyBind;
    }

    public KeyBind unsubscribeKey(KeyBind keyBind) {
        keyBinds.remove(keyBind);
        return keyBind;
    }

    public void toggle() {
        if (isEnabled()) disable();
        else enable();
    }

    public void reload() {
        if (enabled) {
            enabled = false;
            BaseCenter.MODULE_BUS.unregister(this);
            onDisable();
            enabled = true;
            BaseCenter.MODULE_BUS.register(this);
            onEnable();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void enable() {
        enabled = true;
        BaseCenter.MODULE_BUS.register(this);
        subscribe();

        if (!(ChatSettings.INSTANCE.invisibleToggleMessages.getValue() && !visibleSetting.getValue().getVisible())) {
            NotificationManager.moduleToggle(this, displayName.getValue(), true);
        }

        onEnable();
    }

    public void disable() {
        enabled = false;
        BaseCenter.MODULE_BUS.unregister(this);
        unsubscribe();

        if (!(ChatSettings.INSTANCE.invisibleToggleMessages.getValue() && !visibleSetting.getValue().getVisible())) {
            NotificationManager.moduleToggle(this, displayName.getValue(), false);
        }

        onDisable();
    }

    public void onPacketReceive(PacketEvent.Receive event) {
    }

    public void onPacketSend(PacketEvent.Send event) {
    }

    public void onTick() {
    }

    public void onRenderTick() {
    }

    public void onEnable() {
        moduleEnableFlag = true;
    }

    public void onDisable() {
        moduleDisableFlag = true;
    }

    public void onRender(RenderOverlayEvent event) {
    }

    public void onRenderWorld(RenderEvent event) {
    }

    public void onInputUpdate(InputUpdateEvent event) {
    }

    public void onSettingChange(Setting<?> setting) {
    }

    public Setting<VoidTask> actionListener(String name, VoidTask defaultValue) {
        ListenerSetting setting = new ListenerSetting(name, defaultValue);
        getSettings().add(setting);
        return setting;
    }

    @SafeVarargs
    public final <T> List<T> listOf(T... elements) {
        return Arrays.asList(elements);
    }

    public ModuleInfo getAnnotation() {
        if (getClass().isAnnotationPresent(ModuleInfo.class)) {
            return getClass().getAnnotation(ModuleInfo.class);
        }
        throw new IllegalStateException("No Annotation on class " + this.getClass().getCanonicalName() + "!");
    }

    public String getModuleInfo() {
        return "";
    }

    public String getHudSuffix() {
        return this.displayName.getValue() + (!this.getModuleInfo().equals("") ? (ChatUtil.colored("7") + "[" + ChatUtil.colored("f") + this.getModuleInfo() + ChatUtil.colored("7") + "]") : this.getModuleInfo());
    }


}
