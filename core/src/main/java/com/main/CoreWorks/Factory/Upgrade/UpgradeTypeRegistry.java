package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.*;

public class UpgradeTypeRegistry {
    private static final ObjectMap<String, Array<String>> UpgradeTypeReg = new ObjectMap<>();


    public static void register(String type, Array<String> data) {
        UpgradeTypeReg.put(type, data);
    }

    public static Array<String> get(String id) {
        return UpgradeTypeReg.get(id);
    }

    public static String showDB(){
        return UpgradeTypeReg.toString();
    }

    public static void registerDefault() {
        register("Generic", new Array<>(new String[]{"FlatSpeed", "SpeedMult", "Buffer"}));
        register("Miner", new Array<>(new String[]{"FlatSpeed", "SpeedMult", "Buffer", "MineMult", "ResourceMod"}));
        register("Shooter", new Array<>(new String[]{"FlatSpeed", "SpeedMult", "Buffer", "BaseDamage", "DamageMult"}));
        register("Defender", new Array<>(new String[]{"FlatSpeed", "SpeedMult", "Buffer"}));
        register("Refiner", new Array<>(new String[]{"FlatSpeed", "SpeedMult", "Buffer", "ResourceMod"}));
        ObjectSet<String> allTypes = new ObjectSet<>();
        UpgradeTypeReg.values().forEach(allTypes::addAll);
        Array<String> array = allTypes.iterator().toArray();
        array.sort();
        register("Random", array);
    }
}
