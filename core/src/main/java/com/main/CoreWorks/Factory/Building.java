package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.main.CoreWorks.Factory.ResourceRequest.ResourceRequest;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.database.RecipeDatabase;
import com.main.CoreWorks.moveset.Move;


public abstract class Building {


    // confirmed fields
    protected boolean isEnabled = true;
    protected int disabledDur = 0;
    protected boolean onGrid = false;
    protected int cooldownTimer = Integer.MAX_VALUE;
    protected float currCooldown = 0;
    protected Array<ResourceBuffer> inputBuffer;
    protected Array<ResourceBuffer> outputBuffer;
    protected int capacityMult = 5;
    protected int xCoord = -1; // bottom is 0
    protected int yCoord = -1; // left is 0
    protected int rotation = 0; // 0 is "up", +1 for clockwise rotation
    protected String name;
    protected Recipe recipe = null;
    protected Array<IOPort> ports = new Array<>(0);
    protected int priority = 0;
    protected float speedMultiplier = 1f;

    protected ObjectMap<Building, Array<Resource>> inputBuildings = new ObjectMap<>();
    protected ObjectMap<Building, Array<IOPort>> outputBuildings = new ObjectMap<>();

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

    public Building(JsonValue data) {
        this.name = data.getString("Name");
        this.recipe = null;

        inputBuffer = new Array<>(0);
        outputBuffer = new Array<>(0);

        JsonValue shapeData = data.get("Shape");
        int rows = shapeData.size;
        int cols = shapeData.get(0).size;
        this.shape = new boolean[rows][cols];
        for (int y = 0; y < rows; y++) {
            this.shape[y] = shapeData.get(y).asBooleanArray();
        }


        if (data.get("Cooldown") != null) {
            cooldownTimer = data.getInt("Cooldown");
        }

        if (data.get("Ports") != null) {
            JsonValue rawPorts = data.get("Ports");
            for (int p = 0; p < rawPorts.size; p++) {
                JsonValue thisPortData = rawPorts.get(p);
                int portX = thisPortData.getInt("x");
                int portY = thisPortData.getInt("y");
                if (portX >= 0 && portX < this.shape[0].length &&
                    portY >= 0 && portY < this.shape.length) {
                    IOPort thisPort = new IOPort(portX, portY, thisPortData.getInt("dir"), thisPortData.getInt("rate"));
                    addPort(thisPort);
                }
            }
        }

        if (data.get("DefaultRecipe") != null) {
            setRecipe(RecipeDatabase.get(data.getString("DefaultRecipe")));
        }

        if (data.get("SpeedMult") != null) {
            speedMultiplier = data.getFloat("SpeedMult");
        }

    }

    @Override
    public String toString() {
        return name;
    }

    public int[] getGlobalCoord(int x , int y) {
        return tryGlobalCoord(x, y, xCoord, yCoord);
    }

    public int[] tryGlobalCoord(int x , int y, int tryPosX, int tryPosY) {
        return getGlobalCoord(x, y, tryPosX, tryPosY, this.rotation, this.shape);
    }

    public static int[] getGlobalCoord(int locX, int locY, int posX, int posY, int rot, boolean[][] shape) {
        return getGlobalCoord(locX, locY, posX, posY, rot, shape.length, shape[0].length);
    }

    public static int[] getGlobalCoord(int locX, int locY, int posX, int posY, int rot, int height, int width) {
        int globalX = 0;
        int globalY = 0;

        switch (rot) {
            case 0:
                globalX = locX;
                globalY = locY;
                break;
            case 1:
                globalX = height - 1 - locY;
                globalY = locX;
                break;
            case 2:
                globalX = width - 1 - locX;
                globalY = height - 1 - locY;
                break;
            case 3:
                globalX = locY;
                globalY = width - 1 - locX;
                break;
        }

        globalX += posX;
        globalY += posY;

        return new int[]{globalX, globalY};
    }

