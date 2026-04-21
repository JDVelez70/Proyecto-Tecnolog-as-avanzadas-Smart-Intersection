package model;

import java.util.Date;

/**
 * PedestrianRequest — OOP: Representa solicitud de cruce peatonal.
 * SOLID: SRP — solo contiene datos y lógica de validación de la solicitud.
 */
public class PedestrianRequest {

    private String requestId;
    private String status; // "PENDING", "PROCESSED", "REJECTED"
    private Date timestamp;

    public PedestrianRequest(String requestId, String status, Date timestamp) {
        this.requestId = requestId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public boolean validateRequest() {
        return requestId != null && !requestId.isEmpty() && "PENDING".equals(status);
    }

    public void markAsProcessed() {
        this.status = "PROCESSED";
        System.out.println("[PEATÓN] Solicitud " + requestId + " procesada.");
    }

    public String getRequestId()  { return requestId; }
    public String getStatus()     { return status; }
    public Date getTimestamp()    { return timestamp; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "PedestrianRequest [id=" + requestId + ", status=" + status
                + ", time=" + timestamp + "]";
    }
}
