package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
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

    @Override
    public Array<String> changes(Building b) {
        Array<String> arr = new Array<>();
        arr.add("Flat Damage");
        if (b instanceof Shooter) {
            arr.add( String.valueOf(((Shooter) b).getFlatDmg()) );
            arr.add( String.valueOf(((Shooter) b).getFlatDmg() + (int) value) );
        } else {
            arr.add("Not Applicable");
        }
        return arr;
    }
}
