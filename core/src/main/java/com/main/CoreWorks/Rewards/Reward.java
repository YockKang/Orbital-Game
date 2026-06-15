package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.RunPersistence.RunState;

public abstract class Reward {
    protected String name;
    protected String description;

    public Reward(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract boolean needTarget();

    public abstract void apply(RunState runState);
}
