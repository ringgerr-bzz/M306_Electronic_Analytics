import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import { NavbarComponent } from './navbar/navbar.component';
import {AppComponent} from './app.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {UploadComponent} from './upload/upload.component';
import {VisualizeComponent} from './visualize/visualize.component';
import {ExportComponent} from './export/export.component';

import {routes} from './app.routes';

@NgModule({
    declarations: [
        DashboardComponent,
        UploadComponent,
        VisualizeComponent,
        ExportComponent,
        AppComponent,
        NavbarComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        RouterModule.forRoot(routes)
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
