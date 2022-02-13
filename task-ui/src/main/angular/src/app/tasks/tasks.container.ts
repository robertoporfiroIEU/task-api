import { Component, OnDestroy, OnInit } from '@angular/core';
import { PaginatedTasks, TasksService } from '../api';
import { ShellService } from '../shell/shell.service';
import { catchError, map, Observable, Subject, switchMap, takeUntil, tap } from 'rxjs';
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

    projectIdentifier$: Observable<string> = this.activatedRoute.queryParams.pipe(
        takeUntil(this.destroy),
        map( params => {
            if (params['projectIdentifier']) {
                return params['projectIdentifier'];
            }
            return null;
        })
    )

    projectName: string | null  = '';

    paginatedTasks$: Observable<PaginatedTasks> = this.onLazyLoadPaginatedTasksSubject.pipe(
        switchMap(tasksParams => this.tasksService.getTasks(
            tasksParams.pageable,
            tasksParams.identifier,
            tasksParams.projectIdentifier,
            tasksParams.name,
            tasksParams.status,
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
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private errorService: ErrorService,
        private shellService: ShellService
    ) {}

    ngOnInit(): void {
        this.shellService.setFullScreenMode(false);
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
