package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.moveset.*;

public class Refiner extends Building{

    boolean isCrafting = false;

    public Refiner(int coolDown, boolean[][] shape, int mineMult, String name) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "refiner");
    }

    public Refiner(int coolDown, boolean[][] shape, int mineMult, String name, Recipe rec) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "refiner");
        this.recipe = rec;
        setRecipe(rec);
    }

    public Refiner(JsonValue data) {
        super(data);
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name)
            .append("\nOnGrid: ").append(onGrid)
            .append("\nSpeedMult ")
            .append(speedMultiplier)
            .append("\nInput Buffer ")
            .append(inputBuffer)
            .append("\nCrafting\n")
            .append(recipe)
            .append("\nCrafting?: ")
            .append(isCrafting).append(", ").append(currCooldown).append("/").append(cooldownTimer)
            .append("\nOutput Buffer ")
            .append(outputBuffer)
            .toString();
    }

    @Override
    public Array<Move> updateEnabled() {
        if (!isCrafting) {
            if (tryStartCraft()) {
                isCrafting = true;
                startCraft();
            }
        } else {
            currCooldown += getSpeed();
            if (currCooldown >= cooldownTimer) {
                boolean craftSuccess = tryEndCraft();
                if (craftSuccess) {
                    endCraft();
                    currCooldown -= cooldownTimer;
                    if (!tryStartCraft()) {
                        isCrafting = false;
                        currCooldown = 0;
                    } else {
                        startCraft();
                    }
                } else {
                    currCooldown = cooldownTimer - getSpeed();
                }
            }
        }
        return null;
    }

    public boolean tryStartCraft() {
        if (this.recipe == null) {
            return false;
        } else {
            Array<Integer> mults = this.recipe.getInputMultipliers();
            // check if enough to start crafting
            for (int i = 0; i < mults.size; i++) {
                if (!this.inputBuffer.get(i).tryDraw(mults.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public void startCraft() {
        Array<Integer> mults = this.recipe.getInputMultipliers();
        for (int i = 0; i < mults.size; i++) {
            this.inputBuffer.get(i).draw(mults.get(i));
        }
    }

    public boolean tryEndCraft() {
        System.out.println("try end");
        if (this.recipe == null) {
            return false;
        } else {
            Array<Integer> mults = this.recipe.getOutputMultipliers();
            for (int i = 0; i < mults.size; i++) {
                if (!this.outputBuffer.get(i).tryAdd(mults.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public void endCraft() {
        Array<Integer> mults = this.recipe.getOutputMultipliers();
        for (int i = 0; i < mults.size; i++) {
            this.outputBuffer.get(i).add(mults.get(i));
        }
    }

    @Override
    public void clear() {
        super.clear();
        isCrafting = false;
    }
}
