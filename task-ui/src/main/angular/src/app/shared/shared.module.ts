import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ToastModule } from 'primeng/toast';
import {MessageService} from 'primeng/api';


@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        TranslateModule,
        TableModule,
        ButtonModule,
        InputTextModule,
        CalendarModule,
        FormsModule,
        ReactiveFormsModule,
        ProgressSpinnerModule,
        InputTextareaModule,
        ToastModule
    ],
    exports: [
        BrowserModule,
        BrowserAnimationsModule,
        TranslateModule,
        TableModule,
        ButtonModule,
        InputTextModule,
        CalendarModule,
        FormsModule,
        ReactiveFormsModule,
        ProgressSpinnerModule,
        InputTextareaModule,
        ToastModule
    ]
})
export class SharedModule {
}
