package ch.buenzli.m306.electronicanalyticsbackend.controller;

import ch.buenzli.m306.electronicanalyticsbackend.model.SensorData;
import ch.buenzli.m306.electronicanalyticsbackend.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final DataService service;

    @Autowired
    public DataController(DataService service) {
        this.service = service;
    }

    @PostMapping(
            value    = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<SensorData> upload(
            @RequestParam(value = "sdat", required = false) MultipartFile sdat,
            @RequestParam(value = "esl",  required = false) MultipartFile esl
    ) throws Exception {

        if (sdat == null && esl == null) {
            throw new IllegalArgumentException("Mindestens eine Datei muss hochgeladen werden.");
        }

        File tmpS = null, tmpE = null;
        if (sdat != null) {
            tmpS = File.createTempFile("upload-sdat-", ".xml");
            sdat.transferTo(tmpS);
        }
        if (esl != null) {
            tmpE = File.createTempFile("upload-esl-", ".xml");
            esl.transferTo(tmpE);
        }

        // Hier wird immer beides zusammengeführt – auch wenn nur eine der Dateien existiert
        return service.updateData(tmpS, tmpE);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SensorData> getLastData() {
        return service.getLastData();
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<ByteArrayResource> exportCsv() {
        ByteArrayResource csv = service.exportCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
