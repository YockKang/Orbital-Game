package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;

public class SpeedMultUpgrade  extends UpgradeAspect {
    public SpeedMultUpgrade(float value) {
        super(value);
    }

    @Override
    public void execute(Building b) {
        b.addSpeedMult((int) value);
    }

    @Override
    public boolean tryExecute(Building b) {
        return true;
    }
}
