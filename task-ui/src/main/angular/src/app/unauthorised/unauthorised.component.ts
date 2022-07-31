import { Component, OnInit } from '@angular/core';
import { Message } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-unauthorised',
    templateUrl: './unauthorised.component.html',
    styleUrls: ['./unauthorised.component.css']
})
export class UnauthorisedComponent implements OnInit {

    messages: Message[] = [];

    constructor(private translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.messages = [
            {
                severity:'info',
                summary: this.translateService.instant('taskUI.unauthorised-message-title'),
                detail: this.translateService.instant('taskUI.unauthorised-message-content')
            }
        ]
    }

}
