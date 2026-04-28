package sensor;

public class VehicleSensor extends Sensor {

    private int lane;
    private int vehicleCount;
    private double lastDistance; 

    private static final double DETECTION_THRESHOLD_CM = 20.0;

    public VehicleSensor(String sensorId, int lane) {
        super(sensorId);
        this.lane = lane;
        this.vehicleCount = 0;
        this.lastDistance = 999.0;
    }

    @Override
    public double readData() {
        return lastDistance;
    }

    public boolean detectVehicle() {
        return lastDistance < DETECTION_THRESHOLD_CM;
    }

    public void updateDistance(double distance) {
        this.lastDistance = distance;
        if (detectVehicle()) {
            vehicleCount++;
        }
    }

    public int getLane()         { return lane; }
    public int getVehicleCount() { return vehicleCount; }
    public void resetCount()     { vehicleCount = 0; }

    @Override
    public String toString() {
        return "VehicleSensor [lane=" + lane + ", count=" + vehicleCount
                + ", lastDistance=" + lastDistance + "cm, detected=" + detectVehicle() + "]";
    }
}
