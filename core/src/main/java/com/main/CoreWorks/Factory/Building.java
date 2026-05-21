package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.database.RecipeDatabase;
import com.main.CoreWorks.moveset.Move;


public abstract class Building {


    // confirmed fields
    protected boolean isEnabled = true;
    protected boolean onGrid = false;
    protected int cooldownTimer;
    protected int currCooldown = 0;
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

    protected ObjectMap<Building, Array<Resource>> inputBuildings = new ObjectMap<>();
    protected ObjectMap<Building, Array<Resource>> outputBuildings = new ObjectMap<>();

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
        this.cooldownTimer = data.getInt("Cooldown");
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

        if (data.get("Ports") != null) {
            JsonValue rawPorts = data.get("Ports");
            for (int p = 0; p < rawPorts.size; p++) {
                JsonValue thisPortData = rawPorts.get(p);
                int portX = thisPortData.getInt("x");
                int portY = thisPortData.getInt("y");
                if (portX >= 0 && portX < this.shape[0].length &&
                    portY >= 0 && portY < this.shape[0].length) {
                    IOPort thisPort = new IOPort(portX, portY, thisPortData.getInt("dir"), thisPortData.getInt("rate"));
                    addPort(thisPort);
                }
            }
        }

        if (data.get("DefaultRecipe") != null) {
            setRecipe(RecipeDatabase.get(data.getString("DefaultRecipe")));
        }


    }

    @Override
    public String toString() {
        return name;
    }

    protected int[] getGlobalCoord(int x , int y) {
        return tryGlobalCoord(x, y, xCoord, yCoord);
    }

    protected int[] tryGlobalCoord(int x , int y, int tryPosX, int tryPosY) {
        int shapeW = shape[0].length;
        int shapeH = shape.length;
        int globalX = 0;
        int globalY = 0;

        switch (rotation & 3) {
            case 0:
                globalX = x;
                globalY = y;
                break;
            case 1:
                globalX = shapeH - 1 - y;
                globalY = x;
                break;
            case 2:
                globalX = shapeW - 1 - x;
                globalY = shapeH - 1 - y;
                break;
            case 3:
                globalX = y;
                globalY = shapeW - 1 - x;
                break;
        }

        globalX += tryPosX;
        globalY += tryPosY;

        return new int[]{globalX, globalY};
    }

    protected int[] getLocalCoord(int x , int y) {
        int shapeW = shape[0].length;
        int shapeH = shape.length;
        int offsetX = x - xCoord;
        int offsetY = y - yCoord;
        int localX = 0;
        int localY = 0;

        switch (rotation & 3) {
            case 0:
                localX = offsetX;
                localY = offsetY;
                break;
            case 1:
                localX = offsetY;
                localY = shapeH - 1 - offsetX;
                break;
            case 2:
                localX = shapeW - 1 - offsetX;
                localY = shapeH - 1 - offsetY;
                break;
            case 3:
                localX = shapeW - 1 - offsetY;
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
        System.out.println("updating "+name+" inputs");

        ObjectSet<Building> neighbours = new ObjectSet<>();

        for (int lr = 0; lr < shape.length; lr++) {
            for (int lc = 0; lc < shape[lr].length; lc++) {
                int[] gc = getGlobalCoord(lr, lc);
                for (int r = 0; r < 4; r++) {
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
                        maybeNeighbour = grid.get(gc[0]).get(gc[1]);
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
        System.out.println("updating "+name+" outputs");
        if (this.ports != null) {
            for (IOPort p : ports) {
                System.out.println("procesing " + p);
                int[] targetCoord = getGlobalCoord(p.getX(), p.getY());
                System.out.println("port at " + targetCoord[0] + " " + targetCoord[1]);
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
                System.out.println("pointing at " + targetCoord[0] + " " + targetCoord[1]);
                System.out.println("grid");
                System.out.println(grid);
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
                    addOutput(p.target);
                }
            }
        }

    }

    public void addInput(Building b) {
        Array<Resource> matches = matchResource(b, false);
        if (!matches.isEmpty()) {
            inputBuildings.put(b, matches);
            b.outputBuildings.put(this, matches);
        }
    }

    public void addOutput(Building b) {
        System.out.println("adding Output");
        Array<Resource> matches = matchResource(b, true);
        System.out.println("matching Resources: "+ matches);
        outputBuildings.put(b, matches);
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

    public int getPriority() {
        return priority;
    }

    public void setRecipe(Recipe rec) {
        // write new recipe
        this.recipe = rec;

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

    public abstract Move updateTick();

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    public void toggleEnable() {
        if (isEnabled) {
            disable();
        } else {
            enable();
        }
    }

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
    }

    public void addPort(int x, int y, int dir, int speed) {
        IOPort port = new IOPort(x, y, dir, speed);
        ports.add(port);
    }

    public void addPort(IOPort port) {
        ports.add(port);
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
}
