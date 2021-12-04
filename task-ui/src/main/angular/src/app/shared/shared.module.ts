import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        TranslateModule
    ],
    exports: [
        BrowserModule,
        BrowserAnimationsModule,
        TranslateModule
    ]
})
export class SharedModule {
}
