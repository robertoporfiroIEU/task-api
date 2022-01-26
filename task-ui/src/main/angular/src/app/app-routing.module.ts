import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProjectContainerComponent} from './project/project.container';

const routes: Routes = [
    { path: 'projects', component: ProjectContainerComponent },
    { path: '', component: ProjectContainerComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
