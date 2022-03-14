import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { Assign, Spectator, Task } from '../api';
import { Utils } from '../shared/Utils';

@Injectable()
export class TaskDetailsPresenter {

    private updateTaskSubject = new Subject<Task>();
    onUpdateTask = this.updateTaskSubject.asObservable();

    taskForm = new FormGroup({});
    task: Task | null = null;

    isAssignEditable: boolean = true;
    isAssignReadOnly: boolean = true;
    isSpectatorEditable: boolean = true;
    isSpectatorReadOnly: boolean = true;
    isStatusEditable: boolean = true;
    isStatusReadOnly: boolean = true;
    isDueDateEditable: boolean = true;

    init(task: Task | null) {
        this.task = task;
        let usersAssigns: string[] = [];
        let groupsAssigns: string[] = [];

        task?.assigns?.forEach(assign => {
            if (assign.user) {
                usersAssigns.push(assign.user.name);
            }

            if (assign.group) {
                groupsAssigns.push(assign.group.name);
            }
        });

        let usersSpectators: string[] = [];
        let groupsSpectators: string[] = [];

        task?.spectators?.forEach(spectator => {
            if (spectator.user) {
                usersSpectators.push(spectator.user.name);
            }

            if (spectator.group) {
                groupsSpectators.push(spectator.group.name);
            }
        });

        this.taskForm = new FormGroup({
            name: new FormControl(task?.name, [Validators.required]),
            description: new FormControl(task?.description),
            usersAssigns: new FormControl(usersAssigns),
            groupsAssigns: new FormControl(groupsAssigns),
            usersSpectators: new FormControl(usersSpectators),
            groupsSpectators: new FormControl(groupsSpectators),
            status: new FormControl(task?.status, [Validators.required]),
            dueDate: new FormControl(task?.dueDate)
        });
    }

    updateTask(task: Task): void {
        if (this.taskForm.invalid) {
            return;
        }

        let assigns: Assign[] = Utils.getAssignsFromArray(this.taskForm.value.usersAssigns, this.taskForm.value.groupsAssigns);
        let spectators: Spectator[] = Utils.getSpectatorsFromArray(this.taskForm.value.usersSpectators, this.taskForm.value.groupsSpectators);

        if (task.name == this.taskForm.value.name &&
            task.description == this.taskForm.value.description &&
            Utils.isAssignsOrSpectatorsEquals(assigns, this.task?.assigns!) &&
            Utils.isAssignsOrSpectatorsEquals(spectators, this.task?.spectators!) &&
            task.status == this.taskForm.value.status &&
            task.dueDate == this.taskForm.value.dueDate
        ) {
            return;
        }

        task.name = this.taskForm.value.name;
        task.description = this.taskForm.value.description;
        task.assigns = assigns;
        task.spectators = spectators;
        task.status = this.taskForm.value.status;
        task.dueDate = this.taskForm.value.dueDate;

        this.updateTaskSubject.next(task);
    }

    changeAssignState(): void {
        this.isAssignEditable = !this.isAssignEditable;
        this.isAssignReadOnly = !this.isAssignReadOnly;
    }

    changeSpectatorState(): void {
        this.isSpectatorEditable = !this.isSpectatorEditable;
        this.isSpectatorReadOnly = !this.isSpectatorReadOnly;
    }

    changeStatusState(): void {
        this.isStatusEditable = !this.isStatusEditable;
        this.isStatusReadOnly = !this.isStatusReadOnly;
    }

    changeDueDateState(): void {
        this.isDueDateEditable = !this.isDueDateEditable;
    }

    getTaskColor(taskName: string): string {
        return Utils.getColorFromStringValue(taskName, Utils.taskColors);
    }
}
