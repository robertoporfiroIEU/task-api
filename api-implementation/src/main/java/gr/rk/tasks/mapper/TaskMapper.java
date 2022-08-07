package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.PaginatedTasksDTO;
import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", imports = { Util.class }, uses = { AssignMapper.class, SpectatorMapper.class })
public abstract class TaskMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public PaginatedTasksDTO toPaginatedTasksDTO(Page<Task> tasksEntity) {
        return new PaginatedTasksDTO()
                .content(toTasksDTOList(tasksEntity))
                .totalElements((int) tasksEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getClientName())")
    @Mapping(target = "dueDate", expression = "java(Util.toLocalDateTimeFromISO8601WithTimeZone(taskDTO.getDueDate()))")
    @Mapping(source = "label", target = "label")
    @Mapping(source = "priority", target = "priority")
    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "comments")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "project")
    public abstract Task toTask(TaskDTO taskDTO);


    @Mapping(target = "createdAt", expression = "java(Util.toDateISO8601WithTimeZone(task.getCreatedAt()))")
    @Mapping(target = "dueDate", expression = "java(Util.toDateISO8601WithTimeZone(task.getDueDate()))")
    @Mapping(target = "applicationUser", expression = "java(task.getApplicationUser())")
    @Mapping(target = "projectIdentifier", expression = "java(task.getProject().getIdentifier())")
    @Mapping(target = "commentsUrl", expression = "java(Util.getEndPointRelationURL(task.getIdentifier() + \"/comments\"))")
    @Mapping(target = "createdBy", source = "createdBy")
    public abstract TaskDTO toTaskDTO(Task task);

    protected abstract List<TaskDTO> toTasksDTOList(Page<Task> tasks);
}
