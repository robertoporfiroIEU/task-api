import { Component, EventEmitter, forwardRef, Input, OnInit, Optional, Output } from '@angular/core';
import { DropDown } from '../ModelsForUI';
import { AbstractControl, ControlContainer, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ProjectConfiguration } from '../../api';

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
    @Input() onlyPlaceHolder: boolean = false;
    @Input() isMandatory: boolean = false;
    @Input() readOnly: boolean = false;
    @Input() editable: boolean = false
    @Input() taskStatusValue: string | null = null;
    @Input() configurations: ProjectConfiguration[] = [];
    @Output() onStatusChanged = new EventEmitter<string>();
    @Output() onEditClick = new EventEmitter<void>();

    statuses: DropDown[] = [];

    control: AbstractControl | null = null;

    constructor(@Optional() private controlContainer: ControlContainer) {}

    ngOnInit(): void {
        let statusesFromConfigurations: ProjectConfiguration[] = this.configurations?.filter(
            c => c.configurationName === 'status'
        );

        statusesFromConfigurations?.forEach(a => this.statuses.push(
            {
                label: a.configurationValue,
                value: a.configurationValue
            }
        ));

        if (this.controlContainer && this.formControlName){
            this.control = this.controlContainer.control!.get(this.formControlName);
        }
    }

    statusChanged(status: any): void {
        this.onStatusChanged.next(status.value);
    }

    editClick(): void {
        this.onEditClick.emit();
    }

    writeValue(obj: any): void {
        // writeValue
    }

    registerOnChange(fn: any): void {
        // registerOnChange
    }

    registerOnTouched(fn: any): void {
        // registerOnTouched
    }

    getStatusColor(taskStatusValue: string): string {
        return this.configurations.find(
            c => c.configurationValue === taskStatusValue && c.configurationName === 'status'
        )?.color!;
    }
}
