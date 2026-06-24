package com.main.CoreWorks.Factory.Tubes;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.util.*;
import com.main.CoreWorks.Factory.*;

public class Tube extends Structure {
    protected boolean hasDouble = false;
    protected TubeNet network1;
    protected TubeNet network2;
    protected final boolean[] connections1;
    protected final boolean[] connections2;
    protected boolean fullConnect = false;

    public Tube(int x, int y) {
        super(x, y);
        connections1 = new boolean[]{false, false, false, false};
        connections2 = new boolean[]{false, false, false, false};
    }

    public Tube(int x, int y, boolean[] conn) {
        super(x, y);
        connections1 = conn;
        connections2 = new boolean[]{false, false, false, false};
    }

    public Tube(int x, int y, int type) {
        super(x, y);
        switch (type) {
            case 0 -> {
                connections1 = new boolean[]{true, false, true, false};
                connections2 = new boolean[]{false, false, false, false};
            }
            case 1 -> {
                connections1 = new boolean[]{true, true, false, false};
                connections2 = new boolean[]{false, false, false, false};
            }
            case 2 -> {
                connections1 = new boolean[]{true, true, true, false};
                connections2 = new boolean[]{false, false, false, false};
            }
            case 3 -> {
                connections1 = new boolean[]{true, true, true, true};
                connections2 = new boolean[]{false, false, false, false};
                fullConnect = true;
            }
            case 4 -> {
                connections1 = new boolean[]{true, true, false, false};
                connections2 = new boolean[]{false, false, true, true};
                hasDouble = true;
                fullConnect = true;
            }
            case 5 -> {
                connections1 = new boolean[]{true, false, true, false};
                connections2 = new boolean[]{false, true, false, true};
                hasDouble = true;
                fullConnect = true;
            }
            default -> {
                connections1 = new boolean[]{false, false, false, false};
                connections2 = new boolean[]{false, false, false, false};
            }
        }
    }

    @Override
    public String toString() {
        return "Tube @ " + super.toString();
    }

    public void setNetwork(TubeNet oldNet, TubeNet newNet) {
        if (network1 == oldNet) {
            network1 = newNet;
        }
        if (network2 == oldNet) {
            network2 = newNet;
        }
    }

    public void setNetwork(int dir, TubeNet newNet) {
        if (connections1[dir]) {
            network1 = newNet;
        }
        if (connections2[dir]) {
            network2 = newNet;
        }
    }

    public void setNetwork1(TubeNet newNet) {
        network1 = newNet;
    }

    public void setNetwork2(TubeNet newNet) {
        network2 = newNet;
    }

    public int getNetworkNum(int dir) {
        if (connections1[dir]) {
            return 1;
        }
        if (connections2[dir]) {
            return 2;
        }
        return 0;
    }

    public TubeNet getNetwork(int dir) {
        if (connections1[dir]) {
            return network1;
        }
        if (connections2[dir]) {
            return network2;
        }
        return null;
    }

