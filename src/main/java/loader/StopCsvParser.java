package loader;

import java.util.*;
import model.Stop;

public class StopCsvParser {

    public static List<Stop> parse(String path) throws Exception {
        List<String[]> rows = CsvLoader.load(path);
        List<Stop> stops = new ArrayList<>();

        for (String[] r : rows) {
            if (r.length < 8) {
                System.err.println("Advertencia: Fila con menos columnas de las esperadas, se omite");
            } else {
            try {
                int stopId = Integer.parseInt(r[0].trim());
                int planV = Integer.parseInt(r[1].trim());
                String shortName = r[2].trim();
                String longName = r[3].trim();
                // GPS_X y GPS_Y están en índices 4 y 5, pero usamos DECIMALLONG y DECIMALLAT
                double longi = Double.parseDouble(r[6].trim());
                double lati = Double.parseDouble(r[7].trim());

                stops.add(new Stop(stopId, planV, shortName, longName, longi, lati));
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear parada: " + Arrays.toString(r));
            }
            }
        }
        
        System.out.println("✓ Cargadas " + stops.size() + " paradas desde " + path);
        return stops;
    }
}

