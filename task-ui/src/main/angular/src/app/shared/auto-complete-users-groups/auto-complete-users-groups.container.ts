import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { GroupsService, User, UsersService } from '../../api';
import { UserProfileService } from '../../user-profile.service';
import { Subject, switchMap, takeUntil } from 'rxjs';
import { Type } from '../ModelsForUI';

@Component({
    selector: 'app-auto-complete-users-groups',
    templateUrl: './auto-complete-users-groups.container.html',
})
export class AutoCompleteUsersGroupsContainerComponent implements OnInit {

    private destroy: Subject<void> = new Subject();
    private autocompleteUsersSubject = new Subject<string>();
    private autocompleteGroupsSubject = new Subject<string>();

    @Input() type: Type = Type.ASSIGNS;
    @Input() task: Task | null = null;
    @Input() formControlNameForUsers: string | null = null;
    @Input() formControlNameForGroups: string | null = null;
    @Input() readOnly: boolean = false;
    @Input() editable: boolean = false;
    @Input() withSaveButton: boolean = false;
    @Output() onEditClick = new EventEmitter<void>();
    @Output() onCancelClick = new EventEmitter<void>();
    @Output() onSave = new EventEmitter<void>();

    userProfile: User | null = null
    autoCompleteUsersData: string[] = [];
    autoCompleteGroupsData: string[] = [];

    constructor(
        private usersService: UsersService,
        private groupsService: GroupsService,
        private userProfileService: UserProfileService
    ) {}

    ngOnInit(): void {
        this.autocompleteUsersSubject.pipe(
            takeUntil(this.destroy),
            switchMap(query =>
                this.usersService.getUsers(query)
            )
        ).subscribe(users => this.autoCompleteUsersData = users.map(user => user.name));

        this.autocompleteGroupsSubject.pipe(
            takeUntil(this.destroy),
            switchMap(query =>
                this.groupsService.getGroups(query)
            )
        ).subscribe(groups => this.autoCompleteGroupsData = groups);

        this.userProfileService.userProfile$.pipe(
            takeUntil(this.destroy),
        ).subscribe( userProfile => this.userProfile = userProfile);
    }

    getAutoCompleteUsers(query: string): void {
        this.autocompleteUsersSubject.next(query);
    }

    getAutoCompleteGroups(query: string): void {
        this.autocompleteGroupsSubject.next(query);
    }

    editClick(): void {
        this.onEditClick.emit();
    }

    cancelClick(): void {
        this.onCancelClick.emit();
    }

    save(): void {
        this.onSave.emit();
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
