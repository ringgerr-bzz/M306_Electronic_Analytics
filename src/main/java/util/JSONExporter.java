package util;

import model.Messwert;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONExporter {
    public static void export(List<Messwert> messwerte, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n");
            for (int i = 0; i < messwerte.size(); i++) {
                Messwert m = messwerte.get(i);
                writer.write(String.format(
                        "  {\"timestamp\": \"%s\", \"value\": %.4f, \"obis\": \"%s\", \"id\": \"%s\", \"source\": \"%s\"}%s\n",
                        m.getTimestamp(), m.getValue(), m.getObis(), m.getId(), m.getSource(),
                        (i < messwerte.size() - 1 ? "," : "")));
            }
            writer.write("]\n");
            System.out.println("JSON-Export erfolgreich: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim JSON-Export: " + e.getMessage());
        }
    }
}
