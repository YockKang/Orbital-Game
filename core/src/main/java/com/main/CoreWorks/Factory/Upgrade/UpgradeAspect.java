package com.main.CoreWorks.Factory.Upgrade;

import com.main.CoreWorks.Factory.Building;

public abstract class UpgradeAspect {
    protected float value;
    protected String description;

    public UpgradeAspect(float val, String desc) {
        value = val;
        description = desc;
    }

    public abstract void execute(Building b);

    public String display() {
        return description + value;
    }

    public abstract boolean tryExecute(Building b);
}
