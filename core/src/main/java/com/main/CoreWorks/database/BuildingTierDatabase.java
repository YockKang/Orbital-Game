package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;

import java.util.Random;

public class BuildingTierDatabase {
    private static final ObjectMap<Integer, Array<String>> BuildingTierDB = new ObjectMap<>();

    public static void register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(BuildingTierDatabase::register);
        } else {
            String id = data.getString("id");
            int tier = data.getInt("Tier");
            if (!BuildingTierDB.containsKey(tier)) {
                BuildingTierDB.put(tier, new Array<>());
            }
            BuildingTierDB.get(tier).add(id);
        }
    }

    public static Building getRandomBuilding(Random random) {
        int key = random.nextInt(BuildingTierDB.size - 1);
        Array<String> tier = null;
        int i = 0;
        for (Array<String> value : BuildingTierDB.values()) {
            if (i++ == key) {
                tier = value;
                break;
            }
        }
        if (tier == null) {
            return null;
        } else {
            return BuildingDatabase.getBuilding(tier.get(random.nextInt(tier.size - 1)));
        }
    }

    public static Building getRandomBuilding(Random random, int tierIn) {
        Array<String> tier = BuildingTierDB.get(tierIn);
        if (tier == null) {
            return null;
        } else if (tier.size == 1) {
            return BuildingDatabase.getBuilding(tier.get(0));
        } else {
            return BuildingDatabase.getBuilding(tier.get(random.nextInt(tier.size - 1)));
        }
    }

    public static Array<String> getBuildingTier(int tier) {
        return BuildingTierDB.get(tier);
    }

    public static String showDB(){
        return BuildingTierDB.toString();
    }

}
