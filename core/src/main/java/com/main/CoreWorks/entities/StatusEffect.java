package com.main.CoreWorks.entities;

public class StatusEffect {

    private String type;
    private int value;
    private int duration;
    private int currDuration;
    private float reductionMultiplier;

    public StatusEffect(String type, int value, int dur, float reductionMultiplier, boolean immediateAct) {
        this.type = type;
        this.value = value;
        this.duration = dur;
        if (immediateAct) {
            this.currDuration = 0;
        } else {
            this.currDuration = duration;
        }
        this.reductionMultiplier = reductionMultiplier;
    }

    public int tick() {
        if (currDuration == 0) {
            int oldValue = value;
            value = (int) (value * reductionMultiplier);
            currDuration = duration;
            return oldValue;
        } else {
            currDuration--;
            return 0;
        }
    }

    public String getType() {
        return type;
    }

    public void addValue(int delta) {
        this.value += delta;
    }

    public int getValue() {
        return value;
    }

    public int getCurrDuration() {
        return currDuration;
    }

    public float getReductionMultiplier() {
        return reductionMultiplier;
    }
}
