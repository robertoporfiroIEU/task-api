import { Injectable } from '@angular/core';
import { ApplicationMenuItem } from './ApplicationMenuItem';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subject } from "rxjs";


@Injectable()
export class DockPresenter {

    private changeDockItemSubject: Subject<ApplicationMenuItem> = new Subject<ApplicationMenuItem>();
    private dockItemsStateSubject: Subject<ApplicationMenuItem[]> = new Subject<ApplicationMenuItem[]>();

    changeDockItem$: Observable<ApplicationMenuItem> = this.changeDockItemSubject.asObservable();
    dockItemsStateHasChange$: Observable<ApplicationMenuItem[]> = this.dockItemsStateSubject.asObservable();

    isExpanded: boolean = true;

    dockItems: ApplicationMenuItem[] = [];
    dockItemsWithoutMinimise: ApplicationMenuItem[] = [];

    constructor(private translateService: TranslateService) {}

    init(dockItemsState: ApplicationMenuItem[]): void {


        if (dockItemsState.find( i => i.id === 'expandDock')) {
            this.dockItems = [...dockItemsState];
        } else {
            this.dockItemsWithoutMinimise = [...dockItemsState];
            this.dockItems = [...dockItemsState];
            this.dockItems.push(
                {
                    id: 'minimiseDock',
                    tooltipOptions: {
                        tooltipLabel: this.translateService.instant('taskUI.minimise-dock'),
                        tooltipPosition: 'top',
                        positionTop: -40,
                        positionLeft: 15
                    },
                    fontAwesomeClass: "fas fa-caret-down fa-2x text-dark",
                    isSelected: false
                },
            );
        }

    }

    changeDockItem(item: ApplicationMenuItem): void {
        if (item.id === 'minimiseDock') {
            this.dockItems = [
                {
                    id: 'expandDock',
                    tooltipOptions: {
                        tooltipLabel: this.translateService.instant('taskUI.expand-dock'),
                        tooltipPosition: 'top',
                        positionTop: -40,
                        positionLeft: 15
                    },
                    fontAwesomeClass: "fas fa-bars fa-2x text-dark",
                    isSelected: false
                }
            ]
            this.dockItemsStateSubject.next(this.dockItems);
            return;
        }

        if (item.id === 'expandDock') {
            this.dockItemsStateSubject.next([...this.dockItemsWithoutMinimise]);
            return;
        }

        // reset all
        this.dockItemsWithoutMinimise.forEach( i => i.isSelected = false);

        // find the item in the array and make the selection true
        let index: number = this.dockItemsWithoutMinimise.map( i => i.id).indexOf(item.id);
        this.dockItemsWithoutMinimise[index].isSelected = true;

        this.dockItemsStateSubject.next([...this.dockItemsWithoutMinimise]);
        this.changeDockItemSubject.next(item);
    }

}
