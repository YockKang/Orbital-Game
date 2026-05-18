package com.main.CoreWorks.database;

import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.moveset.DamageMove;
import com.main.CoreWorks.moveset.HealMove;

public class EnemyDatabase {

    public static Enemy createMissileDrone() {
        Enemy enemy = new Enemy(10, 0, "Missile Drone", 3);
        enemy.addMove(new DamageMove(2, 500));
        return enemy;
    }

    public static Enemy createShieldDrone() {
        Enemy enemy = new Enemy(5, 5, "Shield Drone", 3);
        enemy.addMove(new HealMove(2, 500));
        return enemy;
    }

    public static Enemy createAnnoyingDrone() {
        Enemy enemy = new Enemy(10, 5, "Annoying Drone", 3);
        enemy.addMove(new HealMove(2, 500));
        enemy.addMove(new DamageMove(2, 500));
        return enemy;
    }
}
