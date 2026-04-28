package sensor;

public abstract class Sensor {

    protected String sensorId;
    protected String status;

    public Sensor(String sensorId) {
        this.sensorId = sensorId;
        this.status = "ACTIVE";
    }

    public abstract double readData();

    public String getSensorId() { return sensorId; }
    public String getStatus()   { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Sensor [id=" + sensorId + ", status=" + status + "]";
    }
}
