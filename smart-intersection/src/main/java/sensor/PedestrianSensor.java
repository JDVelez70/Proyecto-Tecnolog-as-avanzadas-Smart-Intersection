package sensor;

import model.PedestrianRequest;
import java.util.Date;

/**
 * PedestrianSensor — GRASP: Creator
 * Crea PedestrianRequest cuando el botón es presionado.
 * Encapsula la lógica del sensor de cruce peatonal.
 */
public class PedestrianSensor extends Sensor {

    private String crosswalkId;
    private boolean isPressed;

    public PedestrianSensor(String sensorId, String crosswalkId) {
        super(sensorId);
        this.crosswalkId = crosswalkId;
        this.isPressed = false;
    }

    @Override
    public double readData() {
        return isPressed ? 1.0 : 0.0;
    }

    /**
     * GRASP: Creator — PedestrianSensor crea PedestrianRequest
     * porque tiene los datos necesarios para construirlo.
     */
    public PedestrianRequest createRequest() {
        isPressed = false; // Resetear después de crear la solicitud
        return new PedestrianRequest(
            "REQ-" + System.currentTimeMillis(),
            "PENDING",
            new Date()
        );
    }

    public boolean detectPedestrian() {
        return isPressed;
    }

    public void press()   { this.isPressed = true; }
    public void release() { this.isPressed = false; }

    public String getCrosswalkId() { return crosswalkId; }
    public boolean isPressed()     { return isPressed; }

    @Override
    public String toString() {
        return "PedestrianSensor [crosswalk=" + crosswalkId + ", pressed=" + isPressed + "]";
    }
}
