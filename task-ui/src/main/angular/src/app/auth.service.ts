import { Injectable } from '@angular/core';
import { map, Observable, of, switchMap } from 'rxjs';
import { UserProfileService } from './user-profile.service';
import { Group, User, UsersService } from './api';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    userProfile: User | null = null;

    constructor(private userProfileService: UserProfileService, private usersService: UsersService) {}

    init(): void {
        this.userProfileService
            .userProfile$
            .pipe(
                switchMap(user => {
                    this.userProfile = user;
                    /*
                       If the user logs in for the first time, then we need to register the current user into the API
                    */
                    let userNameFromLocalStorage = localStorage.getItem('user');
                    if ( userNameFromLocalStorage &&  userNameFromLocalStorage === user.name) {
                        return of(user);
                    }
                    return this.usersService.createUser(user);
                })
            )
            .subscribe(user => {
                localStorage.setItem('user', user.name);
            });
    }

    rolesAllowed(rolesAllowed: string[]): Observable<boolean> {
        return this.userProfileService.userProfile$.pipe(
            map( user => {
                if (!user?.groups?.length) {
                    return false;
                }
                return rolesAllowed.some(r => user?.groups?.map(g => g.name).includes(r))
            })
        )
    }

    isUserRoleInRoles(rolesAllowed: string[]): boolean {
        if (!this.userProfile) {
            return false;
        }

        if (!this.userProfile?.groups?.length) {
            return false;
        }

        return rolesAllowed.some(r => this.userProfile?.groups?.map((g: Group )=> g.name).includes(r))
    }

}
