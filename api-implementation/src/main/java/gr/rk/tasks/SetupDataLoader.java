package gr.rk.tasks;

import gr.rk.tasks.entity.*;
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

    private final TaskService taskService;
    private final UserService userService;
    private final GroupService groupService;

    @Value("${applicationConfigurations.addTestData:false}")
    private boolean addTestData;

    @Value("${applicationConfigurations.deleteTestData:false}")
    private boolean deleteTestData;

    private final String taskIdentifier = "c8883d60-84fe-4eca-b8ea-0192f6239913";
    private final String groupName = "test group";
    private final String username = "Rafail";
    private final String applicationUser = "TestApplication";
    private final int numberOfComments = 30;

    @Autowired
    public SetupDataLoader(TaskService taskService, UserService userService, GroupService groupService) {
        this.taskService = taskService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            if (addTestData) {
                // Create a user
                User user = new User();
                user.setUsername(username);
                user.setEmail("rafail@gmail.gr");
                user.setApplicationUser(applicationUser);

                // Create a comment
                Comment comment = new Comment();
                comment.setText("This is a test text");
                comment.setCreatedBy(user);
                comment.setApplicationUser(applicationUser);

                // Create a group
                Group group = new Group();
                group.setName(groupName);
                group.setDescription("This is a test group");
                group.setApplicationUser(applicationUser);

                user.setGroups(Arrays.asList(group));

                // Create an assign
                Assign assign = new Assign();
                assign.setUser(user);
                assign.setGroup(group);
                assign.setApplicationUser(applicationUser);

                // Create a spectator
                Spectator spectator = new Spectator();
                spectator.setUser(user);
                spectator.setGroup(group);
                spectator.setApplicationUser(applicationUser);

                // Create a task
                Task task = new Task();
                task.setIdentifier(taskIdentifier);
                task.setName("test task");
                task.setApplicationUser("Test Application");
                task.setStatus("created");
                task.setCreatedBy(user);

                List<Comment> comments = createComments(task, user);

                task.setComments(comments);
                task.setAssigns(List.of(assign));
                task.setSpectators(List.of(spectator));

                assign.setTask(task);
                spectator.setTask(task);
                // persist
                taskService.createTask(task);
            } else if (deleteTestData) {
                taskService.deleteTask(taskIdentifier);
                groupService.deleteGroup(groupName);
                userService.deleteUser(username);
            }

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    private List<Comment> createComments(Task task, User user) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < numberOfComments; i++) {
            Comment comment = new Comment();
            comment.setText("This is a test text");
            comment.setCreatedBy(user);
            comment.setApplicationUser(applicationUser);
            comment.setTask(task);
            comments.add(comment);
        }
        return comments;
    }
}
