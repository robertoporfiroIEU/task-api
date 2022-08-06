import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, ControlContainer } from '@angular/forms';
import { Task, User } from '../../api';
import { Type } from '../ModelsForUI';

@Component({
    selector: 'app-auto-complete-users-groups-ui',
    templateUrl: './auto-complete-users-groups.component.html',
    styleUrls: ['./auto-complete-users-groups.component.css']
})
export class AutoCompleteUsersGroupsComponent implements OnInit {

    @Input() type: Type = Type.ASSIGNS;
    @Input() task: Task | null = null;
    @Input() formControlNameForUsers: string | null = null;
    @Input() formControlNameForGroups: string | null = null;
    @Input() readOnly: boolean = false;
    @Input() editable: boolean = false;
    @Input() withSaveButton: boolean = false;
    @Input() userProfile: User | null = null
    @Input() autoCompleteUsersData: string[] = [];
    @Input() autoCompleteGroupsData: string[] = [];
    @Output() onAutocompleteUsers = new EventEmitter<string>();
    @Output() onAutocompleteGroups = new EventEmitter<string>();
    @Output() onEditClick = new EventEmitter<void>();
    @Output() onCancelClick = new EventEmitter<void>();
    @Output() onSave = new EventEmitter<void>();

    usersControl: AbstractControl | null = null;
    groupsControl: AbstractControl | null = null;
    usersAutoCompletePlaceHolder: string = '';
    groupsAutoCompletePlaceHolder: string = '';
    TYPE = Type;

    constructor(private controlContainer: ControlContainer) {}

    ngOnInit(): void {
        if (this.controlContainer && this.formControlNameForUsers){
            this.usersControl = this.controlContainer.control!.get(this.formControlNameForUsers);
        }

        if (this.controlContainer && this.formControlNameForGroups){
            this.groupsControl = this.controlContainer.control!.get(this.formControlNameForGroups);
        }

        if (this.type === Type.ASSIGNS) {
            this.usersAutoCompletePlaceHolder = 'taskUI.create-task-task-users-assigns';
            this.groupsAutoCompletePlaceHolder = 'taskUI.create-task-task-groups-assigns';
        } else {
            this.usersAutoCompletePlaceHolder = 'taskUI.create-task-task-users-spectators';
            this.groupsAutoCompletePlaceHolder = 'taskUI.create-task-task-groups-spectators';
        }
    }

    autocompleteUsers(event: any): void {
        this.onAutocompleteUsers.emit(event.query);
    }

    autocompleteGroups(event: any): void {
        this.onAutocompleteGroups.emit(event.query);
    }

    editClick(): void {
        this.onEditClick.emit();
    }

    cancelClick(): void {
        this.onCancelClick.emit();
    }

    save(): void {
        this.onSave.emit();
    }
}
