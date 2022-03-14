import { Component, EventEmitter, forwardRef, Input, OnInit, Output } from '@angular/core';
import { DropDown, TaskStatuses } from '../ModelsForUI';
import { TranslateService } from '@ngx-translate/core';
import { AbstractControl, ControlContainer, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
    selector: 'app-task-status-dropdown',
    templateUrl: './task-status-dropdown.component.html',
    styleUrls: ['./task-status-dropdown.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TaskStatusDropdownComponent),
            multi: true
        }
    ],
})
export class TaskStatusDropdownComponent implements OnInit, ControlValueAccessor {
    @Input() formControlName: string | null = null;
    @Input() readOnly: boolean = false;
    @Input() editable: boolean = false
    @Input() taskStatusValue: TaskStatuses | null = null;
    @Output() onStatusChanged = new EventEmitter<string>();
    @Output() onEditClick = new EventEmitter<void>();

    statuses: DropDown[] = [
        {
            label: this.translateService.instant(TaskStatuses.CREATE.valueOf()),
            value: TaskStatuses.CREATE
        },
        {
            label: this.translateService.instant(TaskStatuses.TODO.valueOf()),
            value: TaskStatuses.TODO
        },
        {
            label: this.translateService.instant(TaskStatuses.IN_PROGRESS.valueOf()),
            value: TaskStatuses.IN_PROGRESS
        },
        {
            label: this.translateService.instant(TaskStatuses.WAITING_FOR_REVIEW.valueOf()),
            value: TaskStatuses.WAITING_FOR_REVIEW
        },
        {
            label: this.translateService.instant(TaskStatuses.IN_REVIEW.valueOf()),
            value: TaskStatuses.IN_REVIEW
        },
        {
            label: this.translateService.instant(TaskStatuses.WAITING_FOR_TEST.valueOf()),
            value: TaskStatuses.WAITING_FOR_TEST
        },
        {
            label: this.translateService.instant(TaskStatuses.TEST.valueOf()),
            value: TaskStatuses.TEST
        }
    ];


    control: AbstractControl | null = null;

    constructor(private controlContainer: ControlContainer, private translateService: TranslateService) {}

    ngOnChanges() {}

    ngOnInit(): void {
        if(this.controlContainer && this.formControlName){
            this.control = this.controlContainer.control!.get(this.formControlName);
        }
    }

    writeValue(obj: any): void {

    }

    registerOnChange(fn: any): void {

    }

    registerOnTouched(fn: any): void {
    }

    statusChanged(status: any): void {
        this.onStatusChanged.next(status.value);
    }

    editClick(): void {
        this.onEditClick.emit();
    }
}
