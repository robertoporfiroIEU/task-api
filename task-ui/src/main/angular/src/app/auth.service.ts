import { Injectable } from '@angular/core';
import { map, Observable, Subject } from 'rxjs';
import { UserProfileService } from './user-profile.service';
import { UserPrincipal } from './shared/ModelsForUI';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private userPrincipal: UserPrincipal | null = null;
    private successSubject = new Subject<void>();
    onSuccess$ = this.successSubject.asObservable();

    constructor(private userProfileService: UserProfileService, private httpClient: HttpClient) {}

    init(): void {
        this.userProfileService.userProfile$.subscribe(userPrincipal => this.userPrincipal = userPrincipal);
        this.getCsrfToken();
    }

    rolesAllowed(rolesAllowed: string[]): Observable<boolean> {
        return this.userProfileService.userProfile$.pipe(
            map( user => {
                if (!user?.roles?.length) {
                    return false;
                }
                return rolesAllowed.some(r => user?.roles?.includes(r))
            })
        )
    }

    isUserRoleInRoles(rolesAllowed: string[]): boolean {
        if (!this.userPrincipal) {
            return false;
        }

        if (!this.userPrincipal?.roles?.length) {
            return false;
        }

        return rolesAllowed.some(r => this.userPrincipal?.roles?.includes(r))
    }

    getCsrfToken(): void {
        this.httpClient.get(environment.api.csrf, {
            responseType: 'text'
        }).subscribe();
    }
}
