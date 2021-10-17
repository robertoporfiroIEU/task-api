package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.service.ProjectService;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", imports = { Util.class })
public abstract class TaskMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ProjectService projectService;

    public Page<TaskDTO> toPageTasksDTO(Page<Task> tasksEntity) {
        return new PageImpl<>(toTasksDTO(tasksEntity), tasksEntity.getPageable(), tasksEntity.getTotalElements());
    }

    @Mapping(target = "project", expression = "java(projectService.getProject(taskDTO.getProjectIdentifier().toString()).orElse(null))")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(target = "dueDate", expression = "java(Util.toLocalDateTimeFromISO8601WithTimeZone(taskDTO.getDueDate()))")
    @Mapping(target = "createdBy", expression = "java(userRepository.findById(taskDTO.getCreatedBy().getName()).get())")
    public abstract Task toTask(TaskDTO taskDTO);

    @Mapping(target = "identifier", expression = "java(task.getProject().getPrefixIdentifier() + \"-\" + task.getId())")
    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(task.getCreatedAt()))")
    @Mapping(target = "dueDate", expression = "java(Util.toDateISO8601WithTimeZone(task.getDueDate()))")
    @Mapping(target = "realm", expression = "java(task.getApplicationUser())")
    @Mapping(target = "projectIdentifier", expression = "java(task.getProject().getPrefixIdentifier() + \"-\" + task.getProject().getId())")
    @Mapping(target = "commentsUrl", expression = "java(Util.getEndPointRelationURL(taskDTO.getIdentifier() + \"/comments\"))")
    @Mapping(target = "assignsUrl", expression = "java(Util.getEndPointRelationURL(taskDTO.getIdentifier() + \"/assigns\"))")
    @Mapping(target = "spectatorsUrl", expression = "java(Util.getEndPointRelationURL(taskDTO.getIdentifier() + \"/spectators\"))")
    public abstract TaskDTO toTaskDTO(Task task);

    protected abstract List<TaskDTO> toTasksDTO(Page<Task> tasks);

    @Mapping(target = "name", source = "username")
    public abstract UserDTO toUserDTO(User user);
}
