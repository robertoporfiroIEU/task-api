package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.ProjectDTO;
import gr.rk.tasks.entity.Project;
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

@Mapper(componentModel = "spring", imports =  { Util.class, UUID.class })
public abstract class ProjectMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;

    public Page<ProjectDTO> toPageProjectsDTO(Page<Project> projectEntity) {
        return new PageImpl<>(toProjectsDTO(projectEntity), projectEntity.getPageable(), projectEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(target = "createdBy", expression = "java(userRepository.findById(projectDTO.getCreatedBy().getName()).get())")
    @Mapping(ignore = true, target = "identifier")
    public abstract Project toProject(ProjectDTO projectDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(project.getIdentifier()))")
    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(project.getCreatedAt()))")
    @Mapping(target = "realm", expression = "java(project.getApplicationUser())")
    public abstract ProjectDTO toProjectDTO(Project project);

    protected abstract List<ProjectDTO> toProjectsDTO(Page<Project> projects);
}
