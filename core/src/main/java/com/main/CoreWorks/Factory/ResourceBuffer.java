package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Resources.Modifier;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.database.ResourceDatabase;


public class ResourceBuffer {
    protected String resourceName;
    protected String resourceId;
    protected int capacity;
    protected Queue<Resource> buffer;

    public ResourceBuffer(String id, int cap, int val) {
        resourceId = id;
        resourceName = ResourceDatabase.getName(id);
        capacity = cap;
        buffer = new Queue<>(cap);
        for (int i = 0; i < val; i++) {
            buffer.addLast(ResourceDatabase.get(resourceId));
            if (buffer.size >= capacity) {
                break;
            }
        }
    }

    public ResourceBuffer(String id, int cap) {
        resourceId = id;
        resourceName = ResourceDatabase.getName(id);
        capacity = cap;
        buffer = new Queue<>(cap);
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(resourceName).append(": ").append(buffer.size).append("/").append(capacity).toString();
    }

    public String getResource() {
        return resourceName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrent() {
        return buffer.size;
    }

    public boolean isFull() {
        return buffer.size >= capacity;
    }

    public boolean tryAdd(int val) {
        return buffer.size + val <= capacity;
    }

    public void add(Resource rsc) {
        buffer.addLast(rsc);
    }

    public void add(Array<Resource> rsc) {
        rsc.forEach(r -> buffer.addLast(r));
    }

    public void addNew(int val) {
        for (int i = 0; i < val; i++) {
            buffer.addLast(ResourceDatabase.get(resourceId));
        }
    }

    public void addNew(int val, ObjectMap<String, Modifier> modifiers) {
        if (modifiers == null || modifiers.size == 0) {
            addNew(val);
        } else {
            for (int i = 0; i < val; i++) {
                buffer.addLast(ResourceDatabase.get(resourceId, modifiers));
            }
        }
    }
    public boolean tryDraw(int val) {
        return buffer.size >= val;
    }

    public Array<Resource> draw() {
        return draw(1);
    }

    public Array<Resource> draw(int val) {
        Array<Resource> arr = new Array<>();
        for (int i = 0; i < val; i++) {
            if (buffer.size == 0) {
                break;
            }
            arr.add(buffer.removeFirst());
        }
        return arr;
    }

    public void setCapacity(int newCap) {
        capacity = newCap;
        while (capacity > buffer.size) {
            buffer.removeLast();
        }
    }

    public void changeCapacity(int delta) {
        capacity += delta;
        if (capacity < 0) {
            capacity = 0;
        }
        while (buffer.size > capacity) {
            buffer.removeLast();
        }
    }

    public void setCurrent(int n) {
        if (n <= 0) {
            buffer.clear();
        } else {
            if (n > capacity) {
                n = capacity;
            }
            int delta = n - buffer.size;
            if (delta < 0) {
                draw(-delta);
            } else if (delta > 0) {
                addNew(delta);
            }
        }
    }

    public ResourceRequest generateDemandRequest(Building b) {
        if (!isFull()) {
            return new ResourceRequest(resourceId, b, capacity - buffer.size, 0);
        } else {
            return null;
        }
    }

    public static void directTransfer(ResourceBuffer from, ResourceBuffer to, int amount) {
        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                to.buffer.addLast(from.buffer.removeFirst());
            }
        }
    }
}
