import { Component, OnInit } from '@angular/core';
import { Project, ProjectsService, User } from '../api';
import { ShellService } from '../shell/shell.service';
import { UserProfileService } from '../user-profile.service';
import { catchError, Subject, switchMap, take } from 'rxjs';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error.service';
import { Actions } from './Actions';

@Component({
    selector: 'app-view-project',
    templateUrl: './view-project.container.html',
})
export class ViewProjectContainerComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    action: Actions = Actions.VIEW;
    userProfile: User | null = null;
    project: Project | null = null;

    constructor(
        private projectsService: ProjectsService,
        private shellService: ShellService,
        private userProfileService: UserProfileService,
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
                let projectIdentifier = params.get('project-identifier') as string
                return this.projectsService.getProject(projectIdentifier);
            }),
            take(1),
            catchError(err => {
                this.errorService.showErrorMessage(err);
                return [];
            })
        ).subscribe(project => {
            this.project = project;
        });

        this.userProfileService.userProfile$.pipe(
            take(1)
        ).subscribe(user => this.userProfile = user)
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
