package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.*;

public class MineMultUpgrade extends UpgradeAspect{
    public MineMultUpgrade(float value) {
        super(value, "Mine Multiplier +");
    }

    @Override
    public void execute(Building b) {
        if (b instanceof Miner) {
            ((Miner) b).addMineMult(value);
        }
    }

    @Override
    public boolean tryExecute(Building b) {
        if (b instanceof Miner) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Array<String> changes(Building b) {
        Array<String> arr = new Array<>();
        arr.add("Mine Multiplier");
        if (b instanceof Miner) {
            arr.add( String.valueOf(((Miner) b).getMineMultiplier()) );
            arr.add( String.valueOf(((Miner) b).getMineMultiplier() + (int) value) );
        } else {
            arr.add("Not Applicable");
        }
        return arr;
    }
}
