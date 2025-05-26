import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';


import { AppComponent } from './app.component';
import {NavbarComponent} from './navbar/navbar.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {UploadComponent} from './upload/upload.component';
import {VisualizeComponent} from './visualize/visualize.component';
import {ExportComponent} from './export/export.component';
import {Router, RouterModule} from '@angular/router';
import {routes} from './app.routes';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DashboardComponent,
    UploadComponent,
    VisualizeComponent,
    ExportComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
