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
    private final TaskService taskService;

    @Autowired
    public SetupDataLoader(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            final String applicationUser = "TestApplication";
            // Create a user
            User user = new User();
            user.setUsername("Rafail");
            user.setEmail("rafail@gmail.gr");
            user.setApplicationUser(applicationUser);

            // Create a comment
            Comment comment = new Comment();
            comment.setText("This is a test text");
            comment.setCreatedBy(user);
            comment.setApplicationUser(applicationUser);

            // Create a group
            Group group = new Group();
            group.setName("test group");
            group.setDescription("This is a test group");
            group.setApplicationUser(applicationUser);

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
            task.setName("test task");
            task.setApplicationUser("Test Application");
            task.setStatus("created");
            task.setCreatedBy(user);
            comment.setTask(task);

            task.setComments(List.of(comment));
            task.setAssigns(List.of(assign));
            task.setSpectators(List.of(spectator));

            // persist
            taskService.addTask(task);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }
    }
}
