import {Component, Input, OnInit} from '@angular/core';
import { ShellService } from '../shell.service';
import { ApplicationMenuItem } from "./ApplicationMenuItem";
import { Observable } from 'rxjs';

@Component({
    selector: 'app-dock',
    templateUrl: './dock.container.html'
})
export class DockContainerComponent implements OnInit {

   dockItemsState$: Observable<ApplicationMenuItem[]> = this.shellService.dockItemsState$;

    constructor(private shellService: ShellService) {}

    ngOnInit(): void {}

    setDockItemsState(dockItemsState: ApplicationMenuItem[]) {
        this.shellService.setDockItemsState(dockItemsState);
    }

    changeDockItem(item: ApplicationMenuItem) {
        this.shellService.setDockItemChanged(item);
    }

}
