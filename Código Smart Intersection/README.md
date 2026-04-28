# Smart Street Intersection — Parcial I 2026

## Estructura del proyecto

```
src/main/java/
├── Main.java                         ← Punto de entrada
├── model/                            ← Entidades del dominio
│   ├── User.java
│   ├── SmartIntersection.java
│   ├── Vehicle.java
│   ├── EmergencyVehicle.java
│   ├── TrafficLight.java
│   ├── PedestrianRequest.java
│   ├── Alert.java
│   ├── TrafficReport.java
│   └── Camera.java
├── sensor/                           ← Sensores (HC-SR04, botón)
│   ├── Sensor.java  (abstract)
│   ├── VehicleSensor.java
│   └── PedestrianSensor.java
├── controller/                       ← Lógica de control
│   ├── TrafficController.java
│   └── CycleController.java
├── policy/                           ← Políticas de tiempo (Strategy)
│   ├── TimingPolicy.java  (interface)
│   ├── FixedTimingPolicy.java
│   └── AdaptiveTimingPolicy.java
├── persistence/                      ← Persistencia en JSON
│   └── IntersectionRepository.java
├── arduino/                          ← Comunicación serial Arduino
│   └── ArduinoConnector.java
└── ui/                               ← Interfaz de consola
    └── ConsoleUI.java

arduino/
└── vehicle_sensor.ino                ← Sketch Arduino (Tinkercad)

data/
├── sensor_readings.json              ← Lecturas del HC-SR04
└── actions_log.json                  ← Acciones del controlador
```

## Cómo compilar y ejecutar

```bash
# Desde src/main/java/
javac -d out $(find . -name "*.java")
java -cp out Main
```

## Caso de uso implementado (UC1)

Seleccionar opción [1] en el menú → ingresar distancia en cm:
- Distancia < 20 cm → vehículo detectado → LED Verde → persistencia

## Tinkercad — Circuito Arduino

Componentes:
- Arduino Uno
- HC-SR04: TRIG=7, ECHO=6
- LED Verde: pin 9 + resistencia 220Ω
- LED Amarillo: pin 10 + resistencia 220Ω
- LED Rojo: pin 11 + resistencia 220Ω

## Patrones aplicados

### GRASP
- Information Expert → VehicleSensor.detectVehicle()
- Controller → TrafficController
- Creator → PedestrianSensor.createRequest()
- Low Coupling → CycleController ← TimingPolicy (interface)
- High Cohesion → IntersectionRepository (solo persiste)
- Pure Fabrication → ArduinoConnector

### SOLID
- S: IntersectionRepository solo persiste
- O: TimingPolicy abierta a extensión (Adaptive, Fixed, ...)
- L: EmergencyVehicle sustituye a Vehicle
- I: TimingPolicy interfaz pequeña y específica
- D: CycleController depende de TimingPolicy, no de concretos
