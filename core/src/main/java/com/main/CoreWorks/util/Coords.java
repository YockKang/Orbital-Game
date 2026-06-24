package com.main.CoreWorks.util;

public class Coords {
    /*
    Helper Coordinate class to easily store x and y coordinates (something like Pair class from lectures)
     */

    public final int x;
    public final int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public DirectedCoords addDirection(int dir) {
        return new DirectedCoords(x, y, dir);
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }
}


