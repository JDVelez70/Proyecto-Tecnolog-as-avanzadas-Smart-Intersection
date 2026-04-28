package policy;

public class AdaptiveTimingPolicy implements TimingPolicy {

    private int vehicleCount;

    public AdaptiveTimingPolicy(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    @Override
    public int getGreenTime() {
        return Math.min(15 + (vehicleCount * 5), 60);
    }

    @Override
    public int getRedTime() {
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
