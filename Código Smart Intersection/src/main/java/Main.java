import arduino.ArduinoConnector;
import controller.TrafficController;
import model.*;
import sensor.VehicleSensor;
import sensor.PedestrianSensor;
import persistence.IntersectionRepository;
import ui.ConsoleUI;


public class Main {

    public static void main(String[] args) {

        String dataPath = "data";
        IntersectionRepository repository = new IntersectionRepository(dataPath);

        SmartIntersection intersection = new SmartIntersection("INT-001", "Calle 15 x Carrera 8");
        TrafficLight trafficLight      = new TrafficLight("TL-NORTE");
        User operator                  = new User("USR-01", "Carlos López", "OPERATOR");

        VehicleSensor vehicleSensor       = new VehicleSensor("VS-01", 1);
        PedestrianSensor pedestrianSensor = new PedestrianSensor("PS-01", "CW-NORTE");

        TrafficController trafficController = new TrafficController(
            "TC-001",
            trafficLight,
            vehicleSensor,
            pedestrianSensor,
            repository
        );


        boolean SIMULATION_MODE = false;
        String  COM_PORT        = "COM5"; 

        ArduinoConnector arduino = new ArduinoConnector(COM_PORT, SIMULATION_MODE);

        operator.monitorIntersection();
        intersection.manageTrafficFlow();

        ConsoleUI ui = new ConsoleUI(
            trafficController,
            arduino,
            repository,
            vehicleSensor,
            pedestrianSensor
        );

        ui.start();
    }
}
