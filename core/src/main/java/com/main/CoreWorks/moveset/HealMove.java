package com.main.CoreWorks.moveset;

import com.main.CoreWorks.entities.Character;

public class HealMove extends Move {

    private int heal;

    public HealMove(int heal, int chargeTime) {
        super("Heal", "Heals " + heal + " health", chargeTime);
        this.heal = heal;
    }

    @Override
    public void execute(Character target) {
        target.heal(heal);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
