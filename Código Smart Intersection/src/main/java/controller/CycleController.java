package controller;

import model.PedestrianRequest;
import model.TrafficLight;
import policy.TimingPolicy;

public class CycleController {

    public interface LedCallback {
        void onColorChange(String color);
    }

    private String        currentState;
    private TimingPolicy  timingPolicy;
    private TrafficLight  trafficLight;
    private LedCallback   ledCallback; // puede ser null si no se necesita

    public CycleController(TrafficLight trafficLight, TimingPolicy timingPolicy) {
        this.trafficLight = trafficLight;
        this.timingPolicy = timingPolicy;
        this.currentState = "RED";
        this.ledCallback  = null;
    }

    public void setLedCallback(LedCallback cb) {
        this.ledCallback = cb;
    }

    public void changeLights(String color) {
        this.currentState = color;
        trafficLight.changeLight(color);
        if (ledCallback != null) {
            ledCallback.onColorChange(color);
        }
    }

    private void changeLightsAndWait(String color, int seconds) {
        changeLights(color);
        System.out.printf("    [%s durante %ds]%n", color, seconds);
        sleep(seconds * 1000);
    }

    public void runNormalCycle() {
        System.out.println("[CICLO] Ejecutando ciclo normal...");
        changeLightsAndWait("GREEN",  timingPolicy.getGreenTime());
        changeLightsAndWait("YELLOW", 3);
        changeLightsAndWait("RED",    timingPolicy.getRedTime());
        System.out.println("[CICLO] Ciclo normal completado.");
    }

    public void startSafeCycle() {
        System.out.println("[CICLO] Iniciando ciclo seguro para peatones...");
        changeLightsAndWait("YELLOW", 3);
        changeLightsAndWait("RED",    timingPolicy.getRedTime());
        System.out.println("[CICLO] Cruce seguro completado. Reanudando trafico...");
        changeLights("GREEN");
    }

    public void handlePedestrianRequest(PedestrianRequest request) {
        if (request == null || !request.validateRequest()) {
            System.out.println("[CICLO] Solicitud peatonal invalida o ya procesada.");
            return;
        }
        System.out.println("[CICLO] Solicitud peatonal recibida: " + request.getRequestId());
        startSafeCycle();
        request.markAsProcessed();
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public String        getCurrentState()  { return currentState; }
    public TimingPolicy  getTimingPolicy()  { return timingPolicy; }
    public TrafficLight  getTrafficLight()  { return trafficLight; }

    public void setTimingPolicy(TimingPolicy policy) { this.timingPolicy = policy; }

    @Override
    public String toString() {
        return "CycleController [state=" + currentState + ", policy=" + timingPolicy + "]";
    }
}
