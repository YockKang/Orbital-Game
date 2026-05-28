package com.main.CoreWorks.moveset;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.Character;

public class DisableBuildingMove extends Move {

    private int duration;

    public DisableBuildingMove(int dur, int chargeTime) {
        super("Disable Building", "May disable a random building for " + dur + " ticks", chargeTime);
        this.duration = dur;
    }

    @Override
    public void execute(Character target) {
        // Do nothing
    }

    @Override
    public void execute(Building target) {
        if (target != null) {
            target.disableFor(duration);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int getValue() {
        return this.duration;
    }
}
