package model;

/**
 * Alert — OOP: Representa alertas del sistema.
 * SOLID: SRP — solo maneja datos y envío de la alerta.
 */
public class Alert {

    private String alertId;
    private String severity;  // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private String message;

    public Alert(String alertId, String severity, String message) {
        this.alertId = alertId;
        this.severity = severity;
        this.message = message;
    }

    public void sendAlert() {
        String prefix = "CRITICAL".equals(severity) ? "🚨" :
                        "HIGH".equals(severity)     ? "⚠️ " : "ℹ️ ";
        System.out.println(prefix + " [ALERTA " + severity + "] " + message);
    }

    public String getAlertId()  { return alertId; }
    public String getSeverity() { return severity; }
    public String getMessage()  { return message; }

    @Override
    public String toString() {
        return "Alert [id=" + alertId + ", severity=" + severity
                + ", msg=" + message + "]";
    }
}
