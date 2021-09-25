package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TaskMapper {

    private UserPrincipal userPrincipal;

    @Autowired
    public TaskMapper(UserPrincipal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public TaskDTO toTaskDTO(Task taskEntity) {
        TaskDTO task = new TaskDTO();

        task.setIdentifier(UUID.fromString(taskEntity.getIdentifier()));
        task.setName(taskEntity.getName());
        task.setDescription(taskEntity.getDescription());
        task.setStatus(taskEntity.getStatus());
        task.creationDate(Util.toDateISO8601WithTimeZone(taskEntity.getCreatedAt()));

        if (Objects.nonNull(taskEntity.getCreatedBy())) {
            UserDTO userDTO = new UserDTO();
            task.createdBy(userDTO.name(taskEntity.getCreatedBy().getUsername()));
        }

        task.realm(taskEntity.getApplicationUser());

        if (Objects.nonNull(taskEntity.getDueDate())) {
            task.setDueDate(taskEntity.toString());
        }

        task.setCommentsUrl(Util.getEndPointRelationURL(task.getIdentifier() + "/comments"));
        task.setAssignsUrl(Util.getEndPointRelationURL(task.getIdentifier() + "/assigns"));
        task.setSpectatorsUrl(Util.getEndPointRelationURL(task.getIdentifier() + "/spectators"));
        return task;
    }

    public Page<TaskDTO> toPageTaskDTO(Page<Task> tasksEntity) {
        List<TaskDTO> tasksDTO = new ArrayList<>();
        tasksEntity.forEach( task -> {
            tasksDTO.add(toTaskDTO(task));
        });
        return new PageImpl<>(tasksDTO, tasksEntity.getPageable(), tasksEntity.getTotalElements());
    }
}
