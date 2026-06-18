package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Upgrade.Upgrade;
import com.main.CoreWorks.RunPersistence.RunState;

public class AddFixedUpgradeReward extends Reward{
    protected Upgrade upgrade;
    protected Building building;

    public AddFixedUpgradeReward(Building bldg, Upgrade upg) {
        super("Upgrade a building", String.format("Give %s %s", bldg.displayName(), upg.display()));
        this.building = bldg;
        this.upgrade = upg;
    }

    @Override
    public void apply(RunState runState) {
        upgrade.execute(building);
    }

    @Override
    public boolean needTarget() {
        return true;
    }
}
