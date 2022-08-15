import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CreateTaskPresenter } from './create-task.presenter';
import { FormGroup } from '@angular/forms';
import { DropDown, Type, UserPrincipal } from '../shared/ModelsForUI';
import { Utils } from '../shared/Utils';
import { User, Task, ApplicationConfiguration } from '../api';
import { Subject, takeUntil } from 'rxjs';
import { Message } from 'primeng/api';

@Component({
    selector: 'app-create-task-ui',
    templateUrl: './create-task.component.html',
    styleUrls: ['./create-task.component.css'],
    providers: [CreateTaskPresenter]
})
export class CreateTaskComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    @Input() userPrincipal: UserPrincipal | null = null
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

    get projectIdentifierIsMandatoryMessages(): Message[] {
        return this.createTaskPresenter.projectIdentifierIsMandatoryMessages;
    }

    set projectIdentifierIsMandatoryMessages(messages: Message[]) {
        this.createTaskPresenter.projectIdentifierIsMandatoryMessages = messages;
    }

    datePipeDateFormat = Utils.datePipeDateFormat;
    pCalendarDateFormat = Utils.pCalendarDateFormat;
    TYPE = Type;

    constructor(private createTaskPresenter: CreateTaskPresenter) {}

    ngOnInit(): void {
        this.createTaskPresenter.init(this.userPrincipal!);

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
