package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.GroupDTO;
import gr.rk.tasks.V1.dto.PaginatedGroupsDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", imports =  { Util.class } )
public abstract class GroupMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public PaginatedGroupsDTO toPaginatedGroupsDTO(Page<Group> groupEntity) {
        return new PaginatedGroupsDTO()
                .content(toGroupsDTOList(groupEntity))
                .totalElements((int)groupEntity.getTotalElements());
    }

    @Named("toGroup")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getClientName())")
    @Mapping(ignore = true, target = "users")
    @Mapping(ignore = true, target = "assigns")
    @Mapping(ignore = true, target = "spectators")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    public abstract Group toGroup(GroupDTO groupDTO);

    @Mapping(target = "createdAt", expression = "java(Util.toDateISO8601WithTimeZone(group.getCreatedAt()))")
    @Mapping(target = "applicationUser", expression = "java(group.getApplicationUser())")
    @Mapping(target = "users", source = "users")
    public abstract GroupDTO toGroupDTO(Group group);

    protected abstract List<GroupDTO> toGroupsDTOList(Page<Group> groups);

    @Named("toUserDTOWithoutGroup")
    @Mapping(target = "name", source = "username")
    @Mapping(ignore = true, target = "groups")
    protected abstract UserDTO toUserDTO(User user);
}
