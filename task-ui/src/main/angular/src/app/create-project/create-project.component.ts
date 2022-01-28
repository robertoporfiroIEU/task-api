import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CreateProjectPresenter } from './create-project.presenter';
import { FormGroup } from '@angular/forms';
import { Project, User } from '../api';
import { Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-create-project-ui',
    templateUrl: './create-project.component.html',
    styleUrls: ['./create-project.component.css'],
    providers: [CreateProjectPresenter]
})
export class CreateProjectComponent implements OnInit, OnDestroy {

    private destroy: Subject<void>  = new Subject();
    @Input() userProfile: User | null = null;
    @Output() onCreateProjectSubmitted = new EventEmitter<Project>();

    get createProjectForm(): FormGroup {
        return this.createProjectPresenter.createProjectForm;
    }

    constructor(private createProjectPresenter: CreateProjectPresenter) {}

    ngOnInit(): void {
        this.createProjectPresenter.onCreateProjectSubmitted$.pipe(
            takeUntil(this.destroy)
        ).subscribe(project => this.onCreateProjectSubmitted.emit(project))
    }

    createProjectSubmit(): void {
        this.createProjectPresenter.createProjectSubmit(this.userProfile);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
