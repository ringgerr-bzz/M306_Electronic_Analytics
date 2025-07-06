import { Component, ViewChild } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { HttpClientModule }     from '@angular/common/http';
import { MatSidenav }           from '@angular/material/sidenav';
import {
  trigger, style, animate, transition,
  query, stagger
} from '@angular/animations';

import { MatToolbarModule   } from '@angular/material/toolbar';
import { MatIconModule      } from '@angular/material/icon';
import { MatButtonModule    } from '@angular/material/button';
import { MatSidenavModule   } from '@angular/material/sidenav';
import { MatListModule      } from '@angular/material/list';
import { MatCardModule      } from '@angular/material/card';

import { FileUploadComponent       } from './file-upload/file-upload.component';
import {RelativeChartComponent} from './relative-chart/relative-chart.component';
import {MeterReadingChartComponent} from './meter-reading-chart/meter-reading-chart.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    FileUploadComponent,
    RelativeChartComponent,
    MeterReadingChartComponent,],
  templateUrl: './app.component.html',
  styleUrls:  ['./app.component.scss'],
  animations: [
    trigger('fadeInStagger', [
      transition(':enter', [
        query('.card, .hero, .logo-container', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('logoFade', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('500ms 200ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0, transform: 'scale(0.8)' }))
      ])
    ])
  ]
})
export class AppComponent {
  @ViewChild('sidenav') sidenav!: MatSidenav;
  sensorData: { sensorId: string; data: any[] }[] = [];

  // Gesamtanzahl Messpunkte
  get totalPoints(): number {
    return this.sensorData.reduce((sum, s) => sum + s.data.length, 0);
  }

  // Punkte der letzten 24h
  get last24hPoints(): number {
    const cutoff = Date.now() - 24*60*60*1000;
    return this.sensorData.reduce((sum, s) =>
        sum + s.data.filter(m => new Date(m.timestamp).getTime() >= cutoff).length
      , 0);
  }

  // Wurde von FileUploadComponent gerufen
  onDataLoaded(newData: { sensorId: string; data: any[] }[]) {
    newData.forEach(sd => {
      const existing = this.sensorData.find(s => s.sensorId === sd.sensorId);
      if (existing) {
        existing.data = [
          ...existing.data,
          ...sd.data
        ].sort((a, b) =>
          new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
        );
      } else {
        this.sensorData.push({ sensorId: sd.sensorId, data: [...sd.data] });
      }
    });
    // ChangeDetection anstoßen
    this.sensorData = [...this.sensorData];
  }

  scrollTo(id: string) {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
    this.sidenav.close();
  }

  exportCsv() {
    fetch('api/data/export/csv')
      .then(r => r.blob())
      .then(blob => {
        const url = URL.createObjectURL(blob);
        const a   = document.createElement('a');
        a.href        = url;
        a.download    = 'export.csv';
        a.click();
        URL.revokeObjectURL(url);
      });
  }
}
