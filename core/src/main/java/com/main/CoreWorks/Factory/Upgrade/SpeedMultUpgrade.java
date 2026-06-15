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
    public Array<String> changes(Building b) {
        Array<String> arr = new Array<>();
        arr.add("Speed Multiplier");
        arr.add( String.valueOf( b.getSpeedMult()) );
        arr.add( String.valueOf( b.getSpeedMult() + value) );
        return arr;
    }
}
