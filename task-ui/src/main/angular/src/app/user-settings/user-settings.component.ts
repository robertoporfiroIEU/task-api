import { Component, Input, OnInit } from '@angular/core';
import { UserPrincipal } from '../shared/ModelsForUI';
import { UserSettingsPresenter } from './user-settings.presenter';
import { FormGroup } from '@angular/forms';
import { SelectItem } from 'primeng/api';

@Component({
    selector: 'app-user-settings-ui',
    templateUrl: './user-settings.component.html',
    styleUrls: ['./user-settings.component.css'],
    providers: [UserSettingsPresenter]
})
export class UserSettingsComponent implements OnInit {

    @Input() userPrincipal: UserPrincipal | null = null

    get userForm(): FormGroup {
        return this.userSettingsPresenter.userForm;
    }

    get languages(): SelectItem[] {
        return this.userSettingsPresenter.languages;
    }

    constructor(private userSettingsPresenter: UserSettingsPresenter) {}

    ngOnInit(): void {
        this.userSettingsPresenter.init(this.userPrincipal!);
    }

    changeLanguage(): void {
        this.userSettingsPresenter.changeLanguage();
    }
}
