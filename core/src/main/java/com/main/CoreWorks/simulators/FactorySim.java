package com.main.CoreWorks.simulators;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.Move;
import com.sun.net.httpserver.Request;

import static java.lang.Math.min;

public class FactorySim {

    private FactoryGrid grid;
    private Queue<Move> pendingMoves = new Queue<>();

    public FactorySim(FactoryGrid grid) {
        this.grid = grid;
    }

    public void advanceTick() {
        System.out.println("Advancing Tick");
        pendingMoves.clear();
        Array<Building> buildings = grid.getBuildings();

        // Deterministic sort of buildings to determine which order to process the moves implemented here

        Array<ResourceRequest> requests = new Array<>();

        for (Building building : buildings) {
            requests.addAll(building.generateDemandRequests());
        }

        requests.sort((a,b) -> a.getPriority() - b.getPriority());

        System.out.println("Requests:");
        System.out.println(requests);

        for (ResourceRequest req : requests) {
            System.out.println("Processing: "+ req);
            ObjectMap<Building, Array<Resource>> suppliers = req.getRequester().getInputBuildings();

            System.out.println("Suppliers: "+ suppliers);

            Array<Building> suppliersSorted = suppliers.keys().toArray();
            suppliersSorted.sort((a, b)  -> a.getPriority() - b.getPriority());
            if (!(req instanceof AnythingRequest)) {
                for (Building supplier : suppliersSorted) {
                    if (req.getValue() >= 0) {
                       break;
                    }
                    if (suppliers.get(supplier).contains(req.getResource(), true)) {
                        ResourceBuffer drawBuffer = supplier.getOutputResourceBuffer(req.getResource());
                        if (drawBuffer != null) {
                            int drawAmt = min(drawBuffer.getCurrent(), req.getValue());
                            drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            req.getRequester().getInputResourceBuffer(req.getResource()).add(drawAmt);
                        }
                    }
                }
            } else {
                for (Building supplier : suppliersSorted) {
                    if (req.getValue() >= 0) {
                        break;
                    }
                    for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                        int drawAmt = min(drawBuffer.getCurrent(), req.getValue());
                        drawBuffer.draw(drawAmt);
                        req.reduceValue(drawAmt);
                        for (int i = 0; i < drawAmt; i++) {
                            req.getRequester().addToAnythingQueue(drawBuffer.getResource());
                        }
                    }
                }
            }
        }

        for (Building building : buildings) {
            Move result = building.updateTick();
            if (result != null) {
                pendingMoves.addLast(result);
            }
        }
    }

    public Queue<Move> returnMoves() {
        Queue<Move> result = new Queue<>();
        while (pendingMoves.size > 0) {
            result.addLast(pendingMoves.removeFirst());
        }
        return result;
    }

    public FactoryGrid getGrid() {
        return grid;
    }
}
