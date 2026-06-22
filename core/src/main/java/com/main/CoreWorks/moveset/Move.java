package com.main.CoreWorks.moveset;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.Character;

public abstract class Move {
    protected String name;
    protected String description;
    protected int chargeTime;
    protected int target = 0;
    protected boolean randomTarget = false;

    public Move(String name, String description, int chargeTime) {
        this.name = name;
        this.description = description;
        this.chargeTime = chargeTime;
    }

    @Override
    public String toString() {
        return String.format("Name: %s \n %s \nCooldown: %s ticks", this.name, this.description, this.chargeTime);
    }

    public int getChargeTime() {
        return this.chargeTime;
    }

    public abstract void execute(Character target);

    public abstract void execute(Building target);

    public abstract int getValue();

    public int getTarget() {return target;}

    public boolean getRandomTarget() {return randomTarget;}
}