    public void connect(Array<Array<Structure>> grid, int networkNum, Array<Integer> dirSearch) {
        TubeNet network = null;
        boolean[] connections;
        if (networkNum == 1) {
            if (network1 != null) {
                network = network1;
            }
            connections = connections1;
        } else if (networkNum == 2) {
            if (network2 != null) {
                network = network2;
            }
            connections = connections2;
        } else {
            return;
        }
        ObjectSet<Integer> dirs = new ObjectSet<>();
        dirs.addAll(dirSearch);
        ObjectSet<TubeNet> neighbourNets = new ObjectSet<>();
        if (network != null) {
            neighbourNets.add(network);
        }
        ObjectMap<Building, ObjectSet<IOPort>> newPorts = new ObjectMap<>();
        for (int dir : dirs) {
            if (dir >= 0 && dir <= 3 && connections[dir]) {
                Structure target = getNeighbour(grid, dir);
                if (target instanceof Tube tube) {
                    TubeNet connNet = tube.getNetwork((dir + 2) % 4);
                    if (connNet != null) {
                        neighbourNets.add(connNet);
                    }
                } else if (target instanceof Building bldg) {
                    IOPort newPort = null;
                    Coords tgt = getNeighbourCoord(dir);
                    newPort = bldg.getPortFor(tgt.x, tgt.y, (dir + 2) % 4);
                    if (newPort != null) {
                        if (newPorts.containsKey(bldg)) {
                            newPorts.get(bldg).add(newPort);
                        } else {
                            ObjectSet<IOPort> portSet = new ObjectSet<>(10);
                            portSet.add(newPort);
                            newPorts.put(bldg, portSet);
                        }
                    }
                }
            }
        }
        if (neighbourNets.size == 0) {
            network = new TubeNet(this);
            network.addInput(newPorts);
        } else if (neighbourNets.size == 1) {
            TubeNet net = neighbourNets.first();
            net.addSegment(this);
            net.addInput(newPorts);
            network = net;
        } else {
            TubeNet largestNet = null;
            for (TubeNet net : neighbourNets) {
                if (largestNet == null || net.getComponents().size > largestNet.getComponents().size) {
                    largestNet = net;
                }
            }
            assert largestNet != null;
            for (TubeNet net : neighbourNets) {
                if (net != largestNet) {
                    largestNet.addSegment(net.getComponents());
                    largestNet.addInput(net.getInputs());
                    net.setNetwork(largestNet);
                }
            }
            largestNet.addSegment(this);
            network = largestNet;
            largestNet.addInput(newPorts);
        }
        if (networkNum == 1) {
            network1 = network;
        } else {
            network2 = network;
        }
        System.out.println();
        System.out.println("condensed into " + network.getId());
        System.out.println(network.getInputs());
        for (int dir : dirs) {
            if (dir >= 0 && dir <= 3 && connections[dir]) {
                Structure target = getNeighbour(grid, dir);
                if (target instanceof Building bldg) {
                    System.out.println("found output: " + bldg);
                    Coords tgt = getNeighbourCoord(dir);
                    if (!bldg.hasPortAt(tgt.x, tgt.y, (dir + 2) % 4)) {
                        System.out.println("adding tube");
                        bldg.addTubeInput(this, dir);
                    }
                }
            }
        }
    }

    public void connect(Array<Array<Structure>> grid) {

    }

