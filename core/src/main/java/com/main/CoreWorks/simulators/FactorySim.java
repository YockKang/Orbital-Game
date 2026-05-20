package com.main.CoreWorks.simulators;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.moveset.Move;

public class FactorySim {

    private FactoryGrid grid;
    private Queue<Move> pendingMoves = new Queue<>();

    public FactorySim(FactoryGrid grid) {
        this.grid = grid;
    }

    public void advanceTick() {
        pendingMoves.clear();
        Array<Building> buildings = grid.getBuildings();

        // Deterministic sort of buildings to determine which order to process the moves implemented here

        for (Building building : buildings) {

        }

        // Grid will handle the resource transfer via method call here
    }

    public Queue<Move> returnMoves() {
        Queue<Move> result = new Queue<>();
        while (pendingMoves.size > 0) {
            result.addLast(pendingMoves.removeFirst());
        }
        return result;
    }
}
