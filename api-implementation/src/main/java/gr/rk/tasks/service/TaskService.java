package gr.rk.tasks.service;

import gr.rk.tasks.dto.TaskCriteriaDTO;
import gr.rk.tasks.entity.*;
import gr.rk.tasks.exception.*;
import gr.rk.tasks.exception.i18n.*;
import gr.rk.tasks.repository.*;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final ProjectRepository projectRepository;

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    private final AssignRepository assignRepository;

    private final SpectatorRepository spectatorRepository;

    private final UserService userService;

    private final AttachmentRepository attachmentRepository;

    private final UserPrincipal userPrincipal;

    private final GroupService groupService;

    @Value("${applicationConfigurations.taskService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            CommentRepository commentRepository,
            AssignRepository assignRepository,
            SpectatorRepository spectatorRepository,
            AttachmentRepository attachmentRepository,
            UserPrincipal userPrincipal,
            UserService userService,
            GroupService groupService
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.assignRepository = assignRepository;
        this.spectatorRepository = spectatorRepository;
        this.attachmentRepository = attachmentRepository;
        this.userPrincipal = userPrincipal;
        this.userService = userService;
        this.groupService = groupService;
    }

    @Transactional
    public Task createTask(Task task, String projectIdentifier) {

        Optional<Project> oProject = projectRepository
                .findProjectByIdentifierAndApplicationUserAndDeleted(projectIdentifier, userPrincipal.getClientName(), false);

        if (oProject.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        // happy path
        task.setCreatedBy(userPrincipal.getPropagatedUser());
        task.setProject(oProject.get());
        return taskRepository.saveTask(task);
    }

    public Page<Task> getTasks(TaskCriteriaDTO taskCriteriaDTO) {
        Pageable page = taskCriteriaDTO.getPageable();

        if (taskCriteriaDTO.getPageable().getPageSize() > maxSize) {
            page = PageRequest.of(taskCriteriaDTO.getPageable().getPageNumber(), maxSize, taskCriteriaDTO.getPageable().getSort());
        }

        taskCriteriaDTO.setApplicationUser(userPrincipal.getClientName());
        taskCriteriaDTO.setPageable(page);

        return taskRepository.findTasksDynamicJPQL(taskCriteriaDTO);
    }

    public Optional<Task> getTask(String taskIdentifier) {
        return taskRepository.findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);
    }

    public Page<Comment> getComments(String taskIdentifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return commentRepository.findCommentByTaskIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false, page);
    }

    @Transactional
    public Comment addTaskComment(String taskIdentifier, Comment comment) {
        Optional<Task> oTask = taskRepository
                .findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Set<String> attachmentIdentifiers = comment.getAttachments().stream()
                .map((attachment -> attachment.getIdentifier()))
                .collect(Collectors.toSet());

        Set<Attachment> attachments = attachmentRepository.findByIdentifierIn(attachmentIdentifiers);

        if (comment.getAttachments().size() != attachmentIdentifiers.size()) {
            throw new ApplicationException(I18nErrorMessage.ATTACHMENT_NOT_FOUND);
        }

        comment.setCreatedBy(userPrincipal.getPropagatedUser());
        comment.setAttachments(attachments);
        comment.setTask(oTask.get());

        return commentRepository.save(comment);
    }

    public Page<Assign> getAssigns(String taskIdentifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        List<Assign> assigns = assignRepository
                .findAssignByTaskIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false, page).toList();

        return new PageImpl<>(assigns, pageable, assigns.size());
    }

    @Transactional
    public Assign addAssign(String taskIdentifier, Assign assign) {
        // validations
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        if (Objects.isNull(assign.getUser()) && Objects.isNull(assign.getGroup())) {
            throw new ApplicationException(I18nErrorMessage.USER_AND_GROUP_NOT_FOUND);
        }

        Task task = oTask.get();

        if (Objects.nonNull(assign.getUser())) {
            boolean sameAssign = task.getAssigns()
                    .stream()
                    .anyMatch(a -> a.getUser().equals(assign.getUser()) && !a.isDeleted());

            if (sameAssign) {
                throw new ApplicationException(I18nErrorMessage.ASSIGN_ALREADY_EXIST);
            }

            if (!userService.isUserExist((assign.getUser()))) {
                throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
            }

            assign.setUser(assign.getUser());
        }

        if (Objects.nonNull(assign.getGroup())) {
            boolean sameAssign = task.getAssigns()
                    .stream()
                    .anyMatch(a -> a.getGroup().equals(assign.getGroup()) && !a.isDeleted());

            if (sameAssign) {
                throw new ApplicationException(I18nErrorMessage.ASSIGN_ALREADY_EXIST);
            }

            if (!groupService.isGroupExist(assign.getGroup())) {
                throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
            }

            assign.setGroup(assign.getGroup());
        }

        // happy path
        assign.setTask(task);
        task.getAssigns().add(assign);
        return assignRepository.save(assign);
    }

    @Transactional
    public Spectator addSpectator(String taskIdentifier, Spectator spectator) {
        // validations
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);
        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        if (Objects.isNull(spectator.getUser()) && Objects.isNull(spectator.getGroup())) {
            throw new ApplicationException(I18nErrorMessage.USER_AND_GROUP_NOT_FOUND);
        }

        Task task = oTask.get();

        if (Objects.nonNull(spectator.getUser())) {
            boolean sameSpectator = task.getSpectators()
                    .stream()
                    .anyMatch(s -> s.getUser().equals(spectator.getUser()));

            if (sameSpectator) {
                throw new ApplicationException(I18nErrorMessage.SPECTATOR_ALREADY_EXIST);
            }

            if (Objects.nonNull(spectator.getUser())) {

                if (!userService.isUserExist(spectator.getUser())) {
                    throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
                }

                spectator.setUser(spectator.getUser());
            }
        }

        if (Objects.nonNull(spectator.getGroup())) {

            boolean sameSpectator = task.getAssigns()
                    .stream()
                    .anyMatch(s -> s.getGroup().equals(spectator.getGroup()));

            if (sameSpectator) {
                throw new ApplicationException(I18nErrorMessage.SPECTATOR_ALREADY_EXIST);
            }

            if (!groupService.isGroupExist(spectator.getGroup())) {
                throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
            }

            spectator.setGroup(spectator.getGroup());
        }

        // happy path
        spectator.setTask(task);
        task.getSpectators().add(spectator);
        return spectatorRepository.save(spectator);
    }

    public Page<Spectator> getSpectators(String taskIdentifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        List<Spectator> spectators = spectatorRepository
                .findSpectatorByTaskIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false, page).toList();

        return new PageImpl<>(spectators, pageable, spectators.size());
    }

    @Transactional
    public void deleteTaskLogical(String taskIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Task task = oTask.get();
        task.setDeleted(true);
    }

    @Transactional
    public void deleteCommentLogical(String taskIdentifier, String commentIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Comment> oComment = commentRepository.
                findCommentByIdentifierAndApplicationUserAndDeleted(commentIdentifier, userPrincipal.getClientName(), false);

        if (oComment.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.COMMENT_NOT_FOUND);
        }

        Comment comment = oComment.get();
        if (Objects.nonNull(userPrincipal.getPropagatedUser()) && !comment.getCreatedBy().equals(userPrincipal.getPropagatedUser())){
            throw new ApplicationException(I18nErrorMessage.PROPAGATED_USER_IS_NOT_SAME);
        }

        comment.setDeleted(true);
    }

    @Transactional
    public void deleteAssignLogical(String taskIdentifier, String assignIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Assign> oAssign = assignRepository.
                findAssignByIdentifierAndApplicationUserAndDeleted(assignIdentifier, userPrincipal.getClientName(), false);

        if (oAssign.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.ASSIGN_NOT_FOUND);
        }

        Assign assign = oAssign.get();
        assign.setDeleted(true);
    }

    @Transactional
    public void deleteSpectatorLogical(String taskIdentifier, String spectatorIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Spectator> oSpectator = spectatorRepository.
                findSpectatorByIdentifierAndApplicationUserAndDeleted(spectatorIdentifier, userPrincipal.getClientName(), false);

        if (oSpectator.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.SPECTATOR_NOT_FOUND);
        }

        Spectator spectator = oSpectator.get();
        spectator.setDeleted(true);
    }

    public Task updateTask(Task task) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(task.getIdentifier(), userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Task taskEntity = oTask.get();
        taskEntity.setName(task.getName());
        taskEntity.setStatus(task.getStatus());
        taskEntity.setPriority(task.getPriority());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setAssigns(task.getAssigns());
        taskEntity.setSpectators(task.getSpectators());
        taskEntity.setDueDate(task.getDueDate());
        taskEntity.setDueDate(task.getDueDate());

        return taskRepository.save(taskEntity);
    }

    @Transactional
    public Comment updateComment(String taskIdentifier, Comment comment) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Comment> oComment = commentRepository.
                findCommentByIdentifierAndApplicationUserAndDeleted(comment.getIdentifier(), userPrincipal.getClientName(), false);

        if (oComment.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.COMMENT_NOT_FOUND);
        }

        Comment commentEntity = oComment.get();

        commentEntity.setText(comment.getText());

        Set<String> attachmentIdentifiers = comment.getAttachments().stream()
                .map((attachment -> attachment.getIdentifier()))
                .collect(Collectors.toSet());

        if (attachmentIdentifiers.size() == 0) {
            commentEntity.removeAllAttachments();
        } else {
            Set<Attachment> attachments = attachmentRepository.findByIdentifierIn(attachmentIdentifiers);
            commentEntity.removeAllAttachments();
            commentEntity.setAttachments(attachments);
        }

        return commentEntity;
    }
}
