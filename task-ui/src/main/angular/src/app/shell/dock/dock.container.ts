import { Component, OnInit } from '@angular/core';
import { ShellService } from '../shell.service';
import { ApplicationMenuItem } from './ApplicationMenuItem';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-dock',
    templateUrl: './dock.container.html'
})
export class DockContainerComponent implements OnInit {

    dockItemsState$: Observable<ApplicationMenuItem[]> = this.shellService.dockItemsState$;

    constructor(private shellService: ShellService) {}

    ngOnInit(): void {}

    setDockItemsState(dockItemsState: ApplicationMenuItem[]): void {
        this.shellService.setDockItemsState(dockItemsState);
    }

}
