import { ElementRef, Injectable, OnDestroy } from '@angular/core';
import { Editor } from 'primeng/editor';
import { Observable, Subject, takeUntil } from 'rxjs';
import { AttachmentsMimeType, EditorModel, FileEvent } from '../ModelsForUI';
import { Attachment, FilesService, User } from '../../api';
import Quill from 'quill';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Injectable()
export class EditorPresenter implements OnDestroy  {

    private destroy = new Subject<void>();

    editor!: Editor;

    private saveClickSubject = new Subject<EditorModel>();
    onSaveClick$ = this.saveClickSubject.asObservable();

    private cancelClickSubject = new Subject<void>();
    onCancelClick$ = this.cancelClickSubject.asObservable();

    private uploadFileSubject = new Subject<FileEvent>();
    onUploadFile$ = this.uploadFileSubject.asObservable();
    isEditorEditable: boolean = false;
    onAttachmentUploaded$!: Observable<Attachment>;
    user!: User;
    attachmentsMimeType: AttachmentsMimeType = new AttachmentsMimeType();
    attachmentURLBase: string = `${this.filesService.configuration.basePath}/file/`;

    editorForm!: FormGroup;
    isReadOnly: boolean = false;
    leaveComment: boolean = false;
    initialEditorTextValue: string | null = null;
    initialEditorAttachments: Attachment[] = [];

    constructor(private filesService: FilesService) {}

    init(user: User,
         editorText: string | null,
         attachments: Attachment[],
         onAttachmentUploaded$: Observable<Attachment>,
         readOnly: boolean,
         leaveComment: boolean,
         editorText$: Observable<string> | null
    ): void {
        this.onAttachmentUploaded$ = onAttachmentUploaded$;
        this.user = user;
        this.isReadOnly = readOnly;
        this.leaveComment = leaveComment;
        this.initialEditorTextValue = editorText;
        this.initialEditorAttachments = [...attachments];

        if (!attachments) {
            attachments = [];
        }

        this.editorForm = new FormGroup({
            text: new FormControl(editorText, Validators.required),
            attachments: new FormControl(attachments)
        });

        this.onAttachmentUploaded$?.pipe(
            takeUntil(this.destroy)
        )?.subscribe(attachment => {
            if (this.attachmentsMimeType.IMAGE.includes(attachment.mimeType!)) {
                let range = this.editor.getQuill().getSelection();
                let imageURL = `${this.filesService.configuration.basePath}/file/${attachment.identifier}`
                this.editor.getQuill().insertEmbed(range?.index, 'image', imageURL, Quill.sources.USER);
            }
            this.editorForm.value.attachments.push(attachment);
        })

        if (editorText$) {
            editorText$.pipe(
                takeUntil(this.destroy)
            ).subscribe(text =>{
                this.setReadOnlyState(false);
                (this.editorForm.get('text') as FormControl)?.setValue(text);
                this.focusEditor();
            });
        }
    }

    saveClick(): void {
        if (this.editorForm.invalid) {
            return;
        }
        this.saveClickSubject.next({
            text: this.editorForm.value.text,
            attachments: this.editorForm.value.attachments
        });
    }

    cancelClick(): void {
        if (this.leaveComment) {
            this.clearForm();
        } else {
            (this.editorForm.get('attachments') as FormControl)?.setValue(this.initialEditorAttachments);
            (this.editorForm.get('text') as FormControl)?.setValue(this.initialEditorTextValue);
        }

        this.setReadOnlyState(true);
        this.cancelClickSubject.next();
    }

    setEditor(editor: Editor): void {
        this.editor = editor;
    }

    onFileSelected(event: any) {
        let files: FileList | null = (event?.target as HTMLInputElement)?.files;
        if (files && files.length !== 0) {
            this.uploadFileSubject.next({
                createdBy: this.user.name,
                fileContent: files[0]
            });
        }
        event.target.value = null;
    }

    removeAttachment(identifier: string) {
        let filteredAttachments = this.editorForm.value.attachments.filter((a: Attachment) => a.identifier !== identifier);
        (this.editorForm.get('attachments') as FormControl)?.setValue(filteredAttachments);
    }

    setReadOnlyState(state: boolean): void {
        this.isReadOnly = state;
    }

    editorClick(canEditTheContent: boolean) {
        if (!canEditTheContent) {
            return;
        }

        if (!this.initialEditorTextValue) {
            this.setReadOnlyState(false);
            this.focusEditor();
        }
    }

    editorDoubleClick(canEditTheContent: boolean): void {
        if (!canEditTheContent) {
            return;
        }
        this.setReadOnlyState(false);
        this.focusEditor();
    }

    clearForm(): void {
        (this.editorForm.get('attachments') as FormControl)?.setValue([]);
        (this.editorForm.get('text') as FormControl)?.setValue(null);
    }

    scroll(div: ElementRef): void {
        setTimeout(() => {
            div.nativeElement.scrollIntoView({ behavior: 'smooth' })
            div.nativeElement.style.border = "thick solid #b4e4b9";
        }, 1000);

        setTimeout(() => {
            div.nativeElement.style.border = ''
        }, 6000);
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }

    private focusEditor(): void {
        setTimeout(() => {
            let quill = this.editor.getQuill();
            quill.setSelection(quill.getLength(), 0);
        })
    }

}
