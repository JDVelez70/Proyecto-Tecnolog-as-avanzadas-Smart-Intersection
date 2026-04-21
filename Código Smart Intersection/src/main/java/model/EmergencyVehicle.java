package model;

/**
 * EmergencyVehicle — OOP: Herencia de Vehicle.
 * SOLID: Liskov Substitution — puede reemplazar a Vehicle en cualquier contexto.
 * GRASP: Polymorphism — comportamiento distinto al de un vehículo normal.
 */
public class EmergencyVehicle extends Vehicle {

    private int priorityLevel; // 1 = máxima prioridad
    private boolean sirenOn;

    public EmergencyVehicle(String plateNumber, String type, double speed,
                            int priorityLevel) {
        super(plateNumber, type, speed);
        this.priorityLevel = priorityLevel;
        this.sirenOn = false;
    }

    /**
     * Activa la sirena y solicita prioridad en la intersección.
     */
    public void requestPriority() {
        this.sirenOn = true;
        System.out.println("[EMERGENCY] Vehículo " + plateNumber
                + " solicita prioridad nivel " + priorityLevel);
    }

    public int getPriorityLevel() { return priorityLevel; }
    public boolean isSirenOn()    { return sirenOn; }
    public void setSirenOn(boolean sirenOn) { this.sirenOn = sirenOn; }

    @Override
    public String toString() {
        return "EmergencyVehicle [plate=" + plateNumber + ", priority=" + priorityLevel
                + ", siren=" + sirenOn + "]";
    }
}
