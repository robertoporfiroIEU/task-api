import { Component, OnDestroy, OnInit } from '@angular/core';
import { GroupsService, TasksService, User, UsersService, Task } from '../api';
import { catchError, Subject, switchMap, takeUntil } from 'rxjs';
import { UserProfileService } from '../user-profile.service';
import { ShellService } from '../shell/shell.service';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { ErrorService } from '../error.service';
import { RoutesEnum } from '../RoutesEnum';
import { MessageService } from 'primeng/api';

@Component({
    selector: 'app-create-task',
    templateUrl: './create-task.container.html'
})
export class CreateTaskContainerComponent implements OnInit, OnDestroy {

    private destroy: Subject<void> = new Subject();
    userProfile: User | null = null
    autocompleteUsersSubject = new Subject<string>();
    autocompleteGroupsSubject = new Subject<string>();
    autoCompleteUsersData: string[] = [];
    autoCompleteGroupsData: string[] = [];

    constructor(
        private tasksService: TasksService,
        private usersService: UsersService,
        private groupsService: GroupsService,
        private userProfileService: UserProfileService,
        private shellService: ShellService,
        private translateService: TranslateService,
        private messageService: MessageService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private errorService: ErrorService
        ) {}

    ngOnInit(): void {
        this.shellService.setFullScreenMode(false);

        this.autocompleteUsersSubject.pipe(
            takeUntil(this.destroy),
            switchMap(query =>
                this.usersService.getUsers({}, query)
            )
        ).subscribe(paginatedUsers => {
            if (paginatedUsers.content) {
                this.autoCompleteUsersData = paginatedUsers.content.map(user =>
                    user.name
                )
            }
        });

        this.autocompleteGroupsSubject.pipe(
            takeUntil(this.destroy),
            switchMap(query =>
                this.groupsService.getGroups({}, query)
            )
        ).subscribe(paginatedGroups => {
            if (paginatedGroups.content) {
                this.autoCompleteGroupsData = paginatedGroups.content.map(group =>
                    group.name
                )
            }
        });

        this.userProfileService.userProfile$.pipe(
            takeUntil(this.destroy),
        ).subscribe( userProfile => this.userProfile = userProfile);
    }

    getAutoCompleteUsersAssign(query: string): void {
        this.autocompleteUsersSubject.next(query);
    }

    getAutoCompleteGroupsAssign(query: string): void {
        this.autocompleteGroupsSubject.next(query);
    }

    onCreateTask(task: Task) {
        this.shellService.setLoadingSpinner(true);
        this.tasksService.createTask(task)
            .pipe(catchError(error => {
                this.errorService.showErrorMessage(error);
                return [];
            }))
            .subscribe(() => {
                this.shellService.setLoadingSpinner(false);
                this.messageService.add(
                    {
                        severity:'success',
                        summary: this.translateService.instant('taskUI.success'),
                        detail: this.translateService.instant('taskUI.create-task-success-message')
                    }
                );
                let navigationExtras: NavigationExtras = {};

                navigationExtras = {
                    queryParams: { 'project-identifier': task.projectIdentifier }
                };
                this.router.navigate([RoutesEnum.tasks], navigationExtras);
            });
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
