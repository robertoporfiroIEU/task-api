package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.ProjectDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Project;
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

@Mapper(componentModel = "spring", imports =  { Util.class })
public abstract class ProjectMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;

    public Page<ProjectDTO> toPageProjectsDTO(Page<Project> projectEntity) {
        return new PageImpl<>(toProjectsDTO(projectEntity), projectEntity.getPageable(), projectEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(target = "createdBy", expression = "java(userRepository.findById(projectDTO.getCreatedBy().getName()).orElse(null))")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "tasks")
    @Mapping(ignore = true, target = "identifier")
    public abstract Project toProject(ProjectDTO projectDTO);

    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(project.getCreatedAt()))")
    @Mapping(target = "realm", expression = "java(project.getApplicationUser())")
    public abstract ProjectDTO toProjectDTO(Project project);

    @Mapping(target = "name", source = "username")
    public abstract UserDTO toUserDTO(User user);

    protected abstract List<ProjectDTO> toProjectsDTO(Page<Project> projects);
}
