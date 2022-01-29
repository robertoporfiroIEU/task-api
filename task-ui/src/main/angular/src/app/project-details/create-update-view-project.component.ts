import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CreateUpdateViewProjectPresenter } from './create-update-view-project.presenter';
import { FormGroup } from '@angular/forms';
import { Project, User } from '../api';
import { Subject, takeUntil } from 'rxjs';
import { Actions } from './Actions';
import { Utils } from '../shared/Utils';

@Component({
    selector: 'app-create-project-ui',
    templateUrl: './create-update-view-project.component.html',
    styleUrls: ['./create-update-project.component.css'],
    providers: [CreateUpdateViewProjectPresenter]
})
export class CreateUpdateViewProjectComponent implements OnInit, OnDestroy {

    private destroy: Subject<void>  = new Subject();
    @Input() action: Actions = Actions.VIEW;
    @Input() userProfile: User | null = null;
    @Input() project: Project | null = null;
    @Output() onSubmit = new EventEmitter<Project>();

    datePipeDateFormat = Utils.datePipeDateFormat;
    actions = Actions;
    updateProjectURL: string = '/projects/update-project/';

    get projectForm(): FormGroup {
        return this.createProjectPresenter.projectForm;
    }

    constructor(private createProjectPresenter: CreateUpdateViewProjectPresenter) {}

    ngOnInit(): void {
        this.createProjectPresenter.init(this.project);
        this.createProjectPresenter.submitted$.pipe(
            takeUntil(this.destroy)
        ).subscribe(project => this.onSubmit.emit(project))
    }

    submit(): void {
        this.createProjectPresenter.submit(this.userProfile);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
