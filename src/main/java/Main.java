// Erweiterter Main-Code mit sdat/ESL-Verknüpfung, Export & HTTP-POST
import model.Messwert;
import parser.ESLParser;
import parser.SDATParser;
import util.FileLoader;
import util.CSVExporter;
import util.JSONExporter;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;


public class Main {
    public static void main(String[] args) {
        System.out.println("Starte Verarbeitung aller XML-Dateien im 'data'-Ordner...");

        String folderPath = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\SDAT-Files";
        String exportPath = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\_M306_Electronic_Analytics\\Data\\export";
        new File(exportPath).mkdirs();

        Map<String, Messwert> messwertMap = new TreeMap<>();
        Map<String, List<Messwert>> sensorMap = new HashMap<>();

        List<File> files = FileLoader.getFilesFromDirectory(folderPath, ".xml");

        if (files.isEmpty()) {
            System.out.println("Keine XML-Dateien im Verzeichnis gefunden: " + folderPath);
            return;
        }

        for (File file : files) {
            System.out.println("Verarbeite Datei: " + file.getName());

            List<Messwert> messwerte;
            if (isSDAT(file)) {
                messwerte = SDATParser.parseSDAT(file);
            } else {
                messwerte = ESLParser.parseESL(file);
            }

            String fallbackTimestamp = extractMonthFromFilename(file.getName());

            for (Messwert m : messwerte) {
                if (m.getTimestamp() == null || m.getTimestamp().isBlank()) {
                    m.setTimestamp(fallbackTimestamp);
                }
                String key = m.getKey();
                messwertMap.put(key, m);
                sensorMap.computeIfAbsent(m.getId(), k -> new ArrayList<>()).add(m);
            }
        }

        if (messwertMap.isEmpty()) {
            System.out.println("Keine gültigen Messwerte extrahiert.");
            return;
        }

        List<Messwert> sortedList = new ArrayList<>(messwertMap.values());
        sortedList.sort(Comparator.comparing(Messwert::getTimestamp));

        Map<String, Double> bezugMap = new TreeMap<>();
        Map<String, Double> einspeisungMap = new TreeMap<>();
        Map<String, Double> netzMap = new TreeMap<>();

        double bezugSumme = 0;
        double einspeisungSumme = 0;

        for (Messwert m : sortedList) {
            String ts = m.getTimestamp();
            if (ts == null || ts.isBlank()) continue;

            boolean isBezug = m.getObis().equals("sdat") && m.getId().contains("742") || m.getObis().startsWith("1-1:1.8");
            boolean isEinspeisung = m.getObis().equals("sdat") && m.getId().contains("735") || m.getObis().startsWith("1-1:2.8");

            if (isBezug) {
                bezugSumme += m.getValue();
                bezugMap.merge(ts, m.getValue(), Double::sum);
                m.setAbsoluteValue(bezugSumme);
            } else if (isEinspeisung) {
                einspeisungSumme += m.getValue();
                einspeisungMap.merge(ts, m.getValue(), Double::sum);
                m.setAbsoluteValue(einspeisungSumme);
            }
        }

        for (String ts : bezugMap.keySet()) {
            double b = bezugMap.getOrDefault(ts, 0.0);
            double e = einspeisungMap.getOrDefault(ts, 0.0);
            netzMap.put(ts, b - e);
        }

        // Ausgabe
        System.out.println("\n--- Energiefluss über Zeit ---");
        System.out.println("Zeitpunkt\t\tBezug\tEinspeisung\tNetto");
        for (String ts : netzMap.keySet()) {
            double b = bezugMap.getOrDefault(ts, 0.0);
            double e = einspeisungMap.getOrDefault(ts, 0.0);
            double n = netzMap.get(ts);
            System.out.printf("%s\t%.2f\t%.2f\t%.2f\n", ts, b, e, n);
        }

        // JSON-Ausgabe
        System.out.println("\n--- JSON Output ---");
        System.out.println("[");
        Iterator<String> it = netzMap.keySet().iterator();
        while (it.hasNext()) {
            String ts = it.next();
            double b = bezugMap.getOrDefault(ts, 0.0);
            double e = einspeisungMap.getOrDefault(ts, 0.0);
            double n = netzMap.get(ts);
            System.out.printf("  {\"timestamp\": \"%s\", \"input\": %.2f, \"output\": %.2f, \"net\": %.2f}%s\n",
                    ts, b, e, n, it.hasNext() ? "," : "");
        }
        System.out.println("]");

        // Exporte
        CSVExporter.export(sortedList, new File(exportPath + "/messwerte.csv"));
        JSONExporter.export(sortedList, new File(exportPath + "/messwerte.json"));

        System.out.println("\n✅ CSV- und JSON-Dateien erfolgreich erstellt im Ordner: " + exportPath);
    }

    private static String extractMonthFromFilename(String filename) {
        try {
            String datePart = filename.split("_")[1];
            LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("yyyyMMdd"));
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static boolean isSDAT(File file) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();
            String rootName = doc.getDocumentElement().getNodeName();
            return rootName.contains("ValidatedMeteredData");
        } catch (Exception e) {
            return false;
        }
    }

}