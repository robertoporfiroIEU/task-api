import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PaginatedProjects } from '../api';
import { LazyLoadEvent } from 'primeng/api';
import { ProjectPresenter } from './project.presenter';
import { Subject, takeUntil } from 'rxjs';
import { ProjectsParams } from './ProjectsParams';
import { Utils } from '../shared/Utils';
import { FormGroup } from '@angular/forms';
import {Column} from '../shared/ModelsForUI';

@Component({
    selector: 'app-project-ui',
    templateUrl: './project.component.html',
    styleUrls: ['./project.component.css'],
    providers: [ProjectPresenter]
})
export class ProjectComponent implements OnInit {

    private destroy: Subject<void>  = new Subject();

    @Input() paginatedProjects: PaginatedProjects = {};
    @Input() isFullScreen: boolean = false;
    @Output() lazyLoadPaginatedProjects: EventEmitter<ProjectsParams> = new EventEmitter<ProjectsParams>();
    @Output() onFullScreenMode: EventEmitter<boolean> = new EventEmitter<boolean>();


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

    constructor(private projectPresenter: ProjectPresenter) {}

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

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
