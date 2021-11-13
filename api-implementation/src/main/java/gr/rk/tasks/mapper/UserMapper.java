package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.security.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public Page<UserDTO> toPageUsersDTO(Page<User> usersEntity) {
        return new PageImpl<>(toUsersDTO(usersEntity), usersEntity.getPageable(), usersEntity.getTotalElements());
    }

    protected abstract List<UserDTO> toUsersDTO(Page<User> users);

    @Mapping(target = "username", source = "name")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    public abstract User toUser(UserDTO userDTO);

    @Mapping(target = "name", source = "username")
    public abstract UserDTO toUserDTO(User user);
}
