package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.dto.AssignDTO;
import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.V1.dto.SpectatorDTO;
import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.mapper.TaskMapper;
import gr.rk.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@Validated
public class TaskResource implements TasksApi {

    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @Autowired
    public TaskResource(TaskMapper taskMapper, TaskService taskService) {
        this.taskMapper = taskMapper;
        this.taskService = taskService;
    }

    @Override
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @Valid Pageable pageable,
            @Valid String identifier,
            @Valid String name,
            @Valid String status,
            @Pattern(regexp = "^([0-9]+)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])[Tt]([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]|60)(\\.[0-9]+)?(([Zz])|([\\+|\\-]([01][0-9]|2[0-3]):[0-5][0-9]))$")
            @Valid String creationDate,
            @Valid String createdBy,
            @Pattern(regexp = "^([0-9]+)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])[Tt]([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]|60)(\\.[0-9]+)?(([Zz])|([\\+|\\-]([01][0-9]|2[0-3]):[0-5][0-9]))$")
            @Valid String dueDate) {

        List<TaskDTO> tasksDTO = new ArrayList<>();
        Page<TaskDTO> tasksDTOPage= new PageImpl<>(tasksDTO);

        Page<Task> tasksEntity = taskService.getTasks(
                pageable,
                identifier,
                name,
                status,
                creationDate,
                createdBy,
                dueDate
        );

        if (!tasksEntity.isEmpty()) {
            tasksDTOPage = taskMapper.toPageTaskDTO(tasksEntity);
        }

        return ResponseEntity.ok(tasksDTOPage);
    }

    @Override
    public ResponseEntity<TaskDTO> getTask(UUID identifier) {
        TaskDTO taskDTO = new TaskDTO();
        Optional<Task> oTaskEntity = taskService.getTask(identifier.toString());

        if (oTaskEntity.isPresent()) {
            taskDTO = taskMapper.toTaskDTO(oTaskEntity.get());
        }

        return ResponseEntity.ok(taskDTO);
    }

    @Override
    public ResponseEntity<AssignDTO> addAssign(UUID identifier, @Valid AssignDTO assign) {
        return null;
    }

    @Override
    public ResponseEntity<SpectatorDTO> addSpectator(UUID identifier, @Valid SpectatorDTO spectator) {
        return null;
    }

    @Override
    public ResponseEntity<CommentDTO> addTaskComment(UUID identifier, @Valid CommentDTO comment) {
        return null;
    }

    @Override
    public ResponseEntity<TaskDTO> createTask(TaskDTO taskDTO) {
        return null;
    }

    @Override
    public ResponseEntity<Page<TaskDTO>> getAssigns(UUID identifier, @Valid Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<Page<CommentDTO>> getComments(UUID identifier, @Valid Pageable pageable) {
        List<CommentDTO> commentsDTO = new ArrayList<>();
        Page<CommentDTO> commentsDTOPage= new PageImpl<>(commentsDTO);

        Page<Comment> commentsEntity = taskService.getComments(identifier, pageable);

        if (!commentsEntity.isEmpty()) {
            commentsDTOPage = taskMapper.toPageCommentDTO(commentsEntity);
        }

        return ResponseEntity.ok(commentsDTOPage);
    }

    @Override
    public ResponseEntity<Page<SpectatorDTO>> getSpectators(UUID identifier, @Valid Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<Page<TaskDTO>> getHistory(UUID identifier, @Valid Pageable pageable, @Valid String identifier2, @Valid String newHashCode, @Valid String changedBy) {
        return null;
    }
}
