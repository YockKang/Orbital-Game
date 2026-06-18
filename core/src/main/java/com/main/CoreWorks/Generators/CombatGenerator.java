package com.main.CoreWorks.Generators;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.database.EnemyGroupDatabase;
import com.main.CoreWorks.entities.Enemy;

import java.util.Random;

public class CombatGenerator {
    public static Array<Enemy> createCombat(int tier, float multiplier, Random random) {
        return EnemyGroupDatabase.getRandomGroup(tier, multiplier, random);
    }

    public static Array<Enemy> createCombat(String tier, float multiplier, Random random) {
        return EnemyGroupDatabase.getRandomGroup(tier, multiplier, random);
    }
}
