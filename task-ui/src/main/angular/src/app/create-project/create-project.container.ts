import { Component, OnInit } from '@angular/core';
import { Project, ProjectsService, User } from '../api';
import { ShellService } from '../shell/shell.service';
import { UserProfileService } from '../user-profile.service';
import { Observable, Subject } from 'rxjs';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { RoutesEnum } from '../RoutesEnum';
import { ErrorService } from '../error.service';

@Component({
    selector: 'app-create-project',
    templateUrl: './create-project.container.html',
})
export class CreateProjectContainerComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    userProfile$: Observable<User> = this.userProfileService.userProfile$;

    constructor(
        private projectsService: ProjectsService,
        private shellService: ShellService,
        private userProfileService: UserProfileService,
        private messageService: MessageService,
        private translateService: TranslateService,
        private router: Router,
        private errorService: ErrorService
    ) {}

    ngOnInit(): void {
        this.shellService.setFullScreenMode(false);
    }

    onCreateProjectSubmitted(project: Project): void {
        this.shellService.setLoadingSpinner(true);
        this.projectsService.createProject(project).subscribe( () => {
            this.shellService.setLoadingSpinner(false);
            this.messageService.add(
                {
                    severity:'success',
                    summary: this.translateService.instant('taskUI.success'),
                    detail: this.translateService.instant('taskUI.create-project-success-message')
                }
            );
            this.router.navigate([RoutesEnum.projects])
        }, error => {
            this.errorService.showErrorMessage(error);
        });
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
