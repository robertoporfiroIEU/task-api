import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PaginatedProjects } from '../api';
import { LazyLoadEvent } from 'primeng/api';
import { ProjectsPresenter } from './projects.presenter';
import { Subject, takeUntil } from 'rxjs';
import { ProjectsParams } from './ProjectsParams';
import { Utils } from '../shared/Utils';
import { FormGroup } from '@angular/forms';
import { Column, Roles } from '../shared/ModelsForUI';
import { RoutesEnum } from '../RoutesEnum';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-project-ui',
    templateUrl: './projects.component.html',
    styleUrls: ['./projects.component.css'],
    providers: [ProjectsPresenter]
})
export class ProjectsComponent implements OnInit {

    private destroy: Subject<void>  = new Subject();

    @Input() paginatedProjects: PaginatedProjects = {};
    @Input() isFullScreen: boolean = false;
    @Output() lazyLoadPaginatedProjects: EventEmitter<ProjectsParams> = new EventEmitter<ProjectsParams>();
    @Output() onFullScreenMode: EventEmitter<boolean> = new EventEmitter<boolean>();

    createProjectURL: string = '/' + RoutesEnum.createProject.toString();
    viewProjectURL: string = '/projects/view-project/';
    updateProjectURL: string = '/projects/update-project/';
    creationDate: Date = new Date();
    pCalendarDateFormat = Utils.pCalendarDateFormat;
    datePipeDateFormat = Utils.datePipeDateFormat;

   get columns(): Column[] {
       return this.projectPresenter.columns;
   }

    get projectsFormCriteria(): FormGroup {
        return this.projectPresenter.projectsFormCriteria;
    }

    get responsiveLayout(): string {
       return this.projectPresenter.responsiveLayout;
    }

    constructor(private projectPresenter: ProjectsPresenter, private authService: AuthService) {}

    ngOnInit(): void {
        this.projectPresenter.init();

        this.projectPresenter.onLoadPaginatedProjects$.pipe(
            takeUntil(this.destroy)
        ).subscribe( projectsParams => {
            this.lazyLoadPaginatedProjects.emit(projectsParams);
        });
    }

    loadPaginatedProjects(event: LazyLoadEvent | null): void {
        this.projectPresenter.loadPaginatedProjects(event);
    }

    fullScreenMode(state: boolean): void {
        this.onFullScreenMode.emit(state);
    }

    resetFilters(): void {
       this.projectPresenter.resetFilters();
    }

    canUserCreateOrUpdateProject(): boolean {
       return this.authService.isUserRoleInRoles([Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE])
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
