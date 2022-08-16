import { Injectable } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { ProjectConfiguration, Project } from '../api';
import * as _ from 'lodash';

@Injectable()
export class CreateUpdateViewProjectPresenter {

    private submittedSubject = new Subject<Project>();
    submitted$ = this.submittedSubject.asObservable();

    projectForm: FormGroup = new FormGroup({
        name: new FormControl(null, [Validators.required]),
        prefixIdentifier: new FormControl(null, [Validators.required]),
        description: new FormControl(null),
        statuses: new FormArray( []),
        priorities: new FormArray( []),
    });

    project: Project | null = null;

    init(project: Project | null): void {
        if (project) {
            this.projectForm.get('name')?.setValue(project.name);
            this.projectForm.get('prefixIdentifier')?.setValue(project.prefixIdentifier);
            this.projectForm.get('description')?.setValue(project.description);
            if (project?.configurations && project?.configurations?.length) {
                project?.configurations.forEach(c => {
                    let configuration: FormGroup = new FormGroup(({
                        configurationName: new FormControl(c.configurationName),
                        configurationValue: new FormControl(c.configurationValue, [Validators.required]),
                        color: new FormControl(c.color),
                        weight: new FormControl(c.weight)
                    }));
                    if (c.configurationName === 'status') {
                        (this.projectForm.get('statuses') as FormArray).push(configuration);
                    } else {
                        (this.projectForm.get('priorities') as FormArray).push(configuration);
                    }

                })
            }

        }
        this.project = _.clone(project);
    }

    submit(): void {
        let project: Project;

        // map to ProjectConfiguration
        let statuses: ProjectConfiguration[] = (this.projectForm.get('statuses') as FormArray).controls
            .map(g => { return {
                    configurationName: g.value.configurationName,
                    configurationValue: g.value.configurationValue,
                    color: g.value.color,
                    weight: g.value.weight
            }});


        let priorities: ProjectConfiguration[] = (this.projectForm.get('priorities') as FormArray).controls
            .map(g => { return {
                configurationName: g.value.configurationName,
                configurationValue: g.value.configurationValue,
                color: g.value.color,
                weight: g.value.weight
            }});

        // fix the weights of the configuration because may was reordered
        if (statuses.length > 0) {
            for(let i = 0; i < statuses.length; i++) {
                statuses[i].weight = i;
            }
        }

        if (priorities.length > 0) {
            for(let i = 0; i < priorities.length; i++) {
                priorities[i].weight = i;
            }
        }

        project = {
            identifier: this.project?.identifier,
            name: this.projectForm.value.name,
            prefixIdentifier: this.projectForm.value.prefixIdentifier,
            description: this.projectForm.value.description,
            configurations: [...statuses, ...priorities]
        };

        this.submittedSubject.next(project);
    }

    addStatus(): void {
        let statuses: FormArray = this.projectForm.get('statuses') as FormArray;
        let weight: number = statuses.length;
        statuses.push(
            new FormGroup({
                configurationName: new FormControl('status'),
                configurationValue: new FormControl('', [Validators.required]),
                color: new FormControl('#0dff00'),
                weight: new FormControl(weight++)
            })
        );
    }

    addPriority(): void {
        let priorities: FormArray = this.projectForm.get('priorities') as FormArray;
        let weight: number = priorities.length;
        priorities.push(
            new FormGroup({
                configurationName: new FormControl('priority'),
                configurationValue: new FormControl('', [Validators.required]),
                color: new FormControl('#0dff00'),
                weight: new FormControl(weight++)
            })
        );
    }

    deleteStatus(weight: number) {
        let statuses: FormArray = this.projectForm.get('statuses') as FormArray;
        let index = statuses.controls.findIndex(c => c.value.weight === weight);
        return statuses.removeAt(index);
    }

    deletePriority(weight: number) {
        let priorities: FormArray = this.projectForm.get('priorities') as FormArray;
        let index = priorities.controls.findIndex(c => c.value.weight === weight);
        return priorities.removeAt(index);
    }

}
