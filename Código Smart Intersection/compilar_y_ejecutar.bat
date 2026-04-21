@echo off
echo ============================================
echo  Smart Street Intersection - Compilar/Run
echo ============================================

REM Crear carpeta de salida si no existe
if not exist "out" mkdir out

REM Compilar todos los archivos Java con jSerialComm en el classpath
echo [1/2] Compilando...
javac -cp "lib\jserialcomm-2.11.4.jar" -d out -sourcepath src\main\java src\main\java\Main.java src\main\java\model\*.java src\main\java\sensor\*.java src\main\java\controller\*.java src\main\java\policy\*.java src\main\java\persistence\*.java src\main\java\arduino\*.java src\main\java\ui\*.java

if %errorlevel% neq 0 (
    echo [ERROR] La compilacion fallo. Revisa los errores arriba.
    pause
    exit /b 1
)

echo [2/2] Ejecutando...
echo.
java -cp "out;lib\jserialcomm-2.11.4.jar" Main

pause
