package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.moveset.Move;

public class Enemy extends Character {

    private Array<Move> moveset = new Array<>();
    private int moveTimer;
    private int currMoveIndex = 0;

    public Enemy(int hp, int shield, String name, int gracePeriod) {
        super(hp, shield, name);
        this.moveTimer = gracePeriod;
    }

    public void addMove(Move move) {
        moveset.add(move);
    }

    public String displayIntent() {
        return String.format("Next move in %s ticks: %s", this.moveTimer, moveset.get(currMoveIndex).toString());
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
}
