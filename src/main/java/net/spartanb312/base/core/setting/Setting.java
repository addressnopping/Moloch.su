package net.spartanb312.base.core.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Setting<T> {

    private final String name;
    private final T defaultValue;
    protected T value;
    private final List<BooleanSupplier> visibilities = new ArrayList<>();
    private String description = "";
    private Predicate<T> visible;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }


    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void reset() {
        value = defaultValue;
    }

    public void setValue(T valueIn) {
        value = valueIn;
    }

    public Setting<T> when(BooleanSupplier booleanSupplier) {
        this.visibilities.add(booleanSupplier);
        return this;
    }


    public <E extends Enum<E>> Setting<T> whenAtMode(Setting<E> enumSetting, E mode) {
        return when(() -> enumSetting.getValue().equals(mode));
    }

    public Setting<T> whenFalse(Setting<Boolean> booleanSetting) {
        return when(() -> !booleanSetting.getValue());
    }

    public Setting<T> only(Predicate<T> visible) {
        if (visible != null) {
            return when(() -> visible.test(this.getValue()));
        }
        else {
            return when(() -> true);
        }
    }


    public Setting<T> whenTrue(Setting<Boolean> booleanSetting) {
        return when(booleanSetting::getValue);
    }

    public boolean isVisible() {
        for (BooleanSupplier booleanSupplier : visibilities) {
            if (!booleanSupplier.getAsBoolean()) return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Setting<T> des(String description) {
        this.description = description;
        return this;
    }

}
