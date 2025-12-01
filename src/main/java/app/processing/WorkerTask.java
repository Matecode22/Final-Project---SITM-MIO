package app.processing;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import app.datagrams.Datagram;
import graph.ArcLocator;
import graph.GraphIndex;
import model.Arc;


public class WorkerTask implements Runnable {
    private final List<Datagram> datagrams;
    private final GraphIndex graphIndex;
    private final ArcLocator arcLocator;
    private final ConcurrentMap<Arc, ArcSpeedResult> global;
    private final double proximityThresholdMeters;

    public WorkerTask(List<Datagram> datagrams, GraphIndex graphIndex, 
                      ArcLocator arcLocator, ConcurrentMap<Arc, ArcSpeedResult> global, 
                      double proximityThresholdMeters) {
        this.datagrams = datagrams;
        this.graphIndex = graphIndex;
        this.arcLocator = arcLocator;
        this.global = global;
        this.proximityThresholdMeters = proximityThresholdMeters;
    }

    @Override
    public void run() {
        for (int i = 0; i < datagrams.size() - 1; i++) {
            Datagram d1 = datagrams.get(i);
            Datagram d2 = datagrams.get(i + 1);

            if (d1.getBusId() != d2.getBusId() || 
                d1.getLineId() != d2.getLineId() || 
                d1.getOrientation() != d2.getOrientation()) {
                continue;
            }

            Arc arc = null;
            if (d1.getStopId() > 0 && d2.getStopId() > 0) {
                arc = graphIndex.findArc(d1.getLineId(), d1.getOrientation(), 
                                        d1.getStopId(), d2.getStopId());
            }

            if (arc == null) {
                arc = arcLocator.findArcByProximity(d1.getLineId(), d1.getOrientation(), 
                                                    d1.getLatitude(), d1.getLongitude(), 
                                                    d2.getLatitude(), d2.getLongitude(), 
                                                    proximityThresholdMeters);
            }

            if (arc == null) continue;

            double dist = haversineMeters(d1.getLatitude(), d1.getLongitude(), 
                                         d2.getLatitude(), d2.getLongitude());
            double dt = (d2.getTimestamp() - d1.getTimestamp()) / 1000.0;

            if (dt <= 0 || dist <= 0) continue;

            global.computeIfAbsent(arc, k -> new ArcSpeedResult()).add(dist, dt);
        }
    }

    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) + 
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}

