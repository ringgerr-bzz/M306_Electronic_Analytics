import { Component, EventEmitter, Output } from '@angular/core';
import { HttpClient }                     from '@angular/common/http';
import { FormsModule }                    from '@angular/forms';
import { MatCardModule    }               from '@angular/material/card';
import { MatButtonModule  }               from '@angular/material/button';
import { MatIconModule    }               from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {
  @Output() dataLoaded = new EventEmitter<any[]>();

  selectedSdat: File | null = null;
  selectedEsl:  File | null = null;
  loading = false;

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  onSdatPicked(evt: Event) {
    const input = evt.target as HTMLInputElement;
    this.selectedSdat = input.files?.[0] || null;
  }
  onEslPicked(evt: Event) {
    const input = evt.target as HTMLInputElement;
    this.selectedEsl = input.files?.[0] || null;
  }

  upload() {
    // Frontend-Validierung
    if (!this.selectedSdat && !this.selectedEsl) {
      this.snackBar.open('Bitte wähle mindestens eine Datei aus', 'OK', { duration: 3000 });
      return;
    }

    this.loading = true;
    const form = new FormData();
    if (this.selectedSdat) form.append('sdat', this.selectedSdat);
    if (this.selectedEsl)  form.append('esl',  this.selectedEsl);

    this.http.post<any[]>('/api/data/upload', form).subscribe({
      next: data => {
        this.loading = false;
        this.snackBar.open('Daten erfolgreich geladen', 'Schließen', { duration: 3000 });
        this.dataLoaded.emit(data); // Charts aktualisieren
        // Optional: Inputs zurücksetzen
        this.selectedSdat = null;
        this.selectedEsl  = null;
      },
      error: err => {
        this.loading = false;
        this.snackBar.open('Upload fehlgeschlagen: ' + (err.message||''), 'Schließen', { duration: 5000 });
        console.error('Upload-Error', err);
      }
    });
  }
}