    public boolean[][] getProjectedShape() {
        boolean[][] newshape = {{}};
        switch (rotation & 1) {
            case 0:
                newshape = new boolean[shape.length][shape[0].length];
                break;
            case 1:
                newshape = new boolean[shape[0].length][shape.length];
                break;
        }

        int newHeight = newshape.length;
        int newWidth = newshape[0].length;
        int totalCells = newHeight * newWidth;

        switch (rotation & 3) {
            case 0:
                for (int i = 0; i < totalCells; i++) {
                    int x = i % shape[0].length;
                    int y = i / shape[0].length;
                    int newX = i % shape[0].length;
                    int newY = i / shape[0].length;
                    newshape[newY][newX] = shape[y][x];
                }
                break;
            case 1:
                for (int i = 0; i < totalCells; i++) {
                    int x = i % shape[0].length;
                    int y = i / shape[0].length;
                    int newX = newWidth - 1 - (i / shape[0].length);
                    int newY = i % shape[0].length;
                    newshape[newY][newX] = shape[y][x];
                }
                break;
            case 2:
                for (int i = 0; i < totalCells; i++) {
                    int x = i % shape[0].length;
                    int y = i / shape[0].length;
                    int newX = newWidth - 1 - (i % shape[0].length);
                    int newY = newHeight - 1 - (i / shape[0].length);
                    newshape[newY][newX] = shape[y][x];
                }
                break;
            case 3:
                for (int i = 0; i < totalCells; i++) {
                    int x = i % shape[0].length;
                    int y = i / shape[0].length;
                    int newX = i / shape[0].length;
                    int newY = newHeight - 1 - (i % shape[0].length);
                    newshape[newY][newX] = shape[y][x];
                }
                break;
        }

        return newshape;

    }


    protected int[] getLocalCoord(int x , int y) {
        return getLocalCoord(x, y, this.xCoord, this.yCoord, this.rotation, this.shape);
    }


    public static int[] getLocalCoord(int gloX, int gloY, int posX, int posY, int rot, boolean[][] shape) {
        return getLocalCoord(gloX, gloY, posX, posY, rot, shape.length, shape[0].length);
    }

    public static int[] getLocalCoord(int gloX, int gloY, int posX, int posY, int rot, int height, int width) {
        int offsetX = gloX - posX;
        int offsetY = gloY - posY;
        int localX = 0;
        int localY = 0;

        switch (rot & 3) {
            case 0:
                localX = offsetX;
                localY = offsetY;
                break;
            case 1:
                localX = offsetY;
                localY = height - 1 - offsetX;
                break;
            case 2:
                localX = width - 1 - offsetX;
                localY = height - 1 - offsetY;
                break;
            case 3:
                localX = width - 1 - offsetY;
                localY = offsetX;
                break;
        }

        return new int[]{localX, localY};
    }

    public void clearNeighbours() {
        inputBuildings.keys().forEach(building -> building.removeOutput(this));
        outputBuildings.keys().forEach(building -> building.removeInput(this));
        inputBuildings.clear();
        outputBuildings.clear();
    }

    public void updateInputs(Array<Array<Building>> grid) {
        ObjectSet<Building> neighbours = new ObjectSet<>();

        for (int lr = 0; lr < shape.length; lr++) {
            for (int lc = 0; lc < shape[lr].length; lc++) {
                for (int r = 0; r < 4; r++) {
                    int[] gc = getGlobalCoord(lc, lr);
                    switch (r) {
                        case 0 -> {
                            gc[1]--;
                        }
                        case 1 -> {
                            gc[0]++;
                        }
                        case 2 -> {
                            gc[1]++;
                        }
                        case 3 -> {
                            gc[0]--;
                        }
                    }
                    Building maybeNeighbour = null;
                    try {
                        maybeNeighbour = grid.get(gc[1]).get(gc[0]);
                    } catch (Exception e) {
                        continue;
                    }
                    if ((maybeNeighbour != null) &&
                        (maybeNeighbour != this)) {
                        neighbours.add(maybeNeighbour);
                    }
                }
            }
        }

        neighbours.iterator().forEachRemaining(b -> b.updateOutputs(grid));


    }

    public void updateOutputs(Array<Array<Building>> grid) {
        if (this.ports != null) {
            for (IOPort p : ports) {
                int[] targetCoord = getGlobalCoord(p.getX(), p.getY());
                int portGlobalDir = (p.getDir() + rotation) % 4;

                switch (portGlobalDir) {
                    case 0 -> {
                        targetCoord[1]--;
                    }
                    case 1 -> {
                        targetCoord[0]++;
                    }
                    case 2 -> {
                        targetCoord[1]++;
                    }
                    case 3 -> {
                        targetCoord[0]--;
                    }
                }
                Building target;
                try {
                    target = grid.get(targetCoord[1]).get(targetCoord[0]);
                } catch (Exception e) {
                    target = null;
                }
                p.setTarget(target);
            }

            for (IOPort p : ports) {
                if (p.target != null) {
                    addOutput(p.target, p);
                }
            }
        }

    }


    public void addOutput(Building b, IOPort p) {
        Array<Resource> matches = matchResource(b, true);
        if (!outputBuildings.containsKey(b)) {
            outputBuildings.put(b, new Array<IOPort>());
        }
        outputBuildings.get(b).add(p);
        b.inputBuildings.put(this, matches);
    }

