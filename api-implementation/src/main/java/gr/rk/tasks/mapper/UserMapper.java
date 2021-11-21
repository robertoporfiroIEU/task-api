package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.GroupDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", imports =  { Util.class })
public abstract class UserMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public Page<UserDTO> toPageUsersDTO(Page<User> usersEntity) {
        return new PageImpl<>(toUsersDTOList(usersEntity), usersEntity.getPageable(), usersEntity.getTotalElements());
    }

    @Named("toUser")
    @Mapping(target = "username", source = "name")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "groups")
    @Mapping(ignore = true, target = "assigns")
    @Mapping(ignore = true, target = "spectators")
    public abstract User toUser(UserDTO userDTO);

    @IterableMapping(qualifiedByName = "toUserDTO")
    protected abstract List<UserDTO> toUsersDTOList(Page<User> users);

    @Named("toUserDTO")
    @Mapping(target = "name", source = "username")
    @Mapping(target = "groups", source = "groups", qualifiedByName = "groupDTOFromUser")
    public abstract UserDTO toUserDTO(User user);

    @Named("groupDTOFromUser")
    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(group.getCreatedAt()))")
    @Mapping(target = "realm", expression = "java(group.getApplicationUser())")
    @Mapping(ignore = true, target = "users")
    public abstract GroupDTO toGroupDTO(Group group);
}
