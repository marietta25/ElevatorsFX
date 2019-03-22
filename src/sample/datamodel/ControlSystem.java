package sample.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControlSystem {
    private List<Elevator> elevators;
    private List<Floor> floors;
    private List<String> calledFloors;
    private List<Integer> goUpFrom;
    private List<Integer> goDownFrom;

    public ControlSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
        this.calledFloors = new ArrayList<>();
        this.goUpFrom = new ArrayList<>();
        this.goDownFrom = new ArrayList<>();
    }

    // currently not implemented anywhere
    /*public Elevator findElevator(List<FloorCall> calls) {
        if (calls.isEmpty()) {
            System.out.println("No calls, no need for an elevator");
            return null;
        }
        FloorCall next = calls.get(0);
        int distance;
        int minDistance = 12;
        Elevator selectedElevator = null;

        List<Elevator> idleElevators = new ArrayList<>();
        for (Elevator el : this.elevators) {
            if (el.getCurrentDirection().equals("IDLE")) {
                idleElevators.add(el);
            }
        }
        if (idleElevators.isEmpty()) {
            System.out.println("All elevators are busy servicing requests");
            return null;
        }
        if (idleElevators.size() == 1) {
            selectedElevator = idleElevators.get(0);
            return selectedElevator;
        } else {
            for (int i = 0; i < idleElevators.size(); i++) {
                Elevator currentEl = idleElevators.get(i);
                distance = Math.abs(next.getStartFloor() - currentEl.getCurrentFloor());
                if (distance <= minDistance) {
                    minDistance = distance;
                    selectedElevator = currentEl;
                }
            }
            return selectedElevator;
        }
    }*/

    // find and return requested floor call from main requests list
    private synchronized String findStop(String startFloor, String direction) {
        String searched = startFloor + direction; //up or down
        for (String current : this.calledFloors) {
            if (current.equals(searched)) {
                return current;
            }
        }
        return null;
    }

    // remove floor call from lists
    public synchronized void removeStop(int floor, String direction) {

        if (direction.toLowerCase().equals("up")) {
            this.goUpFrom.remove(Integer.valueOf(floor));
            this.calledFloors.remove(floor+"up");
        } else if (direction.toLowerCase().equals("down")) {
            this.goDownFrom.remove(Integer.valueOf(floor));
            this.calledFloors.remove(floor+"down");
        } else {
            System.out.println("Check direction parameter");
        }
    }

    // create a new floor call in Floor object
    public FloorCall makeFloorCall(int fromFloor, int toFloor, int direction) {
        Floor calledFrom = this.floors.get(fromFloor - 1);
        FloorCall newCall;

        if (direction == 1) {
            // request to go up
            newCall = calledFrom.callToGoUp(toFloor);

        } else if (direction == 0) {
            // request to go down
            newCall = calledFrom.callToGoDown(toFloor);
        } else {
            return null;
        }
        return newCall;
    }

    // add created floor call to lists
    public boolean addFloorCall(FloorCall call) {
        if (call != null) {
            if (addFloorCall(call.getStartFloor(), call.getDestinationFloor(), call.getDirection())) {
                synchronized (this.calledFloors) {

                    if (call.getDirection() == 1) {
                        this.calledFloors.add(call.getStartFloor()+"up");
                    } else {
                        this.calledFloors.add(call.getStartFloor() + "down");
                    }
                    this.calledFloors.notifyAll();
                }
                return true;
            }
            return false;
        }

        return false;
    }

    private boolean addFloorCall(int startFloor, int destinationFloor, int direction) {
        if (direction == 1) {
            // request to go up
            synchronized (this.goUpFrom) {
                this.goUpFrom.add(startFloor);
                Collections.sort(goUpFrom);
                System.out.println("Added floor call from " + startFloor + " to " + destinationFloor + " to uprequest list");
                return true;
            }
        } else if (direction == 0) {
            // request to go down
            synchronized (this.goDownFrom) {
                this.goDownFrom.add(startFloor);
                Collections.sort(this.goDownFrom);
                System.out.println("Added floor call from " + startFloor + " to " + destinationFloor + " to downrequests list");
                return true;
            }
        } else {
            return false;
        }
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<String> getCalledFloors() {
        return calledFloors;
    }

    public List<Integer> getGoUpFrom() {
        return goUpFrom;
    }

    public List<Integer> getGoDownFrom() {
        return goDownFrom;
    }
}
