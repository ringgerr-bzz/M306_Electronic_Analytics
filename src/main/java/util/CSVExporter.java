package util;

import model.Messwert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    public static void export(List<Messwert> messwerte, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("timestamp,value,obis,id,source\n");
            for (Messwert m : messwerte) {
                writer.write(String.format("%s,%.4f,%s,%s,%s\n",
                        m.getTimestamp(),
                        m.getValue(),
                        m.getObis(),
                        m.getId(),
                        m.getSource()));
            }
            System.out.println("CSV-Export erfolgreich: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim CSV-Export: " + e.getMessage());
        }
    }
}
