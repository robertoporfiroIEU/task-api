import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CreateUpdateProjectPresenter } from './create-update-project.presenter';
import { FormGroup } from '@angular/forms';
import { Project, User } from '../api';
import { Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-create-project-ui',
    templateUrl: './create-update-project.component.html',
    styleUrls: ['./create-update-project.component.css'],
    providers: [CreateUpdateProjectPresenter]
})
export class CreateUpdateProjectComponent implements OnInit, OnDestroy {

    private destroy: Subject<void>  = new Subject();
    @Input() userProfile: User | null = null;
    @Input() project: Project | null = null;
    @Output() onSubmit = new EventEmitter<Project>();

    get projectForm(): FormGroup {
        return this.createProjectPresenter.projectForm;
    }

    constructor(private createProjectPresenter: CreateUpdateProjectPresenter) {}

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
