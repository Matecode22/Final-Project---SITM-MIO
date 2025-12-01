package app.processing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import app.datagrams.Datagram;
import app.datagrams.DatagramParser;
import graph.ArcLocator;
import graph.GraphIndex;
import graph.RouteGraph;
import model.Arc;


public class Master {
    private final Map<String, RouteGraph> graphs;
    private final GraphIndex graphIndex;
    private final ArcLocator arcLocator;

    public Master(Map<String, RouteGraph> graphs) {
        this.graphs = graphs;
        this.graphIndex = new GraphIndex(graphs);
        this.arcLocator = new ArcLocator(graphs);
    }


    public ConcurrentMap<Arc, ArcSpeedResult> processFileChunked(String filePath, 
                                                                 int numWorkers, 
                                                                 int chunkLines, 
                                                                 double proximityThresholdMeters) throws Exception {
        DatagramParser parser = new DatagramParser();
        ExecutorService pool = Executors.newFixedThreadPool(numWorkers);
        ConcurrentMap<Arc, ArcSpeedResult> global = new ConcurrentHashMap<>();
        List<Future<?>> futures = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine(); 
            String line;
            List<Datagram> chunk = new ArrayList<>(chunkLines);
            Map<String,Integer> dummyIdx = buildDefaultIndex(); 

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Datagram d = parser.parseLine(line, dummyIdx);
                if (d == null) continue;
                chunk.add(d);

                if (chunk.size() >= chunkLines) {
                    // cargar chunk
                    List<Datagram> toProcess = new ArrayList<>(chunk);
                    WorkerTask w = new WorkerTask(toProcess, graphIndex, arcLocator, global, proximityThresholdMeters);
                    futures.add(pool.submit(w));
                    chunk.clear();
                }
            }

            // ultimo chunk
            if (!chunk.isEmpty()) {
                WorkerTask w = new WorkerTask(new ArrayList<>(chunk), graphIndex, arcLocator, global, proximityThresholdMeters);
                futures.add(pool.submit(w));
            }

            for (Future<?> f : futures) f.get();
        }

        pool.shutdown();
        return global;
    }


    private Map<String,Integer> buildDefaultIndex() {
        Map<String,Integer> idx = new HashMap<>();
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

