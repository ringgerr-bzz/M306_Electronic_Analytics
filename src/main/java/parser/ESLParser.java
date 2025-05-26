package parser;

import model.Messwert;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class ESLParser {
    public static List<Messwert> parseESL(File file) {
        List<Messwert> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();

            Node meter = doc.getElementsByTagName("Meter").item(0);
            String meterId = ((Element) meter).getAttribute("factoryNo");

            NodeList rows = doc.getElementsByTagName("ValueRow");
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                String obis = row.getAttribute("obis");
                String timestamp = row.getAttribute("valueTimeStamp"); // optional
                String status = row.getAttribute("status");
                double value = Double.parseDouble(row.getAttribute("value"));
                list.add(new Messwert(meterId, obis, timestamp, value, status, file.getName()));
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen der ESL-Datei: " + file.getName());
        }
        return list;
    }
}

