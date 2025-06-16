import { Component } from '@angular/core';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent {
  sdatFile: File | null = null;
  eslFile: File | null = null;

  onSdatSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.sdatFile = input.files?.[0] || null;
    console.log('SDAT Datei gewählt:', this.sdatFile?.name);
  }

  onEslSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.eslFile = input.files?.[0] || null;
    console.log('ESL Datei gewählt:', this.eslFile?.name);
  }

  uploadFiles(): void {
    if (!this.sdatFile || !this.eslFile) {
      alert('Bitte beide Dateien auswählen!');
      return;
    }

    console.log('Hochlade Dateien:', this.sdatFile.name, this.eslFile.name);
    // Hier kommt später der Upload zur API oder Parser-Logik rein
  }
}
