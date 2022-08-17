import { Component, OnInit } from '@angular/core';
import { Project, ProjectsService } from '../api';
import { ShellService } from '../shell/shell.service';
import { UserProfileService } from '../user-profile.service';
import { catchError, Subject } from 'rxjs';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { RoutesEnum } from '../RoutesEnum';
import { ErrorService } from '../error.service';
import { Actions } from './Actions';

@Component({
    selector: 'app-create-project',
    templateUrl: './create-project.container.html',
})
export class CreateProjectContainerComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    action: Actions = Actions.CREATE;

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

    createProject(project: Project): void {
        this.shellService.setLoadingSpinner(true);
        this.projectsService.createProject(project)
            .pipe(catchError(error => {
                this.errorService.showErrorMessage(error);
                return [];
            }))
            .subscribe( () => {
            this.shellService.setLoadingSpinner(false);
            this.messageService.add(
                {
                    severity:'success',
                    summary: this.translateService.instant('taskUI.success'),
                    detail: this.translateService.instant('taskUI.create-project-success-message')
                }
            );
            this.router.navigate([RoutesEnum.projects])
        });
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
