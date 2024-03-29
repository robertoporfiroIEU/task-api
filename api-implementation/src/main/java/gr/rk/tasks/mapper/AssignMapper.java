package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.AssignDTO;
import gr.rk.tasks.V1.dto.PaginatedAssignsDTO;
import gr.rk.tasks.entity.Assign;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED, imports =  { Util.class, UUID.class })
public abstract class AssignMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public PaginatedAssignsDTO toPaginatedAssignsDTO(Page<Assign> assignsEntity) {
        return new PaginatedAssignsDTO()
                .content(toAssignsDTOList(assignsEntity))
                .totalElements((int)assignsEntity.getTotalElements());
    }

    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "assignDate")
    @Mapping(ignore = true, target = "task")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "group", source = "group")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getClientName())")
    public abstract Assign toAssign(AssignDTO assignDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(assign.getIdentifier()))")
    @Mapping(target = "assignDate", expression = "java(Util.toDateISO8601WithTimeZone(assign.getAssignDate()))")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "group", source = "group")
    public abstract AssignDTO toAssignDTO(Assign assign);

    protected abstract List<AssignDTO> toAssignsDTOList(Page<Assign> assigns);
}
