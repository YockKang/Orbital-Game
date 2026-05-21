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
        if (x < 0 || y < 0 || y + shp.length > maxHeight || x + shp[0].length > maxWidth) {
            return false;
        }
        int w = shp[0].length;
        int h = shp.length;
        for (int i = 0; i < w * h; i++) {
            int locX = 0, locY = 0;
            switch (rot) {
                case 0 -> {
                    locX = i % w;
                    locY = i / w;
                }
                case 1 -> {
                    locX = i / h;
                    locY = h - 1 - (i % h);
                }
                case 2 -> {
                    locX = w - 1 - (i % w);
                    locY = h - 1 - (i / w);
                }
                case 3 -> {
                    locX = w - 1 - (i / h);
                    locY = i % h;
                }
            }
            if (shp[locY][locX]) {
                if (grid.get(locX + y).get(locX + x) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean placeBuilding(Building bldg, int x, int y, int rot) {
        if (!checkValidPosition(bldg, x, y, rot)) {
            return false;
        } else {
            bldg.setPos(x, y);
            bldg.setRotation(rot);
            boolean[][] shp = bldg.getShape();
            int w = shp[0].length;
            int h = shp.length;
            for (int i = 0; i < w * h; i++) {
                int locX = 0, locY = 0;
                switch (rot) {
                    case 0 -> {
                        locX = i % w;
                        locY = i / w;
                    }
                    case 1 -> {
                        locX = i / h;
                        locY = h - 1 - (i % h);
                    }
                    case 2 -> {
                        locX = w - 1 - (i % w);
                        locY = h - 1 - (i / w);
                    }
                    case 3 -> {
                        locX = w - 1 - (i / h);
                        locY = i % h;
                    }
                }
                if (shp[locY][locX]) {
                    grid.get(locY + y).set(locX + x, bldg);
                }
            }
        }

        bldg.putOnGrid();
        buildingList.add(bldg);
        bldg.updateInputs(grid);
        bldg.updateOutputs(grid);
        return true;
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
