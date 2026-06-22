package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.*;

public abstract class Character {
    // Implementation of Status effects TBD
    // Protected modifier just in case subclass has unique gimmicks to modify
    protected int hp;
    protected int shield;
    protected int maxHp;
    protected String name;
    protected ObjectMap<String, StatusEffect> statusEffects = new ObjectMap<>();

    public Character(int hp, int shield, String name) {
        this.hp = hp;
        this.shield = shield;
        this.maxHp = hp;
        this.name = name;
    }

    public Character(JsonValue data, float multiplier) {
        this.maxHp = (int) (data.getInt("HP") * multiplier);
        this.hp = maxHp;
        this.name = data.getString("Name");
        if (data.get("Shield") != null) {
            this.shield = (int) (data.getInt("Shield") * multiplier);
        }
    }

    public void takeDamage(int damage) {
        if (damage >= shield) {
            this.hp = this.hp - (damage - shield);
            this.shield = 0;
        } else {
            this.shield = this.shield - damage;
        }
    }

    public void takeTrueDamage(int damage) {
        this.hp = this.hp - damage;
    }

    public void heal(int healAmt) {
        // Prevents Overheals (Overhealing is not a mechanic)
        if (this.hp + healAmt >= this.maxHp) {
            this.hp = this.maxHp;
        } else {
            this.hp += healAmt;
        }
    }

    public void gainMaxHp(int gain) {
        this.maxHp += gain;
        this.heal(gain);
    }

    public void gainShield(int shieldAmt) {
        this.shield += shieldAmt;
    }

    public int displayCurrentHp() {
        return this.hp;
    }

    public int displayMaxHp() {
        return this.maxHp;
    }

    public int displayShield() {
        return this.shield;
    }

    public String displayName() {
        return this.name;
    }

    public boolean isDead() {
        return this.hp <= 0;
    }

    public void applyStatusEffect(StatusEffect se) {
        if (se.getValue() > 0) {
            if (!statusEffects.containsKey(se.getType())) {
                statusEffects.put(se.getType(), se);
            } else {
                statusEffects.get(se.getType()).addValue(se.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Name: %s \nHP: %s/%s \nShield: %s", this.name, this.hp, this.maxHp, this.shield);
    }

    public ObjectMap<String, StatusEffect> getStatusEffects() {
        return statusEffects;
    }
}
