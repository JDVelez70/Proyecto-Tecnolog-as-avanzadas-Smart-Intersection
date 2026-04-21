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

        // ── 5. Arduino ───────────────────────────────────────────────────────
        //
        //  CAMBIA ESTOS DOS VALORES SEGÚN TU SITUACIÓN:
        //
        //  SIMULATION_MODE = true  → sin Arduino físico (demo/Tinkercad)
        //  SIMULATION_MODE = false → con Arduino real conectado por USB
        //
        //  COM_PORT = el puerto que viste en el Administrador de dispositivos
        //             por ejemplo: "COM3", "COM4", "COM7"
        //
        // ─────────────────────────────────────────────────────────────────
        //  CONFIGURA AQUÍ TU PUERTO COM:
        //  1. Conecta el Arduino Mega por USB
        //  2. Abre Administrador de dispositivos → Puertos (COM y LPT)
        //  3. Busca "USB Serial" o "Arduino Mega" y anota el número
        //  4. Cambia "COM4" por el tuyo (ej: "COM3", "COM5", "COM7")
        //
        //  SIMULATION_MODE = false → usa el Arduino físico real
        //  SIMULATION_MODE = true  → modo demo sin hardware
        // ─────────────────────────────────────────────────────────────────
        boolean SIMULATION_MODE = false;
        String  COM_PORT        = "COM4";  // <-- cambia este número

        ArduinoConnector arduino = new ArduinoConnector(COM_PORT, SIMULATION_MODE);

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
