package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;

public class FactoryGrid {
    Array<Array<Building>> grid;
    Array<Building> buildingList = new Array<>();

    int maxHeight;
    int maxWidth;


    public FactoryGrid(int h, int w) {
        this.maxHeight = h;
        this.maxWidth = w;
        grid = new Array<>(h);
        for (int y = 0; y < h; y++) {
            Array<Building> row = new Array<>(w);
            for (int x = 0; x < w; x++) {
                row.add(null);
            }
            grid.add(row);
        }
    }

    public Boolean checkValidPosition(Building bldg, int x, int y, int rot) {
        bldg.setRotation(rot);
        boolean[][] shp = bldg.getShape();
        switch (rot & 1) {
            case 0:
                if (x < 0 || y < 0 || y + shp.length > maxHeight || x + shp[0].length > maxWidth) {
                    return false;
                }
                break;
            case 1 :
                if (x < 0 || y < 0 || y + shp[0].length > maxHeight || x + shp.length > maxWidth) {
                    return false;
                }
                break;
        }

        for (int shpY = 0; shpY < shp.length; shpY++) {
            for (int shpX = 0; shpX < shp[shpY].length; shpX++) {
                if (shp[shpY][shpX]) {
                    int[] gc = bldg.tryGlobalCoord(shpX, shpY, x, y);
                    try {
                        if (grid.get(gc[1]).get(gc[0]) != null) {
                            return false;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean placeBuilding(Building bldg, int x, int y, int rot) {
        if (!checkValidPosition(bldg, x, y, rot)) {
            return false;
        } else {
            boolean[][] shp = bldg.getShape();
            bldg.setPos(x, y);
            for (int shpY = 0; shpY < shp.length; shpY++) {
                for (int shpX = 0; shpX < shp[shpY].length; shpX++) {
                    if (shp[shpY][shpX]) {
                        int[] gc = bldg.tryGlobalCoord(shpX, shpY, x, y);
                        grid.get(gc[1]).set(gc[0], bldg);
                    }
                }
            }
            bldg.putOnGrid();
            buildingList.add(bldg);
            bldg.updateInputs(grid);
            bldg.updateOutputs(grid);
            return true;
        }

    }


    public void removeBuilding(int x, int y) {
        Building bldg = grid.get(y).get(x);
        removeBuilding(bldg);
    }

    public void removeBuilding(Building bldg) {
        if (bldg.onGrid) {
            boolean[][] shp = bldg.getShape();
            for (int shpY = 0; shpY < shp.length; shpY++) {
                for (int shpX = 0; shpX < shp[shpY].length; shpX++) {
                    if (shp[shpY][shpX]) {
                        int[] gc = bldg.getGlobalCoord(shpX, shpY);
                        if (grid.get(gc[1]).get(gc[0]) == bldg) {
                            grid.get(gc[1]).set(gc[0], null);
                        }
                    }
                }
            }
            bldg.takeOffGrid();
            buildingList.removeValue(bldg, true);
            bldg.setPos(-1, -1);
            bldg.clearNeighbours();
        }
    }


    public Building getBuildingAt(int x, int y) {
        return grid.get(y).get(x);
    }

    public Array<Building> getBuildings() {
        return buildingList;
    }

    public void changeSize(int newHeight, int newWidth) {
        if (maxHeight > newHeight) {
            for (int i  = maxHeight; i > newHeight; i--) {
                for (int j = 0; i < grid.get(i).size; j++) {
                    removeBuilding(j ,i);
                }
            }
        }
        this.maxHeight = newHeight;
        if (maxWidth > newWidth) {
            for (int i  = maxWidth; i > newWidth; i--) {
                for (int j = 0; i < grid.size; j++) {
                    removeBuilding(j ,i);
                }
            }
        }
        this.maxWidth = newWidth;
    }

    public void changeSize() {}

}
