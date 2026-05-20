package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.Move;

public class Miner extends Building {

    protected int mineMultiplier;

    public Miner(int coolDown, boolean[][] shape, int mineMult, String name) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "miner");
        this.mineMultiplier = mineMult;
    }

    public Miner(int coolDown, boolean[][] shape, int mineMult, String name, Recipe rec) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "miner");
        this.mineMultiplier = mineMult;
        this.recipe = rec;
        setRecipe(rec);
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name)
            .append('\n')
            .append(recipe)
            .append("\nOutput Buffer ")
            .append(outputBuffer)
            .toString();
    }

    @Override
    public Move updateTick() {
        if (currCooldown >= cooldownTimer) {
            boolean mineSuccess = mine();
            if (mineSuccess) {
                currCooldown = 0;
            }
        } else {
            currCooldown++;
        }
        return null;
    }

    public boolean mine() {
        Array<Integer> mults = this.recipe.getOutputMultipliers();
        // attempt to extract all into buffers
        for (int i = 0; i < mults.size; i++) {
            ResourceBuffer currentBuffer = this.outputBuffer.get(i);
            int expected = currentBuffer.getCurrent() + mults.get(i) * this.mineMultiplier;
            // buffer for that type will overfill, cannot mine
            if (!currentBuffer.tryAdd(expected)) {
                return false;
            }
        }
        for (int i = 0; i < mults.size; i++) {
            ResourceBuffer currentBuffer = this.outputBuffer.get(i);
            currentBuffer.add(mults.get(i) * this.mineMultiplier);
        }
        return true;
    }
}
