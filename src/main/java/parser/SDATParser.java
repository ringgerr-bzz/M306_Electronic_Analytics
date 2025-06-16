package parser;

import model.Messwert;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SDATParser {

    private static final String NS = "http://www.strom.ch";

    public static List<Messwert> parseSDAT(File file) {
        List<Messwert> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(file);


            doc.getDocumentElement().normalize();

            String meterId = file.getName().split("_")[0];

            // get StartDateTime
            String startTimeStr = getText(doc, NS, "StartDateTime");
            Instant startTime = Instant.parse(startTimeStr);

            // get Resolution
            String resolutionStr = getText(doc, NS, "Resolution");
            resolutionStr = resolutionStr.replaceAll("[^0-9]", "");
            int intervalMinutes = Integer.parseInt(resolutionStr);

            NodeList observations = doc.getElementsByTagNameNS(NS, "Observation");

            for (int i = 0; i < observations.getLength(); i++) {
                Element ob = (Element) observations.item(i);

                Element pos = (Element) ob.getElementsByTagNameNS(NS, "Position").item(0);
                String seqStr = getText(pos, NS, "Sequence");

                String volumeStr = getText(ob, NS, "Volume");

                if (seqStr == null || volumeStr == null || volumeStr.isEmpty()) continue;

                int sequence = Integer.parseInt(seqStr.trim());
                double value = Double.parseDouble(volumeStr.trim());

                Instant timestamp = startTime.plus(Duration.ofMinutes((sequence - 1) * intervalMinutes));
                String timestampStr = DateTimeFormatter.ISO_INSTANT.format(timestamp);

                list.add(new Messwert(meterId, "sdat", timestampStr, value, "", file.getName()));
            }

        } catch (Exception e) {
            System.err.println("[Fehler] SDAT-PARSING: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        return list;
    }

    private static String getText(Document doc, String ns, String tagName) {
        try {
            return doc.getElementsByTagNameNS(ns, tagName).item(0).getTextContent().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getText(Element parent, String ns, String tagName) {
        try {
            return parent.getElementsByTagNameNS(ns, tagName).item(0).getTextContent().trim();
        } catch (Exception e) {
            return null;
        }
    }
}
