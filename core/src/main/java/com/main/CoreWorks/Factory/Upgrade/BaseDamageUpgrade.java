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
    public String changes(Building b) {
        StringBuilder str = new StringBuilder().append("Base Damage");
        if (b instanceof Shooter) {
            str.append( ((Shooter) b).getBaseDmg() )
                .append( ((Shooter) b).getBaseDmg() + value );
        } else {
            str.append("Not Applicable");
        }
        return str.toString();
    }
}
