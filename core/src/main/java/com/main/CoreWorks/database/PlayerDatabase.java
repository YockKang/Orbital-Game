package com.main.CoreWorks.database;

import com.main.CoreWorks.entities.Player;

public class PlayerDatabase {

    public static Player createEngineer() {
        Player player = new Player(50,0, "Engineer");
        // Add starting inventory buildings here
        // Add starting relics
        // Add any passives here
        player.addBuilding(BuildingDatabase.getBuilding("miner1"));
        player.addBuilding(BuildingDatabase.getBuilding("miner2"));
        player.addBuilding(BuildingDatabase.getBuilding("defender1"));
        player.addBuilding(BuildingDatabase.getBuilding("OPshooter"));
        return player;
    }

    // More player types TBD
}
