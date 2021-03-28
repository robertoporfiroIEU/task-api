package gr.rk.tasks.service;

import gr.rk.tasks.entity.User;
import gr.rk.tasks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(@Valid User user) throws ConstraintViolationException {
        userRepository.save(user);
    }
}
