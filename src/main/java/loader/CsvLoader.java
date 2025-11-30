package loader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvLoader {

    public static List<String[]> load(String resourcePath) throws Exception {
        List<String[]> rows = new ArrayList<>();

     
        InputStream inputStream = CsvLoader.class.getResourceAsStream(resourcePath);
        
        if (inputStream == null) {
            throw new Exception("Recurso no encontrado en classpath: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
           
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                line = line.trim();
                
                if (line.isEmpty()) {
                    continue; 
                }

             
                List<String> fields = new ArrayList<>();
                boolean inQuotes = false;
                StringBuilder currentField = new StringBuilder();

                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        inQuotes = !inQuotes;
                    } else if (c == ',' && !inQuotes) {
                        fields.add(currentField.toString().trim());
                        currentField = new StringBuilder();
                    } else {
                        currentField.append(c);
                    }
                }
                fields.add(currentField.toString().trim()); 

                
                for (int j = 0; j < fields.size(); j++) {
                    String field = fields.get(j);
                    if (field.startsWith("\"") && field.endsWith("\"")) {
                        fields.set(j, field.substring(1, field.length() - 1));
                    }
                }

                rows.add(fields.toArray(new String[fields.size()]));
            }
        }

        if (rows.isEmpty()) {
            throw new Exception("El archivo está vacío o solo contiene encabezados: " + resourcePath);
        }

        return rows;
    }
}

