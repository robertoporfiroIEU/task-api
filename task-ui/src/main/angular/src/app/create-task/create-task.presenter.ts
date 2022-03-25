import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Task, User } from '../api';
import { catchError, debounceTime, distinctUntilChanged, Observable, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Utils } from '../shared/Utils';

@Injectable()
export class CreateTaskPresenter {

    private createTaskSubject = new Subject<Task>();
    onCreateTask$: Observable<Task> = this.createTaskSubject.asObservable();

    private configurationsSubject = new Subject<string>();
    onConfigurations$ = this.configurationsSubject.asObservable();

    userProfile: User | null = null;

    taskForm: FormGroup = new FormGroup({});

    constructor(private translateService: TranslateService, private activatedRoute: ActivatedRoute) {
    }

    init(userProfile: User | null) {
        this.userProfile = userProfile;

        this.taskForm = new FormGroup({
            name: new FormControl(null, [Validators.required]),
            description: new FormControl(null),
            status: new FormControl(null, [Validators.required]),
            priority: new FormControl(null, [Validators.required]),
            projectIdentifier: new FormControl(null, [Validators.required]),
            dueDate: new FormControl(null),
            usersAssigns: new FormControl([this.userProfile?.name]),
            groupsAssigns: new FormControl(null),
            usersSpectators: new FormControl([this.userProfile?.name]),
            groupsSpectators: new FormControl(null)
        });

        let projectIdentifierFormControl = this.taskForm.get('projectIdentifier') as FormControl;

        projectIdentifierFormControl.valueChanges.pipe(
            debounceTime(100),
            distinctUntilChanged()
        ).subscribe( value => this.configurationsSubject.next(value));

        this.activatedRoute.queryParams.pipe(
            catchError(err => {
                return [];
            })
        ).subscribe(params => {
            let projectIdentifier = params['project-identifier'] as string;
            projectIdentifierFormControl.setValue(projectIdentifier);
        });
    }

    createTask(): void {
        let usersAssigns: string[] = this.taskForm.value.usersAssigns;
        let groupsAssigns: string[] = this.taskForm.value.groupsAssigns;
        let assigns = Utils.getAssignsFromArray(usersAssigns, groupsAssigns);

        let usersSpectators: string[] = this.taskForm.value.usersSpectators;
        let groupsSpectators: string[] = this.taskForm.value.groupsSpectators;
        let spectators = Utils.getSpectatorsFromArray(usersSpectators, groupsSpectators);

        let dueDateValue: string | null = this.taskForm.value.dueDate;
        if (dueDateValue) {
            dueDateValue = new Date(dueDateValue).toISOString();
        }

        let task: Task = {
            name: this.taskForm.value.name,
            description: this.taskForm.value.description,
            status: this.taskForm.value.status,
            priority: this.taskForm.value.priority,
            createdBy: this.userProfile!,
            projectIdentifier: this.taskForm.value.projectIdentifier,
            dueDate: dueDateValue!,
            assigns: assigns,
            spectators: spectators
        }

        this.createTaskSubject.next(task);
    }
}
