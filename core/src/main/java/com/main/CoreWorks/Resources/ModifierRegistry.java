package com.main.CoreWorks.Resources;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.util.*;

import java.util.Random;

public class ModifierRegistry {
    private static final ObjectMap<String, Pair<Float, Array<String>>> ModifierReg = new ObjectMap<>();
    private static final Array<String> keys = new Array<>();

    public static void register(String type, float valScale, Array<String> data) {
        if (!ModifierReg.containsKey(type)) {
            keys.add(type);
        }
        ModifierReg.put(type, new Pair<>(valScale, data));
    }

    public static void register(String type, Pair<Float, Array<String>> data) {
        if (!ModifierReg.containsKey(type)) {
            keys.add(type);
        }
        ModifierReg.put(type, data);
    }

    public static Pair<Float, Array<String>> get(String id) {
        return ModifierReg.get(id);
    }

    public static Pair<String, Pair<Float, Array<String>>> getRandom(Random random) {
        String key = keys.get(random.nextInt(keys.size));
        return new Pair<>(key, ModifierReg.get(key));
    }

    public static String showDB(){
        return ModifierReg.toString();
    }

    public static void registerDefault() {
        register("DamageType", new Pair<>(0f, new Array<>(new String[]{"True", "Poison"})));
        register("ExtraDmg", new Pair<>(1f, null));

    }
}
