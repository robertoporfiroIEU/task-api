package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.AssignDTO;
import gr.rk.tasks.entity.Assign;
import gr.rk.tasks.repository.GroupRepository;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports =  { Util.class, UUID.class }, uses = { GroupMapper.class, UserMapper.class })
public abstract class AssignMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected GroupRepository groupRepository;

    public Page<AssignDTO> toPageAssignsDTO(Page<Assign> assignsEntity) {
        return new PageImpl<>(toAssignsDTO(assignsEntity), assignsEntity.getPageable(), assignsEntity.getTotalElements());
    }

    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "assignDate")
    @Mapping(target = "user", expression = "java(userRepository.findById(assignDTO.getUser().getName()).orElse(null))")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    public abstract Assign toAssign(AssignDTO assignDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(assign.getIdentifier()))")
    @Mapping(target = "assignDate", expression = "java(Util.toDateISO8601WithTimeZone(assign.getAssignDate()))")
    public abstract AssignDTO toAssignDTO(Assign assign);

    protected abstract List<AssignDTO> toAssignsDTO(Page<Assign> assigns);
}
