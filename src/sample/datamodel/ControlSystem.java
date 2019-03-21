package sample.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControlSystem {
    private List<Elevator> elevators;
    private List<Floor> floors;
    private List<FloorCall> calledFloors;
    private ArrayList<FloorCall> upRequests;
    private ArrayList<FloorCall> downRequests;

    public ControlSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
        this.calledFloors = new ArrayList<>();
        this.upRequests = new ArrayList<>();
        this.downRequests = new ArrayList<>();
    }

    public Elevator findElevator(List<FloorCall> calls) {
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
    }

    // find and return requested floor call from main requests list
    private synchronized FloorCall findStop(int startFloor, int destinationFloor) {
        for (FloorCall current : this.calledFloors) {
            if (current.getStartFloor() == startFloor && current.getDestinationFloor() == destinationFloor) {
                return current;
            }
        }
        return null;
    }

    // remove floor call from lists
    public synchronized void removeStop(int floor, int destinationFloor, String direction) {
        FloorCall found = findStop(floor, destinationFloor);

        if (found != null) {
            if (direction.toLowerCase().equals("up")) {
                this.upRequests.remove(findStop(floor, destinationFloor));
                this.calledFloors.remove(findStop(floor, destinationFloor));
            } else if (direction.toLowerCase().equals("down")) {
                this.downRequests.remove(findStop(floor, destinationFloor));
                this.calledFloors.remove(findStop(floor, destinationFloor));
            } else {
                System.out.println("Check direction parameter");
            }
        } else {
            System.out.println("Could not find requested stop");
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

                    this.calledFloors.add(call);
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
            synchronized (this.upRequests) {

                this.upRequests.add(new FloorCall(startFloor, destinationFloor, direction));
                Collections.sort(this.upRequests, FloorCall.FloorCallSort);
                System.out.println("Added floor call from " + startFloor + " to " + destinationFloor + " to uprequest list");
                return true;
            }
        } else if (direction == 0) {
            // request to go down
            synchronized (this.downRequests) {

                this.downRequests.add(new FloorCall(startFloor, destinationFloor, direction));
                Collections.sort(this.downRequests, FloorCall.FloorCallSort);
                System.out.println("Added floor call from " + startFloor + " to " + destinationFloor + " to downrequests list");
                return true;
            }
        } else {
            return false;
        }
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<FloorCall> getCalledFloors() {
        return calledFloors;
    }

    public ArrayList<FloorCall> getUpRequests() {
        return upRequests;
    }

    public ArrayList<FloorCall> getDownRequests() {
        return downRequests;
    }
}
