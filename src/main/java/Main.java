import model.Messwert;
import parser.ESLParser;
import parser.SDATParser;
import util.FileLoader;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String folderPath = "C:\\Users\\andri\\OneDrive - Bildungszentrum Zürichsee\\School&Work\\BZZ\\Module\\M306\\Intellij_Projekt_backend\\Intellij_Projekt_backend\\Data\\ESL-Files";  // Pfad zu den hochgeladenen Dateien

        Map<String, Messwert> dataMap = new TreeMap<>();
        List<File> files = FileLoader.getFilesFromDirectory(folderPath, ".xml");

        for (File file : files) {
            if (file.getName().toLowerCase().contains("esl")) {
                for (Messwert m : ESLParser.parseESL(file)) {
                    dataMap.put(m.getKey(), m);
                }
            } else if (file.getName().toLowerCase().contains("sdat")) {
                for (Messwert m : SDATParser.parseSDAT(file)) {
                    dataMap.put(m.getKey(), m);
                }
            }
        }

        // === Aggregation für Graph ===
        Map<String, Double> inputMap = new TreeMap<>();
        Map<String, Double> outputMap = new TreeMap<>();
        Map<String, Double> netMap = new TreeMap<>();

        for (Messwert m : dataMap.values()) {
            if (m.getTimestamp() == null) continue;
            String ts = m.getTimestamp();

            if (m.getObis().startsWith("1-1:1.8")) {
                inputMap.merge(ts, m.getValue(), Double::sum);
            } else if (m.getObis().startsWith("1-1:2.8")) {
                outputMap.merge(ts, m.getValue(), Double::sum);
            } else if (m.getObis().equals("sdat")) {
                // Treat SDAT as input (you can improve this by mapping OBIS properly)
                inputMap.merge(ts, m.getValue(), Double::sum);
            }
        }

        for (String ts : inputMap.keySet()) {
            double input = inputMap.getOrDefault(ts, 0.0);
            double output = outputMap.getOrDefault(ts, 0.0);
            netMap.put(ts, input - output);
        }

        // === Print Tabelle ===
        System.out.println("\n--- Energiefluss über Zeit ---");
        System.out.println("Zeitpunkt\tInput\tOutput\tNetto");
        for (String ts : netMap.keySet()) {
            double input = inputMap.getOrDefault(ts, 0.0);
            double output = outputMap.getOrDefault(ts, 0.0);
            double net = netMap.get(ts);
            System.out.printf("%s\t%.2f\t%.2f\t%.2f\n", ts, input, output, net);
        }

        // === JSON-ähnlich für spätere Frontend-Verwendung ===
        System.out.println("\n--- JSON Output ---");
        System.out.println("[");
        for (String ts : netMap.keySet()) {
            double input = inputMap.getOrDefault(ts, 0.0);
            double output = outputMap.getOrDefault(ts, 0.0);
            double net = netMap.get(ts);
            System.out.printf("  {\"timestamp\": \"%s\", \"input\": %.2f, \"output\": %.2f, \"net\": %.2f},\n",
                    ts, input, output, net);
        }
        System.out.println("]");
    }
}
