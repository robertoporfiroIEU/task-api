import { Component, EventEmitter, forwardRef, Input, OnInit, Optional, Output } from '@angular/core';
import { AbstractControl, ControlContainer, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { DropDown } from '../ModelsForUI';
import { ApplicationConfiguration } from '../../api';
import { TranslateService } from '@ngx-translate/core';
import { Utils } from '../Utils';

@Component({
    selector: 'app-task-priority-dropdown',
    templateUrl: './task-priority-dropdown.component.html',
    styleUrls: ['./task-priority-dropdown.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TaskPriorityDropdownComponent),
            multi: true
        }
    ],
})
export class TaskPriorityDropdownComponent implements OnInit, ControlValueAccessor {

    @Input() formControlName: string | null = null;
    @Input() isMandatory: boolean = false;
    @Input() onlyPlaceHolder: boolean = false;
    @Input() readOnly: boolean = false;
    @Input() editable: boolean = false
    @Input() priorityValue: string | null = null;
    @Input() applicationConfigurations: ApplicationConfiguration[] = [];
    @Output() onPriorityChanged = new EventEmitter<string>();
    @Output() onEditClick = new EventEmitter<void>();

    priorities: DropDown[] = [];

    control: AbstractControl | null = null;

    constructor(@Optional() private controlContainer: ControlContainer, private translateService: TranslateService) {}

    ngOnChanges() {}

    ngOnInit(): void {
        let statusesFromConfigurations: ApplicationConfiguration[] = this.applicationConfigurations?.filter(
            c => c.configurationName === 'priority'
        );

        statusesFromConfigurations?.forEach(a => this.priorities.push(
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
        this.onPriorityChanged.next(status.value);
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

    getPriorityColor(priorityValue: string): string {
        return this.applicationConfigurations.find(
            c => c.configurationValue === priorityValue && c.configurationName === 'priority'
        )?.color!;
    }

}
