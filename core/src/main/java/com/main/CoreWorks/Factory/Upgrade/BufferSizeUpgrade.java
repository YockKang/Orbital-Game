package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;

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
    public String changes(Building b) {
        return new StringBuilder()
            .append("Buffer Size: ")
            .append( b.getCapacityMult() )
            .append(" -> ")
            .append( b.getCapacityMult() + value )
            .toString();
    }
}
