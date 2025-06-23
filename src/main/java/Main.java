// === Verbesserungsvorschlag für die Datenaggregation ===
// Problem: Momentan werden aus ESL nur Zählerstände ausgelesen, d.h. keine Differenzbildung, sondern kumulierte Werte
// SDAT-Daten enthalten valide Sequenzen, aber evtl. mit Volume = 0, was ignoriert wurde

import model.Messwert;
import parser.ESLParser;
import parser.SDATParser;
import util.CSVExporter;
import util.FileLoader;
import util.JSONExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class Main {
    enum AggregationLevel { MONTH, WEEK, DAY }

    public static void main(String[] args) {
        System.out.println("Starte Verarbeitung aller XML-Dateien...");

        String sdatFolder = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\SDAT-Files";
        String eslFolder = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\ESL-Files";
        String exportPath = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\export";
        new File(exportPath).mkdirs();

        List<Messwert> allMesswerte = new ArrayList<>();

        // === ESL-Dateien ===
        for (File file : FileLoader.getFilesFromDirectory(eslFolder, ".xml")) {
            System.out.println("Verarbeite ESL-Datei: " + file.getName());
            List<Messwert> messwerte = ESLParser.parseESL(file);
            String fallback = extractMonthFromFilename(file.getName());
            for (Messwert m : messwerte) {
                if (m.getTimestamp() == null || m.getTimestamp().isBlank()) {
                    m.setTimestamp(fallback + "-01T00:00:00");
                }
                allMesswerte.add(m);
            }
        }

        // === SDAT-Dateien ===
        for (File file : FileLoader.getFilesFromDirectory(sdatFolder, ".xml")) {
            System.out.println("Verarbeite SDAT-Datei: " + file.getName());
            allMesswerte.addAll(SDATParser.parseSDAT(file));
        }

        aggregateAndExport(allMesswerte, AggregationLevel.MONTH, "monat", exportPath);
        aggregateAndExport(allMesswerte, AggregationLevel.WEEK, "woche", exportPath);
        aggregateAndExport(allMesswerte, AggregationLevel.DAY, "tag", exportPath);
    }

    private static void aggregateAndExport(List<Messwert> messwerte, AggregationLevel level, String label, String exportPath) {
        Map<String, Double> erzeugungMap = new TreeMap<>();
        Map<String, Double> verbrauchMap = new TreeMap<>();
        Map<String, Double> differenzMap = new TreeMap<>();

        for (Messwert m : messwerte) {
            String groupKey = getGroupKey(m.getTimestamp(), level);
            if (groupKey.equals("unknown")) continue;

            boolean isVerbrauch = m.getObis().contains("1.8") || (m.getObis().equals("sdat") && m.getId().contains("742"));
            boolean isErzeugung = m.getObis().contains("2.8") || (m.getObis().equals("sdat") && m.getId().contains("735"));

            if (isVerbrauch) verbrauchMap.merge(groupKey, m.getValue(), Double::sum);
            else if (isErzeugung) erzeugungMap.merge(groupKey, m.getValue(), Double::sum);
        }

        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(verbrauchMap.keySet());
        allKeys.addAll(erzeugungMap.keySet());

        for (String key : allKeys) {
            double v = verbrauchMap.getOrDefault(key, 0.0);
            double e = erzeugungMap.getOrDefault(key, 0.0);
            differenzMap.put(key, e - v);
        }

        System.out.printf("\n--- Energiefluss pro %s ---%n", label);
        System.out.printf("%-12s %-12s %-12s %-12s%n", "Gruppe", "Verbrauch", "Erzeugung", "Differenz");
        for (String key : allKeys) {
            System.out.printf("%-12s %-12.2f %-12.2f %-12.2f%n",
                    key,
                    verbrauchMap.getOrDefault(key, 0.0),
                    erzeugungMap.getOrDefault(key, 0.0),
                    differenzMap.getOrDefault(key, 0.0));
        }

        if (level == AggregationLevel.MONTH) {
            CSVExporter.exportMap(verbrauchMap, new File(exportPath + "/verbrauch_" + label + ".csv"));
            CSVExporter.exportMap(erzeugungMap, new File(exportPath + "/erzeugung_" + label + ".csv"));
            CSVExporter.exportMap(differenzMap, new File(exportPath + "/differenz_" + label + ".csv"));

            JSONExporter.exportMap(verbrauchMap, new File(exportPath + "/verbrauch_" + label + ".json"));
            JSONExporter.exportMap(erzeugungMap, new File(exportPath + "/erzeugung_" + label + ".json"));
            JSONExporter.exportMap(differenzMap, new File(exportPath + "/differenz_" + label + ".json"));
        }
    }

    private static String getGroupKey(String timestamp, AggregationLevel level) {
        try {
            LocalDate date = LocalDate.parse(timestamp.substring(0, 10));
            switch (level) {
                case DAY:
                    return date.toString();
                case WEEK:
                    WeekFields wf = WeekFields.of(Locale.GERMANY);
                    int week = date.get(wf.weekOfWeekBasedYear());
                    return String.format("%d-W%02d", date.getYear(), week);
                case MONTH:
                    return String.format("%d-%02d", date.getYear(), date.getMonthValue());
            }
        } catch (Exception e) {
            return "unknown";
        }
        return "unknown";
    }

    private static String extractMonthFromFilename(String filename) {
        try {
            String[] parts = filename.split("_");
            String datePart = parts[1];
            return datePart.substring(0, 4) + "-" + datePart.substring(4, 6);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
