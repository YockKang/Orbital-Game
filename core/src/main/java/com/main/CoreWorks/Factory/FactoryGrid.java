package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coords.Coords;
import com.main.CoreWorks.Factory.Tubes.*;

public class FactoryGrid {
    protected Array<Array<Structure>> grid;
    protected Array<Building> buildingList = new Array<>();
    protected Array<TubeNet> tubeNets = new Array<>();

    protected int maxHeight;
    protected int maxWidth;


    public FactoryGrid(int h, int w) {
        this.maxHeight = h;
        this.maxWidth = w;
        grid = new Array<>(h);
        for (int y = 0; y < h; y++) {
            Array<Structure> row = new Array<>(w);
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
            case 1:
                if (x < 0 || y < 0 || y + shp[0].length > maxHeight || x + shp.length > maxWidth) {
                    return false;
                }
                break;
        }

        for (int shpY = 0; shpY < shp.length; shpY++) {
            for (int shpX = 0; shpX < shp[shpY].length; shpX++) {
                if (shp[shpY][shpX]) {
                    Coords gc = bldg.tryGlobalCoord(shpX, shpY, x, y);
                    try {
                        if (grid.get(gc.y).get(gc.x) != null) {
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
                        Coords gc = bldg.tryGlobalCoord(shpX, shpY, x, y);
                        grid.get(gc.y).set(gc.x, bldg);
                    }
                }
            }
            bldg.putOnGrid();
            buildingList.add(bldg);
            bldg.updateInputs(grid);;
            bldg.updateOutputs(grid);
            return true;
        }

    }

    public void addTube(int x, int y, int dir1, int dir2) {
        if (x >= 0 && y >= 0 && y < maxHeight && x < maxWidth) {
            if (getStructureAt(x, y) == null) {
                boolean[] arr = new boolean[4];
                arr[dir1] = true;
                arr[dir2] = true;
                Tube tube = new Tube(x, y, arr);
                grid.get(y).set(x, tube);
                tube.connect(grid, 1, new Array<>(new Integer[]{dir1, dir2}));
            } else if (getStructureAt(x, y) instanceof Tube tube) {
                tube.addConnection(grid, dir1, dir2);
            }
        }
    }

    public Building removeBuilding(int x, int y) {
        Structure struct = grid.get(y).get(x);
        if (struct instanceof Building bldg) {
            removeStructure(struct);
            return bldg;
        } else {
            return null;
        }
    }

    public Tube removeTube(int x, int y) {
        Structure struct = grid.get(y).get(x);
        if (struct instanceof Tube tube) {
            removeStructure(tube);
            return tube;
        } else {
            return null;
        }
    }

    public void removeStructure(Structure structure) {
        if (structure instanceof Building bldg) {
            if (bldg.onGrid) {
                boolean[][] shp = bldg.getShape();
                for (int shpY = 0; shpY < shp.length; shpY++) {
                    for (int shpX = 0; shpX < shp[shpY].length; shpX++) {
                        if (shp[shpY][shpX]) {
                            Coords gc = bldg.getGlobalCoord(shpX, shpY);
                            if (grid.get(gc.y).get(gc.x) == bldg) {
                                grid.get(gc.y).set(gc.x, null);
                            }
                        }
                    }
                }
                bldg.takeOffGrid();
                buildingList.removeValue(bldg, true);
                bldg.setPos(-1, -1);
                bldg.clearNeighbours();
            }
        } else if (structure instanceof Tube tube) {
            grid.get(tube.getY()).set(tube.getX(), null);
            tube.disconnect(grid);
        }
    }


    public Structure getStructureAt(int x, int y) {
        if (x < 0 || y < 0 || y >= maxHeight || x >= maxWidth) {
            return null;
        }
        return grid.get(y).get(x);
    }

    public Building getBuildingAt(int x, int y) {
        Structure struct = getStructureAt(x, y);
        if (struct instanceof Building bldg) {
            return bldg;
        }
        return null;
    }

    public Array<Building> getBuildings() {
        return buildingList;
    }

    public void changeSize(int newHeight, int newWidth) {
        if (maxHeight > newHeight) {
            for (int i = maxHeight; i > newHeight; i--) {
                for (int j = 0; i < grid.get(i).size; j++) {
                    removeBuilding(j, i);
                }
            }
        } else {
            for (int i = maxHeight; i < newHeight; i++) {
                Array<Structure> row = new Array<>();
                for (int x = 0; x < maxWidth; x++) {
                    row.add(null);
                }
                grid.add(row);
            }
        }
        this.maxHeight = newHeight;
        if (maxWidth > newWidth) {
            for (int i = maxWidth; i > newWidth; i--) {
                for (int j = 0; i < grid.size; j++) {
                    removeBuilding(j, i);
                }
            }
        } else {
            for (int i = 0; i < maxHeight; i++) {
                for (int x = maxWidth - 1; x < newWidth; x++) {
                    grid.get(i).add(null);
                }
            }
        }
        this.maxWidth = newWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }
}
