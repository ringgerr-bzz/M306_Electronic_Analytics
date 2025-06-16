# Electronic Analytics

**Energieagentur Bünzli** – Webanwendung zur Verarbeitung und Visualisierung von Zählerdaten aus SDAT- und ESL-Files.

## Projektbeschreibung
Zählerdaten im Schweizer Stromnetz liegen als relative Verbrauchswerte (SDAT) und absolute Zählerstände (ESL) vor. Diese Textdateien sind schwer zu interpretieren. Die Software:

- liest SDAT- und ESL-Files ein
- rechnet Verbrauchswerte in tatsächliche Zählerstände um
- zeigt Verbrauchs- und Zählerstandsdiagramme an
- exportiert die Daten als CSV
- (Nice-to-have) speichert oder sendet die Daten im JSON-Format via HTTP POST

## Voraussetzungen
- Java 17 oder höher
- Apache Maven 3.9+
- Node.js 16+ und npm 8+

## Repository klonen
git clone https://github.com/ringgerr-bzz/M306_Electronic_Analytics.git
cd M306_Electronic_Analytics

Projekt bauen

Vom Projekt-Root:

mvn clean install

Damit wird

    das Angular-Frontend gebaut (frontend module)

    das Spring Boot Backend kompiliert und beide in ein ausführbares JAR verpackt.

Anwendung starten
Einzelbefehle

    Frontend:

cd frontend
npm install
npm start

startet den Angular-Dev-Server unter http://localhost:4200

Backend:

cd backend
mvn spring-boot:run

startet das Spring Boot API unter http://localhost:8080 (liefert auch das Frontend-Static-Content).