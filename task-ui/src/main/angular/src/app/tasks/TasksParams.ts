import { Pageable } from '../api';

export interface TasksParams {
    pageable?: Pageable,
    identifier?: string,
    projectIdentifier?: string,
    name?: string,
    status?: string,
    creationDateFrom?: string,
    creationDateTo?: string,
    dueDateFrom?: string,
    dueDateTo?: string,
    createdBy?: string,
    assignedTo?: string,
    spectator?: string
}
