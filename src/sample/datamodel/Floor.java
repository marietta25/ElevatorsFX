package sample.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Floor {

    private int floorNumber;
    private List<Integer> requestsToGoDown;
    private List<Integer> requestsToGoUp;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.requestsToGoDown = new ArrayList<>();
        this.requestsToGoUp = new ArrayList<>();
    }

    // create new floor call to go up
    public FloorCall callToGoUp(int destinationFloor) {
        if (this.floorNumber == 13) {
            System.out.println("Cannot go up, final floor");
            return null;
        }
        // don't add new call if similar one already exists
        if (requestsToGoUp.contains(destinationFloor)) {
            System.out.println("Duplicate call");
            return null;
        }
        FloorCall newCall = new FloorCall(this.floorNumber, destinationFloor, 1);
        requestsToGoUp.add(destinationFloor);
        return newCall;
    }

    // create new floor call to go down
    public FloorCall callToGoDown(int destinationFloor) {
        if (this.floorNumber == 1) {
            System.out.println("Cannot go down, on first floor");
            return null;
        }
        // don't add new call if similar one already exists
        if (requestsToGoDown.contains(destinationFloor)) {
            System.out.println("Duplicate call");
            return null;
        }
        FloorCall newCall = new FloorCall(this.floorNumber, destinationFloor, 0);
        requestsToGoDown.add(destinationFloor);
        return newCall;
    }

    public List<Integer> getRequestsToGoDown() {
        return requestsToGoDown;
    }

    public List<Integer> getRequestsToGoUp() {
        return requestsToGoUp;
    }
}
