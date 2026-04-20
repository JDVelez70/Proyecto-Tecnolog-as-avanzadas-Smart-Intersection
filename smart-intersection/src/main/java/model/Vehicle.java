package model;

/**
 * Vehicle — OOP: Abstracción de un vehículo en la intersección.
 * SOLID: Single Responsibility — solo conoce datos del vehículo.
 */
public class Vehicle {

    protected String plateNumber;
    protected String type;
    protected double speed;

    public Vehicle(String plateNumber, String type, double speed) {
        this.plateNumber = plateNumber;
        this.type = type;
        this.speed = speed;
    }

    public void updateSpeed(double newSpeed) {
        this.speed = newSpeed;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getType()        { return type; }
    public double getSpeed()       { return speed; }

    @Override
    public String toString() {
        return "Vehicle [plate=" + plateNumber + ", type=" + type + ", speed=" + speed + "km/h]";
    }
}
