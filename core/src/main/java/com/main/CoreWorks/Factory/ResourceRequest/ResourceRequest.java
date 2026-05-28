package com.main.CoreWorks.Factory.ResourceRequest;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Resources.Resource;

public class ResourceRequest {
    protected Resource resource;
    protected Building requester;
    protected int value;
    protected int priority;

    public ResourceRequest(Resource r, Building building, int v, int p) {
        resource = r;
        requester = building;
        value = v;
        priority = p;
    }

    @Override
    public String toString() {
        return requester.displayName() + " requests " + value + " " + resource + " @P: " + priority;
    }

    public Resource getResource() {
        return resource;
    }

    public Building getRequester() {
        return requester;
    }

    public int getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

    public void reduceValue(int v) {
        value -= v;
    }
}
