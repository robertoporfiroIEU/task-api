import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    GroupsService,
    TasksService,
    User,
    UsersService,
    Task,
    ApplicationConfiguration,
    ProjectsService, ApplicationConfigurationsService
} from '../api';
import { catchError, Subject, switchMap, takeUntil } from 'rxjs';
import { UserProfileService } from '../user-profile.service';
import { ShellService } from '../shell/shell.service';
import { TranslateService } from '@ngx-translate/core';
import { NavigationExtras, Router } from '@angular/router';
import { ErrorService } from '../error.service';
import { RoutesEnum } from '../RoutesEnum';
import { MessageService } from 'primeng/api';

@Component({
    selector: 'app-create-task',
    templateUrl: './create-task.container.html'
})
export class CreateTaskContainerComponent implements OnInit, OnDestroy {

    private destroy: Subject<void> = new Subject();
    private autocompleteUsersSubject = new Subject<string>();
    private autocompleteGroupsSubject = new Subject<string>();
    userProfile: User | null = null
    autoCompleteUsersData: string[] = [];
    autoCompleteGroupsData: string[] = [];
    configurations: ApplicationConfiguration[] | undefined = undefined;

    constructor(
        private tasksService: TasksService,
        private projectsService: ProjectsService,
        private applicationConfigurationsService: ApplicationConfigurationsService,
        private usersService: UsersService,
        private groupsService: GroupsService,
        private userProfileService: UserProfileService,
        private shellService: ShellService,
        private translateService: TranslateService,
        private messageService: MessageService,
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
                    queryParams: { 'projectIdentifier': task.projectIdentifier }
                };
                this.router.navigate([RoutesEnum.tasks], navigationExtras);
            });
    }

    getConfigurations(projectIdentifier: string): void {
        if (projectIdentifier) {
            this.projectsService.getProject(projectIdentifier).pipe(
                takeUntil(this.destroy)
            ).subscribe(project => this.configurations = project.configurations);
        } else {
            this.applicationConfigurationsService.getApplicationConfigurations().pipe(
                takeUntil(this.destroy)
            ).subscribe(configurations => this.configurations = configurations);
        }
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
