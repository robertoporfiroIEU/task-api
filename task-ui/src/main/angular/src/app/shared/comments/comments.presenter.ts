import { ElementRef, Injectable, OnDestroy } from '@angular/core';
import { Clipboard } from '@angular/cdk/clipboard';
import { Subject } from 'rxjs';
import { Comment, Pageable, PaginatedComments, User } from '../../api';
import { ConfirmationService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { EditorModel } from '../ModelsForUI';

@Injectable()
export class CommentsPresenter implements OnDestroy {

    private destroy = new Subject<void>();

    private addNewCommentSubject = new Subject<Comment>();
    onAddNewComment$ = this.addNewCommentSubject.asObservable();

    private deleteClickSubject = new Subject<string>()
    deleteClick$ = this.deleteClickSubject.asObservable();

    private updateCommentSubject = new Subject<Comment>()
    onUpdateComment$ = this.updateCommentSubject.asObservable();

    private replyTextSubject = new Subject<string>()
    onReplyText$ = this.replyTextSubject.asObservable();

    private changePageSubject = new Subject<Pageable>();
    onChangePage$ = this.changePageSubject.asObservable();

    comments: PaginatedComments | null = null;
    user!: User;

    constructor(
        private clipboard: Clipboard,
        private confirmationService: ConfirmationService,
        private translateService: TranslateService
    ) {}

    init(comments: PaginatedComments, user: User) {
        this.user = user;
        this.comments = comments;
    }

    addNewComment(editorModel: EditorModel): void {
        this.addNewCommentSubject.next({
            text: editorModel.text,
            createdBy: this.user,
            attachments: editorModel.attachments
        });
    }

    copyCommentURLToClipboard(identifier: string): void {
        this.clipboard.copy(this.getCommentURL(identifier));
    }

    deleteClick(identifier: string): void {
        this.confirmationService.confirm({
            accept: () => {
                this.deleteClickSubject.next(identifier);
            }
        });
    }

    replyToTheComment(comment: Comment, div: ElementRef): void {
        let text = '<blockquote>' + comment.text + '</blockquote>';
        let commentURL = this.getCommentURL(comment.identifier!);
        let commentedByLabel = this.translateService.instant('taskUI.comment-commented-by');
        let commentUrlLabel = this.translateService.instant('taskUI.comment-url-comment');
        text += `<p>${commentedByLabel} ${comment.createdBy.name} <a href="${commentURL}"> ${commentUrlLabel}</a></p>`;
        div.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        this.replyTextSubject.next(text);
    }

    updateComment(editorModel: EditorModel, comment: Comment): void {
        comment.text = editorModel.text;
        comment.attachments = editorModel.attachments;
        this.updateCommentSubject.next(comment);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

    paginate(paginateEvent: any) {
        let page: number = 0;
        let size: number = 25;

        if (paginateEvent) {
            if (paginateEvent.first && paginateEvent.rows) {
                page = paginateEvent?.first / paginateEvent?.rows;
                size = paginateEvent?.rows;
            }
        }

        this.changePageSubject.next({
            page: page,
            size: size
        });
    }

    private getCommentURL(identifier: string): string {
        // replace the anchor with the new one
        let urlParts: string[] = window.location.href.split('#');
        let url: string;
        if (urlParts.length > 2) {
            url = urlParts[0] + '#' + urlParts[1];
        } else {
            url = window.location.href;
        }
        return url + '#' + identifier;
    }
}
