package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;

public class Upgrade {
    private final Array<UpgradeAspect> upgrades;

    public Upgrade(Array<UpgradeAspect> upgradesIn) {
        upgrades = upgradesIn;
    }

    public boolean anyFail(Building b) {
        for (UpgradeAspect ua : upgrades) {
            if (ua != null && !ua.tryExecute(b)) {
                return true;
            }
        }
        return false;
    }

    public boolean allFail(Building b) {
        for (UpgradeAspect ua : upgrades) {
            if (ua != null && ua.tryExecute(b)) {
                return false;
            }
        }
        return true;
    }

    public void execute(Building b) {
        upgrades.forEach(ua -> { if (ua != null) ua.execute(b); });
    }
}
