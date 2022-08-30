package net.spartanb312.base.core.config;

import com.google.gson.*;
import net.spartanb312.base.core.common.KeyBind;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.core.setting.settings.*;
import me.thediamondsword5.moloch.core.common.Color;
import me.thediamondsword5.moloch.core.common.Visibility;
import me.thediamondsword5.moloch.core.setting.settings.ColorSetting;
import me.thediamondsword5.moloch.core.setting.settings.VisibilitySetting;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigContainer {

    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser jsonParser = new JsonParser();

    private final List<Setting<?>> settings = new ArrayList<>();
    protected File configFile;

    public ConfigContainer() {
    }

    public ConfigContainer(String savePath, String saveName) {
        this.configFile = new File(savePath + saveName + ".json");
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public void saveConfig() {
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JsonObject jsonObject = new JsonObject();
            for (Setting<?> setting : settings) {
                if (setting instanceof BindSetting) {
                    jsonObject.addProperty(setting.getName(), ((BindSetting) setting).getValue().getKeyCode());
                }
                if (setting instanceof VisibilitySetting) {
                    jsonObject.addProperty(setting.getName(), ((VisibilitySetting) setting).getValue().getVisible());
                }
                if (setting instanceof ColorSetting) {
                    jsonObject.addProperty(setting.getName(), ((ColorSetting) setting).getValue().getColor());
                    jsonObject.addProperty(setting.getName() + "SyncGlobal", ((ColorSetting) setting).getValue().getSyncGlobal());
                    jsonObject.addProperty(setting.getName() + "Rainbow", ((ColorSetting) setting).getValue().getRainbow());
                    jsonObject.addProperty(setting.getName() + "RainbowSpeed", ((ColorSetting) setting).getValue().getRainbowSpeed());
                    jsonObject.addProperty(setting.getName() + "RainbowSaturation", ((ColorSetting) setting).getValue().getRainbowSaturation());
                    jsonObject.addProperty(setting.getName() + "RainbowBrightness", ((ColorSetting) setting).getValue().getRainbowBrightness());
                    jsonObject.addProperty(setting.getName() + "Red", ((ColorSetting) setting).getValue().getRed());
                    jsonObject.addProperty(setting.getName() + "Green", ((ColorSetting) setting).getValue().getGreen());
                    jsonObject.addProperty(setting.getName() + "Blue", ((ColorSetting) setting).getValue().getBlue());
                    jsonObject.addProperty(setting.getName() + "Alpha", ((ColorSetting) setting).getValue().getAlpha());
                }
                if (setting instanceof BooleanSetting) {
                    jsonObject.addProperty(setting.getName(), ((BooleanSetting) setting).getValue());
                }
                if (setting instanceof DoubleSetting) {
                    jsonObject.addProperty(setting.getName(), ((DoubleSetting) setting).getValue());
                }
                if (setting instanceof EnumSetting) {
                    jsonObject.addProperty(setting.getName(), ((EnumSetting<? extends Enum<?>>) setting).getValue().name());
                }
                if (setting instanceof FloatSetting) {
                    jsonObject.addProperty(setting.getName(), ((FloatSetting) setting).getValue());
                }
                if (setting instanceof IntSetting) {
                    jsonObject.addProperty(setting.getName(), ((IntSetting) setting).getValue());
                }
                if (setting instanceof StringSetting) {
                    jsonObject.addProperty(setting.getName(), ((StringSetting) setting).getValue());
                }
            }
            try {
                PrintWriter saveJSon = new PrintWriter(new FileWriter(configFile));
                saveJSon.println(gsonPretty.toJson(jsonObject));
                saveJSon.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readConfig() {
        if (configFile.exists()) {
            try {
                BufferedReader bufferedJson = new BufferedReader(new FileReader(configFile));
                JsonObject jsonObject = (JsonObject) jsonParser.parse(bufferedJson);
                bufferedJson.close();
                Map<String, JsonElement> map = new HashMap<>();
                jsonObject.entrySet().forEach(it -> map.put(it.getKey(), it.getValue()));
                for (Setting<?> setting : settings) {
                    JsonElement element = map.get(setting.getName());
                    if (element != null) {
                        if (setting instanceof BindSetting) {
                            ((BindSetting) setting).getValue().setKeyCode(element.getAsInt());
                        } else if (setting instanceof VisibilitySetting) {
                            ((VisibilitySetting) setting).getValue().setVisible(element.getAsBoolean());
                        } else if (setting instanceof ColorSetting) {
                            ((ColorSetting) setting).getValue().setColor(element.getAsInt());
                            ((ColorSetting) setting).getValue().setSyncGlobal(map.get(setting.getName() + "SyncGlobal").getAsBoolean());
                            ((ColorSetting) setting).getValue().setRainbow(map.get(setting.getName() + "Rainbow").getAsBoolean());
                            ((ColorSetting) setting).getValue().setRainbowSpeed(map.get(setting.getName() + "RainbowSpeed").getAsFloat());
                            ((ColorSetting) setting).getValue().setRainbowSaturation(map.get(setting.getName() + "RainbowSaturation").getAsFloat());
                            ((ColorSetting) setting).getValue().setRainbowBrightness(map.get(setting.getName() + "RainbowBrightness").getAsFloat());
                            ((ColorSetting) setting).getValue().setRed(map.get(setting.getName() + "Red").getAsInt());
                            ((ColorSetting) setting).getValue().setGreen(map.get(setting.getName() + "Green").getAsInt());
                            ((ColorSetting) setting).getValue().setBlue(map.get(setting.getName() + "Blue").getAsInt());
                            ((ColorSetting) setting).getValue().setAlpha(map.get(setting.getName() + "Alpha").getAsInt());
                        } else if (setting instanceof BooleanSetting) {
                            ((BooleanSetting) setting).setValue(element.getAsBoolean());
                        } else if (setting instanceof DoubleSetting) {
                            ((DoubleSetting) setting).setValue(element.getAsDouble());
                        } else if (setting instanceof EnumSetting) {
                            ((EnumSetting<?>) setting).setByName(element.getAsString());
                        } else if (setting instanceof FloatSetting) {
                            ((FloatSetting) setting).setValue(element.getAsFloat());
                        } else if (setting instanceof IntSetting) {
                            ((IntSetting) setting).setValue(element.getAsInt());
                        } else if (setting instanceof StringSetting) {
                            ((StringSetting) setting).setValue(element.getAsString());
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else saveConfig();
    }

    public Setting<KeyBind> setting(String name, KeyBind defaultValue) {
        BindSetting setting = new BindSetting(name, defaultValue);
        settings.add(setting);
        return setting;
    }

    public Setting<Visibility> setting(String name, Visibility defaultValue) {
        VisibilitySetting setting = new VisibilitySetting(name, defaultValue);
        settings.add(setting);
        return setting;
    }

    public Setting<Color> setting(String name, Color defaultValue) {
        ColorSetting setting = new ColorSetting(name, defaultValue);
        settings.add(setting);
        return setting;
    }

    public Setting<Boolean> setting(String name, boolean defaultValue) {
        BooleanSetting setting = new BooleanSetting(name, defaultValue);
        settings.add(setting);
        return setting;
    }

    public Setting<Double> setting(String name, double defaultValue, double minValue, double maxValue) {
        DoubleSetting setting = new DoubleSetting(name, defaultValue, minValue, maxValue);
        settings.add(setting);
        return setting;
    }


    public <E extends Enum<E>> Setting<E> setting(String name, E defaultValue) {
        EnumSetting<E> setting = new EnumSetting<>(name, defaultValue);
        settings.add(setting);
        return setting;
    }

    public Setting<Float> setting(String name, float defaultValue, float minValue, float maxValue) {
        FloatSetting setting = new FloatSetting(name, defaultValue, minValue, maxValue);
        settings.add(setting);
        return setting;
    }

    public Setting<Integer> setting(String name, int defaultValue, int minValue, int maxValue) {
        IntSetting setting = new IntSetting(name, defaultValue, minValue, maxValue);
        settings.add(setting);
        return setting;
    }

    public Setting<String> setting(String name, String defaultValue) {
        StringSetting setting = new StringSetting(name, defaultValue);
        settings.add(setting);
        return setting;
    }

}
