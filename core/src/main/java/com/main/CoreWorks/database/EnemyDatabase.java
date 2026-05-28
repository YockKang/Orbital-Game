package com.main.CoreWorks.database;

import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.moveset.DamageMove;
import com.main.CoreWorks.moveset.DisableBuildingMove;
import com.main.CoreWorks.moveset.HealMove;

public class EnemyDatabase {

    public static Enemy createMissileDrone() {
        Enemy enemy = new Enemy(30, 0, "Missile Drone", 3);
        enemy.addMove(new DamageMove(2, 40));
        return enemy;
    }

    public static Enemy createShieldDrone() {
        Enemy enemy = new Enemy(15, 5, "Shield Drone", 3);
        enemy.addMove(new HealMove(2, 40));
        return enemy;
    }

    public static Enemy createAnnoyingDrone() {
        Enemy enemy = new Enemy(20, 5, "Annoying Drone", 3);
        enemy.addMove(new HealMove(2, 40));
        enemy.addMove(new DamageMove(2, 40));
        return enemy;
    }

    public static Enemy createDisablingDrone() {
        Enemy enemy = new Enemy(10, 0, "Disabling Drone", 3);
        enemy.addMove(new DisableBuildingMove(30, 30));
        return enemy;
    }
}
