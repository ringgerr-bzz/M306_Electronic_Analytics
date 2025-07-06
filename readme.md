# M306 Electronic Analytics

Dieses Projekt ermöglicht das Einlesen, Kombinieren und Visualisieren von Energieverbrauchs- und Einspeisedaten aus SDAT- und ESL-XML-Dateien.

## Hauptfunktionen

* **SDAT-Import**: Liest zeitaufgelöste Messwerte (15‑Minuten-Intervalle) ein
* **ESL-Import**: Liest punktuelle Zählerstand-Snapshots ein
* **Kombination**: Mergt beide Datenquellen, sortiert und berechnet relative Verbrauchswerte
* **Visualisierung**: Zeigt Verbrauch & Einspeisung (relativ und absolute Zählerstände) als interaktive Charts (Angular + ApexCharts)
* **CSV-Export**: Exportiert alle Messpunkte als CSV-Datei

## Repository-Struktur

```
M306_Electronic_Analytics/
├── backend/        # Spring Boot Service
├── frontend/       # Angular Applikation
├── .gitignore
└── README.md       # Diese Datei
```

## Voraussetzungen

* **Java 17+** und **Maven** (oder **Gradle**) für das Backend
* **Node.js 16+**, **npm** und **Angular CLI** für das Frontend

## Schnellstart

1. Repository klonen:

   ```bash
   git clone https://github.com/ringgerr-bzz/M306_Electronic_Analytics.git
   cd M306_Electronic_Analytics
   ```
2. Backend starten:

   ```bash
   cd backend
   mvn clean spring-boot:run
   # oder: gradlew bootRun
   ```
3. Frontend starten:

   ```bash
   cd frontend
   npm install
   ng serve --open
   ```
4. Anwendung im Browser aufrufen: [http://localhost:4200](http://localhost:4200)

---
