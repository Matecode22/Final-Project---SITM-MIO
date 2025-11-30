package loader;

import java.util.*;
import model.Route;

public class LineCsvParser {

    public static List<Route> parse(String path) throws Exception {
        List<String[]> rows = CsvLoader.load(path);
        List<Route> routes = new ArrayList<>();

        for (String[] r : rows) {
            if (r.length < 5) {
                System.err.println("Advertencia: Fila con menos columnas de las esperadas, se omite");
            } else {
            try {
                int lineId = Integer.parseInt(r[0].trim());
                int planV = Integer.parseInt(r[1].trim());
                String shortName = r[2].trim();
                String desc = r[3].trim();
                String date = r[4].trim();

                routes.add(new Route(lineId, planV, shortName, desc, date));
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear línea: " + Arrays.toString(r));
            }
            }
        }
        
        System.out.println("✓ Cargadas " + routes.size() + " rutas desde " + path);
        return routes;
    }
}

