package controller;

import model.PedestrianRequest;
import model.TrafficLight;
import policy.TimingPolicy;

/**
 * CycleController — GRASP: Controller
 * Maneja el ciclo del semáforo: verde, amarillo, rojo, ciclo seguro peatonal.
 * SOLID: OCP — recibe TimingPolicy por inyección, no conoce implementaciones.
 * SOLID: DIP — depende de la abstracción TimingPolicy, no de clases concretas.
 */
public class CycleController {

    private String currentState; // "GREEN", "YELLOW", "RED", "SAFE_CYCLE"
    private TimingPolicy timingPolicy;
    private TrafficLight trafficLight;

    public CycleController(TrafficLight trafficLight, TimingPolicy timingPolicy) {
        this.trafficLight = trafficLight;
        this.timingPolicy = timingPolicy;
        this.currentState = "RED";
    }

    /**
     * GRASP: Controller — maneja la solicitud peatonal y delega el ciclo seguro.
     */
    public void handlePedestrianRequest(PedestrianRequest request) {
        if (request == null || !request.validateRequest()) {
            System.out.println("[CICLO] Solicitud peatonal inválida o ya procesada.");
            return;
        }
        System.out.println("[CICLO] Solicitud peatonal recibida: " + request.getRequestId());
        startSafeCycle();
        request.markAsProcessed();
    }

    /**
     * Inicia el ciclo seguro de cruce peatonal.
     * Semáforo en AMARILLO → RED con tiempo suficiente para cruzar.
     */
    public void startSafeCycle() {
        System.out.println("[CICLO] Iniciando ciclo seguro para peatones...");
        changeLights("YELLOW");
        simulateDelay(3);
        changeLights("RED");
        System.out.println("[CICLO] ✅ Cruce seguro activo por " + timingPolicy.getRedTime() + "s");
        simulateDelay(timingPolicy.getRedTime());
        changeLights("GREEN");
        currentState = "GREEN";
    }

    /**
     * Cambia el semáforo al color especificado y actualiza el estado.
     */
    public void changeLights(String color) {
        this.currentState = color;
        trafficLight.changeLight(color);
    }

    /**
     * Ejecuta un ciclo normal: verde → amarillo → rojo.
     */
    public void runNormalCycle() {
        System.out.println("[CICLO] Ejecutando ciclo normal...");
        changeLights("GREEN");
        simulateDelay(timingPolicy.getGreenTime());
        changeLights("YELLOW");
        simulateDelay(3);
        changeLights("RED");
        simulateDelay(timingPolicy.getRedTime());
    }

    /** Simula espera (en consola muestra el tiempo, no bloquea realmente) */
    private void simulateDelay(int seconds) {
        System.out.println("    [⏱ simulando " + seconds + "s]");
    }

    public String getCurrentState()   { return currentState; }
    public TimingPolicy getTimingPolicy() { return timingPolicy; }
    public void setTimingPolicy(TimingPolicy policy) { this.timingPolicy = policy; }
    public TrafficLight getTrafficLight() { return trafficLight; }

    @Override
    public String toString() {
        return "CycleController [state=" + currentState + ", policy=" + timingPolicy + "]";
    }
}
