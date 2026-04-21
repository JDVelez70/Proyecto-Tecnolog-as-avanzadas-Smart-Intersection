package model;

/**
 * SmartIntersection — GRASP: Controller (coordinador del dominio)
 * Representa la intersección inteligente y delega el flujo de tráfico.
 * SOLID: SRP — solo coordina el estado de la intersección.
 */
public class SmartIntersection {

    private String intersectionId;
    private String location;
    private String status; // "ACTIVE", "MAINTENANCE", "EMERGENCY"

    public SmartIntersection(String intersectionId, String location) {
        this.intersectionId = intersectionId;
        this.location = location;
        this.status = "ACTIVE";
    }

    public void manageTrafficFlow() {
        System.out.println("[INTERSECCIÓN " + intersectionId + "] Gestionando flujo en " + location);
    }

    public String getIntersectionId() { return intersectionId; }
    public String getLocation()       { return location; }
    public String getStatus()         { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "SmartIntersection [id=" + intersectionId + ", loc=" + location
                + ", status=" + status + "]";
    }
}
