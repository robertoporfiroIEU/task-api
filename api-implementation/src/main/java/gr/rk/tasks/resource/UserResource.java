package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.UsersApi;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserResource implements UsersApi {

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Secured("ROLE_READ_USER")
    public ResponseEntity<List<UserDTO>> getUsers(String name) {
        return ResponseEntity.ok(userService.getUsers(name));
    }
}
