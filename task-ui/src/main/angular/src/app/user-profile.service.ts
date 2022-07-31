import { Injectable } from '@angular/core';
import { User } from './api';
import { BehaviorSubject, Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class UserProfileService {

    userProfileSubject = new ReplaySubject<User>(1);
    userProfile$: Observable<User> = this.userProfileSubject.asObservable();

    constructor(private httpClient: HttpClient) {}

    init(): void {
        this.getUser().subscribe(user => this.userProfileSubject.next(user));
    }

    getUser(): Observable<User> {
        return this.httpClient.get<User>(environment.api.userDetails);
    }
}
