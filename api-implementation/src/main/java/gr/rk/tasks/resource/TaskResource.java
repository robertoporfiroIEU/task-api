package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.models.Assign;
import gr.rk.tasks.V1.models.Comment;
import gr.rk.tasks.V1.models.Task;
import gr.rk.tasks.V1.models.TaskDetail;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@RestController
public class TaskResource implements TasksApi {

    @Override
    public ResponseEntity<Assign> addAssign(UUID identifier, @Valid Assign assign) {
        return null;
    }

    @Override
    public ResponseEntity<Comment> addTaskComment(UUID identifier, @Valid Comment comment) {
        return null;
    }

    @Override
    public ResponseEntity<TaskDetail> getTaskDetail(UUID identifier) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setName("test");
        taskDetail.setIdentifier(UUID.randomUUID());
        taskDetail.setComments(
                List.of(new Comment().text("text comment")
                        .creationDate(LocalDateTime.now().atZone(ZoneId.of("UTC").normalized()).toString()))
        );
        return ResponseEntity.ok(taskDetail);
    }

    @Override
    public ResponseEntity<Page<Task>> getTasks(@Valid Pageable pageable) {
        List<Task> tasks = List.of(new Task().name("test"));
        return ResponseEntity.ok(new PageImpl<>(tasks));
    }
}
