import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ApplicationMenuItem } from './ApplicationMenuItem';
import { DockPresenter } from './dock-presenter';
import { Observable, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-dock-ui',
    templateUrl: './dock.component.html',
    styleUrls: ['./dock.component.css'],
    providers: [DockPresenter]
})
export class DockComponent implements OnInit, OnDestroy {

    @Input() dockItemsState$: Observable<ApplicationMenuItem[]> = new Observable<ApplicationMenuItem[]>();
    @Output() onDockItemsState: EventEmitter<ApplicationMenuItem[]> = new EventEmitter<ApplicationMenuItem[]>();
    @Output() onChangeDockItem: EventEmitter<ApplicationMenuItem> = new EventEmitter<ApplicationMenuItem>();
    private destroy: Subject<void>  = new Subject();

    get dockItems(): ApplicationMenuItem[] {
        return this.dockPresenter.dockItems;
    }

    constructor(private dockPresenter: DockPresenter) {}

    ngOnInit(): void {

        this.dockItemsState$.pipe(
        ).subscribe( dockItemsState => {
            this.dockPresenter.init(dockItemsState)
        });

        this.dockPresenter.dockItemsStateHasChange$.pipe(
            takeUntil(this.destroy)
        ).subscribe( dockItemsState => this.onDockItemsState.emit(dockItemsState));
    }

    changeDockItem(item: ApplicationMenuItem): void {
        this.dockPresenter.changeDockItem(item);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
