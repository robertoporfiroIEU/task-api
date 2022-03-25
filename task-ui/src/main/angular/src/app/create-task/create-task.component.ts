import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CreateTaskPresenter } from './create-task.presenter';
import { FormGroup } from '@angular/forms';
import { DropDown, Type } from '../shared/ModelsForUI';
import { Utils } from '../shared/Utils';
import { User, Task, ApplicationConfiguration } from '../api';
import { Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-create-task-ui',
    templateUrl: './create-task.component.html',
    styleUrls: ['./create-task.component.css'],
    providers: [CreateTaskPresenter]
})
export class CreateTaskComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    @Input() userProfile: User | null = null
    @Input() autoCompleteUsersData: string[] = [];
    @Input() autoCompleteGroupsData: string[] = [];
    @Input() configurations: ApplicationConfiguration[] | undefined = undefined;
    @Output() onAutocompleteUsersAssign = new EventEmitter<string>();
    @Output() onAutocompleteGroupsAssign = new EventEmitter<string>();
    @Output() onCreateTask = new EventEmitter<Task>();
    @Output() onConfigurations = new EventEmitter<string>();

    get taskForm(): FormGroup {
        return this.createTaskPresenter.taskForm;
    }

    datePipeDateFormat = Utils.datePipeDateFormat;
    pCalendarDateFormat = Utils.pCalendarDateFormat;
    TYPE = Type;

    constructor(private createTaskPresenter: CreateTaskPresenter) {}

    ngOnInit(): void {
        this.createTaskPresenter.init(this.userProfile);

        this.createTaskPresenter.onCreateTask$.pipe(
            takeUntil(this.destroy)
        ).subscribe(task => this.onCreateTask.emit(task))

        this.createTaskPresenter.onConfigurations$.pipe(
            takeUntil(this.destroy)
        ).subscribe(projectIdentifier => this.onConfigurations.emit(projectIdentifier));
    }

    autocompleteUsersAssign(event: any): void {
        this.onAutocompleteUsersAssign.emit(event.query);
    }

    autocompleteGroupsAssign(event: any): void {
        this.onAutocompleteGroupsAssign.emit(event.query);
    }

    submit(): void {
        this.createTaskPresenter.createTask();
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
