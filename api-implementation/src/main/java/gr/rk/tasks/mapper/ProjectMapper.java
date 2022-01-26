package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.PaginatedProjectsDTO;
import gr.rk.tasks.V1.dto.ProjectDTO;
import gr.rk.tasks.entity.Project;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", imports =  { Util.class }, uses = { UserMapper.class })
public abstract class ProjectMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public PaginatedProjectsDTO toPaginatedProjectsDTO(Page<Project> projectEntity) {
        return new PaginatedProjectsDTO()
                .content(toProjectsDTOList(projectEntity))
                .totalElements((int)projectEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "tasks")
    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "deleted")
    public abstract Project toProject(ProjectDTO projectDTO);

    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "toUserDTO")
    @Mapping(target = "createdAt", expression = "java(Util.toDateISO8601WithTimeZone(project.getCreatedAt()))")
    @Mapping(target = "realm", expression = "java(project.getApplicationUser())")
    public abstract ProjectDTO toProjectDTO(Project project);

    protected abstract List<ProjectDTO> toProjectsDTOList(Page<Project> projects);
}
