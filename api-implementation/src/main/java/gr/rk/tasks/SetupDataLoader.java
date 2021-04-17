package gr.rk.tasks;

import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
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

    @Autowired
    public SetupDataLoader(UserService userService, CommentService commentService, GroupService groupService) {
        this.userService = userService;
        this.commentService = commentService;
        this.groupService = groupService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            User user = new User();
            user.setUsername("Rafail");
            user.setEmail("rafail@gmail.gr");
            userService.addUser(user);

            Comment comment = new Comment();
            comment.setText("This is a test text");
            comment.setCreatedBy(user);
            commentService.addComment(comment);

            Group group = new Group();
            group.setName("test group");
            group.setDescription("This is a test group");
            group.setRealm("My Realm");
            group.setUsers(new ArrayList<>());
            groupService.addUsersToGroup(group, List.of(user));

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }

    }
}
