import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { PaginatedTasks } from '../api';
import { TasksParams } from './TasksParams';
import { TasksPresenter } from './tasks.presenter';
import { LazyLoadEvent, SelectItem } from 'primeng/api';
import { FormGroup } from '@angular/forms';
import { Utils } from '../shared/Utils';
import { RoutesEnum } from '../RoutesEnum';
import { NavigationExtras, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { Roles } from '../shared/ModelsForUI';

@Component({
    selector: 'app-tasks-ui',
    templateUrl: './tasks.component.html',
    styleUrls: ['./tasks.component.css'],
    providers: [TasksPresenter]
})
export class TasksComponent implements OnInit {

    private destroy: Subject<void> = new Subject();

    @Input() paginatedTasks: PaginatedTasks = {};
    @Input() projectIdentifier: string | null = null;
    @Output() lazyLoadPaginatedTasks: EventEmitter<TasksParams> = new EventEmitter<TasksParams>();

    datePipeDateFormat = Utils.datePipeDateFormat;
    pCalendarDateFormat = Utils.pCalendarDateFormat;
    createTaskUrl: string = '';
    viewTask: string = '';

    get orderField(): SelectItem[] {
        return this.tasksPresenter.orderField;
    }

    get orderDirection(): SelectItem[] {
        return this.tasksPresenter.orderDirection;
    }

    get tasksFormCriteria(): FormGroup {
        return this.tasksPresenter.tasksFormCriteria;
    }

    constructor(private tasksPresenter: TasksPresenter, private router: Router, private authService: AuthService) {}

    ngOnInit(): void {
        this.createTaskUrl = '/' + RoutesEnum.createTask;
        this.viewTask = '/' + RoutesEnum.tasks;

        this.tasksPresenter.onLoadPaginatedTasks$.pipe(
            takeUntil(this.destroy)
        ).subscribe(taskParams => {
            this.lazyLoadPaginatedTasks.emit(taskParams);
        });
    }


    loadPaginatedTasks(event: LazyLoadEvent | null): void {
        this.tasksPresenter.loadPaginatedTasks(event, this.projectIdentifier);
    }

    clearFilters(): void {
        this.tasksPresenter.clearFilters();
    }

    createTaskOpenPage(): void {
        let navigationExtras: NavigationExtras = {};

        if (this.projectIdentifier) {
            navigationExtras = {
                queryParams: {'project-identifier': this.projectIdentifier}
            };
        }
        this.router.navigate([this.createTaskUrl], navigationExtras);
    }

    canUserCreateATask(): boolean {
        return this.authService.isUserRoleInRoles([
            Roles.LEADER_ROLE,
            Roles.PROJECT_MANAGER_ROLE,
            Roles.ADMIN_ROLE]
        )
    }
}
