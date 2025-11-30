@echo off
echo Ejecutando proyecto SITM-MIO-Grafo...
echo.

if not exist "target\classes" (
    echo Las clases no estan compiladas. Ejecutando compilacion...
    call compile.bat
    echo.
)

java -cp "target/classes;src/main/resources" app.Main

