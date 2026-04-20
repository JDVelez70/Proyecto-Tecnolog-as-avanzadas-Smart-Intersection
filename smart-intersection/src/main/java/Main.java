import arduino.ArduinoConnector;
import controller.TrafficController;
import model.*;
import sensor.VehicleSensor;
import sensor.PedestrianSensor;
import persistence.IntersectionRepository;
import ui.ConsoleUI;

/**
 * Main — Punto de entrada del sistema Smart Street Intersection.
 *
 * GRASP: Creator — Main ensambla el grafo de objetos.
 * SOLID: DIP — las dependencias se inyectan manualmente (sin frameworks).
 *
 * Arquitectura vertical implementada:
 *   ConsoleUI → TrafficController → CycleController → TrafficLight
 *                                ↘ VehicleSensor
 *                                ↘ PedestrianSensor
 *                                ↘ IntersectionRepository (persistencia)
 *               ArduinoConnector (hardware / simulación Tinkercad)
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Persistencia ─────────────────────────────────────────────────
        String dataPath = "data";
        IntersectionRepository repository = new IntersectionRepository(dataPath);

        // ── 2. Modelos de dominio ────────────────────────────────────────────
        SmartIntersection intersection = new SmartIntersection("INT-001", "Calle 15 x Carrera 8");
        TrafficLight trafficLight      = new TrafficLight("TL-NORTE");
        User operator                  = new User("USR-01", "Carlos López", "OPERATOR");

        // ── 3. Sensores ──────────────────────────────────────────────────────
        VehicleSensor vehicleSensor       = new VehicleSensor("VS-01", 1);
        PedestrianSensor pedestrianSensor = new PedestrianSensor("PS-01", "CW-NORTE");

        // ── 4. Controlador principal ─────────────────────────────────────────
        TrafficController trafficController = new TrafficController(
            "TC-001",
            trafficLight,
            vehicleSensor,
            pedestrianSensor,
            repository
        );

        // ── 5. Arduino (simulado en Tinkercad) ──────────────────────────────
        ArduinoConnector arduino = new ArduinoConnector("COM3"); // cambiar a /dev/ttyUSB0 en Linux

        // ── 6. Iniciar sesión ────────────────────────────────────────────────
        operator.monitorIntersection();
        intersection.manageTrafficFlow();

        // ── 7. UI Console ────────────────────────────────────────────────────
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
