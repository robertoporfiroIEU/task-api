package gr.rk.tasks.service;

import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.exception.TaskNotFoundException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.exception.ProjectNotFoundException;
import gr.rk.tasks.exception.UserNotFoundException;
import gr.rk.tasks.repository.CommentRepository;
import gr.rk.tasks.repository.ProjectRepository;
import gr.rk.tasks.repository.TaskRepository;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
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

    private final ProjectRepository projectRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.taskService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            CommentRepository commentRepository,
            UserPrincipal userPrincipal,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
        this.userPrincipal = userPrincipal;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task createTask(Task task) {
        if (!userRepository.existsByUsername(task.getCreatedBy().getUsername())) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        if (Objects.isNull(task.getProject())) {
            throw new ProjectNotFoundException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        return taskRepository.save(task);
    }

    public Page<Task> getTasks(
            Pageable pageable,
            String identifier,
            String projectIdentifier,
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

        return taskRepository.findTaskDynamicJPQL(
                page,
                identifier,
                projectIdentifier,
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
        Long id = Util.getIdFromIdentifier(identifier);
        String prefixIdentifier = Util.getPrefixIdentifierFromIdentifier(identifier);
        return taskRepository.findTaskByByIdAndPrefixIdentifierAndApplicationUser(id, prefixIdentifier, userPrincipal.getApplicationUser());
    }

    public Page<Comment> getComments(String taskIdentifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        Long id = Util.getIdFromIdentifier(taskIdentifier);
        String prefixIdentifier = Util.getPrefixIdentifierFromIdentifier(taskIdentifier);
        return commentRepository.findCommentByTaskIdAndApplicationUser(id, userPrincipal.getApplicationUser(), page);
    }

    @Transactional
    public Comment addTaskComment(String taskIdentifier, Comment comment) {
        Long id = Util.getIdFromIdentifier(taskIdentifier);
        String prefixIdentifier = Util.getPrefixIdentifierFromIdentifier(taskIdentifier);
        Optional<Task> oTask = taskRepository.findTaskByByIdAndPrefixIdentifierAndApplicationUser(id, prefixIdentifier, userPrincipal.getApplicationUser());

        if (oTask.isEmpty()) {
            throw new TaskNotFoundException(I18nErrorMessage.TASK_NOT_FOUND);
        }

        if (!userRepository.existsByUsername(comment.getCreatedBy().getUsername())) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Task task = oTask.get();
        comment.setTask(task);
        task.getComments().add(comment);
        return commentRepository.save(comment);
    }
}
