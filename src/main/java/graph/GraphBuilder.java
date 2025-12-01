package graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Arc;
import model.LineStop;
import model.Route;
import model.Stop;

public class GraphBuilder {

    public static Map<String, RouteGraph> buildGraphs(
            List<Route> routes,
            List<Stop> stops,
            List<LineStop> lineStops
    ) {
        // Crear mapa de paradas 
        Map<Integer, Stop> stopMap = new HashMap<>();
        for (Stop s : stops) {
            stopMap.put(s.getStopId(), s);
        }

        // Crear mapa de rutas para obtener nombres
        Map<Integer, Route> routeMap = new HashMap<>();
        for (Route r : routes) {
            routeMap.put(r.getLineId(), r);
        }

        Map<String, RouteGraph> graphs = new HashMap<>();

        // Agrupar LineStops por ruta y orientación
        Map<String, List<LineStop>> grouped = new HashMap<>();

        for (LineStop ls : lineStops) {
            String key = ls.getLineId() + "-" + ls.getOrientation();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(ls);
        }

        // Para cada grupo (ruta-orientación), ordenar stops y generar arcos
        for (String key : grouped.keySet()) {
            String[] parts = key.split("-");
            int lineId = Integer.parseInt(parts[0]);
            int orient = Integer.parseInt(parts[1]);

            List<LineStop> seq = grouped.get(key);
            
            // Ordenar por secuencia
            seq.sort(Comparator.comparing(LineStop::getSequence));

            RouteGraph graph = new RouteGraph(lineId, orient);
            
            Route route = routeMap.get(lineId);
            if (route != null) {
                graph.setRouteName(route.getShortName());
            }

            // Generar arcos entre paradas consecutivas
            for (int i = 0; i < seq.size() - 1; i++) {
                Stop fromStop = stopMap.get(seq.get(i).getStopId());
                Stop toStop = stopMap.get(seq.get(i + 1).getStopId());

                if (fromStop != null && toStop != null) {
                    graph.addArc(new Arc(fromStop, toStop));
                } else {
                    System.err.println("Advertencia: Parada no encontrada para arco en ruta " + 
                                     lineId + ", orientación " + orient);
                }
            }

            graphs.put(key, graph);
        }

        return graphs;
    }
}

