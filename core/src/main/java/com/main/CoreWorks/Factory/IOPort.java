package com.main.CoreWorks.Factory;

public class IOPort {
    protected int xCoord;
    protected int yCoord;
    protected int dir;
    protected int speedMult;
    protected Building target;
    protected boolean outputFull = false;

    public IOPort(int x, int y, int d, int speed) {
        this.xCoord = x;
        this.yCoord = y;
        this.dir = d;
        this.speedMult = speed;
    }

    public int getX() {
        return xCoord;
    }

    public int getY() {
        return yCoord;
    }

    public int getDir() {
        return dir;
    }

    public int getSpeed() {
        return speedMult;
    }

    public void setTarget(Building b) {
        this.target = b;
    }

    public void setOutputFull() {
        this.outputFull = true;
    }

    public void disableOutputFull() {
        this.outputFull = false;
    }

    public boolean checkOutputFull() {
        return this.outputFull;
    }
}
