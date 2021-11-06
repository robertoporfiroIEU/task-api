package gr.rk.tasks;

import gr.rk.tasks.entity.*;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final UserService userService;
    private final GroupService groupService;
    private final UserRepository userRepository;

    @Value("${applicationConfigurations.addTestData:false}")
    private boolean addTestData;

    @Value("${applicationConfigurations.deleteTestData:false}")
    private boolean deleteTestData;
    private final UserPrincipal userPrincipal;

    private static final String PROJECT_IDENTIFIER = "c8883d60-84fe-4eca-b8ea-0192f6239912";
    private static final String TASK_IDENTIFIER = "c8883d60-84fe-4eca-b8ea-0192f6239913";
    private static final String GROUP_NAME = "test group";
    private static final String USERNAME = "Rafail";
    private static final int NUMBER_OF_COMMENTS = 30;

    @Autowired
    public SetupDataLoader(
            ProjectService projectService,
            TaskService taskService,
            UserService userService,
            GroupService groupService,
            UserPrincipal userPrincipal,
            UserRepository userRepository
            ) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.userService = userService;
        this.groupService = groupService;
        this.userPrincipal = userPrincipal;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            if (addTestData) {
                // Create a user
                User user = new User();
                user.setUsername(USERNAME);
                user.setEmail("rafail@gmail.gr");
                user.setApplicationUser(userPrincipal.getApplicationUser());
                user = userRepository.save(user);

                // Create a comment
                Comment comment = new Comment();
                comment.setText("This is a test text");
                comment.setCreatedBy(user);
                comment.setApplicationUser(userPrincipal.getApplicationUser());

                // Create a group
                Group group = new Group();
                group.setName(GROUP_NAME);
                group.setDescription("This is a test group");
                group.setApplicationUser(userPrincipal.getApplicationUser());

                user.setGroups(Arrays.asList(group));

                // Create an assign
                Assign assign = new Assign();
                assign.setUser(user);
                assign.setGroup(group);
                assign.setApplicationUser(userPrincipal.getApplicationUser());

                // Create a spectator
                Spectator spectator = new Spectator();
                spectator.setUser(user);
                spectator.setGroup(group);
                spectator.setApplicationUser(userPrincipal.getApplicationUser());

                // Create a task
                Task task = new Task();
                task.setIdentifier(TASK_IDENTIFIER);
                task.setName("test task");
                task.setStatus("created");
                task.setCreatedBy(user);
                task.setApplicationUser(userPrincipal.getApplicationUser());

                List<Comment> comments = createComments(task, user);

                task.setComments(comments);
                task.setAssigns(List.of(assign));
                task.setSpectators(List.of(spectator));
                task.setApplicationUser(userPrincipal.getApplicationUser());
                assign.setTask(task);
                spectator.setTask(task);

                Project project = new Project();
                project.setIdentifier(PROJECT_IDENTIFIER);
                project.setName("Test Project");
                project.setDescription("This is a project description");
                project.setCreatedBy(user);
                project.setApplicationUser(userPrincipal.getApplicationUser());

                task.setProject(project);
                project.setTasks(List.of(task));

                // persist
                projectService.createProject(project);
            } else if (deleteTestData) {
                projectService.deleteProject(PROJECT_IDENTIFIER);
                groupService.deleteGroup(GROUP_NAME);
                userService.deleteUser(USERNAME);
            }

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    private List<Comment> createComments(Task task, User user) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_COMMENTS; i++) {
            Comment comment = new Comment();
            comment.setText("This is a test text");
            comment.setCreatedBy(user);
            comment.setTask(task);
            comment.setApplicationUser(userPrincipal.getApplicationUser());
            comments.add(comment);
        }
        return comments;
    }
}
