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
import { ProjectComponent } from './project/project.component';
import { ProjectContainerComponent } from './project/project.container';
import {TooltipModule} from 'primeng/tooltip';

@NgModule({
    declarations: [
        AppComponent,
        ProjectComponent,
        ProjectContainerComponent
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
    providers: [ {
        provide: APP_INITIALIZER,
        useFactory: appInitializerFactory,
        deps: [TranslateService],
        multi: true
    }],
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
