/*
 * Smart Street Intersection — Arduino Sketch
 * UC1: Detección de Vehículo con HC-SR04 + Control de LEDs
 *
 * Componentes (Tinkercad):
 *   - Arduino Uno
 *   - Sensor HC-SR04 (ultrasónico)
 *   - LED Verde  → pin 9
 *   - LED Amarillo → pin 10
 *   - LED Rojo   → pin 11
 *   - Resistencias 220Ω en cada LED
 *
 * Protocolo Serial con Java:
 *   Arduino → Java: "DIST:15.30\n"   (distancia en cm)
 *   Java → Arduino: "LED:GREEN\n"    (comando de color)
 *
 * Umbral de detección: < 20 cm = vehículo presente
 */

// ── Pines ────────────────────────────────────────────────────────────────────
const int TRIG_PIN    = 7;
const int ECHO_PIN    = 6;
const int LED_GREEN   = 9;
const int LED_YELLOW  = 10;
const int LED_RED     = 11;

// ── Constantes ───────────────────────────────────────────────────────────────
const float DETECTION_THRESHOLD = 20.0; // cm
const int   BAUD_RATE           = 9600;
const int   LOOP_DELAY_MS       = 500;

// ── Estado actual ─────────────────────────────────────────────────────────────
String currentLedColor = "RED";
String incomingCommand = "";

void setup() {
    Serial.begin(BAUD_RATE);

    pinMode(TRIG_PIN,   OUTPUT);
    pinMode(ECHO_PIN,   INPUT);
    pinMode(LED_GREEN,  OUTPUT);
    pinMode(LED_YELLOW, OUTPUT);
    pinMode(LED_RED,    OUTPUT);

    // Estado inicial: rojo
    setLed("RED");
    Serial.println("READY:SmartIntersection");
}

void loop() {
    // ── Leer distancia del HC-SR04 ──────────────────────────────────────────
    float distance = measureDistance();

    // ── Enviar lectura a Java por Serial ────────────────────────────────────
    Serial.print("DIST:");
    Serial.println(distance, 2);

    // ── Recibir comando de Java (no bloqueante) ──────────────────────────────
    if (Serial.available() > 0) {
        incomingCommand = Serial.readStringUntil('\n');
        incomingCommand.trim();
        handleCommand(incomingCommand);
    }

    // ── Reacción automática local (si no hay comando externo) ────────────────
    if (distance < DETECTION_THRESHOLD) {
        // Vehículo detectado → LED verde local
        setLed("GREEN");
        Serial.println("EVENT:VEHICLE_DETECTED");
    } else {
        setLed("RED");
    }

    delay(LOOP_DELAY_MS);
}

// ─── Funciones ───────────────────────────────────────────────────────────────

/**
 * Mide distancia con el HC-SR04.
 * Retorna distancia en centímetros.
 */
float measureDistance() {
    // Pulso de disparo
    digitalWrite(TRIG_PIN, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIG_PIN, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIG_PIN, LOW);

    // Medir tiempo del eco
    long duration = pulseIn(ECHO_PIN, HIGH, 30000); // timeout 30ms
    if (duration == 0) return 999.0; // sin eco = sin objeto

    // Convertir a cm (velocidad del sonido ~343 m/s)
    float distance = (duration * 0.0343) / 2.0;
    return distance;
}

/**
 * Procesa comandos recibidos desde la app Java.
 * Formato esperado: "LED:GREEN", "LED:RED", "LED:YELLOW"
 */
void handleCommand(String cmd) {
    if (cmd.startsWith("LED:")) {
        String color = cmd.substring(4);
        setLed(color);
        Serial.print("ACK:LED:");
        Serial.println(color);
    }
}

/**
 * Controla los LEDs del semáforo.
 * Solo enciende el LED del color indicado, apaga los demás.
 */
void setLed(String color) {
    digitalWrite(LED_GREEN,  LOW);
    digitalWrite(LED_YELLOW, LOW);
    digitalWrite(LED_RED,    LOW);

    if (color == "GREEN"  || color.equals("GREEN"))  digitalWrite(LED_GREEN,  HIGH);
    if (color == "YELLOW" || color.equals("YELLOW")) digitalWrite(LED_YELLOW, HIGH);
    if (color == "RED"    || color.equals("RED"))    digitalWrite(LED_RED,    HIGH);

    currentLedColor = color;
}
