package com.main.CoreWorks.moveset;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.Character;

public class TrueDamageMove extends Move {

    private int damage;

    public TrueDamageMove(int damage, int chargeTime) {
        super("Attack", "Deals " + damage + " true damage", chargeTime);
        this.damage = damage;
    }

    @Override
    public void execute(Character target) {
        target.takeTrueDamage(damage);
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
        return this.damage;
    }
}
