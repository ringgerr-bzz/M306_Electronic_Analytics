import {Component, Input, OnChanges} from '@angular/core';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexDataLabels,
  ApexStroke,
  ApexTooltip,
  ApexXAxis,
  ApexYAxis,
  NgApexchartsModule
} from 'ng-apexcharts';
import {MatCardModule} from '@angular/material/card';

export type MeterReadingChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  yaxis: ApexYAxis;
  dataLabels: ApexDataLabels;
  stroke: ApexStroke;
  tooltip: ApexTooltip;
};

@Component({
  selector: 'app-meter-reading-chart',
  standalone: true,
  imports: [NgApexchartsModule, MatCardModule],
  template: `
    <mat-card>
      <mat-card-title>Zählerstände</mat-card-title>
      <mat-card-content>
        <apx-chart
          [series]="opts.series"
          [chart]="opts.chart"
          [xaxis]="opts.xaxis"
          [yaxis]="opts.yaxis"
          [dataLabels]="opts.dataLabels"
          [stroke]="opts.stroke"
          [tooltip]="opts.tooltip">
        </apx-chart>
      </mat-card-content>
    </mat-card>`
})
export class MeterReadingChartComponent implements OnChanges {
  @Input() data: { sensorId: string; data: any[] }[] = [];

  public opts: MeterReadingChartOptions = {
    series: [],
    chart: {type: 'line', height: 300, animations: {enabled: true}},
    xaxis: {type: 'datetime', labels: {datetimeUTC: false, format: 'dd MMM'}},
    yaxis: {
      min: 0,
      decimalsInFloat: 1,
      labels: {formatter: (v) => v.toFixed(1)}
    },
    dataLabels: {enabled: false},
    stroke: {curve: 'smooth'},
    tooltip: {
      x: {format: 'dd MMM HH:mm'},
      y: {formatter: (v) => `${v.toFixed(1)} kWh`}
    }
  };

  ngOnChanges() {
    const map = new Map(this.data.map(s => [s.sensorId, s.data]));
    const raw742 = map.get('ID742') || [];
    const raw735 = map.get('ID735') || [];

    const xs = Array.from(new Set([
      ...raw742.map(m => m.timestamp),
      ...raw735.map(m => m.timestamp)
    ]))
      .map(t => new Date(t).getTime())
      .sort((a, b) => a - b);

    const s742 = xs.map(x => {
      const m = raw742.find(r => new Date(r.timestamp).getTime() === x);
      return {x, y: m ? m.absoluteValue : null};
    });
    const s735 = xs.map(x => {
      const m = raw735.find(r => new Date(r.timestamp).getTime() === x);
      return {x, y: m ? m.absoluteValue : null};
    });

    this.opts = {
      ...this.opts,
      series: [
        {name: 'Bezug Zählerstand (kWh)', data: s742},
        {name: 'Einspeisung Zählerstand (kWh)', data: s735}
      ],
      xaxis: {
        ...this.opts.xaxis,
        min: xs[0],
        max: xs[xs.length - 1]
      }
    };
  }
}
