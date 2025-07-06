package ch.buenzli.m306.electronicanalyticsbackend.service;

import ch.buenzli.m306.electronicanalyticsbackend.model.Messwert;
import ch.buenzli.m306.electronicanalyticsbackend.model.SensorData;
import ch.buenzli.m306.electronicanalyticsbackend.parser.SdatParser;
import ch.buenzli.m306.electronicanalyticsbackend.parser.EslParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataService {

    private final SdatParser sdatParser = new SdatParser();
    private final EslParser eslParser = new EslParser();

    private List<SensorData> lastData = Collections.emptyList();


    public List<SensorData> updateData(File sdatFile, File eslFile) throws Exception {
        Map<String, List<Messwert>> all = new HashMap<>();

        if (sdatFile != null) {
            Map<String, List<Messwert>> sdatMap = sdatParser.parse(sdatFile);
            sdatMap.forEach((id, list) ->
                    all.computeIfAbsent(id, k -> new ArrayList<>()).addAll(list)
            );
        }

        if (eslFile != null) {
            Map<String, List<Messwert>> eslMap = eslParser.parse(eslFile);
            eslMap.forEach((id, list) ->
                    all.computeIfAbsent(id, k -> new ArrayList<>()).addAll(list)
            );
        }

        for (SensorData old : lastData) {
            all.computeIfAbsent(old.getSensorId(), k -> new ArrayList<>())
                    .addAll(old.getData());
        }

        List<SensorData> combined = new ArrayList<>();
        for (var e : all.entrySet()) {
            List<Messwert> sorted = e.getValue().stream()
                    .sorted(Comparator.comparing(Messwert::getTimestamp))
                    .collect(Collectors.toList());

            LinkedHashMap<Instant, Messwert> uniq = new LinkedHashMap<>();
            for (Messwert m : sorted) {
                uniq.putIfAbsent(m.getTimestamp(), m);
            }
            List<Messwert> uniqList = new ArrayList<>(uniq.values());

            for (int i = 0; i < uniqList.size(); i++) {
                Messwert m = uniqList.get(i);
                double absCur = m.getAbsoluteValue();
                double absPrev = i > 0
                        ? uniqList.get(i - 1).getAbsoluteValue()
                        : absCur;
                m.setRelativeValue(absCur - absPrev);
            }

            combined.add(new SensorData(e.getKey(), uniqList));
        }

        lastData = combined;
        return combined;
    }

    public List<SensorData> getLastData() {
        return lastData;
    }

    public ByteArrayResource exportCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("sensorId,timestamp_iso,relativeValue,absoluteValue,source\n");
        for (SensorData sd : lastData) {
            for (Messwert m : sd.getData()) {
                double rel = m.getRelativeValue();
                double abs = m.getAbsoluteValue();
                String src;
                if (rel > 0 && abs > 0) src = "Combined";
                else if (rel > 0) src = "SDAT";
                else if (abs > 0) src = "ESL";
                else src = "Unknown";
                sb.append(sd.getSensorId()).append(',')
                        .append(m.getTimestamp()).append(',')
                        .append(rel).append(',')
                        .append(abs).append(',')
                        .append(src).append('\n');
            }
        }
        return new ByteArrayResource(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
