package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;
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
}
