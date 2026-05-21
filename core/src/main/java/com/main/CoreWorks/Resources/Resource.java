package com.main.CoreWorks.Resources;

import com.badlogic.gdx.utils.JsonValue;

public class Resource {
    String id;
    String name;
    float dmgMult;

    // possibly more multipliers?

    public Resource (String idIn, String nameIn, float dmg) {
        id = idIn;
        name = nameIn;
        dmgMult = dmg;
    }

    public Resource (JsonValue data) {
        id = data.getString("id");
        name = data.getString("Name");
        dmgMult = data.getFloat("dmgMult");
    }


    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public float getDmgMult() {
        return dmgMult;
    }
}
