# Simulation Cruise Control
A Java application that simulates the processes involved in a cruise control system (GUI implementation).

### CruiseControlSystem Directory
Contains all project code for creating the cruise control system.
  * Source folder contains the cruise package:
    * Two Interfaces ICarSpeed & ICruiseControl - allowing multiple inheritance of methods.
    * CruiseControlCanvas & SpeedometerCanvas are implemented to create the GUI for the cruise control and the cars speed.
    * CarSimulatorController & CruiseControlController used to implement functionality in the combined system.
    * SimulatorFrame is used to display the two canvas together in a single window, with accurate layout.
    * Simulator houses the main function used to execute the GUI and functionality.
    
### ltsa Directory
Contains the file for creating a Labelled Transition System for the Cruise Control's states.

An image of the transition system is displayed below:

![CruiseControl LTSA](/ltsa/cruisecontrol.png)
