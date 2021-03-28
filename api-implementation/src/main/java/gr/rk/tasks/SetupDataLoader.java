package gr.rk.tasks;

import gr.rk.tasks.entity.User;
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

    @Autowired
    public SetupDataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            User user = new User();
            user.setUsername("Rafail");
            user.setEmail("rafail@gmail.gr");
            userService.addUser(user);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }

    }
}
