package util;

import model.Messwert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    public static void export(List<Messwert> list, File target) {
        try (FileWriter fw = new FileWriter(target)) {
            fw.write("timestamp,value,obis,id,source\n");
            for (Messwert m : list) {
                fw.write(String.format("%s,%.4f,%s,%s,%s\n",
                        m.getTimestamp(),
                        m.getAbsoluteValue(),
                        m.getObis(),
                        m.getId(),
                        m.getSource()
                ));
            }
            System.out.println("CSV-Export erfolgreich: " + target.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim CSV-Export: " + e.getMessage());
        }
    }
}
