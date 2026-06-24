package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Upgrade.Upgrade;
import com.main.CoreWorks.RunPersistence.RunState;

public class AddUpgradeReward extends Reward{
    protected Upgrade upgrade;
    protected Building building;

    public AddUpgradeReward(Upgrade upg) {
        super("Upgrade a building", String.format("Give a building \n %s", upg.display()));
        this.upgrade = upg;
    }

    public AddUpgradeReward(String desc, Upgrade upg) {
        super("Upgrade a building", desc);
        this.upgrade = upg;
    }

    @Override
    public boolean needTarget() {
        return true;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    @Override
    public void apply(RunState runState) {
        upgrade.execute(building);
    }

    public void setTarget(Building building) {
        this.building = building;
    }
}
