package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.util.Util;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TaskMapper {

    public TaskDTO toTaskDTO(Task taskEntity) {
        TaskDTO task = new TaskDTO();

        task.setIdentifier(UUID.fromString(taskEntity.getIdentifier()));
        task.setName(taskEntity.getName());
        task.setDescription(taskEntity.getDescription());
        task.setStatus(taskEntity.getStatus());
        task.creationDate(taskEntity.getCreatedAt().toString());

        if (Objects.nonNull(taskEntity.getCreatedBy())) {
            UserDTO userDTO = new UserDTO();
            task.createdBy(userDTO.name(taskEntity.getCreatedBy().getUsername()));
        }

        task.realm(taskEntity.getApplicationUser());

        if (Objects.nonNull(taskEntity.getDueDate())) {
            task.setDueDate(taskEntity.toString());
        }

        task.setCommentsUrl(Util.getEndPointRelationURL("comments"));
        task.setAssignsUrl(Util.getEndPointRelationURL("assigns"));
        task.setSpectatorsUrl(Util.getEndPointRelationURL("spectators"));
        return task;
    }
}
