package com.main.CoreWorks.Factory;

public class AnythingRequest extends ResourceRequest{
    public AnythingRequest(Building building, int value){
        super(null, building, value, 2);
    }


    @Override
    public String toString() {
        return requester.name + " requests " + value + " anything @P: " + priority;
    }
}
