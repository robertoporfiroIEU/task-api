import { Component, OnDestroy, OnInit } from '@angular/core';
import { PaginatedProjects, ProjectsService } from '../api';
import { catchError, Observable, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { ProjectsParams } from './ProjectsParams';
import { ShellService } from '../shell/shell.service';
import { ErrorService } from '../error.service';

@Component({
    selector: 'app-project',
    templateUrl: './projects.container.html'
})
export class ProjectContainerComponent implements OnInit, OnDestroy {

    private onLazyLoadPaginatedProjectsSubject = new Subject<ProjectsParams>();
    private destroy: Subject<void> = new Subject();
    isFullScreen: boolean = false;

    paginatedProjects$: Observable<PaginatedProjects> = this.onLazyLoadPaginatedProjectsSubject.pipe(
        switchMap(projectsParams => this.projectsService.getProjects(
                projectsParams.pageable,
                projectsParams.identifier,
                projectsParams.name,
                projectsParams.creationDateFrom,
                projectsParams.creationDateTo,
                projectsParams.createdBy
            )
        ),
        tap( (t) => {
            this.shellService.setLoadingSpinner(false);
        }),
        catchError( error =>{
            this.errorService.showErrorMessage(error);
            return [];
        })
    )

    constructor(
        private projectsService: ProjectsService,
        private shellService: ShellService,
        private errorService: ErrorService
    ) {}

    ngOnInit(): void {
        this.shellService.setLoadingSpinner(true);
        this.shellService.onFullScreenMode$.pipe(
            takeUntil(this.destroy)
        ).subscribe( state => {
            this.isFullScreen = state;
        });
    }

    lazyLoadPaginatedProjects(projectParams: ProjectsParams): void {
        this.shellService.setLoadingSpinner(true);
        this.onLazyLoadPaginatedProjectsSubject.next(projectParams);
    }

    fullScreenMode(state: boolean): void {
        this.shellService.setFullScreenMode(state);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
