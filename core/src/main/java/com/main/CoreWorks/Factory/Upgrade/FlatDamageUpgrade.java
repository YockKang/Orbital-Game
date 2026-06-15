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
    public String changes(Building b) {
        StringBuilder str = new StringBuilder().append("Flat Damage: ");
        if (b instanceof Shooter) {
            str.append( ((Shooter) b).getFlatDmg() )
                .append(" -> ")
                .append( ((Shooter) b).getFlatDmg() + (int) value );
        } else {
            str.append("Not Applicable");
        }
        return str.toString();
    }
}
