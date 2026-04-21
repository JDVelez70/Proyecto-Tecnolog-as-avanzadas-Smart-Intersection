package arduino;

import com.fazecast.jSerialComm.SerialPort;

/**
 * ArduinoConnector — Comunicación Serial real con Arduino via jSerialComm.
 * Compatible con Windows (COM3, COM4, etc.)
 *
 * Protocolo acordado con el sketch:
 *   Arduino → Java : "DIST:15.30\n"
 *   Java → Arduino : "LED:GREEN\n"
 */
public class ArduinoConnector {

    private SerialPort serialPort;
    private boolean connected;
    private final String portName;
    private static final int BAUD_RATE = 9600;

    // ── Simulación (cuando no hay Arduino físico) ─────────────────────────────
    private final boolean simulationMode;
    private double simulatedDistance = 999.0;

    /**
     * @param portName       Nombre del puerto: "COM3", "COM4", etc. en Windows.
     * @param simulationMode true = sin Arduino físico, false = hardware real.
     */
    public ArduinoConnector(String portName, boolean simulationMode) {
        this.portName       = portName;
        this.simulationMode = simulationMode;
        this.connected      = false;
    }

    // ── Conectar ──────────────────────────────────────────────────────────────

    public boolean connect() {
        if (simulationMode) {
            connected = true;
            System.out.println("[ARDUINO] Modo SIMULACIÓN activo (sin hardware).");
            return true;
        }

        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(BAUD_RATE);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(
            SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0
        );

        connected = serialPort.openPort();

        if (connected) {
            System.out.println("[ARDUINO] ✅ Conectado en " + portName + " a " + BAUD_RATE + " baud.");
            sleep(2000); // Esperar reinicio del Arduino
        } else {
            System.out.println("[ARDUINO] ❌ No se pudo conectar en " + portName
                    + ". Verifica el puerto en el Administrador de dispositivos.");
        }
        return connected;
    }

    public void disconnect() {
        if (!simulationMode && serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
        connected = false;
        System.out.println("[ARDUINO] Desconectado.");
    }

    // ── Leer distancia ────────────────────────────────────────────────────────

    /**
     * Lee la distancia enviada por el Arduino.
     * Espera una línea con formato: "DIST:15.30"
     * Retorna -1 si hay error o timeout.
     */
    public double readDistance() {
        if (!connected) {
            System.out.println("[ARDUINO] ⚠️  No conectado.");
            return -1;
        }

        if (simulationMode) {
            return simulatedDistance;
        }

        try {
            String line = readLine();
            if (line != null && line.startsWith("DIST:")) {
                return Double.parseDouble(line.replace("DIST:", "").trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("[ARDUINO] ⚠️  Formato inesperado del serial.");
        }
        return -1;
    }

    // ── Enviar comando LED ────────────────────────────────────────────────────

    /**
     * Envía comando al Arduino para controlar los LEDs.
     * Formato: "LED:GREEN\n", "LED:RED\n", "LED:YELLOW\n"
     */
    public void sendLedCommand(String color) {
        if (!connected) return;

        String command = "LED:" + color + "\n";
        System.out.println("[ARDUINO] → Enviando: " + command.trim());

        if (simulationMode) {
            System.out.println("[ARDUINO] 💡 [SIM] LED cambiado a " + color);
            return;
        }

        byte[] bytes = command.getBytes();
        serialPort.writeBytes(bytes, bytes.length);
        System.out.println("[ARDUINO] 💡 LED físico → " + color);
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private String readLine() {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1];
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 2000) {
            if (serialPort.bytesAvailable() > 0) {
                serialPort.readBytes(buffer, 1);
                char c = (char) buffer[0];
                if (c == '\n') break;
                if (c != '\r') sb.append(c);
            }
        }

        String result = sb.toString().trim();
        return result.isEmpty() ? null : result;
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException ignored) {}
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public boolean isConnected()      { return connected; }
    public boolean isSimulationMode() { return simulationMode; }
    public String getPortName()       { return portName; }

    public void setSimulatedDistance(double distance) {
        this.simulatedDistance = distance;
    }

    @Override
    public String toString() {
        return "ArduinoConnector [port=" + portName
                + ", mode=" + (simulationMode ? "SIMULATION" : "HARDWARE")
                + ", connected=" + connected + "]";
    }
}
