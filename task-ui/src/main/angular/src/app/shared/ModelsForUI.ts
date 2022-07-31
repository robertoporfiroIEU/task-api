import { Attachment } from '../api';

export interface Column {
    field: string; // This is the field name of the object. An example User: { name: 'test' } the field is the name
    header: string; // This is the translate key of the field.
    toolTipLabel?: string; // This is the translate key of the tooltip.
    placeholder?: string;
}

export interface DropDown {
    label: string,
    value: string
}

export enum Type {
    ASSIGNS,
    SPECTATORS
}

export interface FileEvent {
    commentIdentifier?: string;
    createdBy: string
    description?: string
    fileContent: File
}

export class AttachmentsMimeType {
    WORD: string[] = ['application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    IMAGE: string[] = ['image/png', 'image/jpeg'];
    PDF: string[] = ['application/pdf'];
    EXCEL: string[] = ['application/x-tika-ooxml', 'application/vnd.ms-excel', 'application/vnd.ms-excel']
}

export interface EditorModel {
    text: string;
    attachments: Attachment[];
}

export enum Roles {
    CONSULTATION_ROLE = 'CONSULTATION',
    DEVELOPER_ROLE = 'DEVELOPER',
    LEADER_ROLE = 'LEADER',
    PROJECT_MANAGER_ROLE = 'PROJECT_MANAGER',
    ADMIN_ROLE = 'ADMIN'
}
