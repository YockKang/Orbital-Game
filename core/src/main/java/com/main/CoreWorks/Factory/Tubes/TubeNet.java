package com.main.CoreWorks.Factory.Tubes;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;

public class TubeNet {
    private final ObjectMap<Building, ObjectSet<IOPort>> inputs;
    private final ObjectSet<Tube> components;
    private final int id;
    private static int netId;

    public TubeNet() {
        inputs = new ObjectMap<>();
        components = new ObjectSet<>();
        id = netId;
        netId++;
    }

    public TubeNet(Tube tube) {
        inputs = new ObjectMap<>();
        components = new ObjectSet<>();
        components.add(tube);
        id = netId;
        netId++;
    }

    @Override
    public String toString() {
        return "TubeNet #"+id;
    }

    public int getId() {
        return id;
    }

    public ObjectMap<Building, ObjectSet<IOPort>> getInputs() {
        return inputs;
    }

    public void addSegment(Tube newComponent) {
        components.add(newComponent);
    }

    public void addSegment(ObjectSet<Tube> newComponent) {
        components.addAll(newComponent);
    }

    public void removeSegment(Tube component) {
        components.remove(component);
    }

    public ObjectSet<Tube> getComponents() {
        return components;
    }

    public void setNetwork(TubeNet tubeNet) {
        for (Tube tb : components) {
            tb.setNetwork(this, tubeNet);
        }
    }

    public void addInput(Building newInput, ObjectSet<IOPort> ports) {
        if (inputs.containsKey(newInput)) {
            inputs.get(newInput).addAll(ports);
        } else {
            inputs.put(newInput, ports);
        }
    }

    public void addInput(Building newInput, IOPort port) {
        if (inputs.containsKey(newInput)) {
            inputs.get(newInput).add(port);
        } else {
            ObjectSet<IOPort> portSet = new ObjectSet<>(10);
            portSet.add(port);
            inputs.put(newInput, portSet);
        }
    }

    public void addInput(ObjectMap<Building, ObjectSet<IOPort>> newInput) {
        for (ObjectMap.Entry<Building, ObjectSet<IOPort>> entry : newInput) {
            addInput(entry.key, entry.value);
        }
    }

    public void removeInput(Building oldInput, ObjectSet<IOPort> ports) {
        if (inputs.containsKey(oldInput)) {
            ObjectSet<IOPort> portSet = inputs.get(oldInput);
            ports.forEach(portSet::remove);
            if (portSet.isEmpty()) {
                inputs.remove(oldInput);
            }
        }
    }

    public void removeInput(Building oldInput, IOPort port) {
        if (inputs.containsKey(oldInput)) {
            ObjectSet<IOPort> portSet = inputs.get(oldInput);
            portSet.remove(port);
            if (portSet.isEmpty()) {
                inputs.remove(oldInput);
            }
        }
    }

    public void removeInput(ObjectMap<Building, ObjectSet<IOPort>> oldInput) {
        for (ObjectMap.Entry<Building, ObjectSet<IOPort>> entry : oldInput) {
            removeInput(entry.key, entry.value);
        }
    }
}
