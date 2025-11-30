@echo off
echo Compilando proyecto SITM-MIO-Grafo...
echo.

if not exist "target\classes" mkdir target\classes

javac -d target/classes -encoding UTF-8 -sourcepath src/main/java src/main/java/model/*.java src/main/java/loader/*.java src/main/java/graph/*.java src/main/java/app/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilacion exitosa!
    echo.
    echo Para ejecutar el proyecto, usa:
    echo   java -cp "target/classes;src/main/resources" app.Main
) else (
    echo.
    echo Error en la compilacion!
    exit /b 1
)

