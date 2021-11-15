package gr.rk.tasks.service;

import gr.rk.tasks.entity.*;
import gr.rk.tasks.exception.TaskNotFoundException;
import gr.rk.tasks.exception.i18n.*;
import gr.rk.tasks.repository.*;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    private final AssignRepository assignRepository;

    private final SpectatorRepository spectatorRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.taskService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            CommentRepository commentRepository,
            AssignRepository assignRepository,
            SpectatorRepository spectatorRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            UserPrincipal userPrincipal
    ) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.assignRepository = assignRepository;
        this.spectatorRepository = spectatorRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userPrincipal = userPrincipal;
    }

    @Transactional
    public Task createTask(Task task) {
        if (Objects.isNull(task.getCreatedBy())) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        if (Objects.isNull(task.getProject())) {
            throw new ProjectNotFoundException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        return taskRepository.saveTask(task);
    }

    public Page<Task> getTasks(
            Pageable pageable,
            String identifier,
            String name,
            String status,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String dueDateFrom,
            String dueDateTo
    ) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return taskRepository.findTasksDynamicJPQL(
                page,
                identifier,
                name,
                status,
                creationDateFrom,
                creationDateTo,
                createdBy,
                dueDateFrom,
                dueDateTo
        );
    }

    public Optional<Task> getTask(String identifier) {
        return taskRepository.findTaskByIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser());
    }

    public void deleteTask(String taskIdentifier) {
        taskRepository.deleteByIdentifier(taskIdentifier);
    }

    public Page<Comment> getComments(String identifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return commentRepository.findCommentByTaskIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser(), page);
    }

    @Transactional
    public Comment addTaskComment(String identifier, Comment comment) {
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser());

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        if (Objects.isNull(comment.getCreatedBy())) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Task task = oTask.get();
        comment.setTask(task);
        task.getComments().add(comment);
        return commentRepository.save(comment);
    }

    public Page<Assign> getAssigns(String identifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return assignRepository.findAssignByTaskIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser(), page);
    }

    @Transactional
    public Assign addAssign(String identifier, Assign assign) {
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser());

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        if (Objects.isNull(assign.getUser()) && Objects.isNull(assign.getGroup())) {
            throw new UserAndGroupNotFoundException(I18nErrorMessage.USER_AND_GROUP_NOT_FOUND);
        }

        Task task = oTask.get();

        if (Objects.nonNull(assign.getUser())) {
            Optional<User> oUser = userRepository.findById(assign.getUser().getUsername());

            if (oUser.isEmpty()) {
                throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
            }

            assign.setUser(oUser.get());
        }

        if (Objects.nonNull(assign.getGroup())) {
            Optional<Group> oGroup = groupRepository.findById(assign.getGroup().getName());

            if (oGroup.isEmpty()) {
                throw new GroupNotFoundException(I18nErrorMessage.GROUP_NOT_FOUND);
            }

            assign.setGroup(oGroup.get());
        }

        assign.setTask(task);
        task.getAssigns().add(assign);
        return assignRepository.save(assign);
    }

    @Transactional
    public Spectator addSpectator(String identifier, Spectator spectator) {
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser());

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        if (Objects.isNull(spectator.getUser()) && Objects.isNull(spectator.getGroup())) {
            throw new UserAndGroupNotFoundException(I18nErrorMessage.USER_AND_GROUP_NOT_FOUND);
        }

        Task task = oTask.get();

        if (Objects.nonNull(spectator.getUser())) {
            Optional<User> oUser = userRepository.findById(spectator.getUser().getUsername());

            if (oUser.isEmpty()) {
                throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
            }

            spectator.setUser(oUser.get());
        }

        if (Objects.nonNull(spectator.getGroup())) {
            Optional<Group> oGroup = groupRepository.findById(spectator.getGroup().getName());

            if (oGroup.isEmpty()) {
                throw new GroupNotFoundException(I18nErrorMessage.GROUP_NOT_FOUND);
            }

            spectator.setGroup(oGroup.get());
        }

        spectator.setTask(task);
        task.getSpectators().add(spectator);
        return spectatorRepository.save(spectator);
    }
}
