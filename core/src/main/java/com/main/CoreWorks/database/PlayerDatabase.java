package com.main.CoreWorks.database;

import com.main.CoreWorks.entities.Player;

public class PlayerDatabase {

    public static Player createEngineer() {
        Player player = new Player(50,0, "Engineer");
        // Add starting inventory buildings here
        // Add starting relics
        // Add any passives here
        player.addBuilding(BuildingDatabase.getBuilding("Shooter0"));
        player.addBuilding(BuildingDatabase.getBuilding("Healer1"));
        player.addBuilding(BuildingDatabase.getBuilding("BigIronMiner"));
        player.addBuilding(BuildingDatabase.getBuilding("Cannonballer"));
        return player;

    }

    // More player types TBD
}
