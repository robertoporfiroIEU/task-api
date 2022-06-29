package gr.rk.tasks.service;

import gr.rk.tasks.entity.User;
import gr.rk.tasks.exception.ApplicationException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.userService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public UserService(UserRepository userRepository, UserPrincipal userPrincipal) {
        this.userRepository = userRepository;
        this.userPrincipal = userPrincipal;
    }

    public Page<User> getUsers(Pageable pageable, String name, String email) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return userRepository.findUsersDynamicJPQL(page, name, email, userPrincipal.getClientName());
    }

    public Optional<User> getUser(String name) {
        return userRepository.findByUsernameAndApplicationUserAndDeleted(name, userPrincipal.getClientName(), false);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Transactional
    public void deleteUserLogical(String name) {
        Optional<User> oUser = userRepository.findByUsernameAndApplicationUserAndDeleted(name, userPrincipal.getClientName(), false);

        if (oUser.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
        }

        User user = oUser.get();
        user.setDeleted(true);
    }

}
