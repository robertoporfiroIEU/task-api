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
import { DataViewModule } from 'primeng/dataview';
import { TagModule } from 'primeng/tag';
import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { AccordionModule } from 'primeng/accordion';
import { DropdownModule } from 'primeng/dropdown';
import { AutoCompleteModule } from 'primeng/autocomplete';

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
        ToastModule,
        TagModule,
        DataViewModule,
        AvatarModule,
        AvatarGroupModule,
        AccordionModule,
        DropdownModule,
        AutoCompleteModule
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
        ToastModule,
        TagModule,
        DataViewModule,
        AvatarModule,
        AvatarGroupModule,
        AccordionModule,
        DropdownModule,
        AutoCompleteModule
    ]
})
export class SharedModule {
}
