import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { ApplicationMenuItem } from './dock/ApplicationMenuItem';

@Injectable({
  providedIn: 'root'
})
export class ShellService {
    private dockItemsStateSubject = new ReplaySubject<ApplicationMenuItem[]>(1);
    private dockItemChangedSubject = new ReplaySubject<ApplicationMenuItem>(1);

    dockItemsState$ = this.dockItemsStateSubject.asObservable();
    onDockItemChanged$ = this.dockItemChangedSubject.asObservable();

    setDockItemsState(dockItemsState: ApplicationMenuItem[]) {
        this.dockItemsStateSubject.next(dockItemsState);
    }

    setDockItemChanged(item: ApplicationMenuItem): void {
        this.dockItemChangedSubject.next(item);
    }
}
