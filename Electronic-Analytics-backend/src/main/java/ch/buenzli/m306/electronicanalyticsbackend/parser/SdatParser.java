package ch.buenzli.m306.electronicanalyticsbackend.parser;

import ch.buenzli.m306.electronicanalyticsbackend.model.Messwert;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class SdatParser {


    public Map<String, List<Messwert>> parse(File xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(xml);
        Element root = doc.getRootElement();

        boolean isProd = false;
        Iterator<Content> desc1 = root.getDescendants();
        while (desc1.hasNext()) {
            Object o = desc1.next();
            if (o instanceof Element && "ProductionMeteringPoint".equals(((Element) o).getName())) {
                isProd = true;
                break;
            }
        }
        String sensorId = isProd ? "ID735" : "ID742";

        List<Element> readings = new ArrayList<>();
        Iterator<Content> desc2 = root.getDescendants();
        while (desc2.hasNext()) {
            Object o = desc2.next();
            if (o instanceof Element && "IntervalReading".equals(((Element) o).getName())) {
                readings.add((Element) o);
            }
        }

        List<Messwert> absList = new ArrayList<>();
        for (Element ir : readings) {
            Element tp = null;
            for (Element c : ir.getChildren()) {
                if ("TimePeriod".equals(c.getName())) {
                    tp = c;
                    break;
                }
            }
            if (tp == null) continue;

            String tsStr = null;
            for (Element c2 : tp.getChildren()) {
                if ("StartDateTime".equals(c2.getName())) {
                    tsStr = c2.getText();
                    break;
                }
            }
            if (tsStr == null) continue;

            String valStr = null;
            for (Element c3 : ir.getChildren()) {
                if ("Value".equals(c3.getName())) {
                    valStr = c3.getText();
                    break;
                }
            }
            if (valStr == null) continue;

            Instant ts = Instant.parse(tsStr);
            double absVal = Double.parseDouble(valStr);
            absList.add(new Messwert(ts, 0.0, absVal));
        }

        absList.sort(Comparator.comparing(Messwert::getTimestamp));

        List<Messwert> result = new ArrayList<>();
        for (int i = 0; i < absList.size(); i++) {
            Messwert curr = absList.get(i);
            double absV = curr.getAbsoluteValue();
            double rel = (i == 0)
                    ? 0.0
                    : absV - absList.get(i - 1).getAbsoluteValue();
            result.add(new Messwert(curr.getTimestamp(), rel, absV));
        }

        return Collections.singletonMap(sensorId, result);
    }
}
