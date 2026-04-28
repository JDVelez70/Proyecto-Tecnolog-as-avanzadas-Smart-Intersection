package sensor;

import model.PedestrianRequest;
import java.util.Date;

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

    public PedestrianRequest createRequest() {
        isPressed = false; 
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
