package gr.rk.tasks;

import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.service.CommentService;
import gr.rk.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public SetupDataLoader(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
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

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }

    }
}
