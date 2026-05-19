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
        for (int shpY = 0; shpY < shp.length; shpY++) {
            for (int shpX = 0; shpX < shp.length; shpX++) {
                if (shp[shpY][shpX]) {
                    if (grid.get(shpY + y).get(shpX + x) != null) {
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
            bldg.setPos(x, y);
            bldg.setRotation(rot);
            boolean[][] shp = bldg.getShape();
            for (int shpY = 0; shpY < shp.length; shpY++) {
                for (int shpX = 0; shpX < shp.length; shpX++) {
                    if (shp[shpY][shpX]) {
                        grid.get(shpY + y).set(shpX + x, bldg);
                    }
                }
            }

            bldg.putOnGrid();
            buildingList.add(bldg);
            return true;
        }
    }

    public void removeBuilding(int x, int y) {
        Building bldg = grid.get(y).get(x);

        int posX = bldg.getX();
        int posY = bldg.getY();
        boolean[][] shp = bldg.getShape();
        for (int shpY = 0; shpY < shp.length; shpY++) {
            for (int shpX = 0; shpX < shp.length; shpX++) {
                if (shp[shpY][shpX]) {
                    grid.get(shpY + posY).set(shpX + posX, null);
                }
            }
        }

        bldg.takeOffGrid();
        buildingList.removeValue(bldg, true);
        bldg.setPos(-1, -1);
    }

    public Building getBuildingAt(int x, int y) {
        return grid.get(y).get(x);
    }

    public Array<Building> getBuildings() {
        return buildingList;
    }

}
