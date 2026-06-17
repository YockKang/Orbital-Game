package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.entities.*;

import java.util.Random;

public class EnemyGroupDatabase {
    private static final ObjectMap<String, Array<Array<EnemyFactory>>> EnemyGroupDB = new ObjectMap<>(3);

    public static Array<Enemy> register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(com.main.CoreWorks.database.EnemyGroupDatabase::register);
            return null;
        } else {
            String tier = data.getString("Tier");
            String[] enemies = data.get("Enemies").asStringArray();
            Array<EnemyFactory> arr = new Array<>();
            for (String e : enemies) {
                arr.add(EnemyDatabase.getEnemyConstructor(e));
            }
            if (EnemyGroupDB.get(tier) == null) {
                EnemyGroupDB.put(tier, new Array<>());
            }
            EnemyGroupDB.get(tier).add(arr);
            Array<Enemy> enemiesArr = new Array<>();
            arr.forEach(enemyFactory -> enemiesArr.add(enemyFactory.of(1)));
            return enemiesArr;
        }
    }

    public static Array<EnemyFactory> getGroupConstructor(int tier, int num) {
        return EnemyGroupDB.get(String.valueOf(tier)).get(num);
    }

    public static Array<Enemy> getGroup(int tier, int num, float multiplier) {
        Array<Enemy> arr = new Array<>();
        EnemyGroupDB.get(String.valueOf(tier)).get(num).forEach(enemyFactory -> arr.add(enemyFactory.of(multiplier)));
        return arr;
    }

    public static Array<Enemy> getGroup(String tier, int num, float multiplier) {
        Array<Enemy> arr = new Array<>();
        EnemyGroupDB.get(tier).get(num).forEach(enemyFactory -> arr.add(enemyFactory.of(multiplier)));
        return arr;
    }

    public static Array<Enemy> getRandomGroup(int tier, float multiplier, Random random) {
        Array<Enemy> arr = new Array<>();
        Array<Array<EnemyFactory>> group = EnemyGroupDB.get(String.valueOf(tier));
        group.get(random.nextInt(group.size)).forEach(enemyFactory -> arr.add(enemyFactory.of(multiplier)));
        return arr;
    }

    public static String showDB(){
        return EnemyGroupDB.toString();
    }
}
