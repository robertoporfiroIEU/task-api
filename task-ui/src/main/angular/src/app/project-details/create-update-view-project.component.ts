import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CreateUpdateViewProjectPresenter } from './create-update-view-project.presenter';
import { FormArray, FormGroup } from '@angular/forms';
import { Project } from '../api';
import { Subject, takeUntil } from 'rxjs';
import { Actions } from './Actions';
import { Utils } from '../shared/Utils';
import { AuthService } from '../auth.service';
import { Roles } from '../shared/ModelsForUI';

@Component({
    selector: 'app-create-project-ui',
    templateUrl: './create-update-view-project.component.html',
    styleUrls: ['./create-update-project.component.css'],
    providers: [CreateUpdateViewProjectPresenter]
})
export class CreateUpdateViewProjectComponent implements OnInit, OnDestroy {

    private destroy: Subject<void>  = new Subject();
    @Input() action: Actions = Actions.VIEW;
    @Input() project: Project | null = null;
    @Output() onSubmit = new EventEmitter<Project>();

    datePipeDateFormat = Utils.datePipeDateFormat;
    actions = Actions;
    updateProjectURL: string = '/projects/update-project/';

    get projectForm(): FormGroup {
        return this.createProjectPresenter.projectForm;
    }

    get statusesFormArray(): FormArray {
        return this.createProjectPresenter.projectForm.get('statuses') as FormArray;
    }

    get prioritiesFormArray(): FormArray {
        return this.createProjectPresenter.projectForm.get('priorities') as FormArray;
    }

    constructor(private createProjectPresenter: CreateUpdateViewProjectPresenter, private authService: AuthService) {}

    ngOnInit(): void {
        this.createProjectPresenter.init(this.project);
        this.createProjectPresenter.submitted$.pipe(
            takeUntil(this.destroy)
        ).subscribe(project => this.onSubmit.emit(project))
    }

    submit(): void {
        this.createProjectPresenter.submit();
    }

    canUserEditTheProject(): boolean {
        return this.authService.isUserRoleInRoles([Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE])
    }

    addStatus(): void {
        this.createProjectPresenter.addStatus();
    }

    addPriority(): void {
        this.createProjectPresenter.addPriority();
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

    deleteStatus(weight: number): void {
        this.createProjectPresenter.deleteStatus(weight);
    }

    deletePriority(weight: number): void {
        this.createProjectPresenter.deletePriority(weight);
    }
}
