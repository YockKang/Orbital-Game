package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.Array;

public abstract class MapNode {
    protected boolean completed;
    protected String name;
    protected int tier;
    protected float rewardMultiplier = 1f;
    protected boolean unlocked;
    // Stores the position of the node for map drawing
    protected float x;
    protected float y;
    // Stores connected Nodes
    protected Array<MapNode> nextNodes = new Array<>();

    public String getName() {
        return this.name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getTier() {
        return tier;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Array<MapNode> getNextNodes() {
        return nextNodes;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public void addNextNode(MapNode node) {
        this.nextNodes.add(node);
    }

    public float getMultiplier() {
        return rewardMultiplier;
    }
}
