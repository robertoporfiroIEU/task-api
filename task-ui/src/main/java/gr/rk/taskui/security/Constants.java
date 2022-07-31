package gr.rk.taskui.security;

public class Constants {
    // Security Path patterns
    public static final String PROJECTS_ENDPOINT_PATTERN = "/tasks-api/projects/**";
    public static final String TASKS_ENDPOINT_PATTERN = "/tasks-api/tasks/**";
    public static final String COMMENTS_ENDPOINT_PATTERN = "/tasks-api/tasks/{token}/comments/**";
    public static final String APPLICATION_CONFIGURATIONS_ENDPOINT_PATTERN = "/tasks-api/applicationConfigurations/**";
    public static final String FILE_ENDPOINT_PATTERN = "/tasks-api/file/**";
    public static final String USERS_ENDPOINT_PATTERN = "/tasks-api/users";

    // Roles
    public static final String CONSULTATION_ROLE = "CONSULTATION";
    public static final String DEVELOPER_ROLE = "DEVELOPER";
    public static final String LEADER_ROLE = "LEADER";
    public static final String PROJECT_MANAGER_ROLE = "PROJECT_MANAGER";
    public static final String ADMIN_ROLE = "ADMIN";
}
