package com.main.CoreWorks.simulators;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.*;

import static java.lang.Math.min;

public class FactorySim {

    private FactoryGrid grid;
    private Queue<Move> pendingMoves = new Queue<>();

    public FactorySim(FactoryGrid grid) {
        this.grid = grid;
    }

    public void advanceTick() {
        pendingMoves.clear();
        Array<Building> buildings = grid.getBuildings();

        // Deterministic sort of buildings to determine which order to process the moves implemented here

        Array<ResourceRequest> requests = new Array<>();

        for (Building building : buildings) {
            requests.addAll(building.generateDemandRequests());
        }

        requests.sort((a,b) -> a.getPriority() - b.getPriority());

        for (ResourceRequest req : requests) {
            ObjectMap<Building, Array<Resource>> suppliers = req.getRequester().getInputBuildings();

            Array<Building> suppliersSorted = suppliers.keys().toArray();
            suppliersSorted.sort((a, b)  -> a.getPriority() - b.getPriority());
            switch (req) {
                case AnythingRequest anythingRequest -> {
                    for (Building supplier : suppliersSorted) {
                        if (req.getValue() <= 0) {
                            break;
                        }

                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (req.getValue() <= 0) {
                                break;
                            }
                            int throughput = 0;
                            Array<IOPort> portArr = supplier.getOutputBuildings().get(req.getRequester());
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                            drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            for (int i = 0; i < drawAmt; i++) {
                                req.getRequester().addToAnythingQueue(drawBuffer.getResource());
                            }
                        }
                    }
                }
                case WhitelistRequest whitelistRequest -> {
                    for (Building supplier : suppliersSorted) {
                        if (req.getValue() <= 0) {
                            break;
                        }

                        suppliers.get(supplier);
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (!whitelistRequest.getResources().contains(drawBuffer.getResource(), true)) {
                                continue;
                            }
                            if (req.getValue() <= 0) {
                                break;
                            }
                            int throughput = 0;
                            Array<IOPort> portArr = supplier.getOutputBuildings().get(req.getRequester());
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                            drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            for (int i = 0; i < drawAmt; i++) {
                                req.getRequester().addToAnythingQueue(drawBuffer.getResource());
                            }
                        }
                    }
                }
                default -> {
                    for (Building supplier : suppliersSorted) {
                        if (req.getValue() <= 0) {
                            break;
                        }
                        suppliers.get(supplier);
                        if (suppliers.get(supplier).contains(req.getResource(), true)) {
                            int throughput = 0;
                            Array<IOPort> portArr = supplier.getOutputBuildings().get(req.getRequester());
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            ResourceBuffer drawBuffer = supplier.getOutputResourceBuffer(req.getResource());
                            if (drawBuffer != null) {
                                int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                                drawBuffer.draw(drawAmt);
                                req.reduceValue(drawAmt);
                                req.getRequester().getInputResourceBuffer(req.getResource()).add(drawAmt);
                            }
                        }
                    }
                }
            }
        }


        for (Building building : buildings) {
            Array<Move> result = building.updateTick();
            if (result != null) {
                for (Move mv : result) {
                    pendingMoves.addLast(mv);
                }
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

    public void clear() {
        grid.getBuildings().forEach(Building::clear);
    }
}
