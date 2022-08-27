import { Component } from '@angular/core';
import { ApplicationMenuItem } from './shell/dock/ApplicationMenuItem';
import { TranslateService } from '@ngx-translate/core';
import { ShellService } from './shell/shell.service';
import { RoutesEnum } from './RoutesEnum';
import { NavigationEnd, Router } from '@angular/router';
import { UserProfileService } from './user-profile.service';
import { environment } from '../environments/environment';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
    dockItemsState: ApplicationMenuItem[] = [];
    username: string = '';

    constructor(
        private translateService: TranslateService,
        private router: Router,
        private shellService: ShellService,
        private userProfileService: UserProfileService,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.userProfileService.init();
        this.authService.init();

        this.userProfileService.userProfile$.subscribe(user =>{
            this.username = user?.name
            this.dockItemsState = [
                {
                    id: 'projects',
                    tooltipOptions: {
                        tooltipLabel: this.translateService.instant('taskUI.projects'),
                        tooltipPosition: 'top',
                        positionTop: -40,
                        positionLeft: 15
                    },
                    routerLink: RoutesEnum.projects,
                    fontAwesomeClass: "fas fa-project-diagram fa-2x text-dark",
                    isSelected: true
                },
                {
                    id: 'my-tasks',
                    tooltipOptions: {
                        tooltipLabel: this.translateService.instant('taskUI.tasks'),
                        tooltipPosition: 'top',
                        positionTop: -40,
                        positionLeft: 15
                    },
                    routerLink: RoutesEnum.tasks,
                    fontAwesomeClass: "fas fa-tasks fa-2x text-dark",
                    isSelected: false
                },
                {
                    id: 'userSettings',
                    tooltipOptions: {
                        tooltipLabel: this.translateService.instant('taskUI.user-settings'),
                        tooltipPosition: 'top',
                        positionTop: -40,
                        positionLeft: 15
                    },
                    routerLink: RoutesEnum.userSettings,
                    fontAwesomeClass: "fas fa-user-cog fa-2x text-dark",
                    isSelected: false
                }
            ];

            this.shellService.setLoadingSpinner(false);

            this.router.events.subscribe(
                (event: any) => {
                    if (event instanceof NavigationEnd) {
                        this.dockItemsState.forEach( dockItem => {
                            let url: string = this.router.url;
                            if (url === '/' && dockItem.routerLink === RoutesEnum.projects) {
                                dockItem.isSelected = true;
                            }
                            else if (url.includes(dockItem.routerLink)) {
                                dockItem.isSelected = true;
                            } else {
                                dockItem.isSelected = false;
                            }
                        })
                    }
                    this.shellService.setDockItemsState(this.dockItemsState);
                }
            );
        });
    }

    logout(): void {
        location.href = environment.api.logout + '?post_logout_redirect_uri=' + window.location.origin + window.location.pathname;
    }
}
