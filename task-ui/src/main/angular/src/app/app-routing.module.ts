import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectContainerComponent } from './projects/projects.container';
import { RoutesEnum } from './RoutesEnum';
import { CreateProjectContainerComponent } from './create-project/create-project.container';

const routes: Routes = [
    { path: RoutesEnum.projects, component: ProjectContainerComponent },
    { path: RoutesEnum.empty, component: ProjectContainerComponent },
    { path: RoutesEnum.createProject, component: CreateProjectContainerComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
