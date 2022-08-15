import { Component, EventEmitter, forwardRef, Input, OnInit, Optional, Output } from '@angular/core';
import { DropDown } from '../ModelsForUI';
import { TranslateService } from '@ngx-translate/core';
import { AbstractControl, ControlContainer, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ApplicationConfiguration } from '../../api';

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
    @Input() applicationConfigurations: ApplicationConfiguration[] = [];
    @Output() onStatusChanged = new EventEmitter<string>();
    @Output() onEditClick = new EventEmitter<void>();

    statuses: DropDown[] = [];

    control: AbstractControl | null = null;

    constructor(@Optional() private controlContainer: ControlContainer, private translateService: TranslateService) {}

    ngOnChanges() {}

    ngOnInit(): void {
        let statusesFromConfigurations: ApplicationConfiguration[] = this.applicationConfigurations?.filter(
            c => c.configurationName === 'status'
        );

        statusesFromConfigurations?.forEach(a => this.statuses.push(
            {
                label: this.translateService.instant(a.configurationLabel!),
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

    }

    registerOnChange(fn: any): void {

    }

    registerOnTouched(fn: any): void {
    }

    getStatusColor(taskStatusValue: string): string {
        return this.applicationConfigurations.find(
            c => c.configurationValue === taskStatusValue && c.configurationName === 'status'
        )?.color!;
    }
}
