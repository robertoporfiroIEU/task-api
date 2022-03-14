import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DropDown, TaskStatuses } from '../shared/ModelsForUI';
import { TranslateService } from '@ngx-translate/core';
import { Task, User } from '../api';
import { catchError, Observable, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Utils } from '../shared/Utils';

@Injectable()
export class CreateTaskPresenter {

    private createTaskSubject = new Subject<Task>();
    onCreateTask$: Observable<Task> = this.createTaskSubject.asObservable();
    userProfile: User | null = null;

    taskForm: FormGroup = new FormGroup({});

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

    constructor(private translateService: TranslateService, private activatedRoute: ActivatedRoute) {
    }

    init(userProfile: User | null) {
        this.userProfile = userProfile;

        this.taskForm = new FormGroup({
            name: new FormControl(null, [Validators.required]),
            description: new FormControl(null),
            status: new FormControl(TaskStatuses.CREATE, [Validators.required]),
            projectIdentifier: new FormControl(null, [Validators.required]),
            dueDate: new FormControl(null),
            usersAssigns: new FormControl([this.userProfile?.name]),
            groupsAssigns: new FormControl(null),
            usersSpectators: new FormControl([this.userProfile?.name]),
            groupsSpectators: new FormControl(null)
        });

        this.activatedRoute.queryParams.pipe(
            catchError(err => {
                return [];
            })
        ).subscribe(params => {
            let projectIdentifier = params['project-identifier'] as string;
            let projectIdentifierFormControl = this.taskForm.get('projectIdentifier') as FormControl;
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
            createdBy: this.userProfile!,
            projectIdentifier: this.taskForm.value.projectIdentifier,
            dueDate: dueDateValue!,
            assigns: assigns,
            spectators: spectators
        }

        this.createTaskSubject.next(task);
    }
}
