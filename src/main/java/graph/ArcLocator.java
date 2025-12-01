package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Arc;

/**
 * ArcLocator: construye índices espaciales por ruta/orientación y
 * permite buscar el arco más probable dado dos puntos GPS.
 * 
 * Estrategia:
 * - Para cada arco guardamos su segmento (lat1,lon1)-(lat2,lon2).
 * - Para una pareja de puntos (p1,p2) buscamos entre los segmentos de la misma route/orientation
 * aquellos donde las proyecciones de p1/p2 sobre el segmento estén próximas (threshold),
 * y donde el orden del parámetro de proyección t1 < t2 (consistencia de sentido).
 * - Devolver el arco cuyo score (distancia proyección + orden) sea mejor.
 */
public class ArcLocator {
    private static class Segment {
        final Arc arc;
        final double lat1, lon1, lat2, lon2;
        final double lengthMeters;

        Segment(Arc arc) {
            this.arc = arc;
            this.lat1 = arc.getFrom().getDecimalLat();
            this.lon1 = arc.getFrom().getDecimalLong();
            this.lat2 = arc.getTo().getDecimalLat();
            this.lon2 = arc.getTo().getDecimalLong();
            this.lengthMeters = haversineMeters(lat1, lon1, lat2, lon2);
        }
    }


    private final Map<String, List<Segment>> segmentsByRoute = new HashMap<>();

    public ArcLocator(Map<String, RouteGraph> graphs) {
        for (Map.Entry<String, RouteGraph> e : graphs.entrySet()) {
            String routeKey = e.getKey(); 
            RouteGraph rg = e.getValue();
            List<Segment> segs = new ArrayList<>();
            for (Arc a : rg.getArcs()) {
                // guardamos segmento si coordenadas válidas
                try {
                    if (!Double.isNaN(a.getFrom().getDecimalLat()) && 
                        !Double.isNaN(a.getTo().getDecimalLat())) {
                        segs.add(new Segment(a));
                    }
                } catch (Exception ex) {
                    
                }
            }
            segmentsByRoute.put(routeKey, segs);
        }
    }

    private String routeKey(int lineId, int orientation) {
        return lineId + "-" + orientation;
    }

    /**
     * Encuentra arco por proximidad entre dos puntos (p1->p2) sobre rutas/variants especificadas.
     * thresholdMeters: distancia máxima tolerada de la proyección (ej 60 m).
     * Devuelve null si no hay candidato satisfactorio.
     */
    public Arc findArcByProximity(int lineId, int orientation, 
                                  double lat1, double lon1, 
                                  double lat2, double lon2, 
                                  double thresholdMeters) {
        String key = routeKey(lineId, orientation);
        List<Segment> segs = segmentsByRoute.get(key);
        if (segs == null || segs.isEmpty()) return null;

        Arc best = null;
        double bestScore = Double.MAX_VALUE;

        for (Segment s : segs) {
            // proyección paramétrico t para p1 sobre s
            double[] p1proj = projectPointOnSegment(s.lat1, s.lon1, s.lat2, s.lon2, lat1, lon1);
            double t1 = p1proj[0], d1 = p1proj[1]; 
            if (d1 > thresholdMeters) continue; // p1 muy lejos de este segmento

            double[] p2proj = projectPointOnSegment(s.lat1, s.lon1, s.lat2, s.lon2, lat2, lon2);
            double t2 = p2proj[0], d2 = p2proj[1];
            if (d2 > thresholdMeters) continue; // p2 muy lejos


            if (t2 <= t1) continue;


            double score = d1 + d2; 
            if (score < bestScore) {
                bestScore = score;
                best = s.arc;
            }
        }
        return best;
    }

    private static double[] projectPointOnSegment(double lat1, double lon1, 
                                                  double lat2, double lon2, 
                                                  double plat, double plon) {

        double R = 6371000.0;
        double meanLat = Math.toRadians((lat1 + lat2 + plat) / 3.0);
        double x1 = Math.toRadians(lon1) * R * Math.cos(meanLat);
        double y1 = Math.toRadians(lat1) * R;
        double x2 = Math.toRadians(lon2) * R * Math.cos(meanLat);
        double y2 = Math.toRadians(lat2) * R;
        double xp = Math.toRadians(plon) * R * Math.cos(meanLat);
        double yp = Math.toRadians(plat) * R;

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len2 = dx*dx + dy*dy;
        double t = 0.0;
        if (len2 > 1e-6) {
            t = ((xp - x1) * dx + (yp - y1) * dy) / len2;
            if (t < 0) t = 0;
            if (t > 1) t = 1;
        }
        double projx = x1 + t * dx;
        double projy = y1 + t * dy;
        double ddx = xp - projx;
        double ddy = yp - projy;
        double dist = Math.sqrt(ddx*ddx + ddy*ddy);
        return new double[]{t, dist};
    }

    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2-lat1), dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) + 
                   Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2*R*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}

