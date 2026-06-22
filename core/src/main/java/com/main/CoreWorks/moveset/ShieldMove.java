package com.main.CoreWorks.moveset;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.Character;

public class ShieldMove extends Move{
    private int shield;

    public ShieldMove(int shield, int chargeTime) {
        super("Shield", "Shield " + shield + " hp", chargeTime);
        this.shield = shield;
    }

    @Override
    public void execute(Character target) {
        target.gainShield(shield);
    }

    @Override
    public void execute(Building target) {
        // Do nothing
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int getValue() {
        return this.shield;
    }
}
