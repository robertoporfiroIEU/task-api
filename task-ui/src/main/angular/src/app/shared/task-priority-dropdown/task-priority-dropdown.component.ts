import { Component, EventEmitter, forwardRef, Input, OnInit, Optional, Output } from '@angular/core';
import { AbstractControl, ControlContainer, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { DropDown } from '../ModelsForUI';
import { ProjectConfiguration } from '../../api';
import { TranslateService } from '@ngx-translate/core';

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
    @Input() configurations: ProjectConfiguration[] = [];
    @Output() onPriorityChanged = new EventEmitter<string>();
    @Output() onEditClick = new EventEmitter<void>();

    priorities: DropDown[] = [];

    control: AbstractControl | null = null;

    constructor(@Optional() private controlContainer: ControlContainer, private translateService: TranslateService) {}

    ngOnInit(): void {
        let statusesFromConfigurations: ProjectConfiguration[] = this.configurations?.filter(
            c => c.configurationName === 'priority'
        );

        statusesFromConfigurations?.forEach(a => this.priorities.push(
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
        this.onPriorityChanged.next(status.value);
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


    getPriorityColor(priorityValue: string): string {
        return this.configurations.find(
            c => c.configurationValue === priorityValue && c.configurationName === 'priority'
        )?.color!;
    }

}
