package graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Arc;

/**
 * Construye índices para encontrar arcs por (lineId,orientation,fromStopId,toStopId).

 */
public class GraphIndex {
    // key: "lineId-orientation-fromStopId-toStopId"
    private final Map<String, Arc> index = new HashMap<>();

    public GraphIndex(Map<String, RouteGraph> graphs) {
        for (Map.Entry<String, RouteGraph> e : graphs.entrySet()) {
            RouteGraph rg = e.getValue();
            List<Arc> arcs = rg.getArcs();
            for (Arc a : arcs) {
                String k = key(rg.getLineId(), rg.getOrientation(), 
                              a.getFrom().getStopId(), a.getTo().getStopId());
                index.put(k, a);
            }
        }
    }

    private String key(int lineId, int orient, int from, int to) {
        return lineId + "-" + orient + "-" + from + "-" + to;
    }

    public Arc findArc(int lineId, int orient, int fromStopId, int toStopId) {
        return index.get(key(lineId, orient, fromStopId, toStopId));
    }
}

