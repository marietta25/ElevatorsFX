package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sample.datamodel.Building;
import sample.datamodel.ControlSystem;
import sample.datamodel.Elevator;
import sample.datamodel.FloorCall;

public class Controller {
    @FXML
    private TextField destinationFrom13, destinationFrom12, destinationFrom11, destinationFrom10, destinationFrom9;
    @FXML
    private TextField destinationFrom8, destinationFrom7, destinationFrom6, destinationFrom5, destinationFrom4;
    @FXML
    private TextField destinationFrom3, destinationFrom2, destinationFrom1;
    @FXML
    private Button d13, d12, u12, d11, u11, d10, u10, d9, u9, d8, u8, d7, u7;
    @FXML
    private Button d6, u6, d5, u5, d4, u4, d3, u3, d2, u2, u1;

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
    public void onDirectionButtonClicked(ActionEvent e) {
        if (e.getSource().equals(d13)) {
            System.out.println("You clicked " + destinationFrom13.getText() + " from floor 13");
            int toFloor = Integer.parseInt(destinationFrom13.getText());
            makeFloorCall(13, toFloor);
        } else if (e.getSource().equals(d12) || e.getSource().equals(u12)) {
            int toFloor = Integer.parseInt(destinationFrom12.getText());
            makeFloorCall(12, toFloor);
        } else if (e.getSource().equals(d11) || e.getSource().equals(u11)) {
            int toFloor = Integer.parseInt(destinationFrom11.getText());
            makeFloorCall(11, toFloor);
        } else if (e.getSource().equals(d10) || e.getSource().equals(u10)) {
            int toFloor = Integer.parseInt(destinationFrom10.getText());
            makeFloorCall(10, toFloor);
        } else if (e.getSource().equals(d9) || e.getSource().equals(u9)) {
            int toFloor = Integer.parseInt(destinationFrom9.getText());
            makeFloorCall(9, toFloor);
        } else if (e.getSource().equals(d8) || e.getSource().equals(u8)) {
            int toFloor = Integer.parseInt(destinationFrom8.getText());
            makeFloorCall(8, toFloor);
        } else if (e.getSource().equals(d7) || e.getSource().equals(u7)) {
            int toFloor = Integer.parseInt(destinationFrom7.getText());
            makeFloorCall(7, toFloor);
        } else if (e.getSource().equals(d6) || e.getSource().equals(u6)) {
            int toFloor = Integer.parseInt(destinationFrom6.getText());
            makeFloorCall(6, toFloor);
        } else if (e.getSource().equals(d5) || e.getSource().equals(u5)) {
            int toFloor = Integer.parseInt(destinationFrom5.getText());
            makeFloorCall(5, toFloor);
        } else if (e.getSource().equals(d4) || e.getSource().equals(u4)) {
            int toFloor = Integer.parseInt(destinationFrom4.getText());
            makeFloorCall(4, toFloor);
        } else if (e.getSource().equals(d3) || e.getSource().equals(u3)) {
            int toFloor = Integer.parseInt(destinationFrom3.getText());
            makeFloorCall(3, toFloor);
        } else if (e.getSource().equals(d2) || e.getSource().equals(u2)) {
            int toFloor = Integer.parseInt(destinationFrom2.getText());
            makeFloorCall(2, toFloor);
        } else if (e.getSource().equals(u1)) {
            int toFloor = Integer.parseInt(destinationFrom1.getText());
            makeFloorCall(1, toFloor);
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
