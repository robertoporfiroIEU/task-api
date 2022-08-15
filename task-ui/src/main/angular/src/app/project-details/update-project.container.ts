import { Component, OnInit } from '@angular/core';
import { Project, ProjectsService } from '../api';
import { ShellService } from '../shell/shell.service';
import { catchError, Subject, switchMap, take } from 'rxjs';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error.service';
import { RoutesEnum } from '../RoutesEnum';
import { Actions } from './Actions';

@Component({
    selector: 'app-create-project',
    templateUrl: './update-project.container.html',
})
export class UpdateProjectContainerComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    action: Actions = Actions.UPDATE;
    project: Project | null = null;
    projectIdentifier: string = '';

    constructor(
        private projectsService: ProjectsService,
        private shellService: ShellService,
        private messageService: MessageService,
        private translateService: TranslateService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private errorService: ErrorService
    ) {}

    ngOnInit(): void {
        this.shellService.setFullScreenMode(false);

        this.activatedRoute.paramMap.pipe(
            switchMap(params => {
                this.projectIdentifier = params.get('project-identifier') as string
                return this.projectsService.getProject(this.projectIdentifier);
            }),
            take(1),
            catchError(err => {
                this.errorService.showErrorMessage(err);
                return [];
            })
        ).subscribe(project => {
            this.project = project;
        });
    }


    updateProject(project: Project): void {
        this.shellService.setLoadingSpinner(true);
        this.projectsService.updateProject(project)
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
                        detail: this.translateService.instant('taskUI.update-project-success-message')
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
