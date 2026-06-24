package com.main.CoreWorks.simulators;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Factory.Tubes.TubeNet;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.*;

import java.util.Objects;

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

        requests.sort((a, b) -> a.getPriority() - b.getPriority());

        for (ResourceRequest req : requests) {
            ObjectMap<Building, ObjectSet<IOPort>> suppliers = req.getRequester().getInputBuildings();

            Array<Building> suppliersSorted = suppliers.keys().toArray();
            suppliersSorted.sort();

            ObjectMap<Building, ObjectSet<IOPort>> suppliersTube = new ObjectMap<>();
            for (TubeNet tn : req.getRequester().getInputTubeNets()) {
                for (ObjectMap.Entry<Building, ObjectSet<IOPort>> entry : tn.getInputs()) {
                    if (!suppliersTube.containsKey(entry.key)) {
                        suppliersTube.put(entry.key, new ObjectSet<>());
                    }
                    suppliersTube.get(entry.key).addAll(entry.value);
                }
            }
            Array<Building> suppliersTubeSorted = suppliersTube.keys().toArray();
            suppliersTubeSorted.sort();

            for (Building supplier : suppliersSorted) {
                if (req.getValue() <= 0) {
                    break;
                }
                switch (req) {
                    case AnythingRequest anythingRequest -> {
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (req.getValue() <= 0) {
                                break;
                            }
                            int throughput = 0;
                            ObjectSet<IOPort> portArr = suppliers.get(supplier);
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                            Array<Resource> transfers = drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            transfers.forEach(rsc -> req.getRequester().addToAnythingQueue(rsc));
                        }
                    }
                    case WhitelistRequest whitelistRequest -> {
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (req.getValue() <= 0) {
                                break;
                            }
                            if (!whitelistRequest.getWhitelist().contains(drawBuffer.getResourceId(), false)) {
                                continue;
                            }
                            int throughput = 0;
                            ObjectSet<IOPort> portArr = suppliers.get(supplier);
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                            Array<Resource> transfers = drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            transfers.forEach(rsc -> req.getRequester().addToAnythingQueue(rsc));
                        }
                    }
                    default -> {
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (Objects.equals(drawBuffer.getResourceId(), req.getResource())) {
                                int throughput = 0;
                                ObjectSet<IOPort> portArr = suppliers.get(supplier);
                                for (IOPort p : portArr) {
                                    throughput += p.getSpeed();
                                }
                                int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                                ResourceBuffer.directTransfer(
                                    drawBuffer,
                                    req.getRequester().getInputResourceBuffer(req.getResource()),
                                    drawAmt);
                                req.reduceValue(drawAmt);
                            }
                        }
                    }
                }
            }

            for (Building supplier : suppliersTubeSorted) {
                if (req.getValue() <= 0) {
                    break;
                }

                switch (req) {
                    case AnythingRequest anythingRequest -> {
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (req.getValue() <= 0) {
                                break;
                            }
                            int throughput = 0;
                            ObjectSet<IOPort> portArr = suppliersTube.get(supplier);
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                            Array<Resource> transfers = drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            transfers.forEach(rsc -> req.getRequester().addToAnythingQueue(rsc));
                        }
                    }
                    case WhitelistRequest whitelistRequest -> {
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (req.getValue() <= 0) {
                                break;
                            }
                            if (!whitelistRequest.getWhitelist().contains(drawBuffer.getResourceId(), false)) {
                                continue;
                            }
                            int throughput = 0;
                            ObjectSet<IOPort> portArr = suppliersTube.get(supplier);
                            for (IOPort p : portArr) {
                                throughput += p.getSpeed();
                            }
                            int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                            Array<Resource> transfers = drawBuffer.draw(drawAmt);
                            req.reduceValue(drawAmt);
                            transfers.forEach(rsc -> req.getRequester().addToAnythingQueue(rsc));
                        }
                    }
                    default -> {
                        for (ResourceBuffer drawBuffer : supplier.getOutputResourceBuffer()) {
                            if (Objects.equals(drawBuffer.getResourceId(), req.getResource())) {
                                int throughput = 0;
                                ObjectSet<IOPort> portArr = suppliersTube.get(supplier);
                                for (IOPort p : portArr) {
                                    throughput += p.getSpeed();
                                }
                                System.out.println();
                                int drawAmt = min(throughput, min(drawBuffer.getCurrent(), req.getValue()));
                                ResourceBuffer.directTransfer(
                                    drawBuffer,
                                    req.getRequester().getInputResourceBuffer(req.getResource()),
                                    drawAmt);
                                req.reduceValue(drawAmt);
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


    private static class Pair<T, U>{
        T first;
        U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
}
