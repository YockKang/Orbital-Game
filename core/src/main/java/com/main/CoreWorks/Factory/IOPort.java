package com.main.CoreWorks.Factory;

public class IOPort {
    protected int xCoord;
    protected int yCoord;
    protected int dir;
    protected int transferPerTick;
    protected Building target;

    public IOPort(int x, int y, int d, int speed) {
        this.xCoord = x;
        this.yCoord = y;
        this.dir = d;
        this.transferPerTick = speed;
    }

    @Override
    public String toString() {
        return "IOPort("+xCoord+" "+yCoord+"); facing "+dir+"; rate "+transferPerTick;
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
        return transferPerTick;
    }

    public void setTarget(Building b) {
        this.target = b;
    }

}
