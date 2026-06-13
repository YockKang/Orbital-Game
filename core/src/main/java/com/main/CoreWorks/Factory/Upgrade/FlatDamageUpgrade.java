package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Shooter;

public class FlatDamageUpgrade extends UpgradeAspect {
    public FlatDamageUpgrade(int value) {
        super((float) value, "Flat Damage +");
    }

    @Override
    public void execute(Building b) {
        if (b instanceof Shooter) {
            ((Shooter) b).changeFlatDamage(value);
        }
    }

    @Override
    public boolean tryExecute(Building b) {
        if (b instanceof Shooter) {
            return true;
        } else {
            return false;
        }
    }
}
