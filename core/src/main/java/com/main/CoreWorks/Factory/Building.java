package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.Move;

public abstract class Building {


    // confirmed fields
    protected boolean isEnabled = true;
    protected boolean onGrid = false;
    protected int cooldownTimer;
    protected int currCooldown = 0;
    protected Array<ResourceBuffer> inputBuffer;
    protected Array<ResourceBuffer> outputBuffer;
    protected int capacityMult = 5;
    protected int xCoord = -1; // bottom is 0
    protected int yCoord = -1; // left is 0
    protected int rotation = 0; // 0 is "up", +1 for clockwise rotation
    protected String name;
    protected Recipe recipe = null;
    protected Queue<IOPort> ports;

    protected boolean[][] shape;
    /*
    SHAPE GUIDE
    stores the shape that is "up"

    e.g. 2 by 2 L

    {{true, true},
     {true, false}}
     */

    // ?? fields
    protected int HP;

    public Building(int coolDown,
                    Array<ResourceBuffer> inputs,
                    Array<ResourceBuffer> outputs,
                    boolean[][] shape,
                    String nameIn) {
        cooldownTimer = coolDown;
        inputBuffer = inputs;
        outputBuffer = outputs;
        name = nameIn;
        this.shape = shape;
    }

    @Override
    public String toString() {
        return name;
    }

    protected int[] getGlobalCoord(int x , int y) {
        int shapeW = shape[0].length;
        int shapeH = shape.length;
        int globalX = 0;
        int globalY = 0;

        switch (rotation) {
            case 0 -> {
                globalX = x;
                globalY = y;
            }
            case 1 -> {
                globalX = y + yCoord;
                globalY = shapeW - 1 - x;
            }
            case 2 -> {
                globalX = shapeW - 1 - x;
                globalY = shapeH - 1 - y;
            }
            case 3 -> {
                globalX = shapeH - 1 - y;
                globalY = x;
            }
        }

        globalX += xCoord;
        globalY += yCoord;
        return new int[] {globalX, globalY};
    }

    /*
    public boolean resourceTransfer(Array<Array<Building>> grid) {
        // resource transfer is "pushy", suppliers will push to consumers

        Array<Resource> outputs = recipe.getOutputs();

        for (IOPort p : ports) {
            p.disableOutputFull();
            int[] targetCoord = getGlobalCoord(p.getX(), p.getY());
            int portGlobalDir = (p.getDir() + rotation) % 4;

            switch (portGlobalDir) {
                case 0 -> {
                    targetCoord[0]++;
                }
                case 1 -> {
                    targetCoord[1]++;
                }
                case 2 -> {
                    targetCoord[0]--;
                }
                case 3 -> {
                    targetCoord[1]--;
                }
            }
            Building target = grid.get(targetCoord[1]).get(targetCoord[0]);
            p.setTarget(target);
        }

        for (int i = 0; i < outputBuffer.size; i++) {

        }
        return false;
    }
     */


    public void setRecipe(Recipe rec) {
        // write new recipe
        this.recipe = rec;

        // grab new inputs
        Array<Resource> inputs = this.recipe.getInputs();
        Array<Integer> inputMults = this.recipe.getInputMultipliers();
        // reset queues
        this.inputBuffer.clear();
        for (int i  = 0; i < inputs.size; i++) {
            this.inputBuffer.add(new ResourceBuffer(inputs.get(i), capacityMult * inputMults.get(i)));
        }
        this.inputBuffer.shrink();

        // grab new outputs
        Array<Resource> outputs = this.recipe.getOutputs();
        Array<Integer> outputMults = this.recipe.getOutputMultipliers();
        // reset queues
        this.outputBuffer.clear();
        for (int i  = 0; i < outputs.size; i++) {
            this.outputBuffer.add(new ResourceBuffer(outputs.get(i), capacityMult * outputMults.get(i)));
        }
        this.outputBuffer.shrink();
    }

    public abstract Move updateTick();

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
        for (ResourceBuffer b : inputBuffer) {
            b.setCurrent(0);
        }
        for (ResourceBuffer b : outputBuffer) {
            b.setCurrent(0);
        }
        currCooldown = 0;
    }

    public void setPos(int x, int y) {
        xCoord = x;
        yCoord = y;
    }

    public int getX() {
        return xCoord;
    }

    public int getY() {
        return yCoord;
    }

    public boolean[][] getShape() {
        return shape;
    }

    public void setRotation(int rot) {
        rotation = rot;
    }

    public int getRotation() {
        return rotation;
    }

    public void putOnGrid() {
        onGrid = true;
    }

    public void takeOffGrid() {
        onGrid = false;
    }

    public void addPort(int x, int y, int dir, int speed) {
        IOPort port = new IOPort(x, y, dir, speed);
        ports.addLast(port);
    }

    public void addPort(IOPort port) {
        ports.addLast(port);
    }


    public void clearPorts() {
        ports.clear();
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
