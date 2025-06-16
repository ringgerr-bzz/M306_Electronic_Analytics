package util;

import model.Messwert;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

public class JSONExporter {
    public static void export(List<Messwert> list, File target) {
        Map<String, List<String>> sensorMap = new HashMap<>();

        for (Messwert m : list) {
            String sensorId = m.getId();
            double value = m.getAbsoluteValue();
            String ts = m.getTimestamp();

            try {
                long unix;
                if (ts.matches("\\d{4}-\\d{2}")) {
                    // Fallback für "2019-01"
                    LocalDate date = LocalDate.parse(ts + "-01");
                    unix = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                } else {
                    try {
                        Instant instant;
                        if (ts.matches("\\d{4}-\\d{2}")) {
                            // Format: 2019-01
                            LocalDate date = LocalDate.parse(ts + "-01");
                            instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
                        } else if (ts.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                            // Format: 2019-01-15T08:30:00
                            instant = LocalDateTime.parse(ts).toInstant(ZoneOffset.UTC);
                        } else {
                            // Standard
                            instant = Instant.parse(ts);
                        }
                        unix = instant.getEpochSecond();
                    } catch (Exception e) {
                        System.err.println("Ungültiger Zeitstempel bei: " + m);
                        continue;
                    }

                }
                String dataEntry = String.format("{\"ts\": %d, \"value\": %.4f}", unix, value);
                sensorMap.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(dataEntry);
            } catch (DateTimeParseException | NullPointerException e) {
                System.err.println("Ungültiger Zeitstempel bei: " + m);
            }

        }

        try (FileWriter fw = new FileWriter(target)) {
            fw.write("[\n");
            int count = 0;
            for (String id : sensorMap.keySet()) {
                fw.write("  {\n");
                fw.write("    \"sensorId\": \"" + id + "\",\n");
                fw.write("    \"data\": [\n");
                List<String> entries = sensorMap.get(id);
                for (int i = 0; i < entries.size(); i++) {
                    fw.write("      " + entries.get(i));
                    if (i < entries.size() - 1) fw.write(",");
                    fw.write("\n");
                }
                fw.write("    ]\n  }");
                if (++count < sensorMap.size()) fw.write(",");
                fw.write("\n");
            }
            fw.write("]\n");
            System.out.println("JSON-Export erfolgreich: " + target.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim JSON-Export: " + e.getMessage());
        }
    }
}
