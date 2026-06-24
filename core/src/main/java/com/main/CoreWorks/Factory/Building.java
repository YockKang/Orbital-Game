package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.util.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Factory.Tubes.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.*;
import com.main.CoreWorks.database.*;
import com.main.CoreWorks.moveset.*;

import java.util.*;

public abstract class Building extends Structure implements Updatable, Comparable<Building> {


    // confirmed fields
    protected String name;
    protected int idNum;
    protected boolean isEnabled = true;
    protected int disabledDur = 0;
    protected boolean onGrid = false;
    protected int cooldownTimer = Integer.MAX_VALUE;
    protected float currCooldown = 0;
    protected Array<ResourceBuffer> inputBuffer;
    protected Array<ResourceBuffer> outputBuffer;
    protected int capacityMult = 5;
    protected int rotation = 0; // 0 is "up", +1 for clockwise rotation
    protected Recipe recipe = null;
    protected Array<IOPort> ports = new Array<>(3);
    protected int priority = 0;
    protected float speedBase = 1f;
    protected float speedMultiplier = 1f;
    protected float speedFlat = 0f;

    protected ObjectMap<Building, ObjectSet<IOPort>> inputBuildings = new ObjectMap<>();
    protected ObjectMap<Tube, ObjectSet<Integer>> connectedTubes = new ObjectMap<>();
    protected ObjectSet<TubeNet> connectedTubeNet = new ObjectSet<>();

    protected Array<String> whitelist = null;
    protected Array<Recipe> validRecipes = null;

