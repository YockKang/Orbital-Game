package com.main.CoreWorks.RunPersistence;

public class RestNode extends MapNode{

    public RestNode(int tier, float multiplier, float x, float y) {
        this.tier = tier;
        this.rewardMultiplier = multiplier;
        this.x = x;
        this.y = y;
        this.name = "Rest";
    }

}
