package com.main.CoreWorks.database;

import com.main.CoreWorks.Resources.Resource;

import java.util.HashMap;
import java.util.Map;

/*
Starter Resources
- IronOre
- IronIngot
- CannonBall
 */

public class ResourceDatabase {
    private static final Map<String, Resource> ResourceDB = new HashMap<>();

    public static Resource register(String id, String name, double dmgMult) {
        Resource type = new Resource(id, name, dmgMult);
        ResourceDB.put(id, type);
        return type;
    }

    public static Resource get(String id) {
        return ResourceDB.get(id);
    }
}
