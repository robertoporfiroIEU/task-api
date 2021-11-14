package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.GroupDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;

@Mapper(componentModel = "spring", imports =  { Util.class } )
public abstract class GroupMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;

    public Page<GroupDTO> toPageGroupsDTO(Page<Group> groupEntity) {
        return new PageImpl<>(toGroupsDTOList(groupEntity), groupEntity.getPageable(), groupEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(ignore = true, target = "users")
    @Mapping(ignore = true, target = "assigns")
    @Mapping(ignore = true, target = "spectators")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    public abstract Group toGroup(GroupDTO groupDTO);

    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(group.getCreatedAt()))")
    @Mapping(target = "realm", expression = "java(group.getApplicationUser())")
    public abstract GroupDTO toGroupDTO(Group group);

    protected abstract List<GroupDTO> toGroupsDTOList(Page<Group> groups);

    @Mapping(target = "name", source = "username")
    @Mapping(ignore = true, target = "groups")
    public abstract UserDTO toUserDTO(User user);
}
