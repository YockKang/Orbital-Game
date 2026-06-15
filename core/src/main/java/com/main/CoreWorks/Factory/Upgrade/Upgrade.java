package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;
import org.checkerframework.checker.units.qual.A;

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

    public Array<String> displayChanges(Building b) {
        Array<String> arr = new Array<>();
        upgrades.forEach(ua -> {
            String changes = ua.changes(b);
            if (changes != null) {
                arr.add(changes);
            }
        });
        return arr;
    }

    public String display() {
        StringBuilder strB = new StringBuilder();
        strB.append(upgrades.get(0).display());
        for (int i = 1; i < upgrades.size; i++) {
            strB.append(", ");
            strB.append(upgrades.get(i).display());
        }
        return strB.toString();
    }
}
