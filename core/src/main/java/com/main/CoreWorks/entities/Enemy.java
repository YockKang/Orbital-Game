package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.moveset.*;

public class Enemy extends Character {

    private Array<Move> moveset = new Array<>();
    private int moveTimer;
    private int currMoveIndex = 0;
    private float multiplier;

    public Enemy(int hp, int shield, String name, int gracePeriod) {
        super(hp, shield, name);
        this.moveTimer = gracePeriod;
    }

    public Enemy(JsonValue data, float multiplierIn) {
        super(data, multiplierIn);
        if (multiplierIn == 0) {
            multiplierIn = 1;
        }
        this.multiplier = multiplierIn;
        JsonValue moves = data.get("Moveset");
        for (JsonValue mv : moves) {
            try {
                String type = mv.getString("Type");
                int value = (int) (mv.getInt("Value") * multiplier);
                int charge = (int) Math.ceil(mv.getFloat("Charge") / multiplier);
                switch (type) {
                    case "Damage":
                        addMove(new DamageMove(value, charge));
                        break;
                    case "Heal":
                        addMove(new HealMove(value, charge));
                        break;
                    case "Shield":
                        addMove(new ShieldMove(value, charge));
                        break;
                    case "Disable":
                        addMove(new DisableBuildingMove(value, charge));
                        break;
                }
            } catch (Exception ignored) {
            }
        }

        if (data.get("GracePeriod") != null) {
            this.moveTimer = data.getInt("GracePeriod");
        } else {
            this.moveTimer = 0;
        }
    }

    public void addMove(Move move) {
        moveset.add(move);
    }

    public String displayIntent() {
        return String.format("Next move in %s ticks \n %s", this.moveTimer, moveset.get(currMoveIndex).toString());
    }

    public Move tick() {
        if (hp <= 0) {
            return null;
        }
        if (moveTimer > 0) {
            moveTimer--;
            return null;
        }
        Move move = moveset.get(currMoveIndex);

        currMoveIndex = (currMoveIndex + 1) % moveset.size;
        moveTimer = moveset.get(currMoveIndex).getChargeTime();
        return move;
    }

    public void tick(Character target) {
        if (moveTimer > 0) {
            moveTimer--;
            return;
        }
        Move move = moveset.get(currMoveIndex);
        move.execute(target);

        currMoveIndex = (currMoveIndex + 1) % moveset.size;
        moveTimer = moveset.get(currMoveIndex).getChargeTime();
    }

    public void tick(Building target) {
        if (moveTimer > 0) {
            moveTimer--;
            return;
        }
        Move move = moveset.get(currMoveIndex);
        move.execute(target);

        currMoveIndex = (currMoveIndex + 1) % moveset.size;
        moveTimer = moveset.get(currMoveIndex).getChargeTime();
    }

    @Override
    public String toString() {
        return String.format("%s \n %s \n", super.toString(), this.displayIntent());
    }

    public Move getMove() {
        return moveset.get(currMoveIndex);
    }

    public int getMoveTimer() {
        return moveTimer;
    }
}
