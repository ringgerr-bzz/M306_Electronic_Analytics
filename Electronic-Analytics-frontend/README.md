# Frontend (Angular)

Dieses Verzeichnis enthält die Angular-Applikation für das Echtzeit-Monitoring von Energieverbrauch und Einspeisung.

## Technologien

* Angular (Standalone Components)
* Angular Material
* ng‑apexcharts (ApexCharts für Angular)

## Voraussetzungen

* Node.js 16 oder neuer
* npm (Node Package Manager)
* Angular CLI (`npm install -g @angular/cli`)

## Installation

```bash
cd frontend
npm install
```

## Entwicklung

```bash
ng serve --open
```

* Standardmäßig unter `http://localhost:4200`
* Ändere ggf. die API-URL in `src/environments/environment.ts`:

  ```ts
  export const environment = {
    production: false,
    apiUrl: 'http://localhost:8080/api/data'
  };
  ```

## Build für Produktion

```bash
ng build --configuration production
```

* Die kompilierten Dateien liegen dann in `dist/frontend/`

## Projektstruktur

```
frontend/
├── src/
│   ├── app/
│   │   ├── file-upload/ …
│   │   ├── relative-chart/ …
│   │   └── meter-reading-chart/ …
│   ├── assets/            # statische Dateien (SVG, Icons)
│   ├── environments/      # Umgebungsvariablen
│   └── index.html
├── angular.json
└── package.json
```
