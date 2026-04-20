package persistence;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * IntersectionRepository — SOLID: SRP + DIP
 * Toda la lógica de persistencia está aquí, aislada del resto del sistema.
 * Guarda datos en archivos JSON simples.
 * GRASP: Pure Fabrication — clase de servicio sin correlato en el dominio real.
 */
public class IntersectionRepository {

    private final String dataDir;
    private final String readingsFile;
    private final String actionsFile;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public IntersectionRepository(String dataDir) {
        this.dataDir      = dataDir;
        this.readingsFile = dataDir + "/sensor_readings.json";
        this.actionsFile  = dataDir + "/actions_log.json";
        initFiles();
    }

    /** Crea los archivos JSON si no existen */
    private void initFiles() {
        new File(dataDir).mkdirs();
        initJsonFile(readingsFile, "sensor_readings");
        initJsonFile(actionsFile,  "actions");
    }

    private void initJsonFile(String path, String key) {
        File f = new File(path);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("{ \"" + key + "\": [] }");
            } catch (IOException e) {
                System.err.println("[REPO] Error inicializando " + path + ": " + e.getMessage());
            }
        }
    }

    /**
     * Guarda una lectura de sensor en sensor_readings.json
     */
    public void saveSensorReading(String sensorId, double distance,
                                  boolean detected, Date timestamp) {
        String entry = String.format(
            "  { \"sensorId\": \"%s\", \"distance\": %.1f, \"detected\": %b, \"timestamp\": \"%s\" }",
            sensorId, distance, detected, sdf.format(timestamp)
        );
        appendToJsonArray(readingsFile, "sensor_readings", entry);
        System.out.println("[REPO] ✅ Lectura guardada: sensor=" + sensorId
                + " dist=" + distance + "cm detected=" + detected);
    }

    /**
     * Guarda una acción del controlador en actions_log.json
     */
    public void saveAction(String controllerId, String action, Date timestamp) {
        String entry = String.format(
            "  { \"controllerId\": \"%s\", \"action\": \"%s\", \"timestamp\": \"%s\" }",
            controllerId, action, sdf.format(timestamp)
        );
        appendToJsonArray(actionsFile, "actions", entry);
        System.out.println("[REPO] ✅ Acción guardada: " + action + " por " + controllerId);
    }

    /**
     * Lee y muestra todos los registros del archivo de lecturas.
     */
    public void printAllReadings() {
        System.out.println("\n=== LECTURAS DE SENSORES ===");
        printFile(readingsFile);
    }

    /**
     * Lee y muestra todos los registros de acciones.
     */
    public void printAllActions() {
        System.out.println("\n=== LOG DE ACCIONES ===");
        printFile(actionsFile);
    }

    /** Agrega una nueva entrada al array JSON del archivo */
    private void appendToJsonArray(String filePath, String key, String newEntry) {
        try {
            // Leer contenido actual
            StringBuilder content = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) content.append(line).append("\n");
            }

            String current = content.toString().trim();

            // Si el array está vacío
            if (current.contains("\"" + key + "\": []")) {
                current = current.replace(
                    "\"" + key + "\": []",
                    "\"" + key + "\": [\n" + newEntry + "\n]"
                );
            } else {
                // Insertar antes del cierre del array ]
                int lastBracket = current.lastIndexOf("]");
                current = current.substring(0, lastBracket)
                        + ",\n" + newEntry + "\n]"
                        + current.substring(lastBracket + 1);
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
                pw.print(current);
            }
        } catch (IOException e) {
            System.err.println("[REPO] Error guardando en " + filePath + ": " + e.getMessage());
        }
    }

    private void printFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.err.println("[REPO] Error leyendo " + path + ": " + e.getMessage());
        }
    }

    public String getDataDir() { return dataDir; }
}
