package com.main.CoreWorks.Factory;

public abstract class Structure {
    protected int xCoord = -1; // top is 0
    protected int yCoord = -1; // left is 0

    public Structure(int x, int y) {
        xCoord = x;
        yCoord = y;
    }

    public Structure() {}

    @Override
    public String toString() {
        return xCoord + " " + yCoord;
    }

    public int getX() {
        return xCoord;
    }

    public int getY() {
        return yCoord;
    }
}