    public void disconnect(Array<Array<Structure>> grid) {
        if (network1 == network2) {
            ObjectMap<Building, ObjectSet<IOPort>> connPorts = new ObjectMap<>();
            Array<Pair<Integer, Boolean>> connected = new Array<>();
            for (int i = 0; i < connections1.length; i++) {
                Structure target = getNeighbour(grid, i);
                if (target instanceof Tube tube) {
                    connected.add(new Pair<>(i, false));
                } else if (target instanceof Building bldg) {
                    IOPort newPort = null;
                    Coords tgt = getNeighbourCoord(i);
                    newPort = bldg.getPortFor(tgt.x, tgt.y, (i + 2) % 4);
                    if (newPort != null) {
                        if (connPorts.containsKey(bldg)) {
                            connPorts.get(bldg).add(newPort);
                        } else {
                            ObjectSet<IOPort> portSet = new ObjectSet<>(10);
                            portSet.add(newPort);
                            connPorts.put(bldg, portSet);
                        }
                    } else {
                        bldg.removeTubeInput(this, i);
                    }
                }
            }
            if (connected.size == 1) {
                network1.removeInput(connPorts);
                network1.removeSegment(this);
            } else if (connected.size > 1) {
                TubeSearcher searcher = new TubeSearcher(grid, this);
                Pair<Integer, Boolean> entry = connected.first();
                int dir = entry.first;
                searcher.search(dir);
                boolean allConnected = true;
                for (Pair<Integer, Boolean> check : connected) {
                    int dir1 = check.first;
                    if (dir != dir1) {
                        if (!searcher.check(dir1)) {
                            allConnected = false;
                            break;
                        }
                    }
                }
                if (allConnected) {
                    network1.removeInput(connPorts);
                    network1.removeSegment(this);
                } else {
                    searcher.setNetwork(new TubeNet());
                    for (Pair<Integer, Boolean> check : connected) {
                        int dir1 = check.first;
                        if (searcher.check(dir1)) {
                            check.second = true;
                        }
                    }
                    for (int i = 0; i < connected.size; i++) {
                        Pair<Integer, Boolean> set1 = connected.get(i);
                        if (!set1.second) {
                            searcher.clear();
                            searcher.search(set1.first);
                            searcher.setNetwork(new TubeNet());
                            for (int j = i; j < connected.size; j++) {
                                Pair<Integer, Boolean> check = connected.get(i);
                                int dir1 = check.first;
                                if (!check.second && set1.first != dir1) {
                                    if (searcher.check(dir1)) {
                                        check.second = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (hasDouble) {
            // disconnect from network 1
            ObjectMap<Building, ObjectSet<IOPort>> connPorts = new ObjectMap<>();
            Array<Pair<Integer, Boolean>> connected = new Array<>();
            for (int i = 0; i < connections1.length; i++) {
                if (connections1[i]) {
                    Structure target = getNeighbour(grid, i);
                    if (target instanceof Tube) {
                        connected.add(new Pair<>(i, false));
                    } else if (target instanceof Building bldg) {
                        IOPort newPort = null;
                        Coords tgt = getNeighbourCoord(i);
                        newPort = bldg.getPortFor(tgt.x, tgt.y, (i + 2) % 4);
                        if (newPort != null) {
                            if (connPorts.containsKey(bldg)) {
                                connPorts.get(bldg).add(newPort);
                            } else {
                                ObjectSet<IOPort> portSet = new ObjectSet<>(10);
                                portSet.add(newPort);
                                connPorts.put(bldg, portSet);
                            }
                        } else {
                            bldg.removeTubeInput(this, i);
                        }
                    }
                }
            }
            if (connected.size == 1) {
                network1.removeInput(connPorts);
                network1.removeSegment(this);
            } else if (connected.size > 1) {
                TubeSearcher searcher = new TubeSearcher(grid, this);
                Pair<Integer, Boolean> entry = connected.first();
                int dir = entry.first;
                searcher.search(dir);
                boolean allConnected = true;
                for (Pair<Integer, Boolean> check : connected) {
                    int dir1 = check.first;
                    if (dir != dir1) {
                        if (!searcher.check(dir1)) {
                            allConnected = false;
                            break;
                        }
                    }
                }
                if (allConnected) {
                    network1.removeInput(connPorts);
                    network1.removeSegment(this);
                } else {
                    searcher.setNetwork(new TubeNet());
                    for (Pair<Integer, Boolean> check : connected) {
                        int dir1 = check.first;
                        if (searcher.check(dir1)) {
                            check.second = true;
                        }
                    }
                    for (int i = 0; i < connected.size; i++) {
                        Pair<Integer, Boolean> set1 = connected.get(i);
                        if (!set1.second) {
                            searcher.clear();
                            searcher.search(set1.first);
                            searcher.setNetwork(new TubeNet());
                            for (int j = i; j < connected.size; j++) {
                                Pair<Integer, Boolean> check = connected.get(i);
                                int dir1 = check.first;
                                if (!check.second && set1.first != dir1) {
                                    if (searcher.check(dir1)) {
                                        check.second = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // disconnect from network 2
            connPorts.clear();
            connected.clear();
            for (int i = 0; i < connections1.length; i++) {
                if (connections2[i]) {
                    Structure target = getNeighbour(grid, i);
                    if (target instanceof Tube) {
                        connected.add(new Pair<>(i, false));
                    } else if (target instanceof Building bldg) {
                        IOPort newPort = null;
                        Coords tgt = getNeighbourCoord(i);
                        newPort = bldg.getPortFor(tgt.x, tgt.y, (i + 2) % 4);
                        if (newPort != null) {
                            if (connPorts.containsKey(bldg)) {
                                connPorts.get(bldg).add(newPort);
                            } else {
                                ObjectSet<IOPort> portSet = new ObjectSet<>(10);
                                portSet.add(newPort);
                                connPorts.put(bldg, portSet);
                            }
                        } else {
                            bldg.removeTubeInput(this, i);
                        }
                    }
                }
            }
            if (connected.size == 1) {
                network2.removeInput(connPorts);
                network2.removeSegment(this);
            } else if (connected.size > 1) {
                TubeSearcher searcher = new TubeSearcher(grid, this);
                Pair<Integer, Boolean> entry = connected.first();
                int dir = entry.first;
                searcher.search(dir);
                boolean allConnected = true;
                for (Pair<Integer, Boolean> check : connected) {
                    int dir1 = check.first;
                    if (dir != dir1) {
                        if (!searcher.check(dir1)) {
                            allConnected = false;
                            break;
                        }
                    }
                }
                if (allConnected) {
                    network1.removeInput(connPorts);
                    network1.removeSegment(this);
                } else {
                    searcher.setNetwork(new TubeNet());
                    for (Pair<Integer, Boolean> check : connected) {
                        int dir1 = check.first;
                        if (searcher.check(dir1)) {
                            check.second = true;
                        }
                    }
                    for (int i = 0; i < connected.size; i++) {
                        Pair<Integer, Boolean> set1 = connected.get(i);
                        if (!set1.second) {
                            searcher.clear();
                            searcher.search(set1.first);
                            searcher.setNetwork(new TubeNet());
                            for (int j = i; j < connected.size; j++) {
                                Pair<Integer, Boolean> check = connected.get(i);
                                int dir1 = check.first;
                                if (!check.second && set1.first != dir1) {
                                    if (searcher.check(dir1)) {
                                        check.second = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // disconnect from network 1
            ObjectMap<Building, ObjectSet<IOPort>> connPorts = new ObjectMap<>();
            Array<Pair<Integer, Boolean>> connected = new Array<>();
            for (int i = 0; i < connections1.length; i++) {
                if (connections1[i]) {
                    Structure target = getNeighbour(grid, i);
                    if (target instanceof Tube) {
                        connected.add(new Pair<>(i, false));
                    } else if (target instanceof Building bldg) {
                        IOPort newPort = null;
                        Coords tgt = getNeighbourCoord(i);
                        newPort = bldg.getPortFor(tgt.x, tgt.y, (i + 2) % 4);
                        if (newPort != null) {
                            if (connPorts.containsKey(bldg)) {
                                connPorts.get(bldg).add(newPort);
                            } else {
                                ObjectSet<IOPort> portSet = new ObjectSet<>(10);
                                portSet.add(newPort);
                                connPorts.put(bldg, portSet);
                            }
                        } else {
                            System.out.println();
                            System.out.println(i);
                            System.out.println("hi");
                            bldg.removeTubeInput(this, i);
                        }
                    }
                }
            }
            if (connected.size == 1) {
                network1.removeInput(connPorts);
                network1.removeSegment(this);
            } else if (connected.size > 1) {
                TubeSearcher searcher = new TubeSearcher(grid, this);
                Pair<Integer, Boolean> entry = connected.first();
                int dir = entry.first;
                searcher.search(dir);
                boolean allConnected = true;
                for (Pair<Integer, Boolean> check : connected) {
                    int dir1 = check.first;
                    if (dir != dir1) {
                        if (!searcher.check(dir1)) {
                            allConnected = false;
                            break;
                        }
                    }
                }
                if (allConnected) {
                    network1.removeInput(connPorts);
                    network1.removeSegment(this);
                } else {
                    searcher.setNetwork(new TubeNet());
                    for (Pair<Integer, Boolean> check : connected) {
                        int dir1 = check.first;
                        if (searcher.check(dir1)) {
                            check.second = true;
                        }
                    }
                    for (int i = 0; i < connected.size; i++) {
                        Pair<Integer, Boolean> set1 = connected.get(i);
                        if (!set1.second) {
                            searcher.clear();
                            searcher.search(set1.first);
                            searcher.setNetwork(new TubeNet());
                            for (int j = i; j < connected.size; j++) {
                                Pair<Integer, Boolean> check = connected.get(i);
                                int dir1 = check.first;
                                if (!check.second && set1.first != dir1) {
                                    if (searcher.check(dir1)) {
                                        check.second = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private Coords getNeighbourCoord(int dir) {
        int tgtX = xCoord;
        int tgtY = yCoord;
        switch (dir) {
            case 0 -> {
                tgtY--;
            }
            case 1 -> {
                tgtX++;
            }
            case 2 -> {
                tgtY++;
            }
            case 3 -> {
                tgtX--;
            }
        }
        return new Coords(tgtX, tgtY);
    }

    private Structure getNeighbour(Array<Array<Structure>> grid, int dir) {
        Coords tgt = getNeighbourCoord(dir);
        Structure target = null;
        if (tgt.x >= 0 && tgt.y >= 0 && tgt.x < grid.get(0).size && tgt.y < grid.size) {
            target = grid.get(tgt.y).get(tgt.x);
        }
        return target;
    }

    public boolean getDouble() {
        return hasDouble;
    }

    public boolean[] getConnections1() {
        return connections1;
    }

    public boolean[] getConnections2() {
        return connections2;
    }

    public void addConnection(Array<Array<Structure>> grid, int dir1, int dir2) {
        if (!hasDouble && !fullConnect) {
            if (connections1[dir1] ^ connections1[dir2]) {
                Array<Integer> connections = new Array<>();
                if (!connections1[dir1]) {
                    connections1[dir1] = true;
                    connections.add(dir1);
                }
                if (!connections1[dir2]) {
                    connections1[dir2] = true;
                    connections.add(dir2);
                }
                if (connections1[0] && connections1[1] && connections1[2] && connections1[3]) {
                    fullConnect = true;
                }
                connect(grid, 1, connections);
            } else if (!connections1[dir1] && !connections1[dir2]) {
                hasDouble = true;
                connections2[dir1] = true;
                connections2[dir2] = true;
                fullConnect = true;
                connect(grid, 2, new Array<>(new Integer[]{dir1, dir2}));
            }
        }
    }

    private static class TubeSearcher {
        Array<Array<Structure>> grid;
        Tube start;
        Array<Array<boolean[]>> visited;
        ObjectMap<Tube, boolean[]> visitedNet;
        Queue<DirectedCoords> visitQueue;
        ObjectMap<Building, ObjectSet<IOPort>> inPorts;
        ObjectSet<Building> outBldg;

        TubeSearcher(Array<Array<Structure>> gridIn, Tube st) {
            grid = gridIn;
            start = st;
            visited = new Array<>();
            for (Array<Structure> row : grid) {
                Array<boolean[]> rr = new Array<>();
                for (Structure s : row) {
                    rr.add(null);
                }
                visited.add(rr);
            }
            visitedNet = new ObjectMap<>();
            visited.get(start.yCoord).set(start.xCoord, new boolean[]{true, true, true, true});
            visitQueue = new Queue<>();
            inPorts = new ObjectMap<>();
            outBldg = new ObjectSet<>();
        }

        void clear() {
            visitQueue.clear();
            inPorts.clear();
            outBldg.clear();
            for (Array<boolean[]> row : visited) {
                for (int i = 0; i < row.size; i++) {
                    row.set(i, null);
                }
            }
            visitedNet.clear();
            visited.get(start.yCoord).set(start.xCoord, new boolean[]{true, true, true, true});
        }

        void search(int dir) {
            System.out.println();
            System.out.println("searching");

            // time to bfs the tube network
            visitQueue.addLast(new DirectedCoords(start.xCoord, start.yCoord, dir));
            while (!visitQueue.isEmpty()) {
                System.out.println();
                // get next
                DirectedCoords next = visitQueue.removeFirst().pointingToSide();
                System.out.println("visiting " + next);
                // continue if out of bounds
                if (next.x < 0 || next.y < 0 || next.y >= grid.size || next.x >= grid.get(next.y).size) {
                    continue;
                }
                // continue if visited already
                if (visited.get(next.y).get(next.x) != null && visited.get(next.y).get(next.x)[next.dir]) {
                    continue;
                } else {
                    Structure pointingTo = grid.get(next.y).get(next.x);
                    if (pointingTo instanceof Tube tube) {
                        System.out.println("found " + tube);

                        boolean[] newDirs = null;

                        if (tube.getNetworkNum(next.dir) == 1) {
                            System.out.println("connected to network 1");
                            newDirs = tube.getConnections1().clone();
                            if (!visitedNet.containsKey(tube)) {
                                visitedNet.put(tube, new boolean[2]);
                            }
                            visitedNet.get(tube)[0] = true;

                        } else if (tube.getNetworkNum(next.dir) == 2) {
                            System.out.println("connected to network 2");
                            newDirs = tube.getConnections2().clone();
                            if (!visitedNet.containsKey(tube)) {
                                visitedNet.put(tube, new boolean[2]);
                            }
                            visitedNet.get(tube)[1] = true;
                        }


                        if (newDirs != null) {

                            System.out.println("connections: "
                                + newDirs[0] + " "
                                + newDirs[1] + " "
                                + newDirs[2] + " "
                                + newDirs[3]);

                            if (visited.get(next.y).get(next.x) == null) {
                                visited.get(next.y).set(next.x, new boolean[4]);
                            }

                            boolean[] visitCell = visited.get(next.y).get(next.x);
                            for (int i = 0; i < 4; i++) {
                                System.out.println("resolving " + i);
                                if (newDirs[i]) {
                                    visitCell[i] = true;
                                    if (i != next.dir) {
                                        visitQueue.addLast(new DirectedCoords(next.x, next.y, i));
                                    }
                                }
                            }
                        }
                    } else if (pointingTo instanceof Building building) {
                        System.out.println("found " + building);
                        IOPort pointPort = building.getPortFor(next.x, next.y, next.dir);
                        if (pointPort != null) {
                            if (inPorts.containsKey(building)) {
                                inPorts.get(building).add(pointPort);
                            } else {
                                ObjectSet<IOPort> portSet = new ObjectSet<>(10);
                                portSet.add(pointPort);
                                inPorts.put(building, portSet);
                            }
                        } else {
                            outBldg.add(building);
                        }
                    }
                }
            }
        }

        boolean check(int dir) {
            DirectedCoords coords1 = new DirectedCoords(start.xCoord, start.yCoord, dir);
            DirectedCoords targetCoords = coords1.pointingToSide();
            boolean[] cell = visited.get(targetCoords.y).get(targetCoords.x);
            if (cell != null) {
                return cell[targetCoords.dir];
            }
            return false;
        }

        void setNetwork(TubeNet net) {
            System.out.println("setting network " + net);
            net.addInput(inPorts);
            for (ObjectMap.Entry<Tube, boolean[]> entry : visitedNet) {
                Tube tube = entry.key;
                System.out.println("setting " + tube);
                boolean[] nets = entry.value;
                System.out.println("connNets: " + nets[0] + " " + nets[1]);
                net.addSegment(tube);
                if (nets[0]) {
                    tube.setNetwork1(net);
                }
                if (nets[1]) {
                    tube.setNetwork2(net);
                }
            }
            for (Building building : outBldg) {
                System.out.println("connected building: " + building);
                building.updateNets();
            }
            System.out.println();
        }
    }
}
