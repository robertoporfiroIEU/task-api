import {
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild
} from '@angular/core';
import { ApplicationConfiguration, PaginatedComments, Task, User } from '../api';
import { TaskDetailsPresenter } from './task-details.presenter';
import { Utils } from '../shared/Utils';
import { FormGroup } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Roles, Type } from '../shared/ModelsForUI';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-task-details-ui',
    templateUrl: './task-details.component.html',
    styleUrls: ['./task-details.component.css'],
    providers: [TaskDetailsPresenter]
})
export class TaskDetailsComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    @Input() task: Task | null = null;
    @Input() comments: PaginatedComments | null = null;
    @Input() userProfile: User | null = null
    @Input() applicationConfigurations: ApplicationConfiguration[] | undefined = undefined;
    @Output() onUpdateTask = new EventEmitter<Task>();
    @ViewChild('nameInput', {static: false}) nameInput: ElementRef<any> | null = null;

    viewProjectURL: string = '/projects/view-project/';
    datePipeDateFormat = Utils.datePipeDateFormat;
    datePipeDateTimeFormat = Utils.datePipeDateTimeFormat;
    pCalendarDateFormat = Utils.pCalendarDateFormat;

    get taskForm(): FormGroup {
        return this.taskDetailsPresenter.taskForm;
    }

    get isTaskNameReadOnly(): boolean {
        return this.taskDetailsPresenter.isTaskNameReadOnly;
    }

    get isDescriptionEditable(): boolean {
        return this.taskDetailsPresenter.isDescriptionEditable;
    }

    get isDescriptionReadOnly(): boolean {
        return this.taskDetailsPresenter.isDescriptionReadOnly;
    }

    get isStatusEditable(): boolean {
        return this.taskDetailsPresenter.isStatusEditable;
    }

    get isStatusReadOnly(): boolean {
        return this.taskDetailsPresenter.isStatusReadOnly;
    }

    get isPriorityEditable(): boolean {
        return this.taskDetailsPresenter.isPriorityEditable;
    }

    get isPriorityReadOnly(): boolean {
        return this.taskDetailsPresenter.isPriorityReadOnly;
    }

    get isAssignEditable(): boolean {
        return this.taskDetailsPresenter.isAssignEditable;
    }

    get isAssignReadOnly(): boolean {
        return this.taskDetailsPresenter.isAssignReadOnly;
    }

    get isSpectatorEditable(): boolean {
        return this.taskDetailsPresenter.isSpectatorEditable;
    }

    get isSpectatorReadOnly(): boolean {
        return this.taskDetailsPresenter.isSpectatorReadOnly;
    }

    get isDueDateEditable(): boolean {
        return this.taskDetailsPresenter.isDueDateEditable;
    }

    get isDueDateReadOnly(): boolean {
        return this.taskDetailsPresenter.isDueDateReadOnly;
    }

    TYPE = Type;

    constructor(private taskDetailsPresenter: TaskDetailsPresenter, private authService: AuthService) {}

    ngOnInit(): void {
        this.taskDetailsPresenter.init(this.task);
        this.taskDetailsPresenter.onUpdateTask.pipe(
            takeUntil(this.destroy)
        ).subscribe(task => this.onUpdateTask.emit(task));
    }

    updateTask(): void {
        this.taskDetailsPresenter.updateTask(this.task!);
    }

    setDescriptionState(state: boolean): void {
        this.taskDetailsPresenter.setDescriptionState(state);
    }

    changeAssignState(): void {
        this.taskDetailsPresenter.changeAssignState();
    }

    changeSpectatorState(): void {
        this.taskDetailsPresenter.changeSpectatorState();
    }

    changeStatusState(): void {
        this.taskDetailsPresenter.changeStatusState();
    }

    changePriorityState(): void {
        this.taskDetailsPresenter.changePriorityState();
    }

    changeDueDateState(): void {
        this.taskDetailsPresenter.changeDueDateState();
    }

    cancelDescription(): void {
        this.taskDetailsPresenter.cancelDescription();
    }

    canUserUpdateTheTask(): boolean {
        return this.authService.isUserRoleInRoles([
            Roles.DEVELOPER_ROLE,
            Roles.LEADER_ROLE,
            Roles.PROJECT_MANAGER_ROLE,
            Roles.ADMIN_ROLE]
        )
    }
}
