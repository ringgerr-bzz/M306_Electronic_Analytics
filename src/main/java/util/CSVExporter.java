package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CSVExporter {
    public static void exportMap(Map<String, Double> map, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("gruppe,value\n");
            for (String key : map.keySet()) {
                writer.write(String.format("%s,%.4f\n", key, map.get(key)));
            }
            System.out.println("CSV-Export erfolgreich: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim CSV-Export: " + e.getMessage());
        }
    }
}
