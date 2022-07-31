import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectContainerComponent } from './projects/projects.container';
import { RoutesEnum } from './RoutesEnum';
import { CreateProjectContainerComponent } from './project-details/create-project.container';
import { UpdateProjectContainerComponent } from './project-details/update-project.container';
import { TasksContainerComponent } from './tasks/tasks.container';
import { ViewProjectContainerComponent } from './project-details/view-project.container';
import { CreateTaskContainerComponent } from './create-task/create-task.container';
import { TaskDetailsContainerComponent } from './task-details/task-details.container';
import { RoleGuard } from './role.guard';
import { Roles } from './shared/ModelsForUI';
import { UnauthorisedComponent } from './unauthorised/unauthorised.component';

const routes: Routes = [
    {
        path: RoutesEnum.projects,
        component: ProjectContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.CONSULTATION_ROLE, Roles.DEVELOPER_ROLE, Roles.LEADER_ROLE, Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE] }
    },
    {
        path: RoutesEnum.createProject,
        component: CreateProjectContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE] }
    },
    {
        path: RoutesEnum.updateProject,
        component: UpdateProjectContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE] }
    },
    {
        path: RoutesEnum.viewProject,
        component: ViewProjectContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.CONSULTATION_ROLE, Roles.DEVELOPER_ROLE, Roles.LEADER_ROLE, Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE] }
    },
    {
        path: RoutesEnum.tasks,
        component: TasksContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.CONSULTATION_ROLE, Roles.DEVELOPER_ROLE, Roles.LEADER_ROLE, Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE]}
    },
    {
        path: RoutesEnum.createTask,
        component: CreateTaskContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.LEADER_ROLE, Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE]}
    },
    {
        path: RoutesEnum.viewTask,
        component: TaskDetailsContainerComponent,
        canActivate: [RoleGuard],
        data: { rolesAllowed: [Roles.CONSULTATION_ROLE, Roles.DEVELOPER_ROLE, Roles.LEADER_ROLE, Roles.PROJECT_MANAGER_ROLE, Roles.ADMIN_ROLE] }
    },
    {
        path: RoutesEnum.unauthorised,
        component: UnauthorisedComponent
    },
    {
        path: '**',
        redirectTo: RoutesEnum.projects,
        pathMatch: 'full'
    },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
