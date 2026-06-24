package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Resources.Modifier;

public class ResourceModifierUpgrade extends UpgradeAspect {
    Modifier mod;

    public ResourceModifierUpgrade(Modifier mod) {
        super(0, "Add Modifier");
        this.mod = mod;
    }

    @Override
    public void execute(Building b) {
        b.addModifier(mod);
    };

    @Override
    public String changes(Building b) {
        ObjectMap<String, Modifier> bMods = b.getModifiers();
        if (bMods.containsKey(mod.getType())) {
            return new StringBuilder()
                .append("Modifier: ")
                .append(bMods.get(mod.getType()).display())
                .append(" -> ")
                .append(bMods.get(mod.getType()).previewChange(mod).display())
                .toString();
        } else {
            return new StringBuilder()
                .append("Add Modifier: ")
                .append(mod.display())
                .toString();
        }
    }

    @Override
    public String display() {
        return description + " " + mod.display();
    }

    @Override
    public boolean tryExecute(Building b) {return true;}
}

