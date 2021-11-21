package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.UsersApi;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.mapper.UserMapper;
import gr.rk.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserResource implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserResource(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<Page<UserDTO>> getUsers(Pageable pageable, String name, String email) {
        List<UserDTO> usersDTO = new ArrayList<>();
        Page<UserDTO> usersDTOPage = new PageImpl<>(usersDTO);

        Page<User> usersEntity = userService.getUsers(pageable, name, email);

        if (!usersEntity.isEmpty()) {
            usersDTOPage = userMapper.toPageUsersDTO(usersEntity);
        }
        return ResponseEntity.ok(usersDTOPage);
    }

    @Override
    public ResponseEntity<UserDTO> getUser(String name) {
        UserDTO userDTO = new UserDTO();
        Optional<User> oUseEntity = userService.getUser(name);

        if (oUseEntity.isPresent()) {
            userDTO = userMapper.toUserDTO(oUseEntity.get());
        }

        return ResponseEntity.ok(userDTO);
    }

    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
        User userEntity = userMapper.toUser(userDTO);
        userEntity = userService.addUser(userEntity);

        UserDTO userDTOResponse = userMapper.toUserDTO(userEntity);
        return ResponseEntity.ok(userDTOResponse);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String name) {
       userService.deleteUserLogical(name);
       return ResponseEntity.ok().build();
    }

}
