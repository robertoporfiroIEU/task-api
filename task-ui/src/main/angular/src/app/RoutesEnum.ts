export enum RoutesEnum {
    empty = '',
    projects = 'projects',
    createProject = 'projects/create-project',
    updateProject = 'projects/update-project/:project-identifier',
    viewProject = 'projects/view-project/:project-identifier',
    tasks = 'tasks',
    viewTask = 'tasks/:taskIdentifier',
    createTask = 'tasks/create-task',
    userSettings = 'user-settings',
    unauthorised = 'Unauthorised',
}
