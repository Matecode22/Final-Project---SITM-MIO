package loader;

import java.util.*;
import model.LineStop;

public class LineStopsCsvParser {

    public static List<LineStop> parse(String path) throws Exception {
        List<String[]> rows = CsvLoader.load(path);
        List<LineStop> list = new ArrayList<>();

        for (String[] r : rows) {
            if (r.length < 8) {
                System.err.println("Advertencia: Fila con menos columnas de las esperadas, se omite");
            } else {
            try {
                int lsId = Integer.parseInt(r[0].trim());
                int seq = Integer.parseInt(r[1].trim());
                int orientation = Integer.parseInt(r[2].trim());
                int lineId = Integer.parseInt(r[3].trim());
                int stopId = Integer.parseInt(r[4].trim());
                int planV = Integer.parseInt(r[5].trim());
                int lineVariant = Integer.parseInt(r[6].trim());
                int lineVariantType = Integer.parseInt(r[7].trim());

                list.add(new LineStop(lsId, seq, orientation, lineId, stopId, 
                                     planV, lineVariant, lineVariantType));
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear LineStop: " + Arrays.toString(r));
            }
            }
        }
        
        System.out.println("✓ Cargadas " + list.size() + " relaciones línea-parada desde " + path);
        return list;
    }
}

