package com.main.CoreWorks.Factory;

import com.main.CoreWorks.Resources.Resource;

import java.security.DrbgParameters;

public class ResourceBuffer {
    protected Resource resource;
    protected int capacity;
    protected int current;

    public ResourceBuffer(Resource r, int cap, int val) {
        resource = r;
        capacity = cap;
        current = val;
    }

    public ResourceBuffer(Resource r, int cap) {
        resource = r;
        capacity = cap;
        current = 0;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(resource).append(": ").append(current).append("/").append(capacity).toString();
    }

    public Resource getResource() {
        return resource;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrent() {
        return current;
    }

    public boolean tryAdd(int val) {
        return current + val <= capacity;
    }

    public void add(int val) {
        current += val;
    }

    public boolean tryDraw(int val) {
        return current >= val;
    }

    public void draw(int val) {
        current -= val;
    }

    public void setCapacity(int newCap) {
        capacity = newCap;
        if (capacity > current) {
            current = capacity;
        }
    }

    public void setCurrent(int n) {
        if (capacity <= n) {
            current = capacity;
        } else {
            current = n;
        }
    }
}
