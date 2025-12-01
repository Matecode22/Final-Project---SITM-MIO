package app.streaming;

import java.util.concurrent.ConcurrentMap;

import app.datagrams.Datagram;
import app.processing.ArcSpeedResult;
import graph.ArcLocator;
import graph.GraphIndex;
import model.Arc;

public class ConsumerWorker implements Runnable {
    private final MessageQueue<Datagram> queue;
    private final GraphIndex graphIndex;
    private final ArcLocator arcLocator;
    private final ConcurrentMap<Arc, ArcSpeedResult> global;

    public ConsumerWorker(MessageQueue<Datagram> queue,
                          GraphIndex graphIndex,
                          ArcLocator arcLocator,
                          ConcurrentMap<Arc, ArcSpeedResult> global) {
        this.queue = queue;
        this.graphIndex = graphIndex;
        this.arcLocator = arcLocator;
        this.global = global;
    }

    @Override
    public void run() {
        try {
            Datagram d1 = null;
            while (true) { // se detiene cuando StreamingProcessor hace shutdownNow
                if (d1 == null) {
                    d1 = queue.take();
                }
                Datagram d2 = queue.take();

                if (d1.getBusId() != d2.getBusId() ||
                    d1.getLineId() != d2.getLineId() ||
                    d1.getOrientation() != d2.getOrientation()) {
                    d1 = d2; 
                    continue;
                }

                Arc arc = null;

                if (d1.getStopId() > 0 && d2.getStopId() > 0) {
                    arc = graphIndex.findArc(
                            d1.getLineId(),
                            d1.getOrientation(),
                            d1.getStopId(),
                            d2.getStopId());
                }

                if (arc == null) {
                    arc = arcLocator.findArcByProximity(d1.getLineId(), d1.getOrientation(),
                            d1.getLatitude(), d1.getLongitude(),
                            d2.getLatitude(), d2.getLongitude(),
                            60.0); 
                }

                if (arc == null) {
                    d1 = d2;
                    continue;
                }

                double dist = haversineMeters(d1.getLatitude(), d1.getLongitude(),
                        d2.getLatitude(), d2.getLongitude());

                double dt = (d2.getTimestamp() - d1.getTimestamp()) / 1000.0;

                if (dt <= 0 || dist <= 0) {
                    d1 = d2;
                    continue;
                }

                global.computeIfAbsent(arc, k -> new ArcSpeedResult())
                        .add(dist, dt);

                d1 = d2; 
            }
        } catch (InterruptedException ex) {
            // termina
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

