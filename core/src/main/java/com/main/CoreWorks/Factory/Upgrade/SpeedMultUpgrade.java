package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;

public class SpeedMultUpgrade  extends UpgradeAspect {
    public SpeedMultUpgrade(float value) {
        super(value, "Speed Multiplier +");
    }

    @Override
    public void execute(Building b) {
        b.addSpeedMult(value);
    }

    @Override
    public boolean tryExecute(Building b) {
        return true;
    }

    @Override
    public String changes(Building b) {
        return new StringBuilder()
            .append("Speed Multiplier: ")
            .append( b.getSpeedMult() )
            .append(" -> ")
            .append( b.getSpeedMult() + value )
            .toString();
    }
}
