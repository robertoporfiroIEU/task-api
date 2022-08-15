import { Component, OnDestroy, OnInit } from '@angular/core';
import { ShellService } from '../shell/shell.service';
import { UserProfileService } from '../user-profile.service';
import { Subject, takeUntil } from 'rxjs';
import { UserPrincipal } from '../shared/ModelsForUI';

@Component({
    selector: 'app-user-settings',
    templateUrl: './user-settings.container.html'
})
export class UserSettingsContainerComponent implements OnInit, OnDestroy {

    private destroy = new Subject<void>();
    userPrincipal: UserPrincipal | null = null;
    constructor(private shellService: ShellService, private userProfileService: UserProfileService) {}

    ngOnInit(): void {
        this.shellService.setLoadingSpinner(false);
        this.userProfileService.userProfile$
            .pipe(takeUntil(this.destroy))
            .subscribe(userPrincipal => this.userPrincipal = userPrincipal);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

}
