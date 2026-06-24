package com.main.CoreWorks.Resources;

import com.badlogic.gdx.utils.*;

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
    }

    public Resource(JsonValue data, ObjectMap<String, Modifier> mods) {
        id = data.getString("id");
        name = data.getString("Name");
        dmgMult = data.getFloat("DmgMult");
        modifiers = mods;
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
