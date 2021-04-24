package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.dto.AssignDTO;
import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.V1.dto.SpectatorDTO;
import gr.rk.tasks.V1.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;


@RestController
@Validated
public class TaskResource implements TasksApi {

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
        List<TaskDTO> tasks = List.of(new TaskDTO().name("test"));
        return ResponseEntity.ok(new PageImpl<>(tasks));
    }

    @Override
    public ResponseEntity<TaskDTO> getTask(UUID identifier) {

        TaskDTO task = new TaskDTO();

        task.setName("test");
        task.setIdentifier(UUID.randomUUID());
        task.setCommentsUrl(ServletUriComponentsBuilder.fromCurrentRequest().toUriString() + "/comments");
        task.setAssignsUrl(ServletUriComponentsBuilder.fromCurrentRequest().toUriString() + "/assigns");
        task.setSpectatorsUrl(ServletUriComponentsBuilder.fromCurrentRequest().toUriString() + "/spectators");

        return ResponseEntity.ok(task);
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
    public ResponseEntity<Page<TaskDTO>> getComments(UUID identifier, @Valid Pageable pageable, @Valid String identifier2, @Valid String createdBy) {
        return null;
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
