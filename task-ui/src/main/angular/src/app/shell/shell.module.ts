import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DockComponent } from './dock/dock.component';
import { ShellComponent } from './shell.component';
import { DockModule } from 'primeng/dock';
import { SharedModule } from '../shared/shared.module';
import { DockContainerComponent} from './dock/dock.container';
import { MenubarModule } from 'primeng/menubar';


@NgModule({
    declarations: [
        DockContainerComponent,
        DockComponent,
        ShellComponent
    ],
    imports: [
        CommonModule,
        DockModule,
        SharedModule,
        MenubarModule
    ],
    exports: [
        ShellComponent
    ],
})
export class ShellModule {
}
