import { Component, OnInit } from '@angular/core';
import { TasksService, Task, User, ProjectsService, ApplicationConfiguration } from '../api';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Subject, switchMap, take, takeUntil } from 'rxjs';
import { ErrorService } from '../error.service';
import { RoutesEnum } from '../RoutesEnum';
import { UserProfileService } from '../user-profile.service';
import { ShellService } from '../shell/shell.service';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-task-details',
    templateUrl: './task-details.container.html'
})
export class TaskDetailsContainerComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    applicationConfigurations: ApplicationConfiguration[] | undefined = undefined;
    userProfile: User | null = null;
    task: Task | null = null;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private userProfileService: UserProfileService,
        private messageService: MessageService,
        private translateService: TranslateService,
        private shellService: ShellService,
        private tasksService: TasksService,
        private projectsService: ProjectsService,
        private errorService: ErrorService
    ) {}

    ngOnInit(): void {
        this.activatedRoute.paramMap.pipe(
            take(1),
            switchMap(params => {
                let taskIdentifier = params.get('taskIdentifier') as string
                return this.tasksService.getTask(taskIdentifier);
            }),
            switchMap(task => {
                if (Object.keys(task).length === 0) {
                    this.errorService.showCustomErrorMessage(
                        'taskUI.task-details-task-not-found-summary',
                        'taskUI.task-details-task-not-found-details'
                    );
                    this.router.navigate([RoutesEnum.tasks])
                    return [];
                }
                this.task = task;
                return this.projectsService.getProject(task.projectIdentifier!);
            }),
            catchError(err => {
                this.errorService.showErrorMessage(err);
                return [];
            })
        ).subscribe(project => {
            this.applicationConfigurations = project.configurations;
        });

        this.userProfileService.userProfile$.pipe(
            takeUntil(this.destroy),
        ).subscribe( userProfile => this.userProfile = userProfile);

    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

    updateTask(task: Task) {
        this.shellService.setLoadingSpinner(true);
        let identifier: string = task.identifier!;
        this.tasksService.updateTask(identifier, task)
            .pipe(catchError(error => {
                this.errorService.showErrorMessage(error);
                return [];
            })).subscribe(task => {
                this.shellService.setLoadingSpinner(false);
                this.messageService.add(
                    {
                        severity:'success',
                        summary: this.translateService.instant('taskUI.success'),
                        detail: this.translateService.instant('taskUI.task-details-task-updated')
                    }
                );
                this.task = task;
        });
    }
}
