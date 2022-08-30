package net.spartanb312.base.module.modules.other;

import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.concurrent.ConcurrentTaskManager;
import net.spartanb312.base.core.concurrent.repeat.RepeatUnit;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.FileUtil;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static net.spartanb312.base.core.concurrent.ConcurrentTaskManager.runRepeat;

@Parallel
@ModuleInfo(name = "Spammer", category = Category.OTHER, description = "Automatically spam")
public class Spammer extends Module {

    Setting<Integer> delay = setting("DelayS", 10, 1, 100);
    Setting<Boolean> randomSuffix = setting("Random", false);
    Setting<Boolean> greenText = setting("GreenText", false);

    private static final String fileName = "moloch.su/spammer/Spammer.txt";
    private static final String defaultMessage = "hello world";
    private static final List<String> spamMessages = new ArrayList<>();
    private static final Random rnd = new Random();

    RepeatUnit fileChangeListener = new RepeatUnit(1000, this::readSpamFile);

    RepeatUnit runner = new RepeatUnit(() -> delay.getValue() * 1000, () -> {
        if (mc.player == null) {
            disable();
        } else if (spamMessages.size() > 0) {
            String messageOut;
            if (randomSuffix.getValue()) {
                int index = rnd.nextInt(spamMessages.size());
                messageOut = spamMessages.get(index);
                spamMessages.remove(index);
            } else {
                messageOut = spamMessages.get(0);
                spamMessages.remove(0);
            }
            spamMessages.add(messageOut);
            if (this.greenText.getValue()) {
                messageOut = "> " + messageOut;
            }
            mc.player.connection.sendPacket(new CPacketChatMessage(messageOut.replaceAll("\u00a7", "")));
        }
    });

    public Spammer() {
        runner.suspend();
        ConcurrentTaskManager.runRepeat(runner);
        ConcurrentTaskManager.runRepeat(fileChangeListener);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            this.disable();
            return;
        }
        runner.resume();
        readSpamFile();
        moduleEnableFlag = true;

    }

    @Override
    public void onDisable() {
        spamMessages.clear();
        runner.suspend();
        moduleDisableFlag = true;

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void readSpamFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception ignored) {
            }
        }
        List<String> fileInput = FileUtil.readTextFileAllLines(fileName);
        Iterator<String> i = fileInput.iterator();
        spamMessages.clear();
        while (i.hasNext()) {
            String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) continue;
            spamMessages.add(s);
        }

        if (spamMessages.size() == 0) {
            spamMessages.add(defaultMessage);
        }
    }

}

