package ch.buenzli.m306.electronicanalyticsbackend.parser;

import ch.buenzli.m306.electronicanalyticsbackend.model.Messwert;
import ch.buenzli.m306.electronicanalyticsbackend.model.ObisCode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class EslParser {


    public Map<String, List<Messwert>> parse(File xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(xml);
        Element root = doc.getRootElement();
        Element meter = root.getChild("Meter");
        if (meter == null) throw new IllegalArgumentException("ESL-XML ohne <Meter>");

        Map<String, List<Messwert>> readings = new HashMap<>();

        for (Element tp : meter.getChildren("TimePeriod")) {
            String endAttr = tp.getAttributeValue("end");
            Instant ts;
            if (endAttr != null) {
                try {
                    ts = Instant.parse(endAttr);
                } catch (DateTimeParseException ex) {
                    LocalDateTime ldt = LocalDateTime.parse(endAttr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    ts = ldt.toInstant(ZoneOffset.UTC);
                }
            } else {
                ts = Instant.now();
            }

            for (Element vr : tp.getChildren("ValueRow")) {
                String obis = vr.getAttributeValue("obis");
                String valStr = vr.getAttributeValue("value");
                if (obis == null || valStr == null) continue;
                double absVal = Double.parseDouble(valStr);

                Optional<ObisCode> code = Arrays.stream(ObisCode.values())
                        .filter(o -> o.getCode().equals(obis))
                        .findFirst();
                if (code.isEmpty()) continue;
                String sensorId = code.get().getSensorId();

                readings.computeIfAbsent(sensorId, k -> new ArrayList<>())
                        .add(new Messwert(ts, 0.0, absVal));
            }
        }

        return readings;
    }
}
