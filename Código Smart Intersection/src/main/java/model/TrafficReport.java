package model;

import java.util.Date;

/**
 * TrafficReport — OOP: Encapsula datos del reporte de tráfico.
 * SOLID: SRP — solo genera y almacena datos del reporte.
 */
public class TrafficReport {

    private String reportId;
    private Date date;
    private double trafficDensity; // vehículos por minuto

    public TrafficReport(String reportId, Date date, double trafficDensity) {
        this.reportId = reportId;
        this.date = date;
        this.trafficDensity = trafficDensity;
    }

    public void generateReport() {
        System.out.println("=== REPORTE DE TRÁFICO ===");
        System.out.println("ID       : " + reportId);
        System.out.println("Fecha    : " + date);
        System.out.println("Densidad : " + trafficDensity + " veh/min");
        System.out.println("Nivel    : " + getDensityLevel());
        System.out.println("==========================");
    }

    private String getDensityLevel() {
        if (trafficDensity < 5)  return "BAJO";
        if (trafficDensity < 15) return "MEDIO";
        return "ALTO";
    }

    public String getReportId()       { return reportId; }
    public Date getDate()             { return date; }
    public double getTrafficDensity() { return trafficDensity; }

    @Override
    public String toString() {
        return "TrafficReport [id=" + reportId + ", density=" + trafficDensity + "]";
    }
}
