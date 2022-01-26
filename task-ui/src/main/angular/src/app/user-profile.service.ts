import { Injectable } from '@angular/core';
import { User } from './api';
import { Observable, of } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UserProfileService {

    userProfile$: Observable<User> = new Observable<User>();

    constructor() {
        this.userProfile$ = of({
            name: 'rafail'
        })
    }
}
