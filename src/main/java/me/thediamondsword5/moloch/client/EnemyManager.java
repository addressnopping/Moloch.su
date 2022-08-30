package me.thediamondsword5.moloch.client;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    public List<String> enemies = new ArrayList<>();

    public static void init() {
        instance = new EnemyManager();
        instance.enemies.clear();
    }

    public static boolean isEnemy(Entity entity) {
        return isEnemy(entity.getName());
    }

    public static boolean isEnemy(String name) {
        return getInstance().enemies.contains(name);
    }

    public static void add(String name) {
        if (!getInstance().enemies.contains(name)) getInstance().enemies.add(name);
    }

    public static void add(Entity entity) {
        if (!getInstance().enemies.contains(entity.getName())) getInstance().enemies.add(entity.getName());
    }

    public static void remove(String name) {
        getInstance().enemies.remove(name);
    }

    public static void remove(Entity entity) {
        getInstance().enemies.remove(entity.getName());
    }

    private static EnemyManager instance;

    public static EnemyManager getInstance() {
        if (instance == null) instance = new EnemyManager();
        return instance;
    }

}
