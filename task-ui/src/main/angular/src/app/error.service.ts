import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';
import { ModelError } from './api';
import StatusEnum = ModelError.StatusEnum;
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ShellService } from './shell/shell.service';

@Injectable({
    providedIn: 'root'
})
export class ErrorService {

    constructor(
        private messageService: MessageService,
        private translateService: TranslateService,
        private shellService: ShellService,
    ) {}

    showErrorMessage(httpErrorResponse: HttpErrorResponse): void {
        this.shellService.setLoadingSpinner(false);
        if (!httpErrorResponse.error.status) {
            this.messageService.add(
                {
                    severity:'error',
                    summary: this.translateService.instant('taskUI.unknown-error'),
                    detail: this.translateService.instant('taskUI.unknown-error')
                }
            );
            return;
        }

        if(httpErrorResponse.error.status === StatusEnum.Fail) {
            this.messageService.add(
                {
                    severity:'warn',
                    summary: this.translateService.instant(httpErrorResponse.error.translateKey!),
                    detail: httpErrorResponse.error.message!
                }
            );
        } else {
            this.messageService.add(
                {
                    severity:'error',
                    summary: this.translateService.instant(httpErrorResponse.error.message!),
                    detail: httpErrorResponse.error.message!
                }
            );
        }
    }

    showCustomErrorMessage(summaryTranslateKey: string, messageTranslateKey: string): void {
        this.shellService.setLoadingSpinner(false);
        this.messageService.add(
            {
                severity:'error',
                summary: this.translateService.instant(summaryTranslateKey),
                detail: this.translateService.instant(messageTranslateKey),
            }
        );
    }
}
