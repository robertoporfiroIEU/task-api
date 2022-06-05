import {
    AfterViewInit,
    EventEmitter,
    Component,
    Input,
    OnInit,
    Output,
    ViewChild,
    OnDestroy, ElementRef
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { EditorPresenter } from './editor.presenter';
import { Editor } from 'primeng/editor';
import { EditorModel, FileEvent } from '../ModelsForUI';
import { Observable, Subject, takeUntil } from 'rxjs';
import { Attachment, User } from '../../api';
import { AttachmentsMimeType } from '../ModelsForUI'
import { ViewportScroller } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-editor-ui',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.css'],
    providers: [
        EditorPresenter,
    ]
})
export class EditorComponent implements OnInit, AfterViewInit, OnDestroy {

    private destroy = new Subject<void>();

    @Input() identifier: string = '';
    @Input() leaveComment: boolean = false;
    @Input() canEditTheContent: boolean = true;
    @Input() placeholder: string = '';
    @Input() readOnly: boolean = false;
    @Input() user!: User
    @Input() onAttachmentUploaded$!: Observable<Attachment>;
    @Input() editorText: string | null = null;
    @Input() editorText$: Observable<string> | null = null;
    @Input() attachments: Attachment[] = [];
    @Input() onSaveSuccessCancelEditor$: Observable<void> | null = null;
    @Output() onSaveClick = new EventEmitter<EditorModel>();
    @Output() onCancelClick = new EventEmitter<void>();
    @Output() onUploadFile = new EventEmitter<FileEvent>();
    @ViewChild('editor') editor!: Editor;
    @ViewChild('divElement') div: ElementRef | null = null;

    get isEditorEditable(): boolean {
        return this.editorPresenter.isEditorEditable;
    }

    get attachmentURLBase(): string {
        return this.editorPresenter.attachmentURLBase;
    }

    get attachmentsMimeType(): AttachmentsMimeType {
        return this.editorPresenter.attachmentsMimeType;
    }

    get editorForm(): FormGroup {
        return this.editorPresenter.editorForm;
    }

    get isReadOnly(): boolean {
        return this.editorPresenter.isReadOnly;
    }

    constructor(
        private editorPresenter: EditorPresenter,
        private viewportScroller: ViewportScroller,
        private activatedRoute: ActivatedRoute
    ) {
    }

    ngOnInit(): void {
        this.editorPresenter.init(
            this.user,
            this.editorText,
            this.attachments,
            this.onAttachmentUploaded$,
            this.readOnly,
            this.leaveComment,
            this.editorText$
        );

        this.editorPresenter.onSaveClick$.pipe(
            takeUntil(this.destroy)
        ).subscribe(editorModel => this.onSaveClick.emit(editorModel));

        this.editorPresenter.onCancelClick$.pipe(
            takeUntil(this.destroy)
        ).subscribe(() => this.onCancelClick.emit());

        this.editorPresenter.onUploadFile$.pipe(
            takeUntil(this.destroy)
        ).subscribe(fileEvent => this.onUploadFile.emit(fileEvent));

        if (this.onSaveSuccessCancelEditor$) {
            this.onSaveSuccessCancelEditor$.pipe(
                takeUntil(this.destroy)
            ).subscribe(() => {
                this.editorPresenter.clearForm();
                this.editorPresenter.setReadOnlyState(true)
            });
        }
    }

    editorClick(): void {
        this.editorPresenter.editorClick(this.canEditTheContent);
    }

    editorDoubleClick(): void {
        this.editorPresenter.editorDoubleClick(this.canEditTheContent);
    }

    saveClick(): void {
        this.editorPresenter.saveClick();
    }

    cancelClick(): void {
        this.editorPresenter.cancelClick();
    }

    ngAfterViewInit(): void {
        this.editorPresenter.setEditor(this.editor);

        this.activatedRoute.fragment.subscribe(f => {
            if (this.identifier === '' + f) {
                this.editorPresenter.scroll(this.div!);
            }
        });
    }

    onFileSelected(event: Event) {
        this.editorPresenter.onFileSelected(event);
    }

    removeAttachment(identifier: string) {
        this.editorPresenter.removeAttachment(identifier);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
