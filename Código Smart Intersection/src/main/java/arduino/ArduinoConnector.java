package arduino;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ArduinoConnector {

    private SerialPort     serialPort;
    private BufferedReader reader;
    private boolean        connected;
    private final String   portName;
    private static final int BAUD_RATE = 9600;

    private final boolean simulationMode;
    private double simulatedDistance = 999.0;

    public ArduinoConnector(String portName, boolean simulationMode) {
        this.portName       = portName;
        this.simulationMode = simulationMode;
        this.connected      = false;
    }

    public boolean connect() {
        if (simulationMode) {
            connected = true;
            System.out.println("[ARDUINO] Modo SIMULACION activo (sin hardware).");
            return true;
        }

        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(BAUD_RATE);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        connected = serialPort.openPort();

        if (connected) {
            reader = new BufferedReader(
                new InputStreamReader(serialPort.getInputStream())
            );
            System.out.println("[ARDUINO] Conectado en " + portName + " a " + BAUD_RATE + " baud.");
            sleep(2500);    
            flushBuffer(); 
        } else {
            System.out.println("[ARDUINO] No se pudo conectar en " + portName
                + ". Verifica el puerto en el Administrador de dispositivos.");
        }
        return connected;
    }

    public void disconnect() {
        try { if (reader != null) reader.close(); } catch (Exception ignored) {}
        if (!simulationMode && serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
        connected = false;
        System.out.println("[ARDUINO] Desconectado.");
    }

    public double readDistance() {
        if (!connected) return -1;
        if (simulationMode) return simulatedDistance;

        try {
            String ultimaLinea = null;

            while (reader.ready()) {
                String linea = reader.readLine();
                if (linea != null && linea.trim().startsWith("DIST:")) {
                    ultimaLinea = linea.trim();
                }
            }

            if (ultimaLinea != null) {
                return Double.parseDouble(ultimaLinea.replace("DIST:", "").trim());
            }

            long inicio = System.currentTimeMillis();
            while (System.currentTimeMillis() - inicio < 600) {
                if (reader.ready()) {
                    String linea = reader.readLine();
                    if (linea != null && linea.trim().startsWith("DIST:")) {
                        return Double.parseDouble(linea.trim().replace("DIST:", "").trim());
                    }
                }
                sleep(20);
            }

        } catch (Exception e) {
            System.out.println("[ARDUINO] Error leyendo serial: " + e.getMessage());
        }
        return -1;
    }


    public void sendLedCommand(String color) {
        if (!connected) return;
        if (simulationMode) {
            System.out.println("[ARDUINO] [SIM] LED -> " + color);
            return;
        }
        try {
            byte[] bytes = ("LED:" + color + "\n").getBytes();
            serialPort.writeBytes(bytes, bytes.length);
            System.out.println("[ARDUINO] LED fisico -> " + color);
        } catch (Exception e) {
            System.out.println("[ARDUINO] Error enviando comando: " + e.getMessage());
        }
    }

    private void flushBuffer() {
        try {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 1000) {
                if (reader.ready()) reader.readLine();
                else sleep(30);
            }
        } catch (Exception ignored) {}
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public boolean isConnected()      { return connected; }
    public boolean isSimulationMode() { return simulationMode; }
    public String  getPortName()      { return portName; }
    public void setSimulatedDistance(double d) { this.simulatedDistance = d; }

    @Override
    public String toString() {
        return "ArduinoConnector [port=" + portName
            + ", mode=" + (simulationMode ? "SIMULATION" : "HARDWARE")
            + ", connected=" + connected + "]";
    }
}
