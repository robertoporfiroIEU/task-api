package gr.rk.tasks.service;

import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.repository.CommentRepository;
import gr.rk.tasks.repository.TaskRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.taskService.commentPageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(TaskRepository taskRepository, CommentRepository commentRepository, UserPrincipal userPrincipal) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.userPrincipal = userPrincipal;
    }

    public void createTask(Task task) {
        taskRepository.save(task);
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
}
