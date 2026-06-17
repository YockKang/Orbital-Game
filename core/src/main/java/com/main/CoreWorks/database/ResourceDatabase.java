package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Resources.Resource;


/*
Starter Resources
- IronOre
- IronIngot
- CannonBall
 */

public class ResourceDatabase {
    private static final ObjectMap<String, Resource> ResourceDB = new ObjectMap<>();

    public static Resource register(String id, String name, float dmgMult) {
        Resource type = new Resource(id, name, dmgMult);
        ResourceDB.put(id, type);
        return type;
    }

    public static Resource register(JsonValue data) {
        if (data.isArray()){
            data.forEach(ResourceDatabase::register);
            return null;
        } else {
            Resource type = new Resource(data);
            ResourceDB.put(data.getString("id"), type);
            return type;
        }
    }

    public static Resource get(String id) {
        return ResourceDB.get(id);
    }

    public static String showDB(){
        return ResourceDB.toString();
    }
}