    protected ObjectMap<String, Modifier> modifiers = new ObjectMap<>();

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
        super();
        cooldownTimer = coolDown;
        inputBuffer = inputs;
        outputBuffer = outputs;
        name = nameIn;
        this.shape = shape;
    }

    public Building(JsonValue data) {
        super();
        this.name = data.getString("Name");
        this.idNum = data.getInt("idNum");
        this.recipe = null;


        inputBuffer = new Array<>(0);
        outputBuffer = new Array<>(0);

        JsonValue shapeData = data.get("Shape");
        if (shapeData.get(0).isArray()) {
            // custom shape
            int rows = shapeData.size;
            int cols = shapeData.get(0).size;
            this.shape = new boolean[rows][cols];
            for (int y = 0; y < rows; y++) {
                this.shape[y] = shapeData.get(y).asBooleanArray();
            }
        } else {
            String stdShape = shapeData.get(0).asString();
            switch (stdShape) {
                case "R":
                    int rows = shapeData.get(1).asInt();
                    int cols = shapeData.get(2).asInt();
                    this.shape = new boolean[rows][cols];
                    for (int y = 0; y < rows; y++) {
                        Arrays.fill(this.shape[y], true);
                    }
                    break;
                case "S":
                    int side = shapeData.get(1).asInt();
                    this.shape = new boolean[side][side];
                    for (int y = 0; y < side; y++) {
                        Arrays.fill(this.shape[y], true);
                    }
                    break;
            }
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

        if (data.get("BaseSpeed") != null) {
            speedBase = data.getInt("BaseSpeed");
        }

        if (data.get("Whitelist") != null) {
            String[] whitelistArr = data.get("Whitelist").asStringArray();
            this.whitelist = new Array<>(whitelistArr);
        }

        if (data.get("Group") != null) {
            validRecipes = new Array<>();
            addRecipes(data.get("Group"));
        }

        if (validRecipes != null) {
            ObjectSet<Recipe> seen = new ObjectSet<>();
            Array<Recipe> unique = new Array<>();

            for (Recipe item : validRecipes) {
                if (seen.add(item)) {
                    unique.add(item);
                }
            }
            validRecipes = unique;

            validRecipes.sort((r1, r2) -> r1.getName().compareTo(r2.getName()));

        }

    }

    private void addRecipes(JsonValue data) {
        if (data.isArray()) {
            data.forEach(this::addRecipes);
        } else {
            validRecipes.addAll(RecipeGroupDatabase.get(data.asString()));
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public Coords getGlobalCoord(int x, int y) {
        return tryGlobalCoord(x, y, xCoord, yCoord);
    }

    public Coords tryGlobalCoord(int x, int y, int tryPosX, int tryPosY) {
        return getGlobalCoord(x, y, tryPosX, tryPosY, this.rotation, this.shape);
    }

    public static Coords getGlobalCoord(int locX, int locY, int posX, int posY, int rot, boolean[][] shape) {
        return getGlobalCoord(locX, locY, posX, posY, rot, shape.length, shape[0].length);
    }

    public static Coords getGlobalCoord(int locX, int locY, int posX, int posY, int rot, int height, int width) {
        int globalX = 0;
        int globalY = switch (rot) {
            case 0 -> {
                globalX = locX;
                yield locY;
            }
            case 1 -> {
                globalX = height - 1 - locY;
                yield locX;
            }
            case 2 -> {
                globalX = width - 1 - locX;
                yield height - 1 - locY;
            }
            case 3 -> {
                globalX = locY;
                yield width - 1 - locX;
            }
            default -> 0;
        };

        globalX += posX;
        globalY += posY;

        return new Coords(globalX, globalY);
    }

    public boolean[][] getProjectedShape() {
        boolean[][] newshape = {{}};
        newshape = switch (rotation & 1) {
            case 0 -> new boolean[shape.length][shape[0].length];
            case 1 -> new boolean[shape[0].length][shape.length];
            default -> newshape;
        };

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


    protected Coords getLocalCoord(int x, int y) {
        return getLocalCoord(x, y, this.xCoord, this.yCoord, this.rotation, this.shape);
    }


    public static Coords getLocalCoord(int gloX, int gloY, int posX, int posY, int rot, boolean[][] shape) {
        return getLocalCoord(gloX, gloY, posX, posY, rot, shape.length, shape[0].length);
    }

    public static Coords getLocalCoord(int gloX, int gloY, int posX, int posY, int rot, int height, int width) {
        int offsetX = gloX - posX;
        int offsetY = gloY - posY;
        int localX = 0;
        int localY = switch (rot & 3) {
            case 0 -> {
                localX = offsetX;
                yield offsetY;
            }
            case 1 -> {
                localX = offsetY;
                yield height - 1 - offsetX;
            }
            case 2 -> {
                localX = width - 1 - offsetX;
                yield height - 1 - offsetY;
            }
            case 3 -> {
                localX = width - 1 - offsetY;
                yield offsetX;
            }
            default -> 0;
        };

        return new Coords(localX, localY);
    }

    public void clearNeighbours() {
        connectedTubeNet.clear();
        connectedTubes.clear();
        inputBuildings.clear();
    }

    public void updateInputs(Array<Array<Structure>> grid) {
        ObjectSet<Building> neighbours = new ObjectSet<>();

        for (int lr = 0; lr < shape.length; lr++) {
            for (int lc = 0; lc < shape[lr].length; lc++) {
                for (int r = 0; r < 4; r++) {
                    Coords gc = getGlobalCoord(lc, lr);
                    int x = gc.x;
                    int y = gc.y;
                    switch (r) {
                        case 0 -> {
                            y--;
                        }
                        case 1 -> {
                            x++;
                        }
                        case 2 -> {
                            y++;
                        }
                        case 3 -> {
                            x--;
                        }
                    }
                    Structure maybeNeighbour = null;
                    try {
                        maybeNeighbour = grid.get(y).get(x);
                    } catch (Exception e) {
                        continue;
                    }
                    if ((maybeNeighbour != null) &&
                        (maybeNeighbour != this)) {
                        if (maybeNeighbour instanceof Tube tubeNeighbour) {
                            System.out.println();
                            System.out.println(tubeNeighbour);
                            System.out.println((r + 2) % 4);
                            System.out.println(tubeNeighbour.getNetworkNum((r + 2) % 4));
                            if (tubeNeighbour.getNetworkNum((r + 2) % 4) != 0) {
                                addTubeInput(tubeNeighbour, (r + 2) % 4);
                            }
                        } else if (maybeNeighbour instanceof Building bldg) {
                            IOPort port = bldg.getPortFor(x, y, (r + 2) % 4);
                            if (port != null) {
                                if (!inputBuildings.containsKey(bldg)) {
                                    inputBuildings.put(bldg, new ObjectSet<>());
                                }
                                inputBuildings.get(bldg).add(port);
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateOutputs(Array<Array<Structure>> grid) {
        if (this.ports != null) {
            for (IOPort p : ports) {
                Coords targetCoord = getGlobalCoord(p.getX(), p.getY());
                int portGlobalDir = (p.getDir() + rotation) % 4;
                int x = targetCoord.x;
                int y = targetCoord.y;
                switch (portGlobalDir) {
                    case 0 -> {
                        y--;
                    }
                    case 1 -> {
                        x++;
                    }
                    case 2 -> {
                        y++;
                    }
                    case 3 -> {
                        x--;
                    }
                }
                Structure target;
                try {
                    target = grid.get(y).get(x);
                } catch (Exception e) {
                    target = null;
                }
                if (target instanceof Tube tube) {
                    tube.getNetwork((portGlobalDir + 2) % 4).addInput(this, p);
                } else if (target instanceof Building building) {
                    building.addInput(this, p);
                }
            }
        }
    }

    private void addInput(Building building, IOPort p) {
        if (inputBuildings.containsKey(building)) {
            inputBuildings.get(building).add(p);
        } else {
            ObjectSet<IOPort> portSet = new ObjectSet<>(10);
            portSet.add(p);
            inputBuildings.put(building, portSet);
        }
    }


    public void addTubeInput(Tube tube, int dir) {
        if (!connectedTubes.containsKey(tube)) {
            connectedTubes.put(tube, new ObjectSet<>(5));
        }
        connectedTubes.get(tube).add(dir);
        connectedTubeNet.add(tube.getNetwork(dir));
    }

    public void updateNets() {
        connectedTubeNet.clear();
        for (ObjectMap.Entry<Tube, ObjectSet<Integer>> entry : connectedTubes) {
            System.out.println("found tube: " + entry.key);
            for (int dir : entry.value) {
                System.out.println("dir: " + dir);
                System.out.println("new net: " + entry.key.getNetwork(dir));
                connectedTubeNet.add(entry.key.getNetwork(dir));
            }
        }
    }

    public void removeTubeInput(Tube tube, int dir) {
        System.out.println();
        System.out.println(this);
        System.out.println(connectedTubes.containsKey(tube));
        System.out.println(connectedTubes.get(tube));
        if (connectedTubes.containsKey(tube)) {
            connectedTubes.get(tube).remove(dir);
        };
        System.out.println(connectedTubes.get(tube));
        System.out.println(connectedTubes.get(tube).size);
        if (connectedTubes.get(tube).size == 0) {
            connectedTubes.remove(tube);
        }
        TubeNet net = tube.getNetwork(dir);
        boolean stillConnected = false;
        for (ObjectMap.Entry<Tube, ObjectSet<Integer>> entry : connectedTubes) {
            for (int dirs : entry.value) {
                if (entry.key.getNetwork(dirs) == net) {
                    stillConnected = true;
                    break;
                }
            }
        }
        if (!stillConnected) {
            connectedTubeNet.remove(net);
        }
    }


    public void removeInput(Building b) {
        inputBuildings.remove(b);
    }


    public Array<String> matchResource(Building b, boolean thisSupplier) {
        if (this.recipe != null && b.recipe != null) {
            Array<String> sup;
            Array<String> cons;
            if (thisSupplier) {
                sup = this.recipe.getOutputs();
                cons = b.recipe.getInputs();
            } else {
                cons = this.recipe.getInputs();
                sup = b.recipe.getOutputs();
            }

            ObjectSet<String> consAsSet = new ObjectSet<String>();
            consAsSet.addAll(cons);
            Array<String> matches = new Array<>(0);

            for (String rsc : sup) {
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

    public ResourceBuffer getOutputResourceBuffer(String r) {
        for (ResourceBuffer buffer : outputBuffer) {
            if (Objects.equals(buffer.resourceId, r)) {
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

    public ResourceBuffer getInputResourceBuffer(String r) {
        for (ResourceBuffer buffer : inputBuffer) {
            if (Objects.equals(buffer.resourceId, r)) {
                return buffer;
            }
        }
        return null;
    }

    public ObjectSet<TubeNet> getInputTubeNets() {
        return connectedTubeNet;
    }

    public ObjectMap<Building, ObjectSet<IOPort>> getInputBuildings() {
        return inputBuildings;
    }


    public int getPriority() {
        return priority;
    }

    public void setRecipe(Recipe rec) {
        // write new recipe
        this.recipe = rec;
        this.cooldownTimer = this.recipe.getDuration();

        // grab new inputs
        Array<String> inputs = this.recipe.getInputs();
        Array<Integer> inputMults = this.recipe.getInputMultipliers();
        // reset queues
        this.inputBuffer.clear();
        for (int i = 0; i < inputs.size; i++) {
            this.inputBuffer.add(new ResourceBuffer(inputs.get(i), capacityMult * inputMults.get(i)));
        }

        // grab new outputs
        Array<String> outputs = this.recipe.getOutputs();
        Array<Integer> outputMults = this.recipe.getOutputMultipliers();
        // reset queues
        this.outputBuffer.clear();
        for (int i = 0; i < outputs.size; i++) {
            this.outputBuffer.add(new ResourceBuffer(outputs.get(i), capacityMult * outputMults.get(i)));
        }
    }

    public void addModifier(Modifier mod) {
        if (!modifiers.containsKey(mod.getType())) {
            modifiers.put(mod.getType(), mod);
        } else {
            Modifier oldMod = modifiers.get(mod.getType());
            oldMod.changeValue(mod.getValue());
            oldMod.setStrValue(mod.getStrValue());
        }
    }

    public ObjectMap<String, Modifier> getModifiers() {
        return modifiers;
    }

    @Override
    public Array<Move> updateTick() {
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

    public abstract Array<Move> updateEnabled();

    public void clear() {
        for (ResourceBuffer b : inputBuffer) {
            b.setCurrent(0);
        }
        for (ResourceBuffer b : outputBuffer) {
            b.setCurrent(0);
        }
        currCooldown = 0;
        disabledDur = 0;
        isEnabled = true;
    }

    public void setPos(int x, int y) {
        xCoord = x;
        yCoord = y;
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

    public float getSpeed() {
        return speedBase * speedMultiplier + speedFlat;
    }

    public float getSpeedFlat() {
        return speedFlat;
    }

    public float getSpeedMult() {
        return speedMultiplier;
    }

    public void addSpeedMult(float mult) {
        speedMultiplier += mult;
    }

    public void addSpeedFlat(float flat) {
        speedFlat += flat;
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

    public DirectedCoords getPortGlobalCoords(IOPort port) {
        return getGlobalCoord(port.getX(), port.getY()).addDirection((port.getDir() + rotation) % 4);
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

    public void setCapacityMult(int newCap) {
        int delta = newCap - capacityMult;
        capacityMult = newCap;
        for (ResourceBuffer b : inputBuffer) {
            b.changeCapacity(delta);
        }
        for (ResourceBuffer b : outputBuffer) {
            b.changeCapacity(delta);
        }
    }

    public int getCapacityMult() {
        return capacityMult;
    }

    public void changeCapacityMult(int delta) {
        capacityMult += delta;
        for (ResourceBuffer b : inputBuffer) {
            b.changeCapacity(delta);
        }
        for (ResourceBuffer b : outputBuffer) {
            b.changeCapacity(delta);
        }
    }

    public boolean hasPortAt(int x, int y, int dir) {
        Coords local = getLocalCoord(x, y);
        for (IOPort p : ports) {
            if (p.getX() == local.x &&
                p.getY() == local.y &&
                (p.getDir() + rotation) % 4 == dir) {
                return true;
            }
        }
        return false;
    }

    public IOPort getPortFor(int x, int y, int dir) {
        Coords local = getLocalCoord(x, y);
        for (IOPort p : ports) {
            if (p.getX() == local.x &&
                p.getY() == local.y &&
                (p.getDir() + rotation) % 4 == dir) {
                return p;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Building b) {
        if (!isEnabled && b.isEnabled) {
            return -1;
        } else if (isEnabled && !b.isEnabled) {
            return 1;
        } else {
            int priorityDiff = priority - b.priority;
            if (priorityDiff != 0) {
                return priorityDiff;
            } else {
                float speedDiff = getSpeed() - b.getSpeed();
                return (int) Math.copySign(Math.ceil(Math.abs(speedDiff)), speedDiff);
            }
        }
    }

}
