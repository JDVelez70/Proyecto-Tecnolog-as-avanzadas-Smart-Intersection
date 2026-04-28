# Smart Street Intersection — Guía de montaje físico

## Componentes necesarios
- Arduino Mega 2560-R3
- Sensor HC-SR04 (ultrasónico)
- LED Verde  x1
- LED Amarillo x1
- LED Rojo x1
- Resistencias 220Ω x3
- Protoboard
- Cables jumper macho-macho y macho-hembra
- Cable USB tipo B (el del Mega)

---

## Paso 1 — Armar el circuito

### HC-SR04 → Arduino Mega
| HC-SR04 | Arduino Mega |
|---------|-------------|
| VCC     | 5V          |
| GND     | GND         |
| TRIG    | Pin 7       |
| ECHO    | Pin 6       |

### LEDs → Arduino Mega (todos con resistencia 220Ω en serie)
| LED       | Pin Arduino | Resistencia |
|-----------|-------------|-------------|
| Verde (+) | Pin 9       | 220Ω        |
| Amarillo (+) | Pin 10   | 220Ω        |
| Rojo (+)  | Pin 11      | 220Ω        |
| Todos (−) | GND         | —           |

> El lado largo del LED (+) va al pin. El lado corto (−) va a GND a través de la resistencia.

---

## Paso 2 — Subir el sketch al Arduino

1. Abre el **Arduino IDE**
2. Ve a **Herramientas → Placa → Arduino Mega 2560**
3. Ve a **Herramientas → Puerto** y selecciona el COM del Mega
4. Abre el archivo `arduino/vehicle_sensor.ino`
5. Haz clic en **Subir** (flecha →)
6. Cuando diga "Subida completa", cierra el Serial Monitor del IDE

> **IMPORTANTE**: El Serial Monitor del Arduino IDE debe estar CERRADO cuando corras el programa Java. Solo uno puede usar el puerto a la vez.

---

## Paso 3 — Descargar jSerialComm

1. Ve a: https://github.com/Fazecast/jSerialComm/releases
2. Descarga el archivo `jserialcomm-2.11.0.jar` (o la versión más reciente)
3. Colócalo en la carpeta `lib/` del proyecto:
   ```
   smart-intersection/
   └── lib/
       └── jserialcomm-2.11.0.jar
   ```

---

## Paso 4 — Configurar el puerto COM

1. Conecta el Arduino Mega por USB al PC
2. Abre **Administrador de dispositivos** (Win+X)
3. Busca **Puertos (COM y LPT)**
4. Anota el número del puerto (ej: COM4)
5. Abre `src/main/java/Main.java` y cambia:
   ```java
   String COM_PORT = "COM4"; // ← pon el tuyo aquí
   ```

---

## Paso 5 — Compilar y ejecutar

Doble clic en `compilar_y_ejecutar.bat`

O desde la terminal:
```cmd
cd smart-intersection
javac -cp "lib\jserialcomm-2.11.0.jar" -d out -sourcepath src\main\java src\main\java\Main.java src\main\java\model\*.java src\main\java\sensor\*.java src\main\java\controller\*.java src\main\java\policy\*.java src\main\java\persistence\*.java src\main\java\arduino\*.java src\main\java\ui\*.java
java -cp "out;lib\jserialcomm-2.11.0.jar" Main
```

---

## Comportamiento esperado

| Acción en consola | LED que enciende | Arduino imprime |
|---|---|---|
| [1] + distancia < 20 cm | 🟢 Verde | DIST:15.30 |
| [2] Solicitud peatonal | 🟡 Amarillo → 🔴 Rojo | ACK:LED:YELLOW |
| [3] Emergencia | 🔴 Rojo | ACK:LED:RED |

---

## Si algo falla

| Problema | Solución |
|---|---|
| "No se pudo conectar en COM4" | Cambia el número en Main.java al que viste en el Administrador de dispositivos |
| "Puerto ocupado" | Cierra el Serial Monitor del Arduino IDE |
| LED no enciende | Verifica polaridad del LED y que la resistencia esté bien conectada |
| Distancia siempre 999.0 | Revisa que TRIG=7 y ECHO=6 en el circuito |
