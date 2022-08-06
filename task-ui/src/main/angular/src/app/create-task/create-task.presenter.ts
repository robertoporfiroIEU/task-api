import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Assign, Spectator, Task } from '../api';
import { catchError, debounceTime, distinctUntilChanged, Observable, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { UserPrincipal } from '../shared/ModelsForUI';
import { Message } from 'primeng/api';

@Injectable()
export class CreateTaskPresenter {

    private createTaskSubject = new Subject<Task>();
    onCreateTask$: Observable<Task> = this.createTaskSubject.asObservable();

    private configurationsSubject = new Subject<string>();
    onConfigurations$ = this.configurationsSubject.asObservable();

    userPrincipal: UserPrincipal | null = null;

    taskForm: FormGroup = new FormGroup({});

    projectIdentifierIsMandatoryMessages: Message[] = [];

    constructor(private translateService: TranslateService, private activatedRoute: ActivatedRoute) {}

    init(userPrincipal: UserPrincipal) {
        this.userPrincipal = userPrincipal;

        this.taskForm = new FormGroup({
            name: new FormControl(null, [Validators.required]),
            description: new FormControl(null),
            status: new FormControl(null, [Validators.required]),
            priority: new FormControl(null, [Validators.required]),
            projectIdentifier: new FormControl(null, [Validators.required]),
            dueDate: new FormControl(null),
            usersAssigns: new FormControl([this.userPrincipal?.name]),
            groupsAssigns: new FormControl(null),
            usersSpectators: new FormControl([this.userPrincipal?.name]),
            groupsSpectators: new FormControl(null)
        });

        let projectIdentifierFormControl = this.taskForm.get('projectIdentifier') as FormControl;

        projectIdentifierFormControl.valueChanges.pipe(
            debounceTime(100),
            distinctUntilChanged()
        ).subscribe( value => this.configurationsSubject.next(value));

        this.activatedRoute.queryParams.pipe(
            catchError(() => {
                return [];
            })
        ).subscribe(params => {
            let projectIdentifier = params['project-identifier'] as string;
            projectIdentifierFormControl.setValue(projectIdentifier);
        });

        this.projectIdentifierIsMandatoryMessages = [
            {
                severity:'info',
                summary: this.translateService.instant('taskUI.create-task-message-title-project-identifier'),
                detail: this.translateService.instant('taskUI.create-task-message-content-project-identifier')
            }
        ]
    }

    createTask(): void {
        let usersAssignsAutoComplete: string[] = this.taskForm.value.usersAssigns;
        let groupsAssignsAutoComplete: string[] = this.taskForm.value.groupsAssigns;
        let usersAssigns: Assign[] = usersAssignsAutoComplete?.map<Assign>(user => { return {user: user}});
        let groupsAssigns: Assign[] = groupsAssignsAutoComplete?.map<Assign>(group => { return {group: group}});

        let assigns: Assign[] = [...usersAssigns];
        if (groupsAssigns) {
            assigns = usersAssigns.concat(groupsAssigns);
        }

        let usersSpectatorsAutoComplete: string[] = this.taskForm.value.usersSpectators;
        let groupsSpectatorsAutoComplete: string[] = this.taskForm.value.groupsSpectators;
        let usersSpectators: Spectator[] = usersSpectatorsAutoComplete?.map<Spectator>(user => { return {user: user}});
        let groupsSpectators: Spectator[] = groupsSpectatorsAutoComplete?.map<Spectator>(user => { return {user: user}});

        let spectators: Spectator[] = [...usersSpectators];
        if (groupsSpectators) {
            spectators = usersSpectators.concat(groupsSpectators);
        }

        let dueDateValue: string | null = this.taskForm.value.dueDate;
        if (dueDateValue) {
            dueDateValue = new Date(dueDateValue).toISOString();
        }

        let task: Task = {
            name: this.taskForm.value.name,
            description: this.taskForm.value.description,
            status: this.taskForm.value.status,
            priority: this.taskForm.value.priority,
            projectIdentifier: this.taskForm.value.projectIdentifier,
            dueDate: dueDateValue!,
            assigns: assigns,
            spectators: spectators
        }

        this.createTaskSubject.next(task);
    }
}
