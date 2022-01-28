import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { Project, User } from '../api';

@Injectable()
export class CreateProjectPresenter {

    private createProjectSubmittedSubject = new Subject<Project>();
    onCreateProjectSubmitted$ = this.createProjectSubmittedSubject.asObservable();

    createProjectForm: FormGroup = new FormGroup({
        name: new FormControl(null, [Validators.required]),
        prefixIdentifier: new FormControl(null, [Validators.required]),
        description: new FormControl(null),
    });

    createProjectSubmit(userProfile: User | null = null): void {
        let project: Project = this.createProjectForm.value as Project
        project.createdBy = userProfile as User;
        this.createProjectSubmittedSubject.next(this.createProjectForm.value as Project);
    }

}
