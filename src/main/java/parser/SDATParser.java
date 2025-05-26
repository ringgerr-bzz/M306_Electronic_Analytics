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

    public static List<Messwert> parseSDAT(File file) {
        List<Messwert> list = new ArrayList<>();

        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(file);

            doc.getDocumentElement().normalize();

            String meterId = file.getName().split("_")[0];

            // get <rsm:StartDateTime> from header
            String startTimeStr = getText(doc, "rsm:StartDateTime");
            Instant startTime = Instant.parse(startTimeStr);

            // get <rsm:Resolution> → e.g. 15 min
            String resolutionStr = getText(doc, "rsm:Resolution");
            int intervalMinutes = Integer.parseInt(resolutionStr);

            NodeList observations = doc.getElementsByTagName("rsm:Observation");

            for (int i = 0; i < observations.getLength(); i++) {
                Element ob = (Element) observations.item(i);

                String seqStr = getText(ob, "rsm:Sequence");
                String volumeStr = getText(ob, "rsm:Volume");

                int sequence = Integer.parseInt(seqStr);
                double value = Double.parseDouble(volumeStr);

                // calculate timestamp from base + sequence
                Instant timestamp = startTime.plus(Duration.ofMinutes((sequence - 1) * intervalMinutes));
                String timestampStr = DateTimeFormatter.ISO_INSTANT.format(timestamp);

                list.add(new Messwert(meterId, "sdat", timestampStr, value, "", file.getName()));
            }

        } catch (Exception e) {
            System.err.println("[Fehler] SDAT-PARSING: " + e.getMessage());
        }

        return list;
    }

    private static String getText(Document doc, String tagName) {
        try {
            return doc.getElementsByTagName(tagName).item(0).getTextContent();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getText(Element parent, String tagName) {
        try {
            return parent.getElementsByTagName(tagName).item(0).getTextContent();
        } catch (Exception e) {
            return null;
        }
    }
}
