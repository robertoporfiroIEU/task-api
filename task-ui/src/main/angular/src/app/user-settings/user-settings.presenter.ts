import { Injectable } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserPrincipal } from '../shared/ModelsForUI';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class UserSettingsPresenter {

    userForm!: FormGroup;

    languages: SelectItem[] = [];

    constructor(private translateService: TranslateService) {}

    init(userPrincipal: UserPrincipal) {
        this.userForm = new FormGroup({
            name: new FormControl(userPrincipal.name),
            email: new FormControl(userPrincipal.email),
            language: new FormControl(userPrincipal.language)
        });

        this.languages = [
            {
                label: this.translateService.instant('taskUI.user-settings-language-greek'),
                value: 'gr'
            },
            {
                label: this.translateService.instant('taskUI.user-settings-language-english'),
                value: 'en'
            }
        ];
    }

    changeLanguage(): void {
        let language = this.userForm.value.language;
        localStorage.setItem('language', language);
        window.location.reload();
    }
}
