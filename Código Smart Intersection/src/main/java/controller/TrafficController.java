package controller;

import model.*;
import sensor.VehicleSensor;
import sensor.PedestrianSensor;
import policy.TimingPolicy;
import policy.AdaptiveTimingPolicy;
import policy.FixedTimingPolicy;
import persistence.IntersectionRepository;

import java.util.Date;

/**
 * TrafficController — GRASP: Controller principal del sistema.
 * Coordina sensores, semáforos, reportes y alertas.
 * SOLID: SRP — delega cada responsabilidad a la clase correspondiente.
 * SOLID: DIP — depende de abstracciones (Sensor, TimingPolicy).
 */
public class TrafficController {

    private String controllerId;
    private String mode; // "NORMAL", "ADAPTIVE", "EMERGENCY"

    private CycleController cycleController;
    private VehicleSensor vehicleSensor;
    private PedestrianSensor pedestrianSensor;
    private TrafficLight trafficLight;
    private IntersectionRepository repository;

    public TrafficController(String controllerId,
                             TrafficLight trafficLight,
                             VehicleSensor vehicleSensor,
                             PedestrianSensor pedestrianSensor,
                             IntersectionRepository repository) {
        this.controllerId  = controllerId;
        this.mode          = "NORMAL";
        this.trafficLight  = trafficLight;
        this.vehicleSensor = vehicleSensor;
        this.pedestrianSensor = pedestrianSensor;
        this.repository    = repository;
        this.cycleController = new CycleController(trafficLight, new FixedTimingPolicy());
    }

    /**
     * GRASP: Information Expert — recibe dato del sensor y decide qué hacer.
     * UC1 (implementado): Lee señal del VehicleSensor y activa semáforo.
     */
    public void receiveSensorData(double distanceCm) {
        vehicleSensor.updateDistance(distanceCm);
        boolean detected = vehicleSensor.detectVehicle();

        System.out.println("[CTRL] Sensor " + vehicleSensor.getSensorId()
                + " → distancia=" + distanceCm + "cm | detección=" + detected);

        if (detected) {
            optimizeTraffic();
            // Persistir la lectura
            repository.saveSensorReading(
                vehicleSensor.getSensorId(),
                distanceCm,
                detected,
                new Date()
            );
        }
    }

    /**
     * GRASP: Controller — delega al CycleController la lógica del semáforo.
     * Si hay muchos vehículos → cambia a política adaptativa.
     */
    public void optimizeTraffic() {
        int count = vehicleSensor.getVehicleCount();
        System.out.println("[CTRL] Optimizando tráfico. Vehículos detectados: " + count);

        if ("ADAPTIVE".equals(mode) && count > 3) {
            TimingPolicy adaptive = new AdaptiveTimingPolicy(count);
            cycleController.setTimingPolicy(adaptive);
            System.out.println("[CTRL] Política ADAPTATIVA activada: " + adaptive);
        }

        cycleController.changeLights("GREEN");

        // Persistir acción
        repository.saveAction(controllerId, "LIGHT_GREEN", new Date());
    }

    /**
     * GRASP: Controller — delega la solicitud peatonal al CycleController.
     */
    public void delegateToCycle(PedestrianRequest request) {
        System.out.println("[CTRL] Delegando solicitud peatonal al CycleController...");
        cycleController.handlePedestrianRequest(request);
        repository.saveAction(controllerId, "PEDESTRIAN_CYCLE", new Date());
    }

    /**
     * Activa modo emergencia: semáforo en rojo para dar paso al vehículo de emergencia.
     */
    public void activateEmergencyMode(EmergencyVehicle ev) {
        this.mode = "EMERGENCY";
        ev.requestPriority();
        cycleController.changeLights("RED");
        Alert alert = new Alert("ALT-" + System.currentTimeMillis(),
                "CRITICAL", "Vehículo de emergencia: " + ev.getPlateNumber());
        alert.sendAlert();
        repository.saveAction(controllerId, "EMERGENCY_MODE", new Date());
    }

    public void setMode(String mode) {
        this.mode = mode;
        System.out.println("[CTRL] Modo cambiado a: " + mode);
    }

    public String getControllerId()       { return controllerId; }
    public String getMode()               { return mode; }
    public CycleController getCycleController() { return cycleController; }
    public VehicleSensor getVehicleSensor()     { return vehicleSensor; }

    @Override
    public String toString() {
        return "TrafficController [id=" + controllerId + ", mode=" + mode + "]";
    }
}
