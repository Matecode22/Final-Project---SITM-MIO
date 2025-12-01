package app.datagrams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatagramParser {
    public interface LineConsumer {
        void accept(Datagram d);
    }

    public void parseFile(String path, LineConsumer consumer) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine();
            if (header == null) throw new Exception("Archivo vacío: " + path);

            String[] cols = header.split(",");
            Map<String,Integer> idx = new HashMap<>();
            boolean hasHeader = false;
            for (int i = 0; i < cols.length; i++) {
                String c = cols[i].trim().replaceAll("\"", "").toLowerCase();
                idx.put(c, i);
                if (c.contains("timestamp") || c.contains("lineid")) hasHeader = true;
            }

            if (!hasHeader) {
                idx.clear();
                idx.put("timestamp_ms", 0);
                idx.put("lineid", 1);
                idx.put("busid", 2);
                idx.put("tripid", 3);
                idx.put("latitude", 4);
                idx.put("longitude", 5);
                idx.put("stopid", 6);
                idx.put("orientation", 7);
                // process first line as data
                processLine(header, idx, consumer);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                processLine(line, idx, consumer);
            }
        }
    }


    public Datagram parseLine(String line, Map<String,Integer> idx) {
        try {
            String[] parts = splitCsv(line);
            long ts = Long.parseLong(getSafe(parts, idx.getOrDefault("timestamp_ms", idx.getOrDefault("timestamp",0))));
            int lineId = Integer.parseInt(getSafe(parts, idx.getOrDefault("lineid",1)));
            int busId = Integer.parseInt(getSafe(parts, idx.getOrDefault("busid",2)));
            String tripId = getSafe(parts, idx.getOrDefault("tripid",3));
            double lat = Double.parseDouble(getSafe(parts, idx.getOrDefault("latitude",4)));
            double lon = Double.parseDouble(getSafe(parts, idx.getOrDefault("longitude",5)));
            int stopId = Integer.parseInt(getSafe(parts, idx.getOrDefault("stopid",6)));
            int orientation = Integer.parseInt(getSafe(parts, idx.getOrDefault("orientation",7)));

            return new Datagram(ts, lineId, busId, tripId, lat, lon, stopId, orientation);
        } catch (Exception e) {
            return null;
        }
    }


    private void processLine(String line, Map<String,Integer> idx, LineConsumer consumer) {
        Datagram d = parseLine(line, idx);
        if (d != null) consumer.accept(d);
        else System.err.println("Parser: fila mal formada o incompleta, se omite: " + line);
    }

    private static String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                out.add(cur.toString().trim());
                cur = new StringBuilder();
            } else cur.append(c);
        }
        out.add(cur.toString().trim());
        return out.toArray(new String[0]);
    }

    private static String getSafe(String[] arr, Integer idx) {
        if (idx == null || idx < 0 || idx >= arr.length) return "0";
        return arr[idx].replaceAll("\"", "");
    }
}

