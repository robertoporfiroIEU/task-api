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
import { TaskStatusDropdownComponent } from './task-status-dropdown/task-status-dropdown.component';
import { PanelModule } from 'primeng/panel';
import { AutoCompleteUsersGroupsComponent } from './auto-complete-users-groups/auto-complete-users-groups.component';
import { AutoCompleteUsersGroupsContainerComponent } from './auto-complete-users-groups/auto-complete-users-groups.container';
import { TaskPriorityDropdownComponent } from './task-priority-dropdown/task-priority-dropdown.component';
import { BadgeModule } from 'primeng/badge';
import { EditorModule } from 'primeng/editor';
import { CommentsComponent } from './comments/comments.component';
import { CommentsContainerComponent } from './comments/comments.container';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { AppRoutingModule } from '../app-routing.module';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { EditorComponent } from './editor/editor.component';
import { EditorContainerComponent } from './editor/editor.container';
import { ShortTextPipe } from './pipes/short-text.pipe';
import { StringToColorPipe } from './pipes/string-to-color.pipe';
import { PaginatorModule } from 'primeng/paginator';

@NgModule({
    declarations: [
        TaskStatusDropdownComponent,
        AutoCompleteUsersGroupsComponent,
        AutoCompleteUsersGroupsContainerComponent,
        TaskPriorityDropdownComponent,
        CommentsComponent,
        CommentsContainerComponent,
        EditorComponent,
        EditorContainerComponent,
        ShortTextPipe,
        StringToColorPipe
    ],
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
        AutoCompleteModule,
        PanelModule,
        BadgeModule,
        EditorModule,
        CardModule,
        TooltipModule,
        AppRoutingModule,
        ConfirmDialogModule,
        PaginatorModule
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
        AutoCompleteModule,
        TaskStatusDropdownComponent,
        CommentsComponent,
        CommentsContainerComponent,
        PanelModule,
        AutoCompleteUsersGroupsContainerComponent,
        TaskPriorityDropdownComponent,
        EditorContainerComponent,
        BadgeModule,
        EditorModule,
        CardModule,
        TooltipModule,
        ShortTextPipe,
        StringToColorPipe
    ]
})
export class SharedModule {
}
