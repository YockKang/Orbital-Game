package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.moveset.*;

public class Miner extends Building {

    protected float mineMultiplier;

    private static final Array<String> upgradeTags = new Array<>(new String[]{"Speed", "Buffer", "MineMult"});

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

    public Miner(JsonValue data) {
        super(data);
        this.mineMultiplier = data.getInt("MineMult");
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name).append(" #").append(idNum)
            .append("\nSpeed ")
            .append(getSpeed())
            .append("\nOutput Buffer ")
            .append(outputBuffer)
            .toString();
    }

    @Override
    public Array<Move> updateEnabled() {
        currCooldown += getSpeed();
        while (currCooldown >= cooldownTimer) {
            boolean mineSuccess = tryMine();
            if (mineSuccess) {
                mine();
                currCooldown -= cooldownTimer;
            } else {
                currCooldown = cooldownTimer;
                break;
            }
        }
        return null;
    }

    public boolean tryMine() {
        if (this.recipe == null) {
            return false;
        } else {
            Array<Integer> mults = this.recipe.getOutputMultipliers();
            // attempt to extract all into buffers
            for (int i = 0; i < mults.size; i++) {
                ResourceBuffer currentBuffer = this.outputBuffer.get(i);
                // buffer for that type will overfill, cannot mine
                if (!currentBuffer.tryAdd((int) (mults.get(i) * this.mineMultiplier))) {
                    return false;
                }
            }
            return true;
        }
    }

    public void mine() {
        Array<Integer> mults = this.recipe.getOutputMultipliers();
        for (int i = 0; i < mults.size; i++) {
            ResourceBuffer currentBuffer = this.outputBuffer.get(i);
            currentBuffer.add((int) (mults.get(i) * this.mineMultiplier));
        }
    }

    public void addMineMult(float delta) {
        mineMultiplier += delta;
    }
}
