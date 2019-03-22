package sample.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.interrupted;

public class Elevator implements Runnable {

    private int elevatorNumber;
    private int currentFloor;
    private boolean servicingUpCall;
    private String currentDirection;
    private ArrayList<Integer> requestedFloorsUp;
    private ArrayList<Integer> requestedFloorsDown;
    private ControlSystem controlSystem;
    private Thread thread;
    private String color; // to color different thread outputs in IntelliJ

    public Elevator(int elevatorNumber, int currentFloor, ControlSystem controlSystem, String color) {
        this.elevatorNumber = elevatorNumber;

        if (currentFloor < 1 || currentFloor > 13) {
            System.out.println("Invalid floor");
        } else {
            this.currentFloor = currentFloor;
        }

        this.servicingUpCall = false;
        this.currentDirection = "UP";
        this.requestedFloorsUp = new ArrayList<>();
        this.requestedFloorsDown = new ArrayList<>();
        this.controlSystem = controlSystem;
        this.thread = new Thread(this, "Elevator " + this.elevatorNumber);
        this.color = color;
    }

    private synchronized void determineDirection() {
        // change direction if elevator reaches first or last floor
        if (this.currentFloor == 13 && this.currentDirection.equals("UP")) {
            this.currentDirection = "DOWN";
        } else if (this.currentFloor == 1 && this.currentDirection.equals("DOWN")) {
            this.currentDirection = "UP";
        }
    }

    // FIXME! ugly spaghetti code
    // return next stop for an elevator
    private int getNextStop() throws InterruptedException {
        List<String> calledFloors = controlSystem.getCalledFloors();

        synchronized (controlSystem.getCalledFloors()) {
            List<Integer> upRequests = controlSystem.getGoUpFrom();
            List<Integer> downRequests = controlSystem.getGoDownFrom();

            while (upRequests.isEmpty() && downRequests.isEmpty() && this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                // no floor calls nor passenger requests are left to serve
                this.currentDirection = "IDLE";
                System.out.println(color + "[" + this.elevatorNumber + "] is Waiting for a floor call..");
                controlSystem.getCalledFloors().wait();
            }

            // sort passenger requests
            Collections.sort(this.requestedFloorsUp);
            Collections.sort(this.requestedFloorsDown);

            System.out.println(color + "[" + this.elevatorNumber + "] passenger requests to go up " + this.requestedFloorsUp);
            System.out.println(color + "[" + this.elevatorNumber + "] passenger requests to go down " + this.requestedFloorsDown);


            int nextStop = -1;
            String callDirection = "up";

            if (this.currentDirection.equals("IDLE") || this.currentDirection.equals("UP")) {
                if (upRequests.isEmpty() && this.requestedFloorsUp.isEmpty()) {
                    this.currentDirection = "DOWN";
                } else if (!upRequests.isEmpty() && !this.requestedFloorsUp.isEmpty()) {
                    // todo - ...
                    nextStop = upRequests.get(0);
                    if (this.requestedFloorsUp.contains(nextStop)) {
                        // remove duplicate stops
                        this.requestedFloorsUp.remove(Integer.valueOf(nextStop));
                    }
                    //
                    this.servicingUpCall = true;
                } else if (!upRequests.isEmpty()) {
                    nextStop = upRequests.get(0);
                    this.servicingUpCall = true;
                } else {
                    nextStop = this.requestedFloorsUp.get(0);
                    this.requestedFloorsUp.remove(Integer.valueOf(nextStop));
                }
            }

            if (this.currentDirection.equals("DOWN")) {
                callDirection = "down";
                // direction down
                if (downRequests.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                    this.currentDirection = "UP";
                } else if(!downRequests.isEmpty() && !this.requestedFloorsDown.isEmpty()) {
                    nextStop = downRequests.get(downRequests.size() - 1);
                    if (this.requestedFloorsDown.contains(nextStop)) {
                        this.requestedFloorsDown.remove(Integer.valueOf(nextStop));
                    }
                    this.servicingUpCall = false;
                } else if (!downRequests.isEmpty()) {
                    nextStop = downRequests.get(downRequests.size() - 1);
                    this.servicingUpCall = false;
                } else {
                    nextStop = this.requestedFloorsDown.get(this.requestedFloorsDown.size() - 1);
                    this.requestedFloorsDown.remove(Integer.valueOf(nextStop));
                }
            }

            if (callDirection.equals("up")) {
                upRequests.remove(Integer.valueOf(nextStop));
                controlSystem.removeStop(nextStop, "up");
                calledFloors.remove(nextStop + "up");
            } else {
                downRequests.remove(Integer.valueOf(nextStop));
                controlSystem.removeStop(nextStop, "down");
                calledFloors.remove(nextStop + "down");
            }

            return nextStop;
        }
    }


    private void stop() {
        // reached floor that the request came from or reached requested destination floor
        System.out.println(color + "[" + this.elevatorNumber + "] STOPPING on floor " + this.currentFloor);

        // requests to go up or down from the current floor
        Floor stoppedFloor = controlSystem.getFloors().get(this.currentFloor-1);
        List<Integer> requestsUp = stoppedFloor.getRequestsToGoUp();
        List<Integer> requestsDown = stoppedFloor.getRequestsToGoDown();

        // add passenger requests to elevator stops and remove them from floor object
        if (this.servicingUpCall) {
            this.currentDirection = "UP";
            for (Integer floor : requestsUp) {
                this.requestedFloorsUp.add(Integer.valueOf(floor));
            }
            requestsUp.removeAll(this.requestedFloorsUp);
        } else {
            this.currentDirection = "DOWN";
            for (Integer floor : requestsDown) {
                this.requestedFloorsDown.add(Integer.valueOf(floor));

            }
            requestsDown.removeAll(this.requestedFloorsDown);
        }

        // pause elevator to let passengers out / in
        pauseThread(4000);
    }

    // pause elevator thread for a given time (e.g move between floors, let passengers out, etc
    private void pauseThread(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // move elevator up and down depending on destination floor
    private void move(int destinationFloor) {
        if (destinationFloor < 1 || destinationFloor > 13) {
            System.out.println("Invalid destination floor");
            return;
        }

        determineDirection();

        if (this.currentFloor != destinationFloor) {
            System.out.println(color + "[" + this.elevatorNumber + "] starting to move from floor " + this.currentFloor);
        }

        while (true) {
            if (destinationFloor < this.currentFloor) {
                this.currentDirection = "DOWN";
                pauseThread(2000);
                this.currentFloor--;
                System.out.println(color + "[" + this.elevatorNumber + "] going " + this.currentDirection + ", reached floor " + this.currentFloor);
            } else if (destinationFloor > this.currentFloor) {

                this.currentDirection = "UP";
                pauseThread(2000);
                this.currentFloor++;
                System.out.println(color + "[" + this.elevatorNumber + "] going " + this.currentDirection + ", reached floor " + this.currentFloor);
            } else {
                // reached destination floor
                this.currentFloor = destinationFloor;
                stop();
                return;
            }
        }
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        System.out.println(color + "Elevator " + this.elevatorNumber + " thread is running");
        while (true) {
            if (interrupted()) {
                return;
            }

            try {
                int nextStop = getNextStop();
                System.out.println(color + "[" + elevatorNumber + "] is servicing next request to floor " + nextStop);
                move(nextStop);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
