# ElevatorsFX

JavaFX 11 project

Simple multi-threaded application to move 3 elevators up and down inside a 13-story building.

Main requirements:
- it takes 2 seconds for an elevator to move 1 floor up or down
- speed is constant, eg no acceleration
- if an elevator is already in move, it won't stop if somebody makes a floor request outside of an elevator
- there is no maximum passenger capacity
- 1 up and 1 down button for every floor (except 1st and 13th floor)

Knows bugs and a to-do list
- there is no way to be sure which elevator is the first to answer a call, eg there isn't a method to determine which
elevator is currently idle and closest to the requested floor
- if a floor call is made from the same floor and direction as an already existing call, it counts as a separate floor request.
That means 2 elevators will go to the same floor, first one to arrive will move to the next passenger requested floor, the 
second one will wait for the next floor call to be made.
- visual: moving of elevators not yet implemented in app window. Movements are currently logged to console, each elevator 
thread in different color, --coloring works in IntelliJ)
- visual: if elevator stops at requested floor, mark the floor request as uncalled (change the color)
- tests!
