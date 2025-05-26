import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-export',
  imports: [CommonModule],
  templateUrl: './export.component.html',
  styleUrls: ['./export.component.css']
})
export class ExportComponent {
  lastExport: Date | null = null;

  exportCSV(): void {
    console.log('Exportiere CSV...');
    // Hier würdest du die exportierte Datei generieren und runterladen lassen
    this.lastExport = new Date();
  }

  exportJSON(): void {
    console.log('Exportiere JSON...');
    // Hier würdest du JSON-Dateien aus vorhandenen Daten erzeugen
    this.lastExport = new Date();
  }
}
