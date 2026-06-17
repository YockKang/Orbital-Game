package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Upgrade.Upgrade;
import com.main.CoreWorks.RunPersistence.RunState;

public class AddFixedUpgradeReward extends AddUpgradeReward{
    protected Building building;

    public AddFixedUpgradeReward(Building bldg, Upgrade upg) {
        super(String.format("Give %s %s", bldg.displayName(), upg.display()), upg);
        this.building = bldg;
        this.upgrade = upg;
    }

    @Override
    public void apply(RunState runState) {
        upgrade.execute(building);
    }
}
