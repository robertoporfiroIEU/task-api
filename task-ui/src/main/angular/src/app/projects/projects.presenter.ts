import { Injectable } from '@angular/core';
import { LazyLoadEvent } from 'primeng/api';
import { ProjectsParams } from './ProjectsParams';
import { fromEvent, Observable, Subject} from 'rxjs';
import { FormControl, FormGroup} from '@angular/forms';
import { Column } from '../shared/ModelsForUI';

@Injectable()
export class ProjectsPresenter {

    private loadPaginatedProjectsSubject: Subject<ProjectsParams> = new Subject<ProjectsParams>();
    onLoadPaginatedProjects$: Observable<ProjectsParams> = this.loadPaginatedProjectsSubject.asObservable();
    resizeObservable$: Observable<Event> = new Observable<Event>();

    responsiveLayout: string = '';

    columns: Column[] = [
        {
            field: 'identifier',
            header: 'taskUI.projects-header-identifier',
            placeholder: 'taskUI.filter-project-identifier-placeholder'
        },
        {
            field: 'name',
            header: 'taskUI.projects-header-name',
            placeholder: 'taskUI.filter-name-placeholder'
        },
        {
            field: 'createdAt',
            header: 'taskUI.projects-header-creation-date',
        },
        {
            field: 'createdBy',
            header: 'taskUI.projects-header-username',
            placeholder: 'taskUI.filter-username-placeholder'
        },
        {
            field: 'action',
            header: 'taskUI.projects-header-action'
        }
    ];

    projectsFormCriteria: FormGroup = new FormGroup({
        identifier: new FormControl(null),
        name: new FormControl(null),
        creationDateFromTo: new FormControl(new Date()),
        createdBy: new FormControl(null)
    })

    init(): void {
        this.resizeObservable$ = fromEvent(window, 'resize')
        this.resizeObservable$.subscribe( evt => {
            if (window.screen.width === 360) { // 768px portrait
                // scroll mode for the mobiles
                this.responsiveLayout = 'scroll';
            } else {
                this.responsiveLayout = ''
            }
        })
    }

    loadPaginatedProjects(event: LazyLoadEvent | null): void {
        let page: number = 0;
        let size: number = 25;
        let sortField: string = 'createdAt';
        let sortOrder: string = 'desc';

        if (event) {
            if (event.first !== undefined && event.rows !== undefined) {
                page = event?.first / event?.rows;
                size = event?.rows;
            }
            // Sorting criteria in the format -> property,asc|desc no space after comma!.
            sortField = event?.sortField ? event.sortField : 'createdAt';
            sortOrder = event?.sortOrder === -1 ? 'asc' : 'desc'
        }

        let formValues = this.projectsFormCriteria.value;

        let projectsParams: ProjectsParams = {
            pageable: {
                page: page,
                size: size,
                sort: sortField + ',' + sortOrder
            },
            identifier: formValues?.identifier ?  formValues.identifier.trim() : null,
            name: formValues?.name ? formValues.name.trim(): null,
            createdBy: formValues?.createdBy ? formValues.createdBy.trim(): null
        }

        if (formValues?.creationDateFromTo?.length > 1) {
            projectsParams.creationDateFrom = new Date(formValues?.creationDateFromTo[0]).toISOString();
            if (formValues?.creationDateFromTo[1]) {
                projectsParams.creationDateTo = new Date(formValues?.creationDateFromTo[1]).toISOString();
            }
        }


        this.loadPaginatedProjectsSubject.next(projectsParams);
    }

    resetFilters(): void {
        this.projectsFormCriteria.reset();
        let lazyLoadEvent: LazyLoadEvent = {};
        this.loadPaginatedProjects(lazyLoadEvent);
    }

}
