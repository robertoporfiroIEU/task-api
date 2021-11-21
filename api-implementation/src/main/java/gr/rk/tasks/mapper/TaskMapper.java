package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", imports = { Util.class }, uses = { UserMapper.class })
public abstract class TaskMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public Page<TaskDTO> toPageTasksDTO(Page<Task> tasksEntity) {
        return new PageImpl<>(toTasksDTOList(tasksEntity), tasksEntity.getPageable(), tasksEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(target = "dueDate", expression = "java(Util.toLocalDateTimeFromISO8601WithTimeZone(taskDTO.getDueDate()))")
    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "comments")
    @Mapping(ignore = true, target = "assigns")
    @Mapping(ignore = true, target = "spectators")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "project")
    public abstract Task toTask(TaskDTO taskDTO);


    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(task.getCreatedAt()))")
    @Mapping(target = "dueDate", expression = "java(Util.toDateISO8601WithTimeZone(task.getDueDate()))")
    @Mapping(target = "realm", expression = "java(task.getApplicationUser())")
    @Mapping(target = "projectIdentifier", expression = "java(task.getProject().getPrefixIdentifier())")
    @Mapping(target = "commentsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/comments\"))")
    @Mapping(target = "assignsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/assigns\"))")
    @Mapping(target = "spectatorsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/spectators\"))")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "toUserDTO")
    public abstract TaskDTO toTaskDTO(Task task);

    protected abstract List<TaskDTO> toTasksDTOList(Page<Task> tasks);
}
