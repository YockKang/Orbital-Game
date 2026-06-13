package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;

public class FlatSpeedUpgrade  extends UpgradeAspect{
    public FlatSpeedUpgrade(float value) {
        super(value, "Flat Speed +");
    }

    @Override
    public void execute(Building b) {
        b.addSpeedFlat(value);
    }


    @Override
    public boolean tryExecute(Building b) {
        return true;
    }
}
