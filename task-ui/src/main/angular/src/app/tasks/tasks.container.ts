import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    PaginatedTasks,
    ProjectsService,
    TasksService
} from '../api';
import { ShellService } from '../shell/shell.service';
import { catchError, Observable, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { TasksParams } from './TasksParams';
import { ErrorService } from '../error.service';
import { ActivatedRoute,  Router } from '@angular/router';

@Component({
    selector: 'app-tasks',
    templateUrl: './tasks.container.html'
})
export class TasksContainerComponent implements OnInit, OnDestroy {

    private onLazyLoadPaginatedTasksSubject = new Subject<TasksParams>();
    private destroy: Subject<void> = new Subject();

    projectIdentifier: string | null = null;

    paginatedTasks$: Observable<PaginatedTasks> = this.onLazyLoadPaginatedTasksSubject.pipe(
        switchMap(tasksParams => this.tasksService.getTasks(
            tasksParams.pageable,
            tasksParams.identifier,
            tasksParams.projectIdentifier,
            tasksParams.name,
            tasksParams.status,
            tasksParams.priority,
            tasksParams.creationDateFrom,
            tasksParams.creationDateTo,
            tasksParams.createdBy,
            tasksParams.assignedTo,
            tasksParams.spectator,
            tasksParams.dueDateFrom,
            tasksParams.dueDateTo,
        )),
        tap( (t) => {
            this.shellService.setLoadingSpinner(false);
        }),
        catchError( error =>{
            this.errorService.showErrorMessage(error);
            return [];
        })
    )

    constructor(
        private tasksService: TasksService,
        private projectsService: ProjectsService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private errorService: ErrorService,
        private shellService: ShellService
    ) {}

    ngOnInit(): void {
        this.activatedRoute.queryParams.pipe(takeUntil(this.destroy)).subscribe(params => {
            if (params['projectIdentifier']) {
                this.projectIdentifier = params['projectIdentifier'];
            }
        });
        this.shellService.setFullScreenMode(false);
        this.shellService.setLoadingSpinner(true);
    }

    lazyLoadPaginatedTasks(taskParams: TasksParams): void {
        this.shellService.setLoadingSpinner(true);
        this.onLazyLoadPaginatedTasksSubject.next(taskParams);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
