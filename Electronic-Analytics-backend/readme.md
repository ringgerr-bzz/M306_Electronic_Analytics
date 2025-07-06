# Backend (Spring Boot)

Dieses Verzeichnis enthält den Spring Boot Service für das Einlesen, Parsen und Bereitstellen der Energie-Daten.

## Technologien

* Java 17+
* Spring Boot
* JDOM2 für XML-Parsing

## Voraussetzungen

* Java 17 oder neuer
* Maven oder Gradle

## Installation & Start

```bash
cd backend
mvn clean package
mvn spring-boot:run
# oder: ./gradlew bootRun
```

* Standardmäßig läuft der Service auf `http://localhost:8080`

## API-Endpunkte

* **POST** `/api/data/upload`
  Multipart-Form-Data:

    * `sdat` (optional): SDAT-XML-Datei
    * `esl`  (optional): ESL-XML-Datei

  Antwort: JSON-Liste mit `SensorData` (sensorId, Messwert-Liste)

* **GET** `/api/data`
  Liefert die zuletzt geladenen und kombinierten Daten als JSON.

* **GET** `/api/data/export/csv`
  Liefert alle Messpunkte als CSV-Datei (`sensorId,timestamp_iso,relativeValue,absoluteValue,source`).

## Projektstruktur

```
backend/
├── src/main/java/ch/buenzli/m306/electronicanalyticsbackend/
│   ├── parser/   # SdatParser, EslParser
│   ├── model/    # Messwert, SensorData, ObisCode
│   ├── service/  # DataService
│   └── controller/ # DataController
├── pom.xml       # oder build.gradle
└── application.yml
```
