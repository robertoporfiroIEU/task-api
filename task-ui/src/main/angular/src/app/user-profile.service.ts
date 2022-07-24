import { Injectable } from '@angular/core';
import { User } from './api';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class UserProfileService {

    userProfile$: Observable<User> = new Observable<User>();

    constructor(httpClient: HttpClient) {
        this.userProfile$ = of({
            name: 'rafail'
        })
    }
}
