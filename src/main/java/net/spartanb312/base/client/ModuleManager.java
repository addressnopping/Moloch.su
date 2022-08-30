package net.spartanb312.base.client;

import me.thediamondsword5.moloch.hud.huds.*;
import me.thediamondsword5.moloch.module.modules.movement.Timer;
import me.thediamondsword5.moloch.module.modules.other.*;
import net.spartanb312.base.gui.HUDEditorFinal;
import net.spartanb312.base.gui.renderers.HUDEditorRenderer;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.module.modules.client.HUDEditor;
import net.spartanb312.base.module.modules.visuals.NoRender;
import me.thediamondsword5.moloch.module.modules.client.*;
import me.thediamondsword5.moloch.module.modules.combat.*;
import me.thediamondsword5.moloch.module.modules.movement.*;
import me.thediamondsword5.moloch.module.modules.visuals.*;
import net.spartanb312.base.module.modules.movement.*;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.event.Priority;
import net.spartanb312.base.event.events.client.KeyEvent;
import net.spartanb312.base.event.events.render.RenderOverlayEvent;
import net.spartanb312.base.hud.HUDModule;
import net.spartanb312.base.hud.huds.ActiveModuleList;
import net.spartanb312.base.hud.huds.CombatInfo;
import net.spartanb312.base.hud.huds.Welcomer;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.module.modules.other.Spammer;
import net.spartanb312.base.module.modules.other.AntiContainer;
import net.spartanb312.base.module.modules.other.FakePlayer;
import net.spartanb312.base.utils.graphics.RenderUtils2D;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runBlocking;
import static net.spartanb312.base.utils.ItemUtils.mc;

public class ModuleManager {

    public final Map<Class<? extends Module>, Module> moduleMap = new ConcurrentHashMap<>();
    public final List<Module> moduleList = new ArrayList<>();
    private final Set<Class<? extends Module>> classes = new HashSet<>();

    private static ModuleManager instance;

    public static List<Module> getModules() {
        return getInstance().moduleList;
    }


    public static void init() {
        if (instance == null) instance = new ModuleManager();
        instance.moduleMap.clear();

        //Client
        registerNewModule(ClickGUI.class);
        registerNewModule(HUDEditor.class);
        registerNewModule(MoreClickGUI.class);
        registerNewModule(ChatSettings.class);
        registerNewModule(CustomFont.class);
        registerNewModule(Particles.class);
        registerNewModule(Blur.class);
        registerNewModule(ClientInfo.class);
        registerNewModule(RPC.class);

        //Combat
        registerNewModule(SelfBlock.class);
        registerNewModule(Aura.class);
        registerNewModule(SilentXP.class);
        registerNewModule(Criticals.class);
        registerNewModule(MinePlus.class);
        registerNewModule(MultiTask.class);
        registerNewModule(FastUse.class);
        registerNewModule(Surround.class);
        registerNewModule(Offhand.class);
        registerNewModule(SpongeCrystal.class);

        //Other
        registerNewModule(AntiContainer.class);
        registerNewModule(FakePlayer.class);
        registerNewModule(Spammer.class);
        registerNewModule(Freecam.class);
        registerNewModule(EnderChestFarm.class);
        registerNewModule(EntityAlerter.class);
        registerNewModule(NoEntityBlock.class);
        registerNewModule(DummyModule.class);
        registerNewModule(PacketCancel.class);
        registerNewModule(ElytraSwap.class);
        registerNewModule(PingSpoof.class);
        registerNewModule(GapSwapBind.class);
        registerNewModule(NameSpoof.class);
        registerNewModule(AutoClicker.class);

        //Movement
        registerNewModule(Sprint.class);
        registerNewModule(Velocity.class);
        registerNewModule(NoHunger.class);
        registerNewModule(NoSlow.class);
        registerNewModule(ReverseStep.class);
        registerNewModule(Step.class);
        registerNewModule(Timer.class);
        registerNewModule(GUIMove.class);
        registerNewModule(EntityControl.class);

        //Visuals
        registerNewModule(NoRender.class);
        registerNewModule(FullBright.class);
        registerNewModule(ESP.class);
        registerNewModule(Chams.class);
        registerNewModule(FOV.class);
        registerNewModule(HoleRender.class);
        registerNewModule(Nametags.class);
        registerNewModule(CityRender.class);
        registerNewModule(HoveredHighlight.class);
        registerNewModule(EntityPointer.class);
        registerNewModule(CameraClip.class);
        registerNewModule(ChorusTrace.class);
        registerNewModule(HeldModelTweaks.class);
        registerNewModule(PopRender.class);

        //HUD
        registerNewModule(ActiveModuleList.class);
        registerNewModule(CombatInfo.class);
        registerNewModule(Welcomer.class);
        registerNewModule(CustomHUDFont.class);
        registerNewModule(WaterMark.class);
        registerNewModule(ArmorDisplay.class);
        registerNewModule(FPS.class);
        registerNewModule(DebugThing.class);
        registerNewModule(LagNotify.class);

        instance.loadModules();
        BaseCenter.EVENT_BUS.register(instance);

    }

