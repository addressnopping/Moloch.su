package net.spartanb312.base;

import me.peterdev.simplelock.Thingy;
import me.peterdev.simplelock.work.Checker;
import me.thediamondsword5.moloch.client.PopManager;
import me.thediamondsword5.moloch.client.ServerManager;
import me.thediamondsword5.moloch.module.modules.client.ClientInfo;
import me.thediamondsword5.moloch.tracker.Tracker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.spartanb312.base.client.*;
import net.spartanb312.base.module.modules.client.ClickGUI;
import net.spartanb312.base.utils.ColorUtil;
import net.minecraftforge.fml.common.Mod;
import me.thediamondsword5.moloch.client.EnemyManager;
import net.spartanb312.base.core.event.EventManager;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.event.Priority;
import net.spartanb312.base.event.events.client.InitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runBlocking;
import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runTiming;

/**
 * Author B_312
 * Since 05/01/2021
 * Last update on 09/21/2021
 */

@Mod(modid = "moloch", name = "moloch.su", version = "b3")
public class BaseCenter {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static ColorUtil colorUtil = new ColorUtil();
    public static FontManager fontManager = new FontManager();
    public static Thingy hwidManager;
    public static Tracker tracker;

    public static final String AUTHOR = "TheDiamondSword5 && popbob && B_312";
    public static final String GITHUB = "base -> https://github.com/SpartanB312/Cursa";
    public static final String VERSION = "b3";

    public static String CHAT_SUFFIX = " ᵐᵒˡᵒᶜʰ.ˢᵘ";

    public static final Logger log = LogManager.getLogger("moloch.su");
    private static Thread mainThread;

    @Listener(priority = Priority.HIGHEST)
    public void preInitialize(InitializationEvent.PreInitialize event) {
        tracker = new Tracker(); //Initializes the tracker before the HWID lock
        mainThread = Thread.currentThread();
    }
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        hwidManager = new Thingy(); //Awful way to initialize the HWID lock, but at least it works
    }

    @Listener(priority = Priority.HIGHEST)
    public void initialize(InitializationEvent.Initialize event) {
        long tookTime = runTiming(() -> {
            Display.setTitle("moloch.su - " + VERSION);

            //Parallel load managers
            runBlocking(it -> {
                BaseCenter.log.info("Loading Font Manager");
                FontManager.init();

                BaseCenter.log.info("Loading Module Manager");
                ModuleManager.init();

                BaseCenter.log.info("Loading GUI Manager");
                it.launch(GUIManager::init);

                BaseCenter.log.info("Loading Command Manager");
                it.launch(CommandManager::init);

                BaseCenter.log.info("Loading Friend Manager");
                it.launch(FriendManager::init);

                BaseCenter.log.info("Loading Enemy Manager");
                it.launch(EnemyManager::init);

                BaseCenter.log.info("Loading Config Manager");
                it.launch(ConfigManager::init);

                BaseCenter.log.info("Loading Server Manager");
                it.launch(ServerManager::init);

                BaseCenter.log.info("Loading Pop Manager");
                it.launch(PopManager::init);
            });
        });
        log.info("Launched in " + tookTime);
    }

    @Listener(priority = Priority.HIGHEST)
    public void postInitialize(InitializationEvent.PostInitialize event) {
        ClickGUI.instance.disable();
    }

    public static boolean isMainThread(Thread thread) {
        return thread == mainThread;
    }

    public static EventManager EVENT_BUS = new EventManager();
    public static ModuleBus MODULE_BUS = new ModuleBus();

    public static final BaseCenter instance = new BaseCenter();

}