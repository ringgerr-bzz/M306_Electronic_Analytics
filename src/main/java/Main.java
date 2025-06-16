import model.Messwert;
import parser.ESLParser;
import parser.SDATParser;
import util.FileLoader;
import util.CSVExporter;
import util.JSONExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starte Verarbeitung aller XML-Dateien...");

        String sdatFolder = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\SDAT-Files";
        String eslFolder = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\ESL-Files";
        String exportPath = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\export";
        new File(exportPath).mkdirs();

        Map<String, Double> eslBezugMap = new TreeMap<>();
        Map<String, Double> eslEinspeisungMap = new TreeMap<>();

        List<File> eslFiles = FileLoader.getFilesFromDirectory(eslFolder, ".xml");
        for (File file : eslFiles) {
            System.out.println("Verarbeite ESL-Datei: " + file.getName());
            List<Messwert> messwerte = ESLParser.parseESL(file);
            String fallbackTimestamp = extractMonthFromFilename(file.getName());

            for (Messwert m : messwerte) {
                if (m.getTimestamp() == null || m.getTimestamp().isBlank()) {
                    m.setTimestamp(fallbackTimestamp);
                }
                String monat = m.getTimestamp();
                if (monat == null || monat.isBlank()) {
                    System.err.println("[Warnung] ESL ohne Timestamp: " + m);
                    continue;
                }
                if (m.getObis().equals("1-1:1.8.2")) {
                    eslBezugMap.put(monat, m.getValue());
                } else if (m.getObis().equals("1-1:2.8.2")) {
                    eslEinspeisungMap.put(monat, m.getValue());
                }
            }
        }

        Map<String, List<Messwert>> sdatMap = new TreeMap<>();
        List<File> sdatFiles = FileLoader.getFilesFromDirectory(sdatFolder, ".xml");
        for (File file : sdatFiles) {
            System.out.println("Verarbeite SDAT-Datei: " + file.getName());
            List<Messwert> messwerte = SDATParser.parseSDAT(file);
            for (Messwert m : messwerte) {
                String monat = extractMonthFromTimestamp(m.getTimestamp());
                if (monat == null || monat.equals("unknown")) {
                    System.err.println("[Warnung] SDAT mit ungültigem Timestamp: " + m.getTimestamp() + " → " + file.getName());
                    continue;
                }
                sdatMap.computeIfAbsent(monat, k -> new ArrayList<>()).add(m);
            }
        }

        System.out.println("\n--- Energiefluss über Zeit (kombiniert) ---");
        System.out.println("Monat\t\tBezug\tEinspeisung\tNetto");

        List<Messwert> exportList = new ArrayList<>();

        Set<String> alleMonate = new TreeSet<>();
        alleMonate.addAll(eslBezugMap.keySet());
        alleMonate.addAll(sdatMap.keySet());

        for (String monat : alleMonate) {
            double eslBezug = eslBezugMap.getOrDefault(monat, 0.0);
            double eslEinspeisung = eslEinspeisungMap.getOrDefault(monat, 0.0);

            List<Messwert> sdatWerte = sdatMap.getOrDefault(monat, Collections.emptyList());
            double sdatBezugSum = sdatWerte.stream()
                    .filter(m -> m.getObis().equals("sdat") && m.getId().contains("742"))
                    .mapToDouble(Messwert::getValue).sum();

            double sdatEinspeisungSum = sdatWerte.stream()
                    .filter(m -> m.getObis().equals("sdat") && m.getId().contains("735"))
                    .mapToDouble(Messwert::getValue).sum();

            double neuerBezug = eslBezug + sdatBezugSum;
            double neueEinspeisung = eslEinspeisung + sdatEinspeisungSum;
            double netto = neuerBezug - neueEinspeisung;

            System.out.printf("%s\t%.2f\t%.2f\t%.2f\n", monat, neuerBezug, neueEinspeisung, netto);

            exportList.add(new Messwert(monat, neuerBezug, "Bezug", "combined", monat));
            exportList.add(new Messwert(monat, neueEinspeisung, "Einspeisung", "combined", monat));
            exportList.add(new Messwert(monat, netto, "Netto", "combined", monat));
        }

        CSVExporter.export(exportList, new File(exportPath + "/combined_messwerte.csv"));
        JSONExporter.export(exportList, new File(exportPath + "/combined_messwerte.json"));

        System.out.println("\n✅ CSV- und JSON-Dateien erfolgreich erstellt im Ordner: " + exportPath);
    }

    private static String extractMonthFromTimestamp(String timestamp) {
        try {
            if (timestamp == null || timestamp.length() < 7) return "unknown";
            if (timestamp.length() >= 10) {
                LocalDate date = LocalDate.parse(timestamp.substring(0, 10));
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else {
                // fallback für z.B. "2019-03"
                return timestamp.substring(0, 7);
            }
        } catch (Exception e) {
            return "unknown";
        }
    }
    private static String extractMonthFromFilename(String filename) {
        try {
            String[] parts = filename.split("_");
            String datePart = parts[1]; // z. B. "20190131"
            return datePart.substring(0, 4) + "-" + datePart.substring(4, 6); // "2019-01"
        } catch (Exception e) {
            return "unknown";
        }
    }

}
