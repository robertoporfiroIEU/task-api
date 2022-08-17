import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { Assign, Spectator, Task } from '../api';
import { Utils } from '../shared/Utils';
import { AuthService } from '../auth.service';
import { Roles } from '../shared/ModelsForUI';

@Injectable()
export class TaskDetailsPresenter {

    private updateTaskSubject = new Subject<Task>();
    onUpdateTask = this.updateTaskSubject.asObservable();

    taskForm = new FormGroup({});
    task: Task | null = null;

    isTaskNameReadOnly: boolean = true;
    isDescriptionEditable: boolean = false;
    isDescriptionReadOnly: boolean = true;
    isAssignEditable: boolean = false;
    isAssignReadOnly: boolean = true;
    isSpectatorEditable: boolean = false;
    isSpectatorReadOnly: boolean = true;
    isStatusEditable: boolean = false;
    isStatusReadOnly: boolean = true;
    isPriorityEditable: boolean = false;
    isPriorityReadOnly: boolean = true;
    isDueDateEditable: boolean = false;
    isDueDateReadOnly: boolean = true;

    constructor(private authService: AuthService) {}

    init(task: Task | null) {
        this.task = task;
        let usersAssigns: string[] = [];
        let groupsAssigns: string[] = [];

        task?.assigns?.forEach(assign => {
            if (assign.user) {
                usersAssigns.push(assign.user);
            }

            if (assign.group) {
                groupsAssigns.push(assign.group);
            }
        });

        let usersSpectators: string[] = [];
        let groupsSpectators: string[] = [];

        task?.spectators?.forEach(spectator => {
            if (spectator.user) {
                usersSpectators.push(spectator.user);
            }

            if (spectator.group) {
                groupsSpectators.push(spectator.group);
            }
        });

        let dueDate: Date | null = null;
        if (task?.dueDate) {
            dueDate = new Date(task.dueDate);
        }

        this.taskForm = new FormGroup({
            name: new FormControl(task?.name, [Validators.required]),
            description: new FormControl(task?.description, [Validators.maxLength(500)]),
            usersAssigns: new FormControl(usersAssigns),
            groupsAssigns: new FormControl(groupsAssigns),
            usersSpectators: new FormControl(usersSpectators),
            groupsSpectators: new FormControl(groupsSpectators),
            status: new FormControl(task?.status, [Validators.required]),
            priority: new FormControl(task?.priority, [Validators.required]),
            dueDate: new FormControl(dueDate)
        });

        if (this._canUserUpdateTheTask()) {
            this.isTaskNameReadOnly = false;
            this.isDescriptionReadOnly = false;
            this.isAssignEditable = true;
            this.isAssignReadOnly = true;
            this.isSpectatorEditable = true;
            this.isSpectatorReadOnly = true;
            this.isStatusEditable = true;
            this.isStatusReadOnly = true;
            this.isPriorityEditable = true;
            this.isPriorityReadOnly = true;
            this.isDueDateEditable = true;
        }
    }

    updateTask(task: Task): void {
        if (this.taskForm.invalid) {
            return;
        }

        let usersAssignsAutoComplete: string[] = this.taskForm.value.usersAssigns;
        let groupsAssignsAutoComplete: string[] = this.taskForm.value.groupsAssigns;
        let usersAssigns: Assign[] = usersAssignsAutoComplete?.map<Assign>(user => { return {user: user}});
        let groupsAssigns: Assign[] = groupsAssignsAutoComplete?.map<Assign>(group => { return {group: group}});
        let assigns: Assign[] = usersAssigns.concat(groupsAssigns);

        let usersSpectatorsAutoComplete: string[] = this.taskForm.value.usersSpectators;
        let groupsSpectatorsAutoComplete: string[] = this.taskForm.value.groupsSpectators;
        let usersSpectators: Spectator[] = usersSpectatorsAutoComplete?.map<Spectator>(user => { return {user: user}});
        let groupsSpectators: Spectator[] = groupsSpectatorsAutoComplete?.map<Spectator>(user => { return {user: user}});
        let spectators: Spectator[] = usersSpectators.concat(groupsSpectators);

        if (task.name == this.taskForm.value.name &&
            task.description == this.taskForm.value.description &&
            Utils.isAssignsOrSpectatorsEquals(assigns, this.task?.assigns!) &&
            Utils.isAssignsOrSpectatorsEquals(spectators, this.task?.spectators!) &&
            task.status == this.taskForm.value.status &&
            task.priority == this.taskForm.value.priority &&
            task.dueDate == this.taskForm.value.dueDate.toISOString()
        ) {
            return;
        }

        task.name = this.taskForm.value.name;
        task.description = this.taskForm.value.description;
        task.assigns = assigns;
        task.spectators = spectators;
        task.status = this.taskForm.value.status;
        task.priority = this.taskForm.value.priority;
        task.dueDate = this.taskForm.value.dueDate;

        this.updateTaskSubject.next(task);
    }

    setDescriptionState(state: boolean): void {
        if (!this.isDescriptionReadOnly) {
            this.isDescriptionEditable = state;
        }
    }

    editClickAssigns(): void {
        this.isAssignEditable = true;
        this.isAssignReadOnly = false;
    }

    cancelClickAssigns(): void {
        this.isAssignEditable = true;
        this.isAssignReadOnly = true;
    }

    editClickSpectators(): void {
        this.isSpectatorEditable = true;
        this.isSpectatorReadOnly = false;
    }

    cancelClickSpectators(): void {
        this.isSpectatorEditable = true;
        this.isSpectatorReadOnly = true;
    }

    changeStatusState(): void {
        this.isStatusEditable = !this.isStatusEditable;
        this.isStatusReadOnly = !this.isStatusReadOnly;
    }

    changePriorityState(): void {
        this.isPriorityEditable = !this.isPriorityEditable;
        this.isPriorityReadOnly = !this.isPriorityReadOnly;
    }

    changeDueDateState(): void {
        this.isDueDateEditable = !this.isDueDateEditable;
        this.isDueDateReadOnly = !this.isDueDateReadOnly;
    }

    cancelDescription(): void {
        (this.taskForm.get('description') as FormControl).setValue(this.task?.description);
        this.setDescriptionState(false);
    }

    private _canUserUpdateTheTask(): boolean {
        return this.authService.isUserRoleInRoles([
            Roles.DEVELOPER_ROLE,
            Roles.LEADER_ROLE,
            Roles.PROJECT_MANAGER_ROLE,
            Roles.ADMIN_ROLE
        ]);
    }
}
