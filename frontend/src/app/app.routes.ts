import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UploadComponent } from './upload/upload.component';
import { VisualizeComponent } from './visualize/visualize.component';
import { ExportComponent } from './export/export.component';

export const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'upload', component: UploadComponent },
  { path: 'visualize', component: VisualizeComponent },
  { path: 'export', component: ExportComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
];
