package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Upgrade.Upgrade;
import com.main.CoreWorks.RunPersistence.RunState;

public class AddUpgradeReward extends Reward{
    protected Upgrade upgrade;

    public AddUpgradeReward(Upgrade upg) {
        super("Upgrade a building", String.format("Give a building %s", upg.display()));
        this.upgrade = upg;
    }

    public AddUpgradeReward(String desc, Upgrade upg) {
        super("Upgrade a building", desc);
        this.upgrade = upg;
    }

    @Override
    public void apply(RunState runState) {
        // idk gotta go to another screen now
    }
}
