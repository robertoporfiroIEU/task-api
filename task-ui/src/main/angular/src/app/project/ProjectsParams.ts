import { Pageable } from '../api';

export interface ProjectsParams {
    pageable?: Pageable,
    identifier?: string,
    name?: string,
    creationDateFrom?: string,
    creationDateTo?: string,
    createdBy?: string,
}
