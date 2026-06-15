package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
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

    @Override
    public Array<String> changes(Building b) {
        Array<String> arr = new Array<>();
        arr.add("Flat Speed");
        arr.add( String.valueOf( b.getSpeedFlat()) );
        arr.add( String.valueOf( b.getSpeedFlat() + value) );
        return arr;
    }
}
