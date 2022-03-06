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

export enum TaskStatuses {
    CREATE = 'taskUI.create-task-task-status-create',
    TODO = 'taskUI.create-task-task-status-todo',
    IN_PROGRESS = 'taskUI.create-task-task-status-in-progress',
    WAITING_FOR_REVIEW = 'taskUI.create-task-task-status-waiting-for-review',
    IN_REVIEW = 'taskUI.create-task-task-status-in-review',
    WAITING_FOR_TEST = 'taskUI.create-task-task-status-waiting-for-test',
    TEST = 'taskUI.create-task-task-status-test'
}

export enum Type {
    ASSIGNS,
    SPECTATORS
}
