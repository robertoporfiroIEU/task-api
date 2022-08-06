import {
    Component, ElementRef,
    EventEmitter,
    Input, OnDestroy,
    OnInit,
    Output, QueryList, ViewChild, ViewChildren,
} from '@angular/core';
import { Comment, Pageable, PaginatedComments } from '../../api';
import { CommentsPresenter } from './comments.presenter';
import { Observable, Subject, takeUntil } from 'rxjs';
import { Utils } from '../Utils';
import { ConfirmationService } from 'primeng/api';
import { EditorModel, Roles, UserPrincipal } from '../ModelsForUI';
import { Editor } from 'primeng/editor';
import { EditorContainerComponent } from '../editor/editor.container';
import { AuthService } from '../../auth.service';

@Component({
    selector: 'app-comments-ui',
    templateUrl: './comments.component.html',
    styleUrls: ['./comments.component.css'],
    providers: [CommentsPresenter, ConfirmationService]
})
export class CommentsComponent implements OnInit, OnDestroy {
    private destroy = new Subject<void>();

    @Input() comments: PaginatedComments | null = null;
    @Input() userPrincipal!: UserPrincipal;
    @Input() addNewCommentSuccess$!: Observable<void>;
    @Input() page: number = 0;
    @Output() onDeleteClick = new EventEmitter<string>();
    @Output() onAddNewComment = new EventEmitter<Comment>();
    @Output() onUpdateComment = new EventEmitter<Comment>();
    @Output() onChangePage = new EventEmitter<Pageable>();
    @ViewChildren('editor') editorsComponent!: QueryList<EditorContainerComponent>
    @ViewChild('editor') editor!: Editor;
    @ViewChild('divElement') div: ElementRef | null = null;
    replyText$ = this.commentPresenter.onReplyText$

    datePipeDateTimeFormat: string = Utils.datePipeDateTimeFormat

    constructor(private commentPresenter: CommentsPresenter, private authService: AuthService) {}

    ngOnInit(): void {
        this.commentPresenter.init(this.comments!);

        this.commentPresenter.deleteClick$.pipe(
            takeUntil(this.destroy)
        ).subscribe(commentIdentifier => this.onDeleteClick.emit(commentIdentifier));

        this.commentPresenter.onAddNewComment$.pipe(
            takeUntil(this.destroy)
        ).subscribe(comment => this.onAddNewComment.emit(comment));

        this.commentPresenter.onUpdateComment$.pipe(
            takeUntil(this.destroy)
        ).subscribe(comment => this.onUpdateComment.emit(comment))

        this.commentPresenter.onChangePage$.pipe(
            takeUntil(this.destroy)
        ).subscribe(page => this.onChangePage.emit(page));
    }

    addNewComment(editorModel: EditorModel): void {
        this.commentPresenter.addNewComment(editorModel);
    }

    copyCommentURLToClipboard(identifier: string): void {
        this.commentPresenter.copyCommentURLToClipboard(identifier)
    }

    deleteClick(comment: Comment): void {
        this.commentPresenter.deleteClick(comment!.identifier!)
    }

    updateComment(editorModel: EditorModel, comment: Comment): void {
        this.commentPresenter.updateComment(editorModel, comment);
    }

    replyToTheComment(comment: Comment): void {
        this.commentPresenter.replyToTheComment(comment, this.div!);
    }

    canUSerLeaveAComment(): boolean {
        return this.authService.isUserRoleInRoles(
            [
                Roles.DEVELOPER_ROLE,
                Roles.LEADER_ROLE,
                Roles.PROJECT_MANAGER_ROLE,
                Roles.ADMIN_ROLE
            ]
        )
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

    paginate(paginateEvent: any): void {
        this.commentPresenter.paginate(paginateEvent);
    }
}
