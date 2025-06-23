package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JSONExporter {
    public static void exportMap(Map<String, Double> map, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n");
            int count = 0;
            for (String key : map.keySet()) {
                writer.write(String.format("  {\"gruppe\": \"%s\", \"value\": %.4f}%s\n",
                        key, map.get(key), (++count < map.size()) ? "," : ""));
            }
            writer.write("]\n");
            System.out.println("JSON-Export erfolgreich: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim JSON-Export: " + e.getMessage());
        }
    }
}
