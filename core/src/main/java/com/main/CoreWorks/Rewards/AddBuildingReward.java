package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.RunPersistence.RunState;

public class AddBuildingReward extends Reward {
    private Building building;

    public AddBuildingReward(Building building) {
        super("Add a building", String.format("Adds 1 %s to your inventory", building.displayName()));
        this.building = building;
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        runState.getPlayer().addBuilding(building);
    }
}
