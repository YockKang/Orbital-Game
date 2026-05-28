package com.main.CoreWorks.Factory.ResourceRequest;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Resources.Resource;

public class WhitelistRequest extends ResourceRequest {

    protected Array<Resource> resources;

    public WhitelistRequest(Building building, int value, Array<Resource> rsc){
        super(null, building, value, 1);
        resources = rsc;
    }

    @Override
    public String toString() {
        return requester.displayName() + " requests " + value + " any of: " + resource + " @P: " + priority;
    }

    public Array<Resource> getResources() {
        return resources;
    }

}
