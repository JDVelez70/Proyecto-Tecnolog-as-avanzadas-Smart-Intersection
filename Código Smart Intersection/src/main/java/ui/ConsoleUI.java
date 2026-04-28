package ui;

import arduino.ArduinoConnector;
import controller.TrafficController;
import model.*;
import sensor.PedestrianSensor;
import sensor.VehicleSensor;
import persistence.IntersectionRepository;

import java.util.Date;
import java.util.Scanner;

/**
 * ConsoleUI — GRASP: Controller de interfaz + SOLID: SRP
 * Toda la interaccion con el usuario ocurre aqui.
 * No contiene logica de negocio: solo captura entrada y delega.
 */
public class ConsoleUI {

    private final TrafficController      trafficController;
    private final ArduinoConnector       arduino;
    private final IntersectionRepository repository;
    private final VehicleSensor          vehicleSensor;
    private final PedestrianSensor       pedestrianSensor;
    private final Scanner                scanner;
    private boolean running;

    public ConsoleUI(TrafficController trafficController,
                     ArduinoConnector arduino,
                     IntersectionRepository repository,
                     VehicleSensor vehicleSensor,
                     PedestrianSensor pedestrianSensor) {
        this.trafficController = trafficController;
        this.arduino           = arduino;
        this.repository        = repository;
        this.vehicleSensor     = vehicleSensor;
        this.pedestrianSensor  = pedestrianSensor;
        this.scanner           = new Scanner(System.in);
        this.running           = false;
    }

    public void start() {
        running = true;
        printBanner();
        arduino.connect();

        // Registrar callback: cada vez que CycleController cambia el color,
        // ConsoleUI envia el comando al Arduino automaticamente
        trafficController.getCycleController().setLedCallback(
            color -> arduino.sendLedCommand(color)
        );

        System.out.println();

        while (running) {
            printMenu();
            String option = scanner.nextLine().trim();
            handleOption(option);
        }

        arduino.disconnect();
        System.out.println("\n[SISTEMA] Sesion finalizada. Hasta pronto!");
    }

    private void handleOption(String option) {
        System.out.println();
        switch (option) {
            case "1" -> readSensorLive();
            case "2" -> simulatePedestrianRequest();
            case "3" -> simulateEmergencyVehicle();
            case "4" -> changeMode();
            case "5" -> showStatus();
            case "6" -> showPersistence();
            case "7" -> generateReport();
            case "8" -> runNormalCycle();
            case "0" -> running = false;
            default  -> System.out.println("  Opcion no valida. Intenta de nuevo.");
        }
        System.out.println();
    }

    // ─── UC1 (IMPLEMENTADO) ──────────────────────────────────────────────────

    /**
     * UC1: Lee el HC-SR04 en tiempo real sin limite de tiempo.
     * - Sin delays: readDistance() regula la cadencia esperando el proximo dato.
     * - Siempre descarta lecturas viejas del buffer (logica en ArduinoConnector).
     * - Solo envia comando al Arduino cuando cambia el estado (verde <-> rojo).
     * - Escribe Q + ENTER para volver al menu.
     */
    private void readSensorLive() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       UC1: DETECCION DE VEHICULO [ACTIVO]        ║");
        System.out.println("║  Sensor HC-SR04 leyendo en tiempo real.          ║");
        System.out.println("║  Acerca un objeto al sensor para detectarlo.     ║");
        System.out.println("║  Escribe  Q + ENTER  para volver al menu.        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        // Hilo escucha Q para detener el loop
        final boolean[] stop = { false };
        Thread inputThread = new Thread(() -> {
            try {
                while (!stop[0]) {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();
                        if (line.equalsIgnoreCase("Q")) {
                            stop[0] = true;
                        }
                    }
                }
            } catch (Exception ignored) {}
        });
        inputThread.setDaemon(true);
        inputThread.start();

        String lastLedColor = "";

        while (!stop[0]) {
            double reading = arduino.readDistance();

            // -1 significa que aun no hay dato nuevo, reintentar de inmediato
            if (reading < 0) continue;

            boolean detected = reading < 20.0;
            String  ledColor = detected ? "GREEN" : "RED";

            System.out.printf("  [HC-SR04] %6.1f cm  |  %s%n",
                reading,
                detected ? "VEHICULO DETECTADO  ->  LED VERDE"
                         : "Libre               ->  LED ROJO");

            // Enviar al Arduino solo cuando el estado cambia
            if (!ledColor.equals(lastLedColor)) {
                trafficController.receiveSensorData(reading);
                arduino.sendLedCommand(ledColor);
                lastLedColor = ledColor;
            }
            // Sin sleep — readDistance() ya espera el proximo dato del Arduino
        }

        arduino.sendLedCommand("RED");
        System.out.println("\n  [UC1] Monitoreo detenido. Volviendo al menu...");
    }

    // ─── UC2 ─────────────────────────────────────────────────────────────────

    private void simulatePedestrianRequest() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  UC2: SOLICITUD PEATONAL               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("  Simulando presion del boton peatonal...");

        pedestrianSensor.press();
        if (pedestrianSensor.detectPedestrian()) {
            PedestrianRequest request = pedestrianSensor.createRequest();
            System.out.println("  Solicitud creada: " + request);
            trafficController.delegateToCycle(request);
            arduino.sendLedCommand("YELLOW");
        }
    }

    // ─── UC3 ─────────────────────────────────────────────────────────────────

    private void simulateEmergencyVehicle() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  UC3: VEHICULO DE EMERGENCIA           ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("  Placa del vehiculo de emergencia: ");
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
        System.out.println("  Controlador  : " + trafficController);
        System.out.println("  Semaforo     : " + trafficController.getCycleController().getTrafficLight());
        System.out.println("  Estado ciclo : " + trafficController.getCycleController().getCurrentState());
        System.out.println("  Politica     : " + trafficController.getCycleController().getTimingPolicy());
        System.out.println("  Sensor auto  : " + vehicleSensor);
        System.out.println("  Sensor peatón: " + pedestrianSensor);
        System.out.println("  Arduino      : " + arduino);
        System.out.println("═════════════════════════════════════════════════");
    }

    private void showPersistence() {
        repository.printAllReadings();
        repository.printAllActions();
    }

    private void generateReport() {
        int    count   = vehicleSensor.getVehicleCount();
        double density = count / 5.0;
        TrafficReport report = new TrafficReport(
            "RPT-" + System.currentTimeMillis(), new Date(), density
        );
        report.generateReport();
        repository.saveAction("SYSTEM", "REPORT_GENERATED", new Date());
    }

    private void runNormalCycle() {
        System.out.println("  Ejecutando ciclo normal completo...");
        System.out.println("  VERDE -> AMARILLO -> ROJO  (el Arduino cambia en cada paso)");
        trafficController.getCycleController().runNormalCycle();
        // El LedCallback se encarga de enviar cada color al Arduino automaticamente
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
        System.out.println("│                 MENU PRINCIPAL               │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│  [1] Leer sensor HC-SR04 en vivo    ★ UC1   │");
        System.out.println("│  [2] Simular solicitud peatonal       UC2   │");
        System.out.println("│  [3] Simular vehiculo de emergencia   UC3   │");
        System.out.println("│  [4] Cambiar modo del controlador           │");
        System.out.println("│  [5] Ver estado del sistema                 │");
        System.out.println("│  [6] Ver datos persistidos                  │");
        System.out.println("│  [7] Generar reporte de trafico             │");
        System.out.println("│  [8] Ejecutar ciclo normal completo         │");
        System.out.println("│  [0] Salir                                  │");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.print("Opcion: ");
    }
}
