import { Component } from '@angular/core';
import { ApplicationMenuItem } from './shell/dock/ApplicationMenuItem';
import { TranslateService } from '@ngx-translate/core';
import { ShellService } from './shell/shell.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
    dockItemsState: ApplicationMenuItem[] = [
        {
            id: 'projects',
            tooltipOptions: {
                tooltipLabel: this.translateService.instant('taskUI.projects'),
                tooltipPosition: 'top',
                positionTop: -40,
                positionLeft: 15
            },
            routerLink: 'project',
            fontAwesomeClass: "fas fa-project-diagram fa-2x text-dark",
            isSelected: true
        },
        {
            id: 'createTask',
            tooltipOptions: {
                tooltipLabel: this.translateService.instant('taskUI.tasks'),
                tooltipPosition: 'top',
                positionTop: -40,
                positionLeft: 15
            },
            routerLink: 'task',
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
            routerLink: 'task',
            fontAwesomeClass: "fas fa-user-cog fa-2x text-dark",
            isSelected: false
        }
    ];

    constructor(private translateService: TranslateService, private shellService: ShellService) {}

    ngOnInit(): void {
        this.shellService.setDockItemsState(this.dockItemsState);
        this.shellService.setLoadingSpinner(false);
    }

    logout(): void {

    }
}
