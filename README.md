# Proyecto: Construcción del Grafo de Arcos del SITM-MIO

**Curso:** Ingeniería de Software IV - ICESI  
**Objetivo:** Construir grafos de paradas por ruta (ida/vuelta) a partir de tres archivos CSV del sistema de transporte MIO.

##  Estructura del Proyecto

```
SITM-MIO-Grafo/
├── src/
│   ├── app/
│   │   └── Main.java              
│   ├── model/
│   │   ├── Route.java             # Modelo de ruta
│   │   ├── Stop.java              # Modelo de parada
│   │   ├── LineStop.java          # Modelo de relación línea-parada
│   │   └── Arc.java               # Modelo de arco (segmento entre paradas)
│   ├── loader/
│   │   ├── CsvLoader.java         # Cargador genérico de CSV
│   │   ├── LineCsvParser.java     # Parser para lines.csv
│   │   ├── StopCsvParser.java     # Parser para stops.csv
│   │   └── LineStopsCsvParser.java # Parser para linestops.csv
│   └── graph/
│       ├── RouteGraph.java         # Grafo de una ruta específica
│       └── GraphBuilder.java      # Constructor de grafos
└── data/
    ├── lines.csv                   # Archivo de rutas 
    ├── stops.csv                   # Archivo de paradas 
    └── linestops.csv               # Archivo de relaciones línea-parada 
```

## Compilación y Ejecución

### Requisitos
- Java JDK 8 o superior
- Los archivos CSV en la carpeta `data/`

### Compilación

```bash
# Compilar todos los archivos Java
javac -d bin -sourcepath src src/app/Main.java

# O compilar manualmente cada paquete
javac -d bin src/model/*.java
javac -d bin src/loader/*.java
javac -d bin src/graph/*.java
javac -d bin src/app/*.java
```

### Ejecución

```bash
# Ejecutar desde la raíz del proyecto
java -cp bin app.Main
```



