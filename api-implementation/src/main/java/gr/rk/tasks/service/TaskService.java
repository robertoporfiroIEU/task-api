package gr.rk.tasks.service;

import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.exceptions.TaskNotFoundException;
import gr.rk.tasks.exceptions.i18n.I18nErrorMessage;
import gr.rk.tasks.exceptions.i18n.UserNotFoundException;
import gr.rk.tasks.repository.CommentRepository;
import gr.rk.tasks.repository.TaskRepository;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.taskService.commentPageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(TaskRepository taskRepository, CommentRepository commentRepository, UserPrincipal userPrincipal, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.userPrincipal = userPrincipal;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public Page<Task> getTasks(
            Pageable pageable,
            String identifier,
            String name,
            String status,
            String creationDate,
            String createdBy,
            String dueDate
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
                creationDate,
                createdBy,
                dueDate
        );
    }

    public Optional<Task> getTask(String identifier) {
        return taskRepository.findTaskByIdentifierAndApplicationUser(identifier, userPrincipal.getApplicationUser());
    }

    public void deleteTask(String taskIdentifier) {
        taskRepository.deleteByIdentifier(taskIdentifier);
    }

    public Page<Comment> getComments(UUID identifier, Pageable pageable) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return commentRepository.findCommentByTaskIdentifierAndApplicationUser(identifier.toString(), userPrincipal.getApplicationUser(), page);
    }

    @Transactional
    public Comment addTaskComment(UUID identifier, Comment comment) {
        Optional<Task> oTask = taskRepository.findTaskByIdentifierAndApplicationUser(identifier.toString(), userPrincipal.getApplicationUser());

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
