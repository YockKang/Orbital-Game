package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.*;

public class MineMultUpgrade extends UpgradeAspect{
    public MineMultUpgrade(float value) {
        super(value);
    }

    @Override
    public void execute(Building b) {
        if (b instanceof Miner) {
            ((Miner) b).addMineMult(value);
        }
    }

    @Override
    public boolean tryExecute(Building b) {
        if (b instanceof Miner) {
            return true;
        } else {
            return false;
        }
    }
}
