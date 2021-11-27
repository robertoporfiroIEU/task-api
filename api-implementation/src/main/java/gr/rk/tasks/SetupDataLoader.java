package gr.rk.tasks;

import gr.rk.tasks.entity.*;
import gr.rk.tasks.repository.*;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Value("${applicationConfigurations.addTestData:false}")
    private boolean addTestData;

    @Value("${applicationConfigurations.deleteTestData:false}")
    private boolean deleteTestData;
    private final UserPrincipal userPrincipal;

    private static final String PROJECT_IDENTIFIER = "SetupDataLoader#Project";
    private static final String TASK_IDENTIFIER = "SetupDataLoader#Task";
    private static final String COMMENT_IDENTIFIER = "SetupDataLoader#Comment";
    private static final String GROUP_NAME = "SetupDataLoader#test group";
    private static final String USERNAME = "SetupDataLoader#Rafail";
    private static final int NUMBER_OF_COMMENTS = 30;

    @Autowired
    public SetupDataLoader(
            UserRepository userRepository,
            GroupRepository groupRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            UserPrincipal userPrincipal
            ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.groupRepository = groupRepository;
        this.taskRepository = taskRepository;
        this.userPrincipal = userPrincipal;
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
                userRepository.save(user);

                // Create a group
                Group group = new Group();
                group.setName(GROUP_NAME);
                group.setDescription("This is a test group");
                group.setApplicationUser(userPrincipal.getApplicationUser());
                groupRepository.save(group);

                user.setGroups(Set.of(group));

                // create a Project
                Project project = new Project();
                project.setIdentifier(PROJECT_IDENTIFIER);
                project.setPrefixIdentifier("taskapi");
                project.setName("Test Project");
                project.setDescription("This is a project description");
                project.setCreatedBy(user);
                project.setApplicationUser(userPrincipal.getApplicationUser());
                projectRepository.save(project);

                // Create a task
                Task task = new Task();
                task.setIdentifier(TASK_IDENTIFIER);
                task.setName("test task");
                task.setStatus("created");
                task.setCreatedBy(user);
                task.setApplicationUser(userPrincipal.getApplicationUser());
                task.setProject(project);
                taskRepository.save(task);

                // Create an assign
                Assign assign = new Assign();
                assign.setUser(user);
                assign.setGroup(group);
                assign.setApplicationUser(userPrincipal.getApplicationUser());
                assign.setTask(task);

                // Create a spectator
                Spectator spectator = new Spectator();
                spectator.setUser(user);
                spectator.setGroup(group);
                spectator.setTask(task);
                spectator.setApplicationUser(userPrincipal.getApplicationUser());

                Set<Comment> comments = createComments(task, user);
                task.setComments(comments);
                task.setAssigns(Set.of(assign));
                task.setSpectators(Set.of(spectator));
                task.setApplicationUser(userPrincipal.getApplicationUser());
            } else if (deleteTestData) {
                userRepository.deleteByUsername(USERNAME);
                groupRepository.deleteByName(GROUP_NAME);
            }

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    private Set<Comment> createComments(Task task, User user) {
        Set<Comment> comments = new HashSet<>();
        for (int i = 0; i < NUMBER_OF_COMMENTS; i++) {
            Comment comment = new Comment();
            comment.setIdentifier(COMMENT_IDENTIFIER + i);
            comment.setText("This is a test text");
            comment.setCreatedBy(user);
            comment.setTask(task);
            comment.setApplicationUser(userPrincipal.getApplicationUser());
            comments.add(comment);
        }
        return comments;
    }
}
