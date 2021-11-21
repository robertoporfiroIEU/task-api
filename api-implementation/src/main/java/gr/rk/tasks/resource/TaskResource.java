package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.dto.AssignDTO;
import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.V1.dto.SpectatorDTO;
import gr.rk.tasks.V1.dto.TaskDTO;
import gr.rk.tasks.entity.Assign;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Spectator;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.mapper.AssignMapper;
import gr.rk.tasks.mapper.CommentMapper;
import gr.rk.tasks.mapper.SpectatorMapper;
import gr.rk.tasks.mapper.TaskMapper;
import gr.rk.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class TaskResource implements TasksApi {

    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;
    private final TaskService taskService;
    private final AssignMapper assignMapper;
    private final SpectatorMapper spectatorMapper;

    @Autowired
    public TaskResource(
            TaskMapper taskMapper,
            TaskService taskService,
            CommentMapper commentMapper,
            AssignMapper assignMapper,
            SpectatorMapper spectatorMapper
    ) {
        this.taskMapper = taskMapper;
        this.taskService = taskService;
        this.commentMapper = commentMapper;
        this.assignMapper = assignMapper;
        this.spectatorMapper = spectatorMapper;
    }

    @Override
    public ResponseEntity<Page<TaskDTO>> getTasks(
            Pageable pageable,
            String identifier,
            String projectIdentifier,
            String name,
            String status,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String dueDateFrom,
            String dueDateTo) {

        List<TaskDTO> tasksDTO = new ArrayList<>();
        Page<TaskDTO> tasksDTOPage = new PageImpl<>(tasksDTO);

        Page<Task> tasksEntity = taskService.getTasks(
                pageable,
                identifier,
                name,
                status,
                creationDateFrom,
                creationDateTo,
                createdBy,
                dueDateFrom,
                dueDateTo
        );

        if (!tasksEntity.isEmpty()) {
            tasksDTOPage = taskMapper.toPageTasksDTO(tasksEntity);
        }

        return ResponseEntity.ok(tasksDTOPage);
    }

    @Override
    public ResponseEntity<TaskDTO> getTask(String identifier) {
        TaskDTO taskDTO = new TaskDTO();
        Optional<Task> oTaskEntity = taskService.getTask(identifier);

        if (oTaskEntity.isPresent()) {
            taskDTO = taskMapper.toTaskDTO(oTaskEntity.get());
        }

        return ResponseEntity.ok(taskDTO);
    }

    @Override
    public ResponseEntity<TaskDTO> createTask(TaskDTO taskDTO) {
        Task taskEntity = taskMapper.toTask(taskDTO);
        taskEntity = taskService.createTask(taskEntity, taskDTO.getCreatedBy().getName(), taskDTO.getProjectIdentifier());

        // Get taskDTO with information that exists in the Task entity
        TaskDTO taskDTOResponse = taskMapper.toTaskDTO(taskEntity);
        return ResponseEntity.ok(taskDTOResponse);
    }

    @Override
    public ResponseEntity<Page<CommentDTO>> getComments(String identifier,  Pageable pageable) {
        List<CommentDTO> commentsDTO = new ArrayList<>();
        Page<CommentDTO> commentsDTOPage= new PageImpl<>(commentsDTO);

        Page<Comment> commentsEntity = taskService.getComments(identifier, pageable);

        if (!commentsEntity.isEmpty()) {
            commentsDTOPage = commentMapper.toPageCommentsDTO(commentsEntity);
        }

        return ResponseEntity.ok(commentsDTOPage);
    }

    @Override
    public ResponseEntity<CommentDTO> addTaskComment(String identifier, CommentDTO commentDTO) {
        Comment commentEntity = commentMapper.toComment(commentDTO);
        commentEntity = taskService.addTaskComment(identifier, commentEntity, commentDTO.getCreatedBy().getName());

        // Get commentDTO with information that exists in the Comment entity
        CommentDTO commentDTOResponse = commentMapper.toCommentDTO(commentEntity);

        return ResponseEntity.ok(commentDTOResponse);
    }

    @Override
    public ResponseEntity<Page<AssignDTO>> getAssigns(String identifier,  Pageable pageable) {
        List<AssignDTO> assignsDTO = new ArrayList<>();
        Page<AssignDTO> assignsDTOPage = new PageImpl<>(assignsDTO);

        Page<Assign> assignsEntity = taskService.getAssigns(identifier, pageable);

        if (!assignsEntity.isEmpty()) {
            assignsDTOPage = assignMapper.toPageAssignsDTO(assignsEntity);
        }

        return ResponseEntity.ok(assignsDTOPage);
    }

    @Override
    public ResponseEntity<AssignDTO> addAssign(String identifier, AssignDTO assignDTO) {
        Assign assignEntity = assignMapper.toAssign(assignDTO);
        assignEntity = taskService.addAssign(identifier, assignEntity);

        AssignDTO assignDTOResponse = assignMapper.toAssignDTO(assignEntity);

        return ResponseEntity.ok(assignDTOResponse);
    }

    @Override
    public ResponseEntity<SpectatorDTO> addSpectator(String identifier, SpectatorDTO spectatorDTO) {
        Spectator spectatorEntity = spectatorMapper.toSpectator(spectatorDTO);
        spectatorEntity = taskService.addSpectator(identifier, spectatorEntity);

        SpectatorDTO spectatorDTOResponse = spectatorMapper.toSpectatorDTO(spectatorEntity);

        return ResponseEntity.ok(spectatorDTOResponse);
    }

    @Override
    public ResponseEntity<Page<SpectatorDTO>> getSpectators(String identifier, Pageable pageable) {
        List<SpectatorDTO> spectatorsDTO = new ArrayList<>();
        Page<SpectatorDTO> spectatorsDTOPage = new PageImpl<>(spectatorsDTO);

        Page<Spectator> spectatorsEntity = taskService.getSpectators(identifier, pageable);

        if (!spectatorsEntity.isEmpty()) {
            spectatorsDTOPage = spectatorMapper.toPageSpectatorDTO(spectatorsEntity);
        }

        return ResponseEntity.ok(spectatorsDTOPage);
    }

    @Override
    public ResponseEntity<Void> deleteTask(String identifier) {
        taskService.deleteTaskLogical(identifier);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteComment(String taskIdentifier, String commentIdentifier) {
        taskService.deleteCommentLogical(taskIdentifier, commentIdentifier);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteAssign(String taskIdentifier, String assignIdentifier) {
        taskService.deleteAssignLogical(taskIdentifier, assignIdentifier);
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<Void> deleteSpectator(String taskIdentifier, String spectatorIdentifier) {
        taskService.deleteSpectatorLogical(taskIdentifier, spectatorIdentifier);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page<TaskDTO>> getHistory(String identifier,  Pageable pageable,  String identifier2, String newHashCode, String changedBy) {
        return null;
    }
}
