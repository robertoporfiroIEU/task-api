import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { Project, User } from '../api';

@Injectable()
export class CreateUpdateProjectPresenter {

    private submittedSubject = new Subject<Project>();
    submitted$ = this.submittedSubject.asObservable();

    projectForm: FormGroup = new FormGroup({
        name: new FormControl(null, [Validators.required]),
        prefixIdentifier: new FormControl(null, [Validators.required]),
        description: new FormControl(null),
    });

    init(project: Project | null): void {
        if (project) {
            this.projectForm.get('name')?.setValue(project.name);
            this.projectForm.get('prefixIdentifier')?.setValue(project.prefixIdentifier);
            this.projectForm.get('description')?.setValue(project.description);
        }
    }

    submit(userProfile: User | null = null): void {
        let project: Project = this.projectForm.value as Project
        project.createdBy = userProfile as User;
        this.submittedSubject.next(this.projectForm.value as Project);
    }

}
