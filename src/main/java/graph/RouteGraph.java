package graph;

import java.util.*;
import model.*;

public class RouteGraph {
    private final int lineId;
    private final int orientation; // 0 ida, 1 vuelta
    private final List<Arc> arcs = new ArrayList<>();
    private String routeName;

    public RouteGraph(int lineId, int orientation) {
        this.lineId = lineId;
        this.orientation = orientation;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void addArc(Arc arc) {
        arcs.add(arc);
    }

    public int getLineId() { return lineId; }
    public int getOrientation() { return orientation; }
    public List<Arc> getArcs() { return new ArrayList<>(arcs); }
    public int getArcCount() { return arcs.size(); }

    public void print() {
        String orientacionStr = (orientation == 0 ? "IDA" : "VUELTA");
        String nombreRuta = routeName != null ? routeName : String.valueOf(lineId);
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("RUTA: " + nombreRuta + " (ID: " + lineId + ")");
        System.out.println("ORIENTACIÓN: " + orientacionStr);
        System.out.println("NÚMERO DE ARCOS: " + arcs.size());
        System.out.println("═══════════════════════════════════════════════════════════");
        
        if (arcs.isEmpty()) {
            System.out.println("  (No hay arcos en esta ruta)");
        } else {
            for (int i = 0; i < arcs.size(); i++) {
                System.out.println("  Arco " + (i + 1) + ": " + arcs.get(i));
            }
        }
    }
}

