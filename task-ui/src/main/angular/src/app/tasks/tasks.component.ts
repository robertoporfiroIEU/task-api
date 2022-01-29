import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { PaginatedTasks, Task } from '../api';
import { TasksParams } from './TasksParams';
import { TasksPresenter } from './tasks.presenter';
import { LazyLoadEvent, SelectItem } from 'primeng/api';
import { FormGroup } from '@angular/forms';
import { Utils } from '../shared/Utils';

@Component({
    selector: 'app-tasks-ui',
    templateUrl: './tasks.component.html',
    styleUrls: ['./tasks.component.css'],
    providers: [TasksPresenter]
})
export class TasksComponent implements OnInit {

    private destroy: Subject<void>  = new Subject();

    @Input() paginatedTasks: PaginatedTasks = {};
    @Input() projectIdentifier: string | null = null;
    @Output() lazyLoadPaginatedTasks: EventEmitter<TasksParams> = new EventEmitter<TasksParams>();
    datePipeDateFormat = Utils.datePipeDateFormat;
    pCalendarDateFormat = Utils.pCalendarDateFormat;

    get orderField(): SelectItem[]  {
        return this.tasksPresenter.orderField;
    }

    get orderDirection(): SelectItem[]  {
        return this.tasksPresenter.orderDirection;
    }

    get tasksFormCriteria(): FormGroup {
        return this.tasksPresenter.tasksFormCriteria;
    }

    get avatarColors(): string[] {
        return this.tasksPresenter.avatarColors;
    }

    constructor(private tasksPresenter: TasksPresenter) {
    }

    ngOnInit(): void {
        this.tasksPresenter.onLoadPaginatedTasks$.pipe(
            takeUntil(this.destroy)
        ).subscribe( taskParams => {
            this.lazyLoadPaginatedTasks.emit(taskParams);
        });
    }


    loadPaginatedTasks(event: LazyLoadEvent | null): void {
        this.tasksPresenter.loadPaginatedTasks(event, this.projectIdentifier);
    }

    clearFilters(): void {
        this.tasksPresenter.clearFilters();
    }

    flatAssigns(task: Task): string[] {
       return this.tasksPresenter.flatAssigns(task);
    }

    flatSpectators(task: Task): string[] {
        return this.tasksPresenter.flatSpectators(task);
    }

}
