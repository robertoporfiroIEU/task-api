import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectContainerComponent } from './projects/projects.container';
import { RoutesEnum } from './RoutesEnum';
import { CreateProjectContainerComponent } from './project-details/create-project.container';
import { UpdateProjectContainerComponent } from './project-details/update-project.container';
import { TasksContainerComponent } from './tasks/tasks.container';
import { ViewProjectContainerComponent } from './project-details/view-project.container';

const routes: Routes = [
    { path: RoutesEnum.projects, component: ProjectContainerComponent },
    { path: RoutesEnum.empty, component: ProjectContainerComponent },
    { path: RoutesEnum.createProject, component: CreateProjectContainerComponent },
    { path: RoutesEnum.updateProject, component: UpdateProjectContainerComponent },
    { path: RoutesEnum.viewProject, component: ViewProjectContainerComponent },
    { path: RoutesEnum.tasks, component: TasksContainerComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
