package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.database.BuildingDatabase;

public class Player extends Character{

    private Array<Building> inventory;
    // Relics Implementation TBD
    // Passives Implementation TBD

    public Player(int hp, int shield, String name) {
        super(hp, shield, name);
        this.inventory = new Array<>();
        this.inventory.add(BuildingDatabase.getBuilding("miner1"));
        this.inventory.add(BuildingDatabase.getBuilding("miner2"));
        this.inventory.add(BuildingDatabase.getBuilding("defender1"));
        this.inventory.add(BuildingDatabase.getBuilding("shooter1"));
        this.inventory.add(BuildingDatabase.getBuilding("refiner1"));
        this.inventory.add(BuildingDatabase.getBuilding("refiner2"));
    }

    public void addBuilding(Building building) {
        inventory.add(building);
    }

    public boolean removeBuilding(Building building) {
        return inventory.removeValue(building, true);
    }

    public Array<Building> getInventory() {
        return inventory;
    }

    public Building getBuildingAt(int index) {
        return inventory.get(index);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
