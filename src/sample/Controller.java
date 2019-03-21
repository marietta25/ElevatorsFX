package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
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
    private Label el1, el2, el3;
    @FXML
    private Label floor13, floor12, floor11, floor10, floor9, floor8, floor7, floor6, floor5, floor4, floor3, floor2, floor1;

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

    }

    @FXML
    public void onCallButtonClicked(ActionEvent e) {
        try {
            int fromFloor = Integer.parseInt(startFloor.getText());
            int toFloor = Integer.parseInt(targetFloor.getText());
            makeFloorCall(fromFloor, toFloor);
            startFloor.clear();
            targetFloor.clear();
            colorLabel(fromFloor);
            colorLabel(toFloor);
        } catch (NumberFormatException ne) {
            System.out.println("Invalid input");
        }
    }

    @FXML
    public void colorLabel(int floor) {
        switch (floor) {
            case 1:
                floor1.setTextFill(Color.RED);
                break;
            case 2:
                floor2.setTextFill(Color.RED);
                break;
            case 3:
                floor3.setTextFill(Color.RED);
                break;
            case 4:
                floor4.setTextFill(Color.RED);
                break;
            case 5:
                floor5.setTextFill(Color.RED);
                break;
            case 6:
                floor6.setTextFill(Color.RED);
                break;
            case 7:
                floor7.setTextFill(Color.RED);
                break;
            case 8:
                floor8.setTextFill(Color.RED);
                break;
            case 9:
                floor9.setTextFill(Color.RED);
                break;
            case 10:
                floor10.setTextFill(Color.RED);
                break;
            case 11:
                floor11.setTextFill(Color.RED);
                break;
            case 12:
                floor12.setTextFill(Color.RED);
                break;
            case 13:
                floor13.setTextFill(Color.RED);
                break;
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
}
