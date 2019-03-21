package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sample.datamodel.Building;
import sample.datamodel.ControlSystem;
import sample.datamodel.Elevator;
import sample.datamodel.FloorCall;

public class Controller {
    @FXML
    private TextField startFloor;
    @FXML
    private TextField targetFloor;
    @FXML
    private Button callButton;
    @FXML
    private Label el1, el2, el3;

    private static Building building = new Building();
    private static ControlSystem controlSystem = building.getControlSystem();

    public void initialize() {
        // generate floors and elevators
        building.generateFloors();
        building.generateElevators();

        // start elevators
        for (int i = 0; i < building.getElevators().size(); i++) {
            Elevator current = building.getElevators().get(i);
            current.getThread().start();
        }

        System.out.println(building.getElevators().get(0).getThread());
        System.out.println(building.getElevators().get(1).getThread());
        System.out.println(building.getElevators().get(2).getThread());

        System.out.println("El 1 " + building.getElevators().get(0).getCurrentFloor());
        System.out.println("El 2 " + building.getElevators().get(1).getCurrentFloor());
        System.out.println("El 3 " + building.getElevators().get(2).getCurrentFloor());

    }

    @FXML
    public void watchFloors() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String currentfloor1 = ""+ building.getElevators().get(0).getCurrentFloor();
                String currentfloor2 = ""+ building.getElevators().get(1).getCurrentFloor();
                String currentfloor3 = ""+ building.getElevators().get(2).getCurrentFloor();
                el1.setText(currentfloor1);
                el2.setText(currentfloor2);
                el3.setText(currentfloor3);

            }
        });
    }

    @FXML
    public void onCallButtonClicked(ActionEvent e) {
        System.out.println("Calling from " + startFloor.getText() + " to " + targetFloor.getText());
        try {
            int fromFloor = Integer.parseInt(startFloor.getText());
            int toFloor = Integer.parseInt(targetFloor.getText());
            makeFloorCall(fromFloor, toFloor);
            startFloor.clear();
            targetFloor.clear();
        } catch (NumberFormatException ne) {
            System.out.println("Invalid input");
        }
    }
    public void makeFloorCall(int fromFloor, int toFloor) {
        int direction;
        if (fromFloor > 13 || fromFloor < 1 || toFloor > 13 || toFloor < 1) {
            System.out.println("Invalid floor");
            return;
        }
        if (fromFloor > toFloor) {
            direction = 0;
        } else if (fromFloor < toFloor) {
            direction = 1;
        } else {
            System.out.println("Already on floor " + toFloor);
            return;
        }
        FloorCall newCall = controlSystem.makeFloorCall(fromFloor, toFloor, direction);
        if (!controlSystem.addFloorCall(newCall)) {
            System.out.println("Could not add floor request to call stack");
        }
    }

    public Label getEl1() {
        return el1;
    }

    public Label getEl2() {
        return el2;
    }

    public Label getEl3() {
        return el3;
    }
}
