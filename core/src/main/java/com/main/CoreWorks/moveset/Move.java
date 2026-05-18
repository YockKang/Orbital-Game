package com.main.CoreWorks.moveset;

import com.main.CoreWorks.entities.Character;

public abstract class Move {
    protected String name;
    protected String description;
    protected int chargeTime;

    public Move(String name, String description, int chargeTime) {
        this.name = name;
        this.description = description;
        this.chargeTime = chargeTime;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Description: %s, Preparation time: %s", this.name, this.description, this.chargeTime);
    }

    public int getChargeTime() {
        return this.chargeTime;
    }

    public abstract void execute(Character target);
}
