package gr.rk.tasks;

import gr.rk.tasks.entity.*;
import gr.rk.tasks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final CommentService commentService;
    private final GroupService groupService;
    private final AssignService assignService;
    private final SpectatorService spectatorService;
    private final TaskService taskService;

    @Autowired
    public SetupDataLoader(
            UserService userService,
            CommentService commentService,
            GroupService groupService,
            AssignService assignService,
            SpectatorService spectatorService,
            TaskService taskService
            ) {
        this.userService = userService;
        this.commentService = commentService;
        this.groupService = groupService;
        this.assignService = assignService;
        this.spectatorService = spectatorService;
        this.taskService = taskService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            // Create a user
            User user = new User();
            user.setUsername("Rafail");
            user.setEmail("rafail@gmail.gr");

            // Create a comment
            Comment comment = new Comment();
            comment.setText("This is a test text");
            comment.setCreatedBy(user);

            // Create a group
            Group group = new Group();
            group.setName("test group");
            group.setDescription("This is a test group");
            group.setRealm("My Realm");
            group.setUsers(List.of(user));
            user.setGroups(List.of(group));

            // Create an assign
            Assign assign = new Assign();
            assign.setUser(user);
            assign.setGroup(group);

            // Create a spectator
            Spectator spectator = new Spectator();
            spectator.setUser(user);
            spectator.setGroup(group);

            // Create a task
            Task task = new Task();
            task.setName("test task");
            task.setRealm("test realm");
            task.setStatus("created");
            task.setCreatedBy(user);
            task.setComments(List.of(comment));
            task.setAssigns(List.of(assign));
            task.setSpectators(List.of(spectator));
            comment.setTask(task);
            assign.setTask(task);
            spectator.setTask(task);

            // persist
            taskService.addTask(task);
            userService.addUser(user);
            commentService.addComment(comment);
            groupService.addGroup(group);
            assignService.addAssign(assign);
            spectatorService.addSpectator(spectator);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }
    }
}
