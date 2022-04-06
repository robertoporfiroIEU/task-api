import {
    Component,
    Input, OnDestroy,
    OnInit
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PaginatedComments, TasksService, User, Comment, Attachment, Pageable } from '../../api';
import { catchError, Observable, Subject, switchMap, takeUntil } from 'rxjs';
import { UserProfileService } from '../../user-profile.service';
import { ErrorService } from '../../error.service';
import { ShellService } from '../../shell/shell.service';
import { MessageService } from 'primeng/api';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-comments',
    templateUrl: './comments.container.html',
})
export class CommentsContainerComponent implements OnInit, OnDestroy {

    private destroy = new Subject<void>();
    private addNewCommentSuccessSubject = new Subject<void>();
    addNewCommentSuccess$ = this.addNewCommentSuccessSubject.asObservable();

    private attachmentUploadedSubject = new Subject<Attachment>();
    onAttachmentUploaded$ = this.attachmentUploadedSubject.asObservable();

    private changePageSubject = new Subject<Pageable>();
    onChangePage$ = this.changePageSubject.asObservable();

    @Input() taskIdentifier: string | null = null;
    comments: PaginatedComments | null = null;

    user$: Observable<User> = this.userProfileService.userProfile$;
    user!: User;
    page: number = 0;

    constructor(private tasksService: TasksService,
                private userProfileService: UserProfileService,
                private shellService: ShellService,
                private messageService: MessageService,
                private errorService: ErrorService,
                private translateService: TranslateService,
                private route: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.userProfileService.userProfile$.pipe(
            takeUntil(this.destroy)
        ).subscribe( user => this.user = user);

        this.onChangePage$.pipe(
            takeUntil(this.destroy),
            switchMap(pageable => this.tasksService.getComments( this.taskIdentifier!, pageable))
        ).subscribe(comments => this.comments = comments);

        this.route.queryParams.pipe(
            takeUntil(this.destroy)
        ).subscribe(p => {
            if (p['page']) {
                this.page = p['page'] - 1;
            }
            this.changePageSubject.next({page: this.page, size: 25, sort: 'createdAt,desc',});
        });


    }

    addNewComment(comment: Comment): void {
        this.shellService.setLoadingSpinner(true);
        this.tasksService.addTaskComment(this.taskIdentifier!, comment).pipe(
            catchError(error => {
                this.errorService.showErrorMessage(error);
                return [];
            })).subscribe((commentFromAPI) => {
            this.shellService.setLoadingSpinner(false);
            this.messageService.add(
                {
                    severity: 'success',
                    summary: this.translateService.instant('taskUI.success'),
                    detail: this.translateService.instant('taskUI.comment-add-success-message')
                }
            );

            if (this.comments?.content && this.comments.totalElements) {
                this.comments.content.unshift(commentFromAPI);
                this.comments.totalElements++;
            } else {
                this.comments = {
                    content: [commentFromAPI],
                    totalElements: 1
                }
            }

            this.addNewCommentSuccessSubject.next();
        });
    }

    updateComment(comment: Comment): void {
        this.shellService.setLoadingSpinner(true);
        this.tasksService.updateComment(this.taskIdentifier!, comment.identifier!, comment).pipe(
            takeUntil(this.destroy),
            catchError(error => {
                this.errorService.showErrorMessage(error);
                return [];
            })).subscribe((comment) => {
            this.shellService.setLoadingSpinner(false);
            this.messageService.add(
                {
                    severity: 'success',
                    summary: this.translateService.instant('taskUI.success'),
                    detail: this.translateService.instant('taskUI.comment-update-success-message')
                }
            );

            // update the model
            let indexOfComment: number = this.comments!.content!.findIndex(c => c.identifier === comment.identifier);
            this.comments!.content![indexOfComment] = comment!;
        })
    }

    deleteComment(identifier: string): void {
        this.shellService.setLoadingSpinner(true);
        this.tasksService.deleteComment(this.taskIdentifier!, identifier).pipe(
                takeUntil(this.destroy),
                catchError(error => {
                    this.errorService.showErrorMessage(error);
                    return [];
                })
        ).subscribe( () => {
            this.shellService.setLoadingSpinner(false);
            this.messageService.add(
                {
                    severity: 'success',
                    summary: this.translateService.instant('taskUI.success'),
                    detail: this.translateService.instant('taskUI.comment-delete-success-message')
                }
            );

            // update the model
            let indexOfComment: number = this.comments!.content!.findIndex(c => c.identifier === identifier);
            this.comments!.content?.splice(indexOfComment, 1);
        })
    }

    changePage(pageable: Pageable): void {
        this.changePageSubject.next(pageable);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
