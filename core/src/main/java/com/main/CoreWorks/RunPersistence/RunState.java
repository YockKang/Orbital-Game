package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.entities.Player;

import java.util.Random;

public class RunState {
    private Player player;
    private FactoryGrid factoryGrid;
    private RunMap runMap;
    private MapNode currNode;
    private Random random;

    public RunState(Player player, FactoryGrid factoryGrid) {
        this.player = player;
        this.factoryGrid = factoryGrid;
        this.random = new Random();
    }

    public RunState(Player player, FactoryGrid factoryGrid, long seed) {
        this.player = player;
        this.factoryGrid = factoryGrid;
        this.random = new Random(seed);
    }

    public Player getPlayer() {
        return player;
    }

    public FactoryGrid getFactoryGrid() {
        return factoryGrid;
    }

    public RunMap getRunMap() {
        return runMap;
    }

    public void setRunMap(RunMap runMap) {
        this.runMap = runMap;
    }

    public MapNode getCurrNode() {
        return currNode;
    }

    public void setCurrNode(MapNode currNode) {
        this.currNode = currNode;
    }

    public void setFactoryGrid(FactoryGrid factoryGrid) {
        this.factoryGrid = factoryGrid;
    }

    public Array<Building> getOwnedBuildings() {
        Array<Building> arr = new Array<>();
        arr.addAll(factoryGrid.getBuildings());
        arr.addAll(player.getInventory());
        return arr;
    }

    public Random getRandom() {
        return random;
    }
}
