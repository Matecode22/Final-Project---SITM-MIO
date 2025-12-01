package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import app.metrics.MetricsReport;
import app.processing.ArcSpeedResult;
import app.processing.Master;
import app.streaming.StreamingProcessor;
import graph.ArcLocator;
import graph.GraphBuilder;
import graph.GraphIndex;
import graph.RouteGraph;
import loader.LineCsvParser;
import loader.LineStopsCsvParser;
import loader.StopCsvParser;
import model.Arc;
import model.LineStop;
import model.Route;
import model.Stop;

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

            System.out.println("Se construyeron " + graphs.size() + " grafos de rutas\n");

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

            // ========== Procesamiento de Datagramas ==========

            GraphIndex graphIndex = new GraphIndex(graphs);
            ArcLocator arcLocator = new ArcLocator(graphs);

            // Parse args for run mode
            String mode = "none";
            String dataFile = null;
            int workers = Math.max(1, Runtime.getRuntime().availableProcessors());
            int chunkLines = 100_000;
            double proximityThreshold = 60.0; // meters default
            long streamRateMs = 0;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--batch":
                        if (i + 1 < args.length) {
                            mode = "batch";
                            dataFile = args[++i];
                        }
                        break;
                    case "--stream":
                        if (i + 1 < args.length) {
                            mode = "stream";
                            dataFile = args[++i];
                        }
                        break;
                    case "--workers":
                        if (i + 1 < args.length) workers = Integer.parseInt(args[++i]);
                        break;
                    case "--chunk":
                        if (i + 1 < args.length) chunkLines = Integer.parseInt(args[++i]);
                        break;
                    case "--threshold":
                        if (i + 1 < args.length) proximityThreshold = Double.parseDouble(args[++i]);
                        break;
                    case "--rate":
                        if (i + 1 < args.length) streamRateMs = Long.parseLong(args[++i]);
                        break;
                }
            }

            if ("batch".equals(mode) && dataFile != null) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║              PROCESAMIENTO BATCH                          ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                System.out.println("Archivo: " + dataFile);
                System.out.println("Workers: " + workers);
                System.out.println("Chunk size: " + chunkLines);
                System.out.println("Threshold: " + proximityThreshold + "m\n");

                app.metrics.Timer timer = new app.metrics.Timer();
                timer.start();
                Master master = new Master(graphs);
                ConcurrentMap<Arc, ArcSpeedResult> result = master.processFileChunked(
                    dataFile, workers, chunkLines, proximityThreshold);
                long elapsed = timer.stopMs();

                MetricsReport.print("batch", 0, workers, elapsed);
                System.out.println("Arcos procesados: " + result.size());
            }

            if ("stream".equals(mode) && dataFile != null) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║            PROCESAMIENTO STREAMING                        ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                System.out.println("Archivo: " + dataFile);
                System.out.println("Consumers: " + workers);
                System.out.println("Rate: " + streamRateMs + "ms\n");

                app.metrics.Timer timer = new app.metrics.Timer();
                timer.start();
                StreamingProcessor sp = new StreamingProcessor(graphs);
                ConcurrentMap<Arc, ArcSpeedResult> res = sp.streamFromFile(
                    dataFile, workers, streamRateMs, proximityThreshold);
                long elapsed = timer.stopMs();

                MetricsReport.print("stream", 0, workers, elapsed);
                System.out.println("Arcos procesados: " + res.size());
            }

        } catch (Exception e) {
            System.err.println("\n ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

