import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from './shared/shared.module';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { ShellModule } from './shell/shell.module';
import { ProjectsComponent } from './projects/projects.component';
import { ProjectContainerComponent } from './projects/projects.container';
import { TooltipModule } from 'primeng/tooltip';
import { CreateUpdateViewProjectComponent } from './project-details/create-update-view-project.component';
import { CreateProjectContainerComponent } from './project-details/create-project.container';
import { MessageService } from 'primeng/api';
import { UpdateProjectContainerComponent } from './project-details/update-project.container';
import { TasksComponent } from './tasks/tasks.component';
import { TasksContainerComponent } from './tasks/tasks.container';
import { ViewProjectContainerComponent } from './project-details/view-project.container';
import { CreateTaskComponent } from './create-task/create-task.component';
import { CreateTaskContainerComponent } from './create-task/create-task.container';

@NgModule({
    declarations: [
        AppComponent,
        ProjectsComponent,
        ProjectContainerComponent,
        CreateUpdateViewProjectComponent,
        CreateProjectContainerComponent,
        UpdateProjectContainerComponent,
        TasksComponent,
        TasksContainerComponent,
        ViewProjectContainerComponent,
        CreateTaskComponent,
        CreateTaskContainerComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        SharedModule,
        ShellModule,
        HttpClientModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        TooltipModule,
    ],
    providers: [{
            provide: APP_INITIALIZER,
            useFactory: appInitializerFactory,
            deps: [TranslateService],
            multi: true
        },
        MessageService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
    return new TranslateHttpLoader(http);
}

export function appInitializerFactory(translate: TranslateService) {
    return () => {
        let defaultLanguage: string = translate.getBrowserLang() ? translate.getBrowserLang()!: 'en';

        translate.setDefaultLang(defaultLanguage);
        return translate.use(defaultLanguage);
    };
}
