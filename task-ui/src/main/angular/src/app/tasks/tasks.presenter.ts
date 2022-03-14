import { Injectable } from '@angular/core';
import { LazyLoadEvent, SelectItem } from 'primeng/api';
import { ProjectsParams } from '../projects/ProjectsParams';
import { Observable, Subject } from 'rxjs';
import { TasksParams } from './TasksParams';
import { FormControl, FormGroup } from '@angular/forms';
import { Utils } from '../shared/Utils';

@Injectable()
export class TasksPresenter {

    private loadPaginatedTasksSubject: Subject<ProjectsParams> = new Subject<TasksParams>();
    onLoadPaginatedTasks$: Observable<ProjectsParams> = this.loadPaginatedTasksSubject.asObservable();

    orderField: SelectItem[] = [
        {
            label: 'identifier',
            value: 'identifier'
        },
        {
            label: 'name',
            value: 'name'
        },
        {
            label: 'status',
            value: 'status'
        },
        {
            label: 'createdAt',
            value: 'createdAt'
        },
        {
            label: 'dueDate',
            value: 'dueDate'
        },
        {
            label: 'createdBy',
            value: 'createdBy.username'
        }
    ];

    orderDirection: SelectItem[] = [
        {
            label: 'asc',
            value: 'asc'
        },
        {
            label: 'desc',
            value: 'desc'
        }
    ]

    tasksFormCriteria: FormGroup = new FormGroup({
        identifier: new FormControl(null),
        name: new FormControl(null),
        status: new FormControl(null),
        creationDateFromTo: new FormControl(null),
        dueDateFromTo: new FormControl(null),
        createdBy: new FormControl(null),
        assignedTo: new FormControl(null),
        spectator: new FormControl(null),
        orderField: new FormControl('createdAt'),
        orderDirection: new FormControl('desc')
    })

    loadPaginatedTasks(event: LazyLoadEvent | null, projectIdentifier: string | null): void {
        let page: number = 0;
        let size: number = 10;
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

        let tasksParams: TasksParams = {
            pageable: {
                page: page,
                size: size,
                sort: sortField + ',' + sortOrder
            },
        }

        if (projectIdentifier) {
            tasksParams.projectIdentifier = projectIdentifier;
        }

        if (this.tasksFormCriteria.value.identifier) {
            tasksParams.identifier = this.tasksFormCriteria.value.identifier.trim();
        }

        if (this.tasksFormCriteria.value.name) {
            tasksParams.name = this.tasksFormCriteria.value.name.trim();
        }

        if (this.tasksFormCriteria.value.status) {
            tasksParams.status = this.tasksFormCriteria.value.status;
        }

        if (this.tasksFormCriteria.value.creationDateFromTo?.length > 1) {
            tasksParams.creationDateFrom = new Date(this.tasksFormCriteria.value.creationDateFromTo[0]).toISOString();
            if (this.tasksFormCriteria.value.creationDateFromTo[1]) {
                tasksParams.creationDateFrom = new Date(this.tasksFormCriteria.value.creationDateFromTo[1]).toISOString();
            }
        }

        if (this.tasksFormCriteria.value.dueDateFromTo?.length > 1) {
            tasksParams.dueDateFrom = new Date(this.tasksFormCriteria.value.dueDateFromTo[0]).toISOString();
            if (this.tasksFormCriteria.value.dueDateFromTo[1]) {
                tasksParams.dueDateTo = new Date(this.tasksFormCriteria.value.dueDateFromTo[1]).toISOString();
            }
        }

        if (this.tasksFormCriteria.value.createdBy) {
            tasksParams.createdBy = this.tasksFormCriteria.value.createdBy;
        }

        if (this.tasksFormCriteria.value.assignedTo) {
            tasksParams.assignedTo = this.tasksFormCriteria.value.assignedTo;
        }

        if (this.tasksFormCriteria.value.spectator) {
            tasksParams.spectator = this.tasksFormCriteria.value.spectator;
        }

        this.loadPaginatedTasksSubject.next(tasksParams);
    }

    clearFilters(): void {
        this.tasksFormCriteria.reset();
    }

    getTaskColor(taskName: string): string {
        return Utils.getColorFromStringValue(taskName, Utils.taskColors);
    }

}
