import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartType } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  lineChartLabels: any = "Zählerstand LinienDiagramm";
  doughnutLabels: any = "Doughnout Diagramm";
  barChartLabels: any = "Verbrauch Balkendiagramm";


  // Balkendiagramm
  barChartType: ChartType = 'bar';
  barChartData = {
    labels: ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'],
    datasets: [
      { data: [12.4, 10.3, 8.9, 14.2, 9.5, 6.7, 11.1], label: 'Verbrauch (kWh)' }
    ]
  };

  // Liniendiagramm
  lineChartType: ChartType = 'line';
  lineChartData = {
    labels: ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00'],
    datasets: [
      { data: [200, 215, 230, 250, 260, 275], label: 'Zählerstand (kWh)' }
    ]
  };

  // Doughnut Chart
  doughnutType: ChartType = 'doughnut';
  doughnutData = {
    labels: ['Bezug', 'Einspeisung'],
    datasets: [
      { data: [7500, 3200] }
    ]
  };


}