    public void removeInput(Building b) {
        inputBuildings.remove(b);
    }

    public void removeOutput(Building b) {
        outputBuildings.remove(b);
    }

    public Array<Resource> matchResource(Building b, boolean thisSupplier) {
        if (this.recipe != null && b.recipe!= null) {
            Array<Resource> sup;
            Array<Resource> cons;
            if (thisSupplier) {
                sup = this.recipe.getOutputs();
                cons = b.recipe.getInputs();
            } else {
                cons = this.recipe.getInputs();
                sup = b.recipe.getOutputs();
            }

            ObjectSet<Resource> consAsSet = new ObjectSet<Resource>();
            consAsSet.addAll(cons);
            Array<Resource> matches = new Array<>(0);

            for (Resource rsc : sup) {
                if (consAsSet.contains(rsc)) {
                    matches.add(rsc);
                }
            }

            return matches;
        } else {
            return null;
        }
    }


    public Array<ResourceRequest> generateDemandRequests() {
        Array<ResourceRequest> requests = new Array<>();
        for (int i = 0; i < inputBuffer.size; i++) {
            ResourceRequest thisRequest = inputBuffer.get(i).generateDemandRequest(this);
            if (thisRequest != null) {
                requests.add(thisRequest);
            }
        }
        return requests;
    }

    public ResourceBuffer getOutputResourceBuffer(Resource r) {
        for (ResourceBuffer buffer : outputBuffer) {
            if (buffer.resource == r) {
                return buffer;
            }
        }
        return null;
    }

    public Array<ResourceBuffer> getOutputResourceBuffer() {
        return outputBuffer;
    }

    public void addToAnythingQueue(Resource x) {

    }

    public ResourceBuffer getInputResourceBuffer(Resource r) {
        for (ResourceBuffer buffer : inputBuffer) {
            if (buffer.resource == r) {
                return buffer;
            }
        }
        return null;
    }

    public ObjectMap<Building, Array<Resource>> getInputBuildings() {
        return inputBuildings;
    }

    public ObjectMap<Building, Array<IOPort>> getOutputBuildings() {
        return outputBuildings;
    }

    public int getPriority() {
        return priority;
    }

    public void setRecipe(Recipe rec) {
        // write new recipe
        this.recipe = rec;
        this.cooldownTimer = this.recipe.getDuration();

        // grab new inputs
        Array<Resource> inputs = this.recipe.getInputs();
        Array<Integer> inputMults = this.recipe.getInputMultipliers();
        // reset queues
        this.inputBuffer.clear();
        for (int i  = 0; i < inputs.size; i++) {
            this.inputBuffer.add(new ResourceBuffer(inputs.get(i), capacityMult * inputMults.get(i)));
        }

        // grab new outputs
        Array<Resource> outputs = this.recipe.getOutputs();
        Array<Integer> outputMults = this.recipe.getOutputMultipliers();
        // reset queues
        this.outputBuffer.clear();
        for (int i  = 0; i < outputs.size; i++) {
            this.outputBuffer.add(new ResourceBuffer(outputs.get(i), capacityMult * outputMults.get(i)));
        }
    }

    public Move updateTick() {
        if (isEnabled) {
            return updateEnabled();
        } else {
            disabledDur--;
            if (disabledDur <= 0) {
                isEnabled = true;
                disabledDur = 0;
            }
            return null;
        }
    }

    public abstract Move updateEnabled();

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
        clear();
    }

    public void addPort(int x, int y, int dir, int speed) {
        IOPort port = new IOPort(x, y, dir, speed);
        ports.add(port);
    }

    public void addPort(IOPort port) {
        ports.add(port);
    }

    public boolean isOnGrid() {
        return onGrid;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void clearPorts() {
        ports.clear();
    }

    public String displayName() {
        return this.name;
    }

    public Array<IOPort> getPorts() {
        return ports;
    }

    public int[] getPortGlobalCoords(IOPort port) {
        return getGlobalCoord(port.getX(), port.getY());
    }

    public int getPortGlobalDirection(IOPort port) {
        return (port.getDir() + rotation) % 4;
    }

    public void disableFor(int dur) {
        if (isEnabled) {
            isEnabled = false;
        }
        if (dur > disabledDur) {
            disabledDur = dur;
        }
    }

    public void addDisable(int dur) {
        if (isEnabled) {
            isEnabled = false;
        }
        disabledDur += dur;
    }
}
