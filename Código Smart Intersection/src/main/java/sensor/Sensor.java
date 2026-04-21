package sensor;

/**
 * Sensor — GRASP: Polymorphism + SOLID: Liskov Substitution
 * Clase abstracta base para todos los sensores del sistema.
 * Define el contrato común sin imponer implementación.
 */
public abstract class Sensor {

    protected String sensorId;
    protected String status;

    public Sensor(String sensorId) {
        this.sensorId = sensorId;
        this.status = "ACTIVE";
    }

    /**
     * Método abstracto: cada sensor implementa su propia lectura.
     * Retorna el valor leído (distancia, conteo, presión, etc.)
     */
    public abstract double readData();

    public String getSensorId() { return sensorId; }
    public String getStatus()   { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Sensor [id=" + sensorId + ", status=" + status + "]";
    }
}