    public static void registerNewModule(Class<? extends Module> clazz) {
        instance.classes.add(clazz);
    }

    @Listener(priority = Priority.HIGHEST)
    public void onKey(KeyEvent event) {
        moduleList.forEach(it -> it.keyBinds.forEach(binds -> binds.test(event.getKey())));
    }

    @Listener(priority = Priority.HIGHEST)
    public void onRenderHUD(RenderOverlayEvent event) {
        RenderUtils2D.prepareGl();
        for (int i = HUDEditorRenderer.instance.hudModules.size() - 1; i >= 0; i--) {
            HUDModule hudModule = HUDEditorRenderer.instance.hudModules.get(i);

            if (!(mc.currentScreen instanceof HUDEditorFinal) && hudModule.isEnabled())
                hudModule.onHUDRender(event.getScaledResolution());
        }
        RenderUtils2D.releaseGl();
    }

    public static ModuleManager getInstance() {
        if (instance == null) instance = new ModuleManager();
        return instance;
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return getInstance().moduleMap.get(clazz);
    }

    public static Module getModuleByName(String targetName) {
        for (Module module : getModules()) {
            if (module.name.equalsIgnoreCase(targetName)) {
                return module;
            }
        }
        BaseCenter.log.info("Module " + targetName + " is not exist.Please check twice!");
        return null;
    }

    private void loadModules() {
        BaseCenter.log.info("[ModuleManager]Loading modules.");
        runBlocking(unit -> classes.stream().sorted(Comparator.comparing(Class::getSimpleName)).forEach(clazz -> {
            if (clazz != HUDModule.class) {
                try {
                    if (clazz.isAnnotationPresent(Parallel.class) && clazz.getAnnotation(Parallel.class).loadable()) {
                        unit.launch(() -> {
                            try {
                                add(clazz.newInstance(), clazz);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println("Couldn't initiate Module " + clazz.getSimpleName() + "! Error: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
                            }
                        });
                    } else {
                        add(clazz.newInstance(), clazz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Couldn't initiate Module " + clazz.getSimpleName() + "! Error: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
                }
            }
        }));
        sort();
        BaseCenter.log.info("[ModuleManager]Loaded " + moduleList.size() + " modules");
    }

    private synchronized void add(Module module, Class<? extends Module> clazz) {
        moduleList.add(module);
        moduleMap.put(clazz, module);
    }

    private void sort() {
        moduleList.sort(Comparator.comparing(it -> it.name));
    }

    public static String getModuleMiniIcons(Class<? extends Module> clazz) {
        if (clazz == Offhand.class) {
            return "A";
        }
        else if (clazz == NoHunger.class) {
            return "B";
        }
        else if (clazz == Sprint.class) {
            return "C";
        }
        else if (clazz == Velocity.class) {
            return "D";
        }
        else if (clazz == ChatSettings.class) {
            return "E";
        }
        else if (clazz == ClickGUI.class) {
            return "F";
        }
        else if (clazz == MoreClickGUI.class) {
            return "F";
        }
        else if (clazz == CustomFont.class) {
            return "G";
        }
        else if (clazz == HUDEditor.class) {
            return "H";
        }
        else if (clazz == Particles.class) {
            return "I";
        }
        else if (clazz == WaterMark.class) {
            return "J";
        }
        else {
            return "";
        }
    }

}
