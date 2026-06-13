package com.main.CoreWorks.simulators;


public class CombatController {
    private FactorySim factorySim;
    private CombatSim combatSim;

    public CombatController(FactorySim factorySim, CombatSim combatSim) {
        this.factorySim = factorySim;
        this.combatSim = combatSim;
        combatSim.setGrid(factorySim.getGrid());
    }

    public void advanceTick(int tick) {
        if (combatSim.isWin() || combatSim.isLost()) {
            factorySim.clear();
            return;
        }

        // Firstly, tick the factory
        factorySim.advanceTick();

        // Then, transfer the factory actions to combat
        combatSim.enqueueMoves(factorySim.returnMoves());

        // Lastly, resolve combat
        combatSim.advanceTick(tick);
    }

    public boolean isWin() {
        return combatSim.isWin();
    }

    public boolean isLost() {
        return combatSim.isLost();
    }

    public CombatSim getCombatSim() {
        return combatSim;
    }

    public FactorySim getFactorySim() {
        return factorySim;
    }
}
