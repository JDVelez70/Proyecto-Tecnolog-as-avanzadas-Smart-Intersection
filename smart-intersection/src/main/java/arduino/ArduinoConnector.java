package arduino;

/**
 * ArduinoConnector — GRASP: Pure Fabrication + SOLID: SRP
 * Abstrae la comunicación Serial con el Arduino.
 * En simulación (Tinkercad) no hay puerto real, por lo que se simula la entrada.
 *
 * Con hardware real: se usaría jSerialComm o RXTX para leer el puerto COM/ttyUSB.
 * Formato del mensaje Arduino: "DIST:15.3\n"  → distancia en cm del HC-SR04
 */
public class ArduinoConnector {

    private boolean connected;
    private String portName;
    private double simulatedDistance; // usado en modo simulación

    public ArduinoConnector(String portName) {
        this.portName = portName;
        this.connected = false;
        this.simulatedDistance = 999.0;
    }

    /**
     * Intenta conectar al puerto serial del Arduino.
     * En Tinkercad/simulación siempre retorna true (modo simulado).
     */
    public boolean connect() {
        // Con jSerialComm real:
        // SerialPort port = SerialPort.getCommPort(portName);
        // this.connected = port.openPort();
        this.connected = true; // Simulación
        System.out.println("[ARDUINO] Conectado en modo " +
                (isSimulated() ? "SIMULACIÓN (Tinkercad)" : "HARDWARE REAL"));
        return connected;
    }

    public void disconnect() {
        this.connected = false;
        System.out.println("[ARDUINO] Desconectado.");
    }

    /**
     * Lee distancia desde el Arduino.
     * Modo simulación: retorna el valor seteado manualmente.
     * Modo real: leería del puerto serial "DIST:xx.x\n"
     */
    public double readDistance() {
        if (!connected) {
            System.out.println("[ARDUINO] ⚠️  No conectado.");
            return -1;
        }
        // En modo real: leer línea del serial y parsear "DIST:15.3"
        // String line = serialPort.readLine();
        // return Double.parseDouble(line.replace("DIST:", "").trim());
        return simulatedDistance;
    }

    /**
     * Envía comando al Arduino para controlar LED.
     * Protocolo: "LED:GREEN\n", "LED:RED\n", "LED:YELLOW\n"
     */
    public void sendLedCommand(String color) {
        if (!connected) return;
        // En real: serialPort.write("LED:" + color + "\n");
        System.out.println("[ARDUINO] → Enviando: LED:" + color);
        System.out.println("[ARDUINO] 💡 LED físico cambiado a " + color);
    }

    public boolean isSimulated() { return true; } // cambiar a false con hardware real
    public boolean isConnected() { return connected; }
    public String getPortName()  { return portName; }

    public void setSimulatedDistance(double distance) {
        this.simulatedDistance = distance;
    }

    @Override
    public String toString() {
        return "ArduinoConnector [port=" + portName + ", connected=" + connected + "]";
    }
}
