package app;

import graph.*;
import java.util.*;
import loader.*;
import model.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║  CONSTRUCCIÓN DEL GRAFO DE ARCOS DEL SITM-MIO            ║");
            System.out.println("║  Ingeniería de Software IV - ICESI                       ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

            // Cargar datos desde archivos CSV en resources
            System.out.println(" Cargando archivos CSV...\n");
            List<Route> routes = LineCsvParser.parse("/data/lines.csv");
            List<Stop> stops = StopCsvParser.parse("/data/stops.csv");
            List<LineStop> lineStops = LineStopsCsvParser.parse("/data/linestops.csv");

            System.out.println("\n Construyendo grafos de rutas...\n");
            
            // Construir grafos
            Map<String, RouteGraph> graphs = GraphBuilder.buildGraphs(routes, stops, lineStops);

            System.out.println("✓ Se construyeron " + graphs.size() + " grafos de rutas\n");

            // Ordenar grafos para impresión: primero por lineId, luego por orientación (0=ida, 1=vuelta)
            List<RouteGraph> sortedGraphs = new ArrayList<>(graphs.values());
            sortedGraphs.sort((g1, g2) -> {
                int lineCompare = Integer.compare(g1.getLineId(), g2.getLineId());
                if (lineCompare != 0) {
                    return lineCompare;
                }
                return Integer.compare(g1.getOrientation(), g2.getOrientation());
            });

            // Imprimir grafos ordenados
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║           LISTA DE ARCOS POR RUTA Y ORIENTACIÓN           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");

            for (RouteGraph graph : sortedGraphs) {
                graph.print();
            }

            // Resumen final
            System.out.println("\n\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                      RESUMEN FINAL                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println("Total de rutas procesadas: " + routes.size());
            System.out.println("Total de paradas: " + stops.size());
            System.out.println("Total de grafos construidos: " + graphs.size());
            
            int totalArcos = sortedGraphs.stream()
                .mapToInt(RouteGraph::getArcCount)
                .sum();
            System.out.println("Total de arcos generados: " + totalArcos);

            System.out.println("\n Proceso completado exitosamente.");

        } catch (Exception e) {
            System.err.println("\n ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

