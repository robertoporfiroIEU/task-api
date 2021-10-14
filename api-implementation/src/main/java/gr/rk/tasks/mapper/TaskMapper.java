package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.entity.Task;
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

@Mapper(componentModel = "spring", imports = { Util.class, UUID.class })
public abstract class TaskMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;

    public Page<TaskDTO> toPageTaskDTO(Page<Task> tasksEntity) {
        return new PageImpl<>(toTasksDTO(tasksEntity), tasksEntity.getPageable(), tasksEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(target = "dueDate", expression = "java(Util.toLocalDateTimeFromISO8601WithTimeZone(taskDTO.getDueDate()))")
    @Mapping(target = "createdBy", expression = "java(userRepository.findById(taskDTO.getCreatedBy().getName()).get())")
    @Mapping(ignore = true, target = "identifier")
    public abstract Task toTask(TaskDTO taskDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(task.getIdentifier()))")
    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(task.getCreatedAt()))")
    @Mapping(target = "realm", expression = "java(task.getApplicationUser())")
    @Mapping(target = "commentsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/comments\"))")
    @Mapping(target = "assignsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/assigns\"))")
    @Mapping(target = "spectatorsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/spectators\"))")
    public abstract TaskDTO toTaskDTO(Task task);

    protected abstract List<TaskDTO> toTasksDTO(Page<Task> tasks);
}
