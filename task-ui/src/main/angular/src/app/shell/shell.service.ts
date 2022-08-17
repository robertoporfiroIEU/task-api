import { Injectable } from '@angular/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { ApplicationMenuItem } from './dock/ApplicationMenuItem';

@Injectable({
  providedIn: 'root'
})
export class ShellService {
    private dockItemsStateSubject = new ReplaySubject<ApplicationMenuItem[]>(1);
    private dockItemChangedSubject = new ReplaySubject<ApplicationMenuItem>(1);
    private fullScreenModeSubject = new ReplaySubject<boolean>(1);
    private loadingSpinnerSubject = new Subject<boolean>();

    dockItemsState$: Observable<ApplicationMenuItem[]> = this.dockItemsStateSubject.asObservable();
    onFullScreenMode$ = this.fullScreenModeSubject.asObservable();
    onLoadingSpinner$ = this.loadingSpinnerSubject.asObservable();

    setDockItemsState(dockItemsState: ApplicationMenuItem[]) {
        this.dockItemsStateSubject.next(dockItemsState);
    }

    setFullScreenMode(state: boolean): void {
        this.fullScreenModeSubject.next(state);
    }

    setLoadingSpinner(state: boolean): void {
        this.loadingSpinnerSubject.next(state);
    }
}
