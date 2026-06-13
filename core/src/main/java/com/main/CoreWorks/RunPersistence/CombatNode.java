package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.entities.Enemy;

public class CombatNode extends MapNode{
    private Array<Enemy> enemies;

    public CombatNode(Array<Enemy> enemies, int tier, float multiplier, float x, float y) {
        this.enemies = enemies;
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.name = "Combat";
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

}
