package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Shooter;

public class BaseDamageUpgrade extends UpgradeAspect{
    public BaseDamageUpgrade(float value) {
        super(value, "Base Damage +");
    }

    @Override
    public void execute(Building b) {
        if (b instanceof Shooter) {
            ((Shooter) b).changeBaseDamage(value);
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

    @Override
    public Array<String> changes(Building b) {
        Array<String> arr = new Array<>();
        arr.add("Base Damage");
        if (b instanceof Shooter) {
            arr.add( String.valueOf(((Shooter) b).getBaseDmg()) );
            arr.add( String.valueOf(((Shooter) b).getBaseDmg() + value) );
        } else {
            arr.add("Not Applicable");
        }
        return arr;
    }
}
