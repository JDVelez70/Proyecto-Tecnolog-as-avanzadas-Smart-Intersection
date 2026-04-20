package model;

/**
 * User — OOP: Representa al operador del sistema.
 * SOLID: SRP — solo contiene datos del usuario y acción de monitoreo.
 */
public class User {

    private String userId;
    private String name;
    private String role; // "OPERATOR", "ADMIN", "VIEWER"

    public User(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public void monitorIntersection() {
        System.out.println("[MONITOR] " + name + " (" + role + ") está supervisando la intersección.");
    }

    public String getUserId() { return userId; }
    public String getName()   { return name; }
    public String getRole()   { return role; }

    @Override
    public String toString() {
        return "User [id=" + userId + ", name=" + name + ", role=" + role + "]";
    }
}
