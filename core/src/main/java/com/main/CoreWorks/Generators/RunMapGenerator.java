package com.main.CoreWorks.Generators;

import com.main.CoreWorks.RunPersistence.CombatNode;
import com.main.CoreWorks.RunPersistence.RunMap;

import java.util.Random;

public class RunMapGenerator {
    public static RunMap generateRunMap(Random random) {
        // Right now, the runMap is hardcoded for testing, but we plan to eventually get it procedurally generated

        RunMap runMap = new RunMap();

        // Generate all the nodes of the current floor + map position
        CombatNode combat1 = new CombatNode(CombatGenerator.createCombat(1, 0.9f, random), 1, 150, 500);
        CombatNode combat2 = new CombatNode(CombatGenerator.createCombat(1, 1f, random), 1, 450, 500);
        CombatNode combat3 = new CombatNode(CombatGenerator.createCombat(1, 1.1f, random), 1, 750, 500);
        CombatNode combat4 = new CombatNode(CombatGenerator.createCombat(1, 1.5f, random), 1, 1050, 500);

        // Determine how the nodes link to other nodes
        combat1.addNextNode(combat2);
        combat2.addNextNode(combat3);
        combat3.addNextNode(combat4);

        // Adds the nodes to the map
        runMap.addNode(combat1);
        runMap.addNode(combat2);
        runMap.addNode(combat3);
        runMap.addNode(combat4);

        // Set the starting node
        runMap.setStartNode(combat1);

        // Unlock the start node for clicking
        combat1.setUnlocked(true);

        return runMap;
    }
}
