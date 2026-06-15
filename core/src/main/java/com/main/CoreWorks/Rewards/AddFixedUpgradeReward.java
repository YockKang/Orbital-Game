package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Upgrade.*;
import com.main.CoreWorks.RunPersistence.RunState;

public class AddFixedUpgradeReward extends Reward{
    protected Building building;
    protected Upgrade upgrade;

    public AddFixedUpgradeReward(Building bldg, Upgrade upg) {
        super("Upgrade a building", String.format("Give %s %s", bldg.displayName(), upg.display()));
        this.building = bldg;
        this.upgrade = upg;
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        upgrade.execute(building);
    }
}
