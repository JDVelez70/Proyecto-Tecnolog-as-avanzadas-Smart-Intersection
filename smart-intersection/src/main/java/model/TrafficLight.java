package model;

/**
 * TrafficLight — OOP: Encapsulamiento del estado del semáforo.
 * SOLID: SRP — solo maneja el estado y color del semáforo.
 */
public class TrafficLight {

    private String lightId;
    private String currentColor; // "RED", "GREEN", "YELLOW"
    private int timer;

    public TrafficLight(String lightId) {
        this.lightId = lightId;
        this.currentColor = "RED";
        this.timer = 0;
    }

    public void changeLight(String color) {
        this.currentColor = color;
        System.out.println("[SEMÁFORO " + lightId + "] → " + color);
    }

    public boolean isGreen()  { return "GREEN".equals(currentColor); }
    public boolean isRed()    { return "RED".equals(currentColor); }
    public boolean isYellow() { return "YELLOW".equals(currentColor); }

    public String getLightId()      { return lightId; }
    public String getCurrentColor() { return currentColor; }
    public int getTimer()           { return timer; }
    public void setTimer(int timer) { this.timer = timer; }

    @Override
    public String toString() {
        return "TrafficLight [id=" + lightId + ", color=" + currentColor + "]";
    }
}
