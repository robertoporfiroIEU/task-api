package gr.rk.tasks.service;

import gr.rk.tasks.entity.Assign;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.exception.TaskNotFoundException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.exception.i18n.ProjectNotFoundException;
import gr.rk.tasks.exception.i18n.UserNotFoundException;
import gr.rk.tasks.exception.i18n.UserOrGroupNotFoundException;
import gr.rk.tasks.repository.AssignRepository;
import gr.rk.tasks.repository.CommentRepository;
import gr.rk.tasks.repository.TaskRepository;
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

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.taskService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            CommentRepository commentRepository,
            AssignRepository assignRepository,
            UserPrincipal userPrincipal
    ) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.assignRepository = assignRepository;
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
            throw new UserOrGroupNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Task task = oTask.get();
        assign.setTask(task);
        task.getAssigns().add(assign);
        return assignRepository.save(assign);
    }

}
