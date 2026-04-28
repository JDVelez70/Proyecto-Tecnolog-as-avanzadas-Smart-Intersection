/*
 * Smart Street Intersection — Arduino Mega 2560-R3
 * Sensor HC-SR04 + 3 LEDs (Rojo, Amarillo, Verde)
 *
 * CONEXIONES:
 *   HC-SR04  VCC  → 5V
 *   HC-SR04  GND  → GND
 *   HC-SR04  TRIG → Pin 7
 *   HC-SR04  ECHO → Pin 6
 *   LED Verde (+) → Pin 9  → resistencia 220Ω → GND
 *   LED Amarillo(+)→ Pin 10 → resistencia 220Ω → GND
 *   LED Rojo  (+) → Pin 11 → resistencia 220Ω → GND
 *
 * PROTOCOLO SERIAL (9600 baud):
 *   Arduino → Java : "DIST:15.30\n"
 *   Java → Arduino : "LED:GREEN\n" | "LED:YELLOW\n" | "LED:RED\n"
 */

const int TRIG_PIN   = 7;
const int ECHO_PIN   = 6;
const int LED_GREEN  = 9;
const int LED_YELLOW = 10;
const int LED_RED    = 11;

const float DETECTION_CM = 20.0;
String incomingCmd = "";

void setup() {
  Serial.begin(9600);
  pinMode(TRIG_PIN,   OUTPUT);
  pinMode(ECHO_PIN,   INPUT);
  pinMode(LED_GREEN,  OUTPUT);
  pinMode(LED_YELLOW, OUTPUT);
  pinMode(LED_RED,    OUTPUT);
  setLed("RED");
  Serial.println("READY:SmartIntersection");
}

void loop() {
  float dist = measureDistance();

  // Enviar lectura a Java
  Serial.print("DIST:");
  Serial.println(dist, 2);

  // Leer comando de Java (no bloqueante)
  if (Serial.available() > 0) {
    incomingCmd = Serial.readStringUntil('\n');
    incomingCmd.trim();
    if (incomingCmd.startsWith("LED:")) {
      String color = incomingCmd.substring(4);
      setLed(color);
      Serial.print("ACK:LED:");
      Serial.println(color);
    }
  }

  delay(400);
}

float measureDistance() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long dur = pulseIn(ECHO_PIN, HIGH, 30000);
  if (dur == 0) return 999.0;
  return (dur * 0.0343) / 2.0;
}

void setLed(String color) {
  digitalWrite(LED_GREEN,  LOW);
  digitalWrite(LED_YELLOW, LOW);
  digitalWrite(LED_RED,    LOW);
  if      (color == "GREEN")  digitalWrite(LED_GREEN,  HIGH);
  else if (color == "YELLOW") digitalWrite(LED_YELLOW, HIGH);
  else if (color == "RED")    digitalWrite(LED_RED,    HIGH);
}
