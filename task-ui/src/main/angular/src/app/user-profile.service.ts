import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { UserPrincipal } from './shared/ModelsForUI';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class UserProfileService {

    userProfileSubject = new ReplaySubject<UserPrincipal>(1);
    userProfile$: Observable<UserPrincipal> = this.userProfileSubject.asObservable();

    constructor(private httpClient: HttpClient, private translationService: TranslateService) {}

    init(): void {
        this.getUser().subscribe(user => {
            let language = this.translationService.currentLang;
            user.language = language;
            this.userProfileSubject.next(user);
        });
    }

    getUser(): Observable<UserPrincipal> {
        return this.httpClient.get<UserPrincipal>(environment.api.userDetails);
    }
}
