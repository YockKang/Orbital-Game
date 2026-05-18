package com.main.CoreWorks.Buildings;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;

public abstract class Building {


    // confirmed fields
    protected boolean isEnabled = true;
    protected boolean onGrid = false;
    protected int cooldownTimer;
    protected int currCooldown = 0;
    protected int inBufferSize;
    protected int outBufferSize;
    protected Array<Queue<Resource>> inputBuffer;
    protected Array<Queue<Resource>> outputBuffer;
    protected int xCoord = -1; // bottom is 0
    protected int yCoord = -1; // left is 0
    protected int rotation = 0; // 0 is "up"
    protected String name;
    protected boolean[][][] shape;
    protected static int idCount = 0;
    protected int id;
    protected Recipe recipe = null;

    // ?? fields
    protected int HP;

    public Building(int coolDown,
                    int inBuffer,
                    int outBuffer,
                    Array<Queue<Resource>> inputs,
                    Array<Queue<Resource>> outputs,
                    boolean[][][] shape,
                    String nameIn) {
        cooldownTimer = coolDown;
        inBufferSize = inBuffer;
        outBufferSize = outBuffer;
        inputBuffer = inputs;
        outputBuffer = outputs;
        name = nameIn;
        id = idCount;
        idCount++;
    }

    @Override
    public String toString() {
        return name + ' ' + id;
    }

    public void updateTick() {

    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    public void toggleEnable() {
        if (isEnabled) {
            disable();
        } else {
            enable();
        }
    }

    public void clear() {
        for (Queue<Resource> q : inputBuffer) {
            q.clear();
        }
        for (Queue<Resource> q : outputBuffer) {
            q.clear();
        }
        currCooldown = 0;
    }

    public void setPos(int x, int y) {
        xCoord = x;
        yCoord = y;
    }

    public void setRotation(int rot) {
        rotation = rot;
    }

    public void putOnGrid() {
        onGrid = true;
    }

    public void takeOffGrid() {
        onGrid = false;
    }

    /*
	    Bool isEnabled
	    int cooldownTimer
	    int currCooldown
	    Arr inputBuffer [as a queue]
	    Arr outputBuffer [as a queue]
	    int inputLimit
	    int outputLimit
	    int HP?????
	    [x, y] coords
	    String Name
	    int id
	    Something upgrade?
	    [[hw]] shape
	    I/O limitations [TBA]
	    updateTick()
	    enable()
	    disable()
	    toggleEnable()
	    getters()
	    clear()
     */
}
