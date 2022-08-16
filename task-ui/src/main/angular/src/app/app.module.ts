import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from './shared/shared.module';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
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
import { TaskDetailsComponent } from './task-details/task-details.component';
import { TaskDetailsContainerComponent } from './task-details/task-details.container';
import { UnauthorisedComponent } from './unauthorised/unauthorised.component';
import { UserSettingsComponent } from './user-settings/user-settings.component';
import { UserSettingsContainerComponent } from './user-settings/user-settings.container';
import { ApiModule, ConfigurationParameters, Configuration } from './api';
import { CsrfInterceptorInterceptor } from './csrf-interceptor.interceptor';


// configuring providers
export function apiConfigFactory (): Configuration {
    const params: ConfigurationParameters = {
        basePath: window.location.origin + '/tasks-api'
    }
    return new Configuration(params);
}

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
        CreateTaskContainerComponent,
        TaskDetailsComponent,
        TaskDetailsContainerComponent,
        UnauthorisedComponent,
        UserSettingsComponent,
        UserSettingsContainerComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        SharedModule,
        ShellModule,
        HttpClientModule,
        HttpClientXsrfModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        TooltipModule,
        ApiModule.forRoot(apiConfigFactory)
    ],
    providers: [{
            provide: APP_INITIALIZER,
            useFactory: appInitializerFactory,
            deps: [TranslateService],
            multi: true
        },
        { provide: HTTP_INTERCEPTORS, useClass: CsrfInterceptorInterceptor, multi: true },
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
        let language: string | null = localStorage.getItem('language');

        if (!language) {
            let browserLanguage = translate.getBrowserLang();

            if (browserLanguage) {
                language = browserLanguage;
            } else {
                language = 'en';
            }
        }
        translate.setDefaultLang(language);
        return translate.use(language);
    };
}
