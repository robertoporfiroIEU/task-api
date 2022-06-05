import {
    EventEmitter,
    Component,
    Input,
    OnInit,
    Output,
    OnDestroy,
} from '@angular/core';
import { EditorModel, FileEvent } from '../ModelsForUI';
import { catchError, Observable, Subject, takeUntil } from 'rxjs';
import { Attachment, User } from '../../api';
import { ShellService } from '../../shell/shell.service';
import { FilesService } from '../../api';
import { ErrorService } from '../../error.service';

@Component({
    selector: 'app-editor',
    templateUrl: './editor.container.html'
})
export class EditorContainerComponent implements OnInit, OnDestroy {

    private destroy = new Subject<void>();

    @Input() identifier: string = '';
    @Input() canEditTheContent: boolean = true;
    @Input() leaveComment: boolean = false;
    @Input() placeholder: string = '';
    @Input() readOnly: boolean = false;
    @Input() user!: User
    @Input() editorText: string | null = null;
    @Input() editorText$: Observable<string> | null = null;
    @Input() attachments: Attachment[] = [];
    @Input() onSaveSuccessCancelEditor$: Observable<void> | null = null;
    @Output() onEditorClick = new EventEmitter<void>();
    @Output() onEditorDoubleClick = new EventEmitter<void>();
    @Output() onSaveClick = new EventEmitter<EditorModel>();
    @Output() onCancelClick = new EventEmitter<void>();

    attachmentUploadedSubject = new Subject<Attachment>();
    onAttachmentUploaded$: Observable<Attachment> = this.attachmentUploadedSubject.asObservable();

    constructor(
        private shellService: ShellService,
        private filesService: FilesService,
        private errorService: ErrorService
    ) {}

    ngOnInit(): void {}

    saveClick(editorModel: EditorModel): void {
        this.onSaveClick.emit(editorModel);
    }

    cancelClick(): void {
        this.onCancelClick.emit();
    }

    uploadFile(fileEvent: FileEvent): void {
        this.shellService.setLoadingSpinner(true);
        this.filesService.upload(fileEvent.createdBy, fileEvent.description, fileEvent.fileContent).pipe(
            takeUntil(this.destroy),
            catchError(error => {
                this.errorService.showErrorMessage(error);
                return [];
            }),
        ).subscribe( attachment => {
            this.attachmentUploadedSubject.next(attachment);
            this.shellService.setLoadingSpinner(false);
        })
    }

    ngOnDestroy(): void {
        this.destroy.next();
        this.destroy.complete();
    }
}
