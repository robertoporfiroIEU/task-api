package gr.rk.tasks.service;

import gr.rk.tasks.entity.*;
import gr.rk.tasks.exception.TaskNotFoundException;
import gr.rk.tasks.exception.i18n.*;
import gr.rk.tasks.repository.*;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
            UserPrincipal userPrincipal
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.assignRepository = assignRepository;
        this.spectatorRepository = spectatorRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userPrincipal = userPrincipal;
    }

    @Transactional
    public Task createTask(Task task, String createdBy, String projectIdentifier) {
        // validations
        Optional<User> oUser = userRepository
                .findByUsernameAndApplicationUserAndDeleted(createdBy, userPrincipal.getApplicationUser(), false);
        if (oUser.isEmpty()) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Optional<Project> oProject = projectRepository
                .findProjectByIdentifierAndApplicationUserAndDeleted(projectIdentifier, userPrincipal.getApplicationUser(), false);
        if (oProject.isEmpty()) {
            throw new ProjectNotFoundException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        // happy path
        task.setCreatedBy(oUser.get());
        task.setProject(oProject.get());
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
                dueDateTo,
                userPrincipal.getApplicationUser()
        );
    }

    public Optional<Task> getTask(String identifier) {
        return taskRepository.findTaskByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false);
    }

    public Page<Comment> getComments(String identifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return commentRepository.findCommentByTaskIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false, page);
    }

    @Transactional
    public Comment addTaskComment(String identifier, Comment comment, String createdBy) {
        // validations
        Optional<User> oUser = userRepository
                .findByUsernameAndApplicationUserAndDeleted(createdBy, userPrincipal.getApplicationUser(), false);
        if (oUser.isEmpty()) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Optional<Task> oTask = taskRepository
                .findTaskByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false);
        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        // happy path
        comment.setCreatedBy(oUser.get());
        comment.setTask(oTask.get());
        return commentRepository.save(comment);
    }

    public Page<Assign> getAssigns(String identifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        List<Assign> assigns = assignRepository
                .findAssignByTaskIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false, page)
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
    public Assign addAssign(String identifier, Assign assign) {
        // validations
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false);

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

        // happy path
        assign.setTask(task);
        task.getAssigns().add(assign);
        return assignRepository.save(assign);
    }

    @Transactional
    public Spectator addSpectator(String identifier, Spectator spectator) {
        // validations
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false);
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

        // happy path
        spectator.setTask(task);
        task.getSpectators().add(spectator);
        return spectatorRepository.save(spectator);
    }

    public Page<Spectator> getSpectators(String identifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        List<Spectator> spectators = spectatorRepository
            .findSpectatorByTaskIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false, page)
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
    public void deleteTaskLogical(String identifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getApplicationUser(), false);

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Task task = oTask.get();
        task.setDeleted(true);
    }

    @Transactional
    public void deleteCommentLogical(String taskIdentifier, String commentIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getApplicationUser(), false);

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Comment> oComment = commentRepository.
                findCommentByIdentifierAndApplicationUserAndDeleted(commentIdentifier, userPrincipal.getApplicationUser(), false);

        if (oComment.isEmpty()) {
            throw new CommentNotFoundException(I18nErrorMessage.COMMENT_NOT_FOUND);
        }

        Comment comment = oComment.get();
        comment.setDeleted(true);
    }

    @Transactional
    public void deleteAssignLogical(String taskIdentifier, String assignIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getApplicationUser(), false);

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Assign> oAssign = assignRepository.
                findAssignByIdentifierAndApplicationUserAndDeleted(assignIdentifier, userPrincipal.getApplicationUser(), false);

        if (oAssign.isEmpty()) {
            throw new AssignNotFoundException(I18nErrorMessage.ASSIGN_NOT_FOUND);
        }

        Assign assign = oAssign.get();
        assign.setDeleted(true);
    }

    @Transactional
    public void deleteSpectatorLogical(String taskIdentifier, String spectatorIdentifier) {
        Optional<Task> oTask = taskRepository.
                findTaskByIdentifierAndApplicationUserAndDeleted(taskIdentifier, userPrincipal.getApplicationUser(), false);

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        Optional<Spectator> oSpectator = spectatorRepository.
                findSpectatorByIdentifierAndApplicationUserAndDeleted(spectatorIdentifier, userPrincipal.getApplicationUser(), false);

        if (oSpectator.isEmpty()) {
            throw new SpectatorNotFoundException(I18nErrorMessage.SPECTATOR_NOT_FOUND);
        }

        Spectator spectator = oSpectator.get();
        spectator.setDeleted(true);
    }
}
