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

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final AttachmentRepository attachmentRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.taskService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            CommentRepository commentRepository,
            AssignRepository assignRepository,
            SpectatorRepository spectatorRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            AttachmentRepository attachmentRepository,
            UserPrincipal userPrincipal
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.assignRepository = assignRepository;
        this.spectatorRepository = spectatorRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.attachmentRepository = attachmentRepository;
        this.userPrincipal = userPrincipal;
    }

    @Transactional
    public Task createTask(Task task, String createdBy, String projectIdentifier) {
        // validations
        Optional<User> oUser = userRepository
                .findByUsernameAndApplicationUserAndDeleted(createdBy, userPrincipal.getClientName(), false);
        if (oUser.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Optional<Project> oProject = projectRepository
                .findProjectByIdentifierAndApplicationUserAndDeleted(projectIdentifier, userPrincipal.getClientName(), false);
        if (oProject.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        // happy path
        task.setCreatedBy(oUser.get());
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
    public Comment addTaskComment(String taskIdentifier, Comment comment, String createdBy) {
        // validations
        Optional<User> oUser = userRepository
                .findByUsernameAndApplicationUserAndDeleted(createdBy, userPrincipal.getClientName(), false);

        if (Objects.nonNull(userPrincipal.getPropagatedUser()) && !createdBy.equals(userPrincipal.getPropagatedUser())){
            throw new ApplicationException(I18nErrorMessage.PROPAGATED_USER_IS_NOT_SAME);
        }

        if (oUser.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
        }

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

        comment.setCreatedBy(oUser.get());
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
                .findAssignByTaskIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false, page)
                .stream()
                // filter the assigns that have at least one non deleted element
                .filter(a -> {
                    if (Objects.nonNull(a.getUser()) && !a.getUser().isDeleted()) {
                        return true;
                    }

                    if (Objects.nonNull(a.getGroup()) && !a.getGroup().isDeleted()) {
                        return true;
                    }

                    return false;
                })
                // Remove deleted users and deleted groups
                .map(a -> {
                    if (Objects.nonNull(a.getUser())) {
                        if (a.getUser().isDeleted()) {
                            a.setUser(null);
                        }
                    }

                    if (Objects.nonNull(a.getGroup())) {
                        if (a.getGroup().isDeleted()) {
                            a.setGroup(null);
                        }
                    }
                    return a;
                })
                .collect(Collectors.toList());

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
                    .anyMatch(a -> a.getUser().getUsername().equals(assign.getUser().getUsername()) && !a.isDeleted());

            if (sameAssign) {
                throw new ApplicationException(I18nErrorMessage.ASSIGN_ALREADY_EXIST);
            }

            Optional<User> oUser = userRepository.findById(assign.getUser().getUsername());

            if (oUser.isEmpty()) {
                throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
            }

            assign.setUser(oUser.get());
        }

        if (Objects.nonNull(assign.getGroup())) {
            boolean sameAssign = task.getAssigns()
                    .stream()
                    .anyMatch(a -> a.getGroup().getName().equals(assign.getGroup().getName()) && !a.isDeleted());

            if (sameAssign) {
                throw new ApplicationException(I18nErrorMessage.ASSIGN_ALREADY_EXIST);
            }

            Optional<Group> oGroup = groupRepository.findById(assign.getGroup().getName());

            if (oGroup.isEmpty()) {
                throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
            }

            assign.setGroup(oGroup.get());
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
            boolean sameSpectator = task.getAssigns()
                    .stream()
                    .anyMatch(s -> s.getUser().getUsername().equals(spectator.getUser().getUsername()) && !s.isDeleted());

            if (sameSpectator) {
                throw new ApplicationException(I18nErrorMessage.SPECTATOR_ALREADY_EXIST);
            }

            if (Objects.nonNull(spectator.getUser())) {
                Optional<User> oUser = userRepository.findById(spectator.getUser().getUsername());

                if (oUser.isEmpty()) {
                    throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
                }

                spectator.setUser(oUser.get());
            }
        }

        if (Objects.nonNull(spectator.getGroup())) {

            boolean sameSpectator = task.getAssigns()
                    .stream()
                    .anyMatch(s -> s.getGroup().getName().equals(spectator.getGroup().getName()) && !s.isDeleted());

            if (sameSpectator) {
                throw new ApplicationException(I18nErrorMessage.SPECTATOR_ALREADY_EXIST);
            }

            Optional<Group> oGroup = groupRepository.findById(spectator.getGroup().getName());

            if (oGroup.isEmpty()) {
                throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
            }

            spectator.setGroup(oGroup.get());
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
                .findSpectatorByTaskIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getClientName(), false, page)
                .stream()
                // filter the assigns that have at least one non deleted element
                .filter(s -> {
                    if (Objects.nonNull(s.getUser()) && !s.getUser().isDeleted()) {
                        return true;
                    }

                    if (Objects.nonNull(s.getGroup()) && !s.getGroup().isDeleted()) {
                        return true;
                    }

                    return false;
                })
                // Remove deleted users and deleted groups
                .map(s -> {
                    if (Objects.nonNull(s.getUser())) {
                        if (s.getUser().isDeleted()) {
                            s.setUser(null);
                        }
                    }

                    if (Objects.nonNull(s.getGroup())) {
                        if (s.getGroup().isDeleted()) {
                            s.setGroup(null);
                        }
                    }
                    return s;
                })
                .collect(Collectors.toList());

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
        if (Objects.nonNull(userPrincipal.getPropagatedUser()) && !comment.getCreatedBy().getUsername().equals(userPrincipal.getPropagatedUser())){
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

    public Task updateTask(String identifier, Task task) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getClientName(), false);

        if (oTask.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        Task taskEntity = oTask.get();
        if (Objects.nonNull(task.getName())) {
            taskEntity.setName(task.getName());
        }

        if (Objects.nonNull(task.getStatus())) {
            taskEntity.setStatus(task.getStatus());
        }

        if (Objects.nonNull(task.getPriority())) {
            taskEntity.setPriority(task.getPriority());
        }

        if (Objects.nonNull(task.getDescription())) {
            taskEntity.setDescription(task.getDescription());
        }

        if (Objects.nonNull(task.getAssigns())) {
            taskEntity.setAssigns(task.getAssigns());
        }

        if (Objects.nonNull(task.getSpectators())) {
            taskEntity.setSpectators(task.getSpectators());
        }

        if (Objects.nonNull(task.getDueDate())) {
            taskEntity.setDueDate(task.getDueDate());
        } else if (Objects.isNull(task.getDueDate()) && Objects.nonNull(taskEntity.getDueDate())) {
            taskEntity.setDueDate(task.getDueDate());
        }

        return taskRepository.save(taskEntity);
    }

    @Transactional
    public Comment updateComment(String taskIdentifier, String commentIdentifier, Comment comment) {
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

        Comment commentEntity = oComment.get();
        if (Objects.nonNull(userPrincipal.getPropagatedUser()) && !commentEntity.getCreatedBy().getUsername().equals(userPrincipal.getPropagatedUser())){
            throw new ApplicationException(I18nErrorMessage.PROPAGATED_USER_IS_NOT_SAME);
        }

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
