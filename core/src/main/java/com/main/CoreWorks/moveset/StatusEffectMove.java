package com.main.CoreWorks.moveset;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.entities.*;
import com.main.CoreWorks.entities.Character;

public class StatusEffectMove extends Move {

    private StatusEffect effect;

    public StatusEffectMove(String type, int value, int effectDur, float reductMult, boolean instantAct, int chargeTime) {
        super("Status Effect", "Applies " + value + " " + type, chargeTime);
        this.effect = new StatusEffect(type, value, effectDur, reductMult, instantAct);
    }

    public StatusEffectMove(StatusEffect se, int chargeTime) {
        super("Status Effect", "Applies " + se.getValue() + " " + se.getType(), chargeTime);
        this.effect = se;
    }

    @Override
    public void execute(Character target) {
        target.applyStatusEffect(effect);
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
        return this.effect.getValue();
    }

    public StatusEffect getEffect() {
        return this.effect;
    }
}
