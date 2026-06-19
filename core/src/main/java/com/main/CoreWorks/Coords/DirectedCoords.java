package com.main.CoreWorks.Coords;


public class DirectedCoords extends Coords {
    public final int dir;

    public DirectedCoords(int x, int y, int direction) {
        super(x, y);
        this.dir = direction % 4;
    }

    public Coords pointingTo() {
        int newX = x;
        int newY = y;
        switch (dir) {
            case 0 -> {
                newY--;
            }
            case 1 -> {
                newX++;
            }
            case 2 -> {
                newY++;
            }
            case 3 -> {
                newX--;
            }
        }
        return new Coords(newX, newY);
    }

    public DirectedCoords pointingToSide() {
        Coords newCoords = pointingTo();
        return new DirectedCoords(newCoords.x, newCoords.y, (dir + 2) % 4);
    }

    @Override
    public String toString() {
        String trueDir = "";
        switch (dir) {
            case 0:
                trueDir = "up";
                break;
            case 1:
                trueDir = "right";
                break;
            case 2:
                trueDir = "down";
                break;
            case 3:
                trueDir = "left";
                break;
        }
        return "x: " + x + " y: " + y + " direction " + dir + "(" + trueDir + ")";
    }
}
