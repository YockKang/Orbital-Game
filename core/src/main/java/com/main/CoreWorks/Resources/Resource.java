package com.main.CoreWorks.Resources;

import com.badlogic.gdx.utils.*;

import java.util.Objects;

public class Resource {
    String id;
    String name;
    float dmgMult;
    ObjectMap<String, Modifier> modifiers;

    // possibly more multipliers?

    public Resource(String idIn, String nameIn, float dmg) {
        id = idIn;
        name = nameIn;
        dmgMult = dmg;
        modifiers = new ObjectMap<>();
    }

    public Resource(JsonValue data) {
        id = data.getString("id");
        name = data.getString("Name");
        dmgMult = data.getFloat("DmgMult");
        modifiers = new ObjectMap<>();
        if (data.get("Modifiers") != null) {
            JsonValue mods = data.get("Modifiers");
            if (mods.isArray()) {
                for (JsonValue mod : mods) {
                    String type = mod.getString("type");
                    float val = 0;
                    if (data.get("value") != null) {
                        val = mod.getFloat("value");
                    }
                    String str = null;
                    if (data.get("str") != null && !Objects.equals(mod.getString("str"), "")) {
                        str = mod.getString("str");
                    }
                    Modifier newMod = new Modifier(type, val, str);
                    addModifiers(newMod);
                }
            }
        }
    }

    public Resource(JsonValue data, ObjectMap<String, Modifier> inMods) {
        id = data.getString("id");
        name = data.getString("Name");
        dmgMult = data.getFloat("DmgMult");
        modifiers = new ObjectMap<>();
        if (data.get("Modifiers") != null) {
            JsonValue mods = data.get("Modifiers");
            if (mods.isArray()) {
                for (JsonValue mod : mods) {
                    String type = mod.getString("type");
                    float val = 0;
                    if (data.get("value") != null) {
                        val = mod.getFloat("value");
                    }
                    String str = null;
                    if (data.get("str") != null && !Objects.equals(mod.getString("str"), "")) {
                        str = mod.getString("str");
                    }
                    Modifier newMod = new Modifier(type, val, str);
                    addModifiers(newMod);
                }
            }
        }
        addModifiers(inMods);
    }

    @Override
    public String toString() {
        return "Resource: " + name;
    }

    public String getId() {
        return id;
    }

    public float getDmgMult() {
        return dmgMult;
    }

    public ObjectMap<String, Modifier> getModifiers() {
        return modifiers;
    }

    public void addModifiers(Modifier m) {
        modifiers.put(m.getType(), m);
    }

    public void addModifiers(ObjectMap<String, Modifier> m) {
        modifiers.putAll(m);
    }

}
