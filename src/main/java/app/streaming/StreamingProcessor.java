package app.streaming;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.datagrams.Datagram;
import app.datagrams.DatagramParser;
import app.processing.ArcSpeedResult;
import graph.ArcLocator;
import graph.GraphIndex;
import graph.RouteGraph;
import model.Arc;


public class StreamingProcessor {
    private final Map<String, RouteGraph> graphs;
    private final GraphIndex graphIndex;
    private final ArcLocator arcLocator;

    public StreamingProcessor(Map<String, RouteGraph> graphs) {
        this.graphs = graphs;
        this.graphIndex = new GraphIndex(graphs);
        this.arcLocator = new ArcLocator(graphs);
    }


    public ConcurrentMap<Arc, ArcSpeedResult> streamFromFile(String filePath, 
                                                              int numConsumers, 
                                                              long rateMillis, 
                                                              double proximityThresholdMeters) throws Exception {
        MessageQueue<Datagram> mq = new MessageQueue<>();
        ConcurrentMap<Arc, ArcSpeedResult> global = new ConcurrentHashMap<>();

        // start consumers
        ExecutorService consumers = Executors.newFixedThreadPool(numConsumers);
        for (int i = 0; i < numConsumers; i++) {
            consumers.submit(new Runnable() {
                @Override
                public void run() {
                    ConsumerWorker worker = new ConsumerWorker(mq, graphIndex, arcLocator, global);
                    worker.run();
                }
            });
        }

        DatagramParser parser = new DatagramParser();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            String line;
            Map<String,Integer> defaultIdx = buildDefaultIndex();
            while ((line = br.readLine()) != null) {
                Datagram d = parser.parseLine(line, defaultIdx);
                if (d != null) {
                    mq.publish(d);
                }
                if (rateMillis > 0) Thread.sleep(rateMillis);
            }
        } finally {
            Thread.sleep(500); 
            consumers.shutdownNow();
        }

        return global;
    }

    private Map<String,Integer> buildDefaultIndex() {
        Map<String,Integer> idx = new ConcurrentHashMap<>();
        idx.put("timestamp_ms", 0);
        idx.put("lineid", 1);
        idx.put("busid", 2);
        idx.put("tripid", 3);
        idx.put("latitude", 4);
        idx.put("longitude", 5);
        idx.put("stopid", 6);
        idx.put("orientation", 7);
        return idx;
    }
}

