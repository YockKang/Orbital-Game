package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.Miner;
import com.main.CoreWorks.Factory.Shooter;

public class BufferSizeUpgrade extends UpgradeAspect{
    public BufferSizeUpgrade(int value) {
        super((float) value, "Buffer +");
    }

    @Override
    public void execute(Building b) {
        b.changeCapacityMult((int) value);
    }

    @Override
    public boolean tryExecute(Building b) {
        return true;
    }

    @Override
    public Array<String> changes(Building b) {
        Array<String> arr = new Array<>();
        arr.add("Buffer Size");
        arr.add( String.valueOf( b.getCapacityMult()) );
        arr.add( String.valueOf( b.getCapacityMult() + (int) value) );
        return arr;
    }
}
