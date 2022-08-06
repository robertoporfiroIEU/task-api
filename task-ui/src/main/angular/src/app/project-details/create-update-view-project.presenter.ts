import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { Project } from '../api';
import * as _ from 'lodash';

@Injectable()
export class CreateUpdateViewProjectPresenter {

    private submittedSubject = new Subject<Project>();
    submitted$ = this.submittedSubject.asObservable();

    projectForm: FormGroup = new FormGroup({
        name: new FormControl(null, [Validators.required]),
        prefixIdentifier: new FormControl(null, [Validators.required]),
        description: new FormControl(null),
    });

    project: Project | null = null;

    init(project: Project | null): void {
        if (project) {
            this.projectForm.get('name')?.setValue(project.name);
            this.projectForm.get('prefixIdentifier')?.setValue(project.prefixIdentifier);
            this.projectForm.get('description')?.setValue(project.description);
        }
        this.project = _.clone(project);
    }

    submit(): void {
        let project: Project = this.projectForm.value as Project;
        if (this.project) {
            project = _.clone(this.project);
        }

        project.name = this.projectForm.value.name;
        project.prefixIdentifier = this.projectForm.value.prefixIdentifier;
        project.description = this.projectForm.value.description;

        // when we set an empty array, then the system will provide the default configurations
        project.configurations = [];
        this.submittedSubject.next(project);
    }

}
