package com.main.CoreWorks.Factory;

import com.main.CoreWorks.Resources.Resource;

public class ResourceRequest {
    protected Resource resource;
    protected Building requester;
    protected int value;

    public ResourceRequest(Resource r, Building building, int v) {
        resource = r;
        value = v;
    }

    public Resource getResource() {
        return resource;
    }

    public int getValue() {
        return value;
    }
}
