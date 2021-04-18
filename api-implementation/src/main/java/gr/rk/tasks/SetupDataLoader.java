package gr.rk.tasks;

import gr.rk.tasks.entity.Assign;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.service.AssignService;
import gr.rk.tasks.service.CommentService;
import gr.rk.tasks.service.GroupService;
import gr.rk.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final CommentService commentService;
    private final GroupService groupService;
    private final AssignService assignService;

    @Autowired
    public SetupDataLoader(
            UserService userService,
            CommentService commentService,
            GroupService groupService,
            AssignService assignService
            ) {
        this.userService = userService;
        this.commentService = commentService;
        this.groupService = groupService;
        this.assignService = assignService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            // Create a user
            User user = new User();
            user.setUsername("Rafail");
            user.setEmail("rafail@gmail.gr");
            userService.addUser(user);

            // Create a comment
            Comment comment = new Comment();
            comment.setText("This is a test text");
            comment.setCreatedBy(user);
            commentService.addComment(comment);

            // Create a group
            Group group = new Group();
            group.setName("test group");
            group.setDescription("This is a test group");
            group.setRealm("My Realm");
            group.setUsers(List.of(user));
            user.setGroups(List.of(group));
            groupService.addGroup(group);

            // Create an assign
            Assign assign = new Assign();
            // Connect entities
            assign.setUser(user);
            assign.setGroup(group);
            assignService.addAssign(assign);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }

    }
}
