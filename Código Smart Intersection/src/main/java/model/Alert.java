package model;

public class Alert {

    private String alertId;
    private String severity; 
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
