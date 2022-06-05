package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.dto.*;
import gr.rk.tasks.dto.TaskCriteriaDTO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    public ResponseEntity<PaginatedTasksDTO> getTasks(
            Pageable pageable,
            String identifier,
            String projectIdentifier,
            String name,
            String status,
            String priority,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String assignedTo,
            String spectator,
            String dueDateFrom,
            String dueDateTo) {

        TaskCriteriaDTO taskCriteriaDTO = new TaskCriteriaDTO();
        taskCriteriaDTO.setPageable(pageable);
        taskCriteriaDTO.setIdentifier(identifier);
        taskCriteriaDTO.setProjectIdentifier(projectIdentifier);
        taskCriteriaDTO.setName(name);
        taskCriteriaDTO.setStatus(status);
        taskCriteriaDTO.setPriority(priority);
        taskCriteriaDTO.setCreationDateFrom(creationDateFrom);
        taskCriteriaDTO.setCreationDateTo(creationDateTo);
        taskCriteriaDTO.setCreatedBy(createdBy);
        taskCriteriaDTO.setAssignedTo(assignedTo);
        taskCriteriaDTO.setSpectator(spectator);
        taskCriteriaDTO.setDueDateFrom(dueDateFrom);
        taskCriteriaDTO.setDueDateTo(dueDateTo);

        Page<Task> tasksEntity = taskService.getTasks(taskCriteriaDTO);

        PaginatedTasksDTO paginatedTasksDTO = new PaginatedTasksDTO()
                .content(new ArrayList<>())
                .totalElements(0);

        if (!tasksEntity.isEmpty()) {
            paginatedTasksDTO = taskMapper.toPaginatedTasksDTO(tasksEntity);
        }

        return ResponseEntity.ok(paginatedTasksDTO);
    }

    @Override
    public ResponseEntity<TaskDTO> updateTask(String identifier, TaskDTO taskDTO) {
        Task task = this.taskMapper.toTask(taskDTO);
        Task taskEntity = this.taskService.updateTask(identifier, task);
        taskDTO = this.taskMapper.toTaskDTO(taskEntity);
        return ResponseEntity.ok(taskDTO);
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
    public ResponseEntity<PaginatedCommentsDTO> getComments(String identifier, Pageable pageable) {

        Page<Comment> commentsEntity = taskService.getComments(identifier, pageable);

        PaginatedCommentsDTO paginatedCommentsDTO = new PaginatedCommentsDTO()
                .content(new ArrayList<>())
                .totalElements(0);

        if (!commentsEntity.isEmpty()) {
            paginatedCommentsDTO = commentMapper.toPaginatedCommentsDTO(commentsEntity);
        }

        return ResponseEntity.ok(paginatedCommentsDTO);
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
    public ResponseEntity<PaginatedAssignsDTO> getAssigns(String identifier,  Pageable pageable) {

        Page<Assign> assignsEntity = taskService.getAssigns(identifier, pageable);

        PaginatedAssignsDTO paginatedAssignsDTO = new PaginatedAssignsDTO()
                .content(new ArrayList<>())
                .totalElements(0);

        if (!assignsEntity.isEmpty()) {
            paginatedAssignsDTO = assignMapper.toPaginatedAssignsDTO(assignsEntity);
        }

        return ResponseEntity.ok(paginatedAssignsDTO);
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
    public ResponseEntity<PaginatedSpectatorsDTO> getSpectators(String identifier, Pageable pageable) {
        Page<Spectator> spectatorsEntity = taskService.getSpectators(identifier, pageable);

        PaginatedSpectatorsDTO paginatedSpectatorsDTO = new PaginatedSpectatorsDTO()
                .content(new ArrayList<>())
                .totalElements(0);

        if (!spectatorsEntity.isEmpty()) {
            paginatedSpectatorsDTO = spectatorMapper.toPaginatedSpectatorsDTO(spectatorsEntity);
        }

        return ResponseEntity.ok(paginatedSpectatorsDTO);
    }

    @Override
    public ResponseEntity<Void> deleteTask(String identifier) {
        taskService.deleteTaskLogical(identifier);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CommentDTO> updateComment(String taskIdentifier, String commentIdentifier, CommentDTO commentDTO) {
        Comment commentEntity = commentMapper.toComment(commentDTO);
        commentEntity = taskService.updateComment(taskIdentifier, commentIdentifier, commentEntity);

        // Get the updated commentDTO with information that exists in the Comment entity
        CommentDTO commentDTOResponse = commentMapper.toCommentDTO(commentEntity);

        return ResponseEntity.ok(commentDTOResponse);
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
    public ResponseEntity<PaginatedHistoryDTO> getHistory(String identifier,  Pageable pageable,  String identifier2, String newHashCode, String changedBy) {
        return null;
    }
}
