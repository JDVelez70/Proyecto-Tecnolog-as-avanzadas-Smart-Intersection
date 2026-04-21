package policy;

/**
 * AdaptiveTimingPolicy — SOLID: Open/Closed Principle
 * Política adaptativa: tiempos se ajustan según densidad de tráfico.
 * Se puede activar cuando hay más de un umbral de vehículos detectados.
 */
public class AdaptiveTimingPolicy implements TimingPolicy {

    private int vehicleCount;

    public AdaptiveTimingPolicy(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    @Override
    public int getGreenTime() {
        // Más vehículos → más tiempo en verde (hasta 60s)
        return Math.min(15 + (vehicleCount * 5), 60);
    }

    @Override
    public int getRedTime() {
        // Más vehículos → menos tiempo en rojo (mínimo 10s)
        return Math.max(30 - (vehicleCount * 2), 10);
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    @Override
    public String toString() {
        return "AdaptiveTimingPolicy [vehicles=" + vehicleCount
                + ", green=" + getGreenTime() + "s, red=" + getRedTime() + "s]";
    }
}
