package ui;

import arduino.ArduinoConnector;
import controller.TrafficController;
import model.*;
import sensor.PedestrianSensor;
import sensor.VehicleSensor;
import persistence.IntersectionRepository;
import policy.AdaptiveTimingPolicy;
import policy.FixedTimingPolicy;

import java.util.Date;
import java.util.Scanner;

/**
 * ConsoleUI — GRASP: Controller de interfaz + SOLID: SRP
 * Toda la interacción con el usuario ocurre aquí.
 * No contiene lógica de negocio: solo captura entrada y delega.
 */
public class ConsoleUI {

    private final TrafficController trafficController;
    private final ArduinoConnector  arduino;
    private final IntersectionRepository repository;
    private final VehicleSensor vehicleSensor;
    private final PedestrianSensor pedestrianSensor;
    private final Scanner scanner;
    private boolean running;

    public ConsoleUI(TrafficController trafficController,
                     ArduinoConnector arduino,
                     IntersectionRepository repository,
                     VehicleSensor vehicleSensor,
                     PedestrianSensor pedestrianSensor) {
        this.trafficController  = trafficController;
        this.arduino            = arduino;
        this.repository         = repository;
        this.vehicleSensor      = vehicleSensor;
        this.pedestrianSensor   = pedestrianSensor;
        this.scanner            = new Scanner(System.in);
        this.running            = false;
    }

    /** Punto de entrada principal de la UI */
    public void start() {
        running = true;
        printBanner();
        arduino.connect();
        System.out.println();

        while (running) {
            printMenu();
            String option = scanner.nextLine().trim();
            handleOption(option);
        }

        arduino.disconnect();
        System.out.println("\n[SISTEMA] Sesión finalizada. ¡Hasta pronto!");
    }

    private void handleOption(String option) {
        System.out.println();
        switch (option) {
            case "1" -> simulateVehicleDetection();
            case "2" -> simulatePedestrianRequest();
            case "3" -> simulateEmergencyVehicle();
            case "4" -> changeMode();
            case "5" -> showStatus();
            case "6" -> showPersistence();
            case "7" -> generateReport();
            case "8" -> runNormalCycle();
            case "0" -> running = false;
            default  -> System.out.println("  ⚠️  Opción no válida. Intenta de nuevo.");
        }
        System.out.println();
    }

    // ─── UC1 (IMPLEMENTADO) ─────────────────────────────────────────────────

    /**
     * UC1: Detección de Vehículo → Control de Semáforo (CASO IMPLEMENTADO)
     * Simula la lectura del HC-SR04 y activa el semáforo.
     */
    private void simulateVehicleDetection() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  UC1: DETECCIÓN DE VEHÍCULO [ACTIVO] ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("  Ingresa distancia simulada (cm) [ej: 15 = vehículo / 50 = libre]: ");

        try {
            double dist = Double.parseDouble(scanner.nextLine().trim());
            arduino.setSimulatedDistance(dist);
            double reading = arduino.readDistance();

            System.out.println("  [HC-SR04] Distancia leída: " + reading + " cm");

            // Delegar al controlador → lógica → persistencia
            trafficController.receiveSensorData(reading);

            // Enviar comando LED al Arduino
            String ledColor = (reading < 20) ? "GREEN" : "RED";
            arduino.sendLedCommand(ledColor);

        } catch (NumberFormatException e) {
            System.out.println("  ❌ Valor inválido. Ingresa un número.");
        }
    }

    // ─── UC2 ─────────────────────────────────────────────────────────────────

    /**
     * UC2: Solicitud Peatonal
     */
    private void simulatePedestrianRequest() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  UC2: SOLICITUD PEATONAL (DEFINIDO)    ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("  Simulando presión del botón peatonal...");

        pedestrianSensor.press();
        if (pedestrianSensor.detectPedestrian()) {
            PedestrianRequest request = pedestrianSensor.createRequest();
            System.out.println("  Solicitud creada: " + request);
            trafficController.delegateToCycle(request);
            arduino.sendLedCommand("YELLOW");
        }
    }

    // ─── UC3 ─────────────────────────────────────────────────────────────────

    /**
     * UC3: Vehículo de Emergencia
     */
    private void simulateEmergencyVehicle() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  UC3: VEHÍCULO DE EMERGENCIA (DEFINIDO)║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("  Placa del vehículo de emergencia: ");
        String plate = scanner.nextLine().trim();

        EmergencyVehicle ev = new EmergencyVehicle(plate, "AMBULANCIA", 80.0, 1);
        trafficController.activateEmergencyMode(ev);
        arduino.sendLedCommand("RED");
    }

    // ─── UTILIDADES ──────────────────────────────────────────────────────────

    private void changeMode() {
        System.out.println("  Modos disponibles: NORMAL | ADAPTIVE | EMERGENCY");
        System.out.print("  Selecciona modo: ");
        String mode = scanner.nextLine().trim().toUpperCase();
        trafficController.setMode(mode);
    }

    private void showStatus() {
        System.out.println("══════════════ ESTADO DEL SISTEMA ══════════════");
        System.out.println("  Controlador : " + trafficController);
        System.out.println("  Semáforo    : " + trafficController.getCycleController().getTrafficLight());
        System.out.println("  Ciclo actual: " + trafficController.getCycleController().getCurrentState());
        System.out.println("  Política    : " + trafficController.getCycleController().getTimingPolicy());
        System.out.println("  Sensor auto : " + vehicleSensor);
        System.out.println("  Sensor peatón: " + pedestrianSensor);
        System.out.println("  Arduino     : " + arduino);
        System.out.println("═════════════════════════════════════════════════");
    }

    private void showPersistence() {
        repository.printAllReadings();
        repository.printAllActions();
    }

    private void generateReport() {
        int count = vehicleSensor.getVehicleCount();
        double density = count / 5.0; // veh/min aproximado
        TrafficReport report = new TrafficReport(
            "RPT-" + System.currentTimeMillis(), new Date(), density
        );
        report.generateReport();
        repository.saveAction("SYSTEM", "REPORT_GENERATED", new Date());
    }

    private void runNormalCycle() {
        System.out.println("  Ejecutando ciclo normal completo...");
        trafficController.getCycleController().runNormalCycle();
        arduino.sendLedCommand(
            trafficController.getCycleController().getTrafficLight().getCurrentColor()
        );
    }

    // ─── UI HELPERS ──────────────────────────────────────────────────────────

    private void printBanner() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║        SMART STREET INTERSECTION SYSTEM          ║");
        System.out.println("║     Universidad de los Llanos — 2026-I           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private void printMenu() {
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│                   MENÚ PRINCIPAL             │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│  [1] Simular detección de vehículo  ★ UC1   │");
        System.out.println("│  [2] Simular solicitud peatonal       UC2   │");
        System.out.println("│  [3] Simular vehículo de emergencia   UC3   │");
        System.out.println("│  [4] Cambiar modo del controlador           │");
        System.out.println("│  [5] Ver estado del sistema                 │");
        System.out.println("│  [6] Ver datos persistidos                  │");
        System.out.println("│  [7] Generar reporte de tráfico             │");
        System.out.println("│  [8] Ejecutar ciclo normal completo         │");
        System.out.println("│  [0] Salir                                  │");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.print("Opción: ");
    }
}
